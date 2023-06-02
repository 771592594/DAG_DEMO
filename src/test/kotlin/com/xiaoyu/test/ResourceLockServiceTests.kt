package com.xiaoyu.test

import com.xiaoyu.domain.AvailableEnum
import com.xiaoyu.repo.ResourceLockRepo
import com.xiaoyu.service.ResourceLockService
import jakarta.annotation.Resource
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import java.util.*

@SpringBootTest
class ResourceLockServiceTests {

    @Resource
    lateinit var resourceLockRepo: ResourceLockRepo
    @Resource
    lateinit var resourceLockService: ResourceLockService

    @Test
    @Rollback
    @Transactional
    fun testException() {
        val resourceType = "test"
        val resourceId = UUID.randomUUID().toString()
        resourceLockService.tryLockAndExecute(
            resourceType = resourceType,
            resourceId = resourceId,
            action = {
                assertAvailable(resourceType, resourceId, AvailableEnum.N)
                throw RuntimeException()
            }
        )
        assertAvailable(resourceType, resourceId, AvailableEnum.Y)
    }

    @Test
    @Rollback
    @Transactional
    fun testNoException() {
        val resourceType = "test"
        val resourceId = UUID.randomUUID().toString()
        resourceLockService.tryLockAndExecute(
            resourceType = resourceType,
            resourceId = resourceId,
            action = {
                assertAvailable(resourceType, resourceId, AvailableEnum.N)
            }
        )
        assertAvailable(resourceType, resourceId, AvailableEnum.Y)
    }

    fun assertAvailable(resourceType: String, resourceId: String, available: AvailableEnum) {
        resourceLockRepo.findByResourceTypeAndResourceId(
            resourceType = resourceType,
            resourceId = resourceId
        ).let {
            assert(it.available == available.name)
        }
    }
}