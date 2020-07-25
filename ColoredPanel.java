import javax.swing.*;
import java.awt.*;

public class ColoredPanel extends JPanel {
    private Color c;
    public ColoredPanel() {
        this(null);
    }
    public ColoredPanel(Color color) {
        c = color;
        this.setOpaque(false);
    }
    public void setColor(Color color) {
        c = color;
    }
    public void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;

        g.setColor(c);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
}
