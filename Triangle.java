package paint;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * Triangle class
 * 
 * @author Mark Ozdemir (mo732)
 *
 */
public class Triangle extends Shape {

	//constants
	private static final double DEFAULT_EMPTY_VALUE = 0.0;
	private static final int TRI_SIDES = 3;

	//class member variables
	private double side1;
	private double side2;
	private double side3;
	private Coordinates v1, v2, v3;

	/**
	 * Triangle constructor
	 * @param args	a hashmap that contains the shape and associated side values
	 */
	Triangle(ArrayList<Coordinates> triPresses, Color color) {
		int counter = 0;
		setV1(triPresses.get(counter));
		counter++;
		setV2(triPresses.get(counter));
		counter++;
		setV3(triPresses.get(counter));
		updateSides();
		this.color = color;
	}

	/**
	 * sets value of v1
	 * @param c1
	 */
	private void setV1(Coordinates c1) {
		v1 = c1;
	}
	
	/**
	 * sets value of v2
	 * @param c1
	 */
	private void setV2(Coordinates c1) {
		v2 = c1;
	}
	
	/**
	 * sets value of v3
	 * @param c1
	 */
	private void setV3(Coordinates c1) {
		v3 = c1;
	}

	private void updateSides() {
		this.side1 = distanceBetween(getV1(), getV2());
		this.side2 = distanceBetween(getV2(), getV3());
		this.side3 = distanceBetween(getV3(), getV1());
	}

	/**
	 * @return v1 the value of side1
	 */
	protected Coordinates getV1() {
		return v1;
	}

	/**
	 * @return v2 the value of side1
	 */
	protected Coordinates getV2() {
		return v2;
	}

	/**
	 * @return v3 the value of side1
	 */
	protected Coordinates getV3() {
		return v3;
	}
	
	/**
	 * @return side1 the value of side1
	 */
	protected double getSide1() {
		return side1;
	}

	/**
	 * @return side2 the value of side2
	 */
	protected double getSide2() {
		return side2;
	}

	/**
	 * @return side3 the value of side3
	 */
	protected double getSide3() {
		return side3;
	}

	/**
	 * @return tempArea the area of the triangle
	 */
	public double getArea() {
		double intermediateS = 0;
		double tempArea = 0;

		intermediateS = (side1 + side2 + side3)/2;
		if (side1 > DEFAULT_EMPTY_VALUE && side2 > DEFAULT_EMPTY_VALUE && side3 > DEFAULT_EMPTY_VALUE) {
			tempArea = Math.sqrt(intermediateS*(intermediateS-side1)*(intermediateS-side2)*(intermediateS-side3));		
		}
		return tempArea;
	}

	/**
	 * @return tempPerimeter the perimeter of the triangle
	 */
	public double getPerimeter() {
		double tempPerimeter = 0;
		if (side1 > DEFAULT_EMPTY_VALUE && side2 > DEFAULT_EMPTY_VALUE && side3 > DEFAULT_EMPTY_VALUE) {
			tempPerimeter = side1 + side2 + side3;
		}
		return tempPerimeter;
	}

	
	@Override
	public void drawShape(Graphics g) {
		g.setColor(getColor());
		g.drawLine(getV1().getX(), getV1().getY(), getV2().getX(), getV2().getY());
		g.drawLine(getV2().getX(), getV2().getY(), getV3().getX(), getV3().getY());
		g.drawLine(getV3().getX(), getV3().getY(), getV1().getX(), getV1().getY());
		g.fillPolygon(new int[] {getV1().getX(), getV2().getX(), getV3().getX()}, 
				new int [] {getV1().getY(), getV2().getY(), getV3().getY()}, TRI_SIDES);
	}

	@Override
	public boolean isInRange(Coordinates c) {
		System.out.println("in Triangle: isInRange ");

		boolean inRange = false;
		int minCurX = 0;
		int minCurY = 0;
		int maxCurX = 0;
		int maxCurY = 0;

		minCurX = Math.min(v1.getX(), v2.getX());
		if (v3.getX() < minCurX) minCurX = v3.getX();

		minCurY = Math.min(v1.getY(), v2.getY());
		if (v3.getY() < minCurX) minCurY = v3.getY();

		maxCurX = Math.max(v1.getY(), v2.getY());
		if (v3.getY() > maxCurX) maxCurX = v3.getY();

		maxCurY = Math.max(v1.getY(), v2.getY());
		if (v3.getY() > maxCurY) maxCurY = v3.getY();

		// basic out-of-range checks
		if(c.getX() >= minCurX && c.getX() <= maxCurX && c.getY() >= minCurY && c.getY() <= maxCurY)
		{
//			if ((c.getX() >= Math.min(v1.getX(), v2.getX())) && (c.getX() >= v3.getX()) && (c.getX() <= Math.max(v1.getX(), v2.getX())) && c.getX() <= v3.getX()) {
//				if ((c.y >= Math.min(v1.y, v2.y)) && (c.y >= v3.y) && (c.y <= Math.max(v1.y, v2.y)) && c.y <= v3.y) {

					System.out.println("isInRange basic checks true");

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
					System.out.println(left.x+", "+left.y);
					System.out.println(mid.x+", "+mid.y);
					System.out.println(right.x+", "+right.y);
					
					// variables for calculating lines drawn by triangle's sides
					double m1, b1, m2, b2;

					m1 = (double)(((double)(left.y)-(double)(right.y)) / ((double)(left.x)-(double)(right.x)));
					b1 = (double)(left.y) - (double)(m1*(double)(left.x));

					// create lower and upper y-bounds on the left side of mid
					if (c.x <= mid.x) {
						m2 = (double)(((double)(mid.y)-(double)(left.y)) / ((double)(mid.x)-(double)(left.x)));
						b2 = (double)(left.y) - (double)(m2*(double)(left.x));
					} // END if

					// create lower and upper y-bounds on the right side of mid
					else {
						m2 = (double)(((double)(right.y)-(double)(mid.y)) / ((double)(right.x)-(double)(mid.x)));
						b2 = (double)(right.y) - (double)(m2*(double)(right.x));
					}
					
					// check for y between bounds
					if ((c.y <= Math.max((m1*c.x + b1), (m2*c.x + b2)) && 
							(c.y >= Math.min((m1*c.x + b1), (m2*c.x + b2))))) 
					{
							inRange = true;
					}

					System.out.println("m1 = " + m1);
					System.out.println("b1 = " + b1);
					System.out.println("m2 = " + m2);
					System.out.println("b2 = " + b2);
		//		}
//			} // END if (basic out-of-range check)
		} // END if (basic out-of-range check)
		System.out.println("Triag: isInRange(): true? ==> " + inRange);
		return inRange;

	} // END isInRange(Coordinates)
	
	
	@Override
	public void resize (Coordinates press, Coordinates release) {
		if (isInRange(press)) {
			if (distanceBetween(press, v1) <= Math.max(distanceBetween(press, v2), distanceBetween(press, v3))) {
				this.setV1(release);
			}
			else if (distanceBetween(press, v2) < distanceBetween(press, v3)) {
				this.setV2(release);
			}
			else this.setV3(release);
			updateSides();
		} // END if
	} // END resize
	

}
