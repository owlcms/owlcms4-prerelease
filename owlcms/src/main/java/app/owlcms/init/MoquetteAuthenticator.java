/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.init;

import java.nio.charset.StandardCharsets;

import org.slf4j.LoggerFactory;

import app.owlcms.Main;
import app.owlcms.data.config.Config;
import app.owlcms.utils.StartupUtils;
import ch.qos.logback.classic.Logger;
import io.moquette.broker.security.IAuthenticator;

public class MoquetteAuthenticator implements IAuthenticator {
	Logger logger = (Logger) LoggerFactory.getLogger(MoquetteAuthenticator.class);

	public MoquetteAuthenticator() {
		// logger.setLevel(Level.TRACE);
	}

	@Override
	public boolean checkValid(String clientId, String username, byte[] password) {
		String clientPasswordString = new String(password, StandardCharsets.UTF_8);

		if (clientPasswordString.contentEquals(Main.mqttStartup)) {
			// special case -- owlcms is calling it's own moquette locally
			// the shared secret is the milliseconds at which the server started.
			this.logger.trace("owlcms MQTT connection from {}", clientId);
			return true;
		}

		String expectedUserName = Config.getCurrent().getMqttUserName();
		if (expectedUserName == null || expectedUserName.isBlank()) {
			// no check, anonymous allowed
			this.logger.debug("no user name configured, anonymous MQTT access allowed");
			return true;
		}
		if (!expectedUserName.contentEquals(username)) {
			// wrong user name provided
			this.logger./**/warn("wrong MQTT username, {} is denied: {}", clientId, username);
			return false;
		}

		String expectedClearTextPassword = StartupUtils.getStringParam("mqttPassword");
		this.logger.trace("client password string : {}", clientPasswordString);
		if (expectedClearTextPassword != null) {
			// clear text comparison
			boolean plainTextMatch = expectedClearTextPassword.contentEquals(clientPasswordString);
			this.logger.debug("clear text match {}", plainTextMatch);
			return plainTextMatch;
		} else {
			String dbHashedPassword = Config.getCurrent().getMqttPassword();
			if (dbHashedPassword == null || dbHashedPassword.isBlank()) {
				this.logger.debug("no password configured, MQTT access allowed to {}", username);
				return true;
			} else {
				String hashedPassword = Config.getCurrent().encodeUserPassword(clientPasswordString, dbHashedPassword);
				boolean shaMatch = dbHashedPassword.contentEquals(hashedPassword);
				if (shaMatch) {
					this.logger.debug("correct password provided, MQTT access allowed to {}", username);
				} else {
					this.logger./**/warn("wrong MQTT password, incorrect password from {}", clientId);
				}
				return shaMatch;
			}
		}
	}

}
