package com.smart.dao;




import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
            //method for ,pagination:-record show in multiple pages not on one page.Pagination is used to display a large number of records in different parts.
	   /*what is page?:-A page is a sublist of a list of objects.
	                   It allows gain information about the position of it in the containing entire list.
	                  Interface Page<T> Type Parameters: T - All Superinterfaces: Iterable<T>
	                   All Known Implementing Classes: PageImpl public interface Page<T> extends Iterable<T>  */
	
	@Query("from Contact as c where c.user.id=:userId")
	public Page findContactsByUser(@Param("userId")int userId,Pageable page);
	//pageable object contain:- current page information
	//                          contact per page.
	
	//method for fire search query
	public List<Contact> findByNameContainingAndUser(String name,User user);
	
	
}
