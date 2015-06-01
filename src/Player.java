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
		int jokerPos = -1;
		for(int i = 0; i < Constant.numMaxHandCard; i++)
			if(hand[i] != null && hand[i].isJoker())
			{
				jokerPos = i;
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
			genAll(ll, shrinkHand, hasJoker, jokerPos);
		else
		{
			int type = showMove.type;
			switch(type)
			{
				case Constant.SINGLE:
					if(hasJoker)
						findAllSingle(ll, shrinkHand, true, hand[jokerPos]);
					else
						findAllSingle(ll, shrinkHand, false, null);
					break;
					
				case Constant.PAIR:
				case Constant.TRIPLE:
				case Constant.FOUR:
					if(hasJoker)
						findContinuousWithJoker(ll, shrinkHand, type, hand[jokerPos]);
					findContinuousWithoutJoker(ll, shrinkHand, type);
					break;
					
				case Constant.STRAIGHT3:
				case Constant.STRAIGHT4:
				case Constant.STRAIGHT5:
					if(hasJoker)
						findStraightWithJoker(ll, shrinkHand, type - Constant.PAIR, hand[jokerPos]);
					findStraightWithoutJoker(ll, shrinkHand, type - Constant.PAIR);
					break;
				
				default:
					SystemFunc.throwException("Error occurs in player genLegalMove cause showMove has wrong type");
			}
			// genPass
			Card[] c = new Card[0];
			ll.add(new Movement(c));
		}
			
		
		System.out.println(ll);
		
		return ll;
	}

	private void genAll(LinkedList<Movement> ll, Card[] shrinkHand, boolean hasJoker, int jokerPos)
	{
		if(hasJoker)
			for(int i = 3; i <=5 ; i++)
				findStraightWithJoker(ll, shrinkHand, i, hand[jokerPos]);
		for(int i = 3; i <=5 ; i++)
			findStraightWithoutJoker(ll, shrinkHand, i);

		if(hasJoker)
			for(int i = 2; i <= 4; i++)
				findContinuousWithJoker(ll, shrinkHand, i, hand[jokerPos]);
		for(int i = 2; i <= 4; i++)
			findContinuousWithoutJoker(ll, shrinkHand, i);
		
		if(hasJoker)
			findAllSingle(ll, shrinkHand, true, hand[jokerPos]);
		else
			findAllSingle(ll, shrinkHand, false, null);
	}
	
	private void findStraightWithoutJoker(LinkedList<Movement> ll, Card[] shrinkHand, int length)
	{
		// not consider joker 
		int[] allCards = new int[52]; // value from 3 to 15, suit from 0 to 3
		int[] suitCount = new int[4];
		
		Arrays.fill(allCards, -1);
		Arrays.fill(suitCount, 0);
		for(int i = 0; i < shrinkHand.length; i++)
		{
			int value = shrinkHand[i].getValue() - 3;
			int suit = shrinkHand[i].getSuit();
			allCards[suit * 13 + value] = i;
			suitCount[suit]++;
		}
		for(int i = 0; i < 4; i++)
		{
			if(suitCount[i] < length)
				continue;
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
	private void findContinuousWithoutJoker(LinkedList<Movement> ll, Card[] shrinkHand, int length)
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
	private void findAllSingle(LinkedList<Movement> ll, Card[] shrinkHand, boolean hasJoker, Card joker)
	{
		for(int i = 0; i < shrinkHand.length; i++)
		{
			Card[] c = new Card[1];
			c[0] = shrinkHand[i];
			ll.add(new Movement(c));
		}
		if(hasJoker)
		{
			Card[] c = new Card[1];
			c[0] = joker;
			ll.add(new Movement(c));
		}
	}
	
	private void findStraightWithJoker(LinkedList<Movement> ll, Card[] shrinkHand, int length, Card joker)
	{
		// not consider joker 
		int[] allCards = new int[52]; // value from 3 to 15, suit from 0 to 3
		int[] suitCount = new int[4];
		
		Arrays.fill(allCards, -1);
		Arrays.fill(suitCount, 0);
		for(int i = 0; i < shrinkHand.length; i++)
		{
			int value = shrinkHand[i].getValue() - 3;
			int suit = shrinkHand[i].getSuit();
			allCards[suit * 13 + value] = i;
			suitCount[suit]++;
		}
		// enumerate all joker put in middle case 
		// System.out.println("Joker in middle");
		for(int l = 1; l < length - 1; l++)
		{
			for(int i = 0; i < 4; i++)
			{			
				for(int j = 0; j < 13; j++)
				{
					// System.out.println(i + " " + j);
					Card[] c = new Card[length];
					int k;
					for(k = j; k < j + length; k++)
					{
						if(k == j + l)
						{
							c[k - j] = joker;
							continue;
						}
						if(allCards[i * 13 + k] == -1)
							break;
						else
							c[k - j] = shrinkHand[allCards[i * 13 + k]];
					}
					if(k - j == length)
					{
						Movement m = new Movement(c);
						ll.add(m);
						// System.out.println(m);
					}
				}
			}
		}
		// enumerate joker put in side, so from straight length - 1 to length
		// System.out.println("Joker in side");
		int newLength = length - 1;
		for(int i = 0; i < 4; i++)
		{
			if(suitCount[i] < newLength)
				continue;
			for(int j = 0; j < 13 - newLength + 1; j++)
			{
				// System.out.println(i + " " + j);
				Card[] c = new Card[newLength + 1];
				int k;
				for(k = j; k < j + newLength; k++)
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
				if(k - j == newLength)
				{
					c[newLength] = joker;
					Movement m = new Movement(c);
					ll.add(m);
					// System.out.println(m);
				}
			}
		}
	}
	
	private void findContinuousWithJoker(LinkedList<Movement> ll, Card[] shrinkHand, int length, Card joker)
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
			if(valueCount[i] < length - 1)
				continue;
			else
			{
				if(length == 2)
				{
					for(int j = 0; j < 4; j++)
					{
						if(allCards[i * 4 + j] != -1)
						{
							Card[] c = new Card[2];
							c[0] = shrinkHand[allCards[i * 4 + j]];
							c[1] = joker;
							ll.add(new Movement(c));
						}
					}
				}
				else if(length == 3)
				{
					for(int j = 0; j < 4; j++)
						for(int k = j + 1; k < 4; k++)
							if(allCards[i * 4 + j] != -1 && allCards[i * 4 + k] != -1)
							{
								Card[] c = new Card[3];
								c[0] = shrinkHand[allCards[i * 4 + j]];
								c[1] = shrinkHand[allCards[i * 4 + k]];
								c[2] = joker;
								ll.add(new Movement(c));
							}
				}
				else if(length == 4)
				{
					for(int j = 0; j < 4; j++)
					{
						int count = 0;
						Card[] c = new Card[4];
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
						{
							c[count] = joker;
							ll.add(new Movement(c));
						}
					}
				}
			}
		}
	}
	
	// unit test
	public static void main(String[] args)
	{
		Game g = new Game();
		Player p = new Player(g, 0);
		p.getCard(new Card(3));
		p.getCard(new Card(4));
		p.getCard(new Card(0));
		p.getCard(new Card(6));
		p.getCard(new Card(7));
		p.getCard(new Card(9));
		p.getCard(new Card(10));
		p.getCard(new Card(8));
		p.getCard(new Card(21));
		p.getCard(new Card(34));
		p.getCard(new Card(47));
		p.genLegalMove(null);
		System.out.println("End");
	}
}








