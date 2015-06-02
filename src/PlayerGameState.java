public class PlayerGameState
{
	//private Movement[] history;
	private ArrayList<Movement> history;
	private int cardCombination;
	private boolean[] cards;
	private int pass;
	private boolean revolution;
	private boolean tempRev;
	
	public PlayerGameState()
	{
		history = new ArrayList<Movement>();
		cardCombination = NEWROUND;
		cards = new boolean[53];
		for (int i = 0; i < 53; i++)
			cards[i] = true;
		pass = 0;
		revolution = false;
		tempRev = false;
	}
	public PlayerGameState(ArrayList<Movement> history, int cardCombination, boolean[] cards, int pass, boolean revolution, boolean tempRev)
	{
		this.history = new ArrayList<Movement>();
		for (Movement move : history)
			this.history.add(move);
		this.cardCombination = cardCombination;
		this.cards = new boolean[53];
		for (int i = 0; i < 53; i++)
			this.cards[i] = cards[i];
		this.pass = pass;
		this.revolution = revolution;
		this.tempRev = tempRev;
	}
	
	public boolean[] getCards()
	{
		return this.cards;
	}
	public int getCardCombination()
	{
		return this.cardCombination;
	}
	public boolean isLegalMove(Movement movement)
	{
		for (int i = 0; i < movement.numCards; i++)
			if (cards[movement.cards[i].index])
				return false;
			
		boolean isStartGame;
		if (this.cardCombination == -1)
			isStartGame = true;
		else
			isStartGame = false;
		if (Rule.isLegalMove(movement, this.history.get(this.history.size() - 1), this.revolution, isStartGame))
			return true;

		return false;
	}
	public boolean isFinalState()
	{
		if (this.cardCombination == -1)
			return true;
		int num = 0;
		for (int i = 0; i < 53; i++)
			if (!this.cards[i])
				num++;
		if (num == 53)
			return true;
		
		return false;
	}
	public int getValue();
	public PlayerGameState getPlayerGameState(Movement movement)
	{
		PlayerGameState playerGameState = new PlayerGameState(this.history, this.cardCombination, this.cards, this.pass, this.revolution, this.tempRev);
		playerGameState.history.add(movement);
		if (movement.numCards == 0)
			playerGameState.pass += 1;
		else
			playerGameState = 0;
		if (playerGameState.pass == 3)
		{
			playerGameState.cardCombination = -1;
			if (playerGameState.tempRev) {
				playerGameState.revolution = !playerGameState.revolution;
				playerGameState.tempRev = false;
			}
		}
		for (Card card : movement.cards)
			playerGameState.cards[card.index] = false;
		
		return playerGameState;
	}
}