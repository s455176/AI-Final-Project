import java.util.Arrays;

public class Player 
{
	public Card[] hand;
	public int numHandCards;
	private Game game;
	private TestAgent agent;
	
	
	public Player(Game game)
	{
		hand = new Card[Constant.numMaxHandCard];
		reset();
		this.game = game;
		agent = new TestAgent(this);
	}
	public void reset()
	{
		for(int i = 0; i < Constant.numMaxHandCard; i++)
		{
			hand[i] = null;
		}
		numHandCards = 0;
	}
	public void getCard(Card card)
	{
		if(numHandCards < Constant.numMaxHandCard)
		{
			hand[numHandCards] = card;
			numHandCards++;
		}
	}
	public void sortHandCard()
	{
		Arrays.sort(hand, 0, numHandCards);
	}
	public boolean doMove(Movement move)
	{
		// remove the cards in Movement from hand
		for(int i = 0; i < Constant.numMaxHandCard; i++)
		{
			if(hand[i] == null) continue;
			
			// hand[i] != null
			for(int j = 0; j < move.numCards; j++)
			{
				if(hand[i].index == move.cards[j].index)
				{
					hand[i] = null;
					numHandCards--;
					break;
				}
			}
		}
		game.doMove(move);
		return true;
	}
	public boolean takeTurn()
	{
		System.out.println("myTurn");
		Movement move = agent.decideMove();
		doMove(move);
		try
		{
		    Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
		    e.printStackTrace();
		    System.exit(-1);
		}
		return true;
	}
}
