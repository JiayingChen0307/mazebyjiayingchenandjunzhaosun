/**
 * 
 */
package edu.wm.cs.cs301.JunzhaoSun.generation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * BSPNodes are used to build a binary tree, where internal nodes keep track 
 * of lower and upper bounds of (x,y) coordinates.
 * Leaf nodes carry a list of walls. 
 * Branch nodes are internal nodes of the tree.
 * A BSP tree is a data structure to search for a set of walls 
 * to put on display in the FirstPersonView and Map.
 * 
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class BSPBranch extends BSPNode {
	// left and right branches of the binary tree
	private BSPNode lbranch, rbranch;
	// (x,y) coordinates and (dx,dy) direction
	private int x;
	private int y;
	private int dx;
	private int dy;

	/**
	 * Constructor with values for all internal fields
	 * Upper and lower bound values are obtained from the 
	 * given left and right branches, so those trees must
	 * be carry valid values.
	 * @param px x coordinate
	 * @param py y coordinate
	 * @param pdx x direction
	 * @param pdy y direction
	 * @param left child, assumes bounds are valid
	 * @param right child, assumes bounds are valid
	 */
	public BSPBranch(int px, int py, int pdx, int pdy, BSPNode left, BSPNode right) {
		lbranch = left;
		rbranch = right;
		x = px; 
		y = py;
		dx = pdx; 
		dy = pdy;
		// update bounds in super class with values from left and right branches
		// note: own values of this node do not matter
		// obviously tree is built from valid subtrees in a bottom up manner
		// or there must be some update later 
		setLowerBoundX(Math.min(left.getLowerBoundX(), right.getLowerBoundX()));
		setUpperBoundX(Math.max(left.getUpperBoundX(), right.getUpperBoundX()));
		setLowerBoundY(Math.min(left.getLowerBoundY(), right.getLowerBoundY()));
		setUpperBoundY(Math.max(left.getUpperBoundY(), right.getUpperBoundY()));
	}
	/**
	 * @return tells if object is a leaf node
	 */
	@Override
	public boolean isIsleaf() {
		return false ;
	}

	public BSPNode getLeftBranch(){
		return lbranch;
	}

	public BSPNode getRightBranch(){
		return rbranch;
	}

	/**
	 * Store the content of a branch node, in particular its left and right children
	 * 
	 * The method recursively stores BSP nodes for left and right children.
	 * Note that the numbering schemes needs to match with the MazeFileReader class.
	 * 
	 * @param n is the node considered
	 * @param doc document to add data to
	 * @param mazeXML element to add data to
	 * @param number is an index number for this node in the XML format
	 * @return the highest used index number
	 */

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return the dx
	 */
	public int getDx() {
		return dx;
	}

	/**
	 * @return the dy
	 */
	public int getDy() {
		return dy;
	}



}
