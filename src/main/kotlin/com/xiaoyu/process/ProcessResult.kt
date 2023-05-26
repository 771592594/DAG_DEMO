package com.xiaoyu.process

import com.xiaoyu.domain.DagStatus

class ProcessResult(
    val nodeStatus: DagStatus,
    val errorMessage: String?
)