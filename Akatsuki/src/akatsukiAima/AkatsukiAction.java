package akatsukiAima;

import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.domain.State.Checker;

public class AkatsukiAction 
{	
	private Checker player;
	private String from, to, remove;
	private double score;
	
	//Sposto pedina e ne rimuovo una avversaria
	public AkatsukiAction(Checker player, String from, String to, String remove)
	{
		this.player = player;
		this.from = from;
		this.to = to;
		this.remove = remove;
	}
	
	//Metto pedina in campo
	public AkatsukiAction(Checker player, String to)
	{
		this(player, "", to, "");
	}
	
	//Metto pedina e rimuovo oppure sposto pedina
	public AkatsukiAction(Checker player, String from, String to)
	{
		this(player, from, to, "");
	}
	
	public Checker getPlayer() {
		return player;
	}

	
	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getRemove() {
		return remove;
	}
	
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public boolean isPut()
	{
		return from.equals("") && remove.equals("");
	}
	
	public boolean isPutRemove()
	{
		return from.equals("") && !remove.equals("");
	}
	
	public boolean isMove()
	{
		return !from.equals("") && remove.equals("");
	}
	
	public boolean isMoveRemove()
	{
		return !from.equals("") && !remove.equals("");
	}
	
	public String toString() 
	{
		//return (this.player == State.Checker.WHITE ? "W" : "B") + " from (" + from + ") to (" + to + ") and remove (" + remove + ")";
		return (this.player == State.Checker.WHITE ? "W" : "B") + ": " + from + to + remove;
	}
	
	public String toStringMove() 
	{
        return from + to + remove;
    }
}