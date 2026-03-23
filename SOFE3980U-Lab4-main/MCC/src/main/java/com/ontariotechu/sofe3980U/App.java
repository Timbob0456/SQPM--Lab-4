package com.ontariotechu.sofe3980U;

import com.opencsv.CSVReader;
import java.io.FileReader;

public class App 
{

    private static final double EPSILON = 1e-10;

    public static void main(String[] args) 
	{
        try 
		{
            evaluate("model.csv");
        } catch (Exception e) 
		{
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void evaluate(String file) throws Exception 
	{
        CSVReader reader = new CSVReader(new FileReader(file));
        String[] row;

        reader.readNext(); // skip header

        double ceSum = 0.0;
        int[][] matrix = new int[5][5];
        int n = 0;

        while ((row = reader.readNext()) != null) 
			{
            int actual = Integer.parseInt(row[0]); // 1 to 5

            double[] probs = new double[5];
            for (int i = 0; i < 5; i++) 
			{
                probs[i] = Double.parseDouble(row[i + 1]);
            }

            double trueProb = Math.max(EPSILON, probs[actual - 1]);
            ceSum += -Math.log(trueProb);

            int predicted = 0;
            for (int i = 1; i < 5; i++) {
                if (probs[i] > probs[predicted]) 
				{
                    predicted = i;
                }
            }

            matrix[predicted][actual - 1]++;
            n++;
        }

        reader.close();

        double ce = ceSum / n;

        System.out.println("CE = " + ce);
        System.out.println("Confusion matrix");
        System.out.println("          y=1   y=2   y=3   y=4   y=5");
        for (int i = 0; i < 5; i++) 
		{
            System.out.print("y^=" + (i + 1) + "      ");
            for (int j = 0; j < 5; j++) 
			{
                System.out.print(matrix[i][j]);
                if (j < 4) System.out.print("     ");
            }
            System.out.println();
        }
    }
}
