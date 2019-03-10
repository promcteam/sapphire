package com.gotofinal.darkrise.economy.commands;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;

import com.gotofinal.darkrise.core.annotation.DarkRiseSubCommand;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.economy.cfg.VoucherManager;
import com.gotofinal.darkrise.spigot.core.command.Arguments;
import com.gotofinal.darkrise.spigot.core.command.CommandExecutor;

import com.gotofinal.darkrise.spigot.core.command.SubCommandImpl;
import com.gotofinal.messages.api.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@DarkRiseSubCommand(value = EconCommand.class, name = "voucher")
public class EconVoucherCommand extends SubCommandImpl implements CommandExecutor
{
	public EconVoucherCommand(DarkRiseEconomy plugin, EconCommand command)
	{
		super("voucher", Collections.singletonList("voucher"), command);
		this.setUsage(command.getUsage());
		this.setCommandExecutor(this);
	}

	@Override
	public void runCommand(final CommandSender sender, final Command<CommandSender> command, final String label,
	                       final Matcher matchedPattern, final Arguments args)
	{
		if (! this.checkPermission(sender, "econ.voucher"))
		{
			return;
		}

		if (args.length() != 1)
		{
			this.sendUsage(command.getUsage(), sender, command, args);
			return;
		}

		Integer voucherId = args.asInt(0);

		if (voucherId == null)
		{
			this.sendMessage("notANumber", sender, new Message.MessageData("text", args.asText(0)));
			return;
		}

		Optional<VoucherManager.VoucherData> data = VoucherManager.getInstance().getData(voucherId);

		if(data.isPresent())
		{
			Date time = new java.util.Date(data.get().timestamp * 1000);
			OfflinePlayer player = Bukkit.getOfflinePlayer(data.get().playerUUID);

			this.sendMessage("economy.commands.voucher.info", sender,
					new Message.MessageData("voucher_id", voucherId),
					new Message.MessageData("player", player),
					new Message.MessageData("date", time.toString()));
		}
		else
		{
			this.sendMessage("economy.commands.voucher.info-unused", sender,
					new Message.MessageData("voucher_id", voucherId));
		}
	}
}
