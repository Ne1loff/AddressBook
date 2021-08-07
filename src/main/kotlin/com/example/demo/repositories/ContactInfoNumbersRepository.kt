package com.example.demo.repositories

import com.example.demo.entities.ContactInfoNumber
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactInfoNumbersRepository : JpaRepository<ContactInfoNumber, Long> {
    fun findContactInfoNumberById(id:Long): ContactInfoNumber?
    fun existsByNumber(number: String): Boolean
    fun deleteContactInfoNumberById(id: Long): Long
    fun deleteByNumber(number: String): Long
}