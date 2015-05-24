import java.io.*;

public class Movement
{
	public Card[] cards;
	public int numCards;
	/**
	 * Constructor of the Movement
	 * 
	 * @param cards the cards which will be formed as a combination 
	 */
	public Movement(Card[] cards)
	{
		if(cards.length < 0 || cards.length > Constant.maxMovementCard)
		{
			cards = null;
			numCards = 0;
			try
			{
				throw new Exception("Invalid Movement");
			}
			catch(Exception e)
			{
				e.printStackTrace(new PrintStream(System.err));
				System.exit(-1);
			}
		}
		else
		{
			this.cards = new Card[cards.length];
			this.numCards = cards.length;
			for(int i = 0; i < numCards; i++)
			{
				this.cards[i] = cards[i];
			}
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
}
