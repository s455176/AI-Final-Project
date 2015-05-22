import java.util.Random;
import java.util.ArrayList;

public class Rule implements Constant
{
    public static int combination(Card[] cards)
    {
        int length = cards.length;

        // Change rank 1~13 to 3~15 into values array
        int[] values = new int[length];

        // Record the first card's suiti and rank.
        int firstSuit = cards[0].getSuit();
        int firstRank = cards[0].getRank();

        // If cards are in the same suit.
        boolean sameSuit = true;
        boolean sameRank = true;
        
        int ghostPos = 0;

        for (int i = 0; i < length; ++i) 
        {
            int rank = cards[i].getRank();
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
     * Test code
     */
    public static void main(String[] args)
    {
        // Chance are too low to generate some result.
        Random ran = new Random();
        int numExample = 5;
        for (int length = 1; length <= 5; ++ length)
        {
            for (int time = 0; time < numExample; ++time)
            {
                Card[] cards = new Card[length];
                for (int i = 0; i < length; ++i)
                {
                    cards[i] = new Card(ran.nextInt(52));
                    //System.out.println(cards[i]);
                }
                //System.out.println("Result: " + combination(cards));
            }
        }

        Card[] a = new Card[] {new Card(0), new Card(13)};
        Card[] b = new Card[] {new Card(0), new Card(1), new Card(2)};
        Card[] c = new Card[] {new Card(0), new Card(13), new Card(26)};
        Card[] d = new Card[] {new Card(0), new Card(1), new Card(2), new Card(3)};
        Card[] e = new Card[] {new Card(4), new Card(5), new Card(6), new Card(7)};

        System.out.println("Test a:" + combination(a));
        System.out.println("Test b:" + combination(b));
        System.out.println("Test c:" + combination(c));
        System.out.println("Test d:" + combination(d));
        System.out.println("Test e:" + combination(e));

    }
}
