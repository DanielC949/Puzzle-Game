import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.script.*;

public class JSF implements Puzzle {

	private static final ScriptEngine JS_ENGINE = new ScriptEngineManager().getEngineByName("javascript");
	private static final String MSG_1 = "<html>H", MSG_2 = "ving<br>&nbsp f", MSG_3 = "n?</html>";
	private static final int A_POS = 7, U_POS = 23;
	public static final Color COLOR = Color.RED;

	private PuzzleContainer container;
	private Room root;
	private ColoredPanel overlay;
	private JTextField input;
	private String a, u;
	private int letter;
	private Timer t;

	public JSF() {
		container = new PuzzleContainer();
		container.setOpaque(true);
		container.setBounds(0, 0, 500, 500);

		overlay = new ColoredPanel();
		overlay.setBounds(0, 0, container.getWidth(), container.getHeight());
		overlay.setColor(new Color(0, 0, 0, 160));
		overlay.setVisible(false);
		container.add(overlay, Integer.valueOf(1));

		a = "_";
		u = "_";

		JLabel display = new JLabel(MSG_1 + a + MSG_2 + u + MSG_3, SwingConstants.CENTER);
		display.setBounds((container.getWidth() - 220) / 2, 20, 220, 160);
		display.setBackground(Color.BLACK);
		display.setFont(new Font("Monospaced", Font.BOLD, 48));
		display.setForeground(Color.WHITE);
		display.setBorder(BorderFactory.createLineBorder(Color.GRAY, 10));
		display.setOpaque(true);
		display.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				int x = e.getX(), y = e.getY();
				if (x >= 50 && x <= 80 && y >= 30 && y <= 70) {
					letter = 0;
					t.stop();
					if (a.equals("a")) return;
					Lib.removeAllListeners(t);
					t.addActionListener(action -> {
						display.setText(MSG_1 + (display.getText().charAt(A_POS) == '_' ? " " : "_") + MSG_2 + u + MSG_3);
						display.repaint();
					});
					t.start();
					input.setText(" ");
				} else if (x >= 100 && x <= 130 && y >= 90 && y <= 130) {
					letter = 1;
					t.stop();
					if (u.equals("u")) return;
					Lib.removeAllListeners(t);
					t.addActionListener(action -> {
						display.setText(MSG_1 + a + MSG_2 + (display.getText().charAt(U_POS) == '_' ? " " : "_") + MSG_3);
						display.repaint();
					});
					t.start();
					input.setText(" ");
				}
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		container.add(display, Integer.valueOf(0));

		input = new JTextField("Esoteric?");
		input.setEditable(false);
		input.setBounds((container.getWidth() - 450) / 2, 200, 450, 35);
		input.setFont(new Font("Monospaced", Font.PLAIN, 16));
		input.setBackground(Color.BLACK);
		input.setForeground(Color.WHITE);
		input.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
		container.add(input, Integer.valueOf(0));

		JPanel buttons = new JPanel(new FlowLayout());
		buttons.setBounds((container.getWidth() - 220) / 2, 250, 220, 110);
		buttons.add(new JSFInput("!", input));
		buttons.add(new JSFInput("(", input));
		buttons.add(new JSFInput(")", input));
		buttons.add(new JSFSpecialInput("\u232B", e -> {
			if (input.getText().length() == 0) return;
			input.setText(input.getText().substring(0, input.getText().length() - 1));
			input.repaint();
		}));
		buttons.add(new JSFInput("+", input));
		buttons.add(new JSFInput("[", input));
		buttons.add(new JSFInput("]", input));
		buttons.add(new JSFSpecialInput("\u2713", action -> {
			String s;
			try {
				s = (String) JS_ENGINE.eval(input.getText());
			} catch (ScriptException e) {
				String before = input.getText();
				input.setText("Error while evaluating expression");
				Timer t = new Timer(1500, b -> input.setText(before));
				t.setRepeats(false);
				t.start();
				return;
			}
			if (!s.equals("a") && !s.equals("u")) {
				String before = input.getText();
				input.setText("Incorrect answer");
				Timer t = new Timer(1500, e -> input.setText(before));
				t.setRepeats(false);
				t.start();
				return;
			}
			if (letter == 0 && s.equals("a")) {
				a = "a";
				t.stop();
				display.setText(MSG_1 + a + MSG_2 + u + MSG_3);
				display.repaint();
				input.setText("");
				input.repaint();
			} else if (letter == 1 && s.equals("u")) {
				u = "u";
				t.stop();
				display.setText(MSG_1 + a + MSG_2 + u + MSG_3);
				display.repaint();
				input.setText("");
				input.repaint();
			}

			if (a.equals("a") && u.equals("u")) win();
		}));
		container.add(buttons, Integer.valueOf(0));

		letter = -1;
		t = new Timer(500, e -> {});
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
	}
	public void close() {
		root.remove(this.container);
		root.validate();
		root.repaint();
	}

	public Color getColor() {
		return COLOR;
	}

	private static class JSFSpecialInput extends JButton {

		private static final int SIZE = 50;

		public JSFSpecialInput(String character, ActionListener e) {
			this.setText(character);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setVerticalAlignment(SwingConstants.CENTER);
			this.setPreferredSize(new Dimension(SIZE, SIZE));
			this.setBackground(Color.LIGHT_GRAY);
			this.setFont(new Font("Monospaced", Font.BOLD, 32));
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			this.setFocusPainted(false);
			this.addActionListener(e);
		}
	}

	private static class JSFInput extends JSFSpecialInput {
		public JSFInput(String character, JTextField display) {
			super(character, e -> {
				if (display.getText().length() >= 40) return;
				display.setText(display.getText() + character);
				display.repaint();
			});
		}
	}
}
