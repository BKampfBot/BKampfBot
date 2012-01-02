package bkampfbot.output;

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
