package com.xiaoyu.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
@Table(name = "dag_node_instance")
data class DagNodeInstanceDO(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "BIGINT(20) COMMENT '主键'")
    var id: Long?,

    @Column(name = "create_time", updatable = false, nullable = false, columnDefinition = "TIMESTAMP COMMENT '创建时间'")
    @CreationTimestamp
    var createTime: Date?,

    @Column(name = "modified_time", nullable = false, columnDefinition = "TIMESTAMP COMMENT '修改时间'")
    @UpdateTimestamp
    var modifiedTime: Date?,

    @Column(name = "dag_instance_id", nullable = false, columnDefinition = "VARCHAR(64) COMMENT 'DAG实例id'",)
    var dagInstanceId: String?,

    @Column(name = "processor", nullable = false, columnDefinition = "VARCHAR(64) COMMENT '节点执行器'")
    var processor: String?,

    @Column(name = "node_id", nullable = false, columnDefinition = "int(11) COMMENT '节点id'")
    var nodeId: Int?,

    @Column(name = "parent_node_id", nullable = false, columnDefinition = "VARCHAR(256) COMMENT '父节点id列表'",)
    var parentNodeId: String?,

    @Column(
        name = "status",
        nullable = false,
        columnDefinition = "VARCHAR(32) COMMENT 'DAG节点状态: INIT, PROCESSING, SUCCEED, FAIL, CANCEL'",
    )
    var status: String?,

    @Column(name = "context", nullable = false, columnDefinition = "TEXT COMMENT '节点上下文'")
    var context: String?
){
    constructor(
        dagInstanceId: String?,
        processor: String?,
        parentNodeId: String?,
        nodeId: Int?,
        status: String?,
        context: String?
    ) : this(
        id = null,
        createTime = null,
        modifiedTime = null,
        dagInstanceId = dagInstanceId,
        processor = processor,
        parentNodeId = parentNodeId,
        nodeId = nodeId,
        status = status,
        context = context
    )
}
