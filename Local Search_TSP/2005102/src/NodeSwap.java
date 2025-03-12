import java.util.Collections;
import java.util.List;

public class NodeSwap {

    private static double gainFromNodeSwap(List<Point>tour,int i,int j)
    {
        int n=tour.size();

        Point X0_pred=tour.get((i-1+n)%n);
        Point X0=tour.get(i);
        Point X0_succ=tour.get((i+1)%n);

        Point Y0_pred=tour.get((j-1+n)%n);
        Point Y0=tour.get(j);
        Point Y0_succ=tour.get((j+1)%n);

        double lengthBeforeDeleting,lengthAfterDeleting;

        if(Y0==X0_succ){//||X0_succ==Y0_pred){
            lengthBeforeDeleting=TSPPoints.calculateDistance(X0_pred,X0)+TSPPoints.calculateDistance(Y0,Y0_succ);
            lengthAfterDeleting=TSPPoints.calculateDistance(X0_pred,Y0)+TSPPoints.calculateDistance(X0,Y0_succ);
        }
        else if(X0==Y0_succ){//||Y0_succ==X0_pred){
            lengthBeforeDeleting=TSPPoints.calculateDistance(Y0_pred,Y0)+TSPPoints.calculateDistance(X0,X0_succ);
            lengthAfterDeleting=TSPPoints.calculateDistance(Y0_pred,X0)+TSPPoints.calculateDistance(Y0,X0_succ);
        }
        else{
            lengthBeforeDeleting=TSPPoints.calculateDistance(X0_pred,X0)+TSPPoints.calculateDistance(X0,X0_succ)+
                    TSPPoints.calculateDistance(Y0_pred,Y0)+TSPPoints.calculateDistance(Y0,Y0_succ);
            lengthAfterDeleting=TSPPoints.calculateDistance(X0_pred,Y0)+TSPPoints.calculateDistance(Y0,X0_succ)+
                    TSPPoints.calculateDistance(Y0_pred,X0)+TSPPoints.calculateDistance(X0,Y0_succ);
        }

        return lengthBeforeDeleting-lengthAfterDeleting;
    }


    private static void makeNodeSwapMove(List<Point>tour,int i,int j)
    {
        Collections.swap(tour,i,j);
    }


    public static void nodeSwapOptimization(List<Point>tour)
    {
        boolean locallyOptimal=false;
        int n=tour.size();

        while(!locallyOptimal)
        {
            locallyOptimal=true;

            for(int i=0;i<n;i++)
            {
                for(int j=i+1;j<n;j++)
                {
                    if(gainFromNodeSwap(tour,i,j)>0)
                    {
                        makeNodeSwapMove(tour,i,j);
                        locallyOptimal=false;
                        break;
                    }
                }
                if(!locallyOptimal) break;
            }
        }
    }
}

