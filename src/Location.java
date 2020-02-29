import java.io.Serializable;

public class Location implements Serializable {
	private int row;
	private int col;

	public Location(int col, int row) {
		this.row = row;
		this.col = col;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Location) {
			Location other = (Location) obj;
			return row == other.getRow() && col == other.getCol();
		} else {
			return false;
		}
	}

	public String toString() {
		return col + "," + row;
	}

	public int hashCode() {
		return (row << 16) + col;
	}

	public int getRow() {
		return row;
	}


	public int getCol() {
		return col;
	}

	public int gety() {
		return row;
	}

	
	public int getx() {
		return col;
	}
}
