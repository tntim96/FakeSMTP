package com.nilhcem.fakesmtp.integration;

import com.nilhcem.fakesmtp.core.Configuration;
import com.nilhcem.fakesmtp.core.test.TestConfig;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;


@Disabled("This needs to be run manually with a running instance of FakeSMTP")
public final class SendEmailsIT {

	private static final Logger logger = LoggerFactory.getLogger(SendEmailsIT.class);


	@BeforeAll
	public static void displayInfo() {
		logger.info("Launching integration tests...");
		logger.info("You need to run the project and launch the SMTP server on port {} before testing.", String.valueOf(TestConfig.PORT_INTEGRATION_TESTS));
	}


	@Test
	public void sendSimpleTestEmail() throws MessagingException {
		// 1. Set up connection properties
		Message message = getMessage();
		message.setFrom(new InternetAddress("user@gmail.com"));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("foo@bar.com"));
		message.setSubject("Simple email");
		message.setText("This is a simple plain text email :-)");

		// 4. Send it via Angus Mail
		Transport.send(message);
	}


	private static @NonNull Message getMessage() {
		Properties props = new Properties();
		props.put("mail.smtp.host", TestConfig.HOST);
		props.put("mail.smtp.port", String.valueOf(TestConfig.PORT_INTEGRATION_TESTS));
		props.put("mail.smtp.starttls.enable", "true");

		// Note: If your integration tests require authentication, add:
		// props.put("mail.smtp.auth", "true");

		// 2. Create the mail session
		Session session = Session.getInstance(props, null);

		// 3. Build the email message
		Message message = new MimeMessage(session);
		return message;
	}


	@Test
	public void sendEmailWithAttachment() throws MessagingException, IOException {
		// 1. Set up connection properties
		Message message = getMessage();
		message.setFrom(new InternetAddress("me@example.org", "Me"));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("jdoe@somewhere.org"));
		// Note: To keep the display name "John Doe" like your original code, use:
		// message.setRecipients(Message.RecipientType.TO, new Address[]{ new InternetAddress("jdoe@somewhere.org", "John Doe") });

		message.setSubject("File attachment");

		// 4. Create the Multipart container to house the body and the attachment
		Multipart multipart = new MimeMultipart();

		// 5. Add the text body part
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setText("This email contains an enclosed file.");
		multipart.addBodyPart(textPart);

		// 6. Create and attach the file part
		MimeBodyPart attachmentPart = new MimeBodyPart();
		File file = new File("src/main/resources" + Configuration.INSTANCE.get("application.icon.path"));

		attachmentPart.attachFile(file); // Natively handles transmission encoding and disposition
		attachmentPart.setFileName("icon.gif"); // Overrides the disk filename for the recipient
		attachmentPart.setDescription("Image file");

		multipart.addBodyPart(attachmentPart);

		// 7. Bind the multipart content to the message and send
		message.setContent(multipart);
		Transport.send(message);
	}


	@Test
	public void sendHTMLFormattedEmail() throws MessagingException, UnsupportedEncodingException {
		// 1. Set up connection properties
		Message message = getMessage();
		message.setFrom(new InternetAddress("me@example.org", "Me"));
		message.setRecipients(Message.RecipientType.TO, new Address[]{
		    new InternetAddress("jdoe@somewhere.org", "John Doe")
		});
		message.setSubject("HTML email");

		// 4. Create an "alternative" multi-part container
		// This groups the plain text and HTML versions together so the client can choose
		MimeMultipart multipart = new MimeMultipart("alternative");

		// 5. Add the plain text fallback part first
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setText("Your email client does not support HTML messages", "utf-8");
		multipart.addBodyPart(textPart);

		// 6. Add the HTML part second (clients prefer the last part added if they support it)
		MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent("<html><body>This is an <b>HTML</b> email.<br /><br /></body></html>", "text/html; charset=utf-8");
		multipart.addBodyPart(htmlPart);

		// 7. Bind the multipart content to the message and send
		message.setContent(multipart);
		Transport.send(message);
	}


	@Test
	public void sendEmailWithBase64Subject() throws MessagingException {
		// 1. Set up connection properties
		Properties props = new Properties();
		props.put("mail.smtp.host", TestConfig.HOST);
		props.put("mail.smtp.port", String.valueOf(TestConfig.PORT_INTEGRATION_TESTS));

		// 2. Create the mail session
		Session session = Session.getInstance(props, null);

		// 3. Build the email message
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("spammy@example.org"));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("foo@bar.com"));

		// 4. Pass 'null' for the charset to tell Jakarta Mail
		// that the string is already safely RFC 2047 encoded
		message.setSubject("=?UTF-8?B?4pyIIEJvc3RvbiBhaXJmYXJlIGRlYWxzIC0gd2hpbGUgdGhleSBsYXN0IQ==?=");

		message.setText("Not really interesting, huh?");

		// 5. Send it via Angus Mail
		Transport.send(message);
	}


	@Test
	public void sendEmailToManyRecipientsWithTwoHeaders() throws MessagingException {
		// 1. Set up connection properties
		Properties props = new Properties();
		props.put("mail.smtp.host", TestConfig.HOST);
		props.put("mail.smtp.port", String.valueOf(TestConfig.PORT_INTEGRATION_TESTS));

		// 2. Create the mail session
		Session session = Session.getInstance(props, null);

		// 3. Build the email message
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("info@example.com"));

		// 4. Add multiple recipients as a comma-separated list
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("test1@example.com, test2@example.com"));

		// 5. Add custom headers directly to the MIME message [1]
		message.addHeader("Foo", "Bar");
		message.addHeader("Foo2", "Bar2");

		message.setSubject("Hi");
		message.setText("Just to check if everything is OK");

		// 6. Send the message
		Transport.send(message);
	}


	@Test
	public void sendEmailWithDots() throws MessagingException {
		// 1. Set up connection properties
		Properties props = new Properties();
		props.put("mail.smtp.host", TestConfig.HOST);
		props.put("mail.smtp.port", String.valueOf(TestConfig.PORT_INTEGRATION_TESTS));

		// 2. Create the mail session and enable debug output
		Session session = Session.getInstance(props, null);
		session.setDebug(true); // Replaces email.setDebug(true)

		// 3. Build the email message
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("user@example.com"));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("foo@example.com"));
		message.setSubject("Two dots separated with a new line");

		// Jakarta Mail / Angus Mail safely handles lone dots on newlines automatically
		// during the SMTP transparent data phase (dot-stuffing).
		message.setText(".\n.");

		// 4. Send the message
		Transport.send(message);
	}
}
