package com.xiaoyu

import com.xiaoyu.domain.DagStatus
import com.xiaoyu.process.AbstractProcessor
import com.xiaoyu.process.ProcessResult
import org.springframework.stereotype.Component

@Component
class ShoppingProcessor : AbstractProcessor() {
    override fun processorName(): String {
        return "shop"
    }

    override fun process(context: Map<String, Any?>): ProcessResult {
        println(context)
        return ProcessResult(
            nodeStatus = DagStatus.SUCCEEDED,
            errorMessage = null
        )
    }
}