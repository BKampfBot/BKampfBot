package bkampfbot.utils;

import bkampfbot.Control;
import bkampfbot.output.Output;
import bkampfbot.output.SimpleFile;
import bkampfbot.state.Config;

public class ErrorLog {
	public final static String FILENAME = "errors.txt";

	public ErrorLog(String message) {
		StringBuffer content = new StringBuffer();
		content.append("\n----------------------------------------\n----------------------------------------\n");
		content.append(Config.getDate());
		content.append(" -- ");
		content.append(Control.version);
		content.append("\n\n");
		content.append(message);
		content.append("\n\n");
		
		
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		
		int help = 1;
		
		for (StackTraceElement stackTraceElement : st) {
			
			if (help < 0) {
				content.append(stackTraceElement);
				content.append("\n");
			} else {
				--help;
			}
		}

		content.append("\n----------------------------------------\n");
		
		SimpleFile.append(FILENAME, content.toString());

		Output.println("----------------------------", Output.ERROR);
		Output.println("--   Fehler aufgetreten   --", Output.ERROR);
		Output.println("-- siehe Datei errors.txt --", Output.ERROR);
		Output.println("----------------------------", Output.ERROR);
	}
}
