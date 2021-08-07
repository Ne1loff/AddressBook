package com.example.demo.repositories

import com.example.demo.entities.ContactInfoEmail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactInfoEmailRepository : JpaRepository<ContactInfoEmail, Long> {
    fun findContactInfoEmailById(id: Long): ContactInfoEmail?
    fun existsByEmail(email: String): Boolean
    fun deleteContactInfoEmailById(id: Long):Long
    fun deleteByEmail(email: String): Long
}