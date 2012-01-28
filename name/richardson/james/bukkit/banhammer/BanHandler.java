/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * BanHandler.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import name.richardson.james.bukkit.banhammer.util.BanHammerTime;
import name.richardson.james.bukkit.banhammer.util.Logger;
import name.richardson.james.bukkit.util.Handler;


public class BanHandler extends Handler implements BanHammerAPI {

  private final CachedList cache;
  private final Server server;
  
  public BanHandler(Class<?> parentClass) {
    super(parentClass);
  }

  @Override
  public boolean banPlayer(String playerName, String senderName, String reason, Long banLength, boolean notify) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public CachedBan getPlayerBan(String playerName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<CachedBan> getPlayerBans(String playerName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isPlayerBanned(String playerName) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean pardonPlayer(String playerName, String senderName, Boolean notify) {
    // TODO Auto-generated method stub
    return false;
  }

  
}