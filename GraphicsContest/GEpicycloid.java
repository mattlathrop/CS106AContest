
//import acm.program.*;
import acm.graphics.*;

//import java.lang.*;
import java.awt.*;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GEpicycloid extends GCompound {

	
public GOval centerCircle;
public GOval outerCircle;
public GLine armLine;
public GOval markerCircle;
//private GPoint centerCanvas;
private double step = 0;
private boolean rIsNegative;
private double armLength;
private Map<GPoint, Double> pointStorage = new HashMap<GPoint, Double>();
public boolean graph = true;

	/* Constructor for GEpicycloid */

	public GEpicycloid(GOval newCenterCircle, GOval newOuterCircle, GLine newArmLine, GOval newMarkerCircle, boolean originalRIsNegative) {
		// Create duplicates of all objects
		centerCircle = duplicateCircle(newCenterCircle);
		outerCircle = duplicateCircle(newOuterCircle);
		armLine = duplicateLine(newArmLine);
		markerCircle = duplicateCircle(newMarkerCircle);
		rIsNegative = originalRIsNegative;
		
		// do all the required math to move objects to center at origin
		double centerX = newCenterCircle.getX();
		double centerY = newCenterCircle.getY();
		double centerR = newCenterCircle.getWidth()/2;
		double dxCenterOuter = newOuterCircle.getX() - centerX ;
		double dyCenterOuter = newOuterCircle.getY() - centerY;
		double dxCenterLine = newArmLine.getX() - centerX;
		double dyCenterLine = newArmLine.getY() - centerY;
		double dxCenterMarker = newMarkerCircle.getX() - centerX;
		double dyCenterMarker = newMarkerCircle.getY() - centerY;
		
		//Sets all of the objects to new locations
		centerCircle.setLocation(-centerR,-centerR);
		outerCircle.setLocation( -centerR + dxCenterOuter, -centerR + dyCenterOuter);
		armLine.setLocation(-centerR + dxCenterLine, -centerR + dyCenterLine);
		markerCircle.setLocation(-centerR + dxCenterMarker, -centerR + dyCenterMarker);
		//centerCanvas = new GPoint(centerX+centerR , centerY+centerR);
		
		// adds all the objects to the GCompound
		add(centerCircle);
		add(outerCircle);
		add(armLine);
		add(markerCircle);
		armLength = armLine.getWidth();
		if (armLine.getEndPoint().getX() > armLine.getStartPoint().getX()) armLength = -armLength;
		
		// Stores values for t to recall later
		//storeValues();
		
	}
	
	/* duplicates a GOval object */
	private GOval duplicateCircle(GOval oldCircle) {
		double x = oldCircle.getX();
		double y = oldCircle.getY();
		double d = oldCircle.getWidth();
		boolean filled = oldCircle.isFilled();
		GOval newOval = new GOval(x,y,d,d);
		newOval.setFilled(filled);
		return newOval;
	}
	/* duplicates a GLine object */
	private GLine duplicateLine(GLine oldLine) {
		GPoint start = oldLine.getStartPoint();
		GPoint end = oldLine.getEndPoint();
		GLine newLine = new GLine(start.getX(), start.getY(), end.getX(), end.getY());
		return newLine;
	}
	
	/* Stores all values for the shape provided to 100000 */
	//private void storeValues() {
		//double centerR = centerCircle.getWidth()/2.0;
		//double outerR = outerCircle.getWidth()/2.0;
		//if (rIsNegative) outerR = -outerR;
		//for (double i = 0; i < 10000; i += .1) {
			//double X = (centerR+outerR)*Math.cos(i) + armLength*Math.cos(((centerR+outerR)*i)/outerR);
			//double Y = - (centerR+outerR)*Math.sin(i) + armLength*Math.sin(((centerR+outerR)*i)/outerR);
			//pointStorage.put(new GPoint(X,Y), i);
		//}
	//}
	
	/* Finds the step value for a given GPoint */
	public double getValue(GPoint point) {
		Double step = pointStorage.get(point);
		return step;
	}
	
	/* Does all the math for creating each step of the animation */
	public void getNextStep(Color currentColor, boolean reverseDirection) {
		// Let's do some MATH!!!!
		double centerR = centerCircle.getWidth()/2.0;
		double outerR = outerCircle.getWidth()/2.0;
		if (rIsNegative) outerR = -outerR;
		double markerR = markerCircle.getWidth()/2.0;
		double oldMarkerCenterX = markerCircle.getX() + markerR;
		double oldMarkerCenterY = markerCircle.getY() + markerR;
		double newOuterCenterX = (centerR+outerR)*Math.cos(step);
		double newOuterCenterY;
		if (!reverseDirection) {
			newOuterCenterY =  (centerR+outerR)*Math.sin(step);
		} else {
			newOuterCenterY =  - (centerR+outerR)*Math.sin(step);
		}
		double newMarkerCenterX = (centerR+outerR)*Math.cos(step) + armLength*Math.cos(((centerR+outerR)*step)/outerR);
		double newMarkerCenterY;
		if (!reverseDirection) {
			newMarkerCenterY =  (centerR+outerR)*Math.sin(step) + armLength*Math.sin(((centerR+outerR)*step)/outerR);
		} else {
			newMarkerCenterY =  - (centerR+outerR)*Math.sin(step) + armLength*Math.sin(((centerR+outerR)*step)/outerR);
		}
		
		// Time to update the figure!
		remove(outerCircle);
		add(outerCircle, newOuterCenterX - Math.abs(outerR), newOuterCenterY - Math.abs(outerR));
		remove(armLine);
		remove(markerCircle);
		armLine = new GLine(newOuterCenterX, newOuterCenterY, newMarkerCenterX, newMarkerCenterY);
		add(armLine);
		add(markerCircle, newMarkerCenterX - markerR, newMarkerCenterY - markerR);
		
		// Don't Forget to actually graph the line!!!
		if (graph) {
			GLine newLine = new GLine(oldMarkerCenterX, oldMarkerCenterY, newMarkerCenterX, newMarkerCenterY);
			newLine.setColor(currentColor);
			add(newLine);
		}
		
		
		// update the step!
		step += .01;
	}
	
	public void cleanUp() {
		// leaves just the graph and removes constructors 
		remove(centerCircle);
		remove(outerCircle);
		remove(armLine);
		remove(markerCircle);
	}
	
	public double getLogInfoCR() {
		double centerR = centerCircle.getWidth()/2.0;
		return centerR;
	}
	
	public double getLogInfoOR() {
		double outerR = outerCircle.getWidth()/2.0;
		return outerR;
	}
	
	public double getLogInfoAL() {
		armLength = armLine.getWidth();
		if (armLine.getEndPoint().getX() > armLine.getStartPoint().getX()) armLength = -armLength;
		return armLength;
	}
	
	public GPoint getLogInfoPoint() {
		double x = centerCircle.getX();
		double y = centerCircle.getY();
		return new GPoint(x,y);
	}
	
	public double getLogInfoStep() {
		return step;
	}
	
}
