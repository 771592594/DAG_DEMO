package com.xiaoyu.domain

import java.util.*

class DagNodeInstance(
    var createTime: Date?,
    var modifiedTime: Date?,
    var dagInstanceId: String?,
    var processor: String?,
    var nodeId: Int?,
    var parentNodeId: List<Int>,
    var status: String?,
    var context: MutableMap<String, Any?>
){
    constructor(processor: String?, nodeId: Int, parentNodeId: List<Int>, context: MutableMap<String, Any?>) : this(
        createTime = null,
        modifiedTime = null,
        dagInstanceId = null,
        processor = processor,
        nodeId = nodeId,
        parentNodeId = parentNodeId,
        status = null,
        context = context
    )
}