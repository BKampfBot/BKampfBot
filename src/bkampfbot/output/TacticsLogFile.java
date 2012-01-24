package bkampfbot.output;

import java.io.FileNotFoundException;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

public class TacticsLogFile extends AbstractFile implements DoItLater {
	public final static String FILENAME = "tactics.bkampfbot";
	public final JSONObject data;

	public TacticsLogFile(String path, JSONObject data) {
		super(path + "/" + FILENAME);

		this.data = data;
	}

	public TacticsLogFile(String path) {
		super(path + "/" + FILENAME);

		this.data = null;
	}

	@Override
	public void doIt() {
		if (this.data == null) {
			return;
		}

		try {
			JSONArray rs = data.getJSONArray("results");
			String name = data.getJSONObject("p2").getString("name");

			// Versuche die Taktik zu erraten
			JSONArray tactics = new JSONArray();

			for (int i = 0; i < rs.length(); i++) {
				JSONObject current = rs.getJSONObject(i);
				if (current.getInt("player") == 2) {
					if (current.getString("action").equals("spellhit"))
						continue;
					if (current.getString("action").equals("win"))
						continue;

					boolean ok = false;
					for (int j = 0; j < tactics.length(); j++) {
						if (tactics.getString(j).equals(
								current.getString("attack"))) {
							ok = true;
							break;
						}
					}
					if (!ok) {
						tactics.put(current.getString("attack"));
					}
				}
			}

			JSONObject newOpponent = new JSONObject();
			newOpponent.put("tactics", tactics);

			JSONObject content = new JSONObject(read("{}"));
			if (content.has(name)) {
				content.remove(name);
			}
			content.put(name, newOpponent);

			write(content.toString(2));

		} catch (JSONException e) {
		} catch (FileNotFoundException e) {
		}
	}

	public static String[] getTactics(String name) {
		try {
			TacticsLogFile help = new TacticsLogFile(
					Output.getInstance().htmlPath);
			JSONObject content = new JSONObject(help.read("{}"));
			if (content.has(name)) {
				JSONArray t = content.getJSONArray("tactics");
				String[] tactics = new String[t.length()];
				for (int i = 0; i < t.length(); i++) {
					tactics[i] = t.getString(i);
				}
				return tactics;
			}
		} catch (FileNotFoundException e) {
		} catch (JSONException e) {
		}
		return null;
	}

}
