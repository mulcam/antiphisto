package kr.ac.konkuk.islab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class SFSController {
	private static String bestSet = null; // 최적 셋
	private static double[] bestaccuracy = new double[51]; // 최고 정확도

	public static void main(String[] args) throws Exception {

		FileIO fio = new FileIO();

		BufferedWriter writer = null;

		FeatureNormalizer fn = new FeatureNormalizer();

		/****
		 * #1 - 특징 51개에 대한 정확도를 각각 뽑는다. #2 - 제일 높은 정확도를 나타내는 특징을 최적 셋에 입력한다.
		 * (Ex. 32번 특징) #3 - 두번 째 입력될 특징을 선택하기 위해 50가지 특징이 입력될 경우의 정확도를 모두 도출하여
		 * 최적 특징을 선택한다. #4 - 특징을 모두 입력할때까지 반복...
		 */
		for (int i = 1; i < 52; i++) {
			long start = System.currentTimeMillis();
			// 52회 반복
			// 최초 실행시....
			if (i == 1) {
				//새로 입력하는 특징
				for (int j = 1; j < 52; j++) {
					// 시작
					makeWekaFile(i, j);
					checkAccuracy(i, j);
				}
				System.out.println(bestSet);
			} else {
				String best = null;
				String[] bestmem = bestSet.split("\\,");
				for (int j = 1; j < 52; j++) {
					int flag = 0;
					for (int x = 0; x < bestmem.length; x++) {
						if (bestmem[x].equals(Integer.toString(j)))
							flag = 1;
					}

					if (flag == 1) {
						continue;
					}

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

					BufferedReader[] reader = new BufferedReader[bestmem.length];
					BufferedReader nowreader = fio.ReadFile("data/" + j + ".csv");
					for (int y = 0; y < bestmem.length; y++) {
						reader[y] = fio.ReadFile("data/" + bestmem[y] + ".csv");
					}

					for (int y = 0; y < 20000; y++) {
						for (int z = 0; z < reader.length; z++) {
							writer.write(reader[z].readLine() + ",");
						}

						String normInput2 = fn.getPercentage(Double.parseDouble(nowreader.readLine()), j);
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

					nowreader = fio.ReadFile("result" + i + "_" + j + ".arff");
					Instances dataset = new Instances(nowreader);

					dataset.setClassIndex(dataset.numAttributes() - 1);
					RandomForest classifier = new RandomForest();
					Evaluation eval = new Evaluation(dataset);
					eval.crossValidateModel(classifier, dataset, 10, new Random(1));

					if (bestaccuracy[i - 1] < eval.pctCorrect()) {
						bestaccuracy[i - 1] = eval.pctCorrect();
						best = Integer.toString(j);
					}
					System.out.println(bestSet + "," + j + " : " + eval.pctCorrect());
				}
				System.out.println(best);
				bestSet = bestSet + "," + best;
				System.out.println(bestSet);
			}
			long end = System.currentTimeMillis();
			System.out.println(i + " 실행 시간 : " + ( end - start )/1000.0 );
		}
		for (int i = 0; i < bestaccuracy.length; i++) {
			System.out.println(bestaccuracy[i]);
		}
		String[] member = bestSet.split("\\,");
		for (int i = 0; i < member.length; i++) {
			for (int j = 0; j < i + 1; j++) {
				System.out.print(member[i] + ",");
			}
			System.out.println();
		}
	}

	private static void checkAccuracy(int i, int j) throws Exception {
		FileIO fio = new FileIO();
		BufferedReader reader = fio.ReadFile("result" + i + "_" + j + ".arff");
		Instances dataset = new Instances(reader);

		// 데이터 셋 생성
		dataset.setClassIndex(dataset.numAttributes() - 1);

		// 분류기 설정
		RandomForest classifier = new RandomForest();

		// 정확도 도출
		Evaluation eval = new Evaluation(dataset);
		eval.crossValidateModel(classifier, dataset, 10, new Random(1));

		// 최적치 설정
		if (bestaccuracy[0] < eval.pctCorrect()) {
			bestaccuracy[0] = eval.pctCorrect();
			bestSet = Integer.toString(j);
		}
		System.out.println(j + " : " + eval.pctCorrect());

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
