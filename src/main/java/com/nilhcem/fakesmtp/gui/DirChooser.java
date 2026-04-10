package com.nilhcem.fakesmtp.gui;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import javax.swing.JFileChooser;
import com.nilhcem.fakesmtp.core.Configuration;
import com.nilhcem.fakesmtp.core.I18n;
import com.nilhcem.fakesmtp.gui.info.SaveMsgField;
import com.nilhcem.fakesmtp.model.UIModel;

/**
 * Provides a graphical directory chooser dialog.
 * <p>
 * The directory chooser is used to select the folder where emails will be saved in.<br>
 * It can be launched from the menu bar, or from the main panel.
 * </p>
 *
 * @author Nilhcem
 * @since 1.0
 */
public final class DirChooser implements PropertyChangeListener {

	private final JFileChooser dirChooser = new JFileChooser();
	private final Component parent;
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	/**
	 * Creates a {@code JFileChooser} component and sets it to be for directories only.
	 *
	 * @param parent the component from where the chooser will be launched <i>(should be the main panel of the application)</i>.
	 */
	public DirChooser(Component parent) {
		this.parent = parent;
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser.setDialogTitle(String.format(I18n.INSTANCE.get("dirchooser.title"),
		    Configuration.INSTANCE.get("application.name")));
		dirChooser.setApproveButtonText(I18n.INSTANCE.get("dirchooser.approve.btn"));
	}

	/**
	 * Opens the folder selection.
	 * <p>
	 * This method will be called by an {@code PropertyChangeListener} trigger:
	 * </p>
	 * <ul>
	 *   <li>The {@link MenuBar};</li>
	 *   <li>Or the {@link SaveMsgField}.</li>
	 * </ul>
	 *
	 * @param evt the property change event containing the source.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();
		if (source instanceof MenuBar || source instanceof SaveMsgField) {
			openFolderSelection();
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
	 * Opens the folder selection dialog and notify observers once the directory is selected.
	 * <p>
	 * The only observer notified is the {@link SaveMsgField}.
	 * </p>
	 */
	private void openFolderSelection() {
		File filePath = new File(Configuration.INSTANCE.get("emails.default.dir"));
		dirChooser.setCurrentDirectory(filePath);

		int result = dirChooser.showOpenDialog(parent);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedDir = dirChooser.getSelectedFile();
			if (selectedDir != null) {
				UIModel.INSTANCE.setSavePath(selectedDir.getAbsolutePath());
				support.firePropertyChange("path", null, selectedDir.getAbsolutePath());
			}
		}
	}
}
