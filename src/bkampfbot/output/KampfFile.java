package bkampfbot.output;

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

import java.util.ArrayList;
import java.util.List;

import bkampfbot.state.Config;
import bkampfbot.state.Tuple;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

public class KampfFile extends HtmlFile implements DoItLater {

	protected JSONObject result;

	protected KampfFile(JSONObject result, String filename) {
		super(filename);

		this.result = result;
	}

	@Override
	public void doIt() {

		try {
			JSONObject data = result.getJSONObject("fightData");
			JSONArray rs = data.getJSONArray("results");

			// Versuche die Taktik zu erraten
			List<Tuple> list = new ArrayList<Tuple>();
			for (int i = 0; i < rs.length(); i++) {
				JSONObject current = rs.getJSONObject(i);
				if (current.getInt("player") == 2) {
					if (current.getString("action").equals("spellhit"))
						continue;
					if (current.getString("action").equals("win"))
						continue;

					boolean ok = false;
					for (Tuple c : list) {

						if (c.key.equals(current.getString("attack"))) {
							c.count++;
							ok = true;
							break;
						}
					}
					if (!ok) {
						list.add(new Tuple(current.getString("attack")));
					}
				}
			}

			String fightAttacks = "";
			if (list.size() > 0) {
				fightAttacks += "<h2>Taktik des Gegners</h2>";
				fightAttacks += "<table border=\"1\" style=\"width: 400px;\">";
				for (Tuple c : list) {
					fightAttacks += "<tr><td>" + c.key + "</td><td>" + c.count
							+ "</td></tr>";

				}
				fightAttacks += "</table>";
			}

			String guildHpP1 = "", guildHpP2 = "";
			try {
				guildHpP1 = String.valueOf(result.getJSONObject("results")
						.getJSONObject("p1").getInt("guildHp"));
				guildHpP2 = String.valueOf(result.getJSONObject("results")
						.getJSONObject("p2").getInt("guildHp"));
			} catch (JSONException e) {
			}

			write(getHeader(result.getJSONObject("opponent").getString("name"))
					+ "<table border=\"1\" style=\"width: 700px;\">"
					+ "<tr><td style=\"background-color:"
					+ ((result.getJSONObject("results").getBoolean(
							"fightWasWon") ? "green" : "red"))
					+ ";\">"
					+ ((result.getJSONObject("results").getBoolean(
							"fightWasWon") ? "Gewonnen" : "Verloren"))
					+ "</td><th style=\"width: 40%;\">"
					+ result.getJSONObject("opponent").getString("name")
					+ "</th><th style=\"width: 40%;\">"
					+ Config.getUserName()
					+ "</th></tr>"
					+ "<tr><th>Nahkampfschaden</th><td>"
					+ result.getJSONObject("results").getJSONObject("p2")
							.getJSONObject("fightDamage").getInt("from")
					+ "</td><td>"
					+ result.getJSONObject("results").getJSONObject("p1")
							.getJSONObject("fightDamage").getInt("from")
					+ "</td></tr>"
					+ "<tr><th>Dachschaden</th><td>"
					+ result.getJSONObject("results").getJSONObject("p2")
							.getInt("magicDamage")
					+ "</td><td>"
					+ result.getJSONObject("results").getJSONObject("p1")
							.getInt("magicDamage")
					+ "</td></tr>"
					+ "<tr><th>Treffer</th><td>"
					+ result.getJSONObject("results").getJSONObject("p2")
							.getInt("hits")
					+ "</td><td>"
					+ result.getJSONObject("results").getJSONObject("p1")
							.getInt("hits")
					+ "</td></tr>"
					+ "<tr><th>Kritische Treffer</th><td>"
					+ result.getJSONObject("results").getJSONObject("p2")
							.getInt("criticalHits")
					+ "</td><td>"
					+ result.getJSONObject("results").getJSONObject("p1")
							.getInt("criticalHits")
					+ "</td></tr>"
					+ "<tr><th>Angriffe abgelenkt</th><td>"
					+ result.getJSONObject("results").getJSONObject("p2")
							.getInt("blocked")
					+ "</td><td>"
					+ result.getJSONObject("results").getJSONObject("p1")
							.getInt("blocked")
					+ "</td></tr>"
					+ "<tr><th>Eindruck</th><td>"
					+ result.getJSONObject("results").getJSONObject("p2")
							.getInt("platting")
					+ "</td><td>"
					+ result.getJSONObject("results").getJSONObject("p1")
							.getInt("platting")
					+ "</td></tr>"
					+ "<tr><th>Lebensenergie</th><td>"
					+ result.getJSONObject("results").getJSONObject("p2")
							.getInt("lp")
					+ "/"
					+ result.getJSONObject("results").getJSONObject("p2")
							.getInt("maxLp")
					+ "</td><td>"
					+ result.getJSONObject("results").getJSONObject("p1")
							.getInt("lp")
					+ "/"
					+ result.getJSONObject("results").getJSONObject("p1")
							.getInt("maxLp")
					+ "</td></tr>"
					+ "<tr><th>D-Mark</th><td>"
					+ result.getJSONObject("results").getJSONObject("p2")
							.getInt("gold")
					+ "</td><td>"
					+ result.getJSONObject("results").getJSONObject("p1")
							.getInt("gold")
					+ "</td></tr>"
					+ "<tr><th>Respekt Spieler</th><td>"
					+ result.getJSONObject("results").getJSONObject("p2")
							.getInt("hp")
					+ "</td><td>"
					+ result.getJSONObject("results").getJSONObject("p1")
							.getInt("hp") + "</td></tr>"
					+ "<tr><th>Respekt Verein</th><td>" + guildHpP2
					+ "</td><td>" + guildHpP1 + "</td></tr>" + "</table>"
					+ fightAttacks + getFooter());
		} catch (Exception e) {
			if (Config.getDebug()) {
				e.printStackTrace();
			}
		}

	}
}
