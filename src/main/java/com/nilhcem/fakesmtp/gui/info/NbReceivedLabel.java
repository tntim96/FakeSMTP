package com.nilhcem.fakesmtp.gui.info;

import com.nilhcem.fakesmtp.model.UIModel;
import com.nilhcem.fakesmtp.server.MailSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Label class to display the number of received emails.
 *
 * @author Nilhcem
 * @since 1.0
 */
public final class NbReceivedLabel implements PropertyChangeListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(NbReceivedLabel.class);

	private final JLabel nbReceived = new JLabel("0");

	/**
	 * Creates the label class (with a bold font).
	 */
	public NbReceivedLabel() {
		Font boldFont = new Font(nbReceived.getFont().getName(), Font.BOLD, nbReceived.getFont().getSize());
		nbReceived.setFont(boldFont);
	}

	/**
	 * Returns the JLabel object.
	 *
	 * @return the JLabel object.
	 */
	public JLabel get() {
		return nbReceived;
	}

	/**
	 * Actions which will be done when the component will be notified by a property change event.
	 * <ul>
	 *   <li>If the source element is a {@link MailSaver} object, the method will increment
	 *   the number of received messages and update the {@link UIModel};</li>
	 *   <li>If the source element is a {@link ClearAllButton}, the method will reinitialize
	 *   the number of received messages and update the {@link UIModel};</li>
	 *   <li>When running on OS X the method will also update the Dock Icon with the number of
	 *   received messages.</li>
	 * </ul>
	 *
	 * @param evt the property change event containing the source.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();

		if (source instanceof MailSaver) {
			UIModel model = UIModel.INSTANCE;
			int countMsg = model.getNbMessageReceived() + 1;
			String countMsgStr = Integer.toString(countMsg);

			model.setNbMessageReceived(countMsg);
			updateDockIconBadge(countMsgStr);
			nbReceived.setText(countMsgStr);
		} else if (source instanceof ClearAllButton) {
			UIModel.INSTANCE.setNbMessageReceived(0);
			updateDockIconBadge("");
			nbReceived.setText("0");
		}
	}

	private void updateDockIconBadge(String badgeValue) {
		// No-op in headless or unsupported environments
		if (GraphicsEnvironment.isHeadless() || !Taskbar.isTaskbarSupported()) {
			return;
		}

		Taskbar taskbar = Taskbar.getTaskbar();
		try {
			// Prefer text badges when supported; pass null to clear the badge
			if (taskbar.isSupported(Taskbar.Feature.ICON_BADGE_TEXT)) {
				taskbar.setIconBadge(badgeValue);
			} else if (taskbar.isSupported(Taskbar.Feature.ICON_BADGE_NUMBER)) {
				// Some platforms accept only numbers: pass a numeric string or null
				String numeric = (badgeValue != null && badgeValue.matches("\\d+")) ? badgeValue : null;
				taskbar.setIconBadge(numeric);
			}
		} catch (UnsupportedOperationException | SecurityException e) {
			LOGGER.debug("Dock badging not supported/allowed: {}", e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Failed to set dock badge", e);
		}
	}
}
