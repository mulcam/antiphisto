package kr.ac.konkuk.islab;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class WekaFileMaker {

	public static void makeWekaFile(int i, int j, String[] bestmem, String fileExt) throws Exception {
		FileIO fio = new FileIO();

		boolean newFeatureChk = false;
		BufferedReader nowreader = null;

		int fileLineCount = 0;
		BufferedWriter writer = null;

		boolean bestMemChk = false;
		if (bestmem != null) {
			bestMemChk = true;
		}
		if (fileExt == null) {
			newFeatureChk = true;
			// 새 특징 데이터 리더 설정
			nowreader = fio.ReadFile(Constants.dataFolder + "/" + j + ".csv");
			fileLineCount = fio.getFileLineCount(Constants.dataFolder + "/" + j + ".csv");
			fileExt ="";
		} else {
			fileExt = "_" + fileExt;
		}

		// 결과 데이터 리더 설정
		BufferedReader resultReader = fio.ReadFile(Constants.dataFolder + "/result.csv");
		// weka 파일 생성
		writer = fio.WriteFile(Constants.resultFolder + "/result" + i + fileExt + "_" + j + ".arff");
		writer.write("@relation " + Constants.targetName);
		writer.newLine();

		String headerText = null;
		BufferedReader[] reader = null;
		if (bestMemChk) {
			// 기존 특징 데이터 리더 설정
			// 데이터 추가
			reader = new BufferedReader[bestmem.length];
			fileLineCount = fio.getFileLineCount(Constants.dataFolder + "/" + bestmem[0] + ".csv");
			for (int y = 0; y < bestmem.length; y++) {
				reader[y] = fio.ReadFile(Constants.dataFolder + "/" + bestmem[y] + ".csv");
			}

			// 특징 수 만큼 변수 서술
			for (BufferedReader a : reader) {
				headerText = a.readLine();
				headerText = headerText.replace("\"", "");
				writer.write(headerText);
				writer.newLine();
			}
		}

		if (newFeatureChk) {
			// 현재 변수 서술
			headerText = nowreader.readLine();
			headerText = headerText.replace("\"", "");
			writer.write(headerText);
			writer.newLine();
		}

		// 결과 변수 서술
		headerText = resultReader.readLine();
		headerText = headerText.replace("\"", "");
		writer.write(headerText);
		writer.newLine();
		writer.newLine();

		// 파일 내용 작성
		writer.write("@data");
		writer.newLine();
		String tempString = null;
		for (int y = 0; y < fileLineCount - 2; y++) {

			if (bestMemChk) {
				// 변수 데이터 출력
				for (int z = 0; z < reader.length; z++) {
					tempString = reader[z].readLine();
					if (tempString != null) {
						writer.write(tempString + ",");
						// System.out.print(tempString + ",");
					}
				}
			}

			if (newFeatureChk) {
				// 추가되는 특징 데이터 입력
				tempString = nowreader.readLine();
				if (tempString != null) {
					writer.write(tempString + ",");
					// System.out.print(tempString + ",");
				}
			}

			// 결과값 입력
			tempString = resultReader.readLine();
			if (tempString != null) {
				writer.write(tempString);
				// System.out.println(tempString);
			}
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}

	public static void makeWekaFile(int i, int j, String[] bestmem) throws Exception {
		makeWekaFile(i, j, bestmem, null);
	}

	public static void makeWekaFile(int i, int j) throws Exception {
		makeWekaFile(i, j, null, null);
	}

	public static void makeAntiphsitoWekaFile(int i, int j, String[] bestmem) throws Exception {
		FileIO fio = new FileIO();

		BufferedWriter writer = null;
		// weka 파일 생성
		writer = fio.WriteFile(Constants.resultFolder + "/result" + i + "_" + j + ".arff");
		writer.write("@relation Features");
		writer.newLine();

		// 특징 수 만큼 변수 서술
		for (int k = 0; k < i; k++) {
			writer.write("@attribute testfeature" + (k + 1) + " numeric");
			writer.newLine();
		}
		writer.write("@attribute Result {Phish, Legit}");
		writer.newLine();
		writer.newLine();
		writer.write("@data");
		writer.newLine();

		// 데이터 추가
		BufferedReader[] reader = new BufferedReader[bestmem.length];

		// 새 특징 데이터 리더 설정
		BufferedReader nowreader = fio.ReadFile(Constants.dataFolder + "/" + j + ".csv");

		// 최적 특징 데이터 리더 설정
		for (int y = 0; y < bestmem.length; y++) {
			reader[y] = fio.ReadFile(Constants.dataFolder + "/" + bestmem[y] + ".csv");
		}

		// 파일 내용 작성
		for (int y = 0; y < 20000; y++) {
			// 기존 특징 측정값 입력
			// String normInput2 = null;
			for (int z = 0; z < reader.length; z++) {
				// normInput2 =
				// fn.getPercentage(Double.parseDouble(reader[z].readLine()),
				// Integer.parseInt(bestmem[z]));
				writer.write(reader[z].readLine() + ",");
			}

			// 변수 최적화 및 결과값 입력
			// normInput2 =
			// fn.getPercentage(Double.parseDouble(nowreader.readLine()), j);
			if (y < 14000) {
				writer.write(nowreader.readLine() + "," + "Legit");
				writer.newLine();
			} else {
				writer.write(nowreader.readLine() + "," + "Phish");
				writer.newLine();
			}
		}
		writer.flush();
		writer.close();
	}

	public static void makeAntiphsitoWekaFile(int i, int j) throws Exception {
		// Weka 데이터 생성 시작
		// 선택된 특징 개수 만큼 데이터를 생성
		FileIO fio = new FileIO();
		BufferedReader reader = fio.ReadFile(Constants.dataFolder + "/" + j + ".csv");
		BufferedWriter writer = null;
		FeatureNormalizer fn = new FeatureNormalizer();

		writer = fio.WriteFile(Constants.resultFolder + "/result" + i + "_" + j + ".arff");
		writer.write("@relation Features");
		writer.newLine();
		for (int k = 0; k < i; k++) {
			writer.write("@attribute testfeature" + (k + 1) + " numeric");
			writer.newLine();
		}
		writer.write("@attribute Result {Phish, Legit}");
		writer.newLine();
		writer.newLine();
		writer.write("@data");
		writer.newLine();

		String input = null;
		int count = 1;

		// String normInput = null;
		while ((input = reader.readLine()) != null) {

			// Normalization
			// normInput = fn.getPercentage(Double.parseDouble(input), j);

			if (count < 14001) {
				writer.write(input + "," + "Legit");
				writer.newLine();
			} else {
				writer.write(input + "," + "Phish");
				writer.newLine();
			}
			count++;
		}
		writer.flush();
		writer.close();
		// Weka 데이터 생성 끝
	}

}
