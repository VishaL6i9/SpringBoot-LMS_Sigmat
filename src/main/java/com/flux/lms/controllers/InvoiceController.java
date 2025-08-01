package com.flux.lms.controllers;

import com.flux.lms.models.*;
import com.flux.lms.repository.InvoiceRepository;
import com.flux.lms.repository.UserRepo;
import com.flux.lms.services.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private StripeService stripeService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createInvoice(@RequestBody InvoiceRequest invoiceRequest) {
        try {
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(invoiceRequest.getInvoiceNumber());
            invoice.setDate(LocalDate.parse(invoiceRequest.getDate()));
            invoice.setDueDate(LocalDate.parse(invoiceRequest.getDueDate()));

            // Handle User
            UserRequest userRequest = invoiceRequest.getUser();
            Users user;
            if (userRequest.getId() != null && !userRequest.getId().isEmpty()) {
                // Try to find existing user by ID
                Optional<Users> existingUser = userRepository.findById(Long.parseLong(userRequest.getId()));
                if (existingUser.isPresent()) {
                    user = existingUser.get();
                    // Update existing user details if necessary
                    user.setEmail(userRequest.getEmail());
                    String[] nameParts = userRequest.getName().split(" ", 2);
                    user.setFirstName(nameParts[0]);
                    if (nameParts.length > 1) {
                        user.setLastName(nameParts[1]);
                    }
                    UserProfile userProfile = user.getUserProfile();
                    userProfile.setPhone(userRequest.getPhone());
                    userProfile.setAddress(userRequest.getAddress());
                } else {
                    // If ID provided but not found, create new user
                    user = new Users();
                    user.setEmail(userRequest.getEmail());
                    String[] nameParts = userRequest.getName().split(" ", 2);
                    user.setFirstName(nameParts[0]);
                    if (nameParts.length > 1) {
                        user.setLastName(nameParts[1]);
                    }
                    UserProfile userProfile = new UserProfile();
                    userProfile.setPhone(userRequest.getPhone());
                    userProfile.setAddress(userRequest.getAddress());
                    user.setUserProfile(userProfile);
                }
            } else {
                // Create new user if no ID provided
                user = new Users();
                user.setEmail(userRequest.getEmail());
                String[] nameParts = userRequest.getName().split(" ", 2);
                user.setFirstName(nameParts[0]);
                if (nameParts.length > 1) {
                    user.setLastName(nameParts[1]);
                }
                UserProfile userProfile = new UserProfile();
                userProfile.setPhone(userRequest.getPhone());
                userProfile.setAddress(userRequest.getAddress());
                user.setUserProfile(userProfile);
            }
            user = userRepository.save(user);
            invoice.setUser(user);

            // Handle Invoice Items
            Set<InvoiceItem> invoiceItems = new HashSet<>();
            for (InvoiceItemRequest itemRequest : invoiceRequest.getItems()) {
                InvoiceItem item = new InvoiceItem();
                item.setDescription(itemRequest.getDescription());
                item.setQuantity(itemRequest.getQuantity());
                item.setUnitPrice(itemRequest.getUnitPrice());
                item.setTotal(itemRequest.getTotal());
                item.setInvoice(invoice); // Set the parent invoice
                invoiceItems.add(item);
            }
            invoice.setItems(invoiceItems);

            invoice.setSubtotal(invoiceRequest.getSubtotal());
            invoice.setTaxRate(invoiceRequest.getTaxRate());
            invoice.setTaxAmount(invoiceRequest.getTaxAmount());
            invoice.setDiscount(invoiceRequest.getDiscount());
            invoice.setTotal(invoiceRequest.getTotal());
            invoice.setStatus(Invoice.InvoiceStatus.valueOf(invoiceRequest.getStatus().toUpperCase()));
            invoice.setNotes(invoiceRequest.getNotes());

            Invoice savedInvoice = invoiceRepository.save(invoice);

            // Create Stripe Invoice and get URL
            String stripeInvoiceUrl = stripeService.createStripeInvoice(savedInvoice);

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("invoice", savedInvoice);
            response.put("stripeInvoiceUrl", stripeInvoiceUrl);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
