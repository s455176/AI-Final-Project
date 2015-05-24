import java.util.Arrays;

public class Player 
{
	private Card[] hand;
	private int numHandCards;
	private Game game;
	
	public Player(Game game)
	{
		hand = new Card[Constant.numMaxHandCard];
		numHandCards = 0;
		this.game = game;
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
		game.doMove(move);
		return true;
	}
}
