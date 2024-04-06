package studio.magemonkey.sapphire.cfg;

import studio.magemonkey.sapphire.Sapphire;

/**
 * This class is being replaced... We're going to migrate to normal Spigot API things.
 */
@Deprecated
public class SapphireConfig {
    private int timeout;

    {
        timeout = Sapphire.getInstance().getTimeout();
    }

    public int getTimeout() {
        return Sapphire.getInstance().getTimeout();
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
