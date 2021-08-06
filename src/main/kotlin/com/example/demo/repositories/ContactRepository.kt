package com.example.demo.repositories

import com.example.demo.entities.Contact
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactRepository : JpaRepository<Contact, Long> {

    fun findContactById(id: Long): Contact

    fun findContactByName(name: String): Contact

    fun findContactByRegion(region: String): Contact

    fun deleteContactById(id: Long) : Long

}