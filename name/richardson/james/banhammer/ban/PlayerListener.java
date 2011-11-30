/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * PlayerListener.java is part of BanHammer.
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
package name.richardson.james.banhammer.ban;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import name.richardson.james.banhammer.BanHammerPlugin;

import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener extends org.bukkit.event.player.PlayerListener {

  private CachedList cache;

  public PlayerListener() {
    this.cache = new CachedList();
  }

  @Override
  public void onPlayerLogin(PlayerLoginEvent event) {
    String playerName = event.getPlayer().getName();
    String message;

    if (this.cache.contains(playerName)) {
      CachedBan ban = this.cache.get(playerName);
      if (ban.isActive()) {
        if (ban.getType().equals(BanRecord.Type.PERMENANT))
          message = String.format(BanHammerPlugin.getMessage("disallowLoginPermanently"), ban.getReason());
        else {
          Date expiryDate = new Date(ban.getExpiresAt());
          DateFormat dateFormat = new SimpleDateFormat("MMM d H:mm a ");
          String expiryDateString = dateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")";
          message = String.format(BanHammerPlugin.getMessage("disallowLoginTemporarily"), expiryDateString);
        }
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
      } else this.cache.remove(playerName);
    }
  }
}
