import java.util.*;
import java.util.Arrays;

public class HeuristicAgent extends Agent implements Constant
{
        private Player player;
        private boolean[] history;
        private boolean isRevo, is11Revo;
        private Random ran;

	public HeuristicAgent(Player player)
	{
		this.player = player;
                ran = new Random();
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
                int index = 0;
                for (int i = 0; i < handWithNull.length; ++i) {
                        if (handWithNull[i] == null) continue;
                        hand[index] = handWithNull[i];
                        index += 1;
                }
                // All the legal move.
                LinkedList<Movement> legalMoves = player.genLegalMove(player.getGameShowMove(),
                                player.hand, player.getIsRevo(),
                                player.getIsStartGame()
                                );

                // Only choice is pass.
                if (legalMoves.size() == 1) {
                        Movement move = (Movement)legalMoves.get(0);
                        //if (move.type != Constant.PASS) {
                        //        SystemFunc.throwException("getLegamMove didin't generate pass");
                        //}
                        System.out.println("only choice " + move.toString());
                        return (Movement)legalMoves.get(0);
                }

                /*
                // Tag each card for best combination.
                int[] cardTags = new int[numCards];
                Arrays.fill(cardTags, 0);
                ListIterator<Movement> tempIter = legalMoves.listIterator();
                while (tempIter.hasNext()) {
                        Movement move = tempIter.next();      
                        if (move.type == Constant.PASS) continue;
                        int type = move.type;
                        int[] positions = findPos(hand, move.cards);
                        for (int i = 0; i < positions.length; ++i) {
                                System.out.println("type and pos: " + type + " " + cardTags[ positions[i] ]);
                                if (type > cardTags[ positions[i] ]) {
                                        cardTags[ positions[i] ] = type;
                                }
                        }
                }
                for (int i = 0; i < numCards; ++i) {
                        System.out.print(cardTags[i] + " ");
                }
                System.out.println("");
                */

                // find combo for all type.
                LinkedList<Movement> allMoves = player.genLegalMove( // new Movement(new Card[0]),
                                null,
                                player.hand, player.getIsRevo(),
                                player.getIsStartGame()
                                );

                // Last card to play.
                if (legalMoves.size() == 2 && allMoves.size() == 2) {
                        return (Movement)legalMoves.get(0);
                }
                
                // Tag each card for best combination.
                int[] cardTags = new int[numCards];
                Arrays.fill(cardTags, 0);
                ListIterator<Movement> tempIter = allMoves.listIterator();
                while (tempIter.hasNext()) {
                        Movement move = (Movement)tempIter.next();      
                        if (move.type == Constant.PASS) continue;
                        if (hasJokerOr8(move)) continue;
                        int type = move.type;
                        int[] positions = findPos(hand, move.cards);
                        for (int i = 0; i < positions.length; ++i) {
                                //System.out.println("type and pos: " + type + " " + cardTags[ positions[i] ]);
                                if (type > cardTags[ positions[i] ]) {
                                        cardTags[ positions[i] ] = type;
                                }
                        }
                }

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
                LinkedList<MoveScore> singleScore = computeListScore(singlell, isRevo || is11Revo, canRevo);
                LinkedList<MoveScore> doubleScore = computeListScore(doublell, isRevo || is11Revo, canRevo);
                LinkedList<MoveScore> fourScore   = computeListScore(fourll,   isRevo || is11Revo, canRevo);
                LinkedList<MoveScore> otherScore  = computeListScore(otherll,  isRevo || is11Revo, canRevo);

                Collections.sort(singleScore);
                Collections.sort(doubleScore);
                Collections.sort(fourScore);
                Collections.sort(otherScore);

                // Socre for legal moves.
                LinkedList<MoveScore> legalScores = computeListScore(legalMoves, isRevo || is11Revo, canRevo);
                Collections.sort(legalScores);

                ListIterator scoreIter = legalScores.listIterator();

                // Too many other cards left, keep the 2, 8 and joker.
                MoveScore ms = (MoveScore)scoreIter.next();
                if (allMoves.size() - legalMoves.size() > 2) {
                        System.out.println("\nhasJoker or something = " + hasJokerOr8(ms.move));
                        while (scoreIter.hasNext() && hasJokerOr8(ms.move)) {
                                ms = (MoveScore)scoreIter.next();
                        }
                }
                // If some card in this move have better combination.
                boolean hasBetter = true;
                while (hasBetter) {
                        for (Card c : ms.move.cards) {
                                //System.out.println("better type: " + (cardTags[findIndex(hand, c)] > ms.move.type));
                                //System.out.println("two types  : " + cardTags[findIndex(hand, c)] + " " + ms.move.type);
                                System.out.println("-------------------------");
                                System.out.println(ms.toString());
                                System.out.println("has next   : " + scoreIter.hasNext());
                                if (cardTags[ findIndex(hand, c) ] > ms.move.type && scoreIter.hasNext()) {
                                        System.out.println("Better combination: true");
                                        ms = (MoveScore)scoreIter.next();
                                        break;
                                }
                                hasBetter = false;
                        }
                }


                int position, movesLeft;

                //MoveScore ms = (MoveScore)scoreIter.next();
                //System.out.println(ms.toString());
                switch (ms.move.type) {
                        case(Constant.SINGLE):
                                position = listIndex(singleScore, ms);
                                movesLeft = singleScore.size();
                                break;
                        case(Constant.PAIR):
                                position = listIndex(doubleScore, ms);
                                movesLeft = doubleScore.size();
                                break;
                        case(Constant.FOUR):
                                position = listIndex(fourScore, ms);
                                movesLeft = fourScore.size();
                                break;
                        default:
                                position = listIndex(otherScore,ms);
                                movesLeft = otherScore.size();
                }

                // Showing information for testing.
                System.out.println("\nHand: " + handToString(hand));
                if (player.getGameShowMove() != null) {
                        System.out.println("Show Move: " + handToString(player.getGameShowMove().cards));
                }
                //ListIterator newIter = legalScores.listIterator();
                //while (newIter.hasNext()) {
                for (MoveScore moveScore : legalScores) {
                        System.out.println(moveScore.toString());
                }
                System.out.println("Choice is : " + ms.toString());
                SystemFunc.sleep(5000);


                double ratio = (canRevo) ? movesLeft/(1.0*position) : position/(1.0*movesLeft);
                System.out.println("bigger than " + (ratio) + " " + position + " " + movesLeft);
                if (Math.random() + 0.35 > (ratio)){ 
                        if (!legalMoves.contains(ms.move)) {
                                SystemFunc.throwException("heuristic agent return not legal move.");
                        }
                        System.out.println("computed choice " + ms.move.toString());
                        return ms.move;
                }
                if (player.getIsStartGame()) {
                        return ms.move;
                }
                if (player.getGameShowMove() == null) {
                        return ms.move;
                }
                if (player.getNumRemainPlayer() - 1 == player.getPassCount()) {
                        // getPass count is zero.
                        return ms.move;
                }
                if (player.getGameShowMove().type == Constant.PASS) {
                        return ms.move;
                }

                else {
                        System.out.println("pass");
                        Card[] passCard = new Card[0];
                        Movement wtf = new Movement(passCard, false);
                        System.out.println("" + wtf.type);
                        return new Movement(passCard, false);
                }


	}
        public int[] findPos(Card[] hand, Card[] moveCards)
        {
                int[] positions = new int[moveCards.length];
                int moveIndex = 0;
                for (int i = 0; i < hand.length; ++i) {
                        if (hand[i].isEqualTo( moveCards[ moveIndex ] )) {
                                positions[moveIndex] = i;
                                moveIndex += 1;
                        }
                        if (moveIndex == moveCards.length) break;
                }
                return positions;
        }
        public int findIndex(Card[] hand, Card c)
        {
                for (int i = 0; i < hand.length; ++i) {
                        if (hand[i].isEqualTo(c)) {
                                return i;
                        }
                }
                SystemFunc.throwException("cannot find position in hand in Heuristic agent");
                return -1;
        }
        public boolean hasJokerOr8(Movement move) 
        {
                // pass
                if (move == null) return false;
                for (Card c : move.cards) {
                        if (c.getIndex() == 0 || c.getRank() == 8 || c.getRank() == 2) return true;
                }
                return false;
        }
        public int listIndex(LinkedList<MoveScore> ll, MoveScore moveScore)
        {
                int index = 0;
                for (MoveScore ms : ll) {
                        if (ms.equalsTo(moveScore)) 
                                return index;
                        index += 1;
                }
                SystemFunc.throwException("cannot find move score in ll.");
                return -1;
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
                public double totalScore;
                public Movement move;
                public boolean Revo;

                public MoveScore(Movement move, double big, double small, boolean Revo) 
                {
                        this.move = move;
                        this.bigScore = big;
                        this.smallScore = small;
                        this.totalScore = small + big;
                        this.Revo = Revo;
                }
                public void setRevo(boolean b)
                {
                        Revo = b;
                }
                @Override
                public int compareTo(MoveScore other) {
                        if (hasJokerOr2(this.move.cards)) {
                                return 1;
                        }
                        return (this.totalScore > other.totalScore) ? 1 : 0;
                }
                @Override
                public String toString() {
                        String s = "Move: " + handToString(move.cards) + "\n";
                        s += "big= " + Double.toString(bigScore);
                        s += ", small= " + Double.toString(smallScore);
                        s += ", total= " + Double.toString(totalScore) + "\n";
                        return s;
                }
                public boolean equalsTo(MoveScore other) {
                        return (bigScore == other.bigScore && smallScore == other.smallScore) ? true : false;
                }
                public boolean hasJokerOr2(Card[] cards) {
                        for (Card c : cards) {
                                if (c.getIndex() == 0) return true;
                                if (c.getRank()  == 2) return true;
                        }
                        return false;
                }
        }
        
        public LinkedList<MoveScore> computeListScore(LinkedList ll, boolean Revo, boolean canRevo)
        {
                LinkedList<MoveScore> scoreList = new LinkedList<MoveScore>();
                ListIterator iter = ll.listIterator();
                while (iter.hasNext()) {
                        Movement move = (Movement)iter.next();
                        if (move.type == Constant.PASS)
                                continue;
                        MoveScore score = computeValue(move, Revo, canRevo);
                        //scoreList.add( new MoveScore(move, canRevo) );
                        scoreList.add( score );
                }
               return scoreList; 
        }

        public MoveScore computeValue(Movement move, boolean isRevoNow, boolean canRevo)
        {
                double bigWeight = (isRevoNow) ? 0.1 : 1.0;
                double smallWeight = (isRevoNow) ? 1.0 : 0.1;

                if (canRevo) {
                        smallWeight += 2.0;
                }

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
                smallWeight += numRevable*0.01;

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
                smallWeight += num11 * 0.02;

                int[] numRankLeft = new int[13];
                for (int i = 1; i < 53; ++i) {
                        if (!history[i]) {
                                numRankLeft[(i-1)%13] += 1;
                        }
                }

                int stronger = numStronger(move, numRankLeft);
                int weaker   = numWeaker(move, numRankLeft);

                // Not handle different type here.
                double bigScore = (52 - stronger) * bigWeight;
                double smallScore = (52 - weaker) * smallWeight;

                return new MoveScore(move, bigScore, smallScore, isRevoNow);
        }
        public static int numStronger(Movement move, int[] numRankLeft)
        {
                Card[] cards = move.cards;
                Arrays.sort(cards);
                //printHand(cards);
                int type = move.type;
                if (type == STRAIGHT3) type = 3;
                if (type == STRAIGHT4) type = 4;
                if (type == STRAIGHT5) type = 5;
                int rank = cards[cards.length-1].getRank();
                // Joker or 2.
                if ((rank ==  0) || (rank == 2)) return 0;
                int stronger = 0;

                rank = (rank < 3) ? rank + 13 : rank;

                for (int i = 0; i < 13; ++i) {
                        int j = (i+1 < 3) ? i+1 + 13 : i+1;
                        if (rank >= j) continue;
                        stronger += numRankLeft[i] / type;
                }
                return stronger;
        }
        public static int numWeaker(Movement move, int[] numRankLeft)
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
                rank = (rank < 3) ? rank + 13 : rank;

                for (int i = 0; i < 13; ++i) {
                        int j = (i+1 < 3) ? i+1 + 13 : i+1;
                        if (rank <= j) continue;
                        weaker += numRankLeft[i] / type;
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

                // History in rank form.
                int[] numRankLeft = new int[13];
                for (i = 1; i < 53; ++i) {
                        if (!history[i]) {
                                numRankLeft[(i-1)%13] += 1;
                        }
                }
                for (i = 0; i < 13; ++i) {
                        System.out.print(Integer.toString(numRankLeft[i]));
                }
                System.out.println("");

                //Card[] testCards = new Card[1];
                Movement test1 = new Movement(new Card[] {new Card(7)});

                System.out.println("7 stronger: " + numStronger(test1, numRankLeft));
                System.out.println("7 smaller: " + numWeaker(test1, numRankLeft));



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
        public static String handToString(Card[] cards) 
        {
                String s = "";
                for (int i = 0; i < cards.length; ++i) {
                        s += cards[i].toString() + " ";
                        //System.out.print(cards[i].toString() + " ");
                }
                //System.out.println("");
                return s;
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



















