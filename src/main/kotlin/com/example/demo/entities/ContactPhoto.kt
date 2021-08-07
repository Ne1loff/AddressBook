package com.example.demo.entities

import javax.persistence.*

@Entity
@Table(name = "contacts_photos")
data class ContactPhoto(

    @Id
    @MapKey(name = "columns_id")
    @Column(name = "id")
    val id: Long,

    @Column(name = "file_extension")
    val fileExtension: String

)
