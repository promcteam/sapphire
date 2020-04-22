package com.gotofinal.darkrise.economy.cfg;

import com.gotofinal.darkrise.economy.DarkRiseEconomy;

/**
 * This class is being replaced... We're going to migrate to normal Spigot API things.
 */
@Deprecated
public class EconomyConfig {
    private int timeout;

    {
        timeout = DarkRiseEconomy.getInstance().getTimeout();
    }

    public int getTimeout() {
        return DarkRiseEconomy.getInstance().getTimeout();
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
