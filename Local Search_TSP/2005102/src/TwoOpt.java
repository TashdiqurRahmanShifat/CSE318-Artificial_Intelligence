import java.util.Collections;
import java.util.List;

public class TwoOpt {
    public static List<Point>twoOpt(List<Point>tour) {
        boolean locallyOptimal=false;

        while(!locallyOptimal){
            locallyOptimal=true;//will be set to false if a better 2-opt move is found.
            double bestGain=0;
            int bestI=-1,bestJ=-1;

            // Look for the best 2-opt move
            //Ensures there are at least two remaining edges (i and j) for a valid swap
            for(int i=0;i<tour.size()-2;i++){//This for loop iterates over each edge (X1,X2) in the tour to avoid overlapping(self loop may occur)
                Point X1=tour.get(i);
                Point X2=tour.get((i+1)%tour.size());

                int innerLoopCount=(i==0)?tour.size()-2:tour.size()-1;//if i==0 ,the edge between last node and first node should not be calculated,if i!=0,then we can use it

                for(int j=i+2;j<=innerLoopCount;j++){
                    Point Y1=tour.get(j);
                    Point Y2=tour.get((j+1)%tour.size());

                    // Calculate the expected gain from the 2-opt move
                    double expectedGain=calculate2OptGain(X1,X2,Y1,Y2);

                    // Check if this is the best move found so far
                    if(expectedGain>bestGain){
                        bestGain=expectedGain;
                        bestI=i;
                        bestJ=j;
                        locallyOptimal=false;
                    }
                }
            }

            // Apply the best move if an improvement was found
            if(!locallyOptimal && bestI!=-1 && bestJ!=-1){
                apply2OptMove(tour,bestI,bestJ);
            }
        }
        return tour;
    }

    private static double calculate2OptGain(Point X1,Point X2,Point Y1,Point Y2) {
        double originalDistance=TSPPoints.calculateDistance(X1,X2)+TSPPoints.calculateDistance(Y1,Y2);
        double newDistance=TSPPoints.calculateDistance(X1,Y1)+TSPPoints.calculateDistance(X2,Y2);
        return originalDistance-newDistance;
    }

    private static void apply2OptMove(List<Point>tour,int i,int j){
        //Reverse the segment from i+1 to j to complete the 2-opt swap
        Collections.reverse(tour.subList(i+1,j+1));
    }
}
