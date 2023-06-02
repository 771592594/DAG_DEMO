package com.xiaoyu.process

abstract class AbstractAsyncProcessor : AbstractProcessor() {
    abstract fun queryResult(context: Map<String, Any?>): ProcessResult
}