import java.util.Arrays;
import java.util.*;

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
		//System.out.println("myTurn " + index);
		Movement move = agent.decideMove();
		doMove(move);
		SystemFunc.sleep(1000);
		return true;
	}
	public boolean isFinish()
	{
		return numHandCards == 0;
	}

	public LinkedList<Movement> genLegalMove(Movement showMove)
	{
		boolean genAll = (showMove == null);
		LinkedList<Movement> ll = new LinkedList<Movement>();
		
		// not a good method to check whether there is a joker in hand, and then I can initialize the shrinkHand array
		boolean hasJoker = false;
		for(int i = 0; i < Constant.numMaxHandCard; i++)
			if(hand[i] != null && hand[i].isJoker())
			{
				hasJoker = true;
				break;
			}
		
		Card[] shrinkHand = (hasJoker)? new Card[numHandCards - 1]: new Card[numHandCards];
		int count = 0;
		for(int i = 0; i < Constant.numMaxHandCard; i++)
		{
			if(hand[i] != null && !hand[i].isJoker())
				shrinkHand[count++] = hand[i];
		}
		
		if(genAll)
		{
			findAllSingle(ll, shrinkHand);
			for(int i = 2; i <= 4; i++)
			{
				findContinuous(ll, shrinkHand, i);
				findStraight(ll, shrinkHand, i + 1);
			}
		}
		
		System.out.println(ll);
		
		return ll;
	}
	
	private void findStraight(LinkedList<Movement> ll, Card[] shrinkHand, int length)
	{
		// not consider joker 
		int[] allCards = new int[52]; // value from 3 to 15, suit from 0 to 3
		
		Arrays.fill(allCards, -1);
		for(int i = 0; i < shrinkHand.length; i++)
		{
			int value = shrinkHand[i].getValue() - 3;
			int suit = shrinkHand[i].getSuit();
			allCards[suit * 13 + value] = i;
		}
		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 13 - length + 1; j++)
			{
				// System.out.println(i + " " + j);
				Card[] c = new Card[length];
				int k;
				for(k = j; k < j + length; k++)
				{
					if(allCards[i * 13 + k] == -1)
					{
						// System.out.println("*" + k);
						j = k;
						break;
					}
					else
					{
						c[k - j] = shrinkHand[allCards[i * 13 + k]];
					}
				}
				if(k - j == length)
					ll.add(new Movement(c));
			}
		}
	}
	private void findContinuous(LinkedList<Movement> ll, Card[] shrinkHand, int length)
	{
		// not consider joker 
		int[] allCards = new int[52]; // value from 3 to 15, suit from 0 to 3
		int[] valueCount = new int[13];
		
		Arrays.fill(allCards, -1);
		Arrays.fill(valueCount, 0);
		for(int i = 0; i < shrinkHand.length; i++)
		{
			int value = shrinkHand[i].getValue() - 3;
			int suit = shrinkHand[i].getSuit();
			allCards[value * 4 + suit] = i;
			valueCount[value]++;
		}
		for(int i = 0; i < 13; i++)
		{
			if(valueCount[i] < length)
				continue;
			else
			{
				if(length == 2)
				{
					for(int j = 0; j < 4; j++)
						for(int k = j + 1; k < 4; k++)
							if(allCards[i * 4 + j] != -1 && allCards[i * 4 + k] != -1)
							{
								Card[] c = new Card[2];
								c[0] = shrinkHand[allCards[i * 4 + j]];
								c[1] = shrinkHand[allCards[i * 4 + k]];
								ll.add(new Movement(c));
							}
								
				}
				else if(length == 3)
				{
					for(int j = 0; j < 4; j++)
					{
						int count = 0;
						Card[] c = new Card[3];
						for(int k = 0; k < 4; k++)
						{
							if(k == j) continue;
							if(allCards[i * 4 + k] == -1)
								break;
							else
							{
								c[count] = shrinkHand[allCards[i * 4 + k]];
								count++;
							}
						}
						if(count == 3)
							ll.add(new Movement(c));
					}
				}
				else if(length == 4)
				{
					Card[] c = new Card[4];
					for(int j = 0; j < 4; j++)
					{
						c[j] = shrinkHand[allCards[i * 4 + j]];
					}
					ll.add(new Movement(c));
				}
			}
		}
	}
	private void findAllSingle(LinkedList<Movement> ll, Card[] shrinkHand)
	{
		for(int i = 0; i < shrinkHand.length; i++)
		{
			Card[] c = new Card[1];
			c[0] = shrinkHand[i];
			ll.add(new Movement(c));
		}
	}
	
	
	
	// unit test
	public static void main(String[] args)
	{
		Game g = new Game();
		Player p = new Player(g, 0);
		p.getCard(new Card(1));
		p.getCard(new Card(14));
		p.getCard(new Card(27));
		p.getCard(new Card(40));
		p.getCard(new Card(3));
		p.genLegalMove(null);
	}
}








