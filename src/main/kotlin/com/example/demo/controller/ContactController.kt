package com.example.demo.controller

import com.example.demo.dto.ContactDTO
import com.example.demo.dto.ContactInfoDTO
import com.example.demo.entities.Contact
import com.example.demo.entities.ContactInfoEmail
import com.example.demo.entities.ContactInfoNumber
import com.example.demo.services.ContactServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(value = ["/api/contacts/"])
class ContactController(
    private val contactServiceImpl: ContactServiceImpl
) {

    @GetMapping
    fun getAllContacts(): List<Contact> {
        return contactServiceImpl.findAll()
    }

    @GetMapping("{id}")
    fun getContact(@PathVariable id: Long): Contact? {
        return contactServiceImpl.findById(id)
    }

    @GetMapping("{id}/info")
    fun getAllContactInfos(@PathVariable id: Long): ContactInfoDTO {
        return contactServiceImpl.getAllContactInfo(id)
    }

    @GetMapping("{id}/info/emails")
    fun getContactInfosEmails(@PathVariable id: Long): List<ContactInfoEmail> {
        return contactServiceImpl.getContactInfoEmails(id)
    }

    @GetMapping("{id}/info/numbers")
    fun getContactInfosNumbers(@PathVariable id: Long): List<ContactInfoNumber> {
        return contactServiceImpl.getContactInfoNumbers(id)
    }

    @PostMapping
    fun createContact(@RequestBody contact: Contact): HttpStatus {
        contactServiceImpl.createContact(contact)
        return HttpStatus.CREATED
    }

    @PostMapping("{id}/info")
    fun addContactInfo(@PathVariable id: Long, @RequestBody contactInfo: ContactInfoDTO): HttpStatus {
        contactServiceImpl.addContactInfo(id, contactInfo)
        return HttpStatus.OK
    }

    @PutMapping("{id}/edit")
    fun editContact(@PathVariable id: Long, @RequestBody updatedContact: ContactDTO): HttpStatus {
        contactServiceImpl.updateContact(id, updatedContact)
        return HttpStatus.OK
    }

    @DeleteMapping("{id}/delete")
    fun deleteContact(@PathVariable id: Long): HttpStatus {
        return if (contactServiceImpl.deleteContact(id)) HttpStatus.OK else HttpStatus.NO_CONTENT
    }

    @DeleteMapping("{id}/info/emails/{emailId}/delete")
    fun deleteContactInfoEmails(@PathVariable id: Long, @PathVariable emailId: Long): HttpStatus {
        return if (contactServiceImpl.deleteContactInfoEmail(id, emailId)) HttpStatus.OK else HttpStatus.NO_CONTENT
    }

    @DeleteMapping("{id}/info/numbers/{numberId}/delete")
    fun deleteContactInfoNumbers(@PathVariable id: Long, @PathVariable numberId: Long): HttpStatus {
        return if (contactServiceImpl.deleteContactInfoNumbers(id, numberId)) HttpStatus.OK else HttpStatus.NO_CONTENT
    }
}