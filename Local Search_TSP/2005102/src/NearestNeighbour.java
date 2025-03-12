import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NearestNeighbour {
    public static List<Point> nearestNeighbour(List<Point>points)
    {
        List<Point>tour=new ArrayList<>();
        Set<Point>inTour=new HashSet<>();
        Point current=points.get(0);
        tour.add(current);
        inTour.add(current);

        while(tour.size()<points.size())
        {
            Point nearest=null;
            double minimumDistance=Double.MAX_VALUE;
            for(Point p:points){
                if(!inTour.contains(p)){
                    double distance=TSPPoints.calculateDistance(current,p);
                    if(distance<minimumDistance){
                        minimumDistance=distance;
                        nearest=p;
                    }
                }
            }
            tour.add(nearest);
            inTour.add(nearest);
            current=nearest;
        }
        return tour;
    }
}
