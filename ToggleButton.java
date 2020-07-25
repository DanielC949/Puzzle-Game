import javax.swing.*;
import java.awt.*;

public class ToggleButton extends JButton {

	private boolean toggleOn;
	private Color on, off;

	public ToggleButton() {
		super();
		toggleOn = true;
		this.addActionListener(e -> this.toggle());
		this.setFocusPainted(false);
	}
	public ToggleButton(Color colorOn, Color colorOff) {
		this();
		this.setToggleOnColor(colorOn);
		this.setToggleOffColor(colorOff);
		this.setBackground(colorOn);
	}
	public void setToggleOnColor(Color c) {
		on = c;
	}
	public void setToggleOffColor(Color c) {
		off = c;
	}
	public void toggle() {
		this.setBackground(toggleOn ? off : on);
		toggleOn = !toggleOn;
		this.repaint();
	}
}
