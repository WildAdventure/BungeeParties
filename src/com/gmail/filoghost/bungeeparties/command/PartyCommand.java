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
package com.gmail.filoghost.bungeeparties.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gmail.filoghost.bungeeparties.BungeeParties;
import com.gmail.filoghost.bungeeparties.Perms;
import com.gmail.filoghost.bungeeparties.party.ColorCombination;
import com.gmail.filoghost.bungeeparties.party.Format;
import com.gmail.filoghost.bungeeparties.party.Party;
import com.gmail.filoghost.bungeeparties.party.PartyManager;
import com.gmail.filoghost.bungeeparties.utils.Components;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class PartyCommand extends Command implements TabExecutor {
	
	private Joiner joiner = Joiner.on(", ");

	public PartyCommand() {
		super("party", null, "p");
	}
	
	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		Set<String> matches = new HashSet<String>();
		if (args.length > 1) {
			
			if (isServerWideCompleteCommand(args[0])) {
				String partialName = args[1].toLowerCase();
				
				if (sender instanceof ProxiedPlayer) {
					for (ProxiedPlayer player : ((ProxiedPlayer) sender).getServer().getInfo().getPlayers()) {
						if (player.getName().toLowerCase().startsWith(partialName)) {
							if (player.hasPermission(Perms.ANTITAB) && !sender.hasPermission(Perms.BYPASS_ANTITAB)) {
								// Non aggiungere
							} else {
								matches.add(player.getName());
							}
						}
					}
				}
			} else if (isPartyWideCompleteCommand(args[0])) {
				String partialName = args[1].toLowerCase();
				
				if (sender instanceof ProxiedPlayer) {
					Party party = PartyManager.getParty((ProxiedPlayer) sender);
					if (party != null) {
						for (ProxiedPlayer player : party.getMembers()) {
							if (player.getName().toLowerCase().startsWith(partialName)) {
								matches.add(player.getName());
							}
						}
					}
				}
			}
		}
		
		return matches;
	}
	
	private boolean isServerWideCompleteCommand(String s) {
		s = s.toLowerCase();
		return s.equals("invita") || s.equalsIgnoreCase("crea");
	}
	
	private boolean isPartyWideCompleteCommand(String s) {
		s = s.toLowerCase();
		return s.equals("kick") || s.equals("leader");
	}

	@Override
	public void execute(CommandSender sender, String[] args) { try {
		if (args.length == 0) {
			Format.partyTitle(sender, "Comandi Party");
			Format.help(sender, "/p, /party", "/party", "Mostra questa schermata di aiuto.\n§6Puoi utilizzare /p al posto di /party in tutti i comandi.");
			Format.help(sender, "/party crea [invitati...]", "/party crea ", "Crea un nuovo party, di cui sarai il leader.\n§6Puoi invitare altri utenti scrivendoli dopo il comando separati da spazi.");
			Format.help(sender, "/party chat", "/party chat", "Attiva/disattiva la chat privata con i membri del party.");
			Format.help(sender, "/pchat <messaggio>", "/pchat ", "Manda un messaggio nella chat privata, senza doverla abilitare.");
			Format.help(sender, "/party info", "/party info", "Informazioni sul party in cui ti trovi ora.");
			Format.help(sender, "/party accetta <giocatore>", "/party accetta ", "Entra nel party di un giocatore che ti ha invitato.");
			Format.help(sender, "/party on", "/party on", "Attiva gli inviti nei party.");
			Format.help(sender, "/party off", "/party off", "Disattiva gli inviti nei party.");
			Format.help(sender, "/party esci", "/party esci", "Esci dal party.");
			Format.helpLeader(sender, "/party invita <giocatore>", "/party invita ", "Invita un giocatore nel tuo party.");
			Format.helpLeader(sender, "/party kick <giocatore>", "/party kick ", "Caccia un giocatore dal party, o annulla l'invito.");
			Format.helpLeader(sender, "/party colore", "/party colore", "Cambio il colore della chat.");
			Format.helpLeader(sender, "/party leader <giocatore>", "/party leader ", "Nomina un altro giocatore leader del party.");
			Format.helpLeader(sender, "/party sciogli", "/party sciogli", "Elimina il party.");
			Format.newLine(sender);
			Format.coloredMessage(sender, "Passa sopra i comandi con il mouse per vedere i dettagli.", ChatColor.GOLD);
			return;
		}
		
		/**
		 * STATUS
		 */
		if (args[0].equalsIgnoreCase("status")) {
			CommandValidator.checkTrue(sender.hasPermission(Perms.CMD_STATUS), "Non hai il permesso per questo comando.");
			
			// Comando per monitorare lo stato dei vari party.
			List<Party> parties = PartyManager.getParties();
			Format.partyTitle(sender, "Status dei party");
			Format.infoMessage(sender, "Ci sono " + parties.size() + " party attivi:");
			
			List<BaseComponent[]> information = new ArrayList<BaseComponent[]>();
			
			for (Party party : parties) {
				information.add(generatePartyInfo(party));
			}
			
			sender.sendMessage(joinComponents(information));
			return;
		}
		
		/**
		 * BLOCK
		 */
		if (args[0].equalsIgnoreCase("block")) {
			CommandValidator.checkTrue(sender.hasPermission(Perms.CMD_BLOCK), "Non hai il permesso per questo comando.");
			CommandValidator.checkTrue(args.length >= 2, "Utilizzo comando: \"/party block <giocatore>\".");
			String toBlock = args[1];
			
			CommandValidator.checkTrue(!BungeeParties.config.blocked.contains(toBlock), "Questo utente è già bloccato.");
			
			BungeeParties.config.blocked.add(toBlock);
			try {
				BungeeParties.config.save();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
				sender.sendMessage(TextComponent.fromLegacyText("§cImpossibile salvare la configurazione!"));
				return;
			}
			
			sender.sendMessage(TextComponent.fromLegacyText("§eHai bloccato " + toBlock + ", ora non potrà più invitare lo staff nei party."));
			return;
		}
		
		/**
		 * UNBLOCK
		 */
		if (args[0].equalsIgnoreCase("unblock")) {
			CommandValidator.checkTrue(sender.hasPermission(Perms.CMD_UNBLOCK), "Non hai il permesso per questo comando.");
			CommandValidator.checkTrue(args.length >= 2, "Utilizzo comando: \"/party unblock <giocatore>\".");
			String toBlock = args[1];
			
			CommandValidator.checkTrue(BungeeParties.config.blocked.contains(toBlock), "Questo utente non è bloccato.");
			
			BungeeParties.config.blocked.remove(toBlock);
			try {
				BungeeParties.config.save();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
				sender.sendMessage(TextComponent.fromLegacyText("§cImpossibile salvare la configurazione!"));
				return;
			}
			
			sender.sendMessage(TextComponent.fromLegacyText("§aHai sbloccato " + toBlock + ", ora potrà invitare lo staff nei party."));
			return;
		}
		
		
		CommandValidator.checkTrue(sender instanceof ProxiedPlayer, "Devi essere un giocatore per questo comando.");
		ProxiedPlayer player = (ProxiedPlayer) sender;
		
		/**
		 * CHAT
		 */
		if (args[0].equalsIgnoreCase("chat")) {
			CommandValidator.checkTrue(PartyManager.hasParty(player), "Non sei in nessun party.");
			
			if (PartyManager.hasPartyChat(player)) {
				PartyManager.disablePartyChat(player);
				player.sendMessage(TextComponent.fromLegacyText("§6Hai §cdisattivato §6la chat del party, tutti potranno leggere ciò che scrivi nella chat globale."));
			} else {
				PartyManager.enablePartyChat(player);
				player.sendMessage(TextComponent.fromLegacyText("§6Hai §aattivato §6la chat del party, solo i membri del party potranno leggere ciò che scrivi."));
			}
			return;
		}
		
		/**
		 * CREA
		 */
		if (args[0].equalsIgnoreCase("crea")) {
			
			CommandValidator.checkTrue(!PartyManager.hasParty(player), "Sei già in un party!");
			if (player.hasPermission(Perms.NOPARTY)) {
				throw new CommandException("Non hai il permesso per creare i party.");
			}
			
			PartyManager.registerParty(new Party(player));
			PartyManager.enablePartyChat(player);
			Format.partyChat(player, "Hai creato un nuovo party!", ChatColor.LIGHT_PURPLE);
			Format.partyChat(player, "Attiva/disattiva la chat privata con \"/party chat\".", ChatColor.LIGHT_PURPLE);
			
			if (args.length > 1) {
				for (int i = 1; i < args.length; i++) {
					String invitedName = args[i];
					try {
						invite(player, invitedName, false);
					} catch (CommandException e) {
						player.sendMessage(new ComponentBuilder(e.getMessage()).color(ChatColor.RED).create());
					}
				}
			}
			
			return;
		}
		
		/**
		 * INFO
		 */
		if (args[0].equalsIgnoreCase("info")) {
			Party party = PartyManager.getParty(player);
			if (party == null) {
				
				List<Party> invitedIn = PartyManager.getPartiesInvited(player);
				if (invitedIn.size() > 0) {
					List<String> invitedBy = new ArrayList<String>();
					for (Party p : invitedIn) {
						invitedBy.add(p.getLeader().getName());
					}
					Format.infoMessage(player, "Non sei in nessun party. Hai ricevuto un invito da: " + joiner.join(invitedBy));
					Format.tipMessage(player, "Entra in un party con /party accetta <giocatore>");
				} else {
					Format.errorMessage(player, "Non sei in nessun party.");
				}
				
				return;
			}
			
			Format.partyTitle(player, "Informazioni Party");
			Format.partyProperty(player, "Leader", party.getLeader().getName());
			Format.partyProperty(player, "Membri", joiner.join(party.getMembers()));
			Format.partyProperty(player, "Invitati", joiner.join(party.getInvited()));
			Format.newLine(player);
			return;
		}
		
		
		/**
		 *  INVITA
		 */
		if (args[0].equalsIgnoreCase("invita") || args[0].equalsIgnoreCase("add")) {
			CommandValidator.checkTrue(args.length >= 2, "Utilizzo comando: \"/party invita <giocatore>\".");
			invite(player, args[1], args.length >= 3 && args[2].equalsIgnoreCase("confirm"));
			return;
		}
		
		
		/**
		 * ACCETTA
		 */
		if (args[0].equalsIgnoreCase("accetta")) {
			
			CommandValidator.checkTrue(args.length >= 2, "Utilizzo comando: \"/party accetta <giocatore>\".");
			CommandValidator.checkTrue(!PartyManager.hasParty(player), "Sei già in un party.");
			
			ProxiedPlayer inviter = ProxyServer.getInstance().getPlayer(args[1]);
			
			CommandValidator.notNull(inviter, "Quel giocatore non è online.");
			
			Party party = PartyManager.getParty(inviter);
			if (party == null || !party.isInvited(player)) {
				Format.errorMessage(player, "Il giocatore non è in un party, non sei stato invitato, oppure l'invito è stato annullato.");
				return;
			}
			
			List<String> previousMembersNames = Lists.transform(party.getMembers(), new Function<ProxiedPlayer, String>() {

				@Override
				public String apply(ProxiedPlayer arg) {
					return arg.getName();
				}
			});
			previousMembersNames.remove(inviter.getName());
			
			party.broadcast(Components.partyChat(player.getName() + " è entrato nel party.", ChatColor.LIGHT_PURPLE));
			party.addMember(player);
			PartyManager.enablePartyChat(player);
			Format.partyChat(player, "Sei entrato nel party di " + inviter.getName() + "!", ChatColor.LIGHT_PURPLE);
			if (previousMembersNames.size() > 0) {
				Format.partyChat(player, "Altri membri: " + Joiner.on(", ").join(previousMembersNames) + ".", ChatColor.LIGHT_PURPLE);
			} else {
				
			}
			Format.partyChat(player, "Attiva/disattiva la chat privata con \"/party chat\".", ChatColor.LIGHT_PURPLE);
			return;
		}
		
		/**
		 * ESCI
		 */
		if (args[0].equalsIgnoreCase("esci")) {
			
			Party party = PartyManager.getParty(player);
			
			CommandValidator.notNull(party, "Non sei in nessun party.");
			
			if (party.isLeader(player)) {
				CommandValidator.checkTrue(party.getMembers().size() <= 1, "Sei il leader, non puoi abbandonare il party. Puoi nominare un nuovo leader con \"/party leader <giocatore>\" oppure scioglierlo con \"/party sciogli\".");
				
				PartyManager.unregisterParty(party);
				party.removeAll();
				player.sendMessage(Components.partyChat("Hai sciolto il party perché eri l'unico membro.", ChatColor.LIGHT_PURPLE));
				
			} else {
				party.removeMember(player);
				party.broadcast(Components.partyChat(player.getName() + " è uscito dal party.", ChatColor.LIGHT_PURPLE));
				player.sendMessage(Components.partyChat("Sei uscito dal party.", ChatColor.LIGHT_PURPLE));
			}
			
			
			
			return;
		}
		
		/**
		 * ON
		 */
		if (args[0].equalsIgnoreCase("on")) {
			
			CommandValidator.checkTrue(PartyManager.hasDisabledParties(player), "Gli inviti sono già abilitati.");
			
			PartyManager.enableParties(player);
			player.sendMessage(new ComponentBuilder("Hai ").color(ChatColor.YELLOW).append("attivato").color(ChatColor.GREEN).append(" i futuri inviti ai party.").color(ChatColor.YELLOW).create());
			return;
		}
		
		/**
		 * OFF
		 */
		if (args[0].equalsIgnoreCase("off")) {
			
			CommandValidator.checkTrue(!PartyManager.hasDisabledParties(player), "Gli inviti sono già disabilitati.");
			
			PartyManager.disableParties(player);
			player.sendMessage(new ComponentBuilder("Hai ").color(ChatColor.YELLOW).append("disattivato").color(ChatColor.RED).append(" i futuri inviti ai party.").color(ChatColor.YELLOW).create());
			return;
		}
		
		
		/**
		 * LEADER
		 */
		if (args[0].equalsIgnoreCase("leader")) {
			
			CommandValidator.checkTrue(args.length >= 2, "Utilizzo comando: \"/party leader <giocatore>\".");
			
			Party party = PartyManager.getParty(player);
			
			CommandValidator.notNull(party, "Non sei in nessun party.");
			CommandValidator.checkTrue(party.isLeader(player), "Devi essere il leader per utilizzare questo comando.");
			
			ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
			
			CommandValidator.notNull(target, "Giocatore non trovato.");
			CommandValidator.checkTrue(!target.equals(player), "Sei già il leader!");
			CommandValidator.checkTrue(party.isMember(target), "Quel giocatore non è in questo party.");
			
			party.setLeader(target);
			party.broadcast(Components.partyChat(target.getName() + " è stato nominato leader del party!", ChatColor.LIGHT_PURPLE));
			return;
		}
		
		/**
		 * COLORI
		 */
		if (args[0].equalsIgnoreCase("colore")) {
			Party party = PartyManager.getParty(player);
			
			CommandValidator.notNull(party, "Non sei in nessun party.");
			CommandValidator.checkTrue(party.isLeader(player), "Devi essere il leader per utilizzare questo comando.");
			
			if (args.length < 2) {
				Format.newLine(player);
				Format.coloredMessage(player, "Clicca per scegliere un combinazione di colori:", ChatColor.GOLD);
				player.sendMessage(
						new ComponentBuilder("Azzurro (Default)")
							.color(ChatColor.AQUA).underlined(true)
							.event(generateQuickHover("Clicca per scegliere questo colore", ChatColor.AQUA))
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party colore " + ColorCombination.AZZURRO.toString().toLowerCase()))
						.append("  ").underlined(false)
						.append("Verde")
							.color(ChatColor.GREEN).underlined(true)
							.event(generateQuickHover("Clicca per scegliere questo colore", ChatColor.GREEN))
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party colore " + ColorCombination.VERDE.toString().toLowerCase()))
						.append("  ").underlined(false)
						.append("Viola")
							.color(ChatColor.LIGHT_PURPLE).underlined(true)
							.event(generateQuickHover("Clicca per scegliere questo colore", ChatColor.LIGHT_PURPLE))
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party colore " + ColorCombination.VIOLA.toString().toLowerCase()))
						.append("  ").underlined(false)
						.append("Giallo")
							.color(ChatColor.YELLOW).underlined(true)
							.event(generateQuickHover("Clicca per scegliere questo colore", ChatColor.YELLOW))
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party colore " + ColorCombination.GIALLO.toString().toLowerCase()))
						.append("  ").underlined(false)
						.append("Bianco")
							.color(ChatColor.WHITE).underlined(true)
							.event(generateQuickHover("Clicca per scegliere questo colore", ChatColor.WHITE))
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party colore " + ColorCombination.BIANCO.toString().toLowerCase()))
						.create());
				Format.newLine(player);
				return;
			}
			
			ColorCombination colors;
			
			try {
				colors = ColorCombination.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException e) {
				Format.errorMessage(player, "Colore non valido!");
				return;
			}
			
			party.setChatColor(colors.getChatColor());
			party.setNameColor(colors.getNameColor());
			
			player.sendMessage(Components.partyChat("Hai cambiato il colore della chat.", colors.getChatColor()));
			return;
		}
		
		/**
		 * KICK
		 */
		if (args[0].equalsIgnoreCase("kick")) {
			
			CommandValidator.checkTrue(args.length >= 2, "Utilizzo comando: \"/party kick <giocatore>\".");
			
			Party party = PartyManager.getParty(player);
			CommandValidator.notNull(party, "Non sei in nessun party.");
			CommandValidator.checkTrue(party.isLeader(player), "Devi essere il leader per utilizzare questo comando.");
			
			ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
			CommandValidator.notNull(target, "Giocatore non trovato.");
			CommandValidator.checkTrue(!target.equals(player), "Non puoi cacciarti da solo!");
			
			if (party.isMember(target)) {
				party.removeMember(target);
				party.broadcast(Components.partyChat(target.getName() + " è stato cacciato dal party.", ChatColor.LIGHT_PURPLE));
				target.sendMessage(Components.partyChat("Sei stato cacciato dal party.", ChatColor.LIGHT_PURPLE));
			} else if (party.isInvited(target)) {
				party.removeInvited(target);
				player.sendMessage(Components.partyChat("Hai rimosso l'invito a " + target.getName() + ".", ChatColor.LIGHT_PURPLE));
			} else {
				Format.errorMessage(player, "Il giocatore non è membro o invitato del tuo party.");
			}
			return;
		}
		
		/**
		 * SCIOGLI
		 */
		if (args[0].equalsIgnoreCase("sciogli")) {
			
			Party party = PartyManager.getParty(player);
			CommandValidator.notNull(party, "Non sei in nessun party.");
			CommandValidator.checkTrue(party.isLeader(player), "Devi essere il leader per utilizzare questo comando.");
			
			party.broadcast(Components.partyChat("Il party è stato sciolto dal leader " + player.getName() + ".", ChatColor.LIGHT_PURPLE));
			PartyManager.unregisterParty(party);
			party.removeAll();
			return;
		}
		
		throw new CommandException("Comando sconosciuto. Scrivi \"/party\" per una lista dei comandi.");
		
	} catch (CommandException ex) {
		Format.errorMessage(sender, ex.getMessage());
	}}
	
	
	private void invite(ProxiedPlayer player, String invitedName, boolean staffConfirm) throws CommandException {
		CommandValidator.checkTrue(!player.getName().equalsIgnoreCase(invitedName), "Non puoi invitarti da solo!");
		
		Party party = PartyManager.getParty(player);
		
		if (party == null) {
			Format.errorMessage(player, "Devi prima creare un party con \"/party crea\",");
			player.sendMessage(new ComponentBuilder("oppure ").color(ChatColor.RED)
					.append("cliccando qui").color(ChatColor.GRAY).underlined(true)
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party crea"))
						.event(generateQuickHover("Clicca per creare un party.", ChatColor.GRAY))
					.append(".").underlined(false).color(ChatColor.RED).create());
			return;
		}
		
		CommandValidator.checkTrue(party.isLeader(player), "Solo il leader può invitare altre persone.");
		CommandValidator.checkTrue(party.getInvited().size() + party.getMembers().size() < 50, "Possono esserci al massimo 50 persone fra membri e invitati. Per annullare un invito o cacciare un giocatore usa \"/party kick <giocatore>\".");
		
		ProxiedPlayer invited = ProxyServer.getInstance().getPlayer(invitedName);
		
		CommandValidator.notNull(invited, invitedName + " non è online.");
		CommandValidator.checkTrue(!PartyManager.hasDisabledParties(invited), invited.getName() + " ha disattivato gli inviti.");
		CommandValidator.checkTrue(!party.isMember(invited), invited.getName() + " è già in questo party!");
		CommandValidator.checkTrue(!party.isInvited(invited), invited.getName() + " è già stato invitato.");
		CommandValidator.checkTrue(!PartyManager.hasCooldown(player, invited), "Per favore attendi, hai già mandato un invito a " + invited.getName() + " meno di 1 minuto fa.");
		
		if (invited.hasPermission(Perms.STAFF) && !player.hasPermission(Perms.STAFF)) {
			// Quando un utente (non dello staff) invita uno dello staff
			CommandValidator.checkTrue(!BungeeParties.config.blocked.contains(player.getName()), "A causa di un utilizzo improprio dei party, ti è stato revocato il permesso di invitare i membri dello staff.");
		
			if (!staffConfirm) {
				player.sendMessage(new ComponentBuilder
						("Stai per invitare un membro dello staff (" + invitedName + ").\n").color(ChatColor.RED).bold(false)
						.append("NON").color(ChatColor.DARK_RED).bold(true)
						.append(" utilizzare i party per segnalazioni o assistenza.\n").color(ChatColor.RED).bold(false)
						.append("Per procedere ").color(ChatColor.RED)
						.append("clicca qui").color(ChatColor.GRAY).underlined(true)
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party invita " + invitedName + " confirm"))
							.event(generateQuickHover("Clicca per confermare l'invito.", ChatColor.GRAY))
						.append(".").color(ChatColor.RED).underlined(false)
				.create());
				
				return;
			}
		}
		
		PartyManager.registerCooldown(player, invited);
		party.addInvited(invited);
		party.broadcast(Components.partyChat(invited.getName() + " è stato invitato nel party!", ChatColor.LIGHT_PURPLE));
		
		if (PartyManager.hasParty(invited))	{
			player.sendMessage(new ComponentBuilder(invited.getName() + " è già impegnato in un party, potrebbe essere impegnato e non accettare.").color(ChatColor.GRAY).create());
		}
		
		Format.infoMessage(invited, "=--------------------------------------------------=");
		Format.legacyText(invited, "§6         " + generateSpaces(10 - (int) (player.getName().length() * 0.75)) + "Sei stato invitato nel party di " + player.getName() + "!");
		invited.sendMessage(
				new ComponentBuilder("                        Per accettare ").color(ChatColor.GOLD)
				.append("clicca qui").color(ChatColor.GRAY).underlined(true)
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accetta " + player.getName()))
				.event(generateQuickHover("Clicca per accettare l'invito.", ChatColor.GRAY))
				.append(".").color(ChatColor.GOLD).underlined(false)
				.create());
		Format.infoMessage(invited, "=--------------------------------------------------=");
		Party invitedParty = PartyManager.getParty(invited);
		if (invitedParty != null && invitedParty.isLeader(invited)) {
			invited.sendMessage(new ComponentBuilder("Hai già un tuo party, vorresti invece ").color(ChatColor.GRAY)
					.append("invitare " + player.getName())
						.underlined(true)
						.color(ChatColor.WHITE)
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party invita " + player.getName()))
						.event(generateQuickHover("Clicca qui per invitarlo.", ChatColor.GRAY))
					.append("?").color(ChatColor.GRAY).underlined(false).create());
		}
	}
	
	
	private String generateSpaces(int amount) {
		if (amount <= 0) {
			return "";
		}
		
		final char[] array = new char[amount];
		Arrays.fill(array, ' ');
		return new String(array);
	}
	
	
	
	private BaseComponent[] generatePartyInfo(Party party) {
		return new ComponentBuilder(party.getLeader().getName()).color(ChatColor.GRAY).event(generatePartyHover(party)).create();
	}
	
	private HoverEvent generatePartyHover(Party party) {
		return new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Leader: ").color(ChatColor.GOLD)
												.append(party.getLeader().getName()).color(ChatColor.GRAY)
													.append("\n")
												.append("Membri (" + party.getMembers().size() + "): ").color(ChatColor.GOLD)
												.append(joinPlayers(party.getMembers())).color(ChatColor.GRAY)
													.append("\n")
												.append("Invitati (" + party.getInvited().size() + "): ").color(ChatColor.GOLD)
												.append(joinPlayers(party.getInvited())).color(ChatColor.GRAY)
												.create());
	}
	
	private String joinPlayers(List<ProxiedPlayer> players) {
		List<String> names = new ArrayList<String>();
		for (ProxiedPlayer player : players) {
			names.add(player.getName());
		}
		return joiner.join(names);
	}
	
	private HoverEvent generateQuickHover(String text, ChatColor color) {
		return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(text).color(color).create());
	}
	
	private BaseComponent[] joinComponents(List<BaseComponent[]> toJoin) {
		List<BaseComponent> joined = new ArrayList<BaseComponent>();
		for (int i = 0; i < toJoin.size(); i++) {
			if (i > 0) {
				joined.addAll(Arrays.asList(new ComponentBuilder(", ").color(ChatColor.WHITE).create()));
			}
			
			joined.addAll(Arrays.asList(toJoin.get(i)));
		}
		
		return joined.toArray(new BaseComponent[joined.size()]);
	}
}
