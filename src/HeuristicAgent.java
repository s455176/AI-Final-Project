import java.util.*;
import java.util.Arrays;

public class HeuristicAgent extends Agent
{
        private Player player;
	public HeuristicAgent(Player player)
	{
		this.player = player;
	}
	
	public Movement decideMove()
	{
	        GameState gs = new GameState(player.hand, player.getGameShowMove(),
                                player.getIsRevo(), player.getIs11Revo(), player.index,
                                player.getGameHistory(), player.index,
                                player.getIsStartGame(), player.getPassCount(),
                                player.getNumRemainPlayer(), player.getPlayerNumCards(),
                                player.getIsRevoBeforeRound());
        
                //LinkedList ll = gs.genMove(player.index);
                
                int numCards = player.numHandCards;
                Card[] handWithNull = player.hand;
                Card[] hand = new Card[numCards];
                // Remove null in hand.
                for (int i = 0; i < handWithNull.length; ++i) {
                        if (handWithNull == null) continue;
                        hand[i] = handWithNull[i];
                }
                // All the possible move.
                LinkedList ll = player.genLegalMove(player.getGameShowMove(),
                                player.hand, player.getIsRevo(),
                                player.getIsStartGame()
                                );

                // Only choice is pass.
                if (ll.size() == 1) {
                        return new Movement(new Card[0]);
                }

                boolean[] history = player.getGameHistory();

                // Compute the score for each move, choose the best.
                double bestScore = -1;
                int bestIndex = -1;
                for (int i = 0; i < ll.size(); ++i) {
                        Movement move = (Movement)ll.get(i);

                        // Copy hand and history.
                        boolean[] tempHistory = new boolean[history.length];
                        System.arraycopy(history, 0, tempHistory, 0, history.length);

                        Card[] left = playCard(hand, move, tempHistory);
                        //Card[] successor = new Card[hand.length - numPlay];
                        
                        double score = computeValue(left, tempHistory);
                        if (score > bestScore) {
                                bestScore = score;
                                bestIndex = i;
                        }
                }
                return (Movement)ll.get(bestIndex);

                //return new Movement(null);
	}
        public Card[] playCard(Card[] cards, Movement move, boolean[] history)
        {
                int numPlay = move.numCards;
                Card[] left = new Card[cards.length - numPlay];
                List moveArray = Arrays.asList(move.cards);
                int index = 0;
                for (Card c : cards) {
                        if (moveArray.contains(c)) {
                                // Update history
                                history[c.getIndex()] = true;
                                continue;
                        }
                        left[index] = c;
                        index += 1;
                }
                return left;
        }

        public double computeValue(Card[] left, boolean[] his)
        {
                // How many rank still has chance to be played
                // as Revolution.
                List cardList = Arrays.asList(left);
                int numRevable = 0;
                for (int i = 1; i < 14; ++i) {
                        if (his[i] || his[i+13] || his[i+26] || his[i+39]) {
                                continue;
                        }
                        else if (cardList.contains( new Card(i) )) {
                                continue;
                        }
                        else {
                                numRevable += 1;
                        }
                }

                // How many card left in players' hands.
                int totalHand = 53 - left.length;
                for (int i = 0; i < 53; ++i) {
                        if (his[i]) totalHand -= 1;
                }
                
                int avarge = (int)(totalHand/3.0) + 1;

                // 11 left unplayed.
                int num11 = 0;
                for (int i = 11; i < 53; i+= 13) {
                        if (!his[i]) num11 += 1;
                }

                double chance4 = 0.1 * numRevable;
                double chance11 = 0.2 * num11;

                int[] normalWeight = new int[] {14, 12, 13, 1, 2, 3, 4, 5, 6, 7, 15, 9, 10, 11};
                int[] oppositeWeight = new int[] {14, 2, 1, 13, 12, 11, 10, 9, 8, 7, 15, 5, 4, 3};

                double weight = 0.0;
                for (Card c : left) {
                        weight += normalWeight[c.getRank()] * 1.0;
                        weight += oppositeWeight[c.getRank()] * (chance4 + chance11);
                }


                return weight;
        }

        public static void main(String[] args)
        {
                // Generate random hand, testNum are how many
                // cards in hand.
                int testNum = 10;
                Card[] hand = new Card[testNum];
                Random ran = new Random();
                List<Integer> tempIdx = new ArrayList<>();
                int i = 0;
                while (i < testNum) {
                        int index = ran.nextInt(53);
                        
                        if (tempIdx.contains(index)) continue;

                        tempIdx.add( index );
                        i += 1;
                }
                for (i = 0; i < testNum; ++i) {
                        hand[i] = new Card(tempIdx.get(i));
                }
                Arrays.sort(hand);
                printHand(hand);

                // Generate random history.
                boolean[] history = new boolean[53];
                for (i = 0; i < 53; ++i) {
                        history[i] = ran.nextBoolean();
                }

        }
        public boolean allPlayed(int rank, boolean[] his) 
        {
                for (int i = rank; i < rank * 5; i += rank) {
                        if (!his[i]) return false;
                }
                return true;
        }
        public static void printHand(Card[] cards) 
        {
                for (int i = 0; i < cards.length; ++i) {
                        System.out.print(cards[i].toString() + " ");
                }
                System.out.println("");
        }
}



















