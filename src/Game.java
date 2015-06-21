import javax.swing.*;

import java.awt.event.*;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
//import java.io.PrintStream;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Game extends JPanel implements ActionListener
{
	// attribute
	private Timer timer;
	private Player[] players; // player 0 is the human player 
	private Deck deck;
	private GUIResource gui;
	private KeyController keyController;
	private Movement showMove;
	private boolean isGameEnd, isRoundEnd, isStartGame;
	public boolean[] isPlayerPassed;
	private boolean isRevo, is11Revo, isRevoBeforeRound;
	private boolean[] history;
	private int passCount;
	// private Random rn;
	
	// for record simulation data only used in runSimulation and runRoundSimulation
	public int place;
	public int[] playerStat;
	public timeMeasure[] total;
	public timeMeasure[] thinking;

	// test GameState 
//	public GameState gs;
	
	// for player 0
	private boolean[] choose;
	private int numChoose;
	private boolean player0Fin;
	
	// B01902018
	public PlayerGameState gameState;

	// for recording game log
	private PrintWriter gameLog;
	private List<List<String>> playerMoves;
	private int[] playedCards;
	private boolean hasWinner;

	// method 
	/**
	 * Constructor of the game
	 */
	public Game() throws IOException
	{
		// rn = new Random();
		gui = new GUIResource(this);
		setFocusable(true);
		setLayout(null);
		keyController = new KeyController();
		
		players = new Player[Constant.numPlayer];
		isPlayerPassed = new boolean[Constant.numPlayer];
		deck = new Deck();
		for(int i = 0; i < Constant.numPlayer; i++)
		{
//			players[i] = new Player(this, i);
			isPlayerPassed[i] = false;
		}
		// initialize different agent
		//players[0] = new Player(this, 0, Constant.AlphaBetaAgent, new int[]{5});
		players[0] = new Player(this, 0, Constant.AlphaBetaAgent, new int[]{5, 10});
//		players[1] = new Player(this, 1, Constant.MCTSAgent, new int[]{40, 10});
//		players[2] = new Player(this, 2, Constant.MCTSAgent, new int[]{30, 10});
//		players[3] = new Player(this, 3, Constant.MCTSAgent, new int[]{20, 10});
		
		players[1] = new Player(this, 1, Constant.RandomAgent, new int[]{});
		players[2] = new Player(this, 2, Constant.RandomAgent, new int[]{});
		players[3] = new Player(this, 3, Constant.RandomAgent, new int[]{});
		
		choose = new boolean[Constant.numMaxHandCard];
		numChoose = 0;
		for(int i = 0; i < Constant.numMaxHandCard; i++)
		{
			choose[i] = false;
		}
		history = new boolean[Constant.MAX_NUM_CARD];
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
		{
			history[i] = false;
		}
		player0Fin = false;
		showMove = null;
		isGameEnd = false;
		isRoundEnd = false;
		isRevoBeforeRound = false;
		isStartGame = true;
		isRevo = false;
		is11Revo = false;
		passCount = 0;
		timer = new Timer(10, this);
		timer.start();
		
		// B01902018
		gameState = new PlayerGameState();

		// Open a file to record game state for machine learning.
		gameLog = new PrintWriter(new BufferedWriter(new FileWriter("gameLog.txt", true)));
		playedCards = new int[14];
		Arrays.fill(playedCards, 0); // Init to zeros;
		playerMoves = new ArrayList<List<String>>(4);
		for (int i = 0; i < 4; ++i)
		{
			// Record each players' moves.
			playerMoves.add(new ArrayList<String>());
		}
		hasWinner = false;
		place = 1;
		playerStat = new int[Constant.numPlayer];
		Arrays.fill(playerStat, 4);
		
		// for record time
		total = new timeMeasure[Constant.numPlayer];
		thinking = new timeMeasure[Constant.numPlayer];
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			total[i] = new timeMeasure();
			thinking[i] = new timeMeasure();
		}
	}
	
	public Game(playerAttr p0, playerAttr p1, playerAttr p2, playerAttr p3) throws IOException
	{
		this();
		players[0] = new Player(this, 0, p0.getType(), p0.getAttr());
		players[1] = new Player(this, 1, p1.getType(), p1.getAttr());
		players[2] = new Player(this, 2, p2.getType(), p2.getAttr());
		players[3] = new Player(this, 3, p3.getType(), p3.getAttr());
	}
	
	public void reset()
	{
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			players[i].reset();
			isPlayerPassed[i] = false;
		}
		numChoose = 0;
		for(int i = 0; i < Constant.numMaxHandCard; i++)
		{
			choose[i] = false;
		}
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
		{
			history[i] = false;
		}
		player0Fin = false;
		removeShowCards();
		isGameEnd = false;
		isStartGame = true;
		isRevoBeforeRound = false;
		isRoundEnd = false;
		isRevo = false;
		is11Revo = false;
		passCount = 0;
		deck.shuffle();

                Arrays.fill(playedCards, 0);
		playerMoves = new ArrayList<List<String>>(4);
		for (int i = 0; i < 4; ++i)
		{
			// Record each players' moves.
			playerMoves.add(new ArrayList<String>());
		}
		hasWinner = false;
		place = 1;
		Arrays.fill(playerStat, 4);
		
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			total[i] = new timeMeasure();
			thinking[i] = new timeMeasure();
		}
	}
	
	public boolean getIsRevoBeforeRound()
	{
		return isRevoBeforeRound;
	}
	public boolean getIsRevo()
	{
		return isRevo;
	}
	
	public boolean getIsStartGame()
	{
		return isStartGame;
	}
	public boolean getIs11Revo()
	{
		return is11Revo;
	}
	public int getPassCount()
	{
		return passCount;
	}
	public boolean[] getHistory()
	{
		boolean[] value = new boolean[Constant.MAX_NUM_CARD];
		System.arraycopy(history, 0, value, 0, history.length);
		return value;
	}
	public int[] getPlayerNumCards()
	{
		int[] value = new int[Constant.numPlayer];
		for(int i = 0; i < Constant.numPlayer; i++)
			value[i] = players[i].numHandCards;
		
		return value;
	}
	/**
	 * deal the cards to 4 players, and also need to draw the cards on the JPanel
	 */
	public int deal()
	{
		int turn = Constant.rn.nextInt(Constant.numPlayer);
		int startingPlayer = -1;
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
		{
			Card c = deck.getNext();
			if(c.getIndex() == 42)
				startingPlayer = turn;
			players[turn].getCard(c);
			turn = (turn + 1) % 4;
		}
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			players[i].sortHandCard();
		}
		
		// show player 0' s cards on the window
		for(int i = 0; i < players[0].numHandCards; i++)
		{
			int index = players[0].hand[i].index;
			choose[i] = false;
			gui.setPlayerAndPos(index, 0, i);
			gui.setCardLocation(index, Constant.playerCardLocationX[i], Constant.playerCardLocationY[0]);
			add(gui.cardResource[index], 0);
			gui.enableMouseListener(index);
		}
		// show player 1~3 's back cards on the window
		for(int i = 1; i <= 3; i++)
		{
			drawBackCards(i, true);
		}
		// all players are not pass at first
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			remove(gui.passLabel[i]);
		}
		
		if(startingPlayer == -1)
			SystemFunc.throwException("cannot find CLUB 3 when dealing cards");
		
		return startingPlayer;
	}
	/**
	 * label the clicked card as chosen by the player and move up in the hand 
	 * 
	 * @param player which player is the human player that clicks the mouse, actually the player 0
	 * @param position which card in the hand is clicked
	 * @param index the index of the card (0~52, and 0 is the Joker)
	 * @return check whether the clicking player is player 0
	 */
	public boolean playerClickCard(int player, int position, int index)
	{
		// actually for player 0
		if(player == 0)
		{
			choose[position] = !choose[position];
			numChoose = choose[position]? numChoose + 1: numChoose - 1;
			int yLocation = choose[position]? 1: 0;
			gui.setCardLocation(index, 
					Constant.playerCardLocationX[position], Constant.playerCardLocationY[yLocation]);
			return true;
		}
		else
			return false;
	}
	/**
	 * help human player construct the Movement and call the doMove just like an agent will do 
	 * 
	 * @param player which player is the human player that press the Enter, actually the player 0
	 * @return check whether the clicking player is player 0
	 */
	public boolean playerPressedEnter(int player)
	{
		// actually for player 0
		if(player == 0 && numChoose > 0 && numChoose <= Constant.maxMovementCard)
		{
			// construct Movement from chosen card
			Card[] chosenCard = new Card[numChoose];
			int j = 0;
			for(int i = 0; i < Constant.numMaxHandCard; i++)
			{
				if(choose[i])
				{
					chosenCard[j++] = players[player].hand[i];
				}
			}
			Movement move = new Movement(chosenCard, isRevo);
			// cannot do the illegal move
			if(!Rule.isLegalMove(move, showMove, getIsRevo(), isStartGame))
			{
				return false;
			}
			// reset the is chosen boolean array if the move is legal and the move will be played
			numChoose = 0;
			for(int i = 0; i < Constant.numMaxHandCard; i++)
			{
				choose[i] = false;
			}
			System.out.println(move);
			// players[0].doMove();
			players[player].doMove(move);
			// SystemFunc.sleep(1000);
			return true;
		}
		else
			return false;
	}
	/**
	 * not done yet
	 * 
	 * @param player which player is the human player that press the Pass, actually the player 0
	 * @return check whether the clicking player is player 0
	 */
	public boolean playerPressedPass(int player)
	{
		if(player == 0)
		{
			Card[] pass = new Card[0];
			Movement passMovement = new Movement(pass, isRevo);
			// cannot do the illegal move
			if(!Rule.isLegalMove(passMovement, showMove, getIsRevo(), isStartGame))
			{
				return false;
			}
			players[player].doMove(passMovement);
			return true;
		}
		else
			return false;
	}
	/**
	 * show the chosen card on the middle of the window, and if the player is not human player, 
	 * it should decrease the covered cards in hand
	 * 
	 * @param move the movement done by the player 
	 * @param playerIndex the index of the player who do the movements
	 */
	public void doMove(Movement move, int playerIndex)
	{
		if(!Rule.isLegalMove(move, showMove, getIsRevo(), isStartGame))
		{
			SystemFunc.throwException("Illegal Move by player " + playerIndex);
		}
		// Record the move if Constant.RECORDING is set to true.
		else
		{
			// test GameState
//			if(!gs.doMove(playerIndex, move))
//			{
//				SystemFunc.throwException("Illegal Move Occur in GameState");
//			}
			
			if (Constant.RECORDING)
			{
				Card[] cards = move.getCards();
				// Update the played cards
				for (Card c : cards)
				{
					playedCards[ c.getRank() ] += 1;
				}
				playerMoves.get(playerIndex).add( toLogString(move, playerIndex) );
			}
			if (!hasWinner && players[playerIndex].numHandCards == 0)
			{
				for (int i = 0; i < 4; ++i)
				{
					int label = (i == playerIndex) ? 1 : 0;
					for (String s : playerMoves.get(i))
					{
						gameLog.println("" + label + " " + s);
					}
				}
				gameLog.flush();
				hasWinner = true;
			}
		}
		// move with 0 cards means player do the "pass" movement
		if(move.type == Constant.PASS) 
		{
			// draw passed label of the correspondent player
			drawPlayerPass(playerIndex);
			isPlayerPassed[playerIndex] = true;
		}
		else
		{
			if(playerIndex != 0)
			{
				// draw the back of cards
				drawBackCards(playerIndex, false);
			}
			// draw the cards show in the middle
			System.out.println("showMove before: " + showMove);
			drawShowCards(move);
			System.out.println("showMove after : " + showMove);
			for(int i = 0; i < move.numCards; i++)
			{
				if(history[move.cards[i].getIndex()])
					SystemFunc.throwException("card has shown before in Game doMove, Big trouble");
				history[move.cards[i].getIndex()] = true;
			}
			if(move.is4CardsRevo)
			{
				System.out.println("4 cards Revo");
				isRevo = !isRevo;
				is11Revo = false;
			}
			else if(move.has11Revo)
			{
				System.out.println("11 Revo");
				isRevo = !isRevo;
				is11Revo = true;
			}
			resetPass();
		}
		drawRevo();
		// repaint();
	}
	/** 
	 * A function that transfer player's current move into a
	 * LIBSVM data fromat.
	 * First 14 attritubes record player's hand (number of
	 * joker, Ace, 2...). Attritubes 15~29 record the played
	 * cards in game so far. And save as a sparse array.
	 *
	 * LIBSVM data format for a attritube like this
	 * 1 0 2 0
	 * is represented as 
	 * 1:1 3:2
	 *
	 * @param move the current player's move.
	 * @param playerIndex the player who play this move.
	 * @return String, a LIBSVM sparse data format showing 
	 *		current player's movement and game state.
	 */
	private String toLogString(Movement move, int playerIndex)
	{
		String s = "";
		Card[] cards = players[playerIndex].hand;
		int[] inHand = new int[14];
		// Cards in player's hand.
		for (Card c : cards)
		{
			if (c == null) continue;
			inHand[ c.getRank() ] += 1; // Rank 0~13
		}
		// Transfer how many card left to data format.
		for (int i = 0; i < 14; ++i)
		{
			if (inHand[i] > 0)
			{
				// for cards in hand, attribute index from 1~14
				s += Integer.toString(i+1) + ":" + inHand[i] + " ";
			}
		}
		// Cards that has been played in this game.
		for (int i = 0; i < 14; ++i)
		{
			if (playedCards[i] > 0)
			{
				// attribute index start from 15
				s += Integer.toString(i + 15) + ":" + playedCards[i] + " ";
			}
		}
                if (isRevo) s += "29:1 ";
                if (is11Revo) s += "30:1 ";

		return s;
	}
	private void drawPlayerPass(int playerIndex)
	{
		System.out.println("player pass");
		add(gui.passLabel[playerIndex], 0);
	}
	
	public void drawRevo()
	{
		if(isRevo)
			add(gui.revolution);
		else
			remove(gui.revolution);
	}
	
	private void removeShowCards()
	{
		if(showMove != null)
		{
			for(int i = 0; i < showMove.numCards; i++)
			{
				Card c = showMove.cards[i];
				remove(gui.cardResource[c.index]);
				showMove.cards[i] = null;
			}
			showMove.numCards = 0;
			showMove = null;
		}
	}
	
	/**
	 * show the movement card on the middle of the window, called by game.doMove()
	 * 
	 * @param move the movement done by the player 
	 */
	private void drawShowCards(Movement move)
	{
		if(showMove != null)
		{
			removeShowCards();
		}
		int count = 0;
		showMove = move;
		for(int i = 0; i < move.numCards; i++)
		{
			count++;
			Card c = showMove.cards[i];
			gui.setCardLocation(c.index, 
					Constant.showLocationX0 + count * Constant.showLocationDX / (showMove.numCards + 1), 
					Constant.showLocationY);
			gui.disableMouseListener(c.index);
			add(gui.cardResource[c.index], 0);
			// System.out.println(c);
		}
	}
	/**
	 * draw the cards in the player' s hand, called by called by game.doMove()
	 * 
	 * @param playerIndex the index of player whose cards are drawn
	 * @param isInit whether is called when dealing the card
	 */
	private void drawBackCards(int playerIndex, boolean isInit)
	{
		int index = playerIndex - 1;
		if(isInit)
		{
			int width = (playerIndex % 2 == 0)? Constant.cardWidth: Constant.cardHeight;
			int height = (playerIndex % 2 == 0)? Constant.cardHeight: Constant.cardWidth;
			for(int i = 0; i < Constant.numMaxHandCard; i++)
			{
				if(i < players[playerIndex].numHandCards)
				{
					int x = (playerIndex % 2 == 0)? 
							Constant.playerCardBack[index][0] + (i + 1) * Constant.playerCardBack[index][1] / players[playerIndex].numHandCards:
							Constant.playerCardBack[index][2];
					int y = (playerIndex % 2 == 1)? 
							Constant.playerCardBack[index][0] + (i + 1) * Constant.playerCardBack[index][1] / players[playerIndex].numHandCards:
							Constant.playerCardBack[index][2];
					gui.cardBack[index][i].setBounds(x, y, width, height);
					add(gui.cardBack[index][i], 0);
				}
			}
		}
		else
		{
			for(int i = players[playerIndex].numHandCards; i < Constant.numMaxHandCard; i++)
			{
				remove(gui.cardBack[index][i]);
			}
		}
	}
	
	/**
	 * start playing the game begin from the player whose index is "turn"
	 */
	public void runInSimulation()
	{
		int turn = deal();
		isRevoBeforeRound = false;
		printAllPlayerCards();

		// test GameState
//		gs = new GameState(players[0].hand, showMove, isRevo, is11Revo, turn, GameState.initHistory(), 0, true, 0, 
//				Constant.numPlayer, new int[]{players[0].numHandCards, players[1].numHandCards, players[2].numHandCards, players[3].numHandCards}, 
//				false);
		
		while(!isGameEnd)
		{
			isRevoBeforeRound = isRevo;
			turn = runRoundInSimulation(turn);
			System.out.println("========Round end");
			// remove the show cards in the middle
			removeShowCards();
			// if 11 Revo happen in this round then need reset the isRevo
			if(is11Revo)
			{
				isRevo = isRevoBeforeRound;
				is11Revo = false;
			}
			drawRevo();
			// repaint();
			SystemFunc.sleep(2000);
			if(numRemainPlayer() == 1)
			{
				isGameEnd = true;
				break;
			}
			if(turn == -1)
			{
				SystemFunc.throwException("Error: turn == -1");
			}
		}
		System.out.println("========Game end");
	}
	
	/**
	 * start playing the game begin from the player whose index is "turn"
	 */
	public void run()
	{
		int turn = deal();
		isRevoBeforeRound = false;
		printAllPlayerCards();

		// test GameState
//		gs = new GameState(players[0].hand, showMove, isRevo, is11Revo, turn, GameState.initHistory(), 0, true, 0, 
//				Constant.numPlayer, new int[]{players[0].numHandCards, players[1].numHandCards, players[2].numHandCards, players[3].numHandCards}, 
//				false);
		
		while(!isGameEnd)
		{
			isRevoBeforeRound = isRevo;
			turn = runRound(turn);
			System.out.println("========Round end");
			// remove the show cards in the middle
			removeShowCards();
			// if 11 Revo happen in this round then need reset the isRevo
			if(is11Revo)
			{
				isRevo = isRevoBeforeRound;
				is11Revo = false;
			}
			drawRevo();
			// repaint();
			SystemFunc.sleep(2000);
			if(numRemainPlayer() == 1)
			{
				isGameEnd = true;
				break;
			}
			if(turn == -1)
			{
				SystemFunc.throwException("Error: turn == -1");
			}
		}
		System.out.println("========Game end");
	}
	/**
	 * wait the human player to choose the cards and press the key
	 */
	public void waitPlayer0()
	{
		// System.out.println("Sleep");
		SystemFunc.sleep(500);
	}

	public void resetPass()
	{
		// remove all the pass label
		// set all player pass boolean to false
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			remove(gui.passLabel[i]);
			isPlayerPassed[i] = false;
		}
	}
	public int numRemainPlayer()
	{
		int count = 0;
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			if(!players[i].isFinish())
				count++;
		}
		return count;
	}
	
	
	public Movement getShowMove()
	{
		if(showMove == null)
			return null;
		
		return new Movement(showMove);
	}
	
	private int findNextPlayerWithHandCards(int cur)
	{
		int nextStartTurn = -1;
		int i = (cur + 1) % Constant.numPlayer;
		while(i != cur)
		{
			if(!players[i].isFinish())
			{
				nextStartTurn = i;
				break;
			}
			i = (i + 1) % Constant.numPlayer;
		}
		return nextStartTurn;
	}

	public void printAllPlayerCards()
	{
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			System.out.println("player " + i + ": ");
			for(int j = 0; j < Constant.numMaxHandCard; j++)
			{
				if(players[i].hand[j] == null) continue;
				System.out.print(players[i].hand[j] + " ");
			}
			System.out.println();
		}
	}
	
	public int runRound(int initTurn)
	{
		// init the round
		passCount = 0;
		int turn = initTurn; /*note that the player who is the first player in the round is not allowed to pass*/
		int nextRoundStart = -1;
		isRoundEnd = false;
		int turn11Record = -1;
		
		// start the round 
		while(!isRoundEnd)
		{
			if(players[turn].isFinish())
			{
				turn = (turn + 1) % Constant.numPlayer;
				continue;
			}
			
			// all other players pass
			if(passCount == numRemainPlayer() - 1)
			{
				isRoundEnd = true;
				nextRoundStart = turn;
				break;
			}
			// player decide a movement
			System.out.println("turn " + turn + ", isGameStart: " + isStartGame + ", is11Revo: " + is11Revo + 
					", isRevo: " + isRevo);
			if(turn == 0)
			{
				addKeyListener(keyController);
				while(!player0Fin)
				{
					waitPlayer0();
				}
				removeKeyListener(keyController);
				player0Fin = false;
			}
			else
			{
				players[turn].takeTurn();
			}
			// after player do a moment
			isStartGame = false;
			if(!isPlayerPassed[turn] && players[turn].isFinish())
			{
				// a player has no hand cards after do a non-pass movement (ie, this player finishes)
				isRoundEnd = true;
				nextRoundStart = findNextPlayerWithHandCards(turn);
				break;
			}
			else if(!isPlayerPassed[turn] && showMove.has8Cut)
			{
				// a player plays a combination which is 8 cut
				isRoundEnd = true;
				nextRoundStart = turn;
				break;
			}
			if(isPlayerPassed[turn])
				passCount++;
			else
				passCount = 0;
			turn = (turn + 1) % Constant.numPlayer;
		}
		return nextRoundStart;
	}
	
	public int runRoundInSimulation(int initTurn)
	{
		// init the round
		passCount = 0;
		int turn = initTurn; /*note that the player who is the first player in the round is not allowed to pass*/
		int nextRoundStart = -1;
		isRoundEnd = false;
		int turn11Record = -1;
		
		// start the round 
		while(!isRoundEnd)
		{
			if(players[turn].isFinish())
			{
				turn = (turn + 1) % Constant.numPlayer;
				continue;
			}
			
			// all other players pass
			if(passCount == numRemainPlayer() - 1)
			{
				isRoundEnd = true;
				nextRoundStart = turn;
				break;
			}
			// player decide a movement
			System.out.println("turn " + turn + ", isGameStart: " + isStartGame + ", is11Revo: " + is11Revo + 
					", isRevo: " + isRevo);
			
			long startTime = System.currentTimeMillis();
			simulationRecordData record = players[turn].takeTurn();
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			
			// update the recorded time
			total[turn].update(elapsedTime);
			if(record != null && record.curNumLegalMove != 1)
				thinking[turn].update(elapsedTime);
			
			// after player do a moment
			isStartGame = false;
			if(!isPlayerPassed[turn] && players[turn].isFinish())
			{
				// a player has no hand cards after do a non-pass movement (ie, this player finishes)
				isRoundEnd = true;
				if(playerStat[turn] != 4)
					SystemFunc.throwException("player multiple finish in runRoundSimulation");
				playerStat[turn] = place;
				place++;
				nextRoundStart = findNextPlayerWithHandCards(turn);
				break;
			}
			else if(!isPlayerPassed[turn] && showMove.has8Cut)
			{
				// a player plays a combination which is 8 cut
				isRoundEnd = true;
				nextRoundStart = turn;
				break;
			}
			if(isPlayerPassed[turn])
				passCount++;
			else
				passCount = 0;
			turn = (turn + 1) % Constant.numPlayer;
		}
		return nextRoundStart;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		repaint();  
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(gui.background, 0, 0, Constant.width, Constant.height, this);
		// System.out.println("paint");
	}
	private class KeyController extends KeyAdapter
	{
		boolean isEnter;
		boolean isPass;
		public KeyController()
		{
			super();
			isEnter = false;
			isPass = false;
		}
		@Override
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER && !isEnter)
			{
				Player.genLegalMove(showMove, players[0].hand, isRevo, isStartGame);
				isEnter = true;
				player0Fin = playerPressedEnter(0);
			}
			if(key == KeyEvent.VK_P && !isPass)
			{
				isPass = true;
				player0Fin = playerPressedPass(0);
			}
		}
		@Override
		public void keyReleased(KeyEvent e)
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER && isEnter)
			{
				isEnter = false;
			}
			else if(key == KeyEvent.VK_P && isPass)
			{
				isPass = false;
			}
		}
	}
}

class timeMeasure
{
	private long time;
	private int count;
	
	public timeMeasure()
	{
		this.time = 0;
		this.count = 0;
	}
	
	public double mean()
	{
		return ((double)time / (double)count) * 0.001;
	}
	
	public void update(long add)
	{
		this.time = this.time + add;
		this.count = this.count + 1;
	}
	
	public void update(long addTime, int addCount)
	{
		this.time = this.time + addTime;
		this.count = this.count + addCount;
	}
	
	public long getTime()
	{
		return this.time;
	}
	public int getCount()
	{
		return this.count;
	}
}

class playerAttr
{
	private int agentType;
	private int[] attr;
	
	playerAttr(int type, int[] attr)
	{
		this.agentType = type;
		this.attr = attr;
	}
	public int getType()
	{
		return this.agentType;
	}
	public int[] getAttr()
	{
		return this.attr;
	}
	public String toString()
	{
		String s = "";
		
		switch(agentType)
		{
		case Constant.AlphaBetaAgent:
			s += "AlphaBetaAgent" + " ";
			break;
		case Constant.MMTSAgent:
			s += "MMTSAgent" + " ";
			break;
		case Constant.MCTSAgent:
			s += "MCTSAgent" + " ";
			break;
		case Constant.RandomAgent:
			s += "RandomAgent" + " ";
			break;
		case Constant.HeuristicAgent:
			s += "HeuristicAgent" + " ";
			break;
		case Constant.AlphaBetaWithZeroAgent:
			s += "AlphaBetaWithZeroAgent" + " ";
			break;
		}
		for(int i = 0; i < attr.length; i++)
			s += (attr[i] + ", ");
		return s;
	}
}












