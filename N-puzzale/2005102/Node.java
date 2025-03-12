import java.util.Arrays;

class Node
{
    String[][] matrix;
    Node parent;
    int hammingDist;
    int manhattanDist;
    int cost;
    int starRowInd;
    int starColInd;
    private int calculateHammingDist()
    {
        int distance=0;
        int n=matrix.length;
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                if(matrix[i][j].equalsIgnoreCase("*"))continue;
                int element= Integer.parseInt(matrix[i][j]);
                int actualRow=(element-1)/n;
                int actualCol=(element-1)%n;
                if(i!=actualRow||j!=actualCol)
                {
                    distance++;
                }
            }
        }
        return distance;
    }


    private int calculateManhattanDist()
    {
        int distance=0;
        int n=matrix.length;
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                if(matrix[i][j].equalsIgnoreCase("*"))continue;
                int element=Integer.parseInt(matrix[i][j]);
                int actualRow=(element-1)/n;
                int actualCol=(element-1)%n;
                if(i!=actualRow)
                {
                    distance+=Math.abs(i-actualRow);
                }
                if(j!=actualCol)
                {
                    distance+=Math.abs(j-actualCol);
                }
            }
        }
        return distance;
    }

    private String[][] goalState()
    {
        int n=matrix.length,num=1;
        String[][] targetMat=new String[n][n];
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                if(num==n*n)
                {
                    targetMat[i][j]="*";
                    continue;
                }
                targetMat[i][j]=String.valueOf(num);
                num++;
            }
        }
        return targetMat;
    }

    public boolean isSolved()
    {
        String[][] targetMat=goalState();
        int n=matrix.length;
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                if(!targetMat[i][j].equalsIgnoreCase(matrix[i][j]))
                    return false;
            }
        }
        return true;
    }


    private void swap(String[][] Board,int r1,int c1,int r2,int c2)
    {
        String temp=Board[r1][c1];
        Board[r1][c1]=Board[r2][c2];
        Board[r2][c2]=temp;
    }

    //Generating new Node by moving star
    public Node moveStar(int directtion)
    {
        int n= matrix.length;
        String[][] newBoard=new String[n][n];
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                newBoard[i][j]=matrix[i][j];
            }
        }
        //For left,right,up,down direction, 0,1,2,3 is used respectively
        if(directtion==0)
        {
            swap(newBoard,starRowInd,starColInd,starRowInd,starColInd-1);
        }
        else if(directtion==1)
        {
            swap(newBoard,starRowInd,starColInd,starRowInd,starColInd+1);
        }
        else if(directtion==2)
        {
            swap(newBoard,starRowInd,starColInd,starRowInd-1,starColInd);
        }
        else if(directtion==3)
        {
            swap(newBoard,starRowInd,starColInd,starRowInd+1,starColInd);
        }
        return new Node(newBoard,this);
    }

    @Override
    public String toString()
    {
        StringBuilder sb=new StringBuilder();
        int n= matrix.length;
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                sb.append(matrix[i][j]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this==obj)return true;
        if(obj==null||getClass()!=obj.getClass())return false;
        Node newNode=(Node) obj;
        //for comparing 2D content
        return Arrays.deepEquals(matrix,newNode.matrix);
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode(matrix);
    }

    public Node(String[][] board,Node parent)
    {
        int n= board.length;
        matrix=new String[n][n];
        this.parent=parent;
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                if(board[i][j].equalsIgnoreCase("*"))
                {
                    starRowInd=i;
                    starColInd=j;
                }
                this.matrix[i][j]=board[i][j];
            }
        }
        if(parent==null)
        {
            this.cost=0;
        }
        else
        {
            this.cost= parent.cost+1;
        }
        this.manhattanDist=this.calculateManhattanDist();
        this.hammingDist=this.calculateHammingDist();
    }
}
