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

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Date;

import json.JSONException;
import json.JSONObject;
import bkampfbot.Control;

public class LogFile extends AbstractFile implements DoItLater {
	public final static String FILE_EXTENSION = ".bkampfbot";

	JSONObject content;

	public LogFile(JSONObject content, String path) {
		super(path + String.valueOf(new Date().getTime()) + FILE_EXTENSION);

		this.content = content;
	}

	public LogFile(String filename) {
		super(filename);
	}

	public String getHtmlTr() {
		try {
			JSONObject file = new JSONObject(read());

			StringBuffer output = new StringBuffer("<tr><td>");

			// Datum
			output.append(DateFormat.getDateTimeInstance(DateFormat.SHORT,
					DateFormat.SHORT)
					.format(new Date(file.getLong("datetime"))));

			output.append("</td><td>");

			// Informationen
			if (file.has("file")) {
				output.append("<a href=\"" + file.getString("file") + "\">");
			}
			output.append(file.getString("information"));
			if (file.has("file")) {
				output.append("</a>");
			}

			output.append("</td><td");

			// Farbe
			if (file.has("good")) {
				if (file.getBoolean("good")) {
					output.append(" class=\"good\"");
				} else {
					output.append(" class=\"bad\"");
				}
			}

			output.append(">" + file.getString("type") + "</td></tr>");

			return output.toString();

		} catch (JSONException e) {
			return "";
		} catch (FileNotFoundException e) {
			return "";
		}
	}

	@Override
	public void doIt() {
		try {
			content.put("datetime", new Date().getTime());
			content.put("Version", Control.version);
			write(content.toString());
		} catch (JSONException e) {
		}
	}

	/**
	 * Factory for Log entry
	 * 
	 * @param type
	 * @param information
	 * @return Log
	 * @throws JSONException
	 */
	public static JSONObject getLog(String type, String information)
			throws JSONException {
		JSONObject log = new JSONObject();
		log.put("type", type);
		log.put("information", information);
		return log;
	}
}
