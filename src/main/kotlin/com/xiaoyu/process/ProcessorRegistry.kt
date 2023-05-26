package com.xiaoyu.process

object ProcessorRegistry {

    private val registry: MutableMap<String, AbstractProcessor> = mutableMapOf()

    fun register(processor: AbstractProcessor) {
        val processorName = processor.processorName()
        val existProcessor = registry[processorName]
        if (existProcessor != null && existProcessor != processor) {
            throw RuntimeException("processorName exist: ${processorName}, class=${existProcessor.javaClass}")
        } else {
            registry[processorName] = processor
        }
    }

    fun findBy(processorName: String): AbstractProcessor {
        return registry[processorName]!!
    }
}