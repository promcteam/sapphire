package com.gotofinal.darkrise.economy.cfg;

import org.diorite.cfg.annotations.CfgClass;
import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.defaults.CfgIntDefault;

@CfgClass(name = "EconomyConfig")
public class EconomyConfig
{
    @CfgComment("Timeout to use items")
    @CfgIntDefault(15)
    private int timeout;

    public int getTimeout()
    {
        return this.timeout;
    }

    public void setTimeout(final int timeout)
    {
        this.timeout = timeout;
    }
}
