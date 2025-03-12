import java.util.*;

public class GreedyHeuristics {
    public static List<Point>greedyHeuristicWithDSU(List<Point>points)
    {
        List<Edge>edges=new ArrayList<>();
        for(int i=0;i<points.size();i++){
            for(int j=i+1;j<points.size();j++){
                edges.add(new Edge(points.get(i),points.get(j)));
            }
        }
        Collections.sort(edges);

        DisjointSetUnion dsu=new DisjointSetUnion();
        for(Point p:points){
            dsu.makeSet(p);
        }

        Map<Point,Integer>degree=new HashMap<>();
        for(Point p:points){
            degree.put(p,0);
        }

        List<Edge>tourEdges=new ArrayList<>();
        for(Edge edge:edges){
            if(dsu.find(edge.p1)!=dsu.find(edge.p2) && degree.get(edge.p1)<2 && degree.get(edge.p2)<2){
                dsu.union(edge.p1,edge.p2);
                tourEdges.add(edge);
                degree.put(edge.p1,degree.get(edge.p1)+1);
                degree.put(edge.p2,degree.get(edge.p2)+1);

                if(tourEdges.size()==points.size()-1){
                    break;
                }
            }
        }

        // Identify endpoints with degree 1 for the final edge
        Point endpoint1=null,endpoint2=null;
        for(Point p:points){
            if(degree.get(p)==1){
                if(endpoint1==null){
                    endpoint1=p;
                }
                else{
                    endpoint2=p;
                    break;
                }
            }
        }

        if(endpoint1!=null && endpoint2!=null){
            tourEdges.add(new Edge(endpoint1,endpoint2));//Final edge to complete the cycle
        }
        else{
            throw new IllegalStateException("Unable to find two endpoints to complete the tour.");
        }

        //Build the complete tour starting from the initial point
        List<Point>tour=buildTourFromEdges(tourEdges,points.get(0));

        //Verify that we have a complete tour
        if(tour.size()!=points.size()){
            throw new IllegalStateException("Incomplete tour generated.");
        }
        return tour;
    }


    private static List<Point>buildTourFromEdges(List<Edge> tourEdges,Point start){
        List<Point>tour=new ArrayList<>();
        Map<Point,List<Point>>adjacencyList=new HashMap<>();

        //Build adjacency list for quick traversal
        for(Edge edge:tourEdges){
            adjacencyList.computeIfAbsent(edge.p1,k->new ArrayList<>()).add(edge.p2);//check if edge is already in the map,if not add a new entry and add other point
            adjacencyList.computeIfAbsent(edge.p2,k->new ArrayList<>()).add(edge.p1);
        }

        Set<Point>visited=new HashSet<>();
        Point current=start;

        // Traverse the cycle
        while(current!=null && !visited.contains(current)){
            tour.add(current);
            visited.add(current);
            List<Point>neighbors=adjacencyList.get(current);

            // Move to the next unvisited neighbor
            Point next=null;
            for(Point neighbor:neighbors){
                if(!visited.contains(neighbor)){
                    next=neighbor;
                    break;
                }
            }
            current=next;
        }
        return tour;
    }
}
