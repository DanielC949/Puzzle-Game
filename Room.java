import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Room extends JLayeredPane {

	private static final int WIDTH = 1080, HEIGHT = 660;
	private static final int MILLIS_DELAY = 2;

	private Puzzle p;
	private boolean puzzleShown;
	private int squareI, squareJ;
	private JLabel square;
	private Maze m;
	private int id;
	private final Timer W = new Timer(MILLIS_DELAY, e -> {
		if (puzzleShown) return;
		if (squareI > 0) square.setLocation(squareJ, --squareI);
		else m.changeRoom(0);
		repaint();
	});
	private final Timer A = new Timer(MILLIS_DELAY, e -> {
		if (puzzleShown) return;
		if (squareJ > 0) square.setLocation(--squareJ, squareI);
		else m.changeRoom(1);
		repaint();
	});
	private final Timer S = new Timer(MILLIS_DELAY, e -> {
		if (puzzleShown) return;
		if (squareI < HEIGHT - 5) square.setLocation(squareJ, ++squareI);
		else m.changeRoom(2);
		repaint();
	});
	private final Timer D = new Timer(MILLIS_DELAY, e -> {
		if (puzzleShown) return;
		if (squareJ < WIDTH - 5) square.setLocation(++squareJ, squareI);
		else m.changeRoom(3);
		repaint();
	});

	public Room(Maze parent, int id, Puzzle puzzle) {
		p = puzzle;
		m = parent;
		this.id = id;
		this.setPreferredSize(new Dimension(1080, 660));
		this.setFocusable(true);
		this.requestFocusInWindow();
		if (puzzle != null) {
			this.add(puzzle.start(this), Integer.valueOf(2));
			puzzleShown = true;
		}
		square = new JLabel();
		square.setBounds(535, 325, 10, 10);
		squareI = 325;
		squareJ = 535;
		square.setOpaque(true);
		square.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
		this.add(square, Integer.valueOf(1));
		NGonCanvas background = new NGonCanvas(id + 3);
		background.setOpaque(true);
		background.setBounds(0, 0, 1080, 660);
		this.add(background, Integer.valueOf(-1));
	}
	public void puzzleWin() {
		if (this instanceof Monument) ((Monument) this).cheat();
		if (p == null) return;
		m.setSquareColor(p.getColor());
		updateSquare();
		puzzleShown = false;
		for (Component c : getComponents())
			if (c instanceof PuzzleContainer) remove(c);
		this.revalidate();
		this.repaint();
		this.requestFocusInWindow();
	}
	public void enter() {
		square.setBackground(m.getSquareColor());
		square.setLocation(535, 325);
		squareI = 325;
		squareJ = 535;
		if (this.p != null && !m.getSquareColor().equals(p.getColor()) && !m.getMonument().completed(p.getClass())) {
			// Move along, nothing horrendous to see here...
			try {
				this.add(p.getClass().getConstructor().newInstance().start(this), Integer.valueOf(2));
			} catch (Exception e) {
				e.printStackTrace();
			}
			puzzleShown = true;
		}
		this.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				requestFocusInWindow();
			}
		});
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_W)
					W.start();
				else if (e.getKeyCode() == KeyEvent.VK_A)
					A.start();
				else if (e.getKeyCode() == KeyEvent.VK_S)
					S.start();
				else if (e.getKeyCode() == KeyEvent.VK_D)
					D.start();
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					p.close();
					for (Component c : getComponents())
						if (c instanceof PuzzleContainer) remove(c);
					revalidate();
					repaint();
					puzzleShown = false;
				} else if (e.getKeyCode() == KeyEvent.VK_E)
					m.teleport();
				else if (e.getKeyCode() == KeyEvent.VK_Q)
					puzzleWin();
			}
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_W)
					W.stop();
				else if (e.getKeyCode() == KeyEvent.VK_A)
					A.stop();
				else if (e.getKeyCode() == KeyEvent.VK_S)
					S.stop();
				else if (e.getKeyCode() == KeyEvent.VK_D)
					D.stop();
			}
		});
		this.repaint();
	}
	public void leave() {
		this.stopTimers();
		for (FocusListener f : this.getFocusListeners())
			this.removeFocusListener(f);
		for (KeyListener k : this.getKeyListeners())
			this.removeKeyListener(k);
	}
	public int getRoomNum() {
		return id;
	}
	public void stopTimers() {
		W.stop();
		A.stop();
		S.stop();
		D.stop();
	}
	public boolean[] getPressedKeys() {
		return new boolean[] {W.isRunning(), A.isRunning(), S.isRunning(), D.isRunning()};
	}
	public void initKeys(boolean[] keys) {
		if (keys[0]) W.start();
		if (keys[1]) A.start();
		if (keys[2]) S.start();
		if (keys[3]) D.start();
	}
	public Maze getMaze() {return m;}
	public void updateSquare() {
		square.setBackground(m.getSquareColor());
		square.repaint();
	}

	public void add(Component c, Object constraints) {
		super.add(c, constraints);
		if (c instanceof PuzzleContainer) c.setLocation(290, 80);
	}
}
