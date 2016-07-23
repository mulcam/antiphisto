package kr.ac.konkuk.islab;

import java.io.*;

public class FileIO {
	public BufferedReader ReadFile(String loc) throws Exception {
		File fileread = new File(loc);
		FileReader filereader = new FileReader(fileread);
		BufferedReader reader = new BufferedReader(filereader);

		return reader;
	}

	public BufferedWriter WriteFile(String loc) throws Exception {
		File filewrite = new File(loc);
		FileWriter filewriter = new FileWriter(filewrite);
		BufferedWriter writer = new BufferedWriter(filewriter);

		return writer;
	}

	public int getFileLineCount(String loc) throws Exception {
		LineNumberReader lnr = new LineNumberReader(new FileReader(new File(loc)));
		lnr.skip(Long.MAX_VALUE);
		
		// Add 1 because line index starts at 0
		// Finally, the LineNumberReader object should be closed to prevent
		// resource leak
		lnr.close();
		return lnr.getLineNumber() + 1;

	}

}
