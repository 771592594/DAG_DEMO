package com.xiaoyu.domain

class DagNodeInstance constructor(
    var dagInstanceId: String?,
    var processor: String?,
    var nodeId: Int?,
    var parentNodeId: List<Int>,
    var status: String?,
    var context: MutableMap<String, Any?>
){
    constructor() : this(
        dagInstanceId = null,
        processor = null,
        nodeId = null,
        parentNodeId = mutableListOf(),
        status = null,
        context = mutableMapOf()
    )

    constructor(processor: String?, nodeId: Int, parentNodeId: List<Int>, context: MutableMap<String, Any?>) : this(
        dagInstanceId = null,
        processor = processor,
        nodeId = nodeId,
        parentNodeId = parentNodeId,
        status = null,
        context = context
    )
}