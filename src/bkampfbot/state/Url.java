package bkampfbot.state;

/*
 Copyright (C) 2011  georf@georf.de

 This file is part of BKampfBot.

 BKampfBot is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 2 of the License, or
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Ein Model für eine URL.
 * 
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 */
public class Url {
	private final String url;
	private final Url next;
	private final int seconds;
	private boolean guild;

	/**
	 * Konstruktor für eine URL
	 * 
	 * @param url
	 */
	public Url(String url) {
		this.url = url;
		this.next = null;
		this.seconds = -1;
		this.guild = false;
	}

	/**
	 * Konstruktor für eine URL, mit Folge-URL
	 * 
	 * @param url
	 * @param seconds Sekunden Abstand
	 * @param next Pointer auf nächste URL
	 */
	public Url(String url, int seconds, Url next) {
		this.url = url;
		this.seconds = seconds;
		this.next = next;
		this.guild = false;
	}

	/**
	 * Getter für URL
	 * 
	 * @return URL
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Getter für Next
	 * 
	 * @return Pointer auf nächste {@link Url} oder <code>null</code>
	 */
	public final Url getNext() {
		return this.next;
	}

	/**
	 * <code>true</code> wenn es einen Nachfolgeeintrag gibt
	 * 
	 * @return
	 */
	public final boolean hasNext() {
		return (this.next != null);
	}

	/**
	 * Getter für Sekunden
	 * 
	 * @return Sekunden
	 */
	public final int getSeconds() {
		return this.seconds;
	}
	
	/**
	 * Setzt URL auf Verein
	 * 
	 * @return this
	 */
	public Url setOnlyGuild() {
		this.guild = true;
		return this;
	}

	public boolean isOnlyGuild() {
		return guild;
	}
}
