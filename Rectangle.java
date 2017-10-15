package Paint.src.paint;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Rectangle class
 * 
 */

//calculates area and perimeter for rectangles
public class Rectangle extends Shape {
	
	// FIELDS
	private	double height = 0, width = 0;
	Coordinates v1, v2, v3, v4; // start with v1 at top left, go clockwise

	// CONSTRUCTOR
	public Rectangle(Coordinates press, Coordinates release, Color color) {
		setV1(press);
		setV2(new Coordinates(release.x, press.y));
		setV3(release);
		setV4(new Coordinates(press.x, release.y));
		this.color = color;
		updateDimensions();
	}
	
	// METHODS
	public double getArea() { return height * width; }
	public double getPerimeter() { return (2 * height) + (2 * width); }
	
	// methods to adapt to changing a vertex
	public void setV1(Coordinates newCoordinates) {
		this.v1 = newCoordinates;
		updateDimensions();
	}
	public void setV2(Coordinates newCoordinates) {
		this.v2 = newCoordinates;
		updateDimensions();
	}
	public void setV3(Coordinates newCoordinates) {
		this.v3 = newCoordinates;
		updateDimensions();
	}
	public void setV4(Coordinates newCoordinates) {
		this.v4 = newCoordinates;
		updateDimensions();
	}
	
	public void setHeight(double d) {
		this.height = d;
	}
	
	public void setWidth(double aWidth) {
		this.width = aWidth;
	}
	
	public Coordinates getV1() {
		return v1;
	}
	public Coordinates getV2() {
		return v2;
	}
	public Coordinates getV3() {
		return v3;
	}
	public Coordinates getV4() {
		return v4;
	}
	
	
	// use top-left, top-right, and bottom-left vertices to calculate height and width
	private void updateDimensions() {
		if (getV1() != null && getV2() != null && getV3() != null && getV4() != null ) {
			setHeight(distanceBetween(getV1(), getV4()));
			setWidth(distanceBetween(getV1(), getV2()));
		}
	}
	

	public boolean isInRange(Coordinates c) {
		
		Boolean inRange = false;
		
		// check that coordinate is on or inside rectangle
		
		if ( (c.x >= v1.x && c.y <= v1.y) && (c.x <= v2.x && c.y >= v4.y ) ) {
			
			inRange = true;
		}
		
		return inRange;
		
	} // END isInRange(Coordinates)
	
	public void resize (Coordinates press, Coordinates release) {
		if (isInRange(press)) {
			if (distanceBetween(press, v1) < distanceBetween(press, v2) && distanceBetween(press, v1) < distanceBetween(press, v4)) {
				this.setV1(release);
			}
			else if (distanceBetween(press, v2) < distanceBetween(press, v3) && distanceBetween(press, v2) < distanceBetween(press, v1)) {
				this.setV2(release);
			}
			else if (distanceBetween(press, v3) < distanceBetween(press, v1) && distanceBetween(press, v3) < distanceBetween(press, v4)) {
				this.setV3(release);
			}
			else { this.setV4(release); }
			
		} // END if
	} // END resize

	@Override
	public void drawShape(Graphics g) {
		g.drawRect(v1.getX(), v1.getY(), (int)width, (int)height);
		g.setColor(getColor());
		g.fillRect(v1.getX(), v1.getY(), (int)width, (int)height);
	}
}