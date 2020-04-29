package edu.wm.cs.cs301.JunzhaoSun.generation;


import android.os.Handler;
import android.widget.ProgressBar;

/**
 * An order describes functionality needed to order a maze from
 * the maze factory. It allows for asynchronous production 
 * with a mechanism to deliver a MazeConfiguration.
 package edu.wm.cs.cs301.JunzhaoSun.generation;


 import android.os.Handler;
 import android.widget.ProgressBar;

 /**
 * An order describes functionality needed to order a maze from
 * the maze factory. It allows for asynchronous production
 * with a mechanism to deliver a MazeConfiguration.
 *
 * @author Peter Kemper
 *
 */
public interface Order {
	/**
	 * Gives the required skill level, range of values 0,1,2,...,15
	 */
	int getSkillLevel() ;
	/**
	 * Gives the requested builder algorithm, possible values
	 * are listed in the Builder enum type.
	 */
	Builder getBuilder() ;
	/**
	 * Lists all maze generation algorithms that are supported
	 * by the maze factory (Eller needs to be implemented for P2)
	 *
	 */
	enum Builder { DFS, Prim, Kruskal, Eller } ;
	/**
	 * Describes if the ordered maze should be perfect, i.e. there are
	 * no loops and no isolated areas, which also implies that
	 * there are no rooms as rooms can imply loops
	 */
	boolean isPerfect() ;
	/**
	 * Delivers the produced maze.
	 * This method is called by the factory to provide the
	 * resulting maze as a MazeConfiguration.
	 * @param
	 */
	void deliver(Maze mazeConfig) ;

	/**
	 * provide the worker thread a reference to the handler for it to post progress
	 * @return
	 */
	Handler getHandler();

	/**
	 * provide a reference of the progress bar object to the worker thread for it to
	 * report progress on it
	 * @return
	 */
	ProgressBar getProgressBar();

	/**
	 * inform the worker the seed we want to use for maze generation
	 * @return
	 */
	int getSeed();


}
