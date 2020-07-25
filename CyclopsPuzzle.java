import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CyclopsPuzzle implements Puzzle {

	public static final int WIDTH = 7, HEIGHT = 5;
	public static final Color COLOR = Color.MAGENTA;

	private PuzzleContainer container;
	private Room root;
	private JPanel puzzle;
	private ColoredPanel overlay;
	private CyclopsButton[][] grid;

	public CyclopsPuzzle() {
		grid = new CyclopsButton[5][7];
		container = new PuzzleContainer();
		container.setBounds(0, 0, 500, 500);
		container.setPreferredSize(new Dimension(500, 500));
		puzzle = new JPanel(new GridBagLayout());
		puzzle.setBounds(0, 0, 500, 500);
		container.add(puzzle, Integer.valueOf(0));

		JLabel reset = new JLabel("Reset", SwingConstants.CENTER);
		reset.setBounds(220, 430, 60, 40);
		reset.setBackground(Color.LIGHT_GRAY);
		reset.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		reset.setFont(new Font("TimesRoman", Font.BOLD, 16));
		reset.setOpaque(true);
		reset.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				root.remove(container);
				root.add(new CyclopsPuzzle().start(root), Integer.valueOf(2));
			}
		});
		container.add(reset, Integer.valueOf(1));

		overlay = new ColoredPanel();
		overlay.setColor(new Color(0, 0, 0, 160));
		overlay.setBounds(0, 0, 500, 500);
		overlay.setVisible(false);
		container.add(overlay, Integer.valueOf(2));

		container.validate();
	}
	public JComponent start(Room parent) {
		root = parent;
		int[][] template = new int[][] {
				{0, 1, 1, 2, 3, 4, 5},
				{6, 7, 6, 0, 4, 1, 2},
				{6, 6, 6, 8, 9, 1, 6},
				{6, 3, 3, 6, 4, 2, 3},
				{9, 2, 2, 10, 0, 0, 6}
		};
		for (int i = 0; i < template.length; i++) {
			for (int j = 0; j < template[i].length; j++) {
				GridBagConstraints c = new GridBagConstraints();
				c.gridx= j;
				c.gridy = i;
				if (template[i][j] == 0) grid[i][j] = new ShowRight(i, j, this);
				else if (template[i][j] == 1) grid[i][j] = new Generator(i, j, this);
				else if (template[i][j] == 2) grid[i][j] = new ShowLeft(i, j, this);
				else if (template[i][j] == 3) grid[i][j] = new Blank(i, j, this);
				else if (template[i][j] == 4) grid[i][j] = new ShowBelow(i, j, this);
				else if (template[i][j] == 5) grid[i][j] = new Goal(i, j, this);
				else if (template[i][j] == 6) grid[i][j] = new ShowAbove(i, j, this);
				else if (template[i][j] == 7) grid[i][j] = new ShiftColToLeft(i, j, this);
				else if (template[i][j] == 8) grid[i][j] = new ShowAll(i, j, this);
				else if (template[i][j] == 9) grid[i][j] = new ShiftRowBelow(i, j, this);
				else if (template[i][j] == 10) grid[i][j] = new ShowLeftRight(i, j, this);
				puzzle.add(grid[i][j], c);
			}
		}

		return container;
	}
	private void shiftRow(int r) {
		if (r >= HEIGHT || r < 0) return;
		CyclopsButton rightmost = grid[r][WIDTH - 1];
		for (int j = WIDTH - 1; j > 0; j--) {
			CyclopsButton b = grid[r][j - 1];
			grid[r][j] = b;
			b.setLoc(r, j);
			replace(r, j, b);
		}
		grid[r][0] = rightmost;
		rightmost.setLoc(r, 0);
		replace(r, 0, rightmost);
		puzzle.repaint();
	}
	private void shiftCol(int c) {
		if (c >= HEIGHT || c < 0) return;
		CyclopsButton topmost = grid[0][c];
		for (int i = 0; i < HEIGHT - 1; i++) {
			CyclopsButton b = grid[i + 1][c];
			grid[i][c] = b;
			b.setLoc(i, c);
			replace(i, c, b);
		}
		grid[HEIGHT - 1][c] = topmost;
		topmost.setLoc(HEIGHT - 1, c);
		replace(HEIGHT - 1, c, topmost);
		puzzle.repaint();
	}
	private void replace(int i, int j, CyclopsButton b) {
		puzzle.remove(grid[i][j]);
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = i;
		c.gridx = j;
		puzzle.add(b, c);
	}
	private void win() {
		overlay.setVisible(true);
		overlay.setLayout(null);

		JButton youWon = new JButton("You won!");
		youWon.setForeground(Color.GREEN);
		youWon.setBackground(Color.GRAY);
		youWon.setOpaque(true);
		youWon.setFont(new Font("TimesRoman", Font.BOLD, 32));
		youWon.setBounds(overlay.getWidth() / 2 - 200 / 2, overlay.getHeight() / 2 - 50 / 2, 200, 50);
		youWon.addActionListener(e -> {
			root.puzzleWin();
			root.remove(container);
			root.validate();
			root.repaint();
		});
		overlay.add(youWon);
	}
	public void close() {
		root.remove(this.container);
		root.revalidate();
		root.repaint();
	}
	private CyclopsButton getButton(int i, int j) {
		return grid[i][j];
	}
	private JLayeredPane getContainer() {
		return container;
	}

	public Color getColor() {
		return COLOR;
	}

	private static class CyclopsButton extends JLabel {
		private int i, j, charges;
		private String icon;
		private boolean shown;
		private CyclopsPuzzle instance;
		public CyclopsButton(int i, int j, String text, CyclopsPuzzle parent) {
			super("", SwingConstants.CENTER);
			this.setVerticalTextPosition(SwingConstants.CENTER);
			this.i = i;
			this.j = j;
			icon = text;
			this.shown = false;
			instance = parent;

			this.setPreferredSize(new Dimension(60, 70));
			this.setBackground(instance.getContainer().getBackground());
			this.setForeground(Color.BLACK);
			this.setOpaque(true);
			this.setFont(new Font("TimesRoman", Font.BOLD, 16));
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (!shown || charges <= 0) return;
					useCharge();
					press();
					update();
				}
			});

		}
		public void setLoc(int newI, int newJ) {
			i = newI;
			j = newJ;
		}
		public void press() {}
		public void update() {
			if (!shown) return;
			String label = "<html><center>";
			for (int i = 0; i < charges; i++) {
				label += "#";
			}
			label += "<br>" + icon + "</center></html>";
			this.setText(label);
			this.repaint();
		}
		public int getI() {
			return i;
		}
		public int getJ() {
			return j;
		}
		public boolean isShown() {
			return shown;
		}
		public void reveal() {
			this.shown = true;
			this.setBackground(Color.LIGHT_GRAY);
			this.setText(icon);
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			this.update();
			this.repaint();
		}
		public void addCharge() {
			if (charges == 5) return;
			charges++;
		}
		public void useCharge() {
			if (charges == 0) return;
			charges--;
		}
		public CyclopsPuzzle getInstance() {
			return instance;
		}
	}
	private static class ShowAbove extends CyclopsButton {
		public ShowAbove(int i, int j, CyclopsPuzzle parent) {
			super(i, j, "T", parent);
			this.addCharge();
			this.update();
		}
		public void press() {
			if (getI() > 0) getInstance().getButton(getI() - 1, getJ()).reveal();
		}
	}
	private static class ShowLeft extends CyclopsButton {
		public ShowLeft(int i, int j, CyclopsPuzzle parent) {
			super(i, j, "&lt", parent);
			this.addCharge();
			this.update();
		}
		public void press() {
			if (getJ() > 0) getInstance().getButton(getI(), getJ() - 1).reveal();
		}
	}
	private static class ShowBelow extends CyclopsButton {
		public ShowBelow(int i, int j, CyclopsPuzzle parent) {
			super(i, j, "V", parent);
			this.addCharge();
			this.update();
		}
		public void press() {
			if (getI() < CyclopsPuzzle.HEIGHT - 1) getInstance().getButton(getI() + 1, getJ()).reveal();
		}
	}
	private static class ShowRight extends CyclopsButton {
		public ShowRight(int i, int j, CyclopsPuzzle parent) {
			super(i, j, "&gt", parent);
			this.addCharge();
			this.update();
		}
		public void press() {
			if (getJ() < CyclopsPuzzle.WIDTH - 1) getInstance().getButton(getI(), getJ() + 1).reveal();
		}
	}
	private static class ShowLeftRight extends CyclopsButton {
		public ShowLeftRight(int i, int j, CyclopsPuzzle parent) {
			super(i, j, "&gt<br>&lt", parent);
			this.addCharge();
			this.update();
		}
		public void press() {
			if (getJ() < CyclopsPuzzle.WIDTH - 1) getInstance().getButton(getI() - 1, getJ()).reveal();
			else if (getJ() > 0) getInstance().getButton(getI(), getJ() - 1).reveal();
		}
	}
	private static class ShowAll extends CyclopsButton {
		public ShowAll(int i, int j, CyclopsPuzzle parent) {
			super(i, j, "T&nbsp &gt<br>V&nbsp &lt", parent);
			this.reveal();
			this.addCharge();
			this.addCharge();
			this.addCharge();
			this.update();
		}
		public void press() {
			if (getI() > 0 && !getInstance().getButton(getI() - 1, getJ()).isShown()) getInstance().getButton(getI() - 1, getJ()).reveal();
			else if (getJ() < CyclopsPuzzle.WIDTH - 1 && !getInstance().getButton(getI(), getJ() + 1).isShown()) getInstance().getButton(getI(), getJ() + 1).reveal();
			else if (getI() < CyclopsPuzzle.HEIGHT - 1 && !getInstance().getButton(getI() + 1, getJ()).isShown()) getInstance().getButton(getI() + 1, getJ()).reveal();
			else if (getJ() > 0 && !getInstance().getButton(getI(), getJ() - 1).isShown()) getInstance().getButton(getI(), getJ() - 1).reveal();
		}
	}
	private static class ShiftRowBelow extends CyclopsButton {
		public ShiftRowBelow(int i, int j, CyclopsPuzzle parent) {
			super(i, j, "V<br>--&gt", parent);
			this.addCharge();
			this.addCharge();
			this.addCharge();
			this.addCharge();
			this.addCharge();
			this.update();
		}
		public void press() {
			if (getI() == CyclopsPuzzle.HEIGHT - 1) return;
			getInstance().shiftRow(getI() + 1);
		}
	}
	private static class ShiftColToLeft extends CyclopsButton {
		public ShiftColToLeft(int i, int j, CyclopsPuzzle parent) {
			super(i, j, "&lt T<br>&nbsp &nbsp |", parent);
			this.update();
		}
		public void press() {
			if (getJ() == 0) return;
			getInstance().shiftCol(getJ() - 1);
		}
	}
	private static class Generator extends CyclopsButton {
		public Generator(int i, int j, CyclopsPuzzle parent) {
			super(i, j, "&lt 3<br>&nbsp &lt 3", parent);
			if (!(i == 0 && j == 1)) this.addCharge();
			this.update();
		}
		public void press() {
			if (getI() > 0 && getInstance().getButton(getI() - 1, getJ()).isShown()) {
				getInstance().getButton(getI() - 1, getJ()).addCharge();
				getInstance().getButton(getI() - 1, getJ()).update();
			}
			if (getI() < CyclopsPuzzle.HEIGHT - 1 && getInstance().getButton(getI() + 1, getJ()).isShown()) {
				getInstance().getButton(getI() + 1, getJ()).addCharge();
				getInstance().getButton(getI() + 1, getJ()).update();
			}
			if (getJ() > 0 && getInstance().getButton(getI(), getJ() - 1).isShown()) {
				getInstance().getButton(getI(), getJ() - 1).addCharge();
				getInstance().getButton(getI(), getJ() - 1).update();
			}
			if (getJ() < CyclopsPuzzle.WIDTH - 1 && getInstance().getButton(getI(), getJ() + 1).isShown()) {
				getInstance().getButton(getI(), getJ() + 1).addCharge();
				getInstance().getButton(getI(), getJ() + 1).update();
			}
		}
	}
	private static class Blank extends CyclopsButton {
		public Blank(int i, int j, CyclopsPuzzle parent) {
			super(i, j, "", parent);
			this.update();
		}
		public void press() {}
		public void addCharge() {}
		public void useCharge() {}
	}
	private static class Goal extends CyclopsButton {
		public Goal(int i, int j, CyclopsPuzzle parent) {
			super(i, j, "??", parent);
			this.reveal();
			this.update();
		}
		public void press() {
			getInstance().win();
		}
	}
}
