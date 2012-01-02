package bkampfbot.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

abstract public class AbstractFile {
	protected final String filename;

	protected AbstractFile(String filename) {
		this.filename = filename;
	}

	protected String read() throws FileNotFoundException {
		try {
			File file = new File(filename);

			if (!file.isFile() || !file.exists()) {
				throw new FileNotFoundException();
			}

			StringBuilder contents = new StringBuilder();

			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}

			return contents.toString();
		} catch (IOException e) {
			Output.error(e);
			throw new FileNotFoundException();
		}
	}

	protected void write(String content) {
		this.write(content, false);
	}

	private void write(String content, boolean append) {
		try {
			FileWriter f = new FileWriter(new File(filename), append);
			f.write(content);
			f.flush();
			f.close();
		} catch (IOException e) {
			Output.error(e);
		}
	}

	protected void append(String content) {
		this.write(content, true);
	}
}
