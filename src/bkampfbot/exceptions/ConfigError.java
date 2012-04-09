package bkampfbot.exceptions;

public class ConfigError extends FatalError {
	private static final long serialVersionUID = -5272858242919674121L;

	public ConfigError(String message) {
		super("Konfiguration: " + message);
	}
}
