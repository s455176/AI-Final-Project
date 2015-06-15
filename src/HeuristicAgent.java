import java.util.*;
import java.util.Arrays;

public class HeuristicAgent extends Agent implements Constant
{
        private Player player;
        private boolean[] history;
        private boolean isRevo, is11Revo;

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
                
                int numCards  = player.numHandCards;
                this.history  = player.getGameHistory();
                this.isRevo   = player.getIsRevo();
                this.is11Revo = player.getIs11Revo();
                Card[] handWithNull = player.hand;
                Card[] hand = new Card[numCards];
                // Remove null in hand.
                for (int i = 0; i < handWithNull.length; ++i) {
                        if (handWithNull == null) continue;
                        hand[i] = handWithNull[i];
                }
                // All the possible move.
                LinkedList legalMoves = player.genLegalMove(player.getGameShowMove(),
                                player.hand, player.getIsRevo(),
                                player.getIsStartGame()
                                );

                // Only choice is pass.
                if (legalMoves.size() == 1) {
                        return (Movement)legalMoves.get(0);
                }

                //boolean[] history = player.getGameHistory();

                // TODO: find combo for all type.
                LinkedList allMoves = player.genLegalMove(
                                new Movement(new Card[0]),
                                player.hand, player.getIsRevo(),
                                player.getIsStartGame()
                                );
                ListIterator iter = allMoves.listIterator();

                LinkedList<Movement> singlell = new LinkedList<Movement>();
                LinkedList<Movement> doublell = new LinkedList<Movement>();
                LinkedList<Movement> fourll   = new LinkedList<Movement>();
                LinkedList<Movement> otherll  = new LinkedList<Movement>();
                boolean canRevo = false;
                boolean hasJoker = false;
                int num8  = 0;
                int num11 = 0;

                while (iter.hasNext()) {
                        Movement m = (Movement)iter.next();

                        switch(m.type) {
                        case(Constant.PASS):
                                break;
                        case(Constant.SINGLE):
                                if (m.cards[0].getRank() == 8) num8 += 1;
                                else if (m.cards[0].getRank() == 11) num11 += 1;
                                else if (m.cards[0].getRank() == 0) hasJoker = true;
                                singlell.add(m);
                                break;
                        case(Constant.PAIR):
                                doublell.add(m);
                                break;
                        case(Constant.FOUR):
                                fourll.add(m);
                                canRevo = true;
                                break;
                        default:
                                otherll.add(m);
                        }
                }

                // Compute value in each linked list.
                LinkedList singleScore = computeListScore(singlell, canRevo);
                LinkedList doubleScore = computeListScore(doublell, canRevo);
                LinkedList fourScore   = computeListScore(fourll, canRevo);
                LinkedList otherScore  = computeListScore(otherll, canRevo);

                


                // TODO: if other are all pass.
                if (player.getNumRemainPlayer() - 1 == player.getPassCount()) {
                        if (otherScore != null) {
                                return (Movement)otherScore.get(0);
                        }
                        if (doubleScore != null) {
                                return (Movement)doubleScore.get(0);
                        }
                        if (singleScore != null) {
                                return (Movement)doubleScore.get(0);
                        }
                        System.out.println("dont play other, single, double...");
                }

                // TODO: temporary return.
                return (Movement)legalMoves.get(0);

                /*
                // Compute the score for each move, choose the best.
                double bestScore = -1;
                int bestIndex = -1;
                for (int i = 0; i < legalMoves.size(); ++i) {
                        Movement move = (Movement)legalMoves.get(i);

                        // Copy hand and history.
                        boolean[] tempHistory = new boolean[history.length];
                        System.arraycopy(history, 0, tempHistory, 0, history.length);

                        Card[] left = playCard(hand, move, tempHistory);
                        //Card[] successor = new Card[hand.length - numPlay];
                        
                        //double score = computeValue(left, tempHistory);
                        //TODO:
                        double score = -1;
                        if (score > bestScore) {
                                bestScore = score;
                                bestIndex = i;
                        }
                }
                return (Movement)legalMoves.get(bestIndex);

                //return new Movement(null);
                */
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

        private class MoveScore implements Comparable<MoveScore>, Constant
        {
                public double bigScore;
                public double smallScore;
                public Movement move;
                public boolean isRevo;

                public MoveScore(Movement move, double big, double small, boolean isRevo) 
                {
                        this.move = move;
                        this.bigScore = big;
                        this.smallScore = small;
                        this.isRevo = isRevo;
                }
                public void setRevo(boolean b)
                {
                        isRevo = b;
                }
                @Override
                public int compareTo(MoveScore other) {
                        if (isRevo) {
                                return (this.smallScore > other.smallScore) ? 1 : 0;
                        }
                        else {
                                return (this.bigScore > other.bigScore) ? 1 : 0;
                        }
                }

        }
        
        public LinkedList computeListScore(LinkedList ll, boolean canRevo)
        {
                LinkedList<MoveScore> scoreList = new LinkedList<MoveScore>();
                ListIterator iter = ll.listIterator();
                while (iter.hasNext()) {
                        Movement move = (Movement)iter.next();
                        MoveScore score = computeValue(move, canRevo);
                        //scoreList.add( new MoveScore(move, canRevo) );
                        scoreList.add( score );
                }
               return scoreList; 
        }

        public MoveScore computeValue(Movement move, boolean canRevo)
        {
                double bigWeight = (isRevo) ? 0.1 : 1.0;
                double smallWeight = (isRevo) ? 1.0 : 0.1;

                // How many rank still has chance to be played
                // as Revolution.
                int numRevable = 0;
                for (int i = 1; i < 14; ++i) {
                        if (history[i] || history[i+13] || history[i+26] || history[i+39]) {
                                continue;
                        }
                        else {
                                numRevable += 1;
                        }
                }
                // Not handle already revolution case.
                smallWeight += numRevable*0.1;

                /*
                // How many card left in players' hands.
                int totalHand = 53 - left.length;
                for (int i = 0; i < 53; ++i) {
                        if (his[i]) totalHand -= 1;
                }
                */

                // Number of 11 left unplayed.
                int num11 = 0;
                for (int i = 11; i < 53; i+= 13) {
                        if (!history[i]) num11 += 1;
                }
                // Not handle already revolution case.
                smallWeight += num11 * 0.1;

                int[] numRankLeft = new int[13];
                for (int i = 1; i < 53; ++i) {
                        if (!history[i]) {
                                numRankLeft[i%14 - 1] += 1;
                        }
                }

                int stronger = numStronger(move, numRankLeft);
                int weaker   = numWeaker(move, numRankLeft);

                // Not handle different type here.
                double bigScore = (52 - stronger) * bigWeight;
                double smallScore = (52 - weaker) * smallWeight;

                return new MoveScore(move, bigScore, smallScore, isRevo);
        }
        public int numStronger(Movement move, int[] numRankLeft)
        {
                Card[] cards = move.cards;
                Arrays.sort(cards);
                int type = move.type;
                if (type == STRAIGHT3) type = 3;
                if (type == STRAIGHT4) type = 4;
                if (type == STRAIGHT5) type = 5;
                int rank = cards[-1].getRank();
                if ((rank ==  0) || (rank == 2)) return 0;
                int stronger = 0;
                for (int i = 0; i < 2; ++i) {
                        if (rank > 2) break;
                        if (numRankLeft[i] >= type && rank > i + 1) {
                                stronger += numRankLeft[i] / type;
                        }
                }
                for (int i = 2; i < 13; ++ i) {
                        if (rank < 3) break;
                        if (rank < (i + 1)) break;
                        if (numRankLeft[i] >= type) {
                                stronger += numRankLeft[i] / type;
                        }
                }
                return stronger;
        }
        public int numWeaker(Movement move, int[] numRankLeft)
        {
                Card[] cards = move.cards;
                Arrays.sort(cards);
                int type = move.type;
                if (type == STRAIGHT3) type = 3;
                if (type == STRAIGHT4) type = 4;
                if (type == STRAIGHT5) type = 5;
                int rank = cards[0].getRank();
                if ((rank ==  0) || (rank == 3)) return 0;
                int weaker = 0;
                for (int i = 0; i < 2; ++i) {
                        if (rank > 2) break;
                        if (rank > i+1 && numRankLeft[i] >= type) {
                                weaker += numRankLeft[i] / type;
                        }
                }
                for (int i = 2; i < 13; ++ i) {
                        if (rank < 3) break;
                        if (rank < i + 1) break;
                        if (numRankLeft[i] >= type) {
                                weaker += numRankLeft[i] / type;
                        }
                }
                return weaker;
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
                printHistory(history);

        }
        public boolean allPlayed(int rank, boolean[] his) 
        {
                for (int i = rank; i < rank * 5; i += 13) {
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
        public static void printHistory(boolean[] his) 
        {
                int index = 0;
                System.out.println("\nHistory: ");
                System.out.println("1234567890123 Joker played: " + his[0]);
                for (int suit = 0; suit < 4; ++suit) {
                        for (int rank = 0; rank<13; ++rank) {
                                // * mean played.
                                String s = (his[suit*13+rank+1]) ? "*" : " ";
                                System.out.print("" + s);
                        }
                        System.out.println("");
                }
        }
}



















