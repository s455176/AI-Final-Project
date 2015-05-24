import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class GUIResource
{
	// attribute
	public CardLabel[] cardResource;
	public Image background;
	private Game game;
	public JLabel[][] cardBack;

	// method 
	public GUIResource(Game game)
	{
		cardResource = new CardLabel[Constant.MAX_NUM_CARD];
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
		{
			ImageIcon ii = new ImageIcon(this.getClass().getResource(Constant.filePrefix + i + ".gif"));
			cardResource[i] = new CardLabel(ii, i);
		}
		ImageIcon bg_ii = new ImageIcon(this.getClass().getResource("images/background.jpg"));
		background = bg_ii.getImage();
		cardBack = new JLabel[Constant.numPlayer - 1][Constant.numMaxHandCard];
		for(int i = 0; i < Constant.numPlayer - 1; i++)
		{
			for(int j = 0; j < Constant.numMaxHandCard; j++)
			{
				if(i % 2 == 0)
					cardBack[i][j] = new JLabel(new ImageIcon(this.getClass().getResource("images/cards/back_horizontal.gif")));
				else
					cardBack[i][j] = new JLabel(new ImageIcon(this.getClass().getResource("images/cards/back_vertical.gif")));
			}
		}
		this.game = game;
	}
	public void setCardLocation(int index, int x, int y)
	{
		cardResource[index].setLocation(x, y);
	}
	public void setPlayerAndPos(int index, int player, int position)
	{
		cardResource[index].setPlayerAndPos(player, position);
	}
	public void enableMouseListener(int index)
	{
		cardResource[index].enableMouseListener();
	}
	public void disableMouseListener(int index)
	{
		cardResource[index].disableMouseListener();
	}
	private class CardLabel extends JLabel
	{
		private MouseController mouseController;
		private int player;
		private int position;
		private int index;
		private int x, y;
		
		public CardLabel(ImageIcon ii, int index)
		{
			super(ii);
			mouseController = new MouseController();
			player = -1;
			position = -1;
			x = -1;
			y = -1;
			this.index = index;
		}
		public void enableMouseListener()
		{
			addMouseListener(mouseController);
		}
		public void disableMouseListener()
		{
			removeMouseListener(mouseController);
		}
		public void setLocation(int x, int y)
		{
			this.x = x;
			this.y = y;
			setBounds(this.x, this.y, Constant.cardWidth, Constant.cardHeight);
		}
		public void setPlayerAndPos(int player, int position)
		{
			this.player = player;
			this.position = position;
		}
		
		class MouseController extends MouseAdapter
		{
		    public void mouseClicked(MouseEvent e)
		    {
		    	game.playerClickCard(0, position, index);
		    }
		}
	}
}


