package BattleshipGame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * InputListener listens to the clients and server and read the objects, respond to observers.
 *
 * @author Raesup Kim
 * @version Oct.4 2020
 */
public class InputListener implements Runnable {

	Message message;
	BoatPosition bp;
	Socket socket = null;
	ObjectInputStream ois = null;
	int id;
	
	public ArrayList<PropertyChangeListener> listeners = new ArrayList<>();

	/**
	 * InputListener - creates input listener.
	 * 
	 * @param socket The socket value for clients or server
	 * @param id The integer value to identify clients.
	 */
	public InputListener (Socket socket, int id)
	{
		this.socket = socket;
		this.id = id;
	}
	
	/**
	 * getId - returns id value.
	 * 
	 * @return id for the identification
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * addListener - adds observer to array list.
	 * 
	 * @param newListener The observer value to be notified.
	 */
	public void addListener(PropertyChangeListener newListener)
	{
		listeners.add(newListener);
	}
	
	public void removeListener(PropertyChangeListener newListener)
	{
		listeners.remove(newListener);
	}
	
	/**
	 * notifyListeners - notify the changes to observers.
	 * 
	 * @param property The string value about property
	 * @param newValue The object for new value.
	 */
	public void notifyListeners(String property, Object newValue)
	{
		for( PropertyChangeListener listener : listeners )
		{
			listener.propertyChange(new PropertyChangeEvent(this, property, null, newValue));
		}
	}

	/**
	 * run - run the threads for the server.
	 */
	@Override
	public void run() 
	{
		try
		{
			ois = new ObjectInputStream(socket.getInputStream());
			
			while(true)
			{
				Object newObj = ois.readObject();
				notifyListeners(Integer.toString(id), newObj);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
}
