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

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.regex.Pattern;

public class IndexFile extends HtmlFile implements DoItLater {
	public final static String FILENAME = "index.html";
	private final String path;

	public IndexFile(String path) {
		super(path + "/" + FILENAME);
		this.path = path;
	}

	@Override
	public void doIt() {

		// hole Log-Dateien
		File path = new File(this.path + Output.DIR_LOG);

		final Pattern pattern = Pattern.compile(".+"
				+ Pattern.quote(LogFile.FILE_EXTENSION) + "$");

		File[] list = path.listFiles(new FileFilter() {

			@Override
			public boolean accept(File filename) {
				return pattern.matcher(filename.getName()).matches()
						&& filename.isFile();
			}
		});

		Arrays.sort(list);

		StringBuffer content = new StringBuffer(getHeader("Überblick")
				+ "<h1>Auswertung</h1>" + "<table id=\"example\">"
				+ "<thead><tr>"
				+ "<th style=\"min-width:150px\">Zeitpunkt</th>"
				+ "<th style=\"min-width:250px\">Informationen</th>"
				+ "<th style=\"min-width:100px\">Typ</th>" + "</tr></thead>");

		for (File c : list) {
			content.append((new LogFile(c.getAbsolutePath())).getHtmlTr());
		}

		content.append("</table>" + getFooter());
		write(content.toString());
	}
}
