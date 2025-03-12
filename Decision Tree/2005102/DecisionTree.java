import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class DecisionTree
{
    Node rootNode;
    String criteria;//Selects either "Information-Gain" or "Gini-Impure"
    List<String>classList;
    List<String>attributeList;
    List<List<String>>dataSetValue;
    boolean isRandom;//Boolean flag indicating if the tree should randomly select from the top three attributes based on gain
    HashMap<String,List<String>>attributeValueMap;//Maps the attribute names and their possible values
    public DecisionTree(HashMap<String,List<String>>attributeValueMap,List<String>classList,List<List<String>>dataSetValue,String criteria)
    {
        this(attributeValueMap,classList,dataSetValue,criteria,false);
    }
    public DecisionTree(HashMap<String,List<String>>attributeValueMap,List<String>classList,List<List<String>>dataSetValue,String criteria,boolean isRandom)
    {
        this.attributeValueMap=attributeValueMap;
        this.attributeList=new ArrayList<>(attributeValueMap.keySet());//Contains all the attribute names
        this.classList=classList;
        this.criteria=criteria;
        this.dataSetValue=dataSetValue;
        this.isRandom=isRandom;
        this.rootNode=makeDecisionTree(dataSetValue,attributeList,null);
    }

    int attributeIndex(String attributeName)
    {
        switch(attributeName)
        {
            case "buying":
                return 0;
            case "maint":
                return 1;
            case "doors":
                return 2;
            case "persons":
                return 3;
            case "lug_boot":
                return 4;
            case "safety":
                return 5;
            default:
                return -1;
        }
    }


    //Returns number of rows per class...[ClassName1]:X1,[ClassName2]:X2..
    HashMap<String,Integer>classValueCount(List<List<String>>dataSetValue)
    {
        HashMap<String,Integer>classWiseCount=new HashMap<>();
        for(List<String>dataSetRowVal:dataSetValue)
        {
            int rowSize=dataSetRowVal.size();
            //To retrieve the last column value
            String className=dataSetRowVal.get(rowSize-1);
            if(classWiseCount.containsKey(className)) {
                classWiseCount.put(className,classWiseCount.get(className)+1);
            }
            else {
                classWiseCount.put(className,1);
            }
        }
        return classWiseCount;
    }


    double calculateEntireSetIGValue(List<List<String>>dataSetValue)
    {
        double informationGain=0;
        int rowSize=dataSetValue.size();
        HashMap<String,Integer>classDetails=classValueCount(dataSetValue);
        for (String className:classDetails.keySet()) {
            int countTotalRows=classDetails.get(className);
            double ratio=(double)countTotalRows/rowSize;
            informationGain+=-ratio*Math.log(ratio)/Math.log(2);
        }
        return informationGain;
    }


    double calculateEntireSetGiniImpureValue(List<List<String>>dataSetValue)
    {
        double giniGain=1;
        int rowSize=dataSetValue.size();
        HashMap<String,Integer>classDetails=classValueCount(dataSetValue);
        for (String className:classDetails.keySet()) {
            int countTotalRows=classDetails.get(className);
            double ratio=(double)countTotalRows/rowSize;
            giniGain-=ratio*ratio;
        }
        return giniGain;
    }


    List<List<String>>selectsRowOfSameValuePerCol(List<List<String>>dataSetValue,int attrIndex,String attributeVal)
    {
        List<List<String>>subDataSet=new ArrayList<>();
        for(List<String>rowData:dataSetValue)
        {
            if(rowData.get(attrIndex).equalsIgnoreCase(attributeVal))//Fetching attribute name and matches the value with attributeVal
                subDataSet.add(rowData);
        }
        return subDataSet;
    }


    double calculateAttributeIGGain(List<List<String>>dataSetValue,String attributeName,double entireSetVal)
    {
        double attrgain=entireSetVal;
        int attributeIndex=attributeIndex(attributeName);
        for(String attributeVal:attributeValueMap.get(attributeName))
        {
            List<List<String>>subDataSet=selectsRowOfSameValuePerCol(dataSetValue,attributeIndex,attributeVal);
            //double measure=criteria.equals("IG")?calculateEntireSetIGValue(subDataSet):calculateEntireSetGiniImpureValue(subDataSet);//For each branch value of a column..which is how many unique values are present in a column
            double measure=calculateEntireSetIGValue(subDataSet);
            attrgain-=measure*subDataSet.size()/dataSetValue.size();
        }
        return attrgain;
    }


    Node predictClassNode(List<String>rowData)
    {
        Node node=rootNode;
        while(!node.isLeaf)
        {
            String attributeVal=rowData.get(attributeIndex(node.attributeName));
            node=node.childNodeList.get(attributeVal);//moves to the next node in the tree
        }
        return node;
    }


    double calculateTestDataSet(List<List<String>>testDataSetValue)
    {
        int lastindex=-1,correctAns=0;//to keep track of the number of correct predictions made by the decision tree
        for(List<String>rowData:testDataSetValue)
        {
            lastindex=rowData.size()-1;
            String predictedClassName=predictClassNode(rowData).className;
            if(predictedClassName.equalsIgnoreCase(rowData.get(lastindex)))
                correctAns++;
        }
        return (double)correctAns/testDataSetValue.size();
    }

    Node pluralityNode(List<List<String>>dataSetValue)
    {
        HashMap<String,Integer>classDetails=classValueCount(dataSetValue);//stores the count of each class. The keys are class labels, and the values are the number of occurrences of that label
        String classWithMaxFreq="";//Initializes a variable to keep track of the class label with the highest count
        int maxFreq=0;
        for (String className:classDetails.keySet())
        {
            int count=classDetails.get(className);
            if(count>=maxFreq)
            {
                maxFreq=count;
                classWithMaxFreq=className;
            }
        }
        return new Node(classWithMaxFreq,true);

    }


    String bestAttributeSelection(List<List<String>>dataSetValue,List<String>attributeList)
    {
        //double initialMeasure=criteria.equals("IG")?calculateEntireSetIGValue(dataSetValue):calculateEntireSetGiniImpureValue(dataSetValue);
        List<AttributeGain>attributeGains=new ArrayList<>();

        for(String attribute:attributeList)
        {
            double measure;
            if(criteria.equals("IG"))
            {
                // Calculate gain if using Information Gain
                double initialMeasure=calculateEntireSetIGValue(dataSetValue);
                measure=calculateAttributeIGGain(dataSetValue,attribute,initialMeasure);
            }
            else
            {
                // Calculate weighted Gini impurity if using Gini impurity
                measure=calculateWeightedGiniForAttribute(dataSetValue,attribute);
            }
            //System.out.println("Gain asche-"+measure);
            attributeGains.add(new AttributeGain(attribute,measure));
        }

        if(criteria.equals("IG"))
        {
            //Sort in descending order of gain for Information Gain
            attributeGains.sort((a, b) -> Double.compare(b.gain, a.gain));
        }
        else
        {
            //Sort in ascending order of Gini impurity to minimize impurity
            //System.out.println("hello");
            attributeGains.sort((a, b) -> Double.compare(a.gain, b.gain));
        }

        if(isRandom && attributeGains.size()>=3)
        {
            int randomIndex=new Random().nextInt(3);
            return attributeGains.get(randomIndex).attribute;
        }

        return attributeGains.get(0).attribute;
    }

    double calculateWeightedGiniForAttribute(List<List<String>>dataSetValue, String attribute) {
        double weightedGini=0;
        int attrIndex=attributeIndex(attribute);
        for(String attributeValue:attributeValueMap.get(attribute))
        {
            List<List<String>>subset=selectsRowOfSameValuePerCol(dataSetValue,attrIndex,attributeValue);
            double gini=calculateEntireSetGiniImpureValue(subset);
            weightedGini+=gini*subset.size()/dataSetValue.size();
        }
        return weightedGini;
    }


    void saveTreeToFile(String filename)
    {
        try(FileWriter writer = new FileWriter(filename))
        {
            saveTreeRecursive(writer,rootNode,0);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


    void saveTreeRecursive(FileWriter writer,Node node,int depth) throws IOException
    {
        for(int i=0;i<depth;i++)
        {
            writer.write("|	");
        }
        if(node.isLeaf)
        {
            writer.write("Class: "+node.className+"\n");
        }
        else
        {
            writer.write(node.attributeName+"\n");
            for (String key:node.childNodeList.keySet())
            {
                for(int i=0;i<depth;i++)
                {
                    writer.write("|	");
                }
                writer.write("-> "+key+"\n");
                saveTreeRecursive(writer,node.childNodeList.get(key),depth+1);
            }

        }
    }

    Node makeDecisionTree(List<List<String>>currDataSetValue,List<String>attributeList,List<List<String>>parentDataSetValue)
    {
        if(currDataSetValue.isEmpty())
        {
            return pluralityNode(parentDataSetValue);//Suppose splitting based on attribute safety,and no example has safety="medium".In this scenario,the subset becomes empty
        }
        else if(classValueCount(currDataSetValue).size()==1)//all the dataSetVal belong to one class
        {
            int lastIndex=currDataSetValue.get(0).size()-1;
            Node newNode=new Node(currDataSetValue.get(0).get(lastIndex),true);
            return newNode;
        }
        else if(attributeList.isEmpty())
        {
            return pluralityNode(currDataSetValue);
        }
        else
        {
            String selectedBestAttribute=bestAttributeSelection(currDataSetValue,attributeList);
            Node root=new Node(selectedBestAttribute,false);//Root Node Creation
            int bestAttributeIndex=attributeIndex(selectedBestAttribute);
            for(String attrVal:attributeValueMap.get(selectedBestAttribute))
            {
                List<List<String>>subDataSet=selectsRowOfSameValuePerCol(currDataSetValue,bestAttributeIndex,attrVal);
                List<String>modifiedAttributeList=new ArrayList<>(attributeList);//Create a new list of attributes by removing the selectedBestAttribute since it has already been used for splitting
                modifiedAttributeList.remove(selectedBestAttribute);
                root.addChildNode(attrVal,makeDecisionTree(subDataSet,modifiedAttributeList,currDataSetValue));
            }
            return root;
        }
    }
}
