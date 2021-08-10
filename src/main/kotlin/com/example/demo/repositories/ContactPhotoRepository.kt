package com.example.demo.repositories

import com.example.demo.entities.ContactPhoto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactPhotoRepository : JpaRepository<ContactPhoto, Long> {

    fun findContactPhotoById(id: Long) : ContactPhoto?

    fun existsContactPhotoById(id: Long) : Boolean

    fun deleteContactPhotoById(id: Long) : Long
}