import java.util.Arrays;

public class Movement
{
	public Card[] cards;
	public int numCards;
	public int type;
	/**
	 * Constructor of the Movement
	 * 
	 * @param cards the cards which will be formed as a combination 
	 */
	public Movement(Card[] cards)
	{
		if(cards.length < 0 || cards.length > Constant.maxMovementCard)
		{
			type = Constant.ILLEGAL;
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
			}
			Arrays.sort(this.cards, 0, this.numCards);
			this.type = Rule.combination(getCards());
		}
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
		String s = "";
		for(int i = 0; i < numCards; i++)
		{
			s = s + cards[i].toString() + " ";
		}
		s = s + type + "\n";
		return s;
	}
}





