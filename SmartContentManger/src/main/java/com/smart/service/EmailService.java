package com.smart.service;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
  
	public boolean sendEmail(String subject,String message,String to) {
		
		boolean f=false;
		/*
		 * String from="djonh7628"; String password="";
		 */
//		String from="millerwatson38@gmail.com";
//		String password="nigf tujk wpuj zcut";
		String host="smtp.gmail.com";
		Properties properties=System.getProperties();
		System.out.println("Properties  :-"+properties);
		//1. host
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", 465);
		properties.put("mail.smtp.ssl.enable", true);
		properties.put("mail.smtp.auth", true);
//		properties.put("mail.smtp.connectiontimeout", "6000"); // 5 seconds
//		properties.put("mail.smtp.timeout", "6000"); // 5 seconds

		//step1. get session
		
		Session session=Session.getInstance(properties,new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("millerwatson38@gmail.com", "nigf tujk wpuj zcut");
			}
		});
		  session.setDebug(true);
		  
		  //step 2: compose the message
		  MimeMessage message2=new MimeMessage(session);
		  try {
			message2.setFrom("millerwatson38@gmail.com");
			message2.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message2.setSubject(subject);
//			message2.setText(message);
			message2.setContent(message,"text/html");
			//send the message using transport
			Transport.send(message2);
			System.out.println("sent success");
			f=true;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		  
		  
		return f;
	}
	
}
