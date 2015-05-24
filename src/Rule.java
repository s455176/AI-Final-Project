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
            values[i] = (rank < 2) ? rank + 13 : rank;
            if (cards[i].getSuit() != firstSuit)
            {
                sameSuit = false;
            }
            if (cards[i].getRank() != firstRank)
            {
                sameRank = false;
            }
        }

        switch (length){

            case SINGLE:
                return SINGLE;

            case PAIR:
                if (sameRank)
                    return PAIR;
                return ILLEGAL;

            case TRIPLE:
                if (sameRank) 
                    return TRIPLE;
                if ((values[0] == values[1] - 1) &&
                    (values[0] == values[2] - 2) &&
                    sameSuit )
                    return STRAIGHT;

           case FOUR:
                if (sameRank) 
                    return FOUR;
                if ((values[0] == values[1] - 1) &&
                    (values[0] == values[2] - 2) &&
                    (values[0] == values[3] - 3) &&
                    sameSuit )
                    return STRAIGHT;

            case STRAIGHT:
                if (sameSuit &&
                    (values[0] == values[1] - 1) &&
                    (values[0] == values[2] - 2) &&
                    (values[0] == values[3] - 3) &&
                    (values[0] == values[4] - 4) )
                    return STRAIGHT;

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

        Card[] lastCards = lastMove.getCards();
        Card[] playCards = playMove.getCards();

        int[] lastValue = toggleValue(lastCards);
        int[] playValue = toggleValue(playCards);

        int lastType = combination(lastCards);
        int playType = combination(playCards); 
        if (playType == ILLEGAL)
        {
            return false;
        }
        //System.out.println("type: " + playType + " " + lastType);

        int lastBiggest = lastValue[lastValue.length - 1];
        int playBiggest = playValue[playValue.length - 1];

        //System.out.println("value: " + playBiggest + " " + lastBiggest);
        if (lastType == playType)
        {
            if (playBiggest > lastBiggest)
            {
                legal = true;
            }
        }

        else if (playType == FOUR)
        {
            legal = true;
        }
        else
        {
            legal = false;
        }
        //System.out.println("legal: " + legal);
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
            intArray[i] = (value < 2) ? value + 13 : value;
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
    public Card[] replaceJoker(Card[] cards)
    {
        Card[] newCards = new Card[cards.length];
        boolean hasJoker = false;
        int jokerPos = -1;
        int type;
        for (int i = 0; i < cards.length; ++i) 
        {
            if (cards[i].getIndex() == 53) 
            {
                jokerPos = i;
                hasJoker = true;
            }
        }

        System.arraycopy(cards, 0, newCards, 0, cards.length);

        if (!hasJoker)
        {
            return newCards;
        }
        
        //Check for straight
        for (int bestType = 5; bestType > 0; --bestType)
        {
            for (int i = 0; i < 52; ++ i)
            {
                newCards[jokerPos] = new Card(i);
                type = combination(newCards);
                if (type == bestType){
                    return newCards;
                }
            }
        }
        return newCards;
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
        for (int i = 0; i<13; ++i)
        {
            spades[i] = new Card(i + 1);
            hearts[i] = new Card(i + 14);
            diams[i]  = new Card(i + 27);
            clubs[i]  = new Card(i + 40);
        }

        Card[] two1   = new Card[] {spades[0], hearts[0]};
        Card[] triple = new Card[] {spades[1], hearts[1], clubs[1]};
        Card[] four   = new Card[] {spades[2], hearts[2], clubs[2], diams[2]};
        Card[] straight34567 = new Card[] {spades[2], spades[3], spades[4], spades[5], spades[6]};
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










