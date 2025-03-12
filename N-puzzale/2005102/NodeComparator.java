import java.util.Comparator;

class NodeComparator implements Comparator<Node>
{
    private final int useManhattan;
    public NodeComparator(int useManhattan)
    {
        this.useManhattan=useManhattan;
    }

    @Override
    public int compare(Node a,Node b)
    {
        if(useManhattan==1)
        {
            int aPriority=a.manhattanDist+a.cost;
            int bPriority=b.manhattanDist+b.cost;
            return Integer.compare(aPriority,bPriority);
        }
        else
        {
            int aPriority=a.hammingDist+a.cost;
            int bPriority=b.hammingDist+b.cost;
            return Integer.compare(aPriority,bPriority);
        }
    }
}
