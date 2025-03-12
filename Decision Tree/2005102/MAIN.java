import java.io.File;
import java.io.IOException;
import java.util.*;


public class MAIN
{
    public static final int iterationCount=20;
    public static final double splitRatio=0.8;
    public static void printResultsTable(double[][] accuracyResults,int iterationCount)
    {

        System.out.println();
        System.out.println("Decision Tree Results....");
        System.out.println("+------------------------------------------------------+----------------------------------+");
        System.out.printf("| %52s | %-32s |%n", " "," Average accuracy over 20 runs");
        System.out.println("+------------------------------------------------------+----------------------------------+");
        System.out.printf("| %-52s | %-16s | %-12s |%n",
                "Attribute selection strategy", "Information gain", "Gini impurity");
        System.out.println("+------------------------------------------------------+----------------------------------+");

        System.out.printf("| %-52s |     %-8.5f%%    |   %-8.5f%%   |%n",
                "Always select the best attribute",
                (accuracyResults[0][0]/iterationCount)*100,
                (accuracyResults[0][1]/iterationCount)*100);
        //System.out.println("+------------------------------------------------------+----------------------------------+");

        System.out.printf("| %-52s |     %-8.5f%%    |   %-8.5f%%   |%n",
                "Select one randomly from the top three attributes",
                (accuracyResults[1][0]/iterationCount)*100,
                (accuracyResults[1][1]/iterationCount)*100);

        System.out.println("+------------------------------------------------------+----------------------------------+");
    }


    public static void main(String[] args)
    {
        HashMap<String,List<String>>attributeValuePair=new HashMap<>();
        String[] attrNames={"buying","maint","doors","persons","lug_boot","safety"};
        List<List<String>>attrValues=List.of(
                List.of("vhigh","high","med","low"),
                List.of("vhigh","high","med","low"),
                List.of("2","3","4","5more"),
                List.of("2","4","more"),
                List.of("small","med","big"),
                List.of("low","med","high")
        );

        for(int i=0;i<attrNames.length;i++) {
            attributeValuePair.put(attrNames[i],attrValues.get(i));
        }

        List<String>classList=List.of("unacc","acc","good","vgood");



        int totalLine=0;//Number of lines in the file
        List<List<String>>dataSetValue=new ArrayList<>();
        File filename=new File("car.data");

        //Reading the file
        try
        {
            Scanner scn=new Scanner(filename);
            while(scn.hasNextLine())
            {
                String line= scn.nextLine();
                List<String>lineData=Arrays.asList(line.split(","));
                totalLine++;
                dataSetValue.add(lineData);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        double[][] accuracyResults=new double[2][2];

        for(int i=0;i<iterationCount;i++)
        {
            Collections.shuffle(dataSetValue);//Shuffles the dataset to randomize training and testing sets each iteration
            int lastTrainingDataIndex=(int)(totalLine*splitRatio);//Splits the data into training and testing subsets based on splitRatio
            List<List<String>>trainingDataSetValue=dataSetValue.subList(0,lastTrainingDataIndex);
            List<List<String>>testDataSetValue=dataSetValue.subList(lastTrainingDataIndex,totalLine);

            //Decision Tree using Information Gain(best attribute)
            DecisionTree decisionTreeInfoGain=new DecisionTree(attributeValuePair,classList,trainingDataSetValue,"IG");
            double accuracyInfoGain=decisionTreeInfoGain.calculateTestDataSet(testDataSetValue);
            accuracyResults[0][0]+=accuracyInfoGain;
            //System.out.println("Gainnnn : "+accuracyInfoGain);
            //decisionTreeInfoGain.saveTreeToFile("decision_tree_info_gain_best.txt");

            //Decision Tree using Gini Impurity(best attribute)
            DecisionTree decisionTreeGini=new DecisionTree(attributeValuePair,classList,trainingDataSetValue,"gini");
            double accuracyGini=decisionTreeGini.calculateTestDataSet(testDataSetValue);
            accuracyResults[0][1]+=accuracyGini;
            //decisionTreeGini.saveTreeToFile("decision_tree_gini_best.txt");

            //Decision Tree using Information Gain(random top 3 attributes)
            DecisionTree decisionTreeInfoGainRandom=new DecisionTree(attributeValuePair,classList,trainingDataSetValue,"IG",true);
            double accuracyInfoGainRandom=decisionTreeInfoGainRandom.calculateTestDataSet(testDataSetValue);
            accuracyResults[1][0]+=accuracyInfoGainRandom;
            //decisionTreeInfoGainRandom.saveTreeToFile("decision_tree_info_gain_random.txt");

            //Decision Tree using Gini Impurity(random top 3 attributes)
            DecisionTree decisionTreeGiniRandom=new DecisionTree(attributeValuePair,classList,trainingDataSetValue,"gini",true);
            double accuracyGiniRandom=decisionTreeGiniRandom.calculateTestDataSet(testDataSetValue);
            accuracyResults[1][1]+=accuracyGiniRandom;
            //decisionTreeGiniRandom.saveTreeToFile("decision_tree_gini_random.txt");
        }
        printResultsTable(accuracyResults,iterationCount);

    }
}
