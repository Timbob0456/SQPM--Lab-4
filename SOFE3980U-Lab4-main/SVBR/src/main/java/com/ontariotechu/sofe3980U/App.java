package com.ontariotechu.sofe3980U;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.ArrayList;

public class App 
{

    private static final double EPSILON = 1e-10;

    static class Result 
	{
        String name;
        double bce;
        int tp, tn, fp, fn;
        double accuracy, precision, recall, f1, auc;

        Result(String name, double bce, int tp, int tn, int fp, int fn,
               double accuracy, double precision, double recall, double f1, double auc) {
            this.name = name;
            this.bce = bce;
            this.tp = tp;
            this.tn = tn;
            this.fp = fp;
            this.fn = fn;
            this.accuracy = accuracy;
            this.precision = precision;
            this.recall = recall;
            this.f1 = f1;
            this.auc = auc;
        }
    }

    public static void main(String[] args) 
	{
        try {
            Result r1 = evaluate("model_1.csv");
            Result r2 = evaluate("model_2.csv");
            Result r3 = evaluate("model_3.csv");

            print(r1);
            print(r2);
            print(r3);

            System.out.println("According to BCE, The best model is " + bestBCE(r1, r2, r3).name);
            System.out.println("According to Accuracy, The best model is " + bestAccuracy(r1, r2, r3).name);
            System.out.println("According to Precision, The best model is " + bestPrecision(r1, r2, r3).name);
            System.out.println("According to Recall, The best model is " + bestRecall(r1, r2, r3).name);
            System.out.println("According to F1, The best model is " + bestF1(r1, r2, r3).name);
            System.out.println("According to AUC ROC, The best model is " + bestAUC(r1, r2, r3).name);

        } catch (Exception e) 
		{
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static Result evaluate(String file) throws Exception 
	{
        CSVReader reader = new CSVReader(new FileReader(file));
        String[] row;

        reader.readNext(); // skip header

        ArrayList<Integer> actual = new ArrayList<>();
        ArrayList<Double> predicted = new ArrayList<>();

        while ((row = reader.readNext()) != null) 
		{
            actual.add(Integer.parseInt(row[0]));
            predicted.add(Double.parseDouble(row[1]));
        }
        reader.close();

        int n = actual.size();

        // BCE
        double bceSum = 0.0;
        for (int i = 0; i < n; i++) 
		{
            int y = actual.get(i);
            double p = predicted.get(i);
            p = Math.max(EPSILON, Math.min(1.0 - EPSILON, p));
            bceSum += y * Math.log(p) + (1 - y) * Math.log(1 - p);
        }
        double bce = -bceSum / n;

        // Confusion matrix at threshold 0.5
        int tp = 0, tn = 0, fp = 0, fn = 0;
        for (int i = 0; i < n; i++) 
		{
            int y = actual.get(i);
            double p = predicted.get(i);
            int yhat = (p >= 0.5) ? 1 : 0;

            if (y == 1 && yhat == 1) tp++;
            else if (y == 0 && yhat == 0) tn++;
            else if (y == 0 && yhat == 1) fp++;
            else if (y == 1 && yhat == 0) fn++;
        }

        double accuracy = (tp + tn) / (double)(tp + tn + fp + fn);
        double precision = (tp + fp == 0) ? 0.0 : tp / (double)(tp + fp);
        double recall = (tp + fn == 0) ? 0.0 : tp / (double)(tp + fn);
        double f1 = (precision + recall == 0) ? 0.0 : 2.0 * precision * recall / (precision + recall);

        // AUC
        int positives = 0, negatives = 0;
        for (int y : actual) 
		{
            if (y == 1) positives++;
            else negatives++;
        }

        double[] fpr = new double[101];
        double[] tpr = new double[101];

        for (int i = 0; i <= 100; i++) 
		{
            double threshold = i / 100.0;
            int rocTP = 0, rocFP = 0;

            for (int j = 0; j < n; j++) 
			{
                int y = actual.get(j);
                double p = predicted.get(j);

                if (p >= threshold) 
				{
                    if (y == 1) rocTP++;
                    else rocFP++;
                }
            }

            tpr[i] = (positives == 0) ? 0.0 : rocTP / (double) positives;
            fpr[i] = (negatives == 0) ? 0.0 : rocFP / (double) negatives;
        }

        double auc = 0.0;
        for (int i = 1; i <= 100; i++) 
		{
            auc += (tpr[i - 1] + tpr[i]) * Math.abs(fpr[i - 1] - fpr[i]) / 2.0;
        }

        return new Result(file, bce, tp, tn, fp, fn, accuracy, precision, recall, f1, auc);
    }

    public static void print(Result r) 
{
        System.out.println("----- " + r.name + " -----");
        System.out.println("BCE = " + r.bce);
        System.out.println("Confusion Matrix");
        System.out.println("          y=1   y=0");
        System.out.println("y^1    " + r.tp + "         " + r.fp);
        System.out.println("y^0    " + r.fn + "         " + r.tn);
        System.out.println("Accuracy  = " + r.accuracy);
        System.out.println("Precision = " + r.precision);
        System.out.println("Recall    = " + r.recall);
        System.out.println("F1 Score  = " + r.f1);
        System.out.println("AUC ROC   = " + r.auc);
        System.out.println();
    }

    public static Result bestBCE(Result... list) 
	{
        Result best = list[0];
        for (Result r : list) if (r.bce < best.bce) best = r;
        return best;
    }

    public static Result bestAccuracy(Result... list) 
	{
        Result best = list[0];
        for (Result r : list) if (r.accuracy > best.accuracy) best = r;
        return best;
    }

    public static Result bestPrecision(Result... list) 
	{
        Result best = list[0];
        for (Result r : list) if (r.precision > best.precision) best = r;
        return best;
    }

    public static Result bestRecall(Result... list) 
	{
        Result best = list[0];
        for (Result r : list) if (r.recall > best.recall) best = r;
        return best;
    }

    public static Result bestF1(Result... list) 
	{
        Result best = list[0];
        for (Result r : list) if (r.f1 > best.f1) best = r;
        return best;
    }

    public static Result bestAUC(Result... list) 
	{
        Result best = list[0];
        for (Result r : list) if (r.auc > best.auc) best = r;
        return best;
    }
}