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

import json.JSONException;
import json.JSONObject;

public class BefehlFile extends AbstractFile {
	protected final static String FILENAME = "befehl.json";

	public BefehlFile() {
		super("./" + FILENAME);
	}

	public JSONObject getBefehl() {
		JSONObject now = null;
		try {
			JSONObject file = new JSONObject(read());

			if (file.getJSONArray("Befehl").length() == 0) {
				return null;
			}

			now = file.getJSONArray("Befehl").getJSONObject(0);
			file.getJSONArray("Befehl").remove(0);

			write(file.toString());
		} catch (JSONException e) {
			Output.error(e);
			return null;
		} catch (FileNotFoundException e) {
			Output.printTabLn("Keine Befehlsdatei gefunden.", Output.ERROR);
			return null;
		}
		return now;
	}
}
