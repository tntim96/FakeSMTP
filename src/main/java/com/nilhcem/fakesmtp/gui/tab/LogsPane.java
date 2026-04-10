package com.nilhcem.fakesmtp.gui.tab;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.AppenderAttachable;
import com.nilhcem.fakesmtp.core.Configuration;
import com.nilhcem.fakesmtp.gui.info.ClearAllButton;
import com.nilhcem.fakesmtp.log.SMTPLogsAppender;
import com.nilhcem.fakesmtp.log.SMTPLogsObservable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Scrolled text area where will be displayed the SMTP logs.
 *
 * @author Nilhcem
 * @since 1.0
 */
public final class LogsPane implements PropertyChangeListener {

	private final JScrollPane logsPane = new JScrollPane();
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
	private final JTextArea logsArea = new JTextArea();

	/**
	 * Creates the text area, sets it as non-editable and sets an observer to intercept logs.
	 */
	public LogsPane() {
		logsArea.setEditable(false);
		logsPane.getViewport().add(logsArea, null);
		addObserverToSmtpLogAppender();
	}

	/**
	 * Returns the JScrollPane object.
	 *
	 * @return the JScrollPane object.
	 */
	public JScrollPane get() {
		return logsPane;
	}

	/**
	 * Adds this object to the SMTP logs appender observable, to intercept logs.
	 * <p>
	 * The goal is to be informed when the log appender will received some debug SMTP logs.<br>
	 * When a log is written, the appender will notify this class which will display it in the text area.
	 * </p>
	 */
	private void addObserverToSmtpLogAppender() {
		Logger smtpLogger = LoggerFactory.getLogger(org.subethamail.smtp.server.Session.class);
		String appenderName = Configuration.INSTANCE.get("logback.appender.name");

		@SuppressWarnings("unchecked")
		SMTPLogsAppender<ILoggingEvent> appender = (SMTPLogsAppender<ILoggingEvent>)
		    ((AppenderAttachable<ILoggingEvent>) smtpLogger).getAppender(appenderName);
		if (appender == null) {
			LoggerFactory.getLogger(LogsPane.class).error("Can't find logger: {}", appenderName);
		} else {
			appender.getObservable().addPropertyChangeListener(this);
		}
	}

	/**
	 * Updates the content of the text area.
	 * <p>
	 * This method will be called by an observable element.
	 * </p>
	 * <ul>
	 *   <li>If the source is a {@link SMTPLogsObservable} object, the text area will display the received log.</li>
	 *   <li>If the source is a {@link ClearAllButton} object, the text area will be cleared.</li>
	 * </ul>
	 *
	 * @param evt the property change event containing the source and new data.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();

		if (source instanceof SMTPLogsObservable) {
			// Update and scroll pane to the bottom
			Object log = evt.getNewValue();
			logsArea.append(String.format("%s - %s%n", dateFormat.format(new Date()), log));
			logsArea.setCaretPosition(logsArea.getText().length());
		} else if (source instanceof ClearAllButton) {
			// Remove text
			logsArea.setText("");
		}
	}
}
