package paint;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JOptionPane; //////////////////////////////////// added
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Drawing extends JFrame implements MouseListener,  MouseMotionListener, ActionListener, ChangeListener {

	
	private static final String RECTANGLE_STRING = "rectangle";
	private static final String OVAL_STRING = "oval";
	private static final String TRIANGLE_STRING = "triangle";
	private static final String LINE_STRING = "line";
	private static final String TOTAL_AREA_STRING = "Total Area"; ///////////////////// name change
	private static final String TOTAL_PER_STRING = "Total Perimeter";
	
	private static final String ERR_EXIT_MSG = "Now, gracefully exiting program... ";
	private static final String TOT_AREA_MSG = "totalArea=";
	private static final String TOT_PER_MSG = "totalPerimeter=";
	
	static final int SLIDE_MIN = 0;
	static final int SLIDE_MAX = 1020;
	static final int SLIDE_INIT = 0;    //initial frames per second
	static final int MAJOR_TICK_SPACING = 255;
	static final int MINOR_TICK_SPACING = 15;
	static final int WINDOW_HEIGHT = 500;
	
	private JButton rectangleButton;
	private JButton ovalButton;
	private JButton triangleButton;
	private JButton lineButton;
//	private JButton clearButton;
	private JButton totAreaButton;
	private JButton totPerButton;
//	private JFormattedTextField rgb_red = new JFormattedTextField();
//	private IntField rgb_green = new IntField(0,0,255);
//	private IntField rgb_blue = new IntField(0,0,255);
	
//	JSlider colorSlider = new JSlider(JSlider.HORIZONTAL, SLIDE_MIN, SLIDE_MAX, SLIDE_INIT);
	
	//for color pallette
	private static final int COLOR_COL = 1;
	private static final int COLOR_ROW = 9;

	private static final int NEGATIVE_INT = -1;
	Swatch b; // this is the special box out to the side that shows the
	// chosen color
	Swatch[][] boxes; // 2-d array of Box objects, form a color pallet
	//end for color pallete

	// attributes for line
	int downX, downY; // where the mouse is when button is pressed
	int mouseX, mouseY; // mouse last seen at
	int upX, upY; // where mouse is when button is released
	//end attributes for line

	boolean lineExists = false;
	boolean rectangleExists = false;
	boolean ovalExists = false;
	boolean triangleExists = false;
	
////////////////////////////////////////////////////////// changed
	ShapesList allShapes = new ShapesList(); 	//
//////////////////////////////////////////////////////////
	
	public Drawing() {
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		setTitle("Paint Program");

		setLayout( new FlowLayout() );
		addMouseListener(this);
		addMouseMotionListener(this);
		
		//add buttons to panel
		rectangleButton	= new JButton(RECTANGLE_STRING); // makes the object
		add(rectangleButton); // adds it to the window
		rectangleButton.addActionListener(this); // so when you press on it, something happens

		ovalButton = new JButton(OVAL_STRING);
		add(ovalButton);
		ovalButton.addActionListener(this);

		triangleButton = new JButton(TRIANGLE_STRING);
		add(triangleButton);
		triangleButton.addActionListener(this);

		lineButton = new JButton(LINE_STRING);
		add(lineButton);
		lineButton.addActionListener(this);
	
		totAreaButton = new JButton(TOTAL_AREA_STRING); ///////////////// updated variable name
		add(totAreaButton, BorderLayout.SOUTH);
		totAreaButton.addActionListener(this);
		
		totPerButton = new JButton(TOTAL_PER_STRING);
		add(totPerButton, BorderLayout.SOUTH);
		totPerButton.addActionListener(this);
		//finished adding buttons to panel
		
		// declare array and fill with 8 boxes
		boxes = new Swatch[COLOR_ROW][COLOR_COL];
		for ( int i=0; i<COLOR_ROW; i++ )
		{
			for ( int j=0; j<COLOR_COL; j++ )
			{
				boxes[i][j] = new Swatch(15+20*j, WINDOW_HEIGHT/4+20*i );
			}
		}
		
		//add color pallete
		b = new Swatch( 15, WINDOW_HEIGHT/3 + 20*COLOR_ROW);
	
		setSize(500,500);
		setVisible(true);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("click at x="+e.getX()+" y="+e.getY());

		int boxi=0, boxj=0; // index of the clicked on box in the double array
		int mx = e.getX();  int my = e.getY();
		// convert window coords to box array indexes.
		boxi = (mx-15)/20; // convert mouse x to box index
		boxj = (my-WINDOW_HEIGHT/4)/20;

		if (e.getX() <= 35 && e.getX()>= 15  && boxi < COLOR_COL
				&& (boxj > NEGATIVE_INT && boxj < COLOR_ROW))
		{
			System.out.println("click at boxi="+boxi+" boxj="+boxj);
			b.setColor( boxes[boxj][boxi].getColor() );
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	// record position of mouse when mouse button is pressed
	// for line
	@Override public void mousePressed ( MouseEvent m )
	{
		if(lineExists || rectangleExists || ovalExists)
		{
			mouseX = downX = m.getX(); 
			mouseY = downY = m.getY();
		}
//		repaint();
	}
	//for line
	@Override
	public void mouseReleased( MouseEvent m )
	{
		if(lineExists || rectangleExists || ovalExists)
		{
			mouseX = upX = m.getX();
			mouseY = upY = m.getY();
		}
		repaint();
	}

	// MouseMotionListener methods (just 2 needed)
	// when the mouse is dragged, update the mouseXY position
	// for line
	public void mouseDragged ( MouseEvent m )
	{
		if(lineExists || rectangleExists || ovalExists)
		{
			mouseX = m.getX();
			mouseY = m.getY();
		}
//		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		lineExists = ovalExists = triangleExists = rectangleExists = false;
		System.out.println("Shape picked");
		if      ( e.getSource()==ovalButton ) { 
			System.out.println(OVAL_STRING + " selected"); 
			ovalExists = true;
		}
		else if ( e.getSource()==triangleButton ) { 
			System.out.println(TRIANGLE_STRING + " selected"); 
			triangleExists = true;
		}
		else if ( e.getSource()==rectangleButton ) { 
			System.out.println(RECTANGLE_STRING + " selected"); 
			rectangleExists = true;
		}
		else if ( e.getSource()==lineButton ) { 
			System.out.println(LINE_STRING + " selected"); 
			lineExists = true;
		}
////////////////////////////////////////////////////////////////////////////////// new
		else if ( e.getSource()==totAreaButton ) { 								//
			System.out.println(TOTAL_AREA_STRING + " selected");
			JOptionPane.showMessageDialog(this, TOTAL_AREA_STRING + " = " + allShapes.getTotalArea());
		}
		else if ( e.getSource()==totPerButton ) { 
			System.out.println(TOTAL_PER_STRING + " selected");
			JOptionPane.showMessageDialog(this, TOTAL_PER_STRING + " = " + allShapes.getTotalPerimeter());
		}																		//
//////////////////////////////////////////////////////////////////////////////////

	}

	@Override
	public void stateChanged(ChangeEvent e) {

		
	}

	
	// draw the grid of boxes and also the picked one
	public void paint( Graphics g )
	{
		super.paint(g);

		//color pallete painting
		b.drawMe(g);
		for ( int i=0; i<COLOR_ROW; i++ )
		{
			for ( int j=0; j<COLOR_COL; j++ )
			{
				boxes[i][j].drawMe(g);
			}
		}

		g.setColor(b.getColor());
		
////////////////////////////////////////////////////////////////////////////////////////// ShapesList
																						// variable
		// draws over swatches															// name
		for (int i = 0; i < allShapes.size(); i++) {									// updated
			allShapes.get(i).drawShape(g);												//
		}																				//
																						//
		//line painting																	
		if(lineExists)																									
		{																												
			System.out.println("line exists true and entered in paint");												
			Shape tempLine = new Line(new Coordinates(downX, downY), new Coordinates(mouseX, mouseY), b.getColor());	
			allShapes.add(tempLine);																					
			//g.drawLine(downX, downY, mouseX, mouseY);																	
			tempLine.drawShape(g);																						
		}
		if(rectangleExists)
		{
			System.out.println("rectangle exists true and entered in paint");
			Shape tempRect = new Rectangle(new Coordinates(downX, downY), new Coordinates(mouseX, mouseY), b.getColor());
			allShapes.add(tempRect);
			tempRect.drawShape(g);														//
		}																				//
																						//
//////////////////////////////////////////////////////////////////////////////////////////
	}	

}
