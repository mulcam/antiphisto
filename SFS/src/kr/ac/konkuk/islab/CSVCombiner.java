package kr.ac.konkuk.islab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;

public class CSVCombiner {

	public static void main(String args[]) throws Exception {

		ArrayList<String[]> list = new ArrayList<>();
		FileIO fio = null;
		BufferedReader reader = null;
		String input = null;
		
		FeatureNormalizer fn = new FeatureNormalizer();
		
		for (int j = 1; j < 52; j++) {

			fio = new FileIO();

			reader = fio.ReadFile("data/" + j + ".csv");

			input = null;

			int count = 0;
			String[] temp = null;
			while ((input = reader.readLine()) != null) {
				
				if (j == 1) {
					list.add(new String[51]);
				}
				temp = list.get(count);
				list.remove(count);
				//temp[j - 1] = input;
				
				//Normalize
				temp[j - 1] = fn.getPercentage(Double.parseDouble(input),j);
				list.add(count,temp);
				count++;
			}

		}

		BufferedWriter writer = null;
		writer = fio.WriteFile("CSV_TEST.txt");

		String[] temp2 = list.get(0);
		for (int i = 0; i < temp2.length; i++) {
			System.out.print(i + ":");
			System.out.println(temp2[i] + ",");
		}
		
		

		for (int k = 0; k < list.size() ;k++) {
			String[] test = list.get(k);
			for (int i = 0; i < test.length; i++) {
				writer.write(test[i] + ",");
			}
			if (k < 14001) {
				writer.write("Legit");

			} else {
				writer.write("Phish");
			}
			writer.newLine();
		}
		writer.flush();
		writer.close();

	}

}
