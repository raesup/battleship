package BattleshipGame;

import java.io.Serializable;

/**
 * Controller controls various status to manage the program.
 *
 * @author Raesup Kim
 * @version Oct.4 2020
 */
public class Controller implements Serializable {

	private static final long serialVersionUID = 5L;
	
	int i;
	
	/**
	 * Controller - User defined constructor.
	 *
	 * @param i integer value
	 */
	public Controller (int i)
	{
		this.i = i;
	}
	
	/**
	 * @return integer i
	 */
	public int getI() {
		return i;
	}

	/**
	 * @param i integer value
	 */
	public void setI(int i) {
		this.i = i;
	}
	
	/**
	 * @return string values for the messages.
	 */
	public String toString()
	{
		String s = "";
		if (i == 0)
		{
			s = "got the first turn";
		}
		else if (i == 1)
		{
			s = "placed all ships!";
		}
		return s;
	}
}
