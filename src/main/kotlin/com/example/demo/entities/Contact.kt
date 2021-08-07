package com.example.demo.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import javax.persistence.*


@Entity
@Table(name = "contacts")
data class Contact(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name = "name")
    var name: String,

    @Column(name = "region")
    var region: String,

    @Column(name = "locality")
    var locality: String?,

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    val emails: MutableList<ContactInfoEmail> = ArrayList(),

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    val numbers: MutableList<ContactInfoNumber> = ArrayList(),

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "id", unique = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var photo: ContactPhoto?,

    @Column(name = "description")
    var description: String?
)