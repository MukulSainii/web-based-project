package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
public class SearchController {
     @Autowired
     private UserRepository	userRepository;
     @Autowired
     private ContactRepository contactRepository;
     
     //search handler
     @GetMapping("/search/{query}")
     public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
    	 System.out.println("your search query:"+query);
    	 User user = this.userRepository.getUserByUserName(principal.getName());
    	 List<Contact> contact = this.contactRepository.findByNameContainingAndUser(query, user);
    	 System.out.println("your query contact is : "+contact);
    	 return ResponseEntity.ok(contact);
     }
	
	
}
//
//@RestController
//public class SearchController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ContactRepository contactRepository;
//
//    @GetMapping("/search/{query}")
//    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal,
//                                    @RequestParam(value = "currentPage", required = false, defaultValue = "0") Integer currentPage) {
//        try {
//            User user = this.userRepository.getUserByUserName(principal.getName());
//            List<Contact> contact = this.contactRepository.findByNameContainingAndUser(query, user);
//            return ResponseEntity.ok(contact);
//        } catch (NumberFormatException e) {
//            // Handle the case where currentPage is not a valid integer
//            return ResponseEntity.badRequest().body("Invalid currentPage parameter");
//        } catch (Exception e) {
//            // Handle other exceptions if needed
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
//        }
//    }
//}
//
