package com.xiaoyu.process

import org.springframework.beans.factory.InitializingBean

abstract class AbstractProcessor : InitializingBean {
    abstract fun processorName(): String

    abstract fun process(context: Map<String, Any?>): ProcessResult

    override fun afterPropertiesSet() {
        ProcessorRegistry.register(this)
    }
}