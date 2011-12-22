package bkampfbot.output;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bkampfbot.state.Config;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

public class FightResult {
	public FightResult(JSONObject result, JSONObject data,  String method) {
		
		if (Config.getInfoPath() == null) {
			return;
		}

		try {
			JSONObject opponent = result.getJSONObject("opponent");

			String name = opponent.getString("name");
			name = name.replaceAll("[^a-zA-Z0-9]", "_");

			String filename = String.valueOf(new Date().getTime()) + name
					+ ".html";

			InfoFile.writeLog("fight:"
					+ opponent.getString("name")
					+ "\n"
					+ "won:"
					+ result.getJSONObject("results").getBoolean("fightWasWon")
					+ "\n"
					+ "money:"
					+ result.getJSONObject("results").getJSONObject("p1")
							.getInt("gold") + "\n" + "method:" + method,
					filename);
			File toWrite = new File(Config.getInfoPath() + "/" + filename);

			/*
			 * if (!toWrite.canWrite()) {
			 * Output.error("File not writeable: "+InfoFile
			 * .path+"/"+opponent.getString("id")+name); return false; }
			 */

			List<Tuple> list = new ArrayList<Tuple>();
			try {
				JSONArray rs = data.getJSONArray("results");

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

			} catch (JSONException e) {
				e.printStackTrace();
			}

			String fightAttacks = "";
			if (list.size() > 0) {
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

			FileWriter writer = new FileWriter(toWrite);
			// writer.write(result.toString());
			writer
					.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
							+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">"
							+ "<head>" + "<title>"
							+ name
							+ "</title>"
							+ "<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\" />"
							+ "</head>"
							+ "<body>"
							+

							"<table border=\"1\" style=\"width: 700px;\">"
							+ "<tr><td style=\"background-color:"
							+ ((result.getJSONObject("results").getBoolean(
									"fightWasWon") ? "green" : "red"))
							+ ";\">"
							+ ((result.getJSONObject("results").getBoolean(
									"fightWasWon") ? "Gewonnen" : "Verloren"))
							+ "</td><th style=\"width: 40%;\">"
							+ opponent.getString("name")
							+ "</th><th style=\"width: 40%;\">Ich</th></tr>"
							+ "<tr><th>Nahkampfschaden</th><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p2").getJSONObject("fightDamage").getInt(
									"from")
							+ "</td><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p1").getJSONObject("fightDamage").getInt(
									"from")
							+ "</td></tr>"
							+ "<tr><th>Dachschaden</th><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p2").getInt("magicDamage")
							+ "</td><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p1").getInt("magicDamage")
							+ "</td></tr>"
							+ "<tr><th>Treffer</th><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p2").getInt("hits")
							+ "</td><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p1").getInt("hits")
							+ "</td></tr>"
							+ "<tr><th>Kritische Treffer</th><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p2").getInt("criticalHits")
							+ "</td><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p1").getInt("criticalHits")
							+ "</td></tr>"
							+ "<tr><th>Angriffe abgelenkt</th><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p2").getInt("blocked")
							+ "</td><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p1").getInt("blocked")
							+ "</td></tr>"
							+ "<tr><th>Eindruck</th><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p2").getInt("platting")
							+ "</td><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p1").getInt("platting")
							+ "</td></tr>"
							+ "<tr><th>Lebensenergie</th><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p2").getInt("lp")
							+ "/"
							+ result.getJSONObject("results").getJSONObject(
									"p2").getInt("maxLp")
							+ "</td><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p1").getInt("lp")
							+ "/"
							+ result.getJSONObject("results").getJSONObject(
									"p1").getInt("maxLp")
							+ "</td></tr>"
							+ "<tr><th>D-Mark</th><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p2").getInt("gold")
							+ "</td><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p1").getInt("gold")
							+ "</td></tr>"
							+ "<tr><th>Respekt Spieler</th><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p2").getInt("hp")
							+ "</td><td>"
							+ result.getJSONObject("results").getJSONObject(
									"p1").getInt("hp")
							+ "</td></tr>"
							+ "<tr><th>Respekt Verein</th><td>"
							+ guildHpP2
							+ "</td><td>"
							+ guildHpP1
							+ "</td></tr>"
							+ "</table>" + fightAttacks + "</body>" + "</html>");
			writer.flush();
			writer.close();

			toWrite.setWritable(true, false);

			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public class Tuple {
		public String key;
		public int count;

		public Tuple(String k) {
			key = k;
			count = 1;
		}
	}
}
