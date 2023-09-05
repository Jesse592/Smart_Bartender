package com.jmkrijgsman.smartbartender.connection;

import java.util.ArrayList;
import java.util.List;

public class PumpConfigurationCache {
    private static PumpConfigurationCache pumpConfigurationCache;

    public static final PumpConfigurationCache getInstance()
    {
        if (pumpConfigurationCache == null) pumpConfigurationCache = new PumpConfigurationCache();

        return pumpConfigurationCache;
    }

    private List<PumpConfiguration> pumpConfigurations = new ArrayList<>();

    public List<PumpConfiguration> getPumpConfigurations() {
        return pumpConfigurations;
    }

    public void setPumpConfigurations(List<PumpConfiguration> pumpConfigurations) {
        this.pumpConfigurations = pumpConfigurations;
    }
}
