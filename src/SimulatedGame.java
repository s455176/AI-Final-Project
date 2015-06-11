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
