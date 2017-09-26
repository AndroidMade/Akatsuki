package akatsukiAima;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import it.unibo.ai.didattica.mulino.actions.Util;
import it.unibo.ai.didattica.mulino.actions.WrongPositionException;
import it.unibo.ai.didattica.mulino.domain.State;

public class AkatsukiState extends State implements Cloneable
{
	private static final long serialVersionUID = 1L;
	 
	private Checker currentPlayer;
	private double score;
	private Checker opponentPlayer;
	private AkatsukiState previousState;
	private Checker me; //Checker che indica quale giocatore siamo noi (vedi evaluate)
	
	public AkatsukiState(Checker me)
	{
		super();
		currentPlayer = Checker.WHITE;	
		opponentPlayer = Checker.BLACK;
		score = 0;
		previousState = null;
		this.me = me;
	}
	
	public Checker getCurrentPlayer()
	{
		return currentPlayer;
	}
	
	public Checker getOpponentPlayer()
	{
		return opponentPlayer;
	}
	
	public double getScore()
	{
		return score;
	}
	
	public void setCurrentPlayer(Checker currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setOpponentPlayer(Checker opponentPlayer) {
		this.opponentPlayer = opponentPlayer;
	}
	
	@Override
	public Phase getCurrentPhase()
	{
		if(getCheckersToPlay(currentPlayer) > 0)
			return Phase.FIRST;
		if(getCheckersToPlay(currentPlayer) == 0 && getCheckersOnBoard(currentPlayer) > 3)
			return Phase.SECOND;
		return Phase.FINAL;
	}
	
	public void switchPlayers ()
	{
		Checker tmp = currentPlayer;
		currentPlayer = opponentPlayer;
		opponentPlayer = tmp;
	}
	
	//Restituisce numero complessivo pedine da giocare
	private int getCheckersToPlay()
	{
		return getWhiteCheckers() + getBlackCheckers();
	}
	
	public int getCheckersToPlay(Checker player) 
	{
		if(player == Checker.WHITE)
			return getWhiteCheckers();
		return getBlackCheckers();
	}
	
	//Restituisce i checker sulla tavola in base al giocatore
	public int getCheckersOnBoard(Checker player)
	{
		if(player == Checker.WHITE)
			return getWhiteCheckersOnBoard();
		return getBlackCheckersOnBoard();
	}
		
	public Checker getOppositePlayer(Checker player)
	{
		if(player == Checker.WHITE)
			return Checker.BLACK;
		return Checker.WHITE;
	}

	public void makeMove(AkatsukiAction move) throws WrongPositionException
	{
		previousState = clone();
		getBoard().put(move.getTo(), move.getPlayer());
		if (move.isPut()) // mossa di piazzamento senza rimozione pedina avversaria	(FASE 1 )
		{
			putWithoutRemove(move.getPlayer(),move.getTo());
		}
		else if (move.isMove()) // mossa di spostamento senza rimozione (FASE 2 o FASE 3)
		{
			getBoard().put(move.getFrom(), Checker.EMPTY);
		}
		else if (move.isMoveRemove()) // mossa di spostamento con rimozione (FASE 2 o FASE 3 )
		{
			getBoard().put(move.getFrom(), Checker.EMPTY);
			getBoard().put(move.getRemove(), Checker.EMPTY);
			moveWithRemove(move.getPlayer(), move.getFrom(),move.getTo(), move.getRemove());
		}
		else 	// mossa di piazzamento con rimozione pedina avversaria (FASE 1)
		{
			putWithRemove(move.getPlayer(), move.getTo(), move.getRemove());
		}
		evaluate(move);
		switchPlayers();
	}
	
	public void putWithoutRemove(Checker player, String to) 
	{
		switch (player)
		{
			case BLACK:
				setBlackCheckers(getBlackCheckers()-1); // dalla "mano" toglie 1
				setBlackCheckersOnBoard(getBlackCheckersOnBoard()+1); // aggiunge sulla tavola
				break;
			case WHITE: 
				setWhiteCheckers(getWhiteCheckers()-1); 
				setWhiteCheckersOnBoard(getWhiteCheckersOnBoard()+1);
				break;
			default:
				break;
		}
	}
	
	public void putWithRemove(Checker player, String to,String toRemove)
	{
		getBoard().put(toRemove, Checker.EMPTY);
		switch (player)
		{
			case BLACK:
				setBlackCheckers(getBlackCheckers()-1); // dalla "mano" toglie 1
				setBlackCheckersOnBoard(getBlackCheckersOnBoard()+1); // aggiunge sulla tavola
				setWhiteCheckersOnBoard(getWhiteCheckersOnBoard()-1);
				break;
			case WHITE: 
				setWhiteCheckers(getWhiteCheckers()-1); 
				setWhiteCheckersOnBoard(getWhiteCheckersOnBoard()+1);
				setBlackCheckersOnBoard(getBlackCheckersOnBoard()-1); 
				break;
			default:
				break;
		}
	}

	public void moveWithRemove(Checker player, String from, String to,String toRemove)
	{
		switch (player)
		{
		case BLACK:
			setWhiteCheckersOnBoard(getWhiteCheckersOnBoard()-1);
			break;
		case WHITE: 
			setBlackCheckersOnBoard(getBlackCheckersOnBoard()-1); 
			break;
			default:
				break;
		}
	}
	
	public List<AkatsukiAction> getMoves() throws WrongPositionException
	{
		ArrayList<AkatsukiAction> moves = new ArrayList<>();
		HashMap<String, State.Checker> board = this.getBoard();
		// FASE 1
		if(getCurrentPhase() == Phase.FIRST)
		{	
			for (String s : board.keySet())
			{
				if (board.get(s) == Checker.EMPTY)
					addMoves(currentPlayer,"",s,moves);
			}
		}
		else if(getCurrentPhase() == Phase.SECOND)	// la pedina può spostarsi solo nelle immediate vicinanze
		{
			for (String s : getBoard().keySet())
			{
				if (getBoard().get(s) == currentPlayer)
				{
					List<AkatsukiAction> possibleNeighborsToMove = getEmptyNeighbors(currentPlayer, s);
					if (!possibleNeighborsToMove.isEmpty()) // SE LA PEDINA è BLOCCATA NON VENGONO AGGIUNTE MOSSE
						moves.addAll(possibleNeighborsToMove); // aggiunge alle mosse gli spostamenti in punti vicini	
				}
			}
		}
		
		else	// FASE 3 
		{
			for (String s : getBoard().keySet())
			{
				if (getBoard().get(s) == currentPlayer)
					moves.addAll(getPossibleJump(currentPlayer,s));	// inserite tutte le coppie Action from-to
			}
		}
		
		return moves;
	}

	public void evaluate(AkatsukiAction move) throws WrongPositionException
	{
		if(hasWon(currentPlayer))
			score = Integer.MAX_VALUE;
		else if(this.getCurrentPhase() == Phase.FIRST)
			score = 18 * closedMorris() + 26 * nMorrises() + 8 * nBlockedPieces() + 9 * nPieces() + 18 * n2PieceConfiguration() + 10 * n3PieceConfiguration();
		else if(this.getCurrentPhase() == Phase.SECOND)
		{
			if(me == Checker.WHITE)
				score = 14 * closedMorris() + 43 * nMorrises() + 12 * nBlockedPieces() + 11 * nPieces() + 8 * doubleMorris() + 7 * openedMorris();
			else
				score = 14 * closedMorris() + 43 * nMorrises() + 10 * nBlockedPieces() + 11 * nPieces() + 8 * doubleMorris() + 7 * openedMorris();
		}
		
		else
			score = 16 * closedMorris() + 10 * n2PieceConfiguration() + 1 * n3PieceConfiguration();
		move.setScore(score);
		
	}
	
	private int openedMorris() throws WrongPositionException
	{
		return openedMorris(currentPlayer) - openedMorris(opponentPlayer);
	}
	
	private int openedMorris(Checker player) throws WrongPositionException 
	{
		int count = 0;
		HashMap<String, Checker> b = getBoard();
		String adiacents[], k;
		for(Entry<String, Checker> entry : b.entrySet())
		{
			k = entry.getKey();
			if(b.get(k) == Checker.EMPTY)
			{
				adiacents = Util.getAdiacentTiles(k);
				for(int i = 0; i < adiacents.length; i++)
				{
					if(b.get(adiacents[i]) == player && isAMill(player, adiacents[i], k))
						count++;
				}
			}
		}
		
		return count;
	}
	
	//Se non ci sono più pedine da giocare &&( il giocatore ha meno di 2 pedine in campo || il giocatore non può più muoversi)
	public boolean hasWon(Checker player) throws WrongPositionException
	{
		Checker oppositePlayer = getOppositePlayer(player);
		return getCheckersToPlay() == 0 && (getCheckersOnBoard(oppositePlayer) < 3 || (nBlockedPieces(oppositePlayer) == getCheckersOnBoard(oppositePlayer)));
	}
	
	//Current - Opponent(due tris che condividono un pezzo)
	private int doubleMorris()
	{
		return doubleMorris(currentPlayer) - doubleMorris(opponentPlayer);
	}
	
	private int doubleMorris(Checker player) 
	{
		int count = 0;
		
		for(Entry<String, Checker> entry : getBoard().entrySet())
		{
			if(entry.getValue() == player)
			{
				if(Util.isInHTriple(this, entry.getKey()) && Util.isInVTriple(this, entry.getKey()))
						count++;
			}
		}
		
		return count;
	}

	//CURRENT - OPPONENT (Configurazione con due pezzi dove se ne aggiungi uno fai tris sicuramente, ovvero sia da un lato che da un altro)
	private int n3PieceConfiguration() 
	{
		return n3PieceConfiguration(currentPlayer) - n3PieceConfiguration(opponentPlayer);
	}
	
	private int n3PieceConfiguration(Checker player) 
	{
		int count = 0, nV = 0, nH = 0;
		String vAdiacents[], hAdiacents[], k;
		
		HashMap<String, Checker> b = getBoard();
		for(Entry<String, Checker> entry : b.entrySet())
		{
			k = entry.getKey();
			if(!Util.isInHTriple(this, k) && !Util.isInVTriple(this, k))
			{
				hAdiacents = Util.getHSet(k);
				vAdiacents = Util.getVSet(k);
				for(int i = 0; i < 3; i++)
				{
					if(b.get(hAdiacents[i]) == player)
						nH++;
					else if(b.get(hAdiacents[i]) == getOppositePlayer(player))
						nH--;
					if(b.get(vAdiacents[i]) == player)
						nV++;
					else if(b.get(vAdiacents[i]) == getOppositePlayer(player))
						nV--;
				}
				if(nH == 2 && nV == 2)
					count++;
			}
			nH = 0;
			nV = 0;
		}
		return count;
	}

	//CURRENT - OPPONENT (Configurazione con due pezzi dove se ne aggiungi uno fai tris)
	private int n2PieceConfiguration() throws WrongPositionException 
	{
		return n2PieceConfiguration(currentPlayer) - n2PieceConfiguration(opponentPlayer);
	}
	
	private int n2PieceConfiguration(Checker player) throws WrongPositionException 
	{
		int count = 0, n = 0;
		HashMap<String, Checker> b = getBoard();
		for(Entry<String, Checker> entry : b.entrySet())
		{
			String k = entry.getKey();
			if(k.contains("d") || k.equals("b4") || k.equals("f4"))
			{
				if(!Util.isInHTriple(this, k))
				{
					String adiacents[] = Util.getHSet(k);
					for(int i = 0; i < 3; i++)
					{
						if(b.get(adiacents[i]) == player)
							n++;
						else if(b.get(adiacents[i]) == getOppositePlayer(player))
							n--;
					}
					if(n == 2)
						count++;
				}
				n = 0;
			}
			if(k.contains("4") || k.equals("d6") || k.equals("d2"))
			{
				if(!Util.isInVTriple(this, k))
				{
					String adiacents[] = Util.getVSet(k);
					for(int i = 0; i < 3; i++)
					{
						if(b.get(adiacents[i]) == player)
							n++;
						else if(b.get(adiacents[i]) == getOppositePlayer(player))
							n--;
					}
					if(n == 2)
						count++;
				}
				n = 0;
			}
		}
		return count;
	}

	//PIECESONBOARDCURRENT - PIECESONBOARDOPPONETN
	private int nPieces() 
	{
		return getCheckersOnBoard(currentPlayer) - getCheckersOnBoard(opponentPlayer);
	}

	//BLOCKEDOPPONENTPIECES - BLOCKEDCURRENTPIECES
	private int nBlockedPieces() throws WrongPositionException 
	{
		return nBlockedPieces(opponentPlayer) - nBlockedPieces(currentPlayer);
	}
	
	//Metodo che calcola per un giocatore il numero di pezzi bloccati
	private int nBlockedPieces(Checker player) throws WrongPositionException 
	{
		int n = 0;
		
		for(Entry<String, Checker> entry : getBoard().entrySet())
		{
			if(entry.getValue() == player && blockedPiece(entry.getKey()))
				n++;
		}
		
		return n;
	}
	
	private boolean blockedPiece(String piece) throws WrongPositionException
	{
		String adiacents[] = Util.getAdiacentTiles(piece);
		for(String a : adiacents)
		{
			if(getBoard().get(a) == Checker.EMPTY)
				return false;
		}
		return true;
	}

	//CURRENTPLAYERMORRISES - OPPONENTPLAYERMORRISES
	private int nMorrises() 
	{
		return nMorrises(currentPlayer) - nMorrises(opponentPlayer);
	}
	
	private int nMorrises(Checker player)
	{
		int count = 0;
		for(Entry<String, Checker> entry : getBoard().entrySet())
		{
			String k = entry.getKey();
			if(entry.getValue() == player)
			{
				if(k.contains("d") || k.equals("b4") || k.equals("f4"))
				{
					if(Util.isInHTriple(this, k))
						count++;
				}
				if(k.contains("4") || k.equals("d6") || k.equals("d2"))
				{
					if(Util.isInVTriple(this, k))
						count++;
				}
			}
		}
		return count;
	}

	//SE NELLA MOSSA PRECEDENTE IL CURRENT PLAYER HA FATTO TRIS, RESTITUISCO 1, ALTRIMENTI 0
	private int closedMorris() 
	{
		if(getCheckersOnBoard(opponentPlayer) < previousState.getCheckersOnBoard(opponentPlayer))
			return 1;
		return 0;
	}
	
	public List<AkatsukiAction> getEmptyNeighbors(Checker player, String pos) throws WrongPositionException
	{
		List<AkatsukiAction> actions = new ArrayList<AkatsukiAction>();
		String neighbors[] = Util.getAdiacentTiles(pos);
		for(String n : neighbors)
		{
			if(getBoard().get(n) == Checker.EMPTY)
				addMoves(player, pos, n, actions);
		}
		return actions;
	}
		// metodo che mi da tutti i possibili salti che una pedina può fare
		public List<AkatsukiAction> getPossibleJump (Checker player, String from) 
		{
			List<String> possibleDestination = new ArrayList<>();
			List<AkatsukiAction> result = new ArrayList<>();
			for (String s : getBoard().keySet())
			{
				if (getBoard().get(s) == Checker.EMPTY)
					possibleDestination.add(s);
			}
			// ciclo su tutte le caselle libere: posso saltare in tutte. 
			for (String s : possibleDestination)
			{
				addMoves(currentPlayer, from, s, result);
			}
			
			return result;
		}

		// metodo che controlla se muovendo in @to si forma un mulino e in caso aggiunge la mossa di tipo "rimozione",altrimenti aggiunge mossa di solo spostamento
		public void addMoves(Checker player, String from, String to, List<AkatsukiAction> result)
		{
			if (isAMill(player, from, to))
				result.addAll(getRemovalMoves(player, from, to));
			else
				result.add(new AkatsukiAction(player, from, to));
		}
		
		//Controlla se è un tris per fase 2
		private boolean isAMill(Checker player, String from, String to) 
		{
			String[] horizontal = Util.getHSet(to);
			String[] vertical = Util.getVSet(to);
			HashMap<String, Checker> board = getBoard();
			int counterH = 0, counterV = 0;
			
			for(int i = 0; i < 3; i++)
			{
				if (!vertical[i].equals(to) && !vertical[i].equals(from) && board.get(vertical[i]) == player)
					counterV ++;
				if (!horizontal[i].equals(to) && !horizontal[i].equals(from) && board.get(horizontal[i]) == player)
					counterH++;
			}
			
			return (counterH == 2 || counterV == 2);
		}
		
	public List<AkatsukiAction> getRemovalMoves(Checker player, String from, String to)
	{
		// prima guardo tra le pedine avversarie che non sono in un mulino 
		List<String> inMill = new ArrayList<>();
		List<AkatsukiAction> result = new ArrayList<>();
		for (String s : getBoard().keySet())
		{
			if ( getBoard().get(s) == getOppositePlayer(player) )
			{
				if (!Util.hasCompletedTriple(this, s, getOppositePlayer(player))) // se non è parte di un mulino sicuramente è una pedina legittimamente rimovible
					result.add(new AkatsukiAction(player,from,to,s));
				
				else
					inMill.add(s);
						
			}
		}
		if ( result.size() > 0 )
			return result;
		
		// rimuovo, per forza di cose, pedine appartenenti a mulini avversari
		for (String s : inMill)
			result.add(new AkatsukiAction(player, from, to, s));
		return result;
	}

	@Override
	public AkatsukiState clone() 
	{
		AkatsukiState result = new AkatsukiState(me);
		
		// replicate the current board
		result.getBoard().putAll(this.getBoard());

		// update the checkers available to the players
		result.setWhiteCheckers(this.getWhiteCheckers());
		result.setBlackCheckers(this.getBlackCheckers());
		result.setWhiteCheckersOnBoard(this.getWhiteCheckersOnBoard());
		result.setBlackCheckersOnBoard(this.getBlackCheckersOnBoard());

		// update the phase
		result.setCurrentPhase(getCurrentPhase());
		
		result.setCurrentPlayer(this.getCurrentPlayer());
		result.setOpponentPlayer(this.getOpponentPlayer());
		result.setScore(this.getScore());
		
		return result;
		
	}
	
	//Metodo per ottenere un AkatsukiState a partire da uno state
	public void buildState(State s) 
	{
		setWhiteCheckers(s.getWhiteCheckers());
		setWhiteCheckersOnBoard(s.getWhiteCheckersOnBoard());
		setBlackCheckers(s.getBlackCheckers());
		setBlackCheckersOnBoard(s.getBlackCheckersOnBoard());
		setBoard(s.getBoard());
		//setCurrentPhase(s.getCurrentPhase());
	}
}
