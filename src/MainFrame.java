import javax.swing.*;;

public class MainFrame extends JFrame
{
	public Game game;
	public MainFrame()
	{
		game = new Game();
		add(game);
        setSize(Constant.width, Constant.height);
        setTitle("AI_FINAL");
        setResizable(false);
        setVisible(true);
	}
	public static void main(String[] args)
	{
		MainFrame mainFrame = new MainFrame();
		mainFrame.game.run();
	}
}
