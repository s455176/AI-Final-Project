import java.util.ArrayList;

public class MiniMaxAgent extends Agent
{
	// attribute
	//private PlayerGameState playerGameState;
	private int layer;
	private ArrayList<Movement> possibleMoves;
	
	// method
	public MiniMaxAgent(Player player)
	{
		this(player, 4);
	}
	public MiniMaxAgent(Player player, int layer)
	{
		//this.playerGameState = playerGameState;
		this.player = player;
		this.layer = layer;
		possibleMoves = new ArrayList<Movement>();
	}
	
	// get next move
	public Movement decideMove()
	{
		// get PlayerGameState
		// call maxOfMiniMax(PlayerGameState, layer = 0)
		maxOfMiniMax(player.game.gameState, 0);
		
		// return random(possibleMoves)
		int length = possibleMoves.size();
		int index = (int)(Math.random() * length);
		
		return possibleMoves.get(index);
	}
	// minimax (recursive)
	// max part
	private int maxOfMiniMax(PlayerGameState currentPlayerGameState, int layer)
	{
		// if (final state: all pass)
		//     return value(currentPlayerGameState)
		if (currentPlayerGameState.isFinalState())
			return currentPlayerGameState.getValue();
	
		// get possible moves: cards, pass
		//Movement[] moves = currentPlayerGameState.getSuccessor();
		//ArrayList<Movement> moves = currentPlayerGameState.getSuccessor();
		ArrayList<int> handCardIndex;
		for (Card card : this.player.hand)
			handCardIndex.add(card.index);
		for (int cardIndex : handCardIndex)
			ArrayList<Movement> moves = Card.getCombination(cardIndex, handCardIndex, currentPlayerGameState.getCardCombination());
		
		// for each moves
		//     call minOfMiniMax(getPlayerGameState(currentPlayerGameState, move), layer)
		//     record value in list
		ArrayList<int> values = new ArrayList<int>();
		for (Movement move : moves)
		{
			int value = minOfMiniMax(currentPlayerGameState.getPlayerGameState(move), layer);
			values.add(value);
		}
		
		// max(list)
		int maximum = -999999;
		for (int value : values)
			if (value > maximum)
				maximum = value;
		
		// if (layer == 0)
		//     for each value in list
		//         record max moves in possibleMoves
		if (layer == 0)
		{
			int length = values.size();
			for (int i = 0; i < length; i++)
				if (value == maximum)
					possibleMoves.add(moves.get(i));
		}
		
		// return max
		return maximum;
	}
	// min part
	private int minOfMiniMax(PlayerGameState currentPlayerGameState, int layer)
	{
		// if (final state: all pass)
		//     return value(currentPlayerGameState)
		if (currentPlayerGameState.isFinalState())
			return currentPlayerGameState.getValue();
	
		// if (layer == this.layer)
		//     return value(currentPlayerGameState)
		if (layer == this.layer)
			return currentPlayerGameState.getValue();
	
		// get history
		//Movement[] history = currentPlayerGameState.getHistory();
		/*ArrayList<Movement> history = currentPlayerGameState.getHistory();
		
		int[] cards = new int[53];
		for (int i = 0; i < 53; i++)
			cards[i] = 1;
		for (Movement move : history)
			for (Card card : move.cards)
				cards[card.index] = 0;*/
		boolean[] cards = currentPlayerGameState.getCards();
		
		ArrayList<int> cardsLeft = new ArrayList<int>();
		for (int i = 0; i < 53; i++)
			if (cards[i] == 1)
				cardsLeft.add(i);
		
		// combination of possible cards from other players
		ArrayList<Movement[]> combinations = new ArrayList<Movement[]>();
		int cardCombination = currentPlayerGameState.getCardCombination();
		for (int cardIndex : cardsLeft)
		{
			ArrayList<Movement> moves = Card.getCombination(cardIndex, cardsLeft, cardCombination);
			for (Movement move : moves)
				combinations.add(move);
		}
		
		// for each combinations
		//     call maxOfMiniMax(getPlayersGameState(currentPlayerGameState, combination), layer + 1)
		//     min(current min, return value)
		int minValue = 999999;
		int value;
		for (int i = -3; i < combinations.size(); i++)
		{
			PlayerGameState tempPlayerGameState;
			if (i < 0)
				tempPlayerGameState = tempPlayerGameState.getPlayerGameState(new Movement());
			else
				tempPlayerGameState = currentPlayerGameState.getPlayerGameState(combinations.get(i));
			if (tempPlayerGameState.isFinalState())
			{
				tempPlayerGameState = tempPlayerGameState.getPlayerGameState(new Movement());
				tempPlayerGameState = tempPlayerGameState.getPlayerGameState(new Movement());
			}
			else
				for (int j = i + 1; j < combination.size(); j++)
				{
					if (j < 0)
						tempPlayerGameState = tempPlayerGameState.getPlayerGameState(new Movement());
					else
					{
						if (tempPlayerGameState.isLegalMove(combination.get(j)))
							tempPlayerGameState = tempPlayerGameState.getPlayerGameState(combination.get(j));
						else
							continue;
					}
					if (tempPlayerGameState.isFinalState())
						tempPlayerGameState = tempPlayerGameState.getPlayerGameState(new Movement());
					else
						for (int k = j + 1; k < combination.size(); k++)
						{
							if (k < 0)
								tempPlayerGameState = tempPlayerGameState.getPlayerGameState(new Movement());
							else
								if (tempPlayerGameState.isLegalMove(combination.get(k)))
								{
									tempPlayerGameState = tempPlayerGameState.getPlayerGameState(combination.get(k));
									value = maxOfMiniMax(tempPlayerGameState, layer + 1);
									if (value <= minValue)
										minValue = value;
								}
						}
				}
		}
		
		// return min
		return minValue;
	}
}