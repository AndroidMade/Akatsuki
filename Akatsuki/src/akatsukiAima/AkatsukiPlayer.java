package akatsukiAima;

import java.io.IOException;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.mulino.domain.State.Checker;
public class AkatsukiPlayer extends Thread
{
	private AkatsukiClient c;
	private Checker color;
	private boolean myTurn;
	private it.unibo.ai.didattica.mulino.domain.State s;
	
	private AkatsukiIterativeDeepeningAlphaBetaSearch search;
	private AkatsukiGame game;
	
	private AkatsukiState akaS;

	public AkatsukiPlayer(Checker color) throws UnknownHostException, IOException
	{
		this.color = color;
		if(this.color == Checker.WHITE)
			this.myTurn = true;
		else
			this.myTurn = false;
		this.c = new AkatsukiClient(color);
		
		game = new AkatsukiGame();
		search = new AkatsukiIterativeDeepeningAlphaBetaSearch(game, Integer.MIN_VALUE, Integer.MAX_VALUE, 57);
		search.setLogEnabled(true);
		akaS = new AkatsukiState(color);
		if ( color == Checker.BLACK)
		{
			akaS.setCurrentPlayer(Checker.BLACK);
			akaS.setOpponentPlayer(Checker.WHITE);
		}
	}
	
	public void run()
	{
        System.out.println("Hello " + toString() + "!");
        System.out.println("I am player " + c.getPlayer().toString() + "!");
        this.initState();

        while (true) 
        {
            if (!myTurn)
            	this.opponentMove();

            System.out.println("Player " + c.getPlayer().toString() + ", i'm thinking: ");
            String actionString = myMove();
            System.out.println("My move is: " + actionString);

            try {
                c.write(AkatsukiClient.stringToAction(actionString, s.getCurrentPhase()));
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            try {
                s = c.read();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            System.out.println("Effect of my move: \n" + s.toString());

            myTurn = false;
        }
	}
	
	private String myMove() 
	{
		return search.makeDecision(akaS).toStringMove();	
	}

	private void opponentMove() 
	{
		System.out.println("Waiting for your opponent move...");
        try 
        {
            s = c.read();
            akaS.buildState(s);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Your Opponent did his move, and the result is:\n" + this.s.toString());
	}

	private void initState()
	{
		System.out.println("Current state:");
        try {
            s = c.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(s.toString());
	}
}
