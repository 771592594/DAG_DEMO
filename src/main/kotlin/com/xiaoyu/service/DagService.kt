package com.xiaoyu.service

import com.xiaoyu.domain.DagGraph
import com.xiaoyu.domain.DagInstance
import com.xiaoyu.domain.DagNodeInstance

interface DagService {

    /**
     * 创建DAG流程实例
     *
     * @param dagGraph DAG图
     * @return DAG实例id
     */
    fun save(dagGraph: DagGraph): String

    /**
     * 执行DAG图的流程
     *
     * @param dagGraph DAG图
     */
    fun execute(dagGraph: DagGraph)

    /**
     * 查询未完成的DAG实例
     */
    fun findUnfinishedDagInstance(): List<DagInstance>

    /**
     * 查询未完成的DAG节点实例
     *
     * @param dagInstanceId             DAG实例id
     * @return List<DagNodeInstance>    未完成的DAG节点实例
     */
    fun findUnfinishedDagNodeInstanceBy(dagInstanceId: String): List<DagNodeInstance>

    /**
     * 根据DAG实例获取DAG
     *
     * @param dagInstanceId DagInstance
     * @return DagGraph
     */
    fun findDagGraph(dagInstanceId: String): DagGraph
}
