import java.util.List;

public class NodeShift {

    public static List<Point>nodeShiftOptimization(List<Point>tour){
        boolean locallyOptimal=false;//if further improvements are possible
        final double significantThreshold=1e-6;// Minimum significant gain threshold

        while(!locallyOptimal){
            locallyOptimal=true;

            for(int i=0;i<tour.size();i++){
                Point X0_pred=tour.get((i+tour.size()-1)%tour.size());
                Point X0=tour.get(i);
                Point X0_succ=tour.get((i+1)%tour.size());

                //Searches for a position to place X0 that will reduce the tour length.
                for(int counter=1;counter<tour.size()-1;counter++){//Without -1, the loop would end up revisiting positions that effectively create the same connections as the original position
                    int j=(i+counter)%tour.size();

                    //Avoiding placing X0 next to it initial neighbour as it is negligible
                    if (j==i||j==(i+1)%tour.size()) continue;

                    Point Y1=tour.get(j);
                    Point Y2=tour.get((j+1)%tour.size());

                    // Calculate gain from shifting node X0 between Y1 and Y2
                    double gain=calculateNodeShiftGain(X0_pred,X0,X0_succ,Y1,Y2);

                    // Apply the shift only if the gain is significantly positive
                    if(gain>significantThreshold){
                        //System.out.println("Shifting " + X0.id + " between " + Y1.id + " and " + Y2.id + " with gain: " + gain);

                        // Apply the node shift
                        applyNodeShift(tour,i,j);
                        locallyOptimal=false;
                        break;
                    }
                }
                if(!locallyOptimal) break;//Exit outer loop if shift was applied
            }
        }
        return tour;
    }

    private static double calculateNodeShiftGain(Point X0_pred,Point X0,Point X0_succ,Point Y1,Point Y2){
        double lengthBeforeDeleting=TSPPoints.calculateDistance(X0_pred,X0)+TSPPoints.calculateDistance(X0,X0_succ)+TSPPoints.calculateDistance(Y1,Y2);
        double lengthAfterDeleting=TSPPoints.calculateDistance(X0_pred,X0_succ)+TSPPoints.calculateDistance(Y1,X0)+TSPPoints.calculateDistance(X0,Y2);
        return lengthBeforeDeleting-lengthAfterDeleting;
    }


    private static void applyNodeShift(List<Point>tour,int i,int j){
        Point X0=tour.remove(i);

        if(j>i){
            j--;//As X0 is removed,so if index of j is greater than i,it must be decremented
        }//If j <= i, the removal of X0 does not affect the index j because the indices of elements before X0 remain unchanged
        // Perform shift by placing X0 after Y1 and before Y2
        tour.add(j+1,X0);
    }

}
