package akatsukiAima;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unibo.ai.didattica.mulino.domain.State.Checker;
import aima.core.environment.connectfour.ActionValuePair;
import aima.core.search.adversarial.Game;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;

public class AkatsukiIterativeDeepeningAlphaBetaSearch extends IterativeDeepeningAlphaBetaSearch<AkatsukiState, AkatsukiAction, Checker>
{
	
	public AkatsukiIterativeDeepeningAlphaBetaSearch(Game<AkatsukiState, AkatsukiAction, Checker> game, double utilMin,
			double utilMax, int time) {
		super(game, utilMin, utilMax, time);
	}
	
	@Override
	protected double eval(AkatsukiState state, Checker player) 
	{
		super.eval(state, player);
		return game.getUtility(state, player);
	}
	
	//Metodo per ordinare le azioni in base allo score realizzato partendo da uno state
	@Override
	public List<AkatsukiAction> orderActions(AkatsukiState state, List<AkatsukiAction> actions, Checker player, int depth) 
	{
		List<AkatsukiAction> result = actions;
		List<ActionValuePair<AkatsukiAction>> actionEstimates = new ArrayList<ActionValuePair<AkatsukiAction>>(actions.size());
		for (AkatsukiAction action : actions)
		{
			actionEstimates.add(ActionValuePair.createFor(action,  action.getScore()));
		}
		Collections.sort(actionEstimates);
		result = new ArrayList<AkatsukiAction>();
		for (ActionValuePair<AkatsukiAction> pair : actionEstimates)
			result.add(pair.getAction());
		return result;
	}

}
