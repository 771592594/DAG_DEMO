package com.xiaoyu.service

interface ResourceLockService {
    /**
     * 获取资源锁然后执行函数
     *
     * @param resourceType 资源类型
     * @param resourceId 资源id
     * @param action 获取资源锁成功后执行的方法
     */
    fun tryLockAndExecute(resourceType: String, resourceId: String, action: () -> Unit)
}