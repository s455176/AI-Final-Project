import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Card extends JLabel implements Comparable<Card>
{
	// attribute
	public int suit;
	public int rank;
	public int index;
	
	// method 
	public Card()
	{
		suit = -1;
		rank = -1;
		index = -1;
	}
	public Card(int suit, int rank)
	{
		if(suit == 0 && rank == 0)
		{
			this.suit = 0;
			this.rank = 0;
			this.index = 0;
		}
		else
		{
			this.suit = suit;
			this.rank = rank;
			this.index = rank + suit * 13;
		}
	}
	public Card(int index)
	{
		this.index = index;
		this.suit = (index - 1) / 13;
		this.rank = this.index - this.suit * 13;
	}
	@Override
	public String toString()
	{
		String s = new String();
		
		if(suit == 0 && rank == 0)
			return "JOKER";
		
		switch(suit)
		{
		case Constant.DIAMOND:
			s = "\u2666";
			break;
		case Constant.CLUB:
			s = "\u2663";
			break;
		case Constant.HEART:
			s = "\u2665";
			break;
		case Constant.SPADE:
			s = "\u2660";
			break;
		}
		return s + this.rank;
	}
	
	public boolean isJoker()
	{
		return suit == 0 && rank == 0;
	}
	
	public int getSuit()
	{
		return suit;
	}
	public int getRank()
	{
		return rank;
	}
	public int getValue()
	{
		if (rank == 0 && suit == 0)
		{
			return 16;
		}
		return (rank < 3) ? rank + 13 : rank;
	}
	public int getIndex()
	{
		return index;
	}
	public boolean hasSameRank(Card c)
	{
		return (this.rank == c.rank) ? true : false;
	}
	public boolean hasSameSuit(Card c)
	{
		return (this.suit == c.suit) ? true : false;
	}
	public int compareTo(Card c)
	{
		//return (this.index > c.index)? 1: -1;
        return (this.getValue() > c.getValue()) ? 1 : -1;
	}
	
	// B01902018
	public static ArrayList<Movement> getCombination(int startIndex, ArrayList<Integer> cardsIndex, int cardCombination)
	{
		ArrayList<Movement> moves = new ArrayList<Movement>();
		int temp;
		int num;
		switch(cardCombination)
		{
			case -1:
			case 1:
				Card[] temp1 = new Card[1];
				temp1[0] = new Card(startIndex);
				moves.add(new Movement(temp1));
				if (cardCombination > -1)
					break;
			case 2:
				Card[] temp2 = new Card[2];
				temp2[0] = new Card(startIndex);
				temp2[1] = new Card();
				for (int i = temp2[0].suit + 1; i < 4; i++)
				{
					temp = temp2[0].rank + i * 13;
					if (cardsIndex.contains(temp))
					{
						temp2[1].changeCard(temp);
						moves.add(new Movement(temp2));
					}
				}
				temp = 52;
				if (cardsIndex.contains(temp))
				{
					temp2[1].changeCard(temp);
					moves.add(new Movement(temp2));
				}
				if (cardCombination > -1)
					break;
			case 3:
				Card[] temp3 = new Card[3];
				temp3[0] = new Card(startIndex);
				temp3[1] = new Card();
				temp3[2] = new Card();
				for (int i = temp3[0].suit + 1; i < 4; i++)
				{
					temp = temp3[0].rank + i * 13;
					if (cardsIndex.contains(temp))
					{
						temp3[1].changeCard(temp);
						for (int j = i + 1; j < 4; j++)
						{
							temp = temp3[0].rank + j * 13;
							if (cardsIndex.contains(temp))
							{
								temp3[2].changeCard(temp);
								moves.add(new Movement(temp3));
							}
						}
						temp = 52;
						if (cardsIndex.contains(temp))
						{
							temp3[2].changeCard(temp);
							moves.add(new Movement(temp3));
						}
					}
				}
				if (cardCombination > -1)
					break;
			case 4:
				Card[] temp4 = new Card[4];
				temp4[0] = new Card(startIndex);
				num = 1;
				for (int i = temp4[0].suit; i < 4; i++)
				{
					temp = temp4[0].rank + i * 13;
					if (cardsIndex.contains(temp))
					{
						temp4[num] = new Card(temp);
						num++;
					}
					if (num == 4)
						break;
					temp = 52;
					if (cardsIndex.contains(temp))
						temp4[num] = new Card(temp);
				}
				if (num == 4)
					moves.add(new Movement(temp4));
				if (cardCombination > -1)
					break;
			case 5:
				num = 1;
				temp = startIndex + 1;
				for (int i = 0; i < 2; i++)
				{
					if (cardsIndex.contains(temp))
						num++;
					temp++;
				}
				if (num == 3)
				{
					Card[] temp5 = new Card[3];
					for (int i = 0; i < 3; i++)
						temp5[i] = new Card(startIndex + i);
					moves.add(new Movement(temp5));
				}
				// ERROR: Undefind in this domain.
				//moves.add(new Movement(temp5));
				if (cardCombination > -1)
					break;
			case 6:
				num = 1;
				temp = startIndex + 1;
				for (int i = 0; i < 3; i++)
				{
					if (cardsIndex.contains(temp))
						num++;
					temp++;
				}
				if (num == 4)
				{
					Card[] temp6 = new Card[4];
					for (int i = 0; i < 4; i++)
						temp6[i] = new Card(startIndex + i);
					moves.add(new Movement(temp6));
				}
				// ERROR: Undefind in this domain.
				//moves.add(new Movement(temp6));
				if (cardCombination > -1)
					break;
			case 7:
				num = 1;
				temp = startIndex + 1;
				for (int i = 0; i < 4; i++)
				{
					if (cardsIndex.contains(temp))
						num++;
					temp++;
				}
				if (num == 5)
				{
					Card[] temp7 = new Card[5];
					for (int i = 0; i < 5; i++)
						temp7[i] = new Card(startIndex + i);
					moves.add(new Movement(temp7));
				}
				// ERROR: Undefind in this domain.
				//moves.add(new Movement(temp7));
				break;
		}
		
		return moves;
	}
	private void changeCard(int index)
	{
		this.index = index;
		this.suit = (index - 1) / 13;
		this.rank = this.index - this.suit * 13;
	}
}
