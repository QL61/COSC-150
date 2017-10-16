package paint;
/****************************************************************************
 * Name: Qingyue Li															*
 * NetID: QL61																*
 * Class: COSC 150, Fall 2017												*
 * Project: HW3 - Paint														*
/****************************************************************************/

import java.awt.Graphics;

// calculates area and perimeter for triangles

import java.lang.Math;
import java.util.ArrayList;

public class Triangle extends Shape {

	//constants 
	private static final int MAX_COORD = 3;
	
	// FIELDS
	private	Coordinates v1, v2, v3;
	private double side1, side2, side3;

	// CONSTRUCTOR
	public Triangle(Coordinates a, Coordinates b, Coordinates c) {
		this.v1 = a;
		this.v2 = b;
		this.v3 = c;
		this.updateSides();
	}
	
	// METHODS
	public double getArea() {
		double p = (side1 + side2 + side3)/2;
		return Math.sqrt(p * (p-side1) * (p-side2) * (p-side3)); // Heron's Formula
	}
	public double getPerimeter() {
		return side1 + side2 + side3;
	}
	public void setV1(Coordinates newV) {
		this.v1 = newV;
		this.updateSides();
	}
	public void setV2(Coordinates newV) {
		this.v2 = newV;
		this.updateSides();
	}
	public void setV3(Coordinates newV) {
		this.v3 = newV;
		this.updateSides();
	}
	private void updateSides() {
		this.side1 = distanceBetween(this.v1, this.v2);
		this.side2 = distanceBetween(this.v2, this.v3);
		this.side3 = distanceBetween(this.v3, this.v1);
	}

	@Override
	public void setCoordinates(ArrayList<Coordinates> coordList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawShape(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInRange(Coordinates c) {
		
		boolean range = false;

		// basic out-of-range checks
		if (((c.x >= Math.min(v1.x, v2.x)) || (c.x >= v3.x)) && ((c.x <= Math.max(v1.x, v2.x)) || c.x <= v3.x)) {
			if (((c.y >= Math.min(v1.y, v2.y)) || (c.y >= v3.y)) && ((c.y <= Math.max(v1.y, v2.y)) || (c.y <= v3.y))) {
				
				// find vertex with "middle" x-value, assign other vertices to variables for later
				// use in checking y-bounds
				Coordinates left, mid, right;
				if (v1.x >= Math.max(v2.x, v3.x)) {
					if (v2.x >= v3.x) {
						mid = v2;
						left = v3;
						right = v1;
					}
					else { 
						mid = v3; 
						left = v2;
						right = v1;
						}
				}
				else if (v2.x >= v3.x) { 
					mid = v3; 
					left = v1;
					right = v2;
					}
				else { 
					mid = v2;
					left = v1;
					right = v3;
					}
				
				// variables for calculating lines drawn by triangle's sides
				double m1, b1, m2, b2;
				
				m2 = (left.y-right.y) / (left.x-right.x);
				b2 = left.y - m2*left.x;
				
				// check lower and upper y-bounds on the left side of mid
				if (c.x <= mid.x) {
					m1 = (left.y-mid.y) / (left.x-mid.x);
					b1 = left.y - m1*left.x;
					m2 = (left.y-right.y) / (left.x-right.x);
					b2 = left.y - m2*left.x;
					if ((c.y >= Math.max((m1*c.x + b1), (m2*c.x + b2)) 
						&& (c.y <= Math.min((m1*c.x + b1), (m2*c.x + b2))))) {
							range = true;
					} // END if
				} // END if

				// check lower and upper y-bounds on the right side of mid
				else {
					m1 = (mid.y-right.y) / (mid.x-right.x);
					b1 = right.y - m1*right.x;
					if ((c.y >= Math.max((m1*c.x + b1), (m2*c.x + b2)) 
						&& (c.y <= Math.min((m1*c.x + b1), (m2*c.x + b2))))) {
							range = true;
					} // END if
				} // END else
				
			} // END if (basic out-of-range check)
		} // END if (basic out-of-range check)
		
		return range;
		
	} // END isInRange(Coordinates)
	
	public void resize (Coordinates press, Coordinates release) {
		if (isInRange(press)) {
			if (distanceBetween(press, v1) < Math.max(distanceBetween(press, v2), distanceBetween(press, v3))) {
				this.setV1(release);
			}
			else if (distanceBetween(press, v2) < distanceBetween(press, v3)) {
				this.setV2(release);
			}
			else { this.setV3(release); }
		} // END if
	} // END resize
	
}
