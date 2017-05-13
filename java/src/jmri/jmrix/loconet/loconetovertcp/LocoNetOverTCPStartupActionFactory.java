package jmri.jmrix.loconet.loconetovertcp;

import apps.startup.AbstractStartupActionFactory;
import java.util.Locale;

/**
 * {@link apps.startup.StartupActionFactory} for the
 * {@link jmri.jmrix.loconet.loconetovertcp.ServerAction}.
 *
 * @author Randall Wood Copyright (C) 2017
 */
public class LocoNetOverTCPStartupActionFactory extends AbstractStartupActionFactory {

    @Override
    public String getTitle(Class<?> clazz, Locale locale) throws IllegalArgumentException {
        if (!clazz.equals(ServerAction.class)) {
            throw new IllegalArgumentException();
        }
        return Bundle.getMessage(locale, "StartServerAction"); // NOI18N
    }

    @Override
    public Class<?>[] getActionClasses() {
        return new Class[]{ServerAction.class};
    }

}

