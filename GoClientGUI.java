// package go;


import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * GoClientGUI class
 * 
 * Gomoku Game
 * 
 * Creates GUI for Gomoku and its chatter based on information it receives from GoClient, and also
 * acts upon GoClient based on user input
 * 
 * @author Christian Collier (chc46), Qingyue Li (ql61), Mark Ozdemir (mo732)
 */
public class GoClientGUI extends JFrame implements ActionListener, KeyListener, MouseListener {

	private static final int HELP_FRAME_WIDTH = 300;
	private static final int HELP_FRAME_HEIGHT = 350;

	private static final String HELP_MSG = "  Welcome to Gomoku user manual.\n" +
			"  Black plays first.\n" +
			"  Players alternate in placing a stone\n" +
			"  of their color on an empty intersection.\n" +
			"  The winner is the first player to get an unbroken row \n" +
			"  of five stones horizontally, vertically, or diagonally.\n";

	//Go GUI stuff
	private int[] dim = {15,15};                        //Default board width=15 and depth=15
	int boardSize=450;

	//private GameBoardUI pBoard;                          //Game drawer

	private static final int FREE = 3;          //a free place on the board
	private static final String DEFAULT_HOST = "localhost";
	private static final int COLOR_NOT_ASSIGNED = 3;
    private static final int AI_NUM = 1;
    private static final int HUMAN_NUM = 0;

	//pass player mode from the command line
	private int playerMode=1; //0 is human, 1 is AI (computer)
	
	private boolean myTurn = true;  //Players take turns
	private int player = COLOR_NOT_ASSIGNED;   //player on the turn NOT defined yet. 1 - black, 0-white
	
	
	private String[] host = {DEFAULT_HOST,DEFAULT_HOST};  	
	protected int[] winCounter = {0,0};                   //to store the game score [0]-player , 1-opponent 
	

	private int cellSize=30;      //size of the representation of a game field
	private int cellSizeSmall= 4; //start point to draw a piece
	private int cellSizeBig= 21;  //size of a piece
	private int moveThread=0;   //if a move move is being calculate 1 otherwise 0
	private boolean reset = false;  //if true the board will be reseted for a new game
	private boolean gameOver =false;
	protected GameBoardUI pBoard;       //the GameBoard board for this game


	//    private JButton[][] goBoardSquares = new JButton[15][15];
	//    private JPanel goBoard;

	private static final long serialVersionUID = 1L;
	private static final String WELCOME_MESSAGE = "Welcome to Go. In this application, two players will play Go on a 15x15 board."
			+ " Good luck and have fun.\n\n";
	private static final String TYPE_CHAT_MSG = "Type Chat Message Here \n";
	private static final String NICK_NAME_COMMAND = "Type /nick \'name\' to change nickname";
	private static final String SLASH_NICK = "/nick";
	private static final String INVALID_UN = "Invalid Username (cannot contain spaces). Try again.";
	private static final String USER_LOGON_MSG = " logged on! Say hi!\n\n";
	private static final String LOGOUT_MSG = " Logged off :(\n\n";
	//	private JLabel usernameLabel;							// label for username
	private JTextField username;							// hold current username
	private JTextField tfServer, tfPort;					// write server address and port number
	private JButton loginBtn, sendBtn, exitBtn, resetBtn, giveUpBtn, userManBtn;				// buttons
	private JComboBox usernameList; 						// list of clients
	private JTextArea taMsgHistory;							// message history text area
	private JTextArea taMessenger;							// text box
	private int defaultPort;								// the default port number
	private String defaultIP;								// the default ip
	private GoClient client;

	public void setPlayer(int stoneColor){
		player = stoneColor;
	} 


	// Constructor connection receiving a socket number
	GoClientGUI(String host, int port, String playerType) {		
		//System.out.println("inside clientGUI constructor");

		super("Go Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		defaultPort = port;
		defaultIP = host;

		// The NorthPanel
		JPanel northPanel = new JPanel(new GridLayout(3,2)); // the server name and port number
		JPanel serverAddress = new JPanel(new GridLayout(1, 2)); // the JTextField with default value for server address
		JPanel portNumber = new JPanel(new GridLayout(1, 2)); // the JTextField with default value for port number
		tfServer = new JTextField(defaultIP);
		tfPort = new JTextField("" + defaultPort);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);
		serverAddress.add(new JLabel("Server Address:  "));
		serverAddress.add(tfServer);
		portNumber.add(new JLabel("Port Number:  "));
		portNumber.add(tfPort);
		// adds Server port field to GUI
		northPanel.add(serverAddress, BorderLayout.NORTH);
		northPanel.add(portNumber, BorderLayout.NORTH);

		//jpanel to contain buttons for game
		JPanel gameBtns1 = new JPanel(new GridLayout(1, 2));
		JPanel gameBtns2 = new JPanel(new GridLayout(1, 2));

		//mode and user manual buttons
		userManBtn = new JButton("User Manual");
		userManBtn.addActionListener(this);
		userManBtn.setEnabled(true);
		gameBtns1.add(userManBtn);
		northPanel.add(gameBtns1);

		//username TextField
		username = new JTextField("User");					/////////initial username; may need to change depending on human vs ai
		username.setBackground(Color.WHITE);
		northPanel.add(username, BorderLayout.SOUTH);

		//reset and give up buttons
		resetBtn = new JButton("Reset");
		resetBtn.addActionListener(this);
		resetBtn.setEnabled(true);
		giveUpBtn = new JButton("Give Up");
		giveUpBtn.addActionListener(this);
		giveUpBtn.setEnabled(true);
		gameBtns2.add(resetBtn);
		gameBtns2.add(giveUpBtn);
		northPanel.add(gameBtns2);

		// username list creation
		PopupMenuListener pmListener = new PopupMenuListener() {
			boolean initialized = false;

			@Override public void popupMenuCanceled(PopupMenuEvent e) {}
			@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				String[] initList = {"G r o u p"};
				if (!initialized) {
					usernameList = (JComboBox) e.getSource();
					ComboBoxModel model = new DefaultComboBoxModel(initList);
					usernameList.setModel(model);
					initialized = true;
				}
				//System.out.println("should remove " + usernameList.getItemCount() + " -1 items");

				for (int i = usernameList.getItemCount()-1; i > 0; i--) {
					//System.out.println("will remove " + usernameList.getItemAt(i));

					usernameList.removeItemAt(i);
				}
				for (int i = 0; i < client.getNameList().size(); i++) {
					//System.out.println("adding " + client.getNameList().get(i));
					usernameList.addItem(client.getNameList().get(i));
				}
			}
		};
		usernameList = new JComboBox();
		usernameList.addItem("G r o u p");
		usernameList.setSelectedItem("G r o u p");
		usernameList.addPopupMenuListener(pmListener);
		usernameList.addActionListener(this);
		usernameList.setEnabled(false);
		northPanel.add(usernameList, BorderLayout.SOUTH);
		//add to frame
		add(northPanel, BorderLayout.NORTH);

		// CenterPanel
		JPanel centerPanel = new JPanel(new GridLayout(1,2));


		/////////////////////////////go board stuff

		pBoard = new GameBoardUI(15,15);  //Create a new game board

		// add a mouse listener to detect user's mouse clicks on the board
		addMouseListener(new MouseAdapter(){@Override
			public void mouseClicked(MouseEvent e){MouseClicked(e);}});

		centerPanel.add(pBoard);
		pBoard.addMouseListener(this);
		add(centerPanel, BorderLayout.CENTER);

		System.out.println("added center panel to board");

		/////////////////////////////////////////////////////////////////end of board creation
		taMsgHistory = new JTextArea(WELCOME_MESSAGE, 80, 80);
		centerPanel.add(new JScrollPane(taMsgHistory));
		taMsgHistory.setEditable(false);

		// MessengerPanel
		taMessenger = new JTextArea(TYPE_CHAT_MSG + NICK_NAME_COMMAND);
		taMessenger.setBackground(Color.WHITE);
		taMessenger.setEditable(false);
		taMessenger.addKeyListener(this);
		centerPanel.add(taMessenger);
		//add(centerPanel, BorderLayout.LINE_END);

		// button creation
		loginBtn = new JButton("Login");
		loginBtn.addActionListener(this);
		exitBtn = new JButton("Exit");
		exitBtn.addActionListener(this);
		exitBtn.setEnabled(false);		// must login before being able to exit
		sendBtn = new JButton("Send");
		sendBtn.addActionListener(this);
		sendBtn.setEnabled(false);

		JPanel southPanel = new JPanel();
		//southPanel.add(loginBtn);						////// remove login button for the game
		southPanel.add(sendBtn);
		southPanel.add(exitBtn);						
		add(southPanel, BorderLayout.SOUTH);

		//set the word wrap of message history and Messenger
		taMessenger.setWrapStyleWord(true);
		taMessenger.setLineWrap(true);
		taMsgHistory.setWrapStyleWord(true);
		taMsgHistory.setLineWrap(true);


		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				exitBtn.doClick();
			}
		});

		//check to see if playerType is ai or human
		//and sets buttons accordingly
		if (playerType.equals("ai")) {
			playerMode = AI_NUM;   //AI:1
			username.setForeground(Color.GRAY);
			username.setEditable(false);
			usernameList.setEnabled(false);
			sendBtn.setEnabled(false);
			taMessenger.setEditable(false);
			taMessenger.setForeground(Color.GRAY);
			giveUpBtn.setEnabled(false);
		}
		else playerMode = HUMAN_NUM;   //human:0

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1400, 620);
//		pack(); //automatically change the size of the frames according to the size of components in it. 
		setVisible(true);
		taMessenger.requestFocus();
		loginBtn.doClick();
	}

	protected void MouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		pBoard.makeMove(e);

	}


	// for enter key being pressed
	//mainly used for when enter key is pressed
	public void keyPressed(KeyEvent e) {
		if(sendBtn.isEnabled()) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER){
				sendBtn.doClick();
			}
		}
	}

	//for the key being released
	//mainly used for when enter key is released
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER && 
				!(taMessenger.getText().trim().equals(INVALID_UN.trim()))) {			
			taMessenger.setText("");
		}
	}

	/**
	 * Button or JTextField clicked
	 */
	public void actionPerformed(ActionEvent e) {
		// check for which button is pressed
		if(e.getSource() == exitBtn) {

			//Send exit command to the server
			Map<String, String> messageInfo = new HashMap<String, String>();
			// messageInfo.put("type", ChatterMessage.EXIT);
			messageInfo.put("sender", client.getClientUsername());
			messageInfo.put("recipient", client.getClientUsername());
			messageInfo.put("message",client.getClientUsername() + LOGOUT_MSG);
			//	client.sendMessage(new ChatterMessage(messageInfo));
			
			client.sendMessage("EXIT");

			//disable all buttons
			sendBtn.setEnabled(false);
			exitBtn.setEnabled(false);
			loginBtn.setEnabled(false);
			taMessenger.setEditable(false);
			usernameList.setEnabled(false);
			taMsgHistory.append("Logged off. Goodbye!\n\n");  //display bye to the user

		}
		else if (e.getSource() == sendBtn) {
			//System.out.println("click was sendBtn:: in sendBtn:: ");

			String msg = taMessenger.getText();
			String messageType = "";
			String nickChangeStr = "";

			if (msg.contains(SLASH_NICK)) {
				nickChangeStr = msg.substring(msg.indexOf(SLASH_NICK) + SLASH_NICK.length());
			}
			//if ( (msg.indexOf(SLASH_NICK) == 0) && (nickChangeStr.indexOf(" ") == 0) ) {
			if ( msg.indexOf(SLASH_NICK) == 0)   {

				//System.out.println("----" + nickChangeStr + "-----");
				nickChangeStr = nickChangeStr.trim();
				if (checkValidUsernameInput(nickChangeStr)) {
					client.setUsername(nickChangeStr);
					username.setText(nickChangeStr);
					taMessenger.setText("");
				}
				else {
					//System.out.println("invalid username put in taMessenger");
					taMessenger.setText(INVALID_UN);
				}
			}			
			else { 
				/*
				if (((String) usernameList.getSelectedItem()).equalsIgnoreCase("g r o u p")) {
					messageType = ChatterMessage.PUBLIC;
				}
				else {
					messageType = ChatterMessage.PRIVATE;
				}
				*/

				Map<String, String> messageInfo = new HashMap<String, String>();
				messageInfo.put("type", messageType);
				messageInfo.put("sender", client.getClientUsername());
				messageInfo.put("recipient", (String) usernameList.getSelectedItem());
				messageInfo.put("message", msg + "\n\n");

				/*
				if ( messageType.equals(ChatterMessage.PRIVATE) 
						&& !( client.getClientUsername().equals((String) usernameList.getSelectedItem()) ) ) {
					taMsgHistory.append(client.getClientUsername() + " to " 
							+ (String) usernameList.getSelectedItem() + ": " + msg + "\n\n");
				}
				*/

				//client.sendMessage(new ChatterMessage(messageInfo));
				String chatMsg = GomokuProtocol.generateChatMessage(getUsername().trim(), msg + "\n");
				client.sendMessage(chatMsg);

				taMsgHistory.append(getUsername().trim() + " " + msg + "\n\n");
				taMessenger.setText("");
			}
		}
		if(e.getSource() == resetBtn) {

			//Send reset game command to the server
			String msg = GomokuProtocol.generateResetMessage();
			client.sendMessage(msg);

			//reset the game board
			pBoard.reset();			

			taMsgHistory.append("Reset the game\n\n");  

		}
		if(e.getSource() == giveUpBtn) {

			//Send give up message to the server
			String msg = GomokuProtocol.generateGiveupMessage();
			client.sendMessage(msg);

			taMsgHistory.append("Game over\n\n");

		}
		else if(e.getSource() == loginBtn) {
			System.out.println("actionPerformed: in loginBtn");

			// connection requested
			if (checkValidUsernameInput(getUsername())) {
				String username = getUsername().trim();
				String server = tfServer.getText().trim();
				String portNumber = tfPort.getText().trim();

				// create new Client
				client = new GoClient(server, Integer.parseInt(portNumber), username, this);
				// test to start Client
				if(!client.start()) 
					return;

				if (client.getNameList() != null) {
					for (int i = 0; i < client.getNameList().size(); i++) {
						usernameList.addItem(client.getNameList().get(i));
					}
				}
				//enable username List
				this.username.setEditable(false);
				// disable login button
				loginBtn.setEnabled(false);
				
				//disable reset button for ai
				resetBtn.setEnabled(false);
				//disable send button
				sendBtn.setEnabled(false);
				// enable the exit button for all cases
				exitBtn.setEnabled(true);
				
				if (playerMode == HUMAN_NUM) {
					sendBtn.setEnabled(true);	
					resetBtn.setEnabled(true);
					taMessenger.setEditable(true);	
					taMessenger.setFocusable(true);
				}
	
				// disable the Server and Port JTextField
				tfServer.setEditable(false);
				tfPort.setEditable(false);
				// Action listener for when the user enter a message
				taMessenger.addFocusListener(new FocusListener() {
					public void focusGained(FocusEvent e) {}
					public void focusLost(FocusEvent e) {}
				});


				//	client.sendMessage(new ChatterMessage(ChatterMessage.PUBLIC, username, 
				//		(String) usernameList.getSelectedItem(), username + USER_LOGON_MSG));

				//printChatHistory();
			}
			else {
				//				usernameLabel.setText(INVALID_UN);
				this.username.setText(INVALID_UN);
			}

		}
		else if ( e.getSource() == userManBtn ) {
			//System.out.println(HELP + " selected");
			JFrame helpFrame = new JFrame("User Manual Screen");

			JTextArea helpText = new JTextArea(HELP_MSG); 

			helpText.setWrapStyleWord(true);
			helpFrame.add(helpText);
			helpFrame.setSize(HELP_FRAME_HEIGHT,HELP_FRAME_WIDTH);
			helpFrame.setVisible(true);
		}
		if(e.getSource() == resetBtn) {
			//reset gameOver
			gameOver= false;
			pBoard.reset();  //reset the board and start a new game
		}
	}

	// outputs chat messages for the given user/client
	public void printChatHistory(String cm) {	
		taMsgHistory.append(cm + "\n");
	}


	//check to see if the user name entered as a param is valid
	private boolean checkValidUsernameInput(String text) {
		boolean validUN = false;
		//check to see if client exists
		if (client != null) {
			for (int i = 0; i < client.getNameList().size(); i++) {
				String nameCompare = client.getNameList().get(i);
				if (text.equals(nameCompare)) {
					validUN = true;
				}
			}
		}
		//passed user name cannot contain spaces or be empty
		if (!(validUN || text.contains(" ") || text.equals(""))) {
			validUN = true;
		}
		return validUN;
	}

	//get the username from the client username text field
	protected String getUsername() {
		return username.getText();
	}
	// Set the player's stone color
	protected void setPlayer(boolean isBlack) {
		if (isBlack) {
			//playerStone=1;  //set player's stone as black stone
			System.out.println("GoClientGUi line 512 setPlayerStone isBlack:" + isBlack);
			player = 1; //black starts first
			System.out.println("GoClientGUi line 514 setPlayerStone player:" + player);

		}else {
			//playerStone=0;  //set player's stone as white stone
			System.out.println("GoClientGUi line 518 setPlayerStone isBlack:" + isBlack);
			player = 0; //white waits until black starts
			System.out.println("GoClientGUi line 520 setPlayerStone player:" + player);

		}			
	}
	
	//return player
	// 0- white stone
	// 1- black stone
	protected int getPlayer() {
		return player;		
	}

	protected void setMyTurn(boolean takeTurn) {
		myTurn = takeTurn;
	}
	
	public Boolean getMyTurn() {
		return myTurn;
	}

	
	protected void setWinCounter(boolean isWin) {	
		if (isWin) {
			winCounter[0]++;
		}
		else{
			winCounter[1]++;
		}
		//set gameOver
		gameOver= true;
	}
	
	protected int[] getWinCounter() {
		return winCounter;
	}

	//get playerMode
	protected int getPlayerMode() {
		return playerMode;
	}


	//key typed... no action required for function
	@Override
	public void keyTyped(KeyEvent e) {}


	//GameBoardUI moved


	///////////////////////////////////////////////////////////////////////// beg of methods and other stuff from old GameBoardUI

	public class GameBoardUI extends JPanel {
		GameBoard board;
		boolean firstPaint = false;
		int curRow = -1;
		int curCol = -1;
		Color curColor = Color.GREEN;

		public GameBoardUI(int size1, int size2){
			System.out.println("inside gameboardUI constructor");

			board = new GameBoard(size1,size2);  //Create a new game board

			//			// add a mouse listener to detect user's mouse clicks on the board
			//	        addMouseListener(new MouseAdapter(){@Override
			//	        public void mouseClicked(MouseEvent e){thisMouseClicked(e);}});

		}

		/**
		 * Draws the game board
		 */
		public void paintComponent(Graphics g){
			System.out.println("inside gameboardUI::paintComponent()");

			super.paintComponent(g);													//////////////////////////something wrong with this line
			Graphics2D g2 = (Graphics2D) g;
			g2.addRenderingHints(new RenderingHints(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON)
					);

			this.setSize(boardSize,boardSize);  //this refers to the board , its size is 450x450, all cellSizes are populated with integer numbers of 32 and 3
			this.setPreferredSize(new Dimension(boardSize,boardSize));  //board preferred size is set to 450x450
			//not really necessary
			if (!firstPaint) refreshBoard();

			PlayerPosition place = new PlayerPosition(0,0);   //initialize place to (0,0) cellSize
			int x = 0, y = 0;
			while (y < boardSize){    // y less than 30 x 15 = 450  board heigth
				while ( x < boardSize ){    // x is less than 30 x 15 = 450  board width
					place.setColumnRow(1+x/cellSize,1+y/cellSize);   // dividing x by cellSize gives us how many cellSizes x moved like 1,2,3.. . Same for y.
					if (board.getPiece(place)>=4){    // board.getPiece is larger than 4 for frame cellSizes that have 35, and 32 as values
						if (board.getPiece(place)==4) {   //white wins
							//the win fields have special color
							g2.setColor(Color.white);
							g2.fill3DRect(x, y, cellSize, cellSize,true);
						} else {
							//special colors to show winning row
							g2.setColor(Color.black);
							g2.fill3DRect(x, y, cellSize, cellSize,true);						

							
						}
					} else {
						if ((board.getPiece(place)&3)!=FREE) {
							//put grid lines
							g2.setColor(Color.BLACK);
							g2.setStroke(new BasicStroke(3));  //Set line thickness
							g2.drawLine(x, y+(cellSize/2), x+cellSize, y+(cellSize/2));  //draw a horizontal line in the middle of cell
							g2.drawLine(x+(cellSize/2), y, x+(cellSize/2), y+cellSize);  //draw a vertical line in the middle of cell
							//for cellSizes which contains pieces
							g2.setColor(new Color(65,105,225)); //Royal Blue 65,105,225 

						} else {
							//for cellSizes which don't contains pieces
							g2.setColor(new Color((float)1.0,(float)1.0,(float)1.0,(float)0.0));  // white board color, 100% transparent
							g2.fill3DRect(x, y, cellSize, cellSize,true);
							//put grid lines 
							g2.setColor(Color.BLACK);
							g2.setStroke(new BasicStroke(3));  //Set line thickness
							g2.drawLine(x, y+(cellSize/2), x+cellSize, y+(cellSize/2));  //draw a horizontal line in the middle of cell
							g2.drawLine(x+(cellSize/2), y, x+(cellSize/2), y+cellSize);  //draw a vertical line in the middle of cell
						}
					}
					if ((board.getPiece(place))== 0) {  //0 is for white 
						//set the color to white
						g2.setColor(Color.white);
						g2.fillOval(x+cellSizeSmall, y+cellSizeSmall , cellSizeBig, cellSizeBig);
					}
					if ((board.getPiece(place))== 1) {  // a cell value of 1 is for black
						g2.setColor(Color.black);
						g2.fillOval(x+cellSizeSmall, y+cellSizeSmall , cellSizeBig, cellSizeBig);
					}

					x += cellSize;
				}
				x = 0;
				y += cellSize;
			}
			curColor = g2.getColor();
		}	

		/**
		 * redraw the board. But wait a time to be sure that the time between
		 * two moves is at least one second.
		 */
		public void refreshBoard(){
			refresh();
		}


		private void refresh() {
			firstPaint = true;
			repaint();
		}

		/**
		 * Resets the GameBoard board for a new game.
		 */
		public void reset() {
			System.out.println("inside gameboardUI::reset()");
			reset = true;
			gameOver=false;
			board = new GameBoard(dim[0], dim[1]);
			refreshBoard(); 
			reset = false;
			String msg = GomokuProtocol.generateResetMessage();
			client.sendMessage(msg);
		}

		/**
		 * gets the game status
		 * @return     0 = PLAYER_A was the last player
		 * @return     1 = PLAYER_B was the last player
		 * @return     3 = the board is yet empty
		 * @return     4 = game over and PLAYER_A won
		 * @return     5 = game over and PLAYER_B won
		 * @return     8 = game over without winner
		 * @return     9 = board need to be accepted
		 */
		public int getStatus() {
			return board.getStatus();  //returns 0 at the beginning of the game
		}

		/**
		 * returns the next move. 
		 * @param player   PLAYER_A or PLAYER_B player
		 * @param e         position of mouse click (next move for human player)
		 */
		private PlayerPosition play(final int player,final MouseEvent e) {
			PlayerPosition ret = new PlayerPosition();
			if (!gameOver){
				if (playerMode==0 ){     //Human player
					ret = new PlayerPosition(1+e.getX()/cellSize,1+e.getY()/cellSize);
				} else{   //Computer Player        	 
					AI aiPlayer = new AI(new GomokuStrategy(board));  
					//Evaluate the best move based on free spaces and potential game states
					ret = aiPlayer.getNextMove();  

				}
				// Output on console for log the game
				curCol = ret.getColumn();
				curRow = ret.getRow();
				System.out.print("GameBoardUI PlayerPosition play ret value "+ret.toString() + " row: " + ret.getRow() + " column: " + ret.getColumn() + "\n");

				setMyTurn(false);
			}
			return ret;
		}

		/**
		 * starts a new game 
		 */
		public void startGame() {
			System.out.println("inside gameboardUI::startGame()");
			makeMove(null);
		}


		public void makeMove(MouseEvent e) {
			this.run(e);
		}


		/**
		 *starts the calculation
		 */
		public void run(MouseEvent e) {
			System.out.println("inside gameboardUI::run()");

			if (myTurn){ 
				PlayerPosition pp = new PlayerPosition();
				pp = play(player,e);
				System.out.println ("Key released in Gui player position row: "+ pp.getRow()+ " column: " + pp.getColumn() + " player 0 : " + player);
				//set stone color for this client
				if ( board.setPiece(pp,(int) player)) {
				//board.placeAIStone(pp, (int)player);
				System.out.println("line 757 run GoClientGUI player "+ player + "  pp col: " + pp.getColumn() + " pp row " + pp.getRow());
				System.out.println("GoClientGUI line 758 player's stone color : " + player);
				String msg = GomokuProtocol.generatePlayMessage(player==1, pp.getRow()-1, pp.getColumn()-1);
				client.sendMessage(msg);
				}
			}

			repaint();
		} 

	}

	///////////////////////////////////////////////////////////////////////// end of methods and other stuff from old GameBoardUI

	@Override
	public void mouseClicked(MouseEvent e) {
		/////////////////////////////////////////////////////////////////makeMove
		System.out.println("inside mouseclick of goclientGui");
		//pBoard.play(PLAYER_A, e);
		pBoard.makeMove(e);
		/*
		client.sendMessage(new ChatterMessage(ChatterMessage.PRIVATE, client.getClientUsername(),
				(String) usernameList.getSelectedItem(), "\\"+player + "/play\\"+player+pBoard.curColor
				+"\\"+player+pBoard.curRow+"\\"+player+pBoard.curCol));
		 */

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

} // END ChatterClientGui class
