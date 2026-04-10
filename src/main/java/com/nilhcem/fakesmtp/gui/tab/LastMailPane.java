package com.nilhcem.fakesmtp.gui.tab;

import com.nilhcem.fakesmtp.gui.info.ClearAllButton;
import com.nilhcem.fakesmtp.model.EmailModel;
import com.nilhcem.fakesmtp.server.MailSaver;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Scrolled text area where will be displayed the last received email.
 *
 * @author Nilhcem
 * @since 1.0
 */
public final class LastMailPane implements PropertyChangeListener {

	private final JScrollPane lastMailPane = new JScrollPane();
	private final JTextArea lastMailArea = new JTextArea();

	/**
	 * Creates the text area and disables the possibility to edit it.
	 */
	public LastMailPane() {
		lastMailArea.setEditable(false);
		lastMailPane.getViewport().add(lastMailArea, null);
	}

	/**
	 * Returns the JScrollPane object.
	 *
	 * @return the JScrollPane object.
	 */
	public JScrollPane get() {
		return lastMailPane;
	}

	/**
	 * Updates the content of the text area.
	 * <p>
	 * This method will be called by an observable element.
	 * </p>
	 * <ul>
	 *   <li>If the source is a {@link MailSaver} object, the text area will contain the content of the last received email;</li>
	 *   <li>If the source is a {@link ClearAllButton} object, the text area will be cleared.</li>
	 * </ul>
	 *
	 * @param evt the property change event containing the source and new data.
	 */
	@Override
	public synchronized void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();

		if (source instanceof MailSaver) {
			EmailModel model = (EmailModel) evt.getNewValue();
			lastMailArea.setText(model.getEmailStr());
		} else if (source instanceof ClearAllButton) {
			lastMailArea.setText("");
		}
	}
}
