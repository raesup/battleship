package BattleshipGame;

import java.io.Serializable;

/**
 * BoatPosition gets variable for the rows and columns and return them when they are needed.
 *
 * @author Raesup Kim
 * @version Oct.4 2020
 */
public class BoatPosition implements Serializable {
	
	private static final long serialVersionUID = 4L;

	public int[][] boatPosition = new int[10][10];
	public int[][] boatPositionTarget = new int[10][10]; ;
	int rows;
	int columns;
	
	/**
	 * BoatPosition - Default constructor.
	 */
	public BoatPosition ()
	{
		
	}
	
	/**
	 * BoatPosition - User defined constructor.
	 * 
	 * @param rows rows of the grid button
	 * @param columns columns of the grid button
	 */
	public BoatPosition (int rows, int columns)
	{
		this.rows = rows;
		this.columns = columns;
	}

	/**
	 * @return the boatPosition
	 */
	public int[][] getBoatPosition() {
		return boatPosition;
	}

	/**
	 * @param boatPosition the 2 dimension array for the grid button
	 */
	public void setBoatPosition(int[][] boatPosition) {
		this.boatPosition = boatPosition;
	}
	
	/**
	 * @return the boatPositionTarget
	 */
	public int[][] getBoatPositionTarget() {
		return boatPositionTarget;
	}

	/**
	 * @param boatPositionTarget the 2 dimension array for the grid button
	 */
	public void setBoatPositionTarget(int[][] boatPositionTarget) {
		this.boatPositionTarget = boatPositionTarget;
	}

	/**
	 * @return the rows.
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @param rows rows of the grid button
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @return the columns.
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * @param columns columns of the grid button
	 */
	public void setColumns(int columns) {
		this.columns = columns;
	}
}
