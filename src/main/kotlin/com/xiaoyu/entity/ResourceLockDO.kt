package com.xiaoyu.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
@Table(name = "resource_lock")
data class ResourceLockDO(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "BIGINT(20) COMMENT '主键'")
    var id: Long?,

    @CreationTimestamp
    @Column(name = "create_time", updatable = false, nullable = false, columnDefinition = "TIMESTAMP COMMENT '创建时间'")
    var createTime: Date?,

    @UpdateTimestamp
    @Column(name = "modified_time", nullable = false, columnDefinition = "TIMESTAMP COMMENT '修改时间'")
    var modifiedTime: Date?,

    @Column(name = "resource_type", nullable = false, columnDefinition = "VARCHAR(64) COMMENT '资源类型'", length = 64)
    var resourceType: String?,

    @Column(name = "resource_id", nullable = false, columnDefinition = "VARCHAR(64) COMMENT '资源id'", length = 64)
    var resourceId: String?,

    @Column(name = "available", nullable = false, columnDefinition = "char(2) COMMENT '资源是否可获取'", length = 2)
    var available: String?,
){
    constructor() : this(
        id = null,
        createTime = null,
        modifiedTime = null,
        resourceType = null,
        resourceId = null,
        available = null
    )
}