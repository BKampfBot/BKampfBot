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

import json.JSONObject;
import bkampfbot.state.Config;

public class GoldenFile extends HtmlFile implements DoItLater {

	protected JSONObject result;

	protected GoldenFile(JSONObject result, String filename) {
		super(filename);

		this.result = result;
	}

	@Override
	public void doIt() {

		try {
			write(getHeader("Goldener Zwerg")
					+ "<table border=\"1\" style=\"width: 700px;\">"
					+ "<tr><td style=\"background-color:"
					+ ((result.getInt("won") == 1 ? "green" : "red"))
					+ ";\">"
					+ ((result.getInt("won") == 1 ? "Gewonnen" : "Verloren"))
					+ "</td><th style=\"width: 40%;\">"
					+ result.getJSONObject("opponent").getString("name")
					+ "</th><th style=\"width: 40%;\">Ich</th></tr>"
					+ "<tr><th>Nahkampfschaden</th><td>"
					+ result.getJSONObject("p2").getJSONObject("fightDamage")
							.getInt("from")
					+ "</td><td>"
					+ result.getJSONObject("p1").getJSONObject("fightDamage")
							.getInt("from")
					+ "</td></tr>"
					+ "<tr><th>Dachschaden</th><td>"
					+ result.getJSONObject("p2").getInt("magicDamage")
					+ "</td><td>"
					+ result.getJSONObject("p1").getInt("magicDamage")
					+ "</td></tr>"
					+ "<tr><th>Treffer</th><td>"
					+ result.getJSONObject("p2").getInt("hits")
					+ "</td><td>"
					+ result.getJSONObject("p1").getInt("hits")
					+ "</td></tr>"
					+ "<tr><th>Kritische Treffer</th><td>"
					+ result.getJSONObject("p2").getInt("criticalHits")
					+ "</td><td>"
					+ result.getJSONObject("p1").getInt("criticalHits")
					+ "</td></tr>"
					+ "<tr><th>Angriffe abgelenkt</th><td>"
					+ result.getJSONObject("p2").getInt("blocked")
					+ "</td><td>"
					+ result.getJSONObject("p1").getInt("blocked")
					+ "</td></tr>"
					+ "<tr><th>Eindruck</th><td>"
					+ result.getJSONObject("p2").getInt("platting")
					+ "</td><td>"
					+ result.getJSONObject("p1").getInt("platting")
					+ "</td></tr>"
					+ "<tr><th>Lebensenergie</th><td>"
					+ result.getJSONObject("p2").getInt("lp")
					+ "/"
					+ result.getJSONObject("p2").getInt("maxLp")
					+ "</td><td>"
					+ result.getJSONObject("p1").getInt("lp")
					+ "/"
					+ result.getJSONObject("p1").getInt("maxLp")
					+ "</td></tr>"
					+ "</table>"
					+

					"<table border=\"1\">"
					+ "<tr><th>Strategie</th><td>"
					+ result.getJSONObject("opponent").getJSONObject("tactic")
							.toString() + "</td></tr>" + "</table>"
					+ getFooter());
		} catch (Exception e) {
			if (Config.getDebug()) {
				e.printStackTrace();
			}
		}

	}
}
