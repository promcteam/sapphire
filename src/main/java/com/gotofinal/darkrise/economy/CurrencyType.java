package com.gotofinal.darkrise.economy;

import com.gotofinal.darkrise.spigot.core.DarkRiseCore;
import com.gotofinal.darkrise.spigot.core.Vault;
import com.gotofinal.messages.api.messages.Message.MessageData;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

import org.diorite.utils.lazy.LazyValue;

public enum CurrencyType
{
    MONEY
            {
                @Override
                public void sendNoMoney(final Player player, final double money)
                {
                    DarkRiseCore.getInstance().sendMessage(
                            "economy.no-money",
                            player,
                            new MessageData("player", player),
                            new MessageData("needed", money),
                            new MessageData("missing", money - this.get(player)));
                }

                @Override
                public boolean canPay(final Player player, final double money)
                {
                    return Vault.canPay(player, money);
                }

                @Override
                public boolean pay0(final Player player, final double money)
                {
                    return Vault.pay(player, money);
                }

                @Override
                public double get(final Player player)
                {
                    return Vault.getMoney(player);
                }

                @Override
                public boolean add(final Player player, final double money)
                {
                    return Vault.addMoney(player, money);
                }

                @Override
                public void reset(final Player player)
                {
                    Vault.reset(player);
                }
            },
    POINTS
            {
                private final LazyValue<PlayerPointsAPI> plugin = new LazyValue<>(() -> PlayerPoints.getPlugin(PlayerPoints.class).getAPI());

                @Override
                public void sendNoMoney(final Player player, final double money)
                {
                    DarkRiseCore.getInstance().sendMessage(
                            "economy.no-points",
                            player,
                            new MessageData("player", player),
                            new MessageData("needed", money),
                            new MessageData("missing", money - this.get(player)));
                }

                @Override
                public boolean canPay(final Player player, final double money)
                {
                    return this.get(player) >= (int) money;
                }

                @Override
                public boolean pay0(final Player player, final double money)
                {
                    return this.plugin.get().take(player.getUniqueId(), (int) money);
                }

                @Override
                public double get(final Player player)
                {
                    return this.plugin.get().look(player.getUniqueId());
                }

                @Override
                public boolean add(final Player player, final double money)
                {
                    return this.plugin.get().give(player.getUniqueId(), (int) money);
                }

                @Override
                public void reset(final Player player)
                {
                    this.plugin.get().reset(player.getUniqueId());
                }
            };

    public abstract void sendNoMoney(final Player player, double money);

    public boolean canPay(final Player player, final double money, final boolean sendMessage)
    {
        if (this.canPay(player, money))
        {
            return true;
        }
        if (sendMessage)
        {
            this.sendNoMoney(player, money);
        }
        return false;
    }

    public abstract boolean canPay(final Player player, final double money);

    public boolean pay(final Player player, final double money)
    {
        if (! this.canPay(player, money))
        {
            return false;
        }
        return this.pay0(player, money);
    }

    protected abstract boolean pay0(final Player player, final double money);

    public abstract double get(final Player player);

    public abstract boolean add(final Player player, double money);

    public abstract void reset(final Player player);
}
