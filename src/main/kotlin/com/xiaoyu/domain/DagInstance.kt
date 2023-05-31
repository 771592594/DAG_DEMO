package com.xiaoyu.domain

import java.util.*

class DagInstance(
    var instanceId: String?,
    var createTime: Date?,
    var modifiedTime: Date?,
    var status: String?,
    var nodeCount: Int?,
    var context: List<Map<String, Any?>>?
)