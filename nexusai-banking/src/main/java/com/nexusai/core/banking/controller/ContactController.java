package com.nexusai.core.banking.controller;

import com.nexusai.core.banking.domain.model.ContactEntity;
import com.nexusai.core.banking.domain.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactRepository contactRepository;

    @GetMapping
    public ResponseEntity<List<ContactEntity>> getMyContacts(@RequestParam Long userId) {
        return ResponseEntity.ok(contactRepository.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ContactEntity> addContact(@RequestBody ContactEntity contact) {
        return ResponseEntity.ok(contactRepository.save(contact));
    }
}
