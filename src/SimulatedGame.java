import java.util.*;

public class SimulatedGame
{
	private GameState gs;
	private Random rn;
	private Card[][] opponentHand;
	
	public SimulatedGame(GameState gamestate)
	{
		// construct by copying the game state
		gs = new GameState(gamestate);
		rn = new Random();
		opponentHand = new Card[Constant.numPlayer][];
		Deck d = new Deck();
		d.shuffle();
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			if(i == gs.index) continue;
			
			opponentHand[i] = new Card[gs.numCards[i]];
			for(int j = 0; j < gs.numCards[i]; j++)
			{
				Card c = d.getNext();
				while(gs.history[c.getIndex()] || gs.isInPlayerHand(c.getIndex()))
					c = d.getNext();
				opponentHand[i][j] = c;
			}
		}
	}
	
	public Movement genOppoMove()
	{
		LinkedList<Movement> ll = Player.genLegalMove(gs.showMove, opponentHand[gs.next], gs.isRevo, gs.isStartGame);
		int numElement = ll.size();
		int chooseIndex = rn.nextInt(numElement);
		Movement move = ll.get(chooseIndex); 
		
		for(int i = 0; i < move.numCards; i++)
		{
			for(int j = 0; j < opponentHand[gs.next].length; j++)
			{
				if(opponentHand[gs.next][j] == null) continue;
				
				if(opponentHand[gs.next][j].getIndex() == move.cards[i].getIndex())
				{
					opponentHand[gs.next][j] = null;
					break;
				}
			}
		}
		
		return move;
	}
	
	public Movement genOppoMove(int limit)
	{
		boolean[] history = gs.history;
		Movement showMove = gs.showMove;
		boolean hasJoker = false;
		int count = 0;
		
		if(!history[0] && !gs.isInPlayerHand(0))
			hasJoker = true;
		
		for(int i = 1; i < Constant.MAX_NUM_CARD; i++)
			if(!history[i] && !gs.isInPlayerHand(i))
				count++;
		
		Card[] shrinkHand = new Card[count];
		
		int index = 0;
		for(int i = 1; i < Constant.MAX_NUM_CARD; i++)
			if(!history[i] && !gs.isInPlayerHand(i))
				shrinkHand[index++] = new Card(i);

		LinkedList<Movement> ll = new LinkedList<Movement>();
		boolean genAll = (showMove == null);
		
		if(genAll)
		{
			if(hasJoker)
				for(int i = 3; i <= Math.min(limit, 5); i++)
					Player.findStraightWithJoker(ll, shrinkHand, i, new Card(0), null, gs.isRevo, gs.isStartGame);
			for(int i = 3; i <= Math.min(limit, 5); i++)
				Player.findStraightWithoutJoker(ll, shrinkHand, i, null, gs.isRevo, gs.isStartGame);

			if(hasJoker)
				for(int i = 2; i <= Math.min(limit, 4); i++)
					Player.findContinuousWithJoker(ll, shrinkHand, i, new Card(0), null, gs.isRevo, gs.isStartGame);
			for(int i = 2; i <= Math.min(limit, 4); i++)
				Player.findContinuousWithoutJoker(ll, shrinkHand, i, null, gs.isRevo, gs.isStartGame);
			
			if(limit >= 1)
			{
				if(hasJoker)
					Player.findAllSingle(ll, shrinkHand, true, new Card(0), null, gs.isRevo, gs.isStartGame);
				else
					Player.findAllSingle(ll, shrinkHand, false, null, null, gs.isRevo, gs.isStartGame);
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
							Player.findAllSingle(ll, shrinkHand, true, new Card(0), showMove, gs.isRevo, gs.isStartGame);
						else
							Player.findAllSingle(ll, shrinkHand, false, null, showMove, gs.isRevo, gs.isStartGame);
					}
					break;
					
				case Constant.PAIR:
				case Constant.TRIPLE:
				case Constant.FOUR:
					if(limit >= type)
					{
						if(hasJoker)
							Player.findContinuousWithJoker(ll, shrinkHand, type, new Card(0), showMove, gs.isRevo, gs.isStartGame);
						Player.findContinuousWithoutJoker(ll, shrinkHand, type, showMove, gs.isRevo, gs.isStartGame);
					}
					break;
					
				case Constant.STRAIGHT3:
				case Constant.STRAIGHT4:
				case Constant.STRAIGHT5:
					if(limit >= type - 2)
					{
						if(hasJoker)
							Player.findStraightWithJoker(ll, shrinkHand, type - Constant.PAIR, new Card(0), showMove, gs.isRevo, gs.isStartGame);
						Player.findStraightWithoutJoker(ll, shrinkHand, type - Constant.PAIR, showMove, gs.isRevo, gs.isStartGame);
					}
					break;
				
				default:
					SystemFunc.throwException("Error occurs in player genLegalMove cause showMove has wrong type");
			}
			// genPass
			Card[] c = new Card[0];
			ll.add(new Movement(c));
		}
		int numElement = ll.size();
		int chooseIndex = rn.nextInt(numElement);
		
		return ll.get(chooseIndex);
	}
	
	public Movement genSelfMove()
	{
		LinkedList<Movement> ll = Player.genLegalMove(gs.showMove, gs.playerHand, gs.isRevo, gs.isStartGame);
		int numElement = ll.size();
		int chooseIndex = rn.nextInt(numElement);
		
		return ll.get(chooseIndex);
	}
	
	public int[] startSimulate()
	{
		int initRemain = gs.remainPlayer;
		int[] result = new int[]{initRemain, initRemain, initRemain, initRemain};
		int place = 1;
		int remain = gs.remainPlayer;
		
		for(int i = 0; i < Constant.numPlayer; i++)
			if(gs.numCards[i] == 0)
				result[i] = 0;
		
		while(gs.remainPlayer > 1)
		{
			int next = gs.next;
			
			if(next == gs.index)
				gs.doMove(next, genSelfMove());
			else
				gs.doMove(next, genOppoMove());
			
			// check whether a player finish
			if(remain > gs.remainPlayer)
				for(int i = 0; i < Constant.numPlayer; i++)
					if(gs.numCards[i] == 0 && result[i] == initRemain)
					{
						result[i] = place;
						place++;
						remain--;
						break;
					}
		}
		
		return result;
	}
}
