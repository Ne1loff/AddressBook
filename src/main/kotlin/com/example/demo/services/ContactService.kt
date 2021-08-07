package com.example.demo.services

import com.example.demo.dto.ContactDTO
import com.example.demo.dto.ContactInfoDTO
import com.example.demo.entities.Contact
import com.example.demo.entities.ContactInfoEmail
import com.example.demo.entities.ContactInfoNumber
import com.example.demo.entities.ContactPhoto
import com.example.demo.exception.ApiInvalidDataAccessException
import com.example.demo.exception.ApiNotFoundException
import com.example.demo.repositories.ContactInfoEmailRepository
import com.example.demo.repositories.ContactInfoNumbersRepository
import com.example.demo.repositories.ContactPhotoRepository
import com.example.demo.repositories.ContactRepository
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import javax.transaction.Transactional

@Service
class ContactService(
    private val contactRepository: ContactRepository,
    private val contactInfoEmailRepository: ContactInfoEmailRepository,
    private val contactInfoNumbersRepository: ContactInfoNumbersRepository,
    private val contactPhotoRepository: ContactPhotoRepository
) {
    fun findAll(): List<Contact> {
        return contactRepository.findAll()
    }

    fun findById(id: Long): Contact {
        return contactRepository.findContactById(id) ?: throw ApiNotFoundException("Contact with id $id not found")
    }

    fun createContact(contact: Contact): Contact {

        val contactEmails = contact.emails
        val contactNumbers = contact.numbers

        val existEmails: MutableList<String> = ArrayList()
        val existNumbers: MutableList<String> = ArrayList()

        if (contactEmails.isNotEmpty()) {
            for (email in contactEmails) {
                if (!contactInfoEmailRepository.existsByEmail(email.email)) {
                    contact.emails.add(email)
                    contactInfoEmailRepository.save(email)
                } else {
                    existEmails.add(email.email)
                }
            }
        }

        if (contactNumbers.isNotEmpty()) {
            for (number in contactNumbers) {
                if (!contactInfoNumbersRepository.existsByNumber(number.number)) {
                    contact.numbers.add(number)
                    contactInfoNumbersRepository.save(number)
                } else {
                    existNumbers.add(number.number)
                }
            }
        }

        if (existEmails.isNotEmpty() || existNumbers.isNotEmpty()) {
            val message =
                "Email(s) ${existEmails.ifEmpty { "" }} and number(s) ${existNumbers.ifEmpty { "" }} already exists"
            throw ApiInvalidDataAccessException(message = message)
        }

        contactRepository.save(contact)
        return contact
    }

    fun getAllContactInfo(id: Long): ContactInfoDTO {
        val contact =
            contactRepository.findContactById(id) ?: throw ApiNotFoundException("Contact with id $id not found")
        return ContactInfoDTO(emails = contact.emails, numbers = contact.numbers)

    }

    @Transactional
    fun addContactInfo(id: Long, info: ContactInfoDTO): ContactInfoDTO {
        val existEmails: MutableList<ContactInfoEmail> = ArrayList()
        val existNumbers: MutableList<ContactInfoNumber> = ArrayList()

        val contact =
            contactRepository.findContactById(id) ?: throw ApiNotFoundException("Contact with id $id not found")

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
        val contact =
            contactRepository.findContactById(id) ?: throw ApiNotFoundException("Contact with id $id not found")
        return contact.emails
    }

    fun getContactInfoNumbers(id: Long): List<ContactInfoNumber> {
        val contact =
            contactRepository.findContactById(id) ?: throw ApiNotFoundException("Contact with id $id not found")
        return contact.numbers
    }

    @Transactional
    fun updateContact(id: Long, updatedContact: ContactDTO) {

        val contact =
            contactRepository.findContactById(id) ?: throw ApiNotFoundException("Contact with id $id not found")

        contact.name = updatedContact.name ?: contact.name
        contact.region = updatedContact.region ?: contact.region
        contact.locality = updatedContact.locality ?: contact.locality
        contact.description = updatedContact.description ?: contact.description
    }

    @Transactional
    fun deleteContact(id: Long): Boolean {
        return contactRepository.deleteContactById(id) > 0
    }

    @Transactional
    fun deleteContactInfoEmail(id: Long, emailId: Long): Boolean {
        val contact =
            contactRepository.findContactById(id)
                ?: throw ApiNotFoundException("Contact with id $id not found")
        val email =
            contactInfoEmailRepository.findContactInfoEmailById(emailId)
                ?: throw ApiNotFoundException("Email with id $emailId not found")

        return if (contact.emails.contains(email))
            contactInfoEmailRepository.deleteContactInfoEmailById(emailId) > 0
        else
            false
    }

    @Transactional
    fun deleteContactInfoNumbers(id: Long, numberId: Long): Boolean {
        val contact =
            contactRepository.findContactById(id)
                ?: throw ApiNotFoundException("Contact with id $id not found")
        val number =
            contactInfoNumbersRepository.findContactInfoNumberById(numberId)
                ?: throw ApiNotFoundException("Number with id $numberId not found")

        return if (contact.numbers.contains(number))
            contactInfoNumbersRepository.deleteContactInfoNumberById(numberId) > 0
        else
            false
    }


    fun uploadPhoto(id: Long, urlString: String) {
        val contact =
            contactRepository.findContactById(id)
                ?: throw ApiNotFoundException("Contact with id $id not found")

        val url = URL(urlString)
        var fileExtension = FilenameUtils.getExtension(url.path)
        if (fileExtension == "") fileExtension = "jpeg"

        val path = getPhotosPath(id, fileExtension)
        val photo = ContactPhoto(id, fileExtension)

        url.openStream().use { `in` -> Files.copy(`in`, Paths.get(path)) }
        contactPhotoRepository.save(photo)
    }

    @Transactional
    fun deletePhoto(id: Long) : Boolean {
        val contact = contactRepository.findContactById(id)
            ?: throw ApiNotFoundException("Contact with id $id not found")
        val photo = contactPhotoRepository.findContactPhotoById(id)
            ?: throw ApiNotFoundException("The contact's with id $id photo not found")

        val path = getPhotosPath(id, photo.fileExtension)
        val file = File(path)

        contactPhotoRepository.deleteContactPhotoById(id)

        return file.delete()
    }

    fun loadContactPhoto(id: Long) : File {
        val contact = contactRepository.findContactById(id)
            ?: throw ApiNotFoundException("Contact with id $id not found")
        val photo = contactPhotoRepository.findContactPhotoById(id)
            ?: throw ApiNotFoundException("The contact's with id $id photo not found")

        return File(getPhotosPath(id, photo.fileExtension))
    }

    fun getPhotosPath(id: Long, fileExtension: String) : String { //TODO(Change to path from config)
        return "/home/xinik/Postgres/ContactsPhotos/$id.$fileExtension"
    }
}