/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.apputils.queryparameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Location;

public interface ContentParameters extends FOPParameters {

    public static final String SILENT = "silent";
    public static final String SINGLEREF = "singleRef";

    public default void buildDialog(Component target) {
    }

    public boolean isSilenced();

    public default boolean isSilencedByDefault() {
        return true;
    }

    public default boolean isSingleReferee() {
        return false;
    }

    @Override
    public default HashMap<String, List<String>> readParams(Location location,
            Map<String, List<String>> parametersMap) {
        // handle FOP and Group by calling superclass
        HashMap<String, List<String>> params = FOPParameters.super.readParams(location, parametersMap);

        List<String> silentParams = params.get(SILENT);
        // silent is the default. silent=false will cause sound
        boolean silentMode = silentParams == null || silentParams.isEmpty()
                || silentParams.get(0).toLowerCase().equals("true");
        if (!isSilencedByDefault()) {
            // for referee board, default is noise
            silentMode = silentParams != null && !silentParams.isEmpty()
                    && silentParams.get(0).toLowerCase().equals("true");
        }
        switchSoundMode(silentMode, false);
        updateParam(params, SILENT, !isSilenced() ? "false" : "true");

        List<String> refParams = params.get(SINGLEREF);
        boolean sr = refParams != null && !refParams.isEmpty()
                && refParams.get(0).toLowerCase().equals("true");
        setSingleReferee(sr);
        switchSingleRefereeMode((Component) this, sr, false);
        updateParam(params, SINGLEREF, isSingleReferee() ? "true" : null);

        return params;
    }

    public void setSilenced(boolean silent);

    public default void setSingleReferee(boolean b) {
    }

    public default void switchSingleRefereeMode(Component component, boolean b, boolean updateURL) {
        setSingleReferee(b);
        if (updateURL) {
            updateURLLocation(getLocationUI(), getLocation(), SINGLEREF, b ? "true" : "false");
        }
    }

    public default void switchSoundMode(boolean silent, boolean updateURL) {
        if (updateURL) {
            updateURLLocation(getLocationUI(), getLocation(), SILENT, silent ? "true" : "false");
        }
        setSilenced(silent);
    }
}
