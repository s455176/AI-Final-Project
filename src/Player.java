import java.util.Arrays;

public class Player 
{
	public Card[] hand;
	public int numHandCards;
	private Game game;
	private TestAgent agent;
	private int index;
	
	/**
	 * Constructor of the Player
	 * 
	 * @param game the game which player is playing 
	 * @param index the index of the player in the game
	 */
	public Player(Game game, int index)
	{
		this.index = index;
		hand = new Card[Constant.numMaxHandCard];
		reset();
		this.game = game;
		agent = new TestAgent(this);
	}
	/** 
	 * reset the player' s attribute
	 */
	public void reset()
	{
		for(int i = 0; i < Constant.numMaxHandCard; i++)
		{
			hand[i] = null;
		}
		numHandCards = 0;
	}
	/**
	 * assign the card to the player, called by game.deal()
	 * 
	 * @param card the card assign to the player
	 */
	public void getCard(Card card)
	{
		if(numHandCards < Constant.numMaxHandCard)
		{
			hand[numHandCards] = card;
			numHandCards++;
		}
	}
	/**
	 * sort the cards in the hand according to the cards' indexes
	 */
	public void sortHandCard()
	{
		Arrays.sort(hand, 0, numHandCards);
	}
	/**
	 * the player will call this method to do the movement 
	 * 
	 * @param move the Movement will be done 
	 * @return just return true
	 */
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
		game.doMove(move, index);
		return true;
	}
	/**
	 * called by game.run() when it is the player' s turn, first call the agent to decide the movement, 
	 * second call the doMove() to pass the movement, then sleep for a while just make sure that the window will
	 * not change too fast, so I can see clearly what' s happening
	 * 
	 * @return just return true
	 */
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
