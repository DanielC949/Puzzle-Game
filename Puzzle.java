import javax.swing.*;
import java.awt.*;

public interface Puzzle {
	JComponent start(Room parent);
	void close();
	Color getColor();
}
