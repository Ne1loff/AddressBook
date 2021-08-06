package com.example.demo.services

import com.example.demo.dto.ContactDTO
import com.example.demo.dto.ContactInfoDTO
import com.example.demo.entities.Contact
import com.example.demo.entities.ContactInfoEmail
import com.example.demo.entities.ContactInfoNumber
import com.example.demo.repositories.ContactInfoEmailRepository
import com.example.demo.repositories.ContactInfoNumbersRepository
import com.example.demo.repositories.ContactRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class ContactServiceImpl(
    private val contactRepository: ContactRepository,
    private val contactInfoEmailRepository: ContactInfoEmailRepository,
    private val contactInfoNumbersRepository: ContactInfoNumbersRepository
) {
    fun findAll(): List<Contact> {
        return contactRepository.findAll()
    }

    fun findById(id: Long): Contact? {
        return contactRepository.findContactById(id)
    }

    fun createContact(contact: Contact): Contact {

        val contactEmails = contact.emails
        val contactNumbers = contact.numbers

        if (contactEmails.isNotEmpty()) contactInfoEmailRepository.saveAll(contactEmails)
        if (contactNumbers.isNotEmpty()) contactInfoNumbersRepository.saveAll(contactNumbers)

        contactRepository.save(contact)
        return contact
    }

    fun getAllContactInfo(id: Long): ContactInfoDTO {
        val contact = contactRepository.findContactById(id)
        return ContactInfoDTO(emails = contact.emails, numbers = contact.numbers)

    }

    @Transactional
    fun addContactInfo(id: Long, info: ContactInfoDTO): ContactInfoDTO {
        val existEmails: MutableList<ContactInfoEmail> = ArrayList()
        val existNumbers: MutableList<ContactInfoNumber> = ArrayList()

        val contact = contactRepository.findContactById(id)

        if (info.emails.isNotEmpty()) {
            for (contactEmail in info.emails) {
                if (!contactInfoEmailRepository.existsByEmail(contactEmail.email)) {
                    contact.emails.add(contactEmail)
                    contactInfoEmailRepository.save(contactEmail)
                } else {
                    existEmails.add(contactEmail)
                }
            }
        }

        if (info.numbers.isNotEmpty()) {
            for (contactNumbers in info.numbers) {
                if (!contactInfoNumbersRepository.existsByNumber(contactNumbers.number)) {
                    contact.numbers.add(contactNumbers)
                    contactInfoNumbersRepository.save(contactNumbers)
                } else {
                    existNumbers.add(contactNumbers)
                }
            }
        }

        return ContactInfoDTO(emails = existEmails, numbers = existNumbers)
    }

    fun getContactInfoEmails(id: Long): List<ContactInfoEmail> {
        val contact = contactRepository.findContactById(id)
        return contact.emails
    }

    fun getContactInfoNumbers(id: Long): List<ContactInfoNumber> {
        val contact = contactRepository.findContactById(id)
        return contact.numbers
    }

    @Transactional
    fun updateContact(id: Long, updatedContact: ContactDTO) {

        val contact = contactRepository.findContactById(id)

        contact.name = updatedContact.name ?: contact.name
        contact.region = updatedContact.region ?: contact.region
        contact.locality = updatedContact.locality ?: contact.locality
        contact.photo = updatedContact.photo ?: contact.photo
        contact.description = updatedContact.description ?: contact.description
    }

    fun deleteContact(id: Long): Boolean {
        return contactRepository.deleteContactById(id) > 0
    }

    //TODO(Check for Transactional)
    fun deleteContactInfoEmail(id: Long, emailId: Long): Boolean {
        val contact = contactRepository.findContactById(id)
        val email = contactInfoEmailRepository.findContactInfoEmailById(emailId)

        return if (contact.emails.contains(email))
            contactInfoEmailRepository.deleteContactInfoEmailById(emailId) > 0
        else
            false
    }

    //TODO(Check for Transactional)
    fun deleteContactInfoNumbers(id: Long, numberId: Long): Boolean {
        val contact = contactRepository.findContactById(id)
        val number = contactInfoNumbersRepository.findContactInfoNumberById(numberId)

        return if (contact.numbers.contains(number))
            contactInfoNumbersRepository.deleteContactInfoNumberById(numberId) > 0
        else
            false
    }

}