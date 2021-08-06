package com.example.demo.dto

import com.example.demo.entities.ContactInfoEmail
import com.example.demo.entities.ContactInfoNumber

data class ContactInfoDTO(
    val emails: MutableList<ContactInfoEmail> = ArrayList(),
    val numbers: MutableList<ContactInfoNumber> = ArrayList()
)