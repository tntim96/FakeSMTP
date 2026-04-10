package com.nilhcem.fakesmtp.core.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nilhcem.fakesmtp.model.EmailModel;
import com.nilhcem.fakesmtp.model.UIModel;
import com.nilhcem.fakesmtp.server.MailSaver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class MailServerTest {
	private static MailSaver saver;

	@BeforeAll
	public static void createMailSaver() {
		saver = new MailSaver();
	}

	@Test
	public void testGetLock() {
		assertSame(saver, saver.getLock());
	}

	@Test
	public void testSaveDeleteEmail() throws Exception {
		final String from = "from@example.com";
		final String to = "to@example.com";
		final String subject = "Hello";
		final String content = "How are you?";

		final InputStream data = fromString(getMockEmail(from, to, subject, content));

		// Replacement for Observer: PropertyChangeListener
		PropertyChangeListener mockListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// evt.getNewValue() contains the EmailModel sent by the server
				EmailModel model = (EmailModel) evt.getNewValue();

				assertEquals(from, model.getFrom());
				assertEquals(to, model.getTo());
				assertEquals(subject, model.getSubject());
				assertNotNull(model.getEmailStr());
				assertNotNull(model.getFilePath());

				File file = new File(model.getFilePath());
				assertTrue(file.exists());

				// Delete logic
				UIModel.INSTANCE.getListMailsMap().put(0, model.getFilePath());
				saver.deleteEmails();
				assertFalse(file.exists());
			}
		};

		// Standard method names for PropertyChangeSupport
		saver.addPropertyChangeListener(mockListener);
		saver.saveEmailAndNotify(from, to, data);
		saver.removePropertyChangeListener(mockListener);
	}

	private String getMockEmail(String from, String to, String subject, String content) {
		String br = System.getProperty("line.separator");

		StringBuilder sb = new StringBuilder()
			.append("Line 1 will be removed").append(br)
			.append("Line 2 will be removed").append(br)
			.append("Line 3 will be removed").append(br)
			.append("Line 4 will be removed").append(br)
			.append("Date: Thu, 15 May 2042 04:42:42 +0800 (CST)").append(br)
			.append(String.format("From: \"%s\" <%s>%n", from, from))
			.append(String.format("To: \"%s\" <%s>%n", to, to))
			.append("Message-ID: <17000042.0.1300000000042.JavaMail.wtf@OMG00042>").append(br)
			.append(String.format("Subject: %s%n", subject))
			.append("MIME-Version: 1.0").append(br)
			.append("Content-Type: text/plain; charset=us-ascii").append(br)
			.append("Content-Transfer-Encoding: 7bit").append(br).append(br)
			.append(content).append(br).append(br);
		return sb.toString();
	}

	private InputStream fromString(String str) {
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		return new ByteArrayInputStream(bytes);
	}
}
