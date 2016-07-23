package kr.ac.konkuk.islab;

import java.util.ArrayList;


public class SFSController {
	private static String bestSet; // 최적 셋
	private static double[] bestaccuracy; // 최고 정확도
	private static int featureNum;
	private static String best;

	public static void main(String[] args) throws Exception {
		long startTotal = System.currentTimeMillis();
		featureNum = Constants.featureNum;
		best = null;
		bestSet = null;
		bestaccuracy = new double[featureNum];

		// FeatureNormalizer fn = new FeatureNormalizer();

		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		ResultFileWriter rfw = new ResultFileWriter(Constants.resultFilePath, Constants.targetName);
		StringBuilder result = new StringBuilder();

		// #1 - 특징 51개에 대한 정확도를 각각 뽑는다.
		// #2 - 제일 높은 정확도를 나타내는 특징을 최적 셋에 입력한다.
		// (Ex. 32번 특징)
		// #3 - 두번 째 입력될 특징을 선택하기 위해 50가지 특징이 입력될 경우의 정확도를 모두 도출하여
		// 최적 특징을 선택한다.
		// #4 - 특징을 모두 입력할때까지 반복...
		String tempString = null;
		
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
					//WekaFileMaker.makeAntiphsitoWekaFile(i, j);
					WekaFileMaker.makeWekaFile(i, j);

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

					//WekaFileMaker.makeAntiphsitoWekaFile(i, j, bestmem);
					WekaFileMaker.makeWekaFile(i, j, bestmem);

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
				
				//bestSet 설정
				bestSet = bestSet + "," + best;
				
			}
			
			tempString = "Step" + i + "-최적 특징: " + best + "\r\n";
			result.append(tempString);
			System.out.print(tempString);
			
			tempString = "Step" + i + "-최적셋: " + bestSet + "\r\n";
			result.append(tempString);
			System.out.print(tempString);
			
			tempString = "Step" + i + "-정확도: " + bestaccuracy[i - 1] + "\r\n";
			result.append(tempString);
			System.out.print(tempString);
			
			long end = System.currentTimeMillis();
			tempString ="Step" + i + "-실행 시간: " + (end - start) / 1000.0 + " sec\r\n";
			result.append(tempString);
			System.out.print(tempString);
		}
		
		// 최종 정확도	
		String[] member = bestSet.split("\\,");
		String delimeter = "";
		tempString = "";
		for (int i = 0; i < bestaccuracy.length; i++) {
			tempString += "정확도" + i + "\t";
			tempString += bestaccuracy[i] + "\t";
			for (int j = 0; j < i + 1; j++) {
				delimeter = ",";
				if(j == i) {delimeter = "";}
				tempString += member[j] + delimeter;
			}
			tempString += "\r\n";
		}
		result.append(tempString);
		System.out.print(tempString);
		
		// 베스트 정확도		
		System.out.println();
		long endTotal = System.currentTimeMillis();
		tempString = "총 실행 시간: " + (endTotal - startTotal) / 1000.0 + " sec";
		result.append(tempString);
		System.out.print(tempString);
		
		rfw.writeFile(result);
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

}
