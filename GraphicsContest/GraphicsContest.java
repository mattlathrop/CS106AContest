/*
 * File: GraphicsContest.java
 * --------------------------
 */

import acm.program.*;
import acm.util.*;
import acm.graphics.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GraphicsContest extends GraphicsProgram implements ChangeListener {

/** Radius of the marker circle */
private static final int MARKER_CIRCLE_RADIUS = 5;

/** Controls the highest value for RGB */
private static final int MAX_RGB = 255;

/** Controls the lowest value for RGB */
private static final int MIN_RGB = 0;

/** Minimum break time */
private static final int MIN_BREAK = 1;

/** Maximum break time */
private static final int MAX_BREAK = 10000;

/** Break time division factor */
private static final int TIME_FACTOR = 1000;

/** Size of color box */
private static final int COLOR_BOX_SIZE = 30;

/** Width and height of application window in pixels */
public static final int APPLICATION_WIDTH = 1024;
public static final int APPLICATION_HEIGHT = 768;

/** Private Instance Variables */
private int clickTracker = 0; // Tracks the number of clicks
private int figureTracker = 0; // Tracks what figure is active
private GPoint lastClick; //Stores where the last click occurred
private ArrayList<GEpicycloid> figureList = new ArrayList<GEpicycloid>(); //Stores all the figures on the canvas
public GOval centerCircle; // Stores the center circle during construction
private GOval outerCircle; // Stores the outer circle during construction
private GLine armLine; // Stores the arm line during construction
private GOval markerCircle = new GOval(MARKER_CIRCLE_RADIUS*2, MARKER_CIRCLE_RADIUS*2); // stores the marker circle during construction
private JButton makeItCool; // Reads a random configuation.
private JButton startButton; // Button used for starting animation
private JButton stopButton; // Button used for ending animation
private JButton flipDirection; // Flips the direction of rotation of the outer circle.
private JButton doneWithFigure; //Marks a figure as Done and let's you make another
private JButton clear; //clears the screen
private JSlider speedSlider;
private JSlider redSlider;
private JSlider greenSlider;
private JSlider blueSlider;
private JButton liftPen;
private JButton center;
private JButton removeLast;
private boolean animate; //Used to store whether or not the program should be drawing
//private boolean figureDone = false; // Used to store whether or not the user is done with the current figure.
private boolean rIsNegative = false;
private boolean lineActive = false;
private Color currentColor;
private GRect colorBox;
private RandomGenerator rgen = new RandomGenerator();
private boolean reverseDirection = false;


	public static void main(String[] args) {
		new GraphicsContest().start(args);
	}

	/* Sets up the user interface */
	public void init() {
		
		markerCircle.setFilled(true);
		
		startButton = new JButton("Start"); 
		add(startButton, EAST);
		
		stopButton = new JButton("Stop");
		add(stopButton, EAST);
		
		flipDirection = new JButton("Change rotation");
		add(flipDirection, EAST);
		
		doneWithFigure = new JButton("Next Figure");
		add(doneWithFigure, SOUTH);
		
		removeLast = new JButton("Remove Figure");
		add(removeLast, SOUTH);
		
		clear = new JButton("Clear");
		add(clear, SOUTH);
		
		add(new JLabel("Speed"), EAST);
		speedSlider = new JSlider(MIN_BREAK, MAX_BREAK, TIME_FACTOR);
		speedSlider.addChangeListener(this);
		add(speedSlider, EAST);
		
		add(new JLabel("Red"), EAST);
		redSlider = new JSlider(MIN_RGB, MAX_RGB, MIN_RGB);
		redSlider.addChangeListener(this);
		add(redSlider, EAST);
		
		add(new JLabel("Green"), EAST);
		greenSlider = new JSlider(MIN_RGB, MAX_RGB, MIN_RGB);
		greenSlider.addChangeListener(this);
		add(greenSlider, EAST);
		
		add(new JLabel("Blue"), EAST);
		blueSlider = new JSlider(MIN_RGB, MAX_RGB, MIN_RGB);
		blueSlider.addChangeListener(this);
		add(blueSlider, EAST);
		
		JLabel spacer = new JLabel("");
		add(spacer, EAST);
		
		colorBox = new GRect(0,0,COLOR_BOX_SIZE,COLOR_BOX_SIZE);
		colorBox.setFilled(true);
		add(colorBox);
		
		center = new JButton("Center Figure");
		add(center, EAST);
		
		liftPen = new JButton("Toggle Pen");
		add(liftPen, EAST);
		
		makeItCool = new JButton("Make it Cool");
		add(makeItCool, SOUTH);
		
		
		addMouseListeners();
		addActionListeners();
	}
	
	public void run() {
		runAnimation();
	}
	
	/* Draws the original shape based off clicks */
	
	public void mousePressed(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		switch (clickTracker) {
			case 0:
				//figureDone = false;
				flipDirection.setEnabled(false);
				lastClick = new GPoint(x,y);
				lineActive = true;
				break;
			case 1:
				lineActive = false;
				remove(centerCircle);
				drawCenter(x, y);
				//println(lastClick + " x " + x + " y " +y);
				//double centerX = lastClick.getX();
				//double centerY = lastClick.getY();
				lastClick = new GPoint(lastClick.getX() + centerCircle.getWidth(), lastClick.getY());
				break;
			//case 2:
				//lastClick = new GPoint(x,y);
				//lineActive = true;
				//break;
			case 2:
				lineActive = false;
				remove(outerCircle);
				drawOuter(x, y);
				break;
			case 3:
				lineActive = false;
				remove(armLine);
				drawArm(x,y);
				initalizeFigure();
				break;
		}
		clickTracker++;
		if (clickTracker == 3 || clickTracker == 2) lineActive = true;
	}
	
	public void mouseMoved(MouseEvent e) {
		if (clickTracker == 1) {
			if (centerCircle != null) {
				remove(centerCircle);
			}
			if(lineActive) {
				drawCenter(e.getX(),e.getY());
			}
		}
		if (clickTracker == 2) {
			if (outerCircle != null) {
				remove(outerCircle);
			}
			if(lineActive) {
				//lastClick = findClosestPoint(e.getX(),e.getY());
				//println(lastClick);
				drawOuter(e.getX(),e.getY());
			}
		}
		if (clickTracker == 3) {
			if (armLine != null) {
				remove(armLine);
			}
			if(lineActive) {
				drawArm(e.getX(),e.getY());
			}
		}
	}
	
	/* Draws the center circle based off user clicks */
	private void drawCenter(double x, double y) {
		double r = findCenterRadius(x, y);
		GPoint corner = findCenterCorner(r, x ,y);
		centerCircle = new GOval(2*r,2*r);
		add(centerCircle, corner);
		//GPoint center = new GPoint(corner.getX() - r, corner.getY() - r );
	}
	
	/* Finds the radius of the center circle based off the user clicks */
	private double findCenterRadius(double x, double y) {
		double dX = Math.abs(lastClick.getX() - x);
		double dY = Math.abs(lastClick.getY() - y);
		double dSquared = Math.pow(dX, 2) + Math.pow(dY, 2);
		double r = Math.sqrt(dSquared) / 2.0;
		return r;
	}
	/* Finds the point needed to add the center circle */
	private GPoint findCenterCorner(double r, double x, double y) {
		double cornerX = (lastClick.getX() + x)/2.0 - r;
		double cornerY = lastClick.getY() - r;
		GPoint point = new GPoint(cornerX,cornerY);
		return point;
	}
	
	/* Finds the closet point on the center circle to the current location of the mouse */ 
	//private GPoint findClosestPoint(double x2, double y2) {
		//double x1 = centerCircle.getX() + centerCircle.getWidth()/2.0;
		//double y1 = centerCircle.getY() + centerCircle.getWidth()/2.0;
		//double dX = x2 - x1;
		//double dY = y2 - y1;
		//double magD = Math.sqrt(dX*dX + dY*dY);
		//double x = x1 + dX / magD * centerCircle.getWidth()/2.0;
		//double y = y1 + dY / magD * centerCircle.getWidth()/2.0;
		//return (new GPoint(x,y));
	//}
	
	/* Draws the outer circle based off user clicks */
	private void drawOuter(double x, double y) {
		double r = findOuterRadius(x, y);
		GPoint corner = findOuterCorner(r);
		outerCircle = new GOval(2.0*Math.abs(r),2.0*Math.abs(r));
		add(outerCircle, corner);
	}
	/* Finds the radius of the outer circle based off the user clicks */
	private double findOuterRadius(double x, double y) {
		double dX = (lastClick.getX() - x);
		double dY = (lastClick.getY() - y);
		double dSquared = Math.pow(dX, 2) + Math.pow(dY, 2);
		double r = Math.sqrt(dSquared)/2.0;
		if (dX > 0) r = -r;
		return r;
	}
	/* Finds the point needed to add the outer circle */
	private GPoint findOuterCorner(double r) {
		rIsNegative = false;
		if (r < 0) rIsNegative = true;
		//double cornerX = (lastClick.getX() + x)/2.0 - r;
		//double cornerY = lastClick.getY() - r;
		//GPoint point = new GPoint(cornerX,cornerY);
		//return point;
		//double cornerX = 
		double centerX = (centerCircle.getWidth() / 2.0) + centerCircle.getX();
		double centerY = (centerCircle.getHeight() / 2.0) + centerCircle.getY();
		double x;
		if (!rIsNegative) {
			x = centerX + (centerCircle.getWidth() / 2.0);
		} else {
			x = centerX + (centerCircle.getWidth() / 2.0) + r*2.0;
		}
		double y = centerY - Math.abs(r);
		GPoint point = new GPoint(x,y);
		return point;
	}
	/* Draws the arm and marker circle and adds them to the canvas */
	private void drawArm(double x, double y) {
		double outerX = outerCircle.getX();
		double outerY = outerCircle.getY();
		double outerR = outerCircle.getWidth()/2;
		armLine = new GLine(x, outerY+outerR, outerX+outerR, outerY+outerR);
		add(armLine);
		GPoint startpoint = armLine.getStartPoint();
		add(markerCircle, startpoint.getX() - MARKER_CIRCLE_RADIUS, startpoint.getY()- MARKER_CIRCLE_RADIUS);
	}
	
	/* Uses the instance variables centerCircle, outerCircle, armLine, and markerCircle, 
	 * to create a GEpicycloid*/
	
	private void initalizeFigure() {
		double centerX = (centerCircle.getWidth() / 2.0) + centerCircle.getX();
		double centerY = (centerCircle.getHeight() / 2.0) + centerCircle.getY();
		figureList.add(new GEpicycloid(centerCircle, outerCircle, armLine, markerCircle, rIsNegative));
		add(figureList.get(figureTracker), centerX, centerY);
		remove(centerCircle);
		remove(outerCircle);
		remove(armLine);
		remove(markerCircle);
	}
	
	/* Controls what all the buttons do in the UI */
	
	public void actionPerformed(ActionEvent e) {
		if (startButton == e.getSource()) { //starts animating the current figure
			animate = true;
		}
		if (stopButton == e.getSource()) { //stops animating the current figure
			animate = false;
		}
		if (doneWithFigure == e.getSource()) {
			flipDirection.setEnabled(true);
			animate = false;
			//figureDone = true;
			animate = false;
			figureList.get(figureTracker).cleanUp();
			figureList.get(figureTracker).markAsComplete();
			clickTracker = 0;
			if (!figureList.isEmpty()) figureTracker++;
		}
		if (clear == e.getSource()) {
			flipDirection.setEnabled(true);
			animate = false;
			pause(5);
			if (!figureList.isEmpty()) {
				figureList.get(figureTracker).markAsComplete();
			}
			removeAll();
			add(colorBox);
			clickTracker = 0;
			figureTracker= 0;
			figureList.clear();
		}
		if (flipDirection == e.getSource()) {
			if (reverseDirection == true) {
				reverseDirection = false;
			} else {
				reverseDirection = true;
			}
		}
		if (makeItCool == e.getSource()) {
			flipDirection.setEnabled(false);
			if (reverseDirection == false) {
				generateCool();
			} else {
				generateCoolReverse();
			}
			//logStart();
		}
		if (center == e.getSource()) {
			double centerX = getWidth()/2.0;
			double centerY = getHeight()/2.0;
			figureList.get(figureTracker).setLocation(centerX, centerY);
		}
		if (liftPen == e.getSource()) {
			if (figureList.get(figureTracker).graph) {
				figureList.get(figureTracker).graph = false;
			} else {
				figureList.get(figureTracker).graph = true;
			}
		}
		if (removeLast == e.getSource()) {
			if (!figureList.isEmpty()) {
				animate = false;
				figureList.get(figureTracker).markAsComplete();
				remove(figureList.get(figureTracker));
				figureList.remove(figureTracker);
			}
			flipDirection.setEnabled(true);
			//figureDone = true;
			clickTracker = 0;
		}
	}
	
	private void generateCool() {
		if (animate == false) {
		try {
			BufferedReader rd = new BufferedReader(new FileReader("cool.txt"));
    		ArrayList<String> lines = new ArrayList<String>();
    		while (true) {
    			String line = rd.readLine();
    			if (line == null) break;
    			if (line.startsWith("CenterR")) {
    				lines.add(line);
    			}
    		}
    		rd.close();
    		int index = rgen.nextInt(0,lines.size()-1);
			String line = lines.get(index);
    		int centerRStart = line.indexOf("[") +1;
    		int centerREnd = line.indexOf("]");
    		double centerR = Double.parseDouble(line.substring(centerRStart, centerREnd));
    		int outerRStart = line.indexOf("[", centerREnd +1) +1;
    		int outerREnd = line.indexOf("]", centerREnd +1);
    		double outerR = Double.parseDouble(line.substring(outerRStart, outerREnd));
    		int armLStart = line.indexOf("[", outerREnd +1) +1;
    		int armLEnd = line.indexOf("]", outerREnd +1);
    		double armLength = Double.parseDouble(line.substring(armLStart,armLEnd));
    		double scale = rgen.nextDouble(.5,2.0);
    				
    		//Modify values by scale
    		centerR = centerR/scale;
    		outerR = outerR/scale;
    		armLength = armLength/scale;
    				
    		// Create figure
    		centerCircle = new GOval(2*centerR,2*centerR);
    		add(centerCircle, getWidth()/2.0 -centerR, getHeight()/2.0 -centerR);
    		outerCircle = new GOval(2.0*Math.abs(outerR),2.0*Math.abs(outerR));
    		GPoint corner = findOuterCorner(outerR);
    		add(outerCircle, corner);
    		double outerX = outerCircle.getX();
    		double outerY = outerCircle.getY();
    		double absouterR = outerCircle.getWidth()/2;
    		double x = armLength + (outerX+absouterR);
    		armLine = new GLine(x, outerY+absouterR, outerX+absouterR, outerY+absouterR);
    		add(armLine);
    		GPoint startpoint = armLine.getStartPoint();
    		add(markerCircle, startpoint.getX() - MARKER_CIRCLE_RADIUS, startpoint.getY()- MARKER_CIRCLE_RADIUS);
    		initalizeFigure();
		} catch (IOException ex) {
    		throw new ErrorException(ex);
    	}
		}
	}
	
	private void generateCoolReverse() {
		if (animate == false) {
		try {
			BufferedReader rd = new BufferedReader(new FileReader("coolR.txt"));
    		ArrayList<String> lines = new ArrayList<String>();
    		while (true) {
    			String line = rd.readLine();
    			if (line == null) break;
    			if (line.startsWith("CenterR")) {
    				lines.add(line);
    			}
    		}
    		rd.close();
    		int index = rgen.nextInt(0,lines.size()-1);
			String line = lines.get(index);
    		int centerRStart = line.indexOf("[") +1;
    		int centerREnd = line.indexOf("]");
    		double centerR = Double.parseDouble(line.substring(centerRStart, centerREnd));
    		int outerRStart = line.indexOf("[", centerREnd +1) +1;
    		int outerREnd = line.indexOf("]", centerREnd +1);
    		double outerR = Double.parseDouble(line.substring(outerRStart, outerREnd));
    		int armLStart = line.indexOf("[", outerREnd +1) +1;
    		int armLEnd = line.indexOf("]", outerREnd +1);
    		double armLength = Double.parseDouble(line.substring(armLStart,armLEnd));
    		double scale = rgen.nextDouble(.5,2.0);
    				
    		//Modify values by scale
    		centerR = centerR/scale;
    		outerR = outerR/scale;
    		armLength = armLength/scale;
    				
    		// Create figure
    		centerCircle = new GOval(2*centerR,2*centerR);
    		add(centerCircle, getWidth()/2.0 -centerR, getHeight()/2.0 -centerR);
    		outerCircle = new GOval(2.0*Math.abs(outerR),2.0*Math.abs(outerR));
    		GPoint corner = findOuterCorner(outerR);
    		add(outerCircle, corner);
    		double outerX = outerCircle.getX();
    		double outerY = outerCircle.getY();
    		double absouterR = outerCircle.getWidth()/2;
    		double x = armLength + (outerX+absouterR);
    		armLine = new GLine(x, outerY+absouterR, outerX+absouterR, outerY+absouterR);
    		add(armLine);
    		GPoint startpoint = armLine.getStartPoint();
    		add(markerCircle, startpoint.getX() - MARKER_CIRCLE_RADIUS, startpoint.getY()- MARKER_CIRCLE_RADIUS);
    		initalizeFigure();
		} catch (IOException ex) {
    		throw new ErrorException(ex);
    	}
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == redSlider || e.getSource() == blueSlider || e.getSource() == greenSlider)  {
			currentColor = new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
			colorBox.setFillColor(currentColor);
			figureList.get(figureTracker).markerCircle.setFillColor(currentColor);
		}
	}
	
	private void runAnimation() {
		pause(5);
		while(true) {
			pause(speedSlider.getValue()/TIME_FACTOR);
			//pause(100);
			if(animate){
				currentColor = new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
				figureList.get(figureTracker).getNextStep(currentColor, reverseDirection);
				//if (!figureList.isEmpty()) println("Step: " + figureList.get(figureTracker).getLogInfoStep());
			} else {
				//if (!figureList.isEmpty()) println("Step: " + figureList.get(figureTracker).getLogInfoStep());
			}
		}
			//figureList.get(figureTracker).getNextStep();
			//GEpicycloid a = figureList.get(figureTracker); // used for debugging
			//remove(figureList.get(figureTracker));
			//add(figureList.get(figureTracker));
	}
	
	//private void logStart()  {
		//try {
			//PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));
			//double centerR = figureList.get(figureTracker).getLogInfoCR();
			//double outerR = figureList.get(figureTracker).getLogInfoOR();
			//double armLine = figureList.get(figureTracker).getLogInfoAL();
			//GPoint centerXY = figureList.get(figureTracker).getLogInfoPoint();
			//wr.println("***Start***");
			//wr.println("CenterR: ["+ centerR + "] OuterR: [" + outerR + "] ArmLine: [" + armLine + "] X: [" + centerXY.getX() + "] Y: [" + centerXY.getY() + "]");
			//wr.close();
		//} catch(IOException ex) {
		//	throw new ErrorException(ex);  
		//}
//	}
	
	//private void logEnd() {
		//try {
			//PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));
		//	double step = figureList.get(figureTracker).getLogInfoStep();
		//	wr.println("Step: " + step);
		//	wr.println("***End***");
		//	wr.close();
		//} catch(IOException ex) {
		//	throw new ErrorException(ex);  
		//}
	//}

}
