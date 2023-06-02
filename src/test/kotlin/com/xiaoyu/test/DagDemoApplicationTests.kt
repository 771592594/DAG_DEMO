package com.xiaoyu.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.xiaoyu.domain.DagGraph
import com.xiaoyu.domain.DagNodeInstance
import com.xiaoyu.domain.DagStatus
import com.xiaoyu.repo.DagInstanceRepo
import com.xiaoyu.service.DagService
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
class DagDemoApplicationTests {

    @Resource
    private lateinit var dagService: DagService
    @Resource
    private lateinit var dagInstanceRepo: DagInstanceRepo

    private val objectMapper: ObjectMapper = ObjectMapper()

    @Test
    fun contextLoads() {
        val counter = AtomicInteger(0)
        val dagGraph = DagGraph().apply {
            val root1 = buildDagNodeInstance(
                "shop",
                counter,
                listOf(),
                mutableMapOf("name" to "苹果")
            )
            val root2 = buildDagNodeInstance(
                "shop",
                counter,
                listOf(),
                mutableMapOf("name" to "青菜")
            )
            val node1 = buildDagNodeInstance(
                "shop",
                counter,
                listOf(root1.nodeId!!, root2.nodeId!!),
                mutableMapOf("name" to "萝卜")
            )
            val node2 = buildDagNodeInstance(
                "shop",
                counter,
                listOf(node1.nodeId!!),
                mutableMapOf("a" to "")
            )
            addNode(root1)
            addNode(root2)
            addNode(node1)
            addNode(node2)
        }
        val dagInstanceId = dagService.save(dagGraph)
        dagGraph.dagInstanceId = dagInstanceId

        while (true) {
            val dagInstanceDO = dagInstanceRepo.findByInstanceId(dagInstanceId)
            if (dagInstanceDO.status == DagStatus.SUCCEEDED.name) {
                break
            }
            dagService.execute(dagGraph)
        }
    }

    fun buildDagNodeInstance(
        processor: String,
        counter: AtomicInteger,
        parentNodeId: List<Int>,
        context: MutableMap<String, Any?>
    ): DagNodeInstance {
        return DagNodeInstance(processor, counter.getAndIncrement(), parentNodeId, context)
    }
}
