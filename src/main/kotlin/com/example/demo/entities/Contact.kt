package com.example.demo.entities

import javax.persistence.*


@Entity
data class Contact(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    var name: String,
    var region: String,
    var locality: String?,

    @OneToMany(fetch = FetchType.LAZY)
    val emails: MutableList<ContactInfoEmail> = ArrayList(),

    @OneToMany(fetch = FetchType.LAZY)
    val numbers: MutableList<ContactInfoNumber> = ArrayList(),

    var photo: String?,

    var description: String?
)