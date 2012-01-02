package bkampfbot.output;

public class SimpleFile extends AbstractFile {

	protected SimpleFile(String filename) {
		super(filename);
	}
	
	public static void write(String filename, String content) {
		SimpleFile s = new SimpleFile(filename);
		s.write(content);
	}
	
	public static void append(String filename, String content) {
		SimpleFile s = new SimpleFile(filename);
		s.append(content);
	}
}
