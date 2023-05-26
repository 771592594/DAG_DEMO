package com.xiaoyu.repo

import com.xiaoyu.entity.DagNodeInstanceDO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DagNodeInstanceRepo : JpaRepository<DagNodeInstanceDO, Long> {

    fun findByDagInstanceId(dagInstanceId: String): List<DagNodeInstanceDO>

    fun findByDagInstanceIdAndNodeId(dagInstanceId: String, nodeId: Int): DagNodeInstanceDO

    @Query(
        value = "select * from dag_node_instance where dag_instance_id = ?1 and status in ('INIT', 'PROCESSING')",
        nativeQuery = true
    )
    fun findUnfinishedDagInstance(dagInstanceId: String): List<DagNodeInstanceDO>
}