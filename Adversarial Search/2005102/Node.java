public class Node {
    Node parent;
    Node child;
    int alpha;
    int beta;
    int[] bins;
    int additionalMoves;
    int capturedStones;
    boolean isMax;
    int depth;
    int index;
    MancalaGame mancalaGame=new MancalaGame();
    public Node(int[] bins,boolean isMax)
    {
        int n= bins.length;
        this.bins=new int[n];
        for(int i=0;i<n;i++)
        {
            this.bins[i]=bins[i];
        }
        this.parent=null;
        this.child=null;
        this.alpha=Integer.MIN_VALUE;
        this.beta=Integer.MAX_VALUE;
        this.additionalMoves=0;
        this.capturedStones=0;
        this.isMax=isMax;
        this.depth=0;
    }


    public Node(int[] bins,Node parent,int alpha,int beta,int additionalMoves,boolean isMax,int depth,int index)
    {
        int n= bins.length;
        this.bins=new int[n];
        for(int i=0;i<n;i++)
        {
            this.bins[i]=bins[i];
        }
        this.parent=parent;
        this.child=null;
        this.alpha=alpha;
        this.beta=beta;
        this.additionalMoves=additionalMoves;
        this.capturedStones=0;
        this.isMax=isMax;
        this.depth=depth;
        this.index=index;
        moveStone(index);
    }

    void currentStateOfBoard() {
        System.out.println("\tCurrent State of the game\n");
        System.out.println("           Second player bins. Index starts from right");
        System.out.println("             +-----+-----+-----+-----+-----+-----+");
        System.out.print("Player2: ->  | ");

        for (int i = 12; i >= 7; i--) {
            System.out.print(String.format("%2d", bins[i]) + "  | ");
        }
        System.out.println();
        System.out.println("             +-----+-----+-----+-----+-----+-----+");

        System.out.println();
        System.out.println("       +-----+                                   +-----+");
        System.out.print("       | " + String.format("%2d", bins[13]) + "  |                                ");
        System.out.print("   |  " + String.format("%2d", bins[6]) + " |");
        System.out.println();
        System.out.println("       +-----+                                   +-----+");
        System.out.println("Second player storage                      First player storage");
        System.out.println();
        System.out.println("             +-----+-----+-----+-----+-----+-----+");
        System.out.print("Player1: ->  | ");


        for (int i = 0; i < 6; i++) {
            System.out.print(String.format("%2d", bins[i]) + "  | ");
        }
        System.out.println();
        System.out.println("             +-----+-----+-----+-----+-----+-----+");
        System.out.println("            First player bins. Index starts from left");
    }



    boolean isOutofStone()
    {
        boolean firstPlayerAllBinsEmpty=true;
        boolean secondPlayerAllBinsEmpty=true;
        for(int i=0;i<6;i++)
        {
            if(bins[i]!=0)
            {
                firstPlayerAllBinsEmpty=false;
                break;
            }
        }
        for(int i=7;i<13;i++)
        {
            if(bins[i]!=0)
            {
                secondPlayerAllBinsEmpty=false;
                break;
            }
        }
        return firstPlayerAllBinsEmpty||secondPlayerAllBinsEmpty;
    }

    boolean reachEnd()
    {
        boolean isMaxDepthReached=(depth>mancalaGame.Max_Depth);//Check if the maximum depth is exceeded
        boolean isGameComplete=isOutofStone();//Check if the game is complete
        return isMaxDepthReached||isGameComplete;
    }

    void moveStone(int binIndex)
    {
        int countStone=bins[binIndex];
        bins[binIndex]=0;
        int i=binIndex+1;

        //This is for opponent.Index (0-5)
        if(isMax==false)
        {

            while(countStone>0)
            {
                if(i==13)i=0;//Avoiding maximizing player's mancala
                bins[i]++;
                countStone--;
                i=(i+1)%14;//Move to next bin
            }
            i--;//Advance one step ahead.so deduct it by 1
            if(i<0)i=12;

            if(i==6)
            {
                additionalMoves++;
            }
            else if((bins[i]==1)&&(i>=0&&i<=5)&&(bins[12-i]!=0))//Capture stones
            {
                capturedStones=bins[12-i];
                bins[12-i]=0;
                bins[6]+=capturedStones+bins[i];
                bins[i]=0;
            }
        }

        //This is for maximizing player
        else
        {

            while(countStone>0)
            {
                if(i==6)i=7;//Avoiding opponent player's mancala
                bins[i]++;
                countStone--;
                i=(i+1)%14;//Move to next bin
            }
            i--;
            if(i<0)i=13;
            if(i==13)
            {
                additionalMoves++;
            }

            else if((bins[i]==1)&&(i>=7)&&(bins[12-i]!=0))
            {
                capturedStones=bins[12-i];
                bins[12-i]=0;
                bins[13]+=capturedStones+bins[i];
                bins[i]=0;
            }
        }
    }
}
