package BattleshipGame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

/**
 * ClientHandler generate the thread for the program and manage the transaction between clients and server.
 *
 * @author Raesup Kim
 * @version Oct.4 2020
 */
public class ClientHandler implements Runnable, PropertyChangeListener {

	Socket socket1 = null;
	Socket socket2 = null;

	ObjectOutputStream oos1 = null;
	ObjectOutputStream oos2 = null;

	InputListener lis1 = null;
	InputListener lis2 = null;

	int id1 = 1;
	int id2 = 2;

	/**
	 * ClientHandler - get sockets information from server and manage the clients.
	 * 
	 * @param socket1 The socket value for client 1
	 * @param socket2 The socket value for client 2
	 */
	public ClientHandler(Socket socket1, Socket socket2)
	{
		this.socket1 = socket1;
		this.socket2 = socket2;
	}

	/**
	 * run - run the threads for the server.
	 */
	@Override
	public void run()
	{
		try
		{			
			oos1 = new ObjectOutputStream(socket1.getOutputStream());	
			oos2 = new ObjectOutputStream(socket2.getOutputStream());

			lis1 = new InputListener(socket1, id1);
			lis2 = new InputListener(socket2, id2);

			lis1.addListener(this);
			lis2.addListener(this);

			Thread t1 = new Thread(lis1);
			t1.start();

			Thread t2 = new Thread(lis2);
			t2.start();
			
			controllClients();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * controllClients - let clients know that they are ready to play and control the turns.
	 */
	public void controllClients()
	{
		try
		{	
			Message welcomeMsg = new Message("You got paired with other player!!\nPlease select the ships and place them on the fleet map!");
			oos1.writeObject(welcomeMsg);
			oos2.writeObject(welcomeMsg);

			Message readyMsg = new Message("Users are ready to play!!");
			ServerDriver.serverMessage(readyMsg.toString());
			
			Controller turn = new Controller(0);
			Random rand = new Random();
			int firstTurnClient = rand.nextInt(2);
			
			if (firstTurnClient == 0)
			{
				oos1.writeObject(turn);
				ServerDriver.serverMessage("Client 1 " + turn.toString());
			}
			else
			{
				oos2.writeObject(turn);
				ServerDriver.serverMessage("Client 2 " + turn.toString());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * propertyChange - notify the change from the subject and make it change to new value.
	 * 
	 * @param evt - the value which holds the changing event information.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		Object newObj = evt.getNewValue();
		String property = evt.getPropertyName();

		try
		{
			if (newObj instanceof Message)
			{
				if (property.equals("1"))
				{
					Message newMsg = new Message("Client 1: " + evt.getNewValue());
					oos1.writeObject(newMsg);
					oos2.writeObject(newMsg);
					ServerDriver.serverMessage(newMsg.toString());
				}
				else
				{
					Message newMsg = new Message("Client 2: " + evt.getNewValue());
					oos2.writeObject(newMsg);
					oos1.writeObject(newMsg);
					ServerDriver.serverMessage(newMsg.toString());
				}
			}
			else if (newObj instanceof BoatPosition)
			{
				if (property.equals("1"))
				{
					oos2.writeObject(newObj);
					ServerDriver.serverMessage(newObj.toString());
				}
				else
				{
					oos1.writeObject(newObj);
					ServerDriver.serverMessage(newObj.toString());
				}
			}
			else if (newObj instanceof Result)
			{
				if (property.equals("1"))
				{
					oos2.writeObject(newObj);
					ServerDriver.serverMessage(newObj.toString());
				}
				else
				{
					oos1.writeObject(newObj);
					ServerDriver.serverMessage(newObj.toString());
				}
			}
			else
			{
				if (property.equals("1"))
				{
					oos2.writeObject(newObj);
					ServerDriver.serverMessage("Client 1 " + newObj.toString());
				}
				else
				{
					oos1.writeObject(newObj);
					ServerDriver.serverMessage("Client 2 " + newObj.toString());
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}
