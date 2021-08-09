package com.example.demo.controller

import com.example.demo.dto.ContactDTO
import com.example.demo.dto.ContactInfoDTO
import com.example.demo.entities.Contact
import com.example.demo.entities.ContactInfoEmail
import com.example.demo.entities.ContactInfoNumber
import com.example.demo.exception.ApiBadRequestException
import com.example.demo.services.ContactService
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.nio.file.Files
import kotlin.io.path.Path


@RestController
@RequestMapping(value = ["/api/contacts/"])
class ContactController(
    private val contactService: ContactService
) {

    @GetMapping
    fun getAllContacts(): List<Contact> {
        return contactService.findAll()
    }

    @GetMapping("{id}")
    fun getContact(@PathVariable id: Long): Contact? {
        return contactService.findById(id)
    }

    @GetMapping("{id}/info")
    fun getAllContactInfos(@PathVariable id: Long): ContactInfoDTO {
        return contactService.getAllContactInfo(id)
    }

    @GetMapping("{id}/info/emails")
    fun getContactInfosEmails(@PathVariable id: Long): List<ContactInfoEmail> {
        return contactService.getContactInfoEmails(id)
    }

    @GetMapping("{id}/info/numbers")
    fun getContactInfosNumbers(@PathVariable id: Long): List<ContactInfoNumber> {
        return contactService.getContactInfoNumbers(id)
    }

    @PostMapping
    fun createContact(@RequestBody contact: Contact): HttpStatus {
        contactService.createContact(contact)
        return HttpStatus.CREATED
    }

    @PostMapping("{id}/info")
    fun addContactInfo(@PathVariable id: Long, @RequestBody contactInfo: ContactInfoDTO): HttpStatus {
        contactService.addContactInfo(id, contactInfo)
        return HttpStatus.OK
    }

    @PutMapping("{id}/edit")
    fun editContact(@PathVariable id: Long, @RequestBody updatedContact: ContactDTO): HttpStatus {
        contactService.updateContact(id, updatedContact)
        return HttpStatus.OK
    }

    @GetMapping("/report")
    fun getReport(): MutableMap<String, MutableList<Any>> {
        return contactService.getReportUpdated()
    }

    @GetMapping("/report/old")
    fun getOldReport(
        @RequestParam("region", defaultValue = "true") region: Boolean,
        @RequestParam("locality", defaultValue = "true") locality: Boolean,
        @RequestParam("alphabetically", defaultValue = "true") alphabetically: Boolean
    ): Any {
        return contactService.getReport(region, locality, alphabetically)
    }

    @GetMapping("{id}/photo")
    fun getContactPhoto(@PathVariable id: Long): ResponseEntity<InputStreamResource> {
        val filePath = contactService.loadContactPhoto(id)
        val fileResource = FileSystemResource(filePath)

        return ResponseEntity.ok()
            .contentLength(fileResource.contentLength())
            .contentType(
                MediaType.parseMediaType(Files.probeContentType(Path(filePath))))
            .body(InputStreamResource(fileResource.inputStream))
    }

    @PostMapping("{id}/photo/")
    fun uploadContactPhotoFromUrl(@PathVariable id: Long, @RequestParam("url") url: String?): HttpStatus {
        contactService.uploadPhoto(id, url ?: throw ApiBadRequestException("Url is null"))
        return HttpStatus.OK
    }

    @DeleteMapping("{id}/photo/delete")
    fun deleteContactPhoto(@PathVariable id: Long): HttpStatus {
        contactService.deletePhoto(id, fromService = false)
        return HttpStatus.OK
    }

    @DeleteMapping("{id}/delete")
    fun deleteContact(@PathVariable id: Long): HttpStatus {
        return if (contactService.deleteContact(id)) HttpStatus.OK else HttpStatus.NO_CONTENT
    }

    @DeleteMapping("{id}/info/emails/{emailId}/delete")
    fun deleteContactInfoEmails(@PathVariable id: Long, @PathVariable emailId: Long): HttpStatus {
        return if (contactService.deleteContactInfoEmail(id, emailId)) HttpStatus.OK else HttpStatus.NO_CONTENT
    }

    @DeleteMapping("{id}/info/numbers/{numberId}/delete")
    fun deleteContactInfoNumbers(@PathVariable id: Long, @PathVariable numberId: Long): HttpStatus {
        return if (contactService.deleteContactInfoNumbers(id, numberId)) HttpStatus.OK else HttpStatus.NO_CONTENT
    }
}