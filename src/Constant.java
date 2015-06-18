import java.util.Random;

public interface Constant
{
	static Random rn = new Random(System.currentTimeMillis());
	
	static final int MAX_NUM_CARD = 53;

	static final int DIAMOND = 2;
	static final int CLUB = 3;
	static final int HEART = 1;
	static final int SPADE = 0;
	static final int JOKER = 4;
	
	static final int JACK = 11;
	static final int QUEEN = 12;
	static final int KING = 13;
	// static final int ACE = 14;
	
	static final int width = 1024;
	static final int height = 600;
	
	static final String filePrefix = "images/cards/";
	static final int cardWidth = 71;
	static final int cardHeight = 96;
	static final int[] playerCardLocationX = new int[] {280, 310, 340, 370, 400, 430, 460, 490, 520, 
		550, 580, 610, 640, 670, 700};
	static final int[] playerCardLocationY = new int[] {430, 410};
	static final int showLocationX0 = 420;
	static final int showLocationY = 220;
	static final int showLocationDX = 120;
	
	static final int numMaxHandCard = 15;
	static final int numPlayer = 4;
	static final int maxMovementCard = 5;
	
	static final int[][] playerCardBack = new int[][] {{0, 420, 900}, 
		{280, 420, 30}, {0, 420, 50}}; // 0~1 are start and range, 2 is the fixed position
	static final int[][] playerPassLabel = new int[][] {{450, 350}, {800, 210}, {450, 120}, {180, 210}};
	static final int passLabelWidth = 100;
	static final int passLabelHeight = 80;
	static final int revoLabelWidth = 200;
	static final int revoLabelHeight = 80;
	static final int revoLabelX = 600;
	static final int revoLabelY = 100;
	
	// Different combinations of cards
	static final int NEWROUND  = -1;
        static final int ILLEGAL   = 0;
        static final int SINGLE    = 1;
        static final int PAIR      = 2;
        static final int TRIPLE    = 3;
        static final int FOUR      = 4;
        static final int STRAIGHT3 = 5;
        static final int STRAIGHT4 = 6;
        static final int STRAIGHT5 = 7;
	static final int PASS      = 8;

        static boolean RECORDING = true;
        
        static final int cardPoit = 13;
        
        static final int RandomAgent = 1;
        static final int MCTSAgent = 2;
        static final int MMTSAgent = 3;
        static final int AlphaBetaAgent = 4;
        static final int HeuristicAgent = 5;
}
