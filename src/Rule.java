import java.util.Random;
import java.util.ArrayList;

public class Rule implements Constant
{
    /**
     * Return a type in Constant.java that shows which combiantion the 
     * cards is.
     *
     * @param cards a Card array.
     * @return int type, see Constant.java.
     */
    public static int combination(Card[] cards)
    {
        int length = cards.length;

        // Change rank 1~13 to 3~15 into values array
        int[] values = new int[length];

        // Record the first card's suiti and rank.
        int firstSuit = cards[0].getSuit();
        int firstRank = cards[0].getRank();

        // The booleans indicate the cards in the same suit or rank.
        boolean sameSuit = true;
        boolean sameRank = true;
        
        int jokerPos = 0;

        for (int i = 0; i < length; ++i) 
        {
            int rank = cards[i].getRank();
            if (rank == 0) 
            {
                // It's joker, replace it with best choice card.
                cards[i] = replaceJoker(cards, i);
                rank = cards[i].getRank();
            }
            values[i] = (rank < 3) ? rank + 13 : rank;
            
            if (cards[i].getSuit() != firstSuit)
            {
                sameSuit = false;
            }
            if (cards[i].getRank() != firstRank)
            {
                sameRank = false;
            }
        }
        for (int i = 0; i < cards.length; ++i) {
            //System.out.println("" + values[i]);
        }

        switch (length){

            case 1:
                return SINGLE;

            case 2:
                if (sameRank)
                    return PAIR;
                return ILLEGAL;

            case 3:
                if (sameRank) 
                    return TRIPLE;
                if ((values[0] == values[1] - 1) &&
                    (values[0] == values[2] - 2) &&
                    sameSuit )
                    return STRAIGHT3;

           case 4:
                if (sameRank) 
                    return FOUR;
                if ((values[0] == values[1] - 1) &&
                    (values[0] == values[2] - 2) &&
                    (values[0] == values[3] - 3) &&
                    sameSuit )
                    return STRAIGHT4;

            case 5:
                if (sameSuit &&
                    (values[0] == values[1] - 1) &&
                    (values[0] == values[2] - 2) &&
                    (values[0] == values[3] - 3) &&
                    (values[0] == values[4] - 4) )
                    return STRAIGHT5;

            default:
                return ILLEGAL;

        }
    }
    /**
     * Test whether the movement is a legal move.
     *
     * @param playMove the move player want to play.
     * @param lastMove the move last player played.
     * @param isRevo whether is revolution right now.
     * @return boolean shows the move is legal or not.
     */
    public static boolean isLegalMove(Movement playMove, Movement lastMove, 
                               boolean isRevo)
    {
        boolean legal = false;

        if(playMove == null)
        {
        	SystemFunc.throwException("current Move is NULL");
        }
        
        if(playMove.numCards == 0)
        {
        	// current player pass
        	// if lastMove == null, which means starting player of the new round, pass is not allow
        	// if lastMove != null then choose to pass is always OK
        	return lastMove != null;
        }
        if(lastMove == null)
        {
        	// starting player of the new round, if the combination is legal than is OK
        	return combination(playMove.getCards()) != ILLEGAL;
        }
        
        Card[] lastCards = lastMove.getCards();
        Card[] playCards = playMove.getCards();


        // TODO: whether to handle pass here?
        // Should (lastMove != null && playMove == null) return true?

        int[] lastValue = toggleValue(lastCards);
        int[] playValue = toggleValue(playCards);

        int lastType = combination(lastCards);
        int playType = combination(playCards); 
        if (playType == ILLEGAL)
        {
            // The card combination of player played is illegal.
            System.out.println("Illegal combination of cards.");
            return false;
        }

        int lastBiggest = lastValue[lastValue.length - 1];
        int playBiggest = playValue[playValue.length - 1];

        if (lastType == playType)
        {
            // Same type of combination, compare the biggest cards.
            if (playBiggest > lastBiggest)
            {
                legal = true;
            }
        }
        else
        {
            // Not the same type, return false.
            System.out.println("Illegal type against current board.");
            return false;
        }
        // If it's in revolution, big means small, and small means big.
        return (isRevo) ? (!legal) : legal;
    }

    /**
     * Return the toggled value of a card array.
     *
     * The original value of cards rank from 1 ~ 13. Here we change 
     * this value to range 3 ~ 15, which is easier for comparing values.
     *
     * @param  cards   a array of cards.
     * @return the toggled value in a int array.
     */
    public static int[] toggleValue(Card[] cards)
    {
        int[] intArray = new int[cards.length];
        for (int i = 0; i < cards.length; ++i)
        {
            int value = cards[i].getRank();
            if (value == 0)
            {
                cards[i] = replaceJoker(cards, i);
                value = cards[i].getRank();
            }
            intArray[i] = (value < 3) ? value + 13 : value;
        }
        return intArray;
    }

    /**
     * Replace the joker card by the vard that gives best value to 
     * the card combination.
     *
     * @param cards the original card array with joker card in it.
     * @return a new card array which joker has been replaced by 
     *         another card.
     */
    public static Card replaceJoker(Card[] cards, int pos)
    {
        Card[] newCards = new Card[cards.length];
        System.arraycopy(cards, 0, newCards, 0, cards.length);
        
        //Brute force search for best replace card.
        for (int bestType = 7; bestType > 1; --bestType)
        {
            for (int i = 52; i > 0; --i)
            {
                newCards[pos] = new Card(i);
                int type = combination(newCards);
                if (type == bestType){
                    return new Card(i);
                }
            }
        }        
        // Program should not get to this point.
        System.out.println("Can't find a replace card for joker.");
        return new Card(0);
    }

    /**
     * Test code
     */
    public static void main(String[] args)
    {
        System.out.println("\n----------Test for Rule.java----------");
        Card[] spades = new Card[13];
        Card[] hearts = new Card[13];
        Card[] diams  = new Card[13];
        Card[] clubs  = new Card[13];
        Card joker = new Card(0);
        for (int i = 0; i<13; ++i)
        {
            spades[i] = new Card(i + 1);
            hearts[i] = new Card(i + 14);
            diams[i]  = new Card(i + 27);
            clubs[i]  = new Card(i + 40);
        }

        Card[] two1   = new Card[] {spades[3], joker};
        Card[] triple = new Card[] {spades[1], joker, clubs[1]};
        Card[] four   = new Card[] {spades[2], hearts[2], joker, diams[2]};
        Card[] straight34567 = new Card[] {spades[2], joker, spades[4], spades[5], spades[6]};
        Card[] straight12345 = new Card[] {spades[0], spades[1], spades[2], spades[3], spades[4]};
        Card[] straightJQK12 = new Card[] {spades[10], spades[11], spades[12], spades[0], spades[1]};

        System.out.println("Combination test: ");
        System.out.println("Test double: " + combination(two1));
        System.out.println("Test triple: " + combination(triple));
        System.out.println("Test four  : " + combination(four));
        System.out.println("Test 34567 : " + combination(straight34567));
        System.out.println("Test 12345 : " + combination(straight12345));
        System.out.println("Test JQK12 : " + combination(straightJQK12));

        System.out.println("\nisLegal play test:");
        
        Card[] two2    = new Card[] {spades[1], hearts[1]};
        Card[] triple3 = new Card[] {spades[2], hearts[2], clubs[2]};
        Card[] four4   = new Card[] {spades[3], hearts[3], clubs[3]};
        Card[] straight45678 = new Card[] {spades[3], spades[4], spades[5], spades[6], spades[7]};
        
        Movement singleMove = new Movement(new Card[] {spades[0]});
        Movement two1Move = new Movement(two1);
        Movement two2Move = new Movement(two2);
        Movement triple2Move = new Movement(triple);
        Movement triple3Move = new Movement(triple3);
        Movement four3Move = new Movement(four);
        Movement four4Move = new Movement(four4);
        Movement s34567Move = new Movement(straight34567);
        Movement s45678Move = new Movement(straight45678);
        Movement s12345Move = new Movement(straight12345);

        System.out.println("Play 1     against 22     : " + isLegalMove(singleMove, two2Move, false));
        System.out.println("Play 11    against 22     : " + isLegalMove(two1Move, two2Move, false));
        System.out.println("Play 22    against 11     : " + isLegalMove(two2Move, two1Move, false));
        System.out.println("Play 333   against 22     : " + isLegalMove(triple3Move, two2Move, false));
        System.out.println("Play 333   against 222    : " + isLegalMove(triple3Move, triple2Move, false));
        System.out.println("Play 3333  against 1      : " + isLegalMove(four3Move, singleMove, false));
        System.out.println("Play 4444  against 333    : " + isLegalMove(four4Move, triple3Move, false));
        System.out.println("Play 34567 against 45678  : " + isLegalMove(s34567Move, s45678Move, false));
        System.out.println("Play 12345 against 45678  : " + isLegalMove(s12345Move, s45678Move, false));
        System.out.println("");
    }
}










