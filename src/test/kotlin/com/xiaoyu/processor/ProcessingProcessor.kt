package com.xiaoyu.processor

import com.xiaoyu.domain.DagStatus
import com.xiaoyu.process.AbstractProcessor
import com.xiaoyu.process.ProcessResult
import org.springframework.stereotype.Component

@Component
class ProcessingProcessor  : AbstractProcessor() {
    override fun processorName(): String {
        return this::class.simpleName!!
    }

    override fun process(context: Map<String, Any?>): ProcessResult {
        println(context)
        return ProcessResult(
            nodeStatus = DagStatus.PROCESSING,
            errorMessage = null
        )
    }
}