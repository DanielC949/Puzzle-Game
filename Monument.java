import java.awt.*;
import javax.swing.*;

public class Monument extends Room {

	private GameIcon[] icons;

	public Monument(Maze parent, int id) {
		super(parent, id, null);
		icons = new GameIcon[] {new GameIcon(this, new Minesweeper(), "\u2738"), new GameIcon(this, new TicTacToe(), "XO"), new GameIcon(this, new CyclopsPuzzle(), "\uD83D\uDC41"), new GameIcon(this, new JSF(), "JS"), new GameIcon(this, new TwoSAT(), "\u2228\u2227")};
		icons[0].setBounds(500, 40, 80, 80);
		icons[1].setBounds(740, 230, 80, 80);
		icons[2].setBounds(665, 500, 80, 80);
		icons[3].setBounds(335, 500, 80, 80);
		icons[4].setBounds(260, 230, 80, 80);

		for (GameIcon g : icons) this.add(g, Integer.valueOf(0));
	}
	public void enter() {
		super.enter();
		if (!this.getMaze().getSquareColor().equals(new Color(0, 0, 0, 0))) {
			for (GameIcon g : icons) if (this.getMaze().getSquareColor().equals(g.getPuzzle().getColor())) g.place();
			this.getMaze().setSquareColor(new Color(0, 0, 0, 0));
			this.updateSquare();
			if (done()) win();
		}
	}
	public void cheat() {
		for (GameIcon g : icons) g.place();
		win();
	}
	private void win() {
		this.leave();
		ColoredPanel overlay = new ColoredPanel(new Color(0, 0, 0, 160));
		overlay.setBounds(0, 0, 1080, 660);
		overlay.setLayout(new GridBagLayout());

		JButton note = new JButton("<html><center>Congratulations!<br>You finished in<br>" + Lib.convertToHMS(System.currentTimeMillis() - this.getMaze().getStartTime()) + "</center></html>");
		note.setFont(new Font("TimesRoman", Font.PLAIN, 32));
		note.setOpaque(true);
		note.setBackground(Color.GRAY);
		note.setForeground(Color.BLACK);
		note.addActionListener(e -> this.getMaze().quit());

		overlay.add(note, new GridBagConstraints());

		this.add(overlay, Integer.valueOf(10));
	}
	public boolean completed(Class<? extends Puzzle> c) {
		for (GameIcon g : icons) if (g.getPuzzle().getClass() == c) return g.isPlaced();
		return false;
	}
	public boolean done() {
		for (GameIcon g : icons) if (!g.isPlaced()) return false;
		return true;
	}

	private static class GameIcon extends JLabel {
		private Puzzle g;
		private String d;
		public GameIcon(Monument m, Puzzle p, String icon) {
			super(icon, SwingConstants.CENTER);
			g = p;
			d = icon;
			this.setPreferredSize(new Dimension(80, 80));
			this.setBackground(new Color(0, 0, 0, 0));
			this.setOpaque(true);
			this.setForeground(Color.GRAY.brighter());
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			this.setFont(new Font("TimesRoman", Font.PLAIN, 45));
			this.setText(d);
		}
		public void place() {
			this.setBackground(g.getColor());
			this.setForeground(Color.BLACK);
			this.repaint();
		}
		public boolean isPlaced() {
			return this.getForeground().equals(Color.BLACK);
		}
		public Puzzle getPuzzle() {return g;}
	}
}
