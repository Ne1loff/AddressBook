package com.example.demo.entities

import javax.persistence.*
import javax.validation.constraints.Email

@Entity
@Table(name="contacts_info_emails")
data class ContactInfoEmail(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name ="email")
    @Email(message = "Email should be valid")
    var email: String
)
