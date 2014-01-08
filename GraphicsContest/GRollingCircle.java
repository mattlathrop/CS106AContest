import acm.program.*;
import acm.graphics.*;

import java.lang.*;
import java.awt.*;
import java.util.*;

public class GRollingCircle extends rotateGCompound {
	private double armLength;
	private GOval rollingCircle;
	private GOval markerCircle;
	private GLine armLine;
	public GPoint markerCenter;
	
	public GRollingCircle(GOval originalRollingCircle, GOval originalMarkerCircle, GLine originalArmLine) {
		rollingCircle = duplicateCircle(originalRollingCircle);
		armLine = duplicateLine(originalArmLine);
		markerCircle = duplicateCircle(originalMarkerCircle);
		
		double rollingX = originalRollingCircle.getX();
		double rollingY = originalRollingCircle.getY();
		double rollingR = originalRollingCircle.getWidth()/2;
		double dxRollingLine = originalArmLine.getX() - rollingX;
		double dyRollingLine = originalArmLine.getY() - rollingY;
		double dxRollingMarker = originalMarkerCircle.getX() - rollingX;
		double dyRollingMarker = originalMarkerCircle.getY() - rollingY;
		
		rollingCircle.setLocation(-rollingR,-rollingR);
		armLine.setLocation(-rollingR + dxRollingLine, -rollingR + dyRollingLine);
		markerCircle.setLocation(-rollingR + dxRollingMarker, -rollingR + dyRollingMarker);
		
		add(rollingCircle);
		add(armLine);
		add(markerCircle);
		
		double markerCX = markerCircle.getX() + markerCircle.getWidth()/2.0;
		double markerCY = markerCircle.getY() + markerCircle.getWidth()/2.0;
		markerCenter = new GPoint(markerCX, markerCY);
		
		armLength = armLine.getWidth();
		if (armLine.getEndPoint().getX() > armLine.getStartPoint().getX()) armLength = -armLength;
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
	
	public void update(GPoint point) {
		double markerX = point.getX() - markerCircle.getWidth()/2.0;
		double markerY = point.getY() - markerCircle.getWidth()/2.0;
		pause(1000);
		markerCircle.setLocation(markerX, markerY);
		markerCenter = new GPoint(markerX, markerY);
	}
}
