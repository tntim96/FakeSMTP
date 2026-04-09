package com.nilhcem.fakesmtp.core.server;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nilhcem.fakesmtp.core.exception.OutOfRangePortException;
import com.nilhcem.fakesmtp.server.SMTPServerHandler;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;


public class SMTPServerHandlerTest {
	@Test
	public void uniqueInstance() {
		SMTPServerHandler a = SMTPServerHandler.INSTANCE;
		SMTPServerHandler b = SMTPServerHandler.INSTANCE;
		assertSame(a, b);
	}

	@Test
	public void testOutOfRangePort() {
		assertThrows(OutOfRangePortException.class, () -> SMTPServerHandler.INSTANCE.startServer(9999999, InetAddress.getLocalHost()));
	}

	@Test
	public void stopShouldDoNothingIfServerIsAlreadyStopped() {
		SMTPServerHandler.INSTANCE.stopServer();
		SMTPServerHandler.INSTANCE.stopServer();
		SMTPServerHandler.INSTANCE.stopServer();
	}
}
