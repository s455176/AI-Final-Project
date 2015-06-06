import javax.swing.*;
import java.io.*;

public class MainFrame extends JFrame
{
	public Game game;
	public MainFrame() throws IOException
	{
		game = new Game();
		add(game);
		setSize(Constant.width, Constant.height);
		setTitle("AI_FINAL");
		setResizable(false);
		setVisible(true);
	}
	public static void main(String[] args) throws IOException
	{
		MainFrame mainFrame = new MainFrame();
		mainFrame.game.run();
	}
}
