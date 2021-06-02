package BattleshipGame;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;

import javax.swing.*;
import java.awt.Font;

/**
 * ServerDriver generate the GUI of the server and run the server for the battleship
 *
 * @author Raesup Kim
 * @version Oct.4 2020
 */
public class ServerDriver extends JFrame {

	private static final long serialVersionUID = 3L;
	
	final int WIDTH = 500;
	final int HEIGHT = 500;
	
	static JTextArea serverBoard;
	
	JLabel serverLabel;
	
	JPanel serverPanel;
	
	String message;
	
	/**
	 * ServerDriver - Creates the components for server GUI.
	 */
	public ServerDriver()
	{
		setTitle("Battleship Game Server");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		serverPanel = new JPanel();
		serverPanel.setLayout(new BorderLayout());
		
		serverBoard = new JTextArea();
		serverBoard.setColumns(58);
		serverBoard.setRows(23);
		serverPanel.add(serverBoard, BorderLayout.CENTER);
		
		serverLabel = new JLabel("Server");	
		serverLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		serverPanel.add(serverLabel, BorderLayout.NORTH);
		
		getContentPane().add(serverPanel);
		
		super.setVisible(true);
	}
	
	/**
	 * serverMessage - send the chat and connection messages to the server board.
	 * 
	 * @param message The string value of the message
	 */
	public static void serverMessage(String message)
	{
		serverBoard.append(message+"\n");
	}
	
	/**
	 * main - Main method which run the server.
	 */
	public static void main(String[] args) {
		
		new ServerDriver();
		
		ArrayList<Socket> socketList = new ArrayList<>();
		ServerSocket ss = null;
		Socket socket = null;

		try
		{
			ss = new ServerSocket(3333);
			serverMessage("The server is up and running!!");

			while(true)
			{
				socket = ss.accept();
				serverMessage("Client connection is accepted.");

				socketList.add(socket);	
				
				for (int i=0; i<socketList.size(); i++)
				{
					if (i%2 == 1)
					{
						ClientHandler clients = new ClientHandler(socketList.get(i-1), socketList.get(i));
						
						Thread t1 = new Thread(clients);
						t1.start();
						
						socketList.clear();
						
						serverMessage("Clients have got paired.");
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}
