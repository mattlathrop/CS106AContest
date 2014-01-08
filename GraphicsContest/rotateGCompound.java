import java.util.*;

import acm.graphics.*;


public class rotateGCompound extends GCompound {
	
	public GPoint rotate(double theta, GPoint point) {
			double x = point.getX();
			double y = point.getY();
			double r = Math.sqrt(Math.pow(x,2) + Math.pow(y, 2));
			double newtheta = Math.atan(y/x);
			double dx = r*Math.cos(newtheta + theta);
			double dy = r*Math.sin(newtheta + theta);
			point = new GPoint(dx, dy);
			return point;
			
	}
	
}
