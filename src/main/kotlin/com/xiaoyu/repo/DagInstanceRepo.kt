package com.xiaoyu.repo

import com.xiaoyu.entity.DagInstanceDO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DagInstanceRepo : JpaRepository<DagInstanceDO, Long> {

    fun findByInstanceId(instanceId: String): DagInstanceDO

    @Query(
        value = "select * from dag_instance where status in ('INIT', 'PROCESSING') order by id limit 100",
        nativeQuery = true
    )
    fun findUnfinishedDagInstance(): List<DagInstanceDO>

}