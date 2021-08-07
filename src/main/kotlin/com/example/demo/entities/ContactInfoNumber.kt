package com.example.demo.entities

import javax.persistence.*

@Entity
@Table(name ="contacts_info_numbers")
data class ContactInfoNumber(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name = "number")
    var number: String
)
