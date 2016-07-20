package kr.ac.konkuk.islab;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class RFThread implements Runnable {
	private int i;
	private int j;
	private String seq;
	private ArrayList<String> a;

	public RFThread(int i, int j, ArrayList<String> a) {
		this.i = i;
		this.j = j;
		this.a = a;
	
		seq = this.i +"/" + this.j;
	}

	public void run() {

		System.out.println(seq + " RF thread start.");
		try {
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
			
			a.add(seq + "|" + eval.pctCorrect());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(seq + " RF thread end.");
	}
}