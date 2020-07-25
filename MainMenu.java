import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainMenu extends JPanel {

	private JFrame root;

	public MainMenu(JFrame parent) {
		root = parent;
		this.setPreferredSize(new Dimension(1080, 660));
		this.setLayout(new GridBagLayout());

		GridBagConstraints center = new GridBagConstraints();

		JPanel startGame = new JPanel();

		startGame.setLayout(new BoxLayout(startGame, BoxLayout.PAGE_AXIS));
		this.add(startGame, center);

		JPanel seedPanel = new JPanel();
		JLabel seedLabel = new JLabel("Seed:");
		seedLabel.setFont(new Font("TimesRoman", Font.PLAIN, 32));
		JTextField seed = new JTextField();
		seed.setPreferredSize(new Dimension(150, 40));
		JButton start = new JButton("Start Game");

		start.setBackground(Color.LIGHT_GRAY);
		start.setPreferredSize(new Dimension(200, 50));
		start.setFont(new Font("TimesRoman", Font.PLAIN, 32));
		start.addActionListener(e -> {
			root.remove(this);
			Maze m = new Maze(root, seed.getText().equals("") ? RNG.randSeed() : seed.getText().hashCode());
			root.add(m);
			m.requestFocusInWindow();
			root.revalidate();
		});

		seed.setMaximumSize(new Dimension(500, 30));
		seed.setFont(new Font("Monospaced", Font.PLAIN, 26));
		seed.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) start.getActionListeners()[0].actionPerformed(null);
			}
		});
		seedPanel.add(seedLabel);
		seedPanel.add(seed);

		startGame.add(Box.createVerticalGlue());
		startGame.add(seedPanel);
		startGame.add(Box.createVerticalStrut(20));
		startGame.add(start);
		startGame.add(Box.createVerticalGlue());
	}
}
