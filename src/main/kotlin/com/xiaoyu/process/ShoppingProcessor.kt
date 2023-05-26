package com.xiaoyu.process

import com.xiaoyu.domain.DagStatus
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