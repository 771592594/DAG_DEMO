package com.xiaoyu.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.xiaoyu.domain.DagGraph
import com.xiaoyu.domain.DagInstance
import com.xiaoyu.domain.DagNodeInstance
import com.xiaoyu.domain.DagStatus
import com.xiaoyu.domain.DagStatus.*
import com.xiaoyu.entity.DagInstanceDO
import com.xiaoyu.entity.DagNodeInstanceDO
import com.xiaoyu.process.ProcessResult
import com.xiaoyu.process.ProcessorRegistry
import com.xiaoyu.repo.DagInstanceRepo
import com.xiaoyu.repo.DagNodeInstanceRepo
import com.xiaoyu.service.DagService
import jakarta.annotation.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Example
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.util.*


@Service
class DagServiceImpl : DagService {

    @Resource
    private lateinit var dagInstanceRepo: DagInstanceRepo
    @Resource
    private lateinit var dagNodeInstanceRepo: DagNodeInstanceRepo
    @Resource
    private lateinit var transactionTemplate: TransactionTemplate

    private val objectMapper: ObjectMapper = ObjectMapper()

    private final val log: Logger = LoggerFactory.getLogger(DagServiceImpl::class.java)

    override fun save(dagGraph: DagGraph): String {
        val dagInstanceId = UUID.randomUUID().toString()
        val dagNodeInstanceList = dagGraph.dagNodeInstanceList
        dagNodeInstanceList.forEach {
            it.status = INIT.name
            it.dagInstanceId = dagInstanceId
        }

        val dagInstanceDO = DagInstanceDO(
            id = null,
            instanceId = dagInstanceId,
            status = INIT.name,
            nodeCount = dagNodeInstanceList.size,
            context = objectMapper.writeValueAsString(dagNodeInstanceList)
        )
        val dagNodeInstanceDOList = dagNodeInstanceList.asSequence()
            .filter { it.parentNodeId.isEmpty() }
            .map(this::buildDagNodeInstanceDO)
            .toList()
        transactionTemplate.execute {
            dagInstanceRepo.save(dagInstanceDO)
            dagNodeInstanceRepo.saveAll(dagNodeInstanceDOList)
        }
        return dagInstanceId
    }

    override fun execute(dagGraph: DagGraph) {
        // todo: redis lock to avid concurrent invoke
        val dagInstanceId = dagGraph.dagInstanceId!!
        val dagInstanceDO = dagInstanceRepo.findByInstanceId(dagInstanceId)
        dagInstanceDO.status = PROCESSING.name
        dagInstanceRepo.save(dagInstanceDO)

        val unfinishedDagNodeInstance = findUnfinishedDagNodeInstanceBy(dagInstanceId)
        runBlocking {
            for (dagNodeInstance in unfinishedDagNodeInstance) {
                launch {
                    val processor = ProcessorRegistry.findBy(dagNodeInstance.processor!!)
                    val processResult = processor.process(dagNodeInstance.context)
                    updateDagNodeInstanceProcess(dagNodeInstance, processResult, dagGraph.nodeId2Children())
                }
            }
        }
        updateDagInstanceProcess(dagInstanceId)
    }

    private fun updateDagInstanceProcess(dagInstanceId: String) {
        transactionTemplate.execute {
            val dagInstanceDO = dagInstanceRepo.findWithLockByInstanceId(dagInstanceId)
            if (dagInstanceDO.status == PROCESSING.name) {
                val dagNodeInstanceDOList = dagNodeInstanceRepo.findByDagInstanceId(dagInstanceId)
                val nodeStatusSet = dagNodeInstanceDOList.asSequence()
                    .map { DagStatus.valueOf(it.status!!) }
                    .toSet()
                var newDagInstanceStatus = PROCESSING
                if (FAIL in nodeStatusSet) {
                    newDagInstanceStatus = FAIL
                }
                if (dagInstanceDO.nodeCount == dagNodeInstanceDOList.size && nodeStatusSet == setOf(SUCCEEDED)) {
                    newDagInstanceStatus = SUCCEEDED
                }
                dagInstanceDO.status = newDagInstanceStatus.name
                dagInstanceRepo.save(dagInstanceDO)
            } else {
                // do nothing
            }
        }
    }

    private fun updateDagNodeInstanceProcess(
        dagNodeInstance: DagNodeInstance,
        processResult: ProcessResult,
        nodeId2Children: MutableMap<Int, MutableList<DagNodeInstance>>
    ) {
        val dagInstanceId: String = dagNodeInstance.dagInstanceId!!
        val nodeId: Int = dagNodeInstance.nodeId!!
        val nodeStatus = processResult.nodeStatus
        val errorMessage = processResult.errorMessage
        var readyChildren = emptyList<DagNodeInstanceDO>()

        val dagNodeInstanceDO = dagNodeInstanceRepo.findByDagInstanceIdAndNodeId(dagInstanceId, nodeId)
        dagNodeInstanceDO.status = nodeStatus.name
        when (nodeStatus) {
            FAIL -> dagNodeInstance.context["errorMessage"] = errorMessage
            SUCCEEDED -> {
                val existNodeList = dagNodeInstanceRepo.findByDagInstanceId(dagInstanceId)
                existNodeList.find { it.nodeId == nodeId }.also { it!!.status = SUCCEEDED.name }
                readyChildren = findReadyChildren(existNodeList, nodeId, nodeId2Children)
            }
            else -> {}
        }

        transactionTemplate.execute {
            dagNodeInstanceRepo.save(dagNodeInstanceDO)
            dagNodeInstanceRepo.saveAll(readyChildren)
        }
    }

    private fun findReadyChildren(
        existNode: List<DagNodeInstanceDO>,
        nodeId: Int,
        nodeId2Children: MutableMap<Int, MutableList<DagNodeInstance>>,
    ): List<DagNodeInstanceDO> {
        val groupByIfSucceed = existNode.groupBy({ it.status == SUCCEEDED.name }) { it.nodeId }
        val succeedNodeIdList = groupByIfSucceed.getOrDefault(true, listOf())
        val children = nodeId2Children.getOrDefault(nodeId, listOf())
        return children.asSequence()
            .filter { succeedNodeIdList.containsAll(it.parentNodeId) }
            .map(this::buildDagNodeInstanceDO)
            .toList()
    }

    override fun findUnfinishedDagInstance(): List<DagInstance> {
        return dagInstanceRepo.findUnfinishedDagInstance()
            .map(this::buildDagInstance)
    }

    override fun findUnfinishedDagNodeInstanceBy(dagInstanceId: String): List<DagNodeInstance> {
        return dagNodeInstanceRepo.findUnfinishedDagInstance(dagInstanceId)
            .map(this::buildDagNodeInstance)
    }

    override fun findDagGraph(dagInstanceId: String): DagGraph {
        val example = DagInstanceDO().let {
            it.instanceId = dagInstanceId
            return@let Example.of(it)
        }
        val dagInstanceDO = dagInstanceRepo.findOne(example)
            .orElseThrow { RuntimeException("can not find dagInstance where instanceId=${dagInstanceId}") }
        val dagNodeInstanceList = objectMapper.readValue(
            dagInstanceDO.context,
            object : TypeReference<MutableList<DagNodeInstance>>() {}
        )
        return DagGraph(dagInstanceId, dagNodeInstanceList)
    }

    private fun buildDagInstance(dagInstanceDO: DagInstanceDO): DagInstance {
        return dagInstanceDO.let {
            DagInstance(
                instanceId = it.instanceId,
                status = it.status,
                nodeCount = it.nodeCount,
                context = objectMapper.readValue(it.context, object : TypeReference<List<Map<String, Any?>>>() {})
            )
        }
    }

    private fun buildDagNodeInstance(dagNodeInstanceDO: DagNodeInstanceDO): DagNodeInstance {
        return dagNodeInstanceDO.let {
            return@let DagNodeInstance(
                dagInstanceId = it.dagInstanceId,
                processor = it.processor,
                nodeId = it.nodeId!!,
                parentNodeId = objectMapper.readValue(it.parentNodeId, object : TypeReference<List<Int>>() {}),
                status = it.status,
                context = objectMapper.readValue(it.context, object : TypeReference<MutableMap<String, Any?>>() {})
            )
        }
    }

    private fun buildDagNodeInstanceDO(dagNodeInstance: DagNodeInstance): DagNodeInstanceDO {
        return dagNodeInstance.let {
            return@let DagNodeInstanceDO(
                id = null,
                dagInstanceId = it.dagInstanceId,
                processor = it.processor,
                parentNodeId = objectMapper.writeValueAsString(it.parentNodeId),
                nodeId = it.nodeId,
                status = it.status,
                context = objectMapper.writeValueAsString(it.context)
            )
        }
    }
}