package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class User {
     @Id
     @GeneratedValue(strategy = GenerationType.AUTO)
	 private int id;
	 private String  name;
	@Column(unique = true)
	 private String  email;
	 private String  password;
	 private String  role;
	 private String  imageURL;
	 @Column(length = 500)
	 private String  about;
	 private boolean enabled;
	 //orpahanRemoval =true :- when parent entity unlink from child entity then orphan removal remove child 
	 @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "user")
	 private List<Contact>contacts=new ArrayList<>();
	
	 public User() {
			super();
			// TODO Auto-generated constructor stub
		}

	public User(int id, String name, String email, String password, String role, String imageURL, String about,
			boolean enabled) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.imageURL = imageURL;
		this.about = about;
		this.enabled = enabled;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts =contacts;
	}

//	@Override
//	public String toString() {
//		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
//				+ ", imageURL=" + imageURL + ", about=" + about + ", enabled=" + enabled + ", contacts=" + contacts
//				+ "]";
//	}
//    

	
	
	 
}
