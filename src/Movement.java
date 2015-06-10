import java.util.Arrays;

public class Movement
{
	public Card[] cards;
	public int numCards;
	public int type;
	public int biggestRank;
	public boolean has8Cut, has11Revo, is4CardsRevo;
	
	/**
	 * Constructor of the Movement
	 * 
	 * @param cards the cards which will be formed as a combination 
	 */
	public Movement(Card[] cards)
	{
		if(cards.length < 0 || cards.length > Constant.maxMovementCard)
		{
			for(int i = 0; i < cards.length; i++)
				System.out.print(cards[i] + " ");
			System.out.println();
			has11Revo = false;
			is4CardsRevo = false;
			has8Cut = false;
			type = Constant.ILLEGAL;
			biggestRank = -1;
			cards = null;
			numCards = 0;
			SystemFunc.throwException("Invalid Movement");
		}
		else
		{
			this.cards = new Card[cards.length];
			this.numCards = cards.length;
			for(int i = 0; i < numCards; i++)
			{
				this.cards[i] = cards[i];
				if(this.cards[i] == null)
				{
					SystemFunc.throwException("null card in Movement construct");
				}
				
				if((this.cards[i].getIndex() - 1) % 13 + 1 == 8)
					has8Cut = true;
				if((this.cards[i].getIndex() - 1) % 13 + 1 == 11)
					has11Revo = true;
			}
			is4CardsRevo = (this.numCards >= 4);
			Arrays.sort(this.cards, 0, this.numCards);
			Card[] tempCards = getCards();
			this.type = Rule.combination(tempCards);
			if(type != Constant.PASS)
			{
				int[] tempValue = Rule.toggleValue(tempCards);
				this.biggestRank = tempValue[tempValue.length - 1];
			}
		}
	}
	
	// B01902018
	public Movement()
	{
		cards = null;
		numCards = 0;
	}
	
	/**
	 * get the cards of the movement 
	 * 
	 * @return the cards in of the Movement 
	 */
	public Card[] getCards()
    {
        Card[] copyArray = new Card[cards.length];
        System.arraycopy(cards, 0, copyArray, 0, cards.length);
        return copyArray;
    }
	public String toString()
	{
		if(type == Constant.PASS)
			return "Pass";
		
		String s = "";
		for(int i = 0; i < numCards; i++)
		{
			s = s + cards[i].toString() + " ";
		}
		s = s + type + "\n";
		return s;
	}
	public boolean isEqual(Movement m)
	{
		if(this.numCards != m.numCards)
			return false;
		Card[] c1 = this.getCards();
		Card[] c2 = m.getCards();
		Arrays.sort(c1);
		Arrays.sort(c2);
		for(int i = 0; i < c1.length; i++)
		{
			if(c1[i].index != c2[i].index)
				return false;
		}
		return true;
	}
}





