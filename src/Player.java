import java.util.*;
import java.io.*;

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
	public Player(Game game, int index) throws IOException
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

	public Movement getGameShowMove()
	{
		return game.getShowMove();
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
						findAllSingle(ll, shrinkHand, true, hand[jokerPos], showMove);
					else
						findAllSingle(ll, shrinkHand, false, null, showMove);
					break;
					
				case Constant.PAIR:
				case Constant.TRIPLE:
				case Constant.FOUR:
					if(hasJoker)
						findContinuousWithJoker(ll, shrinkHand, type, hand[jokerPos], showMove);
					findContinuousWithoutJoker(ll, shrinkHand, type, showMove);
					break;
					
				case Constant.STRAIGHT3:
				case Constant.STRAIGHT4:
				case Constant.STRAIGHT5:
					if(hasJoker)
						findStraightWithJoker(ll, shrinkHand, type - Constant.PAIR, hand[jokerPos], showMove);
					findStraightWithoutJoker(ll, shrinkHand, type - Constant.PAIR, showMove);
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
				findStraightWithJoker(ll, shrinkHand, i, hand[jokerPos], null);
		for(int i = 3; i <=5 ; i++)
			findStraightWithoutJoker(ll, shrinkHand, i, null);

		if(hasJoker)
			for(int i = 2; i <= 4; i++)
				findContinuousWithJoker(ll, shrinkHand, i, hand[jokerPos], null);
		for(int i = 2; i <= 4; i++)
			findContinuousWithoutJoker(ll, shrinkHand, i, null);
		
		if(hasJoker)
			findAllSingle(ll, shrinkHand, true, hand[jokerPos], null);
		else
			findAllSingle(ll, shrinkHand, false, null, null);
	}
	
	private void findStraightWithoutJoker(LinkedList<Movement> ll, Card[] shrinkHand, int length, Movement showMove)
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
				{
					Movement m = new Movement(c);
					if(Rule.isLegalMove(m, showMove, false, game.getIsStartGame()))
						ll.add(m);
				}
			}
		}
	}
	private void findContinuousWithoutJoker(LinkedList<Movement> ll, Card[] shrinkHand, int length, Movement showMove)
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
								Movement m = new Movement(c);
								if(Rule.isLegalMove(m, showMove, false, game.getIsStartGame()))
									ll.add(m);
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
						{
							Movement m = new Movement(c);
							if(Rule.isLegalMove(m, showMove, false, game.getIsStartGame()))
								ll.add(m);
						}
					}
				}
				else if(length == 4)
				{
					Card[] c = new Card[4];
					for(int j = 0; j < 4; j++)
					{
						c[j] = shrinkHand[allCards[i * 4 + j]];
					}
					Movement m = new Movement(c);
					if(Rule.isLegalMove(m, showMove, false, game.getIsStartGame()))
						ll.add(m);
				}
			}
		}
	}
	private void findAllSingle(LinkedList<Movement> ll, Card[] shrinkHand, boolean hasJoker, Card joker, Movement showMove)
	{
		for(int i = 0; i < shrinkHand.length; i++)
		{
			Card[] c = new Card[1];
			c[0] = shrinkHand[i];
			Movement m = new Movement(c);
			if(Rule.isLegalMove(m, showMove, false, game.getIsStartGame()))
				ll.add(m);
		}
		if(hasJoker)
		{
			Card[] c = new Card[1];
			c[0] = joker;
			Movement m = new Movement(c);
			if(Rule.isLegalMove(m, showMove, false, game.getIsStartGame()))
				ll.add(m);
		}
	}
	
	private void findStraightWithJoker(LinkedList<Movement> ll, Card[] shrinkHand, int length, Card joker, Movement showMove)
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
				for(int j = 0; j < 13 - length + 1; j++)
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
						if(Rule.isLegalMove(m, showMove, false, game.getIsStartGame()))
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
					if(Rule.isLegalMove(m, showMove, false, game.getIsStartGame()))
						ll.add(m);
					// System.out.println(m);
				}
			}
		}
	}
	
	private void findContinuousWithJoker(LinkedList<Movement> ll, Card[] shrinkHand, int length, Card joker, Movement showMove)
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
							Movement m = new Movement(c);
							if(Rule.isLegalMove(m, showMove, false, game.getIsStartGame()))
								ll.add(m);
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
								Movement m = new Movement(c);
								if(Rule.isLegalMove(m, showMove, false, game.getIsStartGame()))
									ll.add(m);
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
							Movement m = new Movement(c);
							if(Rule.isLegalMove(m, showMove, false, game.getIsStartGame()))
								ll.add(m);
						}
					}
				}
			}
		}
	}
	
	// unit test
	public static void main(String[] args) throws IOException
	{
		Game g = new Game();
		Player p = new Player(g, 0);
		Card[] spades = new Card[13];
        Card[] hearts = new Card[13];
        Card[] diams  = new Card[13];
        Card[] clubs  = new Card[13];
        Card joker = new Card(0);
        for (int i = 0; i<13; ++i)
        {
            spades[i] = new Card(i + 1);
            hearts[i] = new Card(i + 14);
            diams[i]  = new Card(i + 27);
            clubs[i]  = new Card(i + 40);
        }
		p.getCard(spades[2]);
		p.getCard(spades[3]);
		p.getCard(diams[4]);
		p.getCard(diams[6]);
		p.getCard(spades[7]);
		p.getCard(hearts[7]);
		p.getCard(hearts[8]);
		p.getCard(spades[9]);
		p.getCard(diams[10]);
		p.getCard(spades[10]);
		p.getCard(diams[11]);
		p.getCard(clubs[0]);
		p.getCard(spades[1]);
		p.getCard(joker);
		
		Card[] showCard = new Card[4];
		showCard[0] = spades[7];
		showCard[1] = spades[8];
		showCard[2] = spades[5];
		showCard[3] = joker;
		
		System.out.println("=== genALL ===");
		p.genLegalMove(null);
		Movement showMove = new Movement(showCard);
		System.out.println("=== against " + showMove + " type: " + showMove.type + ", rank: " + showMove.biggestRank);
		p.genLegalMove(new Movement(showCard));
		
		System.out.println("End");
	}
}









