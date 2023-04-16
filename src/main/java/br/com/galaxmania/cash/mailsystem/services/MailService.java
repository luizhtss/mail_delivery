package br.com.galaxmania.cash.mailsystem.services;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import br.com.galaxmania.cash.mailsystem.MailDelivery;
import br.com.galaxmania.cash.mailsystem.OrderObject;
import br.com.galaxmania.cash.mailsystem.utils.FileUtil;

public class MailService {
	
	private MailDelivery mailDelivery;
	

	private OrderObject oo;
	
	private Session session;
	private Properties props;
	
	public MailService(MailDelivery mailDelivery) {
		this.mailDelivery = mailDelivery;
	}
	
   public void runService() {
      props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", this.mailDelivery.getHOST());
      props.put("mail.smtp.port", this.mailDelivery.getPORT());
      
      session = Session.getInstance(props,
         new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(mailDelivery.getUSER_NAME(), mailDelivery.getPASSWORD());
            }
	});

     
   }
   
   public void setOrderObject(OrderObject oo) {
	   this.oo = oo;
   }
   
   public void sendMail() {
	   try {
		   if (!oo.isReadyToSendEmail())return;
		   
       Message message = new MimeMessage(session);

	   message.setFrom(new InternetAddress(this.mailDelivery.getEMAIL()));

	   message.setRecipients(Message.RecipientType.TO,
             InternetAddress.parse(oo.getEmail()));

	   message.setSubject("Chegou o seu Cash !");

	   message.setContent(FileUtil.email.replace("%codigo%", oo.getKey()), "text/html");

	   Transport.send(message);
	   
	   mailDelivery.mailLog("Email enviado com sucesso ("+oo.getEmail() + " - "+oo.getKey()+" - "+oo.getOrderId()+")");

     } catch (MessagingException e) {
    	 mailDelivery.mailLog("Falha ao enviar o email!");
	   e.printStackTrace();
     }
   }
   
}