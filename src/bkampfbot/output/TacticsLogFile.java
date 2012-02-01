package bkampfbot.output;

import java.io.FileNotFoundException;
import java.util.Date;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

public class TacticsLogFile extends AbstractFile implements DoItLater {
	public final static String FILENAME = "tactics.bkampfbot";
	public final static int minSaveTime = 3600 * 24 * 7;

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
			newOpponent.put("time", (int) ((new Date()).getTime() / 1000));

			JSONObject content = new JSONObject(read("{}"));
			if (content.has(name)) {

				/*
				 * Wenn die neue Menge eine Teilmenge ist, nicht ersetzen.
				 */

				boolean remove = false;

				if (content.getJSONObject(name).has("tactics")) {
					JSONArray oldTactics = content.getJSONObject(name)
							.getJSONArray("tactics");

					for (int i = 0; i < tactics.length(); i++) {
						
						boolean found = false;
						
						for (int j = 0; j < oldTactics.length(); j++) {
							if (tactics.getString(i).equals(
									oldTactics.getString(j))) {
								found = true;
								break;
							}
						}

						if (!found) {
							remove = true;
							break;
						}
					}
				}

				if (remove) {
					content.remove(name);
					content.put(name, newOpponent);
				}
			} else {
				content.put(name, newOpponent);
			}

			write(content.toString());

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
				JSONArray t = content.getJSONObject(name).getJSONArray(
						"tactics");
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
