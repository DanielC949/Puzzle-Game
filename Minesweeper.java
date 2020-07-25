import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Minesweeper implements Puzzle {

	private static final int SIZE = 16, NUM_MINES = 40;
	public static final Color COLOR = Color.YELLOW;

	private MinesweeperButton[][] grid;
	private PuzzleContainer container;
	private Room root;
	private JPanel field;
	private ColoredPanel overlay;
	private int openCells;

	public Minesweeper() {
		grid = new MinesweeperButton[SIZE][SIZE];
		container = new PuzzleContainer();
		container.setBounds(0, 0, 500, 500);
		container.setPreferredSize(new Dimension(500, 500));

		field = new JPanel(new GridBagLayout());
		field.setBounds(0, 0, container.getWidth(), container.getHeight());
		container.add(field, Integer.valueOf(0));

		overlay = new ColoredPanel();
		overlay.setBounds(0, 0, container.getWidth(), container.getHeight());
		overlay.setColor(new Color(0, 0, 0, 160));
		overlay.setVisible(false);
		container.add(overlay, Integer.valueOf(1));

		openCells = Lib.square(SIZE) - NUM_MINES;
	}

	public JComponent start(Room parent) {
		root = parent;
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				MinesweeperButton b = new MinesweeperButton(this, i, j);
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = i;
				c.gridy = j;
				field.add(b, c);
				grid[i][j] = b;
			}
		}
		this.seed(RNG.randInt(0, SIZE - 1), RNG.randInt(0, SIZE - 1));
		return container;
	}
	private void seed(int x, int y) {
		for (int k = 0; k < NUM_MINES; k++) {
			int i, j;
			do {
				i = RNG.randInt(0, SIZE - 1);
				j = RNG.randInt(0, SIZE - 1);
			} while (neighbors(i, j, x, y) || this.getButton(i, j).isBomb);
			this.getButton(i, j).setBomb();
		}

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				this.getButton(i, j).checkMines();
			}
		}

		this.getButton(x, y).press();
	}
	private void checkWin() {
		if (openCells <= 0)
			win();
	}
	private void win() {
		overlay.setVisible(true);
		overlay.setLayout(null);

		JButton youWon = new JButton("You won!");
		youWon.setBackground(Color.GRAY);
		youWon.setOpaque(true);
		youWon.setForeground(Color.GREEN);
		youWon.setFont(new Font("TimesRoman", Font.BOLD, 32));
		youWon.setBounds(overlay.getWidth() / 2 - 200 / 2, overlay.getHeight() / 2 - 50 / 2, 200, 50);
		youWon.addActionListener(e -> {
			root.puzzleWin();
			root.remove(this.container);
			root.validate();
			root.repaint();
		});

		overlay.add(youWon);
		overlay.repaint();

		container.validate();
	}
	private void lose() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				this.getButton(i, j).deactivate();
			}
		}

		overlay.setVisible(true);
		overlay.setLayout(null);

		JButton youLost = new JButton("You lost. Retry?");
		youLost.setBackground(Color.GRAY);
		youLost.setOpaque(true);
		youLost.setForeground(Color.RED);
		youLost.setFont(new Font("TimesRoman", Font.BOLD, 32));
		youLost.setBounds(overlay.getWidth() / 2 - 300 / 2, overlay.getHeight() / 2 - 50 / 2, 300, 50);
		youLost.addActionListener(e -> {
			root.remove(this.container);
			root.add(new Minesweeper().start(root), Integer.valueOf(2));
		});
		overlay.add(youLost);
		overlay.repaint();

		container.validate();
		container.repaint();
	}
	public void close() {
		root.remove(this.container);
		root.validate();
		root.repaint();
	}
	private void markCellCleared() {
		openCells--;
	}
	private JPanel getField() {
		return field;
	}
	private MinesweeperButton getButton(int i, int j) {
		return grid[i][j];
	}

	private static boolean neighbors(int a, int b, int x, int y) {
		return Math.abs(x - a) <= 1 && Math.abs(y - b) <= 1;
	}

	public Color getColor() {
		return COLOR;
	}

	private static class MinesweeperButton extends JButton {
		public static final int SIZE = 30;
		private static final Color[] COLORS = new Color[] {Color.BLACK, Color.MAGENTA, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED, Color.WHITE};

		private int x, y;
		private int state, num;
		private boolean isBomb;
		private Minesweeper instance;

		public MinesweeperButton(Minesweeper m, int x, int y) {
			this.x = x;
			this.y = y;
			state = 0;
			instance = m;

			this.setOpaque(true);
			this.setBackground(Color.LIGHT_GRAY);
			this.setPreferredSize(new Dimension(SIZE, SIZE));
			this.setFont(new Font("TimesRoman", Font.BOLD, 25));
			this.setMargin(new Insets(0, 0, 0, 0));
			this.setFocusPainted(false);
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						if (Math.random() < 0.3) press();
						else flag();
					} else if (SwingUtilities.isLeftMouseButton(e)) {
						press();
						instance.checkWin();
					}
				}
			});
		}

		public void press() {
			if (this.state == -1 || this.state == 1) return;
			if (this.isBomb) {
				this.setBackground(Color.GRAY);
				this.setForeground(Color.BLACK);
				this.setText("\u2738");
				instance.lose();
				this.instance.getField().repaint();
				return;
			}

			this.state = -1;
			this.instance.markCellCleared();

			this.setBackground(Color.GRAY);
			if (this.num > 0) {
				this.setText(num + "");
				this.setForeground(COLORS[num - 1]);
			}

			this.instance.getField().repaint();

			if (this.num > 0) return;

			if (this.x > 0 && !instance.getButton(this.x - 1, this.y).isBomb)
				instance.getButton(this.x - 1, this.y).press();
			if (this.x < Minesweeper.SIZE - 1 && !instance.getButton(this.x + 1, this.y).isBomb)
				instance.getButton(this.x + 1, this.y).press();
			if (this.y > 0 && !instance.getButton(this.x, this.y - 1).isBomb)
				instance.getButton(this.x, this.y - 1).press();
			if (this.y < Minesweeper.SIZE - 1 && !instance.getButton(this.x, this.y + 1).isBomb)
				instance.getButton(this.x, this.y + 1).press();

			if (this.x > 0 && this.y > 0 && !instance.getButton(this.x - 1, this.y - 1).isBomb)
				instance.getButton(this.x - 1, this.y - 1).press();
			if (this.x > 0 && this.y < Minesweeper.SIZE - 1 && !instance.getButton(this.x - 1, this.y + 1).isBomb)
				instance.getButton(this.x - 1, this.y + 1).press();
			if (this.x < Minesweeper.SIZE - 1 && this.y > 0 && !instance.getButton(this.x + 1, this.y - 1).isBomb)
				instance.getButton(this.x + 1, this.y - 1).press();
			if (this.x < Minesweeper.SIZE - 1 && this.y < Minesweeper.SIZE - 1 && !instance.getButton(this.x + 1, this.y + 1).isBomb)
				instance.getButton(this.x + 1, this.y + 1).press();
		}
		public void checkMines() {
			if (this.x > 0) {
				if (this.y > 0 && instance.getButton(this.x - 1, this.y - 1).isBomb)
					this.num++;
				if (this.y < Minesweeper.SIZE - 1 && instance.getButton(this.x - 1, this.y + 1).isBomb)
					this.num++;
				if (instance.getButton(this.x - 1, this.y).isBomb)
					this.num++;
			}

			if (this.y > 0 && instance.getButton(this.x, this.y - 1).isBomb)
				this.num++;
			if (this.y < Minesweeper.SIZE - 1 && instance.getButton(this.x, this.y + 1).isBomb)
				this.num++;

			if (this.x < Minesweeper.SIZE - 1) {
				if (this.y > 0 && instance.getButton(this.x + 1, this.y - 1).isBomb)
					this.num++;
				if (this.y < Minesweeper.SIZE - 1 && instance.getButton(this.x + 1, this.y + 1).isBomb)
					this.num++;
				if (instance.getButton(this.x + 1, this.y).isBomb)
					this.num++;
			}
		}
		public void flag() {
			if (this.state == -1) return;
			if (this.state == 0) {
				this.state = 1;
				this.setForeground(Color.RED);
				this.setFont(new Font("TimesRoman", Font.BOLD, 22));
				this.setText("\uD83D\uDEA9");
			} else {
				this.state = 0;
				this.setFont(new Font("TimesRoman", Font.BOLD, 25));
				this.setText("");
			}
			this.repaint();
		}
		public void deactivate() {
			for (MouseListener m : this.getMouseListeners()) {
				this.removeMouseListener(m);
			}
		}
		public void setBomb() {
			isBomb = true;
		}
	}
}
