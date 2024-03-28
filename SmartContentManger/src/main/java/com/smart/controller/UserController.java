package com.smart.controller;

import java.io.File;
import com.razorpay.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private MyOrderRepository myOrderRepository;
	
	
  /* 
   *  method for adding common data to response
   *  create common method which run for every handler,
   *  And this annotation will make this method to run for every handler of this class 
   *  
   *  */
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		/**principal is object of spring security which could identify the unique value or principal value from table .
		 * this could be username and email,etc.
		 */		  
		String uname = principal.getName();
		System.out.println("username"+uname);
		   
		/** getting the user from database using unique value (Email) which got through principal*/
		User user = userRepository.getUserByUserName(uname);
	    System.out.println("user"+user);
	    
		/*
		 * send that user into model
		 * Model basically take data from controller and send to viewResolver ,
		 * viewResolver send that data to client(from Model-View Architecture)
		 */		
	    model.addAttribute("user",user);
	}
	
	
	//dashBoard handler
	@RequestMapping("/index")
    public String dashBoard(Model model,Principal principal) {
		model.addAttribute("title","User home(DashBoard)");
		return "normal/user_dashboard";
    }
	
	// Add_contact Form handler
	@GetMapping("/addContact")
	public String AddContactForm(Model model) {
		
		//this title show on page tab above on url
		model.addAttribute("title","Add Contact"); 
		
		// this contact object send to addContact.html page form tag (th:object)
		model.addAttribute("contact",new Contact());
		return "normal/addContact";
	}
	
	
	/**processing contact form,after adding successfully again show addContact.html page for add another contact */
	@PostMapping("/process-contact")
	public String  processContact(@ModelAttribute Contact contact,                        //form ke ander jo field h uska sara data contact variable me aa jayga
			                      @RequestParam("profileImage")MultipartFile file  ,     //for store the file which client upload on server(addContact.html)
			                       Principal principal,
			                       HttpSession session) {
		try {
			//user ka data DataBase me save karne ke liye phle user chayye hoga jo login h 
			//use nikalne ke liye security se principal class hamri help karti h
			//ye user ka unique id nikal ke de dega
			String name=principal.getName();
			
			//on this method we write query for fetching data from DB of give unique id,all data store in user
			User user=this.userRepository.getUserByUserName(name);
			
			// if error occur then throw exception
			/*
			if(3>2) {
 			throw new Exception();
			}
			*/
			
			
			//processing the image 
			  if(file.isEmpty()) {
				  //if file is empty then print message 
				  System.out.println("file i empty");
				  contact.setImage("contact.png");
			  }else {
				//otherwise store the file in folder and update its url in database
				  contact.setImage(file.getOriginalFilename());
				  
				  //this  class save file in given folder
				  File sfile = new ClassPathResource("static/Image").getFile();
				  
				  Path path = Paths.get(sfile.getAbsolutePath()+File.separator+file.getOriginalFilename()); //here you can add date also for unique name of file every time ,even the same image

//				  Files.copy(pathSource, pathTarget, copyOption)--this method upload image in DataBAse
				  Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
				  System.out.println("image upload successfully");
			}
			
			
			//by-directional mapping h to contact bhi save karna hoga
			contact.setUser(user);
			
			//getContects method give list of all contact ,and add method add this contact to this user
			user.getContacts().add(contact);
			
			//save this user in database
			this.userRepository.save(user);
			
			System.out.println("DATA :" +contact);
			
			//success message 
			session.setAttribute("message",  new Message("your contact is added!! Add more ..","success"));
		}catch (Exception e) {
			System.out.println("ERROR:"+e.getMessage());
			e.printStackTrace();
			//failure message 
			session.setAttribute("message",  new Message("Somthing went wrong!! ","danger"));

			
		}
			return "normal/addContact";
	}
	
	     //show contact handler
         //per page5[n]-where n is no. of page
	     //current page=[0]
			@GetMapping("/show_contact/{page}")  // user se page bhi le-lenge aur pathvarible ue fetch kar lega url se
			public String showContact(@PathVariable("page") Integer page,Model model,Principal principal) {
				String userUniqueId=principal.getName();   //this get email of user  from database through which user is login
				User user = this.userRepository.getUserByUserName(userUniqueId);// repository fire query and get all detail of user of this email
				
				//page  no. ,page size
				Pageable pageable = PageRequest.of(page,5);
				
				Page contacts = (Page) this.contactRepository.findContactsByUser(user.getId(),pageable);// this repository fire query and get all contacts of given id from dataBase
				model.addAttribute("contacts",contacts); // this send data to view page 
				model.addAttribute("currentPage",page); // this send data to view page 
				model.addAttribute("totalPages",contacts.getTotalPages()); // this send data to view page 
				return "normal/show_contact";
			}
	
//	   Handler for show particular contact details
			@GetMapping("/contact/{Cid}")
			public String showContactDetails(@PathVariable("Cid")Integer Cid,Model model,Principal principal) {
				System.out.println("C_ID:"+ Cid);
				
				
				
				Optional<Contact> contactOpt = this.contactRepository.findById(Cid);  //this fire query and get all detail of given contact ,id which we given,and result wrapped in optional(its like list)  
				Contact contact = contactOpt.get(); //to get data from optional we use get method ,which is method of optinal class
				
				String username = principal.getName();//get unique id  through which user login
				User user = this.userRepository.getUserByUserName(username);//using that id fetch data from data base 
				
				//user.getId()-vo id jo url sho ho rhi h aur jisse banda login h
				//contact.useer.id-vo id jo database me us user ki h jiska aap contact detail dekna cha rhe ho
				//agar ye dono id same h to iska mtlb ,vahi user h isse permission h apna data dekhne ki to ham contact ki detail server pe send kar denge ,, but agar same nhi h to usse persion nhi h
				if(user.getId()==contact.getUser().getId())
					{model.addAttribute("contact",contact);//send contact to client page
					}

				return "normal/contact_detail";
			}
			
	//handler for delete contact
			@GetMapping("/delete/{Cid}")
			public String deleteContact(@PathVariable("Cid") Integer Cid,Model model,HttpSession session,Principal principal) {
				//find user through id from url in data database by fire query
				Optional<Contact> contactOptional = this.contactRepository.findById(Cid);
				    System.out.println(Cid);
				if (contactOptional.isPresent()) {

					Contact contact = contactOptional.get();
					
//					//unlink user from contact
//					contact.setUser(null);
//					System.out.println("contact:"+contact);
//					
//					//delete contact
//					this.contactRepository.delete(contact);
					
					//removing bug from delete contact
					  User user = this.userRepository.getUserByUserName(principal.getName());
					   user.getContacts().remove(contact);  //is contact ki id se jo contact match kar jayga from contacts use remove kar dega ye method
					   this.userRepository.save(user);
					
					
					    System.out.println("delete successfully");
					//send success message to user through session
					session.setAttribute("message",new Message("contact delete successfully..!!","success"));
					
					
				} else {
				    // Handle the case where the contact with the given ID was not found
					System.out.println("given id is not found!!");
				}
				
				return"redirect:/user/show_contact/0";
			}
			
			
		//handler for update form
			@PostMapping("/update_contact/{Cid}")
			public String updateForm(@PathVariable("Cid")Integer Cid,Model model) {
				model.addAttribute("tittle", "update_contact");
				//find contact and send to html(view) resolver
				  Contact contact = this.contactRepository.findById(Cid).get();
				  model.addAttribute("contact",contact);
				  
				return "normal/update_form";
			}
			
			
			//handler for update contact--image store in multipartfile and contact data store in Contact variable
			@PostMapping("/process-update")
			public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file,HttpSession session,Model model,Principal principal) {
//				System.out.println("contact_name:"+contact.getName());
//				System.out.println("contact_Id:"+contact.getCid());
				
				try {
					
					//old contact details
					Contact oldContactDetails = this.contactRepository.findById(contact.getCid()).get();
			
					
					//update image 
					//if file is not empty!
					if(!file.isEmpty()) {
					    //perform your activity	
						
						//delete old photo
						File deleteFile = new ClassPathResource("static/Image").getFile();
						File file2 = new File(deleteFile,oldContactDetails.getImage());
						file2.delete();
						
						//update new photo
						File saveFile = new ClassPathResource("static/Image").getFile();
						Path path = Paths.get(saveFile.getAbsolutePath()+File.separator, file.getOriginalFilename());
						Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
						contact.setImage(file.getOriginalFilename());
					
					}else {
						//if file empty(mtlb user koi image nhi set karna chatta)then no need to update image, sent old data as it as
						contact.setImage(oldContactDetails.getImage());
					}
					//get id and update that id as well
					User user = this.userRepository.getUserByUserName(principal.getName());
					contact.setUser(user);
					//save the update
					this.contactRepository.save(contact);
					session.setAttribute("message", new Message("your contact is updated", "success"));
				} catch (Exception e) {
                      e.printStackTrace();
				}
				return "redirect:/user/contact/"+contact.getCid();
			}
			
			
			//handler for profile
			@GetMapping("/profile")
			public String yourProfile(Model model) {
				
				return "normal/profile";
			}
			
			//open setting handler
			@GetMapping("/setting")
			public String openSetting() {
				return "normal/setting";
			}
			
			//change password handler
			@PostMapping("/changePassword")
			public String changePassword(@RequestParam("oldPassword")String oldPassword,@RequestParam("newPassword")String newPassword,Principal principal,HttpSession session) {
				System.out.println("Old Password :-"+oldPassword);
				System.out.println("new Password :-"+newPassword);
				User currentUser = this.userRepository.getUserByUserName(principal.getName());
				if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
					//change password
					currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
					this.userRepository.save(currentUser);
					session.setAttribute("message", new Message("your password successfully change", "success"));
				}
				else {
					//error
					session.setAttribute("message", new Message("your old password is not match", "danger"));
					return "redirect:/user/setting";
					
				}
				return "redirect:/user/index";
				
				
			}
			
			//handler for first time create order for payment
			@PostMapping("/create_order")
			@ResponseBody
			public String createOrder(@RequestBody Map<String, Object>data,Principal principal) throws Exception {
				System.out.println("order function executed");
				System.out.println(data);
				
				int amt=Integer.parseInt(data.get("amount").toString());
				System.out.println("amt is : "+amt);
				//create client(key,secret) ==from rozarpay site which we generate
				 var client= new RazorpayClient("rzp_test_aetInMhOh05zJ3", "3XoB97MoSvRzLHjcmf7zTTFN");
				 
				 
				 //creating order
				 JSONObject object=new JSONObject();
				 object.put("amount", amt*100);//our amount in rupees ,but we have convert it into paise
				 object.put("currency", "INR");
				 object.put("receipt", "txn_23456");
				
				 Order order = client.orders.create(object);
				 System.out.println("created order is :- "+order);
				 
				 // save order in your database
				 MyOrder myOrder=new MyOrder();
				 myOrder.setAmount(order.get("amount")+"");
				 myOrder.setOrderId(order.get("id"));
				 myOrder.setPaymentId(null);
				 myOrder.setStatus("created");
				 myOrder.setReceipt(order.get("receipt"));
				 myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
				 
				 this.myOrderRepository.save(myOrder);
				 
				return order.toString();
			}
			
			//handler for update order data in data base
			@PostMapping("/update_order")
			public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object>data){
				//get order id from data base where data you want to update	
				MyOrder myOrder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());	
				myOrder.setPaymentId(data.get("payment_id").toString());
				myOrder.setStatus(data.get("status").toString());
				this.myOrderRepository.save(myOrder);
				
				System.out.println(data);
				return ResponseEntity.ok(Map.of("msg","updated"));
			}
}

