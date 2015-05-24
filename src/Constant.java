
public interface Constant
{
		static final int MAX_NUM_CARD = 53;

	    static final int DIAMOND = 2;
	    static final int CLUB = 3;
	    static final int HEART = 1;
	    static final int SPADE = 0;
	    
	    static final int JACK = 11;
	    static final int QUEEN = 12;
	    static final int KING = 13;
	    // static final int ACE = 14;
	    
	    static final int width = 640;
	    static final int height = 480;
	    
	    static final String filePrefix = "images/cards/";
	    static final int cardWidth = 71;
	    static final int cardHeight = 96;
	    static final int[] playerCardLocationX = new int[] {100, 130, 160, 190, 220, 250, 280, 310, 340, 
	    	370, 400, 430, 460, 490, 520};
	    static final int[] playerCardLocationY = new int[] {320, 300};
	    static final int showLocationX0 = 220;
	    static final int showLocationY = 180;
	    static final int showLocationDX = 120;
	    
	    static final int numMaxHandCard = 15;
	    static final int numPlayer = 4;
	    static final int maxMovementCard = 5;
}
