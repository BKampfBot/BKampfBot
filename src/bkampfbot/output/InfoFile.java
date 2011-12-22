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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import json.JSONTokener;
import bkampfbot.Control;
import bkampfbot.state.Config;

public class InfoFile {
	private static final String logPath = "/log/";
	private static final String befehl = "/befehl/befehl.json";

	final public static void initiate() {
		if (!new File(Config.getInfoPath()).isDirectory()) {
			Output.println("Directory doesn't exist: " + Config.getInfoPath(), Output.ERROR);
			Config.setInfoPath(null);
		}

		File logDir = new File(Config.getInfoPath() + InfoFile.logPath);
		if (!logDir.isDirectory() && !logDir.mkdir()) {
			Output.println("Directory doesn't exist: " + Config.getInfoPath()
					+ InfoFile.logPath, 0);
			Config.setInfoPath(null);
		}
	}

	final public static JSONObject getBefehl() {
		if (Config.getInfoPath() == null) {
			return null;
		}

		try {

			File toWrite = new File(Config.getInfoPath() + InfoFile.befehl);
			if (!toWrite.isFile() || !toWrite.exists()) {
				Output.println("File not found", 2);
				return null;
			}

			StringBuilder contents = new StringBuilder();

			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(toWrite));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}

			JSONObject now = null;
			try {
				JSONArray befehl = new JSONObject(new JSONTokener(
						"{\"Befehl\":[" + contents.toString() + "]}"))
						.getJSONArray("Befehl");

				if (befehl.length() == 0)
					return null;
				now = befehl.getJSONObject(0);
				befehl.remove(0);

				FileWriter writer = new FileWriter(toWrite);
				String write = befehl.toString();
				write = write.substring(1);
				write = write.substring(0, write.length() - 1);
				writer.write(write);
				writer.flush();
				writer.close();
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
			return now;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	final public static void writeLog(String text, String file) {
		if (Config.getInfoPath() == null) {
			return;
		}

		try {
			File toWrite = new File(Config.getInfoPath() + InfoFile.logPath
					+ String.valueOf(new Date().getTime()));

			FileWriter writer = new FileWriter(toWrite);
			writer.write(text);
			if (file != null) {
				writer.write("\nurl:" + file);
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	final public static void debug(String filename, String content) {
		debug(filename, content, false);
	}

	final public static void debug(String filename, String content,
			boolean append) {
		if (Config.getInfoPath() == null) {
			return;
		}

		if (Control.debug) {
			Output.println("Write debug output: " + Config.getInfoPath() + "/"
					+ filename, 2);
		}
		try {
			File toWrite = new File(Config.getInfoPath() + "/" + filename);

			FileWriter writer = new FileWriter(toWrite, append);
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void writeGolden(JSONObject result) {
		if (Config.getInfoPath() == null) {
			return;
		}

		try {
			JSONObject opponent = result.getJSONObject("p2");

			String name = opponent.getString("name");
			name = name.replaceAll("[^a-zA-Z0-9]", "_");

			String filename = String.valueOf(new Date().getTime()) + name
					+ ".html";

			InfoFile.writeLog("golden:" + opponent.getString("name") + "\n"
					+ "won:" + ((result.getInt("won") == 1) ? "true" : "false")
					+ "\n", filename);
			File toWrite = new File(Config.getInfoPath() + "/" + filename);

			/*
			 * if (!toWrite.canWrite()) {
			 * Output.error("File not writeable: "+InfoFile
			 * .path+"/"+opponent.getString("id")+name); return false; }
			 */

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
							+ ((result.getInt("won") == 1 ? "green" : "red"))
							+ ";\">"
							+ ((result.getInt("won") == 1 ? "Gewonnen"
									: "Verloren"))
							+ "</td><th style=\"width: 40%;\">"
							+ opponent.getString("name")
							+ "</th><th style=\"width: 40%;\">Ich</th></tr>"
							+ "<tr><th>Nahkampfschaden</th><td>"
							+ result.getJSONObject("p2").getJSONObject(
									"fightDamage").getInt("from")
							+ "</td><td>"
							+ result.getJSONObject("p1").getJSONObject(
									"fightDamage").getInt("from")
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
							+ opponent.getJSONObject("tactic").toString()
							+ "</td></tr>" + "</table>" + "</body>" + "</html>");
			writer.flush();
			writer.close();

			toWrite.setWritable(true, false);

			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}
}
