package com.xiaoyu.domain

class DagInstance constructor(
    var instanceId: String?,
    var status: String?,
    var nodeCount: Int?,
    var context: List<Map<String, Any?>>?
)