import java.awt.*;
import javax.swing.*;

public class NGonCanvas extends JComponent {

	private static final Point CENTER = new Point(540, 330);
	private static final double HALF_PI = Math.PI / 2;
	private static final double R1 = 40, R2 = 105, R3 = 205;

	private int n;

	public NGonCanvas(int n) {
		this.n = n;
		this.setPreferredSize(new Dimension(1080, 660));
		this.setBackground(new Color(204, 204, 204));
		this.setOpaque(true);
	}
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setStroke(new BasicStroke(10));
		Point[] inside = new Point[n + 1], outside = new Point[n + 1];
		final double rad = Math.PI * 2 / n;
		for (int i = 0; i < n; i++) {
			inside[i] = new Point(CENTER.x + (int) (Math.cos(i * rad + HALF_PI) * R1), CENTER.y + (int) (Math.sin(i * rad + HALF_PI) * R1));
			outside[i] = new Point(CENTER.x + (int) (Math.cos(i * rad + HALF_PI + rad / 2) * R3), CENTER.y + (int) (Math.sin(i * rad + HALF_PI + rad / 2) * R3));
		}
		inside[n] = inside[0];
		outside[n] = outside[0];
		for (int i = 0; i < n; i++) {
			g.drawLine(inside[i].x, inside[i].y, inside[i + 1].x, inside[i + 1].y);
		}
		for (int i = 0; i < n; i++) {
			Point mid = new Point(CENTER.x + (int) (Math.cos(i * rad + HALF_PI + rad / 2) * R2), CENTER.y + (int) (Math.sin(i * rad + HALF_PI + rad / 2) * R2));
			g.drawLine(inside[i].x, inside[i].y, mid.x, mid.y);
			g.drawLine(inside[i + 1].x, inside[i + 1].y, mid.x, mid.y);
			g.drawLine(mid.x, mid.y, outside[i].x, outside[i].y);
			g.drawLine(outside[i].x, outside[i].y, outside[i + 1].x, outside[i + 1].y);
		}
	}
}
