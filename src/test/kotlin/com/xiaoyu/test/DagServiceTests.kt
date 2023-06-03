package com.xiaoyu.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.xiaoyu.domain.DagGraph
import com.xiaoyu.domain.DagNodeInstance
import com.xiaoyu.domain.DagStatus.*
import com.xiaoyu.processor.FailProcessor
import com.xiaoyu.processor.SucceededProcessor
import com.xiaoyu.repo.DagInstanceRepo
import com.xiaoyu.repo.DagNodeInstanceRepo
import com.xiaoyu.service.DagService
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
class DagServiceTests {

    @Resource
    private lateinit var dagService: DagService
    @Resource
    private lateinit var dagInstanceRepo: DagInstanceRepo
    @Resource
    private lateinit var dagNodeInstanceRepo: DagNodeInstanceRepo

    private val objectMapper: ObjectMapper = ObjectMapper()

    @Test
    fun testRun() {
        val counter = AtomicInteger(0)
        val dagGraph = DagGraph().apply {
            val root1 = DagNodeInstance(
                SucceededProcessor::class.simpleName,
                counter.getAndIncrement(),
                listOf(),
                mutableMapOf("name" to "apple")
            )
            val root2 = DagNodeInstance(
                SucceededProcessor::class.simpleName,
                counter.getAndIncrement(),
                listOf(),
                mutableMapOf("name" to "banana")
            )
            val node1 = DagNodeInstance(
                SucceededProcessor::class.simpleName,
                counter.getAndIncrement(),
                listOf(root1.nodeId!!, root2.nodeId!!),
                mutableMapOf("name" to "robot")
            )
            val node2 = DagNodeInstance(
                SucceededProcessor::class.simpleName,
                counter.getAndIncrement(),
                listOf(node1.nodeId!!),
                mutableMapOf("a" to "water")
            )
            addNode(root1)
            addNode(root2)
            addNode(node1)
            addNode(node2)
        }
        val dagInstanceId = dagService.save(dagGraph)
        dagGraph.dagInstanceId = dagInstanceId

        scheduleDAG(dagInstanceId, dagGraph)
    }

    @Test
    fun testFail() {
        val counter = AtomicInteger(0)
        val dagGraph = DagGraph().apply {
            val root1 = DagNodeInstance(
                FailProcessor::class.simpleName,
                counter.getAndIncrement(),
                listOf(),
                mutableMapOf("name" to "apple")
            )
            val root2 = DagNodeInstance(
                SucceededProcessor::class.simpleName,
                counter.getAndIncrement(),
                listOf(),
                mutableMapOf("name" to "banana")
            )
            val node1 = DagNodeInstance(
                SucceededProcessor::class.simpleName,
                counter.getAndIncrement(),
                listOf(root1.nodeId!!, root2.nodeId!!),
                mutableMapOf("name" to "robot")
            )
            addNode(root1)
            addNode(root2)
            addNode(node1)
        }
        val dagInstanceId = dagService.save(dagGraph)
        dagGraph.dagInstanceId = dagInstanceId

        scheduleDAG(dagInstanceId, dagGraph)
        val dagInstanceDO = dagInstanceRepo.findByInstanceId(dagInstanceId)
        assert(dagInstanceDO.status == FAIL.name)
        val nodeInstanceDOList = dagNodeInstanceRepo.findByDagInstanceId(dagInstanceId)
        assert(nodeInstanceDOList.size == 2)
        assert(nodeInstanceDOList[0].status == FAIL.name)
        assert(nodeInstanceDOList[1].status == SUCCEEDED.name)
    }

    @Test
    fun testSucceeded() {
        val counter = AtomicInteger(0)
        val dagGraph = DagGraph().apply {
            val root1 = DagNodeInstance(
                SucceededProcessor::class.simpleName,
                counter.getAndIncrement(),
                listOf(),
                mutableMapOf("name" to "apple")
            )
            val root2 = DagNodeInstance(
                SucceededProcessor::class.simpleName,
                counter.getAndIncrement(),
                listOf(),
                mutableMapOf("name" to "banana")
            )
            val node1 = DagNodeInstance(
                SucceededProcessor::class.simpleName,
                counter.getAndIncrement(),
                listOf(root1.nodeId!!, root2.nodeId!!),
                mutableMapOf("name" to "robot")
            )
            addNode(root1)
            addNode(root2)
            addNode(node1)
        }
        val dagInstanceId = dagService.save(dagGraph)
        dagGraph.dagInstanceId = dagInstanceId

        scheduleDAG(dagInstanceId, dagGraph)
        val dagInstanceDO = dagInstanceRepo.findByInstanceId(dagInstanceId)
        assert(dagInstanceDO.status == SUCCEEDED.name)
        val nodeInstanceDOList = dagNodeInstanceRepo.findByDagInstanceId(dagInstanceId)
        assert(nodeInstanceDOList.size == 3)
        assert(nodeInstanceDOList[0].status == SUCCEEDED.name)
        assert(nodeInstanceDOList[1].status == SUCCEEDED.name)
        assert(nodeInstanceDOList[2].status == SUCCEEDED.name)
    }

    private fun scheduleDAG(dagInstanceId: String, dagGraph: DagGraph) {
        while (true) {
            val dagInstanceDO = dagInstanceRepo.findByInstanceId(dagInstanceId)
            when (valueOf(dagInstanceDO.status!!)) {
                INIT, PROCESSING -> { dagService.execute(dagGraph) }
                SUCCEEDED, FAIL -> { break }
            }
        }
    }
}
