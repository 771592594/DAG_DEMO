package com.xiaoyu.job

import com.xiaoyu.domain.DagGraph
import com.xiaoyu.service.DagService
import jakarta.annotation.Resource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * DAG定时调度任务
 */
@Component
class DagScheduleJob {

    @Resource
    private lateinit var dagService: DagService

    @Scheduled(fixedDelay = 30 * 1000)
    fun schedule() {
        val unfinishedDagInstanceList = dagService.findUnfinishedDagInstance()
        for (dagInstance in unfinishedDagInstanceList) {
            val instanceId = dagInstance.instanceId
            val dagGraph: DagGraph = dagService.findDagGraph(instanceId!!)
        }
    }
}