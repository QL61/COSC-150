/********** MAINTAINS LIST OF ALL SHAPES ON SCREEN *************/



////////////////////////////////// mostly new/changed



package paint;

//import java.awt.Color;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Scanner;

public class ShapesList {

	/********** FIELDS **********/
	private ArrayList<Shape> list = new ArrayList<Shape>();
	private double totalArea = 0, totalPerimeter = 0;

	/*********** METHODS **********/
//
//	public void add(String shapeType, ArrayList<Coordinates> coordinates, Color color) {
////		try {
//		if (shapeType.equalsIgnoreCase("line")) {
//			Line toAdd = new Line(coordinates.get(0), coordinates.get(1), color);
//			list.add(toAdd);
//		} // END if (line)
//		else if (shapeType.equalsIgnoreCase("oval")) {
////				Oval toAdd = new Oval(coordinates.get(0), coordinates.get(1), color);
////				list.add(toAdd);
//		} // END else if (oval)
//		else if (shapeType.equalsIgnoreCase("rectangle")) {
//			Rectangle toAdd = new Rectangle(coordinates.get(0), coordinates.get(1), color);
//			list.add(toAdd);
//		} // END else if (rectangle)
//		else if (shapeType.equalsIgnoreCase("triangle")) {
////			Triangle toAdd = new Triangle(coordinates.get(0), coordinates.get(1), coordinates.get(2));
////			list.add(toAdd);
//		} // END else if (triangle)	
//	} // END addToList(String, Coordinates[])
	
	public void add(Shape toAdd) {	
		this.list.add(toAdd);
		this.calculateTotalArea();
		this.calculateTotalPerimeter();
	}		
	
	public Shape get(int index) {			
		return this.list.get(index);				
	}	
	
	public int size() {
		return this.list.size();
	}
	
	public void calculateTotalArea() {
		double sum = 0;
		for (int i = 0; i < list.size(); i++)
		{
			sum += (list.get(i)).getArea();

		}
		this.totalArea = sum;
	}

	public void calculateTotalPerimeter() {
		double sum = 0;
		for (int i = 0; i < list.size(); i++)
		{
			sum += (list.get(i)).getPerimeter();
		}
		this.totalPerimeter = sum;
	}

	public double getTotalArea() {
		return this.totalArea;
	}

	public double getTotalPerimeter() {
		return this.totalPerimeter;
	}

} // END class ShapesList
