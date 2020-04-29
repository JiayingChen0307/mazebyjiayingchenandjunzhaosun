package edu.wm.cs.cs301.JunzhaoSun.generation;

import java.util.ArrayList;

/**
 * This class has the responsibility to create a maze of given dimensions (width, height)
 * using Eller's algorithm.
 * It extends  MazeBuilder class which implements Runnable.
 * The MazeFactory has a MazeBuilder and handles the thread management. 
 */
public class MazeBuilderEller extends MazeBuilder implements Runnable {
    
	protected SingleRandom rd;
	 
	/**
	 * This method is a constructor of MazeBuilderEller class.It initializes a SingleRandom object
	 * that we can use in other methods to randomly merge cells.
	 */
	public MazeBuilderEller (){
		//instantiate a random number generator class
	    rd = SingleRandom.getRandom();

	}
   
	/**
	 * This method is also a constructor of MazeBuilderEller class. It allows to make the random number
	 * deterministic for testing.
	 * @param deterministic
	 */
	public MazeBuilderEller (boolean deterministic){
	    if (true == deterministic){
	        SingleRandom.setSeed(0);
	    }
        rd = SingleRandom.getRandom();
	}

	/**
	 * Construction with option to set seed for maze generation
	 * @param seed (cannot be null)
	 */
	public MazeBuilderEller(int seed){
		SingleRandom.setSeed(seed);
		rd = SingleRandom.getRandom();
	}
	/**
	 * This method generates pathways in the maze by tearing down wallboards following Eller's algorithm.
	 * It goes through each row, tear downs wallboards and then go to the next row and forget row above.
	 */
	@Override
	protected void generatePathways() {
		final int rowindex = height-1;
	    //initialize first row and the list of sets	for the first row, and assign cells to sets
		int[] row = new int[width];//columns range [0,width-1]
		ArrayList<ArrayList<Integer>> sets = new ArrayList<ArrayList<Integer>>();
		for (int c=0; c<width;c++) {
			row[c] = c+1;
			ArrayList<Integer> set = new ArrayList<Integer>();
			set.add(c);
			sets.add(set);
		}
		//repeat the horizontal merge and vertical merge (Note that 'row' and 'sets' are reset for each row)
	 	for (int r=0; r<rowindex;r++) {
			mergeHorizontal(r,row, sets);
			row = this.mergeVertical(r,row, sets);
			mapNextRowCellSet(row, sets);
		} 
	 	//merge cells in the last row
	 	this.finish(row);
	}
	
	/**
	 * This method assigns sets to the unassigned cells in the new row.
	 * @param row: an integer array. The index refers to the column index of a cell and the value refers to the set that a cell belongs to.
	 * @param sets: a collection of sets. A set records the the column indices of cells in it.
	 */
	private void mapNextRowCellSet(int[] row, ArrayList<ArrayList<Integer>> sets) {
	    //obtain a list of unassigned cells
		ArrayList<Integer> empty = new ArrayList<Integer>();
		for (int c=0; c<row.length;c++) {
			if (row[c] == 0) {
               empty.add(c);
			}
		}
		//assign sets to the cells 
		for (int i=0; i<sets.size();i++) {
			if (sets.get(i).size() == 0) {
				if (empty.isEmpty()) {
					break;
				}else {
				sets.get(i).add((int)empty.get(0));
				row[empty.get(0)] = i+1;
				empty.remove(0);}
		    }
	    }
	}
	/**
	 * This method randomly merges cells in a row.
	 * It would not tear down wallboards belong to rooms
	 * @param rowindex:the index of a row (0<=rowindex<height)
	 * @param row:an integer array. The index refers to the column index of a cell and the value refers to the set that a cell belongs to.
	 * @param sets:a collection of sets. A set records the the column indices of cells in it.
	 */
	private void mergeHorizontal(int rowindex, int[]row, ArrayList<ArrayList<Integer>> sets) {
		//assign cells in the room with the same set
		for (int col=0;col<width-1;col++) {
		   if (floorplan.isInRoom(col,rowindex)&floorplan.isInRoom(col+1, rowindex)&row[col+1]!=row[col]) {
			  mergeEast(sets,row,col);}
		}
		//merge for a random number of cells
		int nummerge = rd.nextIntWithinInterval(width/2,width-1);
		for (int i=0; i<nummerge;i++) {
			//randomly choose a cell to merge
			int c = rd.nextIntWithinInterval(0,width-2);
			//delete the wallboard between the two cells
    	    Wallboard curwallboard = new Wallboard(c,rowindex,CardinalDirection.East);
			if(floorplan.canTearDown(curwallboard)&row[c+1]!=row[c]) {
				floorplan.deleteWallboard(curwallboard);
                mergeEast(sets,row,c);
			}
		}
	}
	
	/**
	 * This method merges the set of a cell with the set of its right cell.
	 * Note that this method only update the cells and sets. It does not tear down any wall.
	 * @param sets:a collection of sets. A set records the the column indices of cells in it.
	 * @param row:an integer array. The index refers to the column index of a cell and the value refers to the set that a cell belongs to.
	 * @param c:the column index of a cell
	 */
	private void mergeEast(ArrayList<ArrayList<Integer>> sets, int[] row, int c) {
        //update the mapping of cells and sets
        ArrayList<Integer> s = (ArrayList<Integer>)(sets.get(row[c+1]-1));      
        while (s.size()!=0) {
           //merge two sets by removing every cell from one set and add it to another
           int column = s.get(0);
     	   sets.get(row[c]-1).add(column);
     	   s.remove(0);
     	   row[column] = row[c];
        }
	}
	/**
	 * This method randomly merges cells in the next row with cells in the current row
	 * Note that this method generates a new 'row' but write on the existing collection of sets
	 * @param rowindex:the index of a row (0<=rowindex<height)
	 * @param row:an integer array. The index refers to the column index of a cell and the value refers to the set that a cell belongs to.
	 * @param sets:a collection of sets. A set records the the column indices of cells in it.
	 * @return the next row
	 */
	private int[] mergeVertical(int rowindex,int[] row, ArrayList<ArrayList<Integer>> sets) {
		int[] nextrow = new int[sets.size()];
		//randomly select one cell from each set in a row
		for (int i=0; i<sets.size(); i++) {
			ArrayList<Integer> s = (ArrayList<Integer>) sets.get(i);
			if (s.size()>0) {
			   ArrayList<Integer> newset = new ArrayList<Integer>();
			   //note that the actual number of cells merged in the next line may be less than nummmerge,
			   //since the random function may generate the same index
			   int num = rd.nextIntWithinInterval(0, s.size()-1);
			   int c = s.get(num);
			   Wallboard curwallboard = new Wallboard(c,rowindex,CardinalDirection.South);
			   mergeSouth(curwallboard,c,row,nextrow,newset,sets);
			   if (s.size()!=1) {
			      int nummerge = rd.nextIntWithinInterval(1, s.size()-1);
			      for (int counter=0;counter<nummerge;counter++) {
				     num = rd.nextIntWithinInterval(0, s.size()-1);
				     c = s.get(num);
				     curwallboard = new Wallboard(c,rowindex,CardinalDirection.South);
				     if (floorplan.canTearDown(curwallboard)) {
					    nextrow = mergeSouth(curwallboard,c,row,nextrow,newset,sets);}
			      }
			   }
			}
		}
		return nextrow;
	}
	
	/**
	 * This method merges the set of the current cell and with the set of the cell below it.
	 * @param curwallboard: the south wallboard of the current cell.
	 * @param c:the index of the column
	 * @param row:an integer array. The index refers to the column index of a cell and the value refers to the set that a cell belongs to.
	 * @param nextrow:an integer array that records the column index of a cell and the corresponding set in the next row.
	 * @param newset:an arraylist of integers.The container for the cells that belongs to a given set in the next row.
	 * @param sets:a collection of sets. A set records the the column indices of cells in it.
	 * @return the updated next row
	 */
	private int[] mergeSouth(Wallboard curwallboard,int c,int[]row,int[]nextrow,ArrayList<Integer>newset,ArrayList<ArrayList<Integer>>sets) {
	    //delete the wallboard between the selected cell and the cell down below
        //assign sets to the chosen cells in the next row
		floorplan.deleteWallboard(curwallboard);
        nextrow[c] = row[c];
        //rewrite the sets for the next row (note that after this step,
        //there are still cells unassigned to any set)
        if (newset.contains((Object)c) == false) {               	  
           newset.add(c);
           sets.set(row[c]-1, newset);}
        return nextrow;
	}

	/**
	 * This method merges cells in the last row that are not in the same set.
	 * @param row:an integer array. The index refers to the column index of a cell and the value refers to the set that a cell belongs to.
	 */
	private void finish(int[]row) {
		//merge every cell to the cell on its left
		for (int i=0;i<width-1;i++) {
			if (row[i]!=row[i+1]) {
			//delete the wallboard
			Wallboard curwallboard = new Wallboard(i,height-1,CardinalDirection.East);
			floorplan.deleteWallboard(curwallboard);}
		}
	}
      
}
