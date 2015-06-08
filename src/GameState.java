import java.io.*;
import java.util.*;

public class GameState
{
	// attribute of normal game need
	public int index;
	public Card[] playerHand;
	public Movement showMove;
	public boolean isRevo, is11Revo, isStartGame;
	public int[] numCards;
	
	// attribute of the control flow 
	public int next;
	public boolean[] history; // record the card that is shown, indexing by the card's index (size is the Constant.MAX_NUM_CARD)
	public int passCount, remainPlayer;
	public boolean lastRoundRevo;
	
	// used for a new game and assume dealing starts from player 0
	public GameState(Card[] hand, int n, int indx)
	{
		this(hand, null, false, false, n, initHistory(), indx, true, 0, 4, new int[]{Constant.numMaxHandCard - 1, 
			Constant.numMaxHandCard - 2, Constant.numMaxHandCard - 2, Constant.numMaxHandCard - 2}, false);
	}
	
	public GameState(GameState gs)
	{
		this(gs.playerHand, gs.showMove, gs.isRevo, gs.is11Revo, gs.next, gs.history, gs.index, gs.isStartGame, 
				gs.passCount, gs.remainPlayer, gs.numCards, gs.lastRoundRevo);
	}
	
	
	public GameState(Card[] hand, Movement show, boolean revo, boolean revo11, int n, boolean[] record, int indx, 
			boolean start, int pass, int remain, int[] num, boolean lastRevo)
	{
		// clone the hand
		playerHand = new Card[Constant.numMaxHandCard];
		for(int i = 0; i < hand.length; i++)
		{
			if(hand[i] != null)
				playerHand[i] = new Card(hand[i].getIndex());
			else 
				playerHand[i] = null;
		}
		
		// clone the showMove
		if(showMove == null)
			showMove = null;
		else
		{
			Card[] c = new Card[show.numCards];
			for(int i = 0; i < show.numCards; i++)
				c[i] = new Card(show.cards[i].getIndex());
			showMove = new Movement(c);
		}
		
		// copy the value of the primitive type variable
		isRevo = revo;
		is11Revo = revo11;
		isStartGame = start;
		next = n;
		index = indx;
		passCount = pass;
		remainPlayer = remain;
		lastRoundRevo = lastRevo;
		
		// clone the history
		history = new boolean[Constant.MAX_NUM_CARD];
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
			history[i] = record[i];
		
		// copy the # of cards for each player
		numCards = new int[num.length];
		for(int i = 0; i < num.length; i++)
			numCards[i] = num[i];
	}
	
	public static boolean[] initHistory()
	{
		boolean[] temp = new boolean[Constant.MAX_NUM_CARD];
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
			temp[i] = false;
		
		return temp;
	}
	
	public boolean doMove(int playerIndex, Movement move)
	{	
		// not the player_playerIndex's turn
		if(playerIndex != next)
			return false;
		
		// check is the shown card is shown again (if move is pass, numCard == 0)
		for(int i = 0; i < move.numCards; i++)
			if(history[move.cards[i].getIndex()])
				return false;
		
		// check is the move legal against the last move
		if(!Rule.isLegalMove(move, showMove, isRevo, isStartGame))
			return false;
		
		// the move is legal move do the move (if move is pass, numCard == 0)  
		for(int i = 0; i < move.numCards; i++)
		{
			history[move.cards[i].getIndex()] = true;
			if(playerIndex == index)
			{
				for(int j = 0; j < Constant.numMaxHandCard; j++)
				{
					if(playerHand[j] == null) continue;
					
					if(playerHand[j].getIndex() == move.cards[i].getIndex())
					{
						playerHand[j] = null;
						break;
					}
				}
			}
			numCards[playerIndex]--;
		}
		
		// continue the game flow
		if(move.type == Constant.PASS)
		{
			passCount++;
			// all other player pass
			if(passCount == remainPlayer - 1)
			{
				// start the new round
				showMove = null;
				next = findNext(playerIndex);
				resetNewRoundRevo();
			}
			else
				next = findNext(playerIndex);
		}
		else
		{
			showMove = move;
			passCount = 0;
			// check revolution
			if(move.has11Revo)
			{
				is11Revo = true;
				isRevo = !isRevo;
			}
			else if(move.is4CardsRevo)
			{
				is11Revo = false;
				isRevo = !isRevo;
			}
			// the cur player finish
			if(numCards[playerIndex] == 0)
			{
				showMove = null;
				next = findNext(playerIndex);
				resetNewRoundRevo();
				remainPlayer--;
			}
			// the cur move has 8-cut
			else if(move.has8Cut)
			{
				showMove = null;
				next = playerIndex;
				resetNewRoundRevo();
			}
			else
				next = findNext(playerIndex);
		}
		isStartGame = false;
		
		
		System.out.println(this);
		
		return true;
	}
	
	public String toString()
	{
		String s = "=========================================\n";
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			s += "player " + i + ": " + numCards[i] + ", ";
		}
		s += "\n";
		if(showMove != null)
			s += "showMove: " + showMove.toString();
		s += "isRevo: " + isRevo + ", is11Revo: " + is11Revo + "\n";
		s += "nextPlayer: " + next + "\n";
		
		s += "++++++++++++++++++++++++++++++++++++++++++++";
		return s;
	}
	
	
	private void resetNewRoundRevo()
	{
		if(is11Revo)
		{
			is11Revo = false;
			isRevo = lastRoundRevo;
		}
		lastRoundRevo = isRevo;
	}
	
	private int findNext(int cur)
	{
		int nextStartTurn = -1;
		int i = (cur + 1) % Constant.numPlayer;
		while(i != cur)
		{
			if(numCards[i] != 0)
			{
				nextStartTurn = i;
				break;
			}
			i = (i + 1) % Constant.numPlayer;
		}
		return nextStartTurn;
	}
}


















