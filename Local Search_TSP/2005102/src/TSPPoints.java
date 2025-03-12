import java.io.*;
import java.util.*;

class Point
{
    int id;
    double x;
    double y;

    public Point(int id,double x,double y)
    {
        this.id=id;
        this.x=x;
        this.y=y;
    }
}


class Edge implements Comparable<Edge>
{
    Point p1;
    Point p2;
    double weight;

    public Edge(Point p1,Point p2) {
        this.p1=p1;
        this.p2=p2;
        this.weight=Math.sqrt(Math.pow(p1.x-p2.x,2)+Math.pow(p1.y-p2.y,2));
    }

    @Override
    public int compareTo(Edge secondEdge)
    {
        return Double.compare(this.weight,secondEdge.weight);
    }
}

class DisjointSetUnion {
    private final Map<Point,Point>parentNode=new HashMap<>();
    private final Map<Point,Integer>size=new HashMap<>();

    public void makeSet(Point p){
        parentNode.put(p,p);//Setting p as its own parent
        size.put(p,1);// Each set initially has size 1
    }

    public Point find(Point p){
        if(parentNode.get(p)!=p){
            parentNode.put(p,find(parentNode.get(p)));//Recursively finding the root of p's parent
        }
        return parentNode.get(p);
    }

    public void union(Point p1,Point p2) {
        Point root1=find(p1);
        Point root2=find(p2);

        if(root1!=root2){
            // Union by size
            int size1=size.get(root1);
            int size2=size.get(root2);

            if(size1<size2){
                parentNode.put(root1,root2);
                size.put(root2,size1+size2);
            }
            else{
                parentNode.put(root2,root1);
                size.put(root1,size1+size2);
            }
        }
    }
}

public class TSPPoints {
    public static List<Point>readTSPFile(String filename) throws IOException {
        List<Point>points=new ArrayList<>();
        BufferedReader br=new BufferedReader(new FileReader(filename));
        String line;
        boolean startReadingNodes=false;

        while((line=br.readLine())!=null) {
            line=line.trim();
            if(line.isEmpty()){
                continue;
            }
            if(line.equals("NODE_COORD_SECTION")){
                startReadingNodes=true;
                continue;
            }
            if(startReadingNodes){
                if(line.equals("EOF")) break;
                String[] parts=line.split("\\s+");
                if(parts.length==3){
                    try{
                        int id=Integer.parseInt(parts[0]);
                        double x=Double.parseDouble(parts[1]);
                        double y=Double.parseDouble(parts[2]);
                        points.add(new Point(id,x,y));
                    }
                    catch(NumberFormatException e){
                        System.err.println("Invalid number format in line: "+line);
                    }
                }
            }
        }
        br.close();
        return points;
    }

    public static double calculateDistance(Point p1,Point p2) {
        double dx=p1.x-p2.x;
        double dy=p1.y-p2.y;
        return Math.sqrt(dx*dx+dy*dy);
    }

    public static double calculateTourCost(List<Point> tour) {
        double cost=0;
        for(int i=0;i<tour.size()-1;i++)
        {
            cost+=calculateDistance(tour.get(i),tour.get(i+1));
        }
        //Complete the cycle
        cost+=calculateDistance(tour.get(tour.size()-1),tour.get(0));
        return cost;
    }


    public static void main(String[] args) {
        String folderPath=System.getProperty("user.dir")+"\\TSP_assignment_task_benchmark_data";
        File folder=new File(folderPath);
        try(FileWriter csvWriter=new FileWriter("TSP_Final_Results.csv"))
        {
            csvWriter.append("Filename,Construction Method,Initial Cost,Perturbative Method,Optimized Cost,Cost Reduced\n");
            if(folder.exists() && folder.isDirectory())
            {
                File[] files=folder.listFiles();
                if (files != null)
                {
                    for (File file : files)
                    {
                        if (file.isFile())
                        {
                            try
                            {
                                List<Point>pointList=readTSPFile(file.getAbsolutePath());
                                String filename=file.getName();
                                //if(!filename.equalsIgnoreCase("st70.tsp"))continue;
                                System.out.println("Currently reading:"+filename+";It contains total "+pointList.size()+" points");

                                //Nearest Neighbour Heuristic
                                List<Point>nearestNeighbourTour=NearestNeighbour.nearestNeighbour(pointList);
                                double nearestNeighbourCost=calculateTourCost(nearestNeighbourTour);

                                //TwoOpt
                                List<Point>optimizedTour=TwoOpt.twoOpt(nearestNeighbourTour);
                                double optimizedCost=calculateTourCost(optimizedTour);
                                double delta=nearestNeighbourCost-optimizedCost;

                                csvWriter.append(String.format("%s,%s,%.6f,%s,%.6f,%.6f\n",
                                        filename,"Nearest Neighbour Insertion",
                                        nearestNeighbourCost,"2-Opt",optimizedCost,delta));

                                //Node Shift
                                nearestNeighbourTour=NearestNeighbour.nearestNeighbour(pointList);
                                nearestNeighbourTour=NodeShift.nodeShiftOptimization(nearestNeighbourTour);
                                optimizedCost=calculateTourCost(nearestNeighbourTour);
                                delta=nearestNeighbourCost-optimizedCost;

                                csvWriter.append(String.format("%s,%s,%.6f,%s,%.6f,%.6f\n",
                                        filename,"Nearest Neighbour Insertion",
                                        nearestNeighbourCost,"Node Shift",optimizedCost,delta));

                                //Node Swap
                                nearestNeighbourTour=NearestNeighbour.nearestNeighbour(pointList);
                                NodeSwap.nodeSwapOptimization(nearestNeighbourTour);
                                optimizedCost=calculateTourCost(nearestNeighbourTour);
                                delta=nearestNeighbourCost-optimizedCost;

                                csvWriter.append(String.format("%s,%s,%.6f,%s,%.6f,%.6f\n",
                                        filename,"Nearest Neighbour Insertion",
                                        nearestNeighbourCost,"Node Swap",optimizedCost,delta));

                                //Cheapest Insertion
                                List<Point>cheapestInsertionTour=CheapestInsertion.cheapestInsertion(pointList);
                                double cheapestInsertionCost=calculateTourCost(cheapestInsertionTour);

                                //TwoOpt
                                optimizedTour=TwoOpt.twoOpt(cheapestInsertionTour);
                                optimizedCost=calculateTourCost(optimizedTour);
                                delta=cheapestInsertionCost-optimizedCost;

                                csvWriter.append(String.format("%s,%s,%.6f,%s,%.6f,%.6f\n",
                                        filename,"Cheapest Insertion",
                                        cheapestInsertionCost,"2-Opt",optimizedCost,delta));



                                //Node Shift
                                cheapestInsertionTour=CheapestInsertion.cheapestInsertion(pointList);
                                cheapestInsertionTour=NodeShift.nodeShiftOptimization(cheapestInsertionTour);
                                optimizedCost=calculateTourCost(cheapestInsertionTour);
                                delta=cheapestInsertionCost-optimizedCost;

                                csvWriter.append(String.format("%s,%s,%.6f,%s,%.6f,%.6f\n",
                                        filename,"Cheapest Insertion",
                                        cheapestInsertionCost,"Node Shift",optimizedCost,delta));

                                //Node Swap
                                cheapestInsertionTour=CheapestInsertion.cheapestInsertion(pointList);
                                NodeSwap.nodeSwapOptimization(cheapestInsertionTour);
                                optimizedCost=calculateTourCost(cheapestInsertionTour);
                                delta=cheapestInsertionCost-optimizedCost;

                                csvWriter.append(String.format("%s,%s,%.6f,%s,%.6f,%.6f\n",
                                        filename,"Cheapest Insertion",
                                        cheapestInsertionCost,"Node Swap",optimizedCost,delta));

                                //Greedy
                                List<Point>greedyTour=GreedyHeuristics.greedyHeuristicWithDSU(pointList);
                                double greedyCost=calculateTourCost(greedyTour);

                                //TwoOpt
                                optimizedTour=TwoOpt.twoOpt(greedyTour);
                                optimizedCost=calculateTourCost(optimizedTour);
                                delta=greedyCost-optimizedCost;

                                csvWriter.append(String.format("%s,%s,%.6f,%s,%.6f,%.6f\n",
                                        filename,"Greedy",
                                        greedyCost,"2-Opt",optimizedCost,delta));


                                //Node Shift
                                greedyTour=GreedyHeuristics.greedyHeuristicWithDSU(pointList);
                                greedyTour=NodeShift.nodeShiftOptimization(greedyTour);
                                optimizedCost=calculateTourCost(greedyTour);
                                delta=greedyCost-optimizedCost;

                                csvWriter.append(String.format("%s,%s,%.6f,%s,%.6f,%.6f\n",
                                        filename,"Greedy",
                                        greedyCost,"Node Shift",optimizedCost,delta));


                                //Node Swap
                                greedyTour=GreedyHeuristics.greedyHeuristicWithDSU(pointList);
                                NodeSwap.nodeSwapOptimization(greedyTour);
                                optimizedCost=calculateTourCost(greedyTour);
                                delta=greedyCost-optimizedCost;

                                csvWriter.append(String.format("%s,%s,%.6f,%s,%.6f,%.6f\n",
                                        filename,"Greedy",
                                        greedyCost,"Node Swap",optimizedCost,delta));

                            }
                            catch (IOException e)
                            {
                                System.err.println("Error reading the TSP file: " + e.getMessage());
                            }
                        }
                    }
                }
                else
                {
                    System.out.println("The folder is empty or an error occurred.");
                }

            }
            else
            {
                System.out.println("The folder path is invalid.");
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}


