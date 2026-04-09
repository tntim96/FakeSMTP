package com.nilhcem.fakesmtp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nilhcem.fakesmtp.core.exception.BindPortException;
import com.nilhcem.fakesmtp.core.exception.InvalidHostException;
import com.nilhcem.fakesmtp.core.exception.InvalidPortException;
import com.nilhcem.fakesmtp.core.exception.OutOfRangePortException;
import com.nilhcem.fakesmtp.core.test.TestConfig;
import org.junit.jupiter.api.Test;

public class UIModelTest {
	@Test
	public void uniqueInstance() {
		UIModel a = UIModel.INSTANCE;
		UIModel b = UIModel.INSTANCE;
		assertSame(a, b);
	}

	@Test
	public void shouldHaveZeroMsgReceivedFirst() {
		assertEquals(0, UIModel.INSTANCE.getNbMessageReceived());
	}

	@Test
	public void testInvalidPort() throws BindPortException, OutOfRangePortException, InvalidPortException, InvalidHostException {
		UIModel.INSTANCE.setPort("INVALID");
		assertThrows(InvalidPortException.class, () -> UIModel.INSTANCE.toggleButton());
	}

	@Test
	public void testInvalidHost() throws BindPortException, OutOfRangePortException, InvalidPortException, InvalidHostException {
		UIModel.INSTANCE.setHost("INVALID");
                UIModel.INSTANCE.setPort(Integer.toString(TestConfig.PORT_UNIT_TESTS));
		assertThrows(InvalidHostException.class, () -> UIModel.INSTANCE.toggleButton());
	}

	@Test
	public void testIsStarted() throws BindPortException, OutOfRangePortException, InvalidPortException, InvalidHostException {
                UIModel.INSTANCE.setHost("127.0.0.1");
		UIModel.INSTANCE.setPort(Integer.toString(TestConfig.PORT_UNIT_TESTS));
		assertFalse(UIModel.INSTANCE.isStarted());

		UIModel.INSTANCE.toggleButton();
		assertTrue(UIModel.INSTANCE.isStarted());

		UIModel.INSTANCE.toggleButton();
		assertFalse(UIModel.INSTANCE.isStarted());
	}
}
