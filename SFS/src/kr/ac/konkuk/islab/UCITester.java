package kr.ac.konkuk.islab;

import java.io.BufferedReader;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class UCITester {

	public static void main(String[] args) throws Exception {
		String tempString = "";
		long start = System.currentTimeMillis();
		
		
		FileIO fio = new FileIO();
		BufferedReader reader = fio.ReadFile("uci_data/vehicle.arff");
		Instances dataset = new Instances(reader);


		dataset.setClassIndex(dataset.numAttributes() - 1);


		RandomForest classifier = new RandomForest();

		Evaluation eval = new Evaluation(dataset);
		eval.crossValidateModel(classifier, dataset, 10, new Random(1));
		
		System.out.println("정확도 : " + eval.pctCorrect());
		
		long end = System.currentTimeMillis();
		tempString = "실행 시간: " + (end - start) / 1000.0 + " sec\r\n";
		System.out.print(tempString);
	}

}
