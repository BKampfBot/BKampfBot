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

import bkampfbot.Control;

public abstract class HtmlFile extends AbstractFile {

	protected HtmlFile(String filename) {
		super(filename);
	}

	protected String getHeader(String title) {
		return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">"
				+ "<head>"
				+ "<title>"
				+ title
				+ " - BKampfBot</title>"
				+ "<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\"/>"
				+ "<meta name=\"generator\" content=\"BKampfBot "
				+ Control.version
				+ "\"/>"
				+ "<style type=\"text/css\">"
				+ ".good{background-color:#90EE90}"
				+ ".bad{background-color:#FF6B6B}"
				+ "</style>"
				+ "</head>"
				+ "<body>";
	}

	protected String getFooter() {
		return "</body></html>";
	}

}
