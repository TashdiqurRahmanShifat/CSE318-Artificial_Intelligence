import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

class AStar
{
    private boolean isValid(int direction,int starRow,int starCol,int upperRow,int upperCol)
    {
        if(direction==0)
        {
            return starCol>0;
        }
        else if(direction==1)
        {
            return starCol<upperCol-1;
        }
        else if(direction==2)
        {
            return starRow>0;
        }
        else if(direction==3)
        {
            return starRow<upperRow-1;
        }
        return false;
    }
    public void aStarSolver(Node initial,int choice)
    {
        PriorityQueue<Node>pq=new PriorityQueue<>(new NodeComparator(choice));
        Set<Node> visited=new HashSet<>();
        pq.add(initial);
        int expanded=0;
        int explored=1;
        while(true)
        {
            Node current=pq.poll();
            expanded++;
            visited.add(current);

            for(int direction=0;direction<4;direction++)
            {
                if(!isValid(direction,current.starRowInd,current.starColInd,current.matrix.length,current.matrix.length))
                    continue;
                Node newNode=current.moveStar(direction);
                if(visited.contains(newNode))
                    continue;
                if(newNode.manhattanDist==0&&newNode.hammingDist==0)
                {
                    Stack<Node>st=new Stack<>();
                    Node temp=newNode;
                    while(newNode!=null)
                    {
                        st.push(newNode);
                        newNode=newNode.parent;
                    }
                    while(!st.empty())
                    {
                        System.out.println(st.pop());
                    }
                    System.out.println("Minimum Moves/Cost="+temp.cost);
                    System.out.println("Number of expanded nodes="+expanded);
                    System.out.println("Number of explored nodes="+explored+"\n");
                    return;
                }
                pq.add(newNode);
                explored++;
            }
        }

    }
}
