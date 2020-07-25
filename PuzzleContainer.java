import javax.swing.*;
import java.awt.*;

public class PuzzleContainer extends JLayeredPane {
	public PuzzleContainer() {
		this.setPreferredSize(new Dimension(500, 500));
		ColoredPanel border = new ColoredPanel();
		border.setColor(new Color(250, 250, 250, 0));
		border.setBounds(0, 0, 500, 500);
		border.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
		this.add(border, Integer.valueOf(20));
	}
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setStroke(new BasicStroke(5));
		g.drawLine(0, 0, 500, 0);
		g.drawLine(500, 0, 500, 500);
		g.drawLine(500, 500, 0, 500);
		g.drawLine(0, 500, 0, 0);
	}
}
