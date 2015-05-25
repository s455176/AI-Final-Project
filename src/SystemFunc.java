import java.io.PrintStream;


public class SystemFunc
{
	public static void sleep(long millis)
	{
		try
		{
		    Thread.sleep(millis);
		}
		catch (InterruptedException e)
		{
		    e.printStackTrace();
		    System.exit(-1);
		}
	}
	public static void throwException(String s)
	{
		try
		{
			throw new Exception(s);
		}
		catch(Exception e)
		{
			e.printStackTrace(new PrintStream(System.err));
			System.exit(-1);
		}
	}
}
