package com.ontariotechu.sofe3980U;

import com.opencsv.CSVReader;
import java.io.FileReader;

public class App 
{

    private static final double EPSILON = 1e-10;

    // each model has results stored for all metrics
    static class Result 
    {
        String name;
        double mse;
        double mae;
        double mare;

        Result(String name, double mse, double mae, double mare) {
            this.name = name;
            this.mse = mse;
            this.mae = mae;
            this.mare = mare;
        }
    }

    public static void main(String[] args) 
    {
        try 
        {
            Result r1 = evaluate("model_1.csv");
            Result r2 = evaluate("model_2.csv");
            Result r3 = evaluate("model_3.csv");

            print(r1);
            print(r2);
            print(r3);

            System.out.println("According to MSE, The best model is " + bestMSE(r1, r2, r3).name);
            System.out.println("According to MAE, The best model is " + bestMAE(r1, r2, r3).name);
            System.out.println("According to MARE, The best model is " + bestMARE(r1, r2, r3).name);

        } catch (Exception e) 
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //  CSV file evaluation
    public static Result evaluate(String file) throws Exception 
{

        CSVReader reader = new CSVReader(new FileReader(file));
        String[] row;

        reader.readNext(); // skip header

        double sumSq = 0;
        double sumAbs = 0;
        double sumRel = 0;
        int n = 0;

        while ((row = reader.readNext()) != null) 
        {
            double actual = Double.parseDouble(row[0]);
            double predicted = Double.parseDouble(row[1]);

            double error = actual - predicted;

            sumSq += error * error;
            sumAbs += Math.abs(error);
            sumRel += Math.abs(error) / (Math.abs(actual) + EPSILON);

            n++;
        }

        reader.close();

        double mse = sumSq / n;
        double mae = sumAbs / n;
        double mare = sumRel / n;

        return new Result(file, mse, mae, mare);
    }

    // Print line by line results for one model
    public static void print(Result r) 
    {
        System.out.println("for " + r.name + ":");
        System.out.println("MSE  = " + r.mse);
        System.out.println("MAE  = " + r.mae);
        System.out.println("MARE = " + r.mare);
        System.out.println();
    }

    // Find best models
    public static Result bestMSE(Result... list) 
    {
        Result best = list[0];
        for (Result r : list)
            if (r.mse < best.mse) best = r;
        return best;
    }

    public static Result bestMAE(Result... list) 
    {
        Result best = list[0];
        for (Result r : list)
            if (r.mae < best.mae) best = r;
        return best;
    }

    public static Result bestMARE(Result... list) 
    {
        Result best = list[0];
        for (Result r : list)
            if (r.mare < best.mare) best = r;
        return best;
    }
}