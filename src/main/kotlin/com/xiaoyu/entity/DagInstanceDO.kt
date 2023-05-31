package com.xiaoyu.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "dag_instance")
data class DagInstanceDO(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "BIGINT(20) COMMENT '主键'")
    var id: Long?,

    @Column(
        name = "create_time",
        nullable = false,
        columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'"
    )
    var createTime: Date?,

    @Column(
        name = "modified_time",
        nullable = false,
        columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间'"
    )
    var modifiedTime: Date?,

    @Column(
        name = "instance_id",
        nullable = false,
        unique = true,
        columnDefinition = "VARCHAR(64) COMMENT 'DAG实例id'",
        length = 64
    )
    var instanceId: String?,

    @Column(
        name = "status",
        nullable = false,
        columnDefinition = "VARCHAR(32) COMMENT 'DAG状态: INIT, PROCESSING, SUCCEED, FAIL, CANCEL'",
        length = 32
    )
    var status: String?,

    @Column(name = "node_count", nullable = false, columnDefinition = "int(11) COMMENT 'DAG节点个数'")
    var nodeCount: Int?,

    @Column(name = "context", nullable = false, columnDefinition = "TEXT COMMENT '上下文信息'")
    var context: String?
){
    constructor() : this(
        id = null,
        createTime = null,
        modifiedTime = null,
        instanceId = null,
        status = null,
        nodeCount = null,
        context = null
    )
}