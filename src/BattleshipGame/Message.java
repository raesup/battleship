package BattleshipGame;

import java.io.Serializable;

/**
 * Message gets string value for the messages and stores them.
 *
 * @author Raesup Kim
 * @version Oct.4 2020
 */
public class Message implements Serializable {

private static final long serialVersionUID = 1L;
	
	String message = null;
	
	/**
	 * Message - User defined constructor
	 * 
	 * @param message the message
	 */
	public Message (String message)
	{
		this.message = message;
	}

	/**
	 * @return message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @param message the message
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	/**
	 * @return string values for the messages.
	 */
	public String toString()
	{
		return getMessage();
	}
}
