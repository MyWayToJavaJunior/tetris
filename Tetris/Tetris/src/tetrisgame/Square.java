package tetrisgame;

public class Square {

	private int x = 0;
	private int y = 0;
	private String rotationPhase; // Five phases: One, Two, Three, Four, Middle
	private int targetRotateX = 0;
	private int targetRotateY = 0;
	private int targetMoveX = 0;
	private int targetMoveY = 0;

	public Square(int xInput, int yInput, String p) {
		x = xInput;
		y = yInput;
		setRotationPhase(p);
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

	public int getTargetRotateX() {
		return targetRotateX;
	}

	public void setTargetRotateX(int targetRotateX) {
		this.targetRotateX = targetRotateX;
	}

	public int getTargetRotateY() {
		return targetRotateY;
	}

	public void setTargetRotateY(int targetRotateY) {
		this.targetRotateY = targetRotateY;
	}

	public int getTargetMoveX() {
		return targetMoveX;
	}

	public void setTargetMoveX(int targetMoveX) {
		this.targetMoveX = targetMoveX;
	}

	public int getTargetMoveY() {
		return targetMoveY;
	}

	public void setTargetMoveY(int targetMoveY) {
		this.targetMoveY = targetMoveY;
	}

	public String getRotationPhase() {
		return rotationPhase;
	}

	public void setRotationPhase(String rotationPhase) {
		this.rotationPhase = rotationPhase;
	}

}