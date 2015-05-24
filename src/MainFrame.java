import javax.swing.*;;

public class MainFrame extends JFrame
{
	public MainFrame()
	{
		add(new Game());
        setSize(Constant.width, Constant.height);
        setTitle("AI_FINAL");
        setResizable(false);
        setVisible(true);
	}
	public static void main(String[] args)
	{
		new MainFrame();
	}
}
