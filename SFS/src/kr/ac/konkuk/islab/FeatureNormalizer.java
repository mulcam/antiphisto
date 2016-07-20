package kr.ac.konkuk.islab;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class FeatureNormalizer {
	private Map<Integer, Double>  featureMax;
	private Map<Integer, Double> featureMin;
	

	public  FeatureNormalizer() throws Exception {

		featureMax = new HashMap<Integer, Double>();
		featureMin = new HashMap<Integer, Double>();
		FileIO fio = null;
		BufferedReader reader = null;
		String input = null;
		
		//System.out.println("No\tMAX\tMIN");
		for (int j = 1; j < 52; j++) {

			fio = new FileIO();

			reader = fio.ReadFile("data/" + j + ".csv");

			input = null;

			double max = 0.0, tempInt = 0.0, min = 0.0;
			
			while ((input = reader.readLine()) != null) {
				tempInt = Double.parseDouble(input);
				if (tempInt > max) {
					max = tempInt;
				}
				if (tempInt < min) {
					min = tempInt;
				}
			}
			//System.out.println(j + "\t" + max + "\t" + min);
			featureMax.put(j,max);
			featureMin.put(j,min);
		}
		
	}
	
	String getPercentage(double a, int tempnum){
		double temp = (a - featureMin.get(tempnum))/(featureMax.get(tempnum) - featureMin.get(tempnum));
		temp = Math.round(temp * 100);
		temp = temp/100;
		return String.valueOf(temp);	
	}

}
