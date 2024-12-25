/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.utils;

public class CSSUtils {

    public static String sanitizeCSSClassName(String input) {
        // Remove illegal characters
        String sanitized = input.replaceAll("[^a-zA-Z0-9-_]", "");
        // Ensure it doesn't start with a digit
        if (sanitized.length() > 0 && Character.isDigit(sanitized.charAt(0))) {
            sanitized = "_" + sanitized;
        }
        return sanitized;
    }
    
}
