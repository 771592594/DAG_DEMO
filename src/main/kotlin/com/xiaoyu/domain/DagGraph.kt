package com.xiaoyu.domain

class DagGraph constructor(
    var dagInstanceId: String?,
    var dagNodeInstanceList: MutableList<DagNodeInstance>
) {
    constructor() : this(null, mutableListOf())

    fun addNode(dagNodeInstance: DagNodeInstance) {
        dagNodeInstanceList.add(dagNodeInstance)
    }

    fun nodeId2Children(): MutableMap<Int, MutableList<DagNodeInstance>> {
        val map = mutableMapOf<Int, MutableList<DagNodeInstance>>()
        for (dagNodeInstance in dagNodeInstanceList) {
            for (parentId in dagNodeInstance.parentNodeId) {
                map.merge(parentId, mutableListOf(dagNodeInstance)) {old, new ->
                    old.addAll(new)
                    return@merge old
                }
            }
        }
        return map
    }
}