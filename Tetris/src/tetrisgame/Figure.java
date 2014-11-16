package tetrisgame;

import java.util.ArrayList;
import java.util.Random;

public class Figure {

	// Create list to hold square objects
	public ArrayList<Square> movingSquares = new ArrayList<Square>();

	// Create array to hold tile objects
	Tile[][] tilemap = new Tile[15][22];

	private int speedX;
	private int speedY;
	private int middleSquareX;
	private int middleSquareY;
	private int count = 1; // Used in moveSquare() method to count how many times object was moved
	private int timePassed = 0; // How much time the figure has been moving across the tile

	final int tileWidth = 40; // Width and height of a tile or square
	final int tileUpdate = 10; // How much time passes with each update
	final int timeMovement = 400; // How much time it takes for a square to move one tile
	final int speedOfFigure = 4;

	// This is changed to true after user input
	public boolean moveLeft = false;
	public boolean moveRight = false;
	public boolean moveDown = false;
	public boolean rotate = false;
	public boolean keepRunning = true;

	private static String movementDirection = "default"; // Four direction: 1) default 2) left 3) rigth 4) down
	private static String movementPhase = "chooseDirection"; // Three phases: 1) chooseDirection 2) move 3) sleep

	public void update() {

		switch (movementPhase) {
		case "chooseDirection": // Phase one
			if (bottomTileIsFree() == false) { // If the tile to the bottom is taken
				// Ground the figure
				emptyMovingArray(); // Empty movingSquares list and make tiles taken by squares
				fillMovingObjectWithRandomFigure(); // Fill movingSquares list with new figure
				removeFilledRows(); // Remove filled rows and move remaining grounded squares downward

			} else { // If the tile to the bottom is free

				chooseDirectionToMove(); // Choose a direction to move
				setFigureSpeed(); // Set the x and y speed of the figure
				movementPhase = "move";
			}
			break;
		case "move": // Phase two
			moveFigure(); // Move the square in chosen direction
			break;
		case "sleep": // Phase three

			if (userIsPressingSomething()) { // If user is pressing something
				movementPhase = "chooseDirection";
			} else { // If user isn't pressing anything
				if (rotate) { // If user wants to rotate the figure
					if (canRotateFigure()) { // If it's possible to rotate the figure
						rotateFigure();
					}
				}
				timePassed += tileUpdate;
				if (timePassed >= timeMovement) { // If figure has been sleeping enough time
					movementPhase = "chooseDirection";
					timePassed = 0;
				}

			}
			break;
		}
	}

	private void rotateFigure() {
		for (int i = 1; i < movingSquares.size(); i++) {

			/*
			 * if (movingSquares.get(0).getX() != 0) { System.out.println("not zero"); // debugging }
			 */

			Square square = movingSquares.get(i);
			square.setX(square.getTargetRotateX());
			square.setY(square.getTargetRotateY());

			/*
			 * if (movingSquares.get(0).getX() == 0) { // debugging System.out.println(movementPhase);
			 * System.out.println("it's zero!"); keepRunning = false; }
			 */
		}
	}

	private boolean canRotateFigure() {
		boolean canRotate = true; // Allow rotating by default
		rotate = false;

		// Distance between the square's x and y and the middle square's x and y
		int differenceX = 0;
		int differenceY = 0;

		for (int i = 1; i < movingSquares.size(); i++) { // Go through each square of movingSquares list
			Square square = movingSquares.get(i);
			String rotationPhase = square.getRotationPhase();

			// Update the middle square's position
			middleSquareX = movingSquares.get(0).getX();
			middleSquareY = movingSquares.get(0).getY();

			// Distance between the square and middle square in tilemap coordinates
			differenceX = Math.abs(middleSquareX - square.getX());
			differenceY = Math.abs(middleSquareY - square.getY());

			// Swap values of xDiff and yDiff
			int temp;
			temp = differenceX;
			differenceX = differenceY;
			differenceY = temp;

			// Set tilemap coordinates of destination after rotation
			switch (rotationPhase) {
			case "One":
				square.setTargetRotateX(middleSquareX + differenceX);
				square.setTargetRotateY(middleSquareY + differenceY);
				square.setRotationPhase("Two");
				break;
			case "Two":
				square.setTargetRotateX(middleSquareX - differenceX);
				square.setTargetRotateY(middleSquareY + differenceY);
				square.setRotationPhase("Three");
				break;
			case "Three":
				square.setTargetRotateX(middleSquareX - differenceX);
				square.setTargetRotateY(middleSquareY - differenceY);
				square.setRotationPhase("Four");
				break;
			case "Four":
				square.setTargetRotateX(middleSquareX + differenceX);
				square.setTargetRotateY(middleSquareY - differenceY);
				square.setRotationPhase("One");
				break;
			}

			if (tilemap[square.getTargetRotateX() / tileWidth][square.getTargetRotateY() / tileWidth].isTakenBySquare()
					|| tilemap[square.getTargetRotateX() / tileWidth][square.getTargetRotateY() / tileWidth]
							.isTakenByBorder()) { // If the target tile is taken
				canRotate = false; // Prevent from rotating
				break; // Stop the for loop
			}

		}
		return canRotate;
	}

	private void removeFilledRows() {
		// Find next row that is filled with squares
		for (int rows = 0; rows < tilemap[0].length - 1; rows++) { // Go through each row except last, since it's a
																	// bottom border

			int count = 0;
			for (int columns = 1; columns < tilemap.length - 1; columns++) { // Go through each column except first and
																				// last, since they are borders

				if (tilemap[columns][rows].isTakenBySquare()) { // If the tile is taken
					count++;
				}

			}

			if (count == 13) { // If the row is filled
				count = 0;
				// Empty the row
				for (int columns = 1; columns < tilemap.length - 1; columns++) { // Go through each column except first
																					// and
																					// last, since they are borders
					tilemap[columns][rows].setTakenBySquare(false);
				}
				moveUpperRowsDown(rows);

			}
		}
		movementPhase = "chooseDirection";
	}

	// Move the upper rows down
	private void moveUpperRowsDown(int r) {
		int currentRow = r - 1;
		for (int rows = currentRow; rows >= 0; rows--) { // Go through each row up
			for (int columns = 1; columns < tilemap.length - 1; columns++) { // Go through each column except first and
																				// last, since they are borders
				if (tilemap[columns][rows].isTakenBySquare()) { // If the tile is taken by a square
					tilemap[columns][rows].setTakenBySquare(false); // Set that tile to not taken
					tilemap[columns][rows + 1].setTakenBySquare(true); // Set the tile to the bottom
																		// as
																		// taken
				}

			}
		}
	}

	private boolean userIsPressingSomething() {
		boolean result = false;
		if (moveLeft || moveRight || moveDown) {
			result = true;
		}
		return result;
	}

	private void chooseDirectionToMove() {

		// If tiles to both sides of all moving squares are free
		if (tilesToTheLeftAreFree() && tilesToTheRightAreFree()) {
			if (moveLeft) {
				movementDirection = "left";
			} else if (moveRight) {
				movementDirection = "right";
			} else if (moveDown) {
				movementDirection = "down";
			} else {
				movementDirection = "default";
			}
			// If tiles to the right of all moving squares are free
		} else if (tilesToTheRightAreFree()) {
			if (moveRight) {
				movementDirection = "right";
			} else if (moveDown) {
				movementDirection = "down";
			} else {
				movementDirection = "default";
			}
			// If tiles to the left of all moving squares are free
		} else if (tilesToTheLeftAreFree()) {
			if (moveLeft) {
				movementDirection = "left";
			} else if (moveDown) {
				movementDirection = "down";
			} else {
				movementDirection = "default";
			}
		}

		// Set x and y coordinates of next tile
		for (int i = 0; i < movingSquares.size(); i++) {
			Square square = movingSquares.get(i);
			int xCoordinate = square.getX() / tileWidth;
			int yCoordinate = square.getY() / tileWidth;
			switch (movementDirection) {
			case "left":
				square.setTargetMoveX(tilemap[xCoordinate - 1][yCoordinate].getX());
				square.setTargetMoveY(tilemap[xCoordinate - 1][yCoordinate].getY());
				break;
			case "right":
				square.setTargetMoveX(tilemap[xCoordinate + 1][yCoordinate].getX());
				square.setTargetMoveY(tilemap[xCoordinate + 1][yCoordinate].getY());
				break;
			case "down":
				square.setTargetMoveX(tilemap[xCoordinate][yCoordinate + 1].getX());
				square.setTargetMoveY(tilemap[xCoordinate][yCoordinate + 1].getY());
				break;
			case "default":
				square.setTargetMoveX(tilemap[xCoordinate][yCoordinate + 1].getX());
				square.setTargetMoveY(tilemap[xCoordinate][yCoordinate + 1].getY());
				break;
			}
		}
	}

	public void fillMovingObjectWithRandomFigure() {
		Random r = new Random();
		int figureNumber = r.nextInt(6);
		switch (figureNumber) {
		case 0:
			movingSquares.add(new Square(7 * tileWidth, 0 * tileWidth, "One"));
			movingSquares.add(new Square(7 * tileWidth, 1 * tileWidth, "One"));
			movingSquares.add(0, new Square(7 * tileWidth, 2 * tileWidth, "Middle"));
			movingSquares.add(new Square(7 * tileWidth, 3 * tileWidth, "Three"));
			break;
		case 1:
			movingSquares.add(new Square(6 * tileWidth, 0 * tileWidth, "Four"));
			movingSquares.add(new Square(7 * tileWidth, 0 * tileWidth, "One"));
			movingSquares.add(0, new Square(7 * tileWidth, 1 * tileWidth, "Middle"));
			movingSquares.add(new Square(8 * tileWidth, 1 * tileWidth, "Two"));
			break;
		case 2:
			movingSquares.add(new Square(7 * tileWidth, 0 * tileWidth, "One"));
			movingSquares.add(new Square(8 * tileWidth, 0 * tileWidth, "One"));
			movingSquares.add(new Square(6 * tileWidth, 1 * tileWidth, "Four"));
			movingSquares.add(0, new Square(7 * tileWidth, 1 * tileWidth, "Middle"));
			break;
		case 3:
			movingSquares.add(new Square(6 * tileWidth, 0 * tileWidth, "Four"));
			movingSquares.add(new Square(7 * tileWidth, 0 * tileWidth, "One"));
			movingSquares.add(0, new Square(7 * tileWidth, 1 * tileWidth, "Middle"));
			movingSquares.add(new Square(7 * tileWidth, 2 * tileWidth, "Three"));
			break;
		case 4:
			movingSquares.add(new Square(7 * tileWidth, 0 * tileWidth, "One"));
			movingSquares.add(new Square(8 * tileWidth, 0 * tileWidth, "One"));
			movingSquares.add(0, new Square(7 * tileWidth, 1 * tileWidth, "Middle"));
			movingSquares.add(new Square(7 * tileWidth, 2 * tileWidth, "Three"));
			break;
		case 5:
			movingSquares.add(new Square(7 * tileWidth, 0 * tileWidth, "One"));
			movingSquares.add(new Square(6 * tileWidth, 1 * tileWidth, "Four"));
			movingSquares.add(0, new Square(7 * tileWidth, 1 * tileWidth, "Middle"));
			movingSquares.add(new Square(8 * tileWidth, 1 * tileWidth, "Two"));
			break;
		}

	}

	private void emptyMovingArray() {
		for (int i = movingSquares.size() - 1; i >= 0; i--) {
			Square square = movingSquares.get(i);

			// Make the square's tile taken
			tilemap[square.getX() / tileWidth][square.getY() / tileWidth].setTakenBySquare(true);

			// Remove the square from movingSquares
			movingSquares.remove(i);
		}

	}

	private void moveFigure() {
		int distance = tileWidth;
		int speed = 0;

		// Determine which value changes x or y and assign speed to it
		if (speedX > 0) {
			speed = speedX;
		} else if (speedX < 0) {
			speed = -speedX;
		} else {
			speed = speedY;
		}

		if (distance - count * speed < 0) { // If the figure finished moving across the tile
			movementPhase = "sleep";
			count = 1;

			// Set figure at next position
			for (int i = 0; i < movingSquares.size(); i++) {
				Square square = movingSquares.get(i);
				square.setX(square.getTargetMoveX());
				square.setY(square.getTargetMoveY());
			}

		} else { // If the figure is still moving across the tile

			count++;

			// Move the square
			for (int i = 0; i < movingSquares.size(); i++) {
				Square square = movingSquares.get(i);

				int x = square.getX() + speedX;
				int y = square.getY() + speedY;
				square.setX(x);
				square.setY(y);
			}
		}

	}

	private void setFigureSpeed() {
		switch (movementDirection) {
		case "left":
			speedX = -speedOfFigure;
			speedY = 0;
			break;
		case "right":
			speedX = speedOfFigure;
			speedY = 0;
			break;
		case "down":
			speedX = 0;
			speedY = speedOfFigure * 4;
			break;
		case "default":
			speedX = 0;
			speedY = speedOfFigure;
			break;
		}

	}

	private boolean bottomTileIsFree() {

		boolean result = true;
		int xCoordinate;
		int yCoordinate;

		for (int i = 0; i < movingSquares.size(); i++) {
			Square square = movingSquares.get(i);

			xCoordinate = square.getX() / tileWidth;
			yCoordinate = square.getY() / tileWidth;
			if (tilemap[xCoordinate][yCoordinate + 1].isTakenByBorder() == true
					|| tilemap[xCoordinate][yCoordinate + 1].isTakenBySquare() == true) { // If the tile to the bottom
																							// is taken

				result = false;
			}

		}
		return result;
	}

	private boolean tilesToTheRightAreFree() {
		boolean result = true;
		int xCoordinate;
		int yCoordinate;
		for (int i = 0; i < movingSquares.size(); i++) {
			Square square = movingSquares.get(i);
			xCoordinate = square.getX() / tileWidth;
			yCoordinate = square.getY() / tileWidth;

			if (tilemap[xCoordinate + 1][yCoordinate].isTakenByBorder() == true
					|| tilemap[xCoordinate + 1][yCoordinate].isTakenBySquare() == true) { // If the tile to the right is
																							// taken by square or border
				result = false;
			}
		}
		return result;
	}

	private boolean tilesToTheLeftAreFree() {
		boolean result = true;
		int xCoordinate;
		int yCoordinate;
		for (int i = 0; i < movingSquares.size(); i++) {
			xCoordinate = movingSquares.get(i).getX() / tileWidth;
			yCoordinate = movingSquares.get(i).getY() / tileWidth;

			if (tilemap[xCoordinate - 1][yCoordinate].isTakenByBorder() == true
					|| tilemap[xCoordinate - 1][yCoordinate].isTakenBySquare() == true) { // If the tile to the
																							// left is taken
				result = false;
			}
		}
		return result;
	}

	public static String getPhase() {
		return movementPhase;
	}

	public void setUpTilemap() {
		// Fill tilemap array
		for (int columns = 0; columns < tilemap.length; columns++) {
			for (int rows = 0; rows < tilemap[0].length; rows++) {
				tilemap[columns][rows] = new Tile(columns, rows);
			}
		}

		// Define tilemap borders
		for (int rows = 0; rows < tilemap[0].length; rows++) {
			tilemap[0][rows].setTakenByBorder(true);
			tilemap[14][rows].setTakenByBorder(true);
		}
		for (int columns = 0; columns < tilemap.length; columns++) {
			tilemap[columns][21].setTakenByBorder(true);
		}
	}

}
