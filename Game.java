import javax.swing.*;
public class Game {
	
	public Game() {
		
	}
	public void start() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

 		frame.add(new MainMenu(frame));

		frame.pack();
		frame.setVisible(true);
	}
}
