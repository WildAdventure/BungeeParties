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

import com.gmail.filoghost.bungeeparties.utils.Components;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import static net.md_5.bungee.api.ChatColor.*;

public class Format {

	public static void newLine(CommandSender sender) {
		sender.sendMessage(new TextComponent(""));
	}
	
	public static void partyChat(CommandSender sender, String message) {
		partyChat(sender, message, WHITE);
	}
	
	public static void partyChat(CommandSender sender, String message, ChatColor optionalColor) {
		sender.sendMessage(Components.partyChat(message, optionalColor));
	}
	
	public static void partyTitle(CommandSender sender, String message) {
		sender.sendMessage(new ComponentBuilder("=--------------= ").color(DARK_GRAY).append(message).color(GOLD).append(" =--------------=").color(DARK_GRAY).create());
	}
	
	public static void partyProperty(CommandSender sender, String key, String value) {
		sender.sendMessage(new ComponentBuilder(key + ": ").color(YELLOW).append(value).color(GRAY).create());
	}
	
	public static void errorMessage(CommandSender sender, String message) {
		coloredMessage(sender, message, RED);
	}
	
	public static void infoMessage(CommandSender sender, String message) {
		coloredMessage(sender, message, YELLOW);
	}
	
	public static void tipMessage(CommandSender sender, String message) {
		coloredMessage(sender, message, GOLD);
	}
	
	public static void coloredMessage(CommandSender sender, String message, ChatColor color) {
		sender.sendMessage(new ComponentBuilder(message).color(color).create());
	}
	
	public static void help(CommandSender sender, String command, String commandSuggestion, String description) {
		sender.sendMessage(
			new ComponentBuilder(command)
						  .color(YELLOW)
						  .event(createHover(description, GOLD))
						  .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandSuggestion))
						  .create()
		);
	}
	
	public static void helpLeader(CommandSender sender, String command, String commandSuggestion, String description) {
		sender.sendMessage(
			new ComponentBuilder("[Leader] ")
						  .color(RED)
						  .event(createHover(description, GOLD))
						  .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandSuggestion))
						  .append(command)
						  .color(YELLOW)
						  .create()
		);
	}
	
	private static HoverEvent createHover(String text, ChatColor color) {
		return new HoverEvent( Action.SHOW_TEXT, new ComponentBuilder(text).color(color).create() );
	}
	
	public static void legacyText(CommandSender sender, String message) {
		sender.sendMessage(TextComponent.fromLegacyText(message));
	}
}
