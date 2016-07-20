package kr.ac.konkuk.islab;

import java.io.*;

public class FileIO 
{
	public BufferedReader ReadFile(String loc) throws Exception
	{
		File fileread = new File(loc);
		FileReader filereader = new FileReader(fileread);
		BufferedReader reader = new BufferedReader(filereader);
		
		return reader;
	}
	
	public BufferedWriter WriteFile(String loc) throws Exception
	{
		File filewrite = new File(loc);
		FileWriter filewriter = new FileWriter(filewrite);
		BufferedWriter writer = new BufferedWriter(filewriter);
		
		return writer;
	}
}
