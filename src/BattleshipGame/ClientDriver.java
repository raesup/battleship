package BattleshipGame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import javax.swing.border.LineBorder;

/**
 * ClientDriver generate the GUI of the client and run the game for the battleship
 *
 * @author Raesup Kim
 * @version Oct.4 2020
 */
public class ClientDriver extends JFrame implements PropertyChangeListener{

	private static final long serialVersionUID = 2L;

	Socket socket = null;
	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;

	static InputListener lis = null;

	Message message = null;
	BoatPosition bp;
	BoatPosition bpTarget;
	BoatPosition bp2;

	final int WIDTH = 1000;
	final int HEIGHT = 1000;

	private JPanel targetPanel;
	private JPanel fleetPanel;
	private JPanel chatPanel;
	private JPanel settingPanel;

	private JTextField messageSend;

	private JTextArea messageBoard;

	private JLabel chat;
	private JLabel targetMap;
	private JLabel fleetMap;
	private JLabel setting;
	private JLabel myScoreLabel;
	private JLabel yourScoreLabel;

	private JButton aircraftButton;
	private JButton battleshipButton;
	private JButton cruiserButton;
	private JButton submarineButton;
	private JButton destroyerButton;
	private JButton sendMessageButton;
	private JButton startButton;
	private JButton quitButton;

	public static JButton[][] buttonAlly = new JButton[10][10];
	public static JButton[][] buttonEnemy = new JButton[10][10];

	int rows;
	int columns;
	int myScore = 17;
	int yourScore = 17;

	public int[][] boatPosition = new int[10][10];
	public int[][] boatPositionEnemy = new int[10][10];

	boolean aircraftChecked = false;
	boolean battleshipChecked = false;
	boolean cruiserChecked = false;
	boolean submarineChecked = false;
	boolean destroyerChecked = false;
	boolean putAllyShips = false;
	boolean putEnemyShips = false;
	boolean myTurn = false;
	boolean win = false;

	/**
	 * ClientDriver - Creates the components for client GUI.
	 */
	public ClientDriver()
	{
		setTitle("Battleship Game Client");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		targetPanel = createTargetPanel();
		getContentPane().add(targetPanel);

		fleetPanel = createFleetPanel();
		getContentPane().add(fleetPanel);

		chatPanel = createChatPanel();
		getContentPane().add(chatPanel);

		settingPanel = createSettingPanel();
		getContentPane().add(settingPanel);

		super.setVisible(true);

	}

	/**
	 * createTargetPanel - creates the target map.
	 * 
	 * @return JPanel for the target map.
	 */
	private JPanel createTargetPanel()
	{
		JPanel targetPanel = new JPanel();
		targetPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		targetPanel.setBounds(12, 13, 600, 450);
		targetPanel.setLayout(new BorderLayout());

		JPanel panelGrid = new JPanel();
		panelGrid.setLayout(new GridLayout(10, 10));

		targetMap = new JLabel("TARGET MAP");
		targetMap.setFont(new Font("Tahoma", Font.BOLD, 15));

		for (rows = 0; rows < 10; rows++)
		{
			for (columns = 0; columns < 10; columns++)
			{
				buttonEnemy[rows][columns] = new JButton(rows + " " + columns);
				buttonEnemy[rows][columns].setBackground(Color.GRAY);
				buttonEnemy[rows][columns].setPreferredSize(new Dimension(55, 40));
				buttonEnemy[rows][columns].setEnabled(false);

				buttonEnemy[rows][columns].addActionListener(new gridButtonPressedListener(rows, columns));
				panelGrid.add(buttonEnemy[rows][columns]);
			}
		}

		targetPanel.add(targetMap, BorderLayout.NORTH);
		targetPanel.add(panelGrid, BorderLayout.CENTER);

		return targetPanel;
	}

	/**
	 * createFleetPanel - creates the fleet map.
	 * 
	 * @return JPanel for the fleet map.
	 */
	private JPanel createFleetPanel()
	{
		JPanel fleetPanel = new JPanel();
		fleetPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		fleetPanel.setBounds(12, 472, 600, 450);
		fleetPanel.setLayout(new BorderLayout());

		JPanel panelGrid = new JPanel();
		panelGrid.setLayout(new GridLayout(10, 10));

		fleetMap = new JLabel("FLEET MAP");
		fleetMap.setFont(new Font("Tahoma", Font.BOLD, 15));

		for (rows = 0; rows < 10; rows++)
		{
			for (columns = 0; columns < 10; columns++)
			{
				buttonAlly[rows][columns] = new JButton(rows + " " + columns);
				buttonAlly[rows][columns].setBackground(Color.GRAY);
				buttonAlly[rows][columns].setPreferredSize(new Dimension(55, 40));
				buttonAlly[rows][columns].setEnabled(false);

				panelGrid.add(buttonAlly[rows][columns]);
			}
		}

		fleetPanel.add(fleetMap, BorderLayout.NORTH);
		fleetPanel.add(panelGrid, BorderLayout.CENTER);

		return fleetPanel;
	}

	/**
	 * createChatPanel - creates chat board.
	 * 
	 * @return JPanel for the board.
	 */
	private JPanel createChatPanel()
	{
		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		chatPanel.setBounds(620, 375, 350, 547);
		chatPanel.setLayout(new BorderLayout());

		JPanel panelMessageBoard = new JPanel();
		panelMessageBoard.setLayout(null);
		messageBoard = new JTextArea();
		messageBoard.setEditable(false);
		messageBoard.setBounds(12, 10, 324, 511);
		panelMessageBoard.add(messageBoard);

		JPanel panelMessageSend = new JPanel();
		panelMessageSend.setLayout(new FlowLayout());

		messageSend = new JTextField();
		messageSend.setColumns(22);
		sendMessageButton = new JButton("SEND");
		sendMessageButton.setFont(new Font("Tahoma", Font.BOLD, 15));
		sendMessageButton.addActionListener(new sendButtonListener());

		panelMessageSend.add(messageSend);
		panelMessageSend.add(sendMessageButton);

		chat = new JLabel("CHAT");
		chat.setFont(new Font("Tahoma", Font.BOLD, 15));

		chatPanel.add(chat, BorderLayout.NORTH);
		chatPanel.add(panelMessageBoard, BorderLayout.CENTER);
		chatPanel.add(panelMessageSend, BorderLayout.SOUTH);

		return chatPanel;
	}

	/**
	 * createSettingPanel - creates the setting board.
	 * 
	 * @return JPanel for the setting board.
	 */
	private JPanel createSettingPanel()
	{
		JPanel settingPanel = new JPanel();
		settingPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		settingPanel.setBounds(620, 13, 350, 356);
		settingPanel.setLayout(new BorderLayout());

		JPanel scorePanel = new JPanel();
		myScoreLabel = new JLabel("Ally's remaing battleship: " + myScore + " / 17");
		myScoreLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		myScoreLabel.setBounds(12, 13, 300, 16);
		yourScoreLabel = new JLabel("Enemy's remaing battleship: " + yourScore + " /17");
		yourScoreLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		yourScoreLabel.setBounds(12, 45, 300, 16);

		aircraftButton = new JButton("Aircraft Carrier: 5s");
		aircraftButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		aircraftButton.setBounds(12, 84, 300, 30);
		aircraftButton.setEnabled(false);
		aircraftButton.addActionListener(new shipButtonListener("aircraft"));

		battleshipButton = new JButton("Battleship: 4s");
		battleshipButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		battleshipButton.setBounds(12, 124, 240, 30);
		battleshipButton.setEnabled(false);
		battleshipButton.addActionListener(new shipButtonListener("battleship"));

		cruiserButton = new JButton("Cruiser: 3s");
		cruiserButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		cruiserButton.setBounds(12, 164, 180, 30);
		cruiserButton.setEnabled(false);
		cruiserButton.addActionListener(new shipButtonListener("cruiser"));

		submarineButton = new JButton("Submarine: 3s");
		submarineButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		submarineButton.setBounds(12, 204, 180, 30);
		submarineButton.setEnabled(false);
		submarineButton.addActionListener(new shipButtonListener("submarine"));

		destroyerButton = new JButton("Destroyer: 2s");
		destroyerButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		destroyerButton.setBounds(12, 244, 136, 30);
		destroyerButton.setEnabled(false);
		destroyerButton.addActionListener(new shipButtonListener("destroyer"));

		scorePanel.setLayout(null);
		scorePanel.add(myScoreLabel);
		scorePanel.add(yourScoreLabel);
		scorePanel.add(aircraftButton);
		scorePanel.add(battleshipButton);
		scorePanel.add(cruiserButton);
		scorePanel.add(submarineButton);
		scorePanel.add(destroyerButton);

		JPanel settingButtonPanel = new JPanel();
		FlowLayout fl_settingButtonPanel = new FlowLayout();
		fl_settingButtonPanel.setHgap(40);
		settingButtonPanel.setLayout(fl_settingButtonPanel);

		setting = new JLabel("STATUS");
		setting.setFont(new Font("Tahoma", Font.BOLD, 15));
		settingPanel.add(setting, BorderLayout.NORTH);

		startButton = new JButton("Start/Queue");
		startButton.setFont(new Font("Tahoma", Font.BOLD, 15));
		startButton.addActionListener(new startButtonListener());

		quitButton = new JButton("Quit");
		quitButton.setFont(new Font("Tahoma", Font.BOLD, 15));
		quitButton.addActionListener(new quitButtonListener());

		settingButtonPanel.add(startButton);
		settingButtonPanel.add(quitButton);

		settingPanel.add(scorePanel, BorderLayout.CENTER);
		settingPanel.add(settingButtonPanel, BorderLayout.SOUTH);

		return settingPanel;
	}

	/**
	 * sendButtonListener - Inner action listener class that listens for a send button to be clicked.
	 * 
	 * @author Raesup Kim
	 * @version Oct.4 2020
	 */
	private class sendButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{		
			String msg = messageSend.getText();
			message = new Message(msg);

			try
			{
				oos.writeObject(message);
				messageSend.setText("");
			}
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}		
		}
	}

	/**
	 * startButtonListener - Inner action listener class that listens for a start button to be clicked.
	 * 
	 * @author Raesup Kim
	 * @version Oct.4 2020
	 */
	private class startButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				socket = new Socket("localhost", 3333);
				oos = new ObjectOutputStream(socket.getOutputStream());
				lis = new InputListener(socket, 0);
				lis.addListener(ClientDriver.this);

				Thread t1 = new Thread(lis);
				t1.start();

				aircraftButton.setEnabled(true);
				battleshipButton.setEnabled(true);
				cruiserButton.setEnabled(true);
				submarineButton.setEnabled(true);
				destroyerButton.setEnabled(true);
				startButton.setEnabled(false);
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	/**
	 * quitButtonListener - Inner action listener class that listens for a quit button to be clicked.
	 * 
	 * @author Raesup Kim
	 * @version Oct.4 2020
	 */
	private class quitButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				oos.close();
				ois.close();
				socket.close();
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	/**
	 * shipButtonListener - Inner action listener class that listens for a ship button to be clicked.
	 * 
	 * @author Raesup Kim
	 * @version Oct.4 2020
	 */
	private class shipButtonListener implements ActionListener
	{
		String ship1;
		boolean vertical;
		boolean inBound;
		boolean notOverlap;
		int inBoundNumber;
		int overlapNumber;

		public shipButtonListener (String ship1)
		{
			this.ship1 = ship1;
		}

		public void actionPerformed(ActionEvent e)
		{
			String s = JOptionPane.showInputDialog("Would you put the ship vertically? y/n");
			if (s.equals("y"))
			{
				vertical = true;
			}
			else
			{
				vertical = false;
			}

			int rows = 0 ;
			int columns = 0;
			inBound = false;
			notOverlap = false;

			while (!inBound || !notOverlap)
			{
				overlapNumber = 0;

				while (true)
				{
					try
					{
						rows = Integer.parseInt(JOptionPane.showInputDialog("What is the row number 0-9"));
						columns = Integer.parseInt(JOptionPane.showInputDialog("What is the column number 0-9"));
						break;
					}
					catch (NumberFormatException e1)
					{
						JOptionPane.showMessageDialog(null, "Invalid input!!!!");
					}
				}

				if (vertical)
				{
					if (ship1.equals("aircraft"))
					{
						inBoundNumber = rows + 4;
					}
					else if (ship1.equals("battleship"))
					{
						inBoundNumber = rows + 3;
					}
					else if (ship1.equals("cruiser") || ship1.equals("submarine"))
					{
						inBoundNumber = rows + 2;
					}
					else
					{
						inBoundNumber = rows + 1;
					}
				}
				else
				{
					if (ship1.equals("aircraft"))
					{
						inBoundNumber = columns + 4;
					}
					else if (ship1.equals("battleship"))
					{
						inBoundNumber = columns + 3;
					}
					else if (ship1.equals("cruiser") || ship1.equals("submarine"))
					{
						inBoundNumber = columns + 2;
					}
					else
					{
						inBoundNumber = columns + 1;
					}
				}

				if (inBoundNumber < 10)
				{
					inBound = true;
				}
				else
				{
					inBound = false;
					JOptionPane.showMessageDialog(null, "The ship is out of grid. Please select other rows and columns");
				}

				if (inBound)
				{
					if (vertical)
					{
						if (ship1.equals("aircraft"))
						{
							for (int i=0; i<5; i++)
							{
								if (boatPosition[rows+i][columns] == 1)
								{
									overlapNumber = overlapNumber + 1;
								}
							}
							if (overlapNumber == 0)
							{
								for (int i=0; i<5; i++)
								{
									buttonAlly[rows+i][columns].setBackground(Color.WHITE);
									buttonAlly[rows+i][columns].setEnabled(false);
									boatPosition[rows+i][columns] = 1;
									aircraftButton.setEnabled(false);
									aircraftChecked = true;
									notOverlap = true;
								}
							}
						}
						else if (ship1.equals("battleship"))
						{
							for (int i=0; i<4; i++)
							{
								if (boatPosition[rows+i][columns] == 1)
								{
									overlapNumber = overlapNumber + 1;
								}
							}
							if (overlapNumber == 0)
							{
								for (int i=0; i<4; i++)
								{
									buttonAlly[rows+i][columns].setBackground(Color.WHITE);
									buttonAlly[rows+i][columns].setEnabled(false);
									boatPosition[rows+i][columns] = 1;
									battleshipButton.setEnabled(false);
									battleshipChecked = true;
									notOverlap = true;
								}
							}
						}
						else if (ship1.equals("cruiser"))
						{
							for (int i=0; i<3; i++)
							{
								if (boatPosition[rows+i][columns] == 1)
								{
									overlapNumber = overlapNumber + 1;
								}
							}
							if (overlapNumber == 0)
							{
								for (int i=0; i<3; i++)
								{
									buttonAlly[rows+i][columns].setBackground(Color.WHITE);
									buttonAlly[rows+i][columns].setEnabled(false);
									boatPosition[rows+i][columns] = 1;
									cruiserButton.setEnabled(false);
									cruiserChecked = true;
									notOverlap = true;
								}
							}
						}
						else if (ship1.equals("submarine"))
						{
							for (int i=0; i<3; i++)
							{
								if (boatPosition[rows+i][columns] == 1)
								{
									overlapNumber = overlapNumber + 1;
								}
							}
							if (overlapNumber == 0)
							{
								for (int i=0; i<3; i++)
								{
									buttonAlly[rows+i][columns].setBackground(Color.WHITE);
									buttonAlly[rows+i][columns].setEnabled(false);
									boatPosition[rows+i][columns] = 1;
									submarineButton.setEnabled(false);
									submarineChecked = true;
									notOverlap = true;
								}
							}
						}
						else
						{
							for (int i=0; i<2; i++)
							{
								if (boatPosition[rows+i][columns] == 1)
								{
									overlapNumber = overlapNumber + 1;
								}
							}
							if (overlapNumber == 0)
							{
								for (int i=0; i<2; i++)
								{
									buttonAlly[rows+i][columns].setBackground(Color.WHITE);
									buttonAlly[rows+i][columns].setEnabled(false);
									boatPosition[rows+i][columns] = 1;
									destroyerButton.setEnabled(false);
									destroyerChecked = true;
									notOverlap = true;
								}
							}
						}
					}
					else
					{
						if (ship1.equals("aircraft"))
						{
							for (int i=0; i<5; i++)
							{
								if (boatPosition[rows][columns+i] == 1)
								{
									overlapNumber = overlapNumber + 1;
								}
							}
							if (overlapNumber == 0)
							{
								for (int i=0; i<5; i++)
								{
									buttonAlly[rows][columns+i].setBackground(Color.WHITE);
									buttonAlly[rows][columns+i].setEnabled(false);
									boatPosition[rows][columns+i] = 1;
									aircraftButton.setEnabled(false);
									aircraftChecked = true;
									notOverlap = true;
								}
							}
						}
						else if (ship1.equals("battleship"))
						{
							for (int i=0; i<4; i++)
							{
								if (boatPosition[rows][columns+i] == 1)
								{
									overlapNumber = overlapNumber + 1;
								}
							}
							if (overlapNumber == 0)
							{
								for (int i=0; i<4; i++)
								{
									buttonAlly[rows][columns+i].setBackground(Color.WHITE);
									buttonAlly[rows][columns+i].setEnabled(false);
									boatPosition[rows][columns+i] = 1;
									battleshipButton.setEnabled(false);
									battleshipChecked = true;
									notOverlap = true;
								}
							}
						}
						else if (ship1.equals("cruiser"))
						{
							for (int i=0; i<3; i++)
							{
								if (boatPosition[rows][columns+i] == 1)
								{
									overlapNumber = overlapNumber + 1;
								}
							}
							if (overlapNumber == 0)
							{
								for (int i=0; i<3; i++)
								{
									buttonAlly[rows][columns+i].setBackground(Color.WHITE);
									buttonAlly[rows][columns+i].setEnabled(false);
									boatPosition[rows][columns+i] = 1;
									cruiserButton.setEnabled(false);
									cruiserChecked = true;
									notOverlap = true;
								}
							}
						}
						else if (ship1.equals("submarine"))
						{
							for (int i=0; i<3; i++)
							{
								if (boatPosition[rows][columns+i] == 1)
								{
									overlapNumber = overlapNumber + 1;
								}
							}
							if (overlapNumber == 0)
							{
								for (int i=0; i<3; i++)
								{
									buttonAlly[rows][columns+i].setBackground(Color.WHITE);
									buttonAlly[rows][columns+i].setEnabled(false);
									boatPosition[rows][columns+i] = 1;
									submarineButton.setEnabled(false);
									submarineChecked = true;
									notOverlap = true;
								}
							}
						}
						else
						{
							for (int i=0; i<2; i++)
							{
								if (boatPosition[rows][columns+i] == 1)
								{
									overlapNumber = overlapNumber + 1;
								}
							}
							if (overlapNumber == 0)
							{
								for (int i=0; i<2; i++)
								{
									buttonAlly[rows][columns+i].setBackground(Color.WHITE);
									buttonAlly[rows][columns+i].setEnabled(false);
									boatPosition[rows][columns+i] = 1;
									destroyerButton.setEnabled(false);
									destroyerChecked = true;
									notOverlap = true;
								}
							}
						}
					}
					if (!notOverlap)
					{
						JOptionPane.showMessageDialog(null, "The ship is placed on the other ship. Please select other rows and columns");
					}
				}
			}
			if (aircraftChecked && battleshipChecked && cruiserChecked && submarineChecked && destroyerChecked)
			{
				bp = new BoatPosition();
				bp.setBoatPosition(boatPosition);
				putAllyShips = true;
				messageBoard.append("All ships are placed!\n");

				Controller putShips = new Controller(1);
				try
				{
					oos.writeObject(putShips);
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}

				checkReady();
			}		
		}
	}

	/**
	 * gridButtonPressedListener - Inner action listener class that listens for grid buttons of the fleet map to be clicked.
	 * 
	 * @author Raesup Kim
	 * @version Oct.4 2020
	 */
	private class gridButtonPressedListener implements ActionListener
	{
		int rows;
		int columns;

		public gridButtonPressedListener (int rows, int columns)
		{
			this.rows = rows;
			this.columns = columns;
		}

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			buttonEnemy[rows][columns].setBackground(Color.WHITE);
			buttonEnemy[rows][columns].setEnabled(false);

			bpTarget = new BoatPosition (rows, columns);
			Controller nextTurn = new Controller(2);

			try
			{
				oos.writeObject(bpTarget);
				oos.writeObject(nextTurn);
			}
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}	

			for (rows = 0; rows < 10; rows++)
			{
				for (columns = 0; columns < 10; columns++)
				{
					buttonEnemy[rows][columns].setEnabled(false);
				}
			}
			
			bpTarget = null;
		}	
	}

	/**
	 * propertyChange - notify the change from the subject and make it change to new value.
	 * 
	 * @param evt - the value which holds the changing event information.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		Object newObj = evt.getNewValue();
		String hit;
		String miss;

		if(newObj instanceof Message)
		{
			messageBoard.append(newObj.toString()+"\n");
		}
		else if (newObj instanceof BoatPosition)
		{
			bp2 = (BoatPosition)newObj;
			int r = bp2.getRows();
			int c = bp2.getColumns();
			int[][] position = bp.getBoatPosition();
			if (position[r][c] == 1)
			{
				hit=Integer.toString(r)+Integer.toString(c)+"t";
				buttonAlly[r][c].setBackground(Color.RED);
				myScore = myScore - 1;
				myScoreLabel.setText("Ally's remaing battleship: " + myScore + " / 17");

				Result result = new Result(hit);
				try
				{
					oos.writeObject(result);
				}
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}	
			}
			else
			{
				miss=Integer.toString(r)+Integer.toString(c)+"f";
				buttonAlly[r][c].setBackground(Color.BLUE);
				Result result = new Result(miss);
				try
				{
					oos.writeObject(result);
				}
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}	
			}
		}
		else if (newObj instanceof Result)
		{
			int i = Character.getNumericValue(newObj.toString().charAt(0));
			int j = Character.getNumericValue(newObj.toString().charAt(1));
			String s = Character.toString(newObj.toString().charAt(2));

			if (s.equals("t"))
			{
				buttonEnemy[i][j].setBackground(Color.RED);
				boatPositionEnemy[i][j] = 1;
				yourScore = yourScore - 1;
				yourScoreLabel.setText("Enemy's remaing battleship: " + yourScore + " /17");

				if (yourScore == 0)
				{
					messageBoard.append("I WON!!!!\n");
					win = true;
					Controller winLose = new Controller(3);

					try
					{
						oos.writeObject(winLose);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

					String keepPlaying = JOptionPane.showInputDialog("You WON!!!!\nDo you want to keep playing? y/n");
					continueOrNot(keepPlaying);
				}
			}
			else
			{
				buttonEnemy[i][j].setBackground(Color.BLUE);
			}
		}
		else if (newObj instanceof Controller)
		{
			int i = ((Controller) newObj).getI();

			if (i == 0)
			{
				myTurn = true;
				messageBoard.append("You " + newObj.toString()+"\n");
			}

			if (i == 1)
			{
				putEnemyShips = true;
				messageBoard.append("Enemy " + newObj.toString()+"\n");
			}

			if (i == 2)
			{
				//				JOptionPane.showMessageDialog(null, "It is your turn, now!");
				for (rows = 0; rows < 10; rows++)
				{
					for (columns = 0; columns < 10; columns++)
					{
						buttonEnemy[rows][columns].setEnabled(true);
					}
				}
			}

			if (i == 3)
			{
				messageBoard.append("I LOST!!!!!\n");
				win = false;
				String keepPlaying = JOptionPane.showInputDialog("You LOST!!!\nDo you want to keep playing? y/n");
				continueOrNot(keepPlaying);
			}
			checkReady();
		}
	}

	/**
	 * checkReady - check if all players are ready to play.
	 */
	public void checkReady()
	{
		if (myTurn && putAllyShips && putEnemyShips)
		{
			JOptionPane.showMessageDialog(null, "The game starts with your first turn!");
			for (rows = 0; rows < 10; rows++)
			{
				for (columns = 0; columns < 10; columns++)
				{
					if (boatPositionEnemy[rows][columns] != 1)
					{
						buttonEnemy[rows][columns].setEnabled(true);
					}
				}
			}
			myTurn = false;
		}
	}

	/**
	 * continueOrNot - check if the user keeps playing or not.
	 * 
	 * @param s the value which holds data if player keeps playing or not.
	 */
	public void continueOrNot(String s)
	{
		if (s.equalsIgnoreCase("y"))
		{
			for (rows = 0; rows < 10; rows++)
			{
				for (columns = 0; columns < 10; columns++)
				{
					buttonEnemy[rows][columns].setEnabled(false);
					buttonEnemy[rows][columns].setBackground(Color.GRAY);
					buttonAlly[rows][columns].setEnabled(false);
					buttonAlly[rows][columns].setBackground(Color.GRAY);
					boatPosition[rows][columns] = 0;
					boatPositionEnemy[rows][columns] = 0;
				}
			}
			bp.setBoatPosition(boatPosition);
//			bp2.setBoatPosition(boatPosition);
//			bpTarget = null;
//			bp = null;
//			bp2 = null;
			aircraftButton.setEnabled(true);
			battleshipButton.setEnabled(true);
			cruiserButton.setEnabled(true);
			submarineButton.setEnabled(true);
			destroyerButton.setEnabled(true);
			aircraftChecked = false;
			battleshipChecked = false;
			cruiserChecked = false;
			submarineChecked = false;
			destroyerChecked = false;
			putAllyShips = false;
			putEnemyShips = false;
			myScore = 17;
			myScoreLabel.setText("Ally's remaing battleship: " + myScore + " / 17");
			yourScore = 17;
			yourScoreLabel.setText("Enemy's remaing battleship: " + yourScore + " /17");
			if (win)
			{
				myTurn = true;
			}
			else
			{
				myTurn = false;
			}
		}
		else
		{
			try
			{
				lis.removeListener(ClientDriver.this);
				oos.close();
				ois.close();
				socket.close();
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	/**
	 * main - Main method.
	 */
	public static void main (String[] args)
	{
		new ClientDriver();
	}
}
