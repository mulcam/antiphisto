package kr.ac.konkuk.islab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class SFSController {
	private static String bestSet; // 최적 셋
	private static double[] bestaccuracy; // 최고 정확도
	private static int featureNum = 51;
	private static String best;

	public static void main(String[] args) throws Exception {
		long startTotal = System.currentTimeMillis();
		FileIO fio = new FileIO();

		BufferedWriter writer = null;
		best = null;
		bestSet = null;
		bestaccuracy = new double[featureNum];

		FeatureNormalizer fn = new FeatureNormalizer();

		ArrayList<Thread> threads = new ArrayList<Thread>();

		// #1 - 특징 51개에 대한 정확도를 각각 뽑는다.
		// #2 - 제일 높은 정확도를 나타내는 특징을 최적 셋에 입력한다.
		// (Ex. 32번 특징)
		// #3 - 두번 째 입력될 특징을 선택하기 위해 50가지 특징이 입력될 경우의 정확도를 모두 도출하여
		// 최적 특징을 선택한다.
		// #4 - 특징을 모두 입력할때까지 반복...

		ArrayList<String> accuracyAl = null;
		for (int i = 1; i < featureNum + 1; i++) {
			long start = System.currentTimeMillis();
			// 52회 반복
			// 최초 실행시....

			if (i == 1) {
				// 새로 입력하는 특징
				accuracyAl = new ArrayList<String>();
				for (int j = 1; j < featureNum + 1; j++) {

					// Weka파일 생성
					makeWekaFile(i, j);

					// RF 쓰레드 시작
					Thread t = new Thread(new RFThread(i, j, accuracyAl));
					t.start();
					threads.add(t);

				}

				// RF 쓰레드 JOIN
				for (int temp = 0; temp < threads.size(); temp++) {
					Thread t = threads.get(temp);
					try {
						t.join();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// 최적치 설정
				setBestAccuracy(accuracyAl, i);
				System.out.println("Step"+ i + " 최적셋:" + bestSet);

			} else {

				String[] bestmem = bestSet.split("\\,");
				accuracyAl = new ArrayList<String>();
				for (int j = 1; j < featureNum + 1; j++) {

					// 자기 자신과 같은 특징을 제외하기 위해 flag 처리
					int flag = 0;
					for (int x = 0; x < bestmem.length; x++) {
						if (bestmem[x].equals(Integer.toString(j)))
							flag = 1;
					}

					if (flag == 1) {
						continue;
					}

					// weka 파일 생성
					writer = fio.WriteFile("result" + i + "_" + j + ".arff");
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
					BufferedReader nowreader = fio.ReadFile("data/" + j + ".csv");

					// 최적 특징 데이터 리더 설정
					for (int y = 0; y < bestmem.length; y++) {
						reader[y] = fio.ReadFile("data/" + bestmem[y] + ".csv");
					}

					// 파일 내용 작성
					for (int y = 0; y < 20000; y++) {
						// 기존 특징 측정값 입력
						String normInput2 = null;
						for (int z = 0; z < reader.length; z++) {
							normInput2 = fn.getPercentage(Double.parseDouble(reader[z].readLine()),
									Integer.parseInt(bestmem[z]));
							writer.write(normInput2 + ",");
						}

						// 변수 최적화 및 결과값 입력
						normInput2 = fn.getPercentage(Double.parseDouble(nowreader.readLine()), j);
						if (y < 14000) {
							writer.write(normInput2 + "," + "Legit");
							writer.newLine();
						} else {
							writer.write(normInput2 + "," + "Phish");
							writer.newLine();
						}
					}
					writer.flush();
					writer.close();

					// RF 쓰레드 시작
					Thread t = new Thread(new RFThread(i, j, accuracyAl));
					t.start();
					threads.add(t);
				}
				
				// RF 쓰레드 JOIN
				for (int temp = 0; temp < threads.size(); temp++) {
					Thread t = threads.get(temp);
					try {
						t.join();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				// 최적치 설정
				setBestAccuracy(accuracyAl, i);
				System.out.println("Step"+ i + "-최적 특징: " + best);
				bestSet = bestSet + "," + best;
				System.out.println("Step"+ i + "-최적셋: " + bestSet);
			}
			long end = System.currentTimeMillis();
			System.out.println("Step"+ i + "-실행 시간: " + (end - start) / 1000.0 + " sec");
		}
		// 최종 정확도

		for (int i = 0; i < bestaccuracy.length; i++) {
			System.out.print("정확도" + i + ":");
			System.out.println(bestaccuracy[i]);
		}
		String[] member = bestSet.split("\\,");
		// 베스트 정확도
		for (int i = 0; i < member.length; i++) {
			System.out.print(member[i] + ",");
		}
		System.out.println();
		long endTotal = System.currentTimeMillis();
		System.out.println("총 실행 시간: " + (endTotal - startTotal) / 1000.0 + " sec");
	}

	private static void setBestAccuracy(ArrayList<String> accuracyAl, int i) {
		for (String a : accuracyAl) {
			String[] temp = a.split("\\|");
			double tempAccuracy = Double.parseDouble(temp[1]);
			String[] num = temp[0].split("\\/");
			String featNum = num[1];
			if (bestaccuracy[i - 1] < tempAccuracy) {
				bestaccuracy[i - 1] = tempAccuracy;
				if (i == 1)
					bestSet = featNum;
				best = featNum;
			}

			for (int x = 0; x < temp.length; x++) {
				System.out.print(temp[x]);
				System.out.print("\t");
			}
			System.out.println();
		}

	}

	private static void makeWekaFile(int i, int j) throws Exception {
		// Weka 데이터 생성 시작
		// 선택된 특징 개수 만큼 데이터를 생성
		FileIO fio = new FileIO();
		BufferedReader reader = fio.ReadFile("data/" + j + ".csv");
		BufferedWriter writer = null;
		FeatureNormalizer fn = new FeatureNormalizer();

		writer = fio.WriteFile("result" + i + "_" + j + ".arff");
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

		String normInput = null;
		while ((input = reader.readLine()) != null) {

			// Normalization
			normInput = fn.getPercentage(Double.parseDouble(input), j);

			if (count < 14001) {
				writer.write(normInput + "," + "Legit");
				writer.newLine();
			} else {
				writer.write(normInput + "," + "Phish");
				writer.newLine();
			}
			count++;
		}
		writer.flush();
		writer.close();
		// Weka 데이터 생성 끝
	}

}
