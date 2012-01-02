package bkampfbot.output;

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
