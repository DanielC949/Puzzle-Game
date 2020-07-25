import java.awt.*;
import javax.swing.*;

public class TwoSAT implements Puzzle {

	private static final int NUM_LITERALS = 8, NUM_CLAUSES = 16;
	public static final Color COLOR = Color.GREEN;

	private PuzzleContainer container;
	private Room root;
	private ColoredPanel overlay;
	private TwoCNF cnf;
	private boolean[] inputs;

	public TwoSAT() {
		container = new PuzzleContainer();
		container.setBounds(0, 0, 500, 500);
		container.setOpaque(true);

		cnf = new TwoCNF();
		JTextArea display = new JTextArea(cnf.toString());
		display.setLineWrap(true);
		display.setWrapStyleWord(true);
		display.setOpaque(true);
		display.setBackground(Color.LIGHT_GRAY);
		display.setEditable(false);
		display.setFont(new Font("Monospaced", Font.BOLD, 20));
		display.setBounds(25, 25, 450, 170);
		container.add(display, Integer.valueOf(0));

		inputs = new boolean[NUM_LITERALS];
		JPanel userInput = new JPanel(new FlowLayout());
		userInput.setBounds(175, 250, 150, 180);
		for (int i = 0; i < NUM_LITERALS; i++) {
			int num = i;
			inputs[i] = true;
			ToggleButton b = new ToggleButton(Color.LIGHT_GRAY, Color.GRAY);
			b.addActionListener(e -> {
				inputs[num] = !inputs[num];
				b.setText((char)(num + 65) + ": " + inputs[num]);
				b.repaint();
			});
			b.setMargin(new Insets(0, 0, 0, 0));
			b.setPreferredSize(new Dimension(70, 30));
			b.setOpaque(true);
			b.setText((char)(num + 65) + ": " + inputs[num]);
			userInput.add(b);
		}
		JButton eval = new JButton("Evaluate");
		eval.setPreferredSize(new Dimension(145, 30));
		eval.setBackground(Color.LIGHT_GRAY);
		eval.setOpaque(true);
		eval.setFocusPainted(false);
		eval.addActionListener(e -> {
			if (cnf.verify(inputs)) win();
			else {
				eval.setText("Evaluated to false");
				Timer t = new Timer(1500, a -> eval.setText("Evaluate"));
				t.setRepeats(false);
				t.start();
			}
		});
		userInput.add(eval);
		container.add(userInput, Integer.valueOf(0));

		overlay = new ColoredPanel();
		overlay.setColor(new Color(0, 0, 0, 160));
		overlay.setBounds(0, 0, 500, 500);
		overlay.setVisible(false);
		container.add(overlay, Integer.valueOf(2));
	}
	public JComponent start(Room parent) {
		root = parent;
		return container;
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
		container.repaint();
	}
	public void close() {
		root.remove(this.container);
		root.validate();
		root.repaint();
	}

	public Color getColor() {
		return COLOR;
	}

	private static class TwoCNF {

		private boolean[] vars;
		private Clause[] clauses;

		public TwoCNF() {
			vars = new boolean[TwoSAT.NUM_LITERALS];
			for (int i = 0; i < vars.length; i++) {
				vars[i] = RNG.flipCoin();
			}
			clauses = new Clause[TwoSAT.NUM_CLAUSES];
			for (int i = 0; i < clauses.length; i++) {
				Clause c;
				do {
					boolean not1 = RNG.flipCoin(), not2 = RNG.flipCoin();
					int l1, l2;
					do {
						l1 = RNG.randInt(0, TwoSAT.NUM_LITERALS - 1);
						l2 = RNG.randInt(0, TwoSAT.NUM_LITERALS - 1);
					} while (l1 == l2);
					c = new Clause(l1, l2, not1, not2, not1 ? !vars[l1] : vars[l1], not2 ? !vars[l2] : vars[l2]);
				} while (!c.eval());
				clauses[i] = c;
			}
		}
		public boolean verify(boolean[] input) {
			boolean truth = true;
			for (Clause c : clauses) {
				truth &= c.check(input[c.literal1], input[c.literal2]);
			}
			return truth;
		}
		public String toString() {
			String out = "";
			for (int i = 0; i < clauses.length; i++) {
				out += clauses[i].toString();
				if (i != clauses.length - 1) out += " && ";
			}
			return out;
		}
	}
	private static class Clause {

		private int literal1, literal2;
		private boolean not1, not2;
		private boolean truth;

		public Clause(int l1, int l2, boolean n1, boolean n2, boolean v1, boolean v2) {
			literal1 = l1;
			literal2 = l2;
			not1 = n1;
			not2 = n2;
			truth = v1 || v2;
		}
		public boolean eval() {
			return truth;
		}
		public boolean check(boolean b1, boolean b2) {
			return (not1 ? !b1 : b1) || (not2 ? !b2 : b2);
		}
		public String toString() {
			return "(" + (not1 ? "!" : "") + (char)(literal1 + 65) + "\u00A0||\u00A0" + (not2 ? "!" : "") + (char)(literal2 + 65) + ")";
		}
	}
}
