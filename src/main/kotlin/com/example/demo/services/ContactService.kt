package com.example.demo.services

import com.example.demo.dto.ContactDTO
import com.example.demo.dto.ContactInfoDTO
import com.example.demo.entities.Contact
import com.example.demo.entities.ContactInfoEmail
import com.example.demo.entities.ContactInfoNumber
import com.example.demo.entities.ContactPhoto
import com.example.demo.exception.ApiBadRequestException
import com.example.demo.exception.ApiNotFoundException
import com.example.demo.repositories.ContactInfoEmailRepository
import com.example.demo.repositories.ContactInfoNumbersRepository
import com.example.demo.repositories.ContactPhotoRepository
import com.example.demo.repositories.ContactRepository
import org.apache.commons.io.FilenameUtils
import org.hibernate.Session
import org.springframework.stereotype.Service
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
class ContactService(
    private val contactRepository: ContactRepository,
    private val contactInfoEmailRepository: ContactInfoEmailRepository,
    private val contactInfoNumbersRepository: ContactInfoNumbersRepository,
    private val contactPhotoRepository: ContactPhotoRepository,
    private val entityManager: EntityManager
) {
    fun findAll(): List<Contact> {
        return contactRepository.findAll()
    }

    fun findById(id: Long): Contact {
        return contactRepository.findContactById(id) ?: throw ApiNotFoundException("Contact with id $id not found")
    }

    @Transactional
    fun createContact(contact: Contact): Contact {

        val contactEmails = contact.emails
        val contactNumbers = contact.numbers

        val emailsForAdding: MutableList<ContactInfoEmail> = ArrayList()
        val numbersForAdding: MutableList<ContactInfoNumber> = ArrayList()

        val existEmails: MutableList<String> = ArrayList()
        val existNumbers: MutableList<String> = ArrayList()

        if (contactEmails.isNotEmpty()) {
            for (email in contactEmails) {
                if (!contactInfoEmailRepository.existsByEmail(email.email)) {
                    emailsForAdding.add(email)
                } else {
                    existEmails.add(email.email)
                }
            }
        }

        if (contactNumbers.isNotEmpty()) {
            for (number in contactNumbers) {
                if (!contactInfoNumbersRepository.existsByNumber(number.number)) {
                    numbersForAdding.add(number)
                } else {
                    existNumbers.add(number.number)
                }
            }
        }

        if (existEmails.isNotEmpty() || existNumbers.isNotEmpty()) {
            val message =
                "Email(s) ${existEmails.ifEmpty { "" }} and number(s) ${existNumbers.ifEmpty { "" }} already exists"
            throw ApiBadRequestException(message = message)
        }

        contact.emails = emailsForAdding
        contact.numbers = numbersForAdding

        contactInfoEmailRepository.saveAll(emailsForAdding)
        contactInfoNumbersRepository.saveAll(numbersForAdding)

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
    fun deletePhoto(id: Long): Boolean {
        contactRepository.findContactById(id)
            ?: throw ApiNotFoundException("Contact with id $id not found")
        val photo = contactPhotoRepository.findContactPhotoById(id)
            ?: throw ApiNotFoundException("The contact's with id $id photo not found")

        val path = getPhotosPath(id, photo.fileExtension)
        val file = File(path)

        contactPhotoRepository.deleteContactPhotoById(id)

        return file.delete()
    }

    fun loadContactPhoto(id: Long): File {
        contactRepository.findContactById(id)
            ?: throw ApiNotFoundException("Contact with id $id not found")
        val photo = contactPhotoRepository.findContactPhotoById(id)
            ?: throw ApiNotFoundException("The contact's with id $id photo not found")

        return File(getPhotosPath(id, photo.fileExtension))
    }

    fun getReport(region: Boolean, locality: Boolean, alphabetically: Boolean): Any {
        val session = entityManager.unwrap(Session::class.java)
        val desc = if (alphabetically) "" else " desc"
        var hql: String

        if (!region && !locality) {
            hql = "from Contact order by region$desc"
            return session.createQuery(hql).resultList
        }
        if (region) {
            val regionsMap: MutableMap<String, Any> = HashMap()

            hql = "select DISTINCT region from Contact order by region$desc"
            val regions = session.createQuery(hql).resultList

            for (reg in regions) {
                if (reg is String) {
                    if (locality) {
                        val localityMap: MutableMap<String, List<Contact>> = HashMap()

                        hql = "select DISTINCT locality from Contact " +
                                "where region = :region and locality IS NOT NULL order by locality$desc"
                        val localities = session.createQuery(hql).setParameter("region", reg).resultList

                        for (local in localities) {
                            if (local is String) {
                                localityMap.putIfAbsent(local,
                                    if (alphabetically) contactRepository.findContactsByLocalityOrderByName(local)
                                    else contactRepository.findContactsByLocalityOrderByNameDesc(local)
                                )
                            } else {
                                throw ApiNotFoundException("Localities not found")
                            }
                        }

                        hql = "from Contact where region = :region and locality IS NULL order by name$desc"
                        val contactsWithoutLocality = session.createQuery(hql)
                            .setParameter("region", reg)
                            .resultList

                        val listOfContacts: MutableList<Any> = ArrayList()
                        listOfContacts.add(localityMap)
                        listOfContacts.add(contactsWithoutLocality)

                        regionsMap.putIfAbsent(reg, listOfContacts)
                    } else {
                        regionsMap.putIfAbsent(reg,
                            if (alphabetically) contactRepository.getContactsByRegionOrderByName(reg)
                            else contactRepository.getContactsByRegionOrderByNameDesc(reg)
                        )
                    }
                } else {
                    throw ApiNotFoundException("Regions not found")
                }
            }
            return regionsMap
        }

        // logic for (region = false, locality =true, alphabetically = any)
        val localityMap: MutableMap<String, List<Contact>> = HashMap()
        hql = "select DISTINCT locality from Contact where locality IS NOT NULL order by locality$desc"
        val localities = session.createQuery(hql).resultList
        for (local in localities) {
            if (local is String) {
                localityMap.putIfAbsent(local,
                    if (alphabetically) contactRepository.findContactsByLocalityOrderByName(local)
                    else contactRepository.findContactsByLocalityOrderByNameDesc(local))
            }
        }

        return localityMap
    }

    fun getPhotosPath(id: Long, fileExtension: String): String { //TODO(Change to path from config)
        return "/home/xinik/Postgres/ContactsPhotos/$id.$fileExtension"
    }
}