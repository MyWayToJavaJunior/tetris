package tetrisgame;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

public class StartingClass extends Applet implements Runnable, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4020925098330389744L;

	private Figure figure = new Figure();
	private Tile[][] tilemap = figure.tilemap;

	private Image image, square;
	private Graphics second;
	private URL base;

	final int tileWidth = figure.tileWidth;

	@Override
	public void init() {

		setSize(600, 860);
		setFocusable(true);
		addKeyListener(this);
		// Frame frame = (Frame) this.getParent().getParent();
		// frame.setTitle("Tetris");
		try {
			base = getDocumentBase();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Image Setups
		square = getImage(base, "data/Square.png");

		// Fill array tilemap
		figure.setUpTilemap();

		// Fill movingSquares list with a random figure
		figure.fillMovingObjectWithRandomFigure();
	}

	@Override
	public void start() {

		Thread thread = new Thread(this);
		thread.start();

	}

	@Override
	public void run() {

		while (figure.keepRunning) {

			// Update the moving figure
			figure.update();

			repaint();
			try {
				Thread.sleep(17);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		super.stop();
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void update(Graphics g) {
		if (image == null) {
			image = createImage(this.getWidth(), this.getHeight());
			second = image.getGraphics();
		}

		second.setColor(getBackground());
		second.fillRect(0, 0, getWidth(), getHeight());
		second.setColor(getForeground());
		paint(second);

		g.drawImage(image, 0, 0, this);

	}

	@Override
	public void paint(Graphics g) {

		// Paint tilemap for debugging

		for (int i = 0; i < tilemap.length; i++) {
			for (int j = 0; j < tilemap[0].length; j++) {
				Tile tile = figure.tilemap[i][j];
				g.drawRect(tile.getX(), tile.getY(), tileWidth, tileWidth);
			}
		}

		// Paint tiles taken by squares
		for (int columns = 0; columns < tilemap.length; columns++) {
			for (int rows = 0; rows < tilemap[0].length; rows++) {
				Tile tile = figure.tilemap[columns][rows];
				if (tile.isTakenBySquare()) { // If the tile is taken by a square
					g.drawImage(square, tile.getX(), tile.getY(), this);
				}
			}
		}

		// // Paint tiles taken by borders
		// for (int columns = 0; columns < tilemap.length; columns++) {
		// for (int rows = 0; rows < tilemap[0].length; rows++) {
		// Tile tile = figure.tilemap[columns][rows];
		// if (tile.isTakenByBorder()) { // If the tile is taken by a square
		// g.setColor(Color.ORANGE);
		// g.fillRect(tile.getX(), tile.getY(), tileWidth, tileWidth);
		// }
		// }
		// }

		// Paint the border
		g.drawRect(40, 0, 520, 840);

		// Paint movingSquares
		for (int i = 0; i < figure.movingSquares.size(); i++) {
			g.drawImage(square, figure.movingSquares.get(i).getX(), figure.movingSquares.get(i).getY(), this);
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			figure.moveLeft = true;
			break;

		case KeyEvent.VK_RIGHT:
			figure.moveRight = true;
			break;

		case KeyEvent.VK_DOWN:
			figure.moveDown = true;
			break;
		case KeyEvent.VK_SPACE:
			if (Figure.getPhase() == "sleep") {
				figure.rotate = true;
			}
			break;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			figure.moveLeft = false;
			break;

		case KeyEvent.VK_RIGHT:
			figure.moveRight = false;
			break;

		case KeyEvent.VK_DOWN:
			figure.moveDown = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
