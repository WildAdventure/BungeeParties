/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.bungeeparties.party;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import wild.api.BungeeCommons;

import com.gmail.filoghost.bungeeparties.events.PartyChatEvent;
import com.gmail.filoghost.bungeeparties.utils.Validator;

public class Party {

	private ProxiedPlayer leader;
	private List<ProxiedPlayer> members;
	private List<ProxiedPlayer> invitedPlayers;
	
	@Getter @Setter	@NonNull
	private ChatColor nameColor, chatColor;
	
	public Party(ProxiedPlayer leader) {
		Validator.notNull(leader, "leader cannot be null");
		this.leader = leader;
		members = Collections.synchronizedList(new ArrayList<ProxiedPlayer>());
		invitedPlayers = Collections.synchronizedList(new ArrayList<ProxiedPlayer>());
		
		nameColor = ChatColor.DARK_AQUA;
		chatColor = ChatColor.AQUA;
		
		// Il leader è sempre un membro
		members.add(leader);
	}
	
	public void addMember(ProxiedPlayer player) {
		Validator.checkArgument(!isMember(player), "Player " + player.getName() + " is already a member!");
		members.add(player);
		invitedPlayers.remove(player);
	}
	
	public boolean isMember(ProxiedPlayer player) {
		return members.contains(player);
	}
	
	public void removeMember(ProxiedPlayer player) {
		members.remove(player);
	}
	
	// Ritorna una copia dei membri del party
	public List<ProxiedPlayer> getMembers() {
		return new ArrayList<ProxiedPlayer>(members);
	}
	
	public void addInvited(ProxiedPlayer player) {
		Validator.checkArgument(!isMember(player), "Player " + player.getName() + " is already a member!");
		Validator.checkArgument(!isInvited(player), "Player " + player.getName() + " is already invited!");
		invitedPlayers.add(player);
	}
	
	public boolean isInvited(ProxiedPlayer player) {
		return invitedPlayers.contains(player);
	}
	
	public void removeInvited(ProxiedPlayer player) {
		invitedPlayers.remove(player);
	}

	// Ritorna una copia degli invitati
	public List<ProxiedPlayer> getInvited() {
		return new ArrayList<ProxiedPlayer>(invitedPlayers);
	}
	
	public void setLeader(ProxiedPlayer player) {
		Validator.checkArgument(isMember(player), "Player " + player.getName() + " cannot be the new leader because it's not in that party!");
		this.leader = player;
	}
	
	public ProxiedPlayer getLeader() {
		return leader;
	}
	
	public boolean isLeader(ProxiedPlayer player) {
		return leader.equals(player);
	}
	
	public int getSize() {
		return members.size();
	}
	
	public void chat(ProxiedPlayer player, String message) {
		broadcast(BungeeCommons.fixLinks(
			new ComponentBuilder("Party").color(ChatColor.GOLD).bold(true)
			.append(" | ").color(ChatColor.DARK_GRAY).bold(false)
			.append(player.getName()).color(nameColor)
			.append(" » ").color(ChatColor.DARK_GRAY)
			.append(message).color(chatColor)
			.create()
		));
		ProxyServer.getInstance().getPluginManager().callEvent(new PartyChatEvent(this, player, message));
	}
	
	public void broadcast(BaseComponent[] message) {
		for (ProxiedPlayer member : members) {
			member.sendMessage(message);
		}
	}
	
	public Role getRole(ProxiedPlayer player) {
		if (isLeader(player)) {
			return Role.LEADER;
		} else if (isMember(player)) {
			return Role.MEMBER;
		} else if (isInvited(player)) {
			return Role.INVITED;
		}
		
		throw new IllegalArgumentException(player.getName() + " is not in the party!");
	}
	
	public void removeAll() {
		members.clear();
		invitedPlayers.clear();
		leader = null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Party) {
			return ((Party) o).leader.equals(this.leader);
		}
		
		return false;
	}
	
}
