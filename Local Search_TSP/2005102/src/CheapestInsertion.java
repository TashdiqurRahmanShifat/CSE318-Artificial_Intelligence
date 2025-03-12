import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheapestInsertion {
    public static List<Point> cheapestInsertion(List<Point>points)
    {
        List<Point>tour=new ArrayList<>();
        Set<Point>inTour=new HashSet<>();//Visited Array

        Point first=points.get(0);
        Point second=null;
        double minimumDistance=Double.MAX_VALUE;
        for(int i=1;i<points.size();i++)
        {
            double distance=TSPPoints.calculateDistance(first,points.get(i));
            if(distance<minimumDistance)
            {
                minimumDistance=distance;
                second=points.get(i);
            }
        }

        //System.out.println("Selected "+second.id);
        tour.add(first);
        tour.add(second);
        inTour.add(first);
        inTour.add(second);

        //Chepest Insertion Heuristic
        while(tour.size()<points.size())
        {
            Point nearest=null;
            minimumDistance=Double.MAX_VALUE;

            for(Point p:points)
            {
                if(!inTour.contains(p)) {
                    for(int i=0;i<tour.size();i++)
                    {
                        double difference=TSPPoints.calculateDistance(tour.get(i),p)+TSPPoints.calculateDistance(p,tour.get((i+1)%tour.size()))-
                                TSPPoints.calculateDistance(tour.get(i),tour.get((i+1)%tour.size()));
                        //System.out.println(tour.get(i).id+" diff "+difference);
                        if(difference<minimumDistance){
                            minimumDistance=difference;
                            nearest=p;
                        }
                    }
                }
            }


            //Insert the nearest point at the optimal position
            minimumDistance=Double.MAX_VALUE;
            int insertPos=0;
            for(int i=0;i<tour.size();i++)
            {
                double distance=TSPPoints.calculateDistance(tour.get(i),nearest)+TSPPoints.calculateDistance(nearest,tour.get((i+1)%tour.size()))-
                        TSPPoints.calculateDistance(tour.get(i),tour.get((i+1)%tour.size()));
                if(distance<minimumDistance) {
                    minimumDistance=distance;
                    insertPos=i+1;
                }
            }

            //Insert nearest point in the optimal position
            tour.add(insertPos,nearest);
            inTour.add(nearest);
        }

        return tour;
    }
}
