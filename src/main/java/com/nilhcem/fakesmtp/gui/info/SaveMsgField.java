package com.nilhcem.fakesmtp.gui.info;

import com.nilhcem.fakesmtp.core.ArgsHandler;
import com.nilhcem.fakesmtp.core.I18n;
import com.nilhcem.fakesmtp.gui.DirChooser;
import com.nilhcem.fakesmtp.model.UIModel;

import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Text field in which will be written the path where emails will be automatically saved.
 *
 * @author Nilhcem
 * @since 1.0
 */
public final class SaveMsgField implements PropertyChangeListener {

	private final JTextField saveMsgField = new JTextField(UIModel.INSTANCE.getSavePath());
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	/**
	 * Creates a text field and adds a mouse listener, to display the directory chooser dialog when a user clicks on the field.
	 * <p>
	 * The text field will be disabled by default to avoid the user to type any folder directly.<br>
	 * Instead, he can use the directory chooser dialog to select the path he wants.
	 * </p>
	 */
	public SaveMsgField() {
		saveMsgField.setToolTipText(I18n.INSTANCE.get("savemsgfield.tooltip"));

		// Disable edition but keep the same background color
		Color bg = saveMsgField.getBackground();
		saveMsgField.setEditable(false);
		saveMsgField.setBackground(bg);

		if (!ArgsHandler.INSTANCE.memoryModeEnabled()) {
			// Add a MouseListener
			saveMsgField.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
					openFolderSelection();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				private void openFolderSelection() {
					support.firePropertyChange("selection", null, null);
				}
			});
		}
	}

	/**
	 * Returns the JTextField object.
	 *
	 * @return the JTextField object.
	 */
	public JTextField get() {
		return saveMsgField;
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
	 * Updates the content of the JTextField with the new directory value.
	 * <p>
	 * Once a directory has been chosen by the {@link DirChooser}, the latter will
	 * notify this class, so that it can update its content.
	 * </p>
	 *
	 * @param evt the property change event containing the source.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof DirChooser) {
			saveMsgField.setText(UIModel.INSTANCE.getSavePath());
		}
	}
}
