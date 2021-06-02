package BattleshipGame;

import java.io.Serializable;

/**
 * Result gets string variable for the results of the clients turn.
 *
 * @author Raesup Kim
 * @version Oct.4 2020
 */
public class Result implements Serializable {

private static final long serialVersionUID = 5L;
	
	String result = null;
	
	/**
	 * Result - User defined constructor.
	 * 
	 * @param result
	 */
	public Result (String result)
	{
		this.result = result;
	}

	/**
	 * @return results
	 */
	public String getResult()
	{
		return result;
	}

	/**
	 * @param result the results
	 */
	public void setResult(String result)
	{
		this.result = result;
	}
	
	/**
	 * @return string values for the results.
	 */
	public String toString()
	{
		return getResult();
	}
}
