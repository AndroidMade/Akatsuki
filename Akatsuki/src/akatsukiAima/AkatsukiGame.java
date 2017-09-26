package akatsukiAima;

import it.unibo.ai.didattica.mulino.actions.WrongPositionException;
import it.unibo.ai.didattica.mulino.domain.State.Checker;

import java.util.List;

import aima.core.search.adversarial.Game;

public class AkatsukiGame implements Game<AkatsukiState, AkatsukiAction, Checker>
{
	AkatsukiState initState = new AkatsukiState(Checker.EMPTY);

	@Override
	public List<AkatsukiAction> getActions(AkatsukiState state) 
	{
		try {
			return state.getMoves();
		} catch (WrongPositionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AkatsukiState getInitialState() 
	{
		return initState;
	}

	@Override
	public Checker getPlayer(AkatsukiState state) 
	{
		return state.getCurrentPlayer();
	}

	@Override
	public Checker[] getPlayers() 
	{
		return new Checker[] {Checker.WHITE, Checker.BLACK};
	}

	@Override
	public AkatsukiState getResult(AkatsukiState state, AkatsukiAction move) 
	{
		AkatsukiState s = state.clone();
		try {
			s.makeMove(move);
		} catch (WrongPositionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	@Override
	public double getUtility(AkatsukiState state, Checker player) 
	{
		if(state.getOpponentPlayer() == player)
			return state.getScore();
		return -state.getScore();
	}

	@Override
	public boolean isTerminal(AkatsukiState state) 
	{
		Double s = state.getScore();
		return s == Integer.MAX_VALUE || s == Integer.MIN_VALUE;
	}

}
