package akatsukiAima;

import java.io.IOException;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.mulino.actions.Action;
import it.unibo.ai.didattica.mulino.actions.Phase1Action;
import it.unibo.ai.didattica.mulino.actions.Phase2Action;
import it.unibo.ai.didattica.mulino.actions.PhaseFinalAction;
import it.unibo.ai.didattica.mulino.client.MulinoClient;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.domain.State.Checker;
import it.unibo.ai.didattica.mulino.domain.State.Phase;

public class AkatsukiClient extends MulinoClient
{

	public AkatsukiClient(Checker player) throws UnknownHostException, IOException {
		super(player);
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException 
	{
		State.Checker color = Checker.EMPTY;

		if (args.length == 1 && (args[0].equalsIgnoreCase("B") || args[0].equalsIgnoreCase("black")))
			color = State.Checker.BLACK;
		else if (args.length == 1 && (args[0].equalsIgnoreCase("W") || args[0].equalsIgnoreCase("white")))
			color = State.Checker.WHITE;
		else 
		{
			System.out.println("Lanciare con W (white) or B (black)");
			System.exit(-1); // uscita con errore
		}
		
		AkatsukiPlayer player = new AkatsukiPlayer(color);
		player.start();
	}

	/**
	 * Converte una stringa testuale in un oggetto azione
	 * 
	 * @param actionString
	 *            La stringa testuale che esprime l'azione desiderata
	 * @param fase
	 *            La fase di gioco attuale
	 * @return L'oggetto azione da comunicare al server
	 */
	public static Action stringToAction(String actionString, Phase fase) {
		if (fase == Phase.FIRST) { // prima fase
			Phase1Action action;
			action = new Phase1Action();
			action.setPutPosition(actionString.substring(0, 2));
			if (actionString.length() == 4)
				action.setRemoveOpponentChecker(actionString.substring(2, 4));
			else
				action.setRemoveOpponentChecker(null);
			return action;
		} else if (fase == Phase.SECOND) { // seconda fase
			Phase2Action action;
			action = new Phase2Action();
			action.setFrom(actionString.substring(0, 2));
			action.setTo(actionString.substring(2, 4));
			if (actionString.length() == 6)
				action.setRemoveOpponentChecker(actionString.substring(4, 6));
			else
				action.setRemoveOpponentChecker(null);
			return action;
		} else { // ultima fase
			PhaseFinalAction action;
			action = new PhaseFinalAction();
			action.setFrom(actionString.substring(0, 2));
			action.setTo(actionString.substring(2, 4));
			if (actionString.length() == 6)
				action.setRemoveOpponentChecker(actionString.substring(4, 6));
			else
				action.setRemoveOpponentChecker(null);
			return action;
		}
	}

}
