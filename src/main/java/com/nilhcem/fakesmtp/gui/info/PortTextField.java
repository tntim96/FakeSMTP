package com.nilhcem.fakesmtp.gui.info;

import com.nilhcem.fakesmtp.core.Configuration;
import com.nilhcem.fakesmtp.core.I18n;
import com.nilhcem.fakesmtp.model.UIModel;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Text field in which will be written the desired SMTP port.
 *
 * @author Nilhcem
 * @since 1.0
 */
public final class PortTextField implements PropertyChangeListener {

	private final JTextField portTextField = new JTextField();
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	/**
	 * Creates the port field object and adds a listener on change to alert the presentation model.
	 * <p>
	 * The default port's value is defined in the configuration.properties file.<br>
	 * Each time the port is modified, the port from the {@link UIModel} will be reset.
	 * </p>
	 */
	public PortTextField() {
		portTextField.setToolTipText(I18n.INSTANCE.get("porttextfield.tooltip"));
		portTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				warn();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				warn();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				warn();
			}

			private void warn() {
				UIModel.INSTANCE.setPort(portTextField.getText());
			}
		});

		portTextField.setText(Configuration.INSTANCE.get("smtp.default.port"));
		portTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				support.firePropertyChange("action", null, null);
			}
		});
	}

	/**
	 * Returns the JTextField object.
	 *
	 * @return the JTextField object.
	 */
	public JTextField get() {
		return portTextField;
	}

	/**
	 * Sets the specified port in the text field only if this latter is not {@code null}.
	 *
	 * @param portStr the port to set.
	 */
	public void setText(String portStr) {
		if (portStr != null && !portStr.isEmpty()) {
			portTextField.setText(portStr);
		}
	}

	/**
	 * Adds a property change listener (replaces addObserver).
	 *
	 * @param listener the listener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	/**
	 * Enables or disables the port text field.
	 * <p>
	 * When the element will receive an action from the {@link StartServerButton} object,
	 * it will enable or disable the port, so that the user can't modify it
	 * when the server is already launched.
	 * </p>
	 *
	 * @param evt the property change event containing the source.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof StartServerButton) {
			portTextField.setEnabled(!UIModel.INSTANCE.isStarted());
		}
	}
}
