package com.xiaoyu.service.impl

import com.xiaoyu.domain.AvailableEnum
import com.xiaoyu.entity.ResourceLockDO
import com.xiaoyu.repo.ResourceLockRepo
import com.xiaoyu.service.ResourceLockService
import jakarta.annotation.Resource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Example
import org.springframework.stereotype.Service

@Service
class ResourceLockServiceImpl : ResourceLockService {

    @Resource
    private lateinit var resourceLockRepo: ResourceLockRepo

    private final val log: Logger = LoggerFactory.getLogger(DagServiceImpl::class.java)

    override fun tryLockAndExecute(resourceType: String, resourceId: String, action: () -> Unit) {
        val searchResourceLock: ResourceLockDO = ResourceLockDO()
            .apply {
                this.resourceType = resourceType
                this.resourceId = resourceId
            }
        val requireResourceLock = resourceLockRepo.findOne(Example.of(searchResourceLock))
            .orElse(searchResourceLock)
        if (requireResourceLock.available != AvailableEnum.N.name) {
            requireResourceLock.available = AvailableEnum.N.name
            resourceLockRepo.save(requireResourceLock)
            try {
                action()
            } catch (e: Exception) {
                log.error("tryLockAndExecute failed: resourceType=$resourceType, resourceId=$resourceId, message=${e.message}", e)
            }
            requireResourceLock.available = AvailableEnum.Y.name
            resourceLockRepo.save(requireResourceLock)
        } else {
            log.info("lock resource fail: resourceType=$resourceType, resourceId=$resourceId")
            return
        }
    }
}