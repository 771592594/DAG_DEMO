package com.xiaoyu.entity

import jakarta.persistence.*

@Entity
@Table(name = "dag_instance")
data class DagInstanceDO(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "instance_id")
    var instanceId: String?,

    @Column(name = "status")
    var status: String?,

    @Column(name = "node_count")
    var nodeCount: Int?,

    @Column(name = "context")
    var context: String?
){
    constructor() : this(
        id = null,
        instanceId = null,
        status = null,
        nodeCount = null,
        context = null
    )
}