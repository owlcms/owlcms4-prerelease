/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.uievents;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;

import app.owlcms.i18n.Translator;

public class AppEvent {

	public static class AppNotification {

		private String message;

		public AppNotification(String message) {
			this.message = message;
		}

		public void doNotification() {
			UI.getCurrent().access(() -> {
				Notification notification = new Notification();
				Div div = new Div();
				div.setText(this.message + "\u00A0\u00A0\u00A0" + "\u2715");
				div.addClickListener(e -> notification.close());
				notification.add(div);
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
				notification.setPosition(Position.MIDDLE);
				notification.setDuration(0);
				notification.open();
			});
		}
	}

	public static class CloseUI {
		public void closeUI() {
			UI.getCurrent().access(() -> {
				Notification notification = new Notification();
				Div div = new Div();
				div.setText(Translator.translate("App.Closing") + "\u00A0\u00A0\u00A0" + "\u2715");
				div.addClickListener(e -> notification.close());
				notification.add(div);
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
				notification.setPosition(Position.MIDDLE);
				notification.setDuration(0);
				notification.open();
			});
		}
	}
}
