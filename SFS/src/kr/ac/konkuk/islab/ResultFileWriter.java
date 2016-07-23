package kr.ac.konkuk.islab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ResultFileWriter {

	private String filePath;

	public ResultFileWriter(String path) {	
		makeFilePath(path, "");
	}

	public ResultFileWriter(String path, String headerText ) {	
		makeFilePath(path, headerText + "_");
	}

	private void makeFilePath( String path, String fileHeader ){ 

		//result file process
		DateFormat sdFormat1 = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");
		DateFormat sdFormat2 = new SimpleDateFormat("yyyyMMdd-HH-mm");
		Date nowDate = new Date();

		String fileTime = sdFormat1.format(nowDate);
		String datefolder = sdFormat2.format(nowDate);

		// File dir = new File(path, datefolder);
		//
		// if a folder doesn't exist, create a folder
		// if(!dir.exists()){
		// dir.mkdir();
		// }

		//filePath = dir.getAbsolutePath() + "\\" + fileHeader + "result_"+ fileTime +".txt" ;
		filePath = path + fileHeader + "result_"+ fileTime +".txt" ;
	}


	public void writeFile(StringBuilder stringBuilder){
		try {
			if(stringBuilder.length() > 0){
				FileWriter fos = new FileWriter(filePath, true);
				fos.write(stringBuilder.toString());
				fos.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



}
