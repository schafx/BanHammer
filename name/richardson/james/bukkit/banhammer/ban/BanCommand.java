/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * BanCommand.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with BanHammer.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.ban;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanHandler;
import name.richardson.james.bukkit.util.Time;
import name.richardson.james.bukkit.util.command.Command;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.CommandPermissionException;
import name.richardson.james.bukkit.util.command.CommandUsageException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class BanCommand extends PlayerCommand {

  public static final String NAME = "ban";
  public static final String DESCRIPTION = "Ban a player";
  public static final String PERMISSION_DESCRIPTION = "Allow users to ban players.";
  public static final String USAGE = "<name> [t:time] [reason]";
  public static final List<String> ALIAS = Arrays.asList("ban");

  public static final Permission PERMISSION = new Permission("banhammer.ban", BanCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final BanHammer plugin;
  private final BanHandler banHandler;

  public BanCommand(final BanHammer plugin) {
    super(plugin, BanCommand.NAME, BanCommand.DESCRIPTION, BanCommand.USAGE, BanCommand.PERMISSION_DESCRIPTION, BanCommand.PERMISSION);
    this.plugin = plugin;
    this.banHandler = plugin.getHandler(BanCommand.class);
    this.registerBanLimits();
  }
  
  private void registerBanLimits() {
    Map<String, Long> limits = plugin.getBanLimits();
    if (!limits.isEmpty()) { 
      final Permission wildcard = new Permission(BanCommand.PERMISSION.getName() + ".*", "Allow a user to ban for an unlimited amount of time", PermissionDefault.OP);
      this.plugin.addPermission(wildcard, true);
      for (final Entry<String, Long> limit : limits.entrySet()) {
        Permission permission = new Permission("banhammer.ban." + limit.getKey(), "Allow a user to ban a player for up to specified amount of time.", PermissionDefault.OP);
        permission.addParent(wildcard, true);
        this.plugin.addPermission(permission, false);
      }
    }
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final String unmatchedPlayerName = (String) (arguments.get("playerName"));
    final Player player = this.plugin.getServer().getPlayer(unmatchedPlayerName);
    final String playerName = player != null ? player.getName() : unmatchedPlayerName;
    final String senderName = sender.getName();
    final String reason = (String) arguments.get("reason");
    
    final String expiryTimeString = (String) arguments.get("time");
    Long expiryTime = (long) 0;
    
    if (plugin.getBanLimits().containsKey(expiryTimeString)) {
      expiryTime = plugin.getBanLimits().get(expiryTimeString);
    } else {
      expiryTime = Time.parseTime(expiryTimeString);
    }
    
    // check the user can ban for the specified amount of time
    if (banLengthAuthorised(sender, expiryTime)) {
      if (!this.banHandler.banPlayer(playerName, senderName, reason, expiryTime, true)) {
        sender.sendMessage(ChatColor.RED + String.format(BanHammer.getMessage("player-already-banned"), playerName));
      } else {
        sender.sendMessage(ChatColor.RED + String.format(BanHammer.getMessage("player-banned"), playerName));
      }
    } else {
      throw new CommandPermissionException("You are not allowed to ban for that long.", BanCommand.PERMISSION);
    }
  }
  
  private boolean banLengthAuthorised(CommandSender sender, long banLength) {
    if (banLength == 0 && !sender.hasPermission("banhammer.ban.*")) {
      return false;
    } else {
      for (final Entry<String, Long> limit : plugin.getBanLimits().entrySet()) {
        if (sender.hasPermission("banhammer.ban." + limit.getKey()) && banLength <= limit.getValue()) {
          return true;
        }
      }
    }
    logger.info(Long.toString(banLength));
    return false;
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) throws CommandArgumentException {
    Map<String, Object> m = new HashMap<String, Object>();

    m.put("reason", "No reason provided");
    m.put("time", "0");
    
    try {
      for (String argument : arguments) {
        if (argument.startsWith("t:")) {
          m.put("time", argument.replaceAll("t:", ""));
          arguments.remove(argument);
          break;
        }
      }

      m.put("playerName", arguments.remove(0));
      m.put("reason", this.combineString(arguments, " "));
    } catch (IndexOutOfBoundsException e) {
      throw new CommandArgumentException("You must specify a valid player name!", "If they are online, you can type part of the name.");
    }

    return m;
  }
  
  protected String combineString(List<String> arguments, String seperator) {
    StringBuilder reason = new StringBuilder();
    try {
      for (String argument : arguments) {
        reason.append(argument);
        reason.append(seperator);
      }
      reason.deleteCharAt(reason.length() - seperator.length());
      return reason.toString();
    } catch (StringIndexOutOfBoundsException e) {
      return BanHammer.getMessage("default-reason");
    }
  }



}
