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
