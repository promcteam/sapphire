package studio.magemonkey.sapphire;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.codex.util.messages.MessageData;

public enum CurrencyType {
    MONEY {
        public void sendNoMoney(Player player, double money) {
            CodexEngine.get().getMessageUtil().sendMessage("sapphire.no-money",
                    player,
                    new MessageData("player", player),
                    new MessageData("needed",

                            Double.valueOf(money)),
                    new MessageData("missing",
                            Double.valueOf(money - get(player))));
        }

        public boolean canPay(Player player, double money) {
            return CodexEngine.getEngine().getVault().canPay(player, money);
        }

        public boolean pay0(Player player, double money) {
            return CodexEngine.getEngine().getVault().take(player, money);
        }

        public double get(Player player) {
            return CodexEngine.getEngine().getVault().getBalance(player);
        }

        public boolean add(Player player, double money) {
            return CodexEngine.getEngine().getVault().give(player, money);
        }

        public void reset(Player player) {
            CodexEngine.getEngine().getVault().reset(player);
        }
    },
    POINTS {
        private final PlayerPointsAPI plugin = PlayerPoints.getPlugin(PlayerPoints.class).getAPI();

        public void sendNoMoney(Player player, double money) {
            CodexEngine.get().getMessageUtil().sendMessage("sapphire.no-points",
                    player,
                    new MessageData("player", player),
                    new MessageData("needed",

                            Double.valueOf(money)),
                    new MessageData("missing",
                            Double.valueOf(money - get(player))));
        }

        public boolean canPay(Player player, double money) {
            return (get(player) >= (int) money);
        }

        public boolean pay0(Player player, double money) {
            return this.plugin.take(player.getUniqueId(), (int) money);
        }

        public double get(Player player) {
            return this.plugin.look(player.getUniqueId());
        }

        public boolean add(Player player, double money) {
            return this.plugin.give(player.getUniqueId(), (int) money);
        }

        public void reset(Player player) {
            this.plugin.reset(player.getUniqueId());
        }
    };

    public boolean canPay(Player player, double money, boolean sendMessage) {
        if (canPay(player, money))
            return true;
        if (sendMessage)
            sendNoMoney(player, money);
        return false;
    }

    public boolean pay(Player player, double money) {
        if (!canPay(player, money))
            return false;
        return pay0(player, money);
    }

    public abstract void sendNoMoney(Player paramPlayer, double paramDouble);

    public abstract boolean canPay(Player paramPlayer, double paramDouble);

    protected abstract boolean pay0(Player paramPlayer, double paramDouble);

    public abstract double get(Player paramPlayer);

    public abstract boolean add(Player paramPlayer, double paramDouble);

    public abstract void reset(Player paramPlayer);
}
