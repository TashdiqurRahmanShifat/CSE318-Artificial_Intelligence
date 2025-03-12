import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;

public class MancalaGame
{
    static Random random=new Random();
    private static int W1;
    private static int W2;
    private static int W3;
    private static int W4;
    public int Max_Depth;
    public long exploredNodes;
    public long prunedNodes;
    public int heuristicForPlayer1;
    public int heuristicForPlayer2;

    public int stoneDifference(Node node)
    {
        int firstPlayerStone=0,secondPlayerStone=0;
        for(int i=0;i<6;i++)
        {
            firstPlayerStone+=node.bins[i];
        }
        for(int i=7;i<13;i++)
        {
            secondPlayerStone+=node.bins[i];
        }
        return firstPlayerStone-secondPlayerStone;
    }
    public int heuristic1(Node node)
    {
        return node.bins[6]-node.bins[13];
    }
    public int heuristic2(Node node)
    {
        W1=random.nextInt(20)+1;
        W2=random.nextInt(20)+1;
        return W1*heuristic1(node)+W2*stoneDifference(node);
    }
    public int heuristic3(Node node)
    {
        W3=random.nextInt(20)+1;
        int numberOfAdditionalMoves;
        if(node.isMax)
            numberOfAdditionalMoves=node.additionalMoves;
        else
            numberOfAdditionalMoves=-node.additionalMoves;
        return heuristic2(node)+W3*numberOfAdditionalMoves;
    }
    public int heuristic4(Node node)
    {
        W4=random.nextInt(20)+1;
        int stonesCaptured;
        if(node.isMax)
            stonesCaptured=node.capturedStones;
        else
            stonesCaptured=-node.capturedStones;
        return heuristic3(node)+W4*stonesCaptured;
    }

    public void visitNode(Node node)
    {
        this.exploredNodes++;
        //Base Case
        if(node.reachEnd())
        {
            //For maximizing player
            if(node.isMax)
            {
                switch(heuristicForPlayer1)
                {
                    case 1:
                        node.alpha=heuristic1(node);
                        break;
                    case 2:
                        node.alpha=heuristic2(node);
                        break;
                    case 3:
                        node.alpha=heuristic3(node);
                        break;
                    case 4:
                        node.alpha=heuristic4(node);
                        break;
                }
            }
            else
            {
                switch(heuristicForPlayer2)
                {
                    case 1:
                        node.beta=heuristic1(node);
                        break;
                    case 2:
                        node.beta=heuristic2(node);
                        break;
                    case 3:
                        node.beta=heuristic3(node);
                        break;
                    case 4:
                        node.beta=heuristic4(node);
                        break;
                }
            }
        }

        //Recursive Case
        else
        {
            int initIndex;
            if(node.isMax)
                initIndex=0;
            else initIndex=7;
            int numberOfBins=6;
            while(numberOfBins>0)
            {
                //current bin is not empty
                if(node.bins[initIndex]!=0)
                {
                    //create a child
                    Node nodeChild=new Node(node.bins,node,node.alpha,node.beta,node.additionalMoves,!node.isMax,node.depth+1,initIndex);
                    //check if nodeChild gets any additional move
                    if(nodeChild.additionalMoves>node.additionalMoves)
                    {
                        //if additional move gets,make it current player and no change in captured stones
                        nodeChild.capturedStones=node.capturedStones;
                        //no change in player
                        nodeChild.isMax=node.isMax;
                    }
                    else {
                        //find last node with same isMax value as nodeChild
                        Node temp = node;
                        while ((temp != null) && (temp.isMax != nodeChild.isMax))
                            temp = temp.parent;
                        if (temp == null)//That means first time the player is making a move
                        {
                            //Set to 0
                            nodeChild.capturedStones = 0;
                            nodeChild.additionalMoves = 0;
                        } else {
                            nodeChild.capturedStones += temp.capturedStones;//stones captured in previous moves are combined with the current captured stones
                            nodeChild.additionalMoves = temp.additionalMoves;//Game remembers how many extra moves the player had
                        }
                    }
                    visitNode(nodeChild);

                    //updating alpha
                    if(node.isMax)
                    {
                        if((nodeChild.alpha>node.alpha)&&(nodeChild.isMax))
                        {
                            node.alpha=nodeChild.alpha;
                            node.child=nodeChild;
                        }
                        else if((nodeChild.beta>node.alpha)&&(!nodeChild.isMax))
                        {
                            node.alpha=nodeChild.beta;
                            node.child=nodeChild;
                        }
                    }
                    //updating beta
                    else
                    {
                        if((nodeChild.alpha<node.beta)&&(nodeChild.isMax))
                        {
                            node.beta=nodeChild.alpha;
                            node.child=nodeChild;
                        }
                        else if((nodeChild.beta<node.beta)&&(!nodeChild.isMax))
                        {
                            node.beta=nodeChild.beta;
                            node.child=nodeChild;
                        }
                    }
                }
                //alpha-beta pruning
                //if alpha>beta,the current branch can be pruned as it won't change the result
                //node and child is different here as it ensures that one is maximizing and another is minimizing the score
                if((node.alpha>=node.beta)&&(node.parent.isMax!=node.isMax))
                {
                    while(numberOfBins>0)
                    {
                        if(node.bins[initIndex]!=0)
                            prunedNodes+=1;
                        numberOfBins--;
                        initIndex++;
                    }
                    break;
                }
                numberOfBins--;
                initIndex++;
            }
        }
    }

    public void adjustRemainingStones(Node node)
    {
        for(int i=0;i<=5;i++)
        {
            node.bins[6]+=node.bins[i];
            node.bins[i]=0;
        }
        for(int i=7;i<=12;i++)
        {
            node.bins[13]+=node.bins[i];
            node.bins[i]=0;
        }
    }

    public void resetNodeCounter()
    {
        exploredNodes=0;
        prunedNodes=0;
    }

    public void printNodeStatistics()
    {
        System.out.println();
        System.out.println("      Explored Nodes:"+exploredNodes+"; Pruned Nodes:"+prunedNodes);
    }

    public void printPlayerMove(Node parent,Node node)
    {
        String playerNumber;
        if(parent.isMax)playerNumber="1";
        else playerNumber="2";
        System.out.println();
        System.out.println("Match Update: -> Player "+playerNumber+" selects from bin "+(node.index+1));
    }

    public String declareWinner(Node node)
    {
        String result="";
        System.out.println();
        System.out.println("             -------------------------------------");
        if(node.bins[6]==node.bins[13])
        {
            result="tie";
            System.out.println("             |            It's a tie!            |");
        }
        else if(node.bins[6]>node.bins[13])
        {
            result="player1";
            System.out.println("             |           Player1 wins!           |");
        }
        else
        {
            result="player2";
            System.out.println("             |           Player 2 wins!          |");
        }
        System.out.println("             -------------------------------------");
        System.out.println("      Final Result: Player1 score -> "+node.bins[6]+", Player2 score -> "+node.bins[13]);
        String finalUpdate=" Final Result: Player1 score -> "+node.bins[6]+", Player2 score -> "+node.bins[13];
        return result+finalUpdate;
    }
    public String AI_vs_AI(boolean choice)
    {
        int[] bins=new int[14];
        for(int i=0;i<14;i++)
        {
            if(i==6||i==13)continue;
            bins[i]=4;
        }
        //If choice is true it would be first player,if false it would be second player
        Node node=new Node(bins,choice);
        System.out.println();
        System.out.println("\tMancala game starts between two computers\n");
        System.out.println();
        node.currentStateOfBoard();

        while(true)
        {
            resetNodeCounter();
            visitNode(node);
            Node parentNode=node;
            printNodeStatistics();
            node=node.child;
            printPlayerMove(parentNode,node);
            node.currentStateOfBoard();
            if(node.isOutofStone())
                break;//Game Over
            else
            {
                if(parentNode.isMax==node.isMax)
                {
                    System.out.println("\n\tMatch Update: -> You have got additional move!\n");
                }
                node=new Node(node.bins,node.isMax);
            }
        }
        adjustRemainingStones(node);
        return declareWinner(node);
    }

    public void yourMove(int[] bins,Scanner scn)
    {
        while(true)
        {
            System.out.println("\nYour move!Please see player2 row to know your current state.Choose a bin from (1-6):");
            int binIndex;
            while(true)
            {
                int bin=scn.nextInt();
                binIndex=bin+6;//I am playing as player 2.So index starts from 7
                if(binIndex>12||binIndex<7)
                {
                    System.out.println("Invalid bin.Enter again from (1-6)");
                }
                else
                {
                    int numberOfStone=bins[binIndex];
                    if(numberOfStone==0)
                    {
                        System.out.println("You are selecting an empty bin which can't be done");
                    }
                    else break;
                }
            }
            System.out.println("\n\tMatch Update: -> You have selected from bin "+(binIndex-6));
            System.out.println();
            int countStone=bins[binIndex];
            bins[binIndex]=0;
            int i=binIndex+1;
            while(countStone>0)
            {
                if(i==6)i=7;//Avoiding opponent player's mancala
                bins[i]++;
                countStone--;
                i=(i+1)%14;//Move to next bin
            }
            i--;
            if(i<0)i=13;

            //Check for additional Moves
            if(i==13)
            {
                Node nextNode=new Node(bins,false);
                nextNode.currentStateOfBoard();
                if(nextNode.isOutofStone())break;
                System.out.println("\n\tMatch Update: -> You have got an additional move!\n");
            }
            else if((bins[i]==1)&&(i>=7)&&(bins[12-i]!=0))
            {
                int stones=bins[12-i];
                bins[12-i]=0;
                bins[13]+=stones+bins[i];
                bins[i]=0;
                break;
            }
            else break;
        }
    }


    public String play_As_Player2()
    {
        int[] bins=new int[14];
        for(int i=0;i<14;i++)
        {
            if(i==6||i==13)continue;
            bins[i]=4;
        }
        //I will make the first move
        Node newNode=new Node(bins,false);
        System.out.println();
        System.out.println("\tWelcome to Mancala game.You will play with a bot as player2,Bot as player1\n");
        newNode.currentStateOfBoard();

        while(true)
        {
            //Player 1 will play
            if(newNode.isMax)
            {
                visitNode(newNode);
                newNode = newNode.child;
                System.out.println();
                System.out.println("\n\tNow it's time for the bot to make a move");
                System.out.println();
                System.out.println("\n\tMatch Update: -> Bot selects from bin "+(newNode.index + 1)+"\n");
                newNode.currentStateOfBoard();

                if (newNode.isOutofStone()) break;
                else if (newNode.isMax) {
                    System.out.println("\n\tMatch Update: -> Bot gets additional move\n");
                    newNode = new Node(newNode.bins, true);//set true as bot will play as player1
                }
            }
            else
            {
                Scanner scanner=new Scanner(System.in);
                int[] bins2=new int[newNode.bins.length];
                for(int i=0;i<newNode.bins.length;i++)
                {
                    bins2[i]=newNode.bins[i];
                }
                yourMove(bins2,scanner);
                //next bot's turn
                newNode=new Node(bins2,true);
                newNode.currentStateOfBoard();
                if(newNode.isOutofStone())break;

            }
        }
        adjustRemainingStones(newNode);
        return declareWinner(newNode);
    }

    public void runningGames(int n,int option,Scanner scanner)
    {
        boolean choice=false;
        int opinion;
        if(option==1)
        {
            System.out.println("Enter the max depth:");
            int depth=scanner.nextInt();
            Max_Depth=depth;
            System.out.println("Type 1 to select player1 as the first mover and 2 to select player2 as first mover :");
            opinion=scanner.nextInt();
            if(opinion==1)choice=true;
        }
        try(BufferedWriter writer=new BufferedWriter(new FileWriter("game_results.txt"))) {
            writer.write("------------------------------------------------------------------------------\n");
            writer.write("\t\t\t\t\t\t\tMancala game statistics\n");
            writer.write("------------------------------------------------------------------------------\n");
            writer.write("\n");
            if (option == 1){
                int countHeuristic1wins=0,countHeuristic2wins=0,countHeuristic3wins=0,countHeuristic4wins=0;
                int numberOfPlayer1Wins=0,numberOfPlayer2Wins=0,draw=0;
                for (int j = 1; j <= 3; j++) {

                    heuristicForPlayer1 = j;
                    for (int k = j + 1; k <= 4; k++) {
                        numberOfPlayer1Wins = 0;
                        numberOfPlayer2Wins = 0;
                        draw = 0;

                        heuristicForPlayer2 = k;
                        writer.write("------------------------------------------------------------------------------\n");
                        writer.write("Heuristics for player1:" + heuristicForPlayer1 + "\n");
                        writer.write("Heuristics for player2:" + heuristicForPlayer2 + "\n");
                        writer.write("------------------------------------------------------------------------------\n");
                        for (int i = 0; i < n; i++) {
                            String res;
                            if (option == 1)
                                res = AI_vs_AI(choice);
                            else res = play_As_Player2();
                            String[] parts = res.split(" ", 2);
                            String firstword = parts[0];
                            String secondpart = parts[1];

                            if (firstword.equalsIgnoreCase("player1")) {
                                numberOfPlayer1Wins++;
                                writer.write("Game " + (i + 1) + ": Player 1 wins.");
                                writer.write(secondpart + "\n");
                            } else if (firstword.equalsIgnoreCase("player2")) {
                                numberOfPlayer2Wins++;
                                writer.write("Game " + (i + 1) + ": Player 2 wins.");
                                writer.write(secondpart + "\n");
                            } else {
                                draw++;
                                writer.write("Game " + (i + 1) + ": Draw.");
                                writer.write(secondpart + "\n");
                            }
                        }

                        System.out.println("Player 1 wins : " + numberOfPlayer1Wins + " times");
                        System.out.println("Player 2 wins : " + numberOfPlayer2Wins + " times");
                        System.out.println("Match draw : " + draw + " times");

                        // Writing into file
                        writer.write("------------------------------------------------------------------------------\n");
                        writer.write("\t\t\t\t\t\t\tTotal games played: " + n + "\n");
                        writer.write("\t\t\t\t  Player 1 wins: " + numberOfPlayer1Wins + "; Player 1 win ratio: "+String.format("%.2f",(double)(numberOfPlayer1Wins*100)/n)+"%\n");
                        writer.write("\t\t\t\t  Player 2 wins: " + numberOfPlayer2Wins + "; Player 2 win ratio: "+String.format("%.2f",(double)(numberOfPlayer2Wins*100)/n)+"%\n");
                        writer.write("\t\t\t\t\t\t  Draws: " + draw + "; Draw ratio: "+String.format("%.2f",(double)(draw*100)/n)+"%\n");
                        writer.write("------------------------------------------------------------------------------\n");
                        writer.write("\n");
                        writer.write("\n");
                        if(j==1&&k==2||j==1&&k==3||j==1&&k==4)countHeuristic1wins+=numberOfPlayer1Wins;
                        if(j==2&&k==3||j==2&&k==4)countHeuristic2wins+=numberOfPlayer1Wins;
                        if(j==1&&k==2)countHeuristic2wins+=numberOfPlayer2Wins;
                        if(j==1&&k==3||j==2&&k==3)countHeuristic3wins+=numberOfPlayer2Wins;
                        if(j==3&&k==4)countHeuristic3wins+=numberOfPlayer1Wins;
                        if(j==1&&k==4||j==2&&k==4||j==3&&k==4)countHeuristic4wins+=numberOfPlayer2Wins;
                    }

                }
                writer.write("------------------------------------------------------------------------------\n");
                writer.write("Total winnings of all heuristics for depth : "+Max_Depth+" ->\n");
                writer.write("Heuristics 1 = "+countHeuristic1wins+"; Win ratio for heuristic 1 : "+String.format("%.2f",(double)(countHeuristic1wins*100)/(6*n))+"%\n");
                writer.write("Heuristics 2 = "+countHeuristic2wins+"; Win ratio for heuristic 2 : "+String.format("%.2f",(double)(countHeuristic2wins*100)/(6*n))+"%\n");
                writer.write("Heuristics 3 = "+countHeuristic3wins+"; Win ratio for heuristic 3 : "+String.format("%.2f",(double)(countHeuristic3wins*100)/(6*n))+"%\n");
                writer.write("Heuristics 4 = "+countHeuristic4wins+"; Win ratio for heuristic 4 : "+String.format("%.2f",(double)(countHeuristic4wins*100)/(6*n))+"%\n");
                writer.write("------------------------------------------------------------------------------\n");


            }
            else
            {
                int numberOfPlayer1Wins = 0, numberOfPlayer2Wins = 0, draw = 0;
                writer.write("------------------------------------------------------------------------------\n");
                writer.write("Heuristics for player1:" + heuristicForPlayer1 + "\n");
                writer.write("------------------------------------------------------------------------------\n");
                for (int i = 0; i < n; i++) {
                    String res = play_As_Player2();
                    String[] parts = res.split(" ", 2);
                    String firstword = parts[0];
                    String secondpart = parts[1];

                    if (firstword.equalsIgnoreCase("player1")) {
                        numberOfPlayer1Wins++;
                        writer.write("Game " + (i + 1) + ": Player 1 wins.");
                        writer.write(secondpart + "\n");
                    } else if (firstword.equalsIgnoreCase("player2")) {
                        numberOfPlayer2Wins++;
                        writer.write("Game " + (i + 1) + ": Player 2 wins.");
                        writer.write(secondpart + "\n");
                    } else {
                        draw++;
                        writer.write("Game " + (i + 1) + ": Draw.");
                        writer.write(secondpart + "\n");
                    }
                }

                System.out.println("Player 1 wins : " + numberOfPlayer1Wins + " times");
                System.out.println("Player 2 wins : " + numberOfPlayer2Wins + " times");
                System.out.println("Match draw : " + draw + " times");

                writer.write("------------------------------------------------------------------------------\n");
                writer.write("\t\t\t\t\t\t\tTotal games played: " + n + "\n");
                writer.write("\t\t\t\t  Player 1 wins: " + numberOfPlayer1Wins + "; Player 1 win ratio: "+String.format("%.2f",(double)(numberOfPlayer1Wins*100)/n)+"%\n");
                writer.write("\t\t\t\t  Player 2 wins: " + numberOfPlayer2Wins + "; Player 2 win ratio: "+String.format("%.2f",(double)(numberOfPlayer2Wins*100)/n)+"%\n");
                writer.write("\t\t\t\t\t\t  Draws: " + draw + "; Draw ratio: "+String.format("%.2f",(double)(draw*100)/n)+"%\n");
                writer.write("------------------------------------------------------------------------------\n");
                writer.write("\n");
                writer.write("\n");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        MancalaGame mancalaGame=new MancalaGame();
        Scanner scanner=new Scanner(System.in);
        System.out.println("There are two choices:");
        System.out.println("1.Computer vs Computer");
        System.out.println("2.Computer vs Human");

        int choice=-1;
        while(choice!=1&&choice!=2)
        {
            System.out.println("Select an option(1 or 2):");
            choice = scanner.nextInt();
        }
        if(choice==1)
            System.out.println("You have selected Computer vs Computer game");
        else
        {
            System.out.println("You have selected Computer vs Human game");
            System.out.println("Enter the number for player1 heuristic:");
            mancalaGame.heuristicForPlayer1=scanner.nextInt();
            System.out.println("Enter the number for player2 heuristic:");
            mancalaGame.heuristicForPlayer2=scanner.nextInt();
        }

        System.out.println("How many times you want to run the game");
        int n=scanner.nextInt();
        mancalaGame.runningGames(n,choice,scanner);
    }
}
