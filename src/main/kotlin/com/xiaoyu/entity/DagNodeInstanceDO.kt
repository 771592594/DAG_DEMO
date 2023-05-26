package com.xiaoyu.entity

import jakarta.persistence.*

@Entity
@Table(name = "dag_node_instance")
data class DagNodeInstanceDO(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "dag_instance_id")
    var dagInstanceId: String?,

    @Column(name = "processor")
    var processor: String?,

    @Column(name = "node_id")
    var nodeId: Int?,

    @Column(name = "parent_node_id")
    var parentNodeId: String?,

    @Column(name = "status")
    var status: String?,

    @Column(name = "context")
    var context: String?
){
    constructor() : this(
        id = null,
        dagInstanceId = null,
        processor = null,
        nodeId = null,
        parentNodeId = null,
        status = null,
        context = null
    )
}
