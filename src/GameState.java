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
	public GameState(Card[] hand, int n, int indx, int p0, int p1, int p2, int p3)
	{
		/*
		 *  playerHand is input, showMove is null, isRevo and is11Revo are false, next is input n, history is initial,
		 *  player index of the cur GameState is input indx, isGameStart is true, passCount is 0, remainPlayer is 4, 
		 *  cardsNum in each players' hands are as following, lastRound is not Revo 
		 */
		this(hand, null, false, false, n, initHistory(), indx, true, 0, 4, new int[]{p0, p1, p2, p3}, false);
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
		if(show == null)
			showMove = null;
		else
			showMove = new Movement(show);
		
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
		// System.out.println(move);
		if(playerIndex != next)
		{
			SystemFunc.throwException("GameState doMove: not the player_playerIndex's turn, playerIndex: " + 
					playerIndex + ", next is " + next);
			return false;
		}
		
		// check is the shown card is shown again (if move is pass, numCard == 0)
		for(int i = 0; i < move.numCards; i++)
			if(history[move.cards[i].getIndex()])
			{
				SystemFunc.throwException("GameState doMove: Card has been shown before, player " + playerIndex +
						", card: " + new Card(move.cards[i].getIndex()));
				return false;
			}
		
		// check is the move legal against the last move
		if(!Rule.isLegalMove(move, showMove, isRevo, isStartGame))
		{
			SystemFunc.throwException("GameState doMove: Move not legal, player " + playerIndex);
			return false;
		}
		
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
		
		// check numCards for debugging check 
		for(int i = 0; i < Constant.numPlayer; i++)
			if(numCards[i] < 0 || numCards[i] > Constant.numMaxHandCard)
				SystemFunc.throwException("Invalid num of Cards for player " + i + " with " + numCards[i] + " cards "
						+ " thrown in GameState doMove");
		
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
		
		// System.out.println(this);
		
		return true;
	}
	
	public String toString()
	{
		String s = "=================" + index + "========================\n";
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			s += "player " + i + ": " + numCards[i] + ", ";
		}
		s += "\n";
		for(int i = 0; i < Constant.numMaxHandCard; i++)
			if(playerHand[i] != null)
				s += playerHand[i].toString() + " ";
		s += "\n";
		if(showMove != null)
			s += "showMove: " + showMove.toString();
		s += "isRevo: " + isRevo + ", is11Revo: " + is11Revo + "\n";
		s += "nextPlayer: " + next + "\n";
		
		s += "++++++++++++++++++++++++++++++++++++++++++++";
		return s;
	}

	public boolean isInPlayerHand(int index)
	{
		for(int i = 0; i < Constant.numMaxHandCard; i++)
		{
			if(playerHand[i] == null) continue;
			else if(playerHand[i].getIndex() == index)
				return true;
		}
		return false;
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
//		if(nextStartTurn == -1)
//		{
//			for(int j = 0; j < Constant.numPlayer; j++)
//			{
//				System.out.print(numCards[j] + " ");
//			}
//			SystemFunc.throwException("\ngame should be finish, error in find next player in GameState");
//		}
		return nextStartTurn;
	}
	
	public boolean isJokerShown()
	{
		return this.history[0];
	}
	
	public LinkedList<Movement> genMove(int playerIndex)
	{
		if(playerIndex == this.index)
		{
			LinkedList<Movement> ll = Player.genLegalMove(this.showMove, this.playerHand, this.isRevo, this.isStartGame);
			return ll;
		}
		else
		{
			LinkedList<Movement> ll = genOppoMove(numCards[playerIndex]);
			return ll;
		}		
	}
	
	public LinkedList<Movement> genOppoMove(int limit)
	{
		boolean[] history = this.history;
		Movement showMove = this.showMove;
		boolean hasJoker = false;
		int count = 0;
		
		if(!history[0] && !this.isInPlayerHand(0))
			hasJoker = true;
		
		for(int i = 1; i < Constant.MAX_NUM_CARD; i++)
			if(!history[i] && !this.isInPlayerHand(i))
				count++;
		
		Card[] shrinkHand = new Card[count];
		
		int index = 0;
		for(int i = 1; i < Constant.MAX_NUM_CARD; i++)
			if(!history[i] && !this.isInPlayerHand(i))
				shrinkHand[index++] = new Card(i);

		LinkedList<Movement> ll = new LinkedList<Movement>();
		boolean genAll = (showMove == null);
		
		if(genAll)
		{
			if(hasJoker)
				for(int i = 3; i <= Math.min(limit, 5); i++)
					Player.findStraightWithJoker(ll, shrinkHand, i, new Card(0), null, this.isRevo, this.isStartGame);
			for(int i = 3; i <= Math.min(limit, 5); i++)
				Player.findStraightWithoutJoker(ll, shrinkHand, i, null, this.isRevo, this.isStartGame);

			if(hasJoker)
				for(int i = 2; i <= Math.min(limit, 4); i++)
					Player.findContinuousWithJoker(ll, shrinkHand, i, new Card(0), null, this.isRevo, this.isStartGame);
			for(int i = 2; i <= Math.min(limit, 4); i++)
				Player.findContinuousWithoutJoker(ll, shrinkHand, i, null, this.isRevo, this.isStartGame);
			
			if(limit >= 1)
			{
				if(hasJoker)
					Player.findAllSingle(ll, shrinkHand, true, new Card(0), null, this.isRevo, this.isStartGame);
				else
					Player.findAllSingle(ll, shrinkHand, false, null, null, this.isRevo, this.isStartGame);
			}
		}
		else
		{
			int type = showMove.type;
			switch(type)
			{
				case Constant.SINGLE:
					if(limit >= type)
					{
						if(hasJoker)
							Player.findAllSingle(ll, shrinkHand, true, new Card(0), showMove, this.isRevo, this.isStartGame);
						else
							Player.findAllSingle(ll, shrinkHand, false, null, showMove, this.isRevo, this.isStartGame);
					}
					break;
					
				case Constant.PAIR:
				case Constant.TRIPLE:
				case Constant.FOUR:
					if(limit >= type)
					{
						if(hasJoker)
							Player.findContinuousWithJoker(ll, shrinkHand, type, new Card(0), showMove, this.isRevo, this.isStartGame);
						Player.findContinuousWithoutJoker(ll, shrinkHand, type, showMove, this.isRevo, this.isStartGame);
					}
					break;
					
				case Constant.STRAIGHT3:
				case Constant.STRAIGHT4:
				case Constant.STRAIGHT5:
					if(limit >= type - 2)
					{
						if(hasJoker)
							Player.findStraightWithJoker(ll, shrinkHand, type - Constant.PAIR, new Card(0), showMove, this.isRevo, this.isStartGame);
						Player.findStraightWithoutJoker(ll, shrinkHand, type - Constant.PAIR, showMove, this.isRevo, this.isStartGame);
					}
					break;
				
				default:
					SystemFunc.throwException("Error occurs in player genLegalMove cause showMove has wrong type");
			}
			// genPass
			Card[] c = new Card[0];
			ll.add(new Movement(c, this.isRevo));
		}
		
		return ll;
	}
}


















