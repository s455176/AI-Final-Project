public class Movement implements Constant
{
    private Card[] card;
    private int numCards;

    public Movement(Card[] card)
    {
        this.card = card;
        numCards = card.length;
    }
    public Card[] getCards()
    {
        Card[] copyArray = new Card[card.length];
        System.arraycopy(card, 0, copyArray, 0, card.length);
        return copyArray;
    }

}

