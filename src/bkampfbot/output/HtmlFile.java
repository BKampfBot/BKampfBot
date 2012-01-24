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
				+ "<head>" + "<title>"
				+ title
				+ " - BKampfBot</title>"
				+ "<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\"/>"
				+ "<meta name=\"generator\" content=\"BKampfBot "
				+ Control.version
				+ "\"/>"

				+ "<script type=\"text/javascript\" src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js\"></script>"
				+ "<script type=\"text/javascript\" src=\"http://datatables.net/download/build/jquery.dataTables.min.js\"></script>"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://datatables.net/release-datatables/media/css/demo_page.css\"/>"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://datatables.net/release-datatables/media/css/demo_table.css\"/>"
				+ "<script type=\"text/javascript\">"
				+ "$(document).ready(function(){"
				+ "$('#example').dataTable({"
				+ "'aaSorting':[[0,'desc']],"
				+ "'iDisplayLength':25"
				+ "});"
				+ "});"
				+ "</script>"

				+ "<style type=\"text/css\">"
				+ "body{padding:50px}"
				+ "#example{clear:both;margin:auto 5px}"
				+ "#example th{cursor:pointer}"
				+ ".good{background-color:#90EE90}"
				+ ".bad{background-color:#FF6B6B}" + "</style>"

				+ "</head>" + "<body>" + "<div id=\"dt_example\">";
	}

	protected String getFooter() {
		return "</div></body></html>";
	}

}
