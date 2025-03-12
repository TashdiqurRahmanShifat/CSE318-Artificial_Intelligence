import java.util.Scanner;

public class MAIN
{
    public static void main(String[] args) {
        Scanner scn=new Scanner(System.in);
        int k=-1;
        while(k<3)
        {
            System.out.println("Enter the value of k(3,4,5,......):");
            k=scn.nextInt();
        }
        System.out.println("Enter the elements of matrix");
        int starRow=-1;
        String[][] matrix=new String[k][k];
        for(int i=0;i<k;i++)
        {
            for(int j=0;j<k;j++)
            {
                matrix[i][j]=scn.next();
                if(matrix[i][j].equalsIgnoreCase("*"))starRow=i;
            }
        }

        //Check for Solvable
        if(!CheckSolvable.isSolvable(matrix,starRow))
        {
            System.out.println("The Puzzle is not solvable");
            return;
        }
        Node root=new Node(matrix,null);
        if(root.isSolved())
        {
            System.out.println("The puzzle is already solved!");
            System.out.println(root);
            return;
        }
        System.out.println("\nUsing Manhattan Distance");
        AStar astar=new AStar();
        astar.aStarSolver(root,1);


        System.out.println("Using Hamming Distance");
        astar.aStarSolver(root,2);
        /*for(int i=0;i<k;i++)
        {
            for(int j=0;j<k;j++)
            {
                System.out.print(matrix[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println(starRow);*/
    }
}
