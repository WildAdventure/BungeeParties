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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.gmail.filoghost.bungeeparties.Perms;
import com.gmail.filoghost.bungeeparties.utils.Components;
import com.gmail.filoghost.bungeeparties.utils.Validator;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PartyManager {
	
	private static final long INVITE_COOLDOWN = TimeUnit.MINUTES.toMillis(1);

	private static List<Party> parties = new ArrayList<Party>();
	
	private static Set<ProxiedPlayer> partyChatEnabled = Sets.newConcurrentHashSet();
	private static Set<ProxiedPlayer> partyDisabled = Sets.newConcurrentHashSet();
	private static Map<ProxiedPlayer, Map<String, Long>> lastInvites = Maps.newConcurrentMap();
	
	// Attenzione: restituisce la lista originale MODIFICABILE
	public static List<Party> getParties() {
		return parties;
	}
	
	public static void registerParty(Party party) {
		Validator.checkArgument(!parties.contains(party), "Party already registered!");
		parties.add(party);
	}
	
	public static void unregisterParty(Party party) {
		parties.remove(party);
	}
	
	public static Party getParty(ProxiedPlayer player) {
		for (Party party : parties) {
			if (party.isMember(player)) {
				return party;
			}
		}
		return null;
	}
	
	public static boolean hasParty(ProxiedPlayer player) {
		return getParty(player) != null;
	}
	
	public static List<Party> getPartiesInvited(ProxiedPlayer player) {
		List<Party> invitedIn = new ArrayList<Party>();
		for (Party party : parties) {
			if (party.isInvited(player)) {
				invitedIn.add(party);
			}
		}
		return invitedIn;
	}
	
	public static void enablePartyChat(ProxiedPlayer player) {
		partyChatEnabled.add(player);
	}
	
	public static void disablePartyChat(ProxiedPlayer player) {
		partyChatEnabled.remove(player);
	}
	
	public static boolean hasPartyChat(ProxiedPlayer player) {
		return partyChatEnabled.contains(player);
	}

	public static void enableParties(ProxiedPlayer player) {
		partyDisabled.remove(player);
	}
	
	public static void disableParties(ProxiedPlayer player) {
		partyDisabled.add(player);
	}
	
	public static boolean hasDisabledParties(ProxiedPlayer player) {
		return partyDisabled.contains(player);
	}
	
	public static boolean hasCooldown(ProxiedPlayer sender, ProxiedPlayer target) {
		if (sender.hasPermission(Perms.BYPASS_COOLDOWN)) {
			return false;
		}
		Map<String, Long> senderLastInvites = lastInvites.get(sender);
		if (senderLastInvites == null) {
			return false;
		}
		
		Long targetLastInvite = senderLastInvites.get(target.getName().toLowerCase());
		if (targetLastInvite == null) {
			return false;
		}
		
		return System.currentTimeMillis() - targetLastInvite < INVITE_COOLDOWN;
	}
	
	public static void registerCooldown(ProxiedPlayer sender, ProxiedPlayer target) {
		Map<String, Long> senderLastInvites = lastInvites.get(sender);
		if (senderLastInvites == null) {
			senderLastInvites = Maps.newConcurrentMap();
			lastInvites.put(sender, senderLastInvites);
		}
		
		senderLastInvites.put(target.getName().toLowerCase(), System.currentTimeMillis());
	}
	
	public static void onQuit(ProxiedPlayer player) {
		
		Party party = getParty(player);
		if (party != null) {
			Role role = party.getRole(player);
			switch (role) {
				case MEMBER:
					// Rimuove dai membri e avvisa
					party.removeMember(player);
					party.broadcast(Components.partyChat(player.getName() + " è stato rimosso perché è uscito.", ChatColor.LIGHT_PURPLE));
					break;
				case LEADER:
					// Scioglie il party
					party.broadcast(Components.partyChat("Il party è stato sciolto perché il leader è uscito.", ChatColor.LIGHT_PURPLE));
					PartyManager.unregisterParty(party);
					party.removeAll();
					break;
				default:
					break;
			}
		}
		for (Party invitedIn : PartyManager.getPartiesInvited(player)) {
			invitedIn.removeInvited(player);
		}
		
		partyDisabled.remove(player);
		partyChatEnabled.remove(player);
		lastInvites.remove(player);
	}
	
}
