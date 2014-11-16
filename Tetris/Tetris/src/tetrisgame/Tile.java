package tetrisgame;

public class Tile {

	private int x = 0;
	private int y = 0;
	private boolean takenByBorder = false;
	private boolean takenBySquare = false;

	public Tile(int xInput, int yInput) {
		x = xInput * 40;
		y = yInput * 40;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isTakenByBorder() {
		return takenByBorder;
	}

	public void setTakenByBorder(boolean free) {
		this.takenByBorder = free;
	}

	public boolean isTakenBySquare() {
		return takenBySquare;
	}

	public void setTakenBySquare(boolean takenBySquare) {
		this.takenBySquare = takenBySquare;
	}

}
