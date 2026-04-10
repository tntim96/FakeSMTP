package com.nilhcem.fakesmtp.log;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Provides an observable object to notify the {@code LogsPane} object when a new log is received.
 *
 * @author Nilhcem
 * @since 1.0
 */
public final class SMTPLogsObservable {

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	/**
	 * Notify the listeners when a new log is received.
	 *
	 * @param arg a String representing the received log.
	 */
	public void notifyAboutLog(Object arg) {
		support.firePropertyChange("log", null, arg);
	}

	/**
	 * Adds a property change listener to the observer list.
	 *
	 * @param listener the listener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a property change listener from the observer list.
	 *
	 * @param listener the listener to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}
}
