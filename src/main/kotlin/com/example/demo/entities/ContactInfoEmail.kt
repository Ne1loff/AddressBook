package com.example.demo.entities

import javax.persistence.*

@Entity
@Table(name="contacts_info_emails")
data class ContactInfoEmail(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name ="email")
    var email: String
)
