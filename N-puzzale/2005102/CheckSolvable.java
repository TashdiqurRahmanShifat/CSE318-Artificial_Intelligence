import java.util.ArrayList;
import java.util.List;

class CheckSolvable
{
    public static boolean isSolvable(String[][] matrix,int starRow)
    {
        int inversion=0;
        List<String> makeList=new ArrayList<>();

        int n= matrix.length;
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                makeList.add(matrix[i][j]);
            }
        }

        for(int i=0;i<makeList.size();i++)
        {
            for(int j=i+1;j<makeList.size();j++)
            {
                if((!makeList.get(i).equalsIgnoreCase("*"))&&(!makeList.get(j).equalsIgnoreCase("*"))&&
                        Integer.parseInt(makeList.get(i))>Integer.parseInt(makeList.get(j)))
                {
                    inversion++;
                }
            }
        }
        if(n%2!=0)
        {
            return inversion%2==0;
        }
        else
        {
            int fromBottomStar=n-starRow-1;
            if((fromBottomStar+inversion)%2==0)return true;
            return false;
        }
    }
}
