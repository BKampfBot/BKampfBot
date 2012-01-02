package bkampfbot.output;

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
						+ "<h1>Auswertung</h1>"
						+ "<table>"
						+ "<tr>"
						+ "<th>Zeitpunkt</th>"
						+ "<th>Informationen</th>" + "<th>Typ</th>" + "</tr>");

		for (File c : list) {
			content.append((new LogFile(c.getAbsolutePath())).getHtmlTr());
		}

		content.append("</table>"+getFooter());
		write(content.toString());
	}
}
