package com.xiaoyu.repo

import com.xiaoyu.entity.ResourceLockDO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ResourceLockRepo : JpaRepository<ResourceLockDO, Long> {
    fun findByResourceTypeAndResourceId(resourceType: String, resourceId: String): ResourceLockDO
}