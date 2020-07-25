import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Maze extends JPanel {

	private static final int NUM_ROOMS = 16;
	private static final Puzzle[] ALL_PUZZLES = new Puzzle[] {new Minesweeper(), new TicTacToe(), new CyclopsPuzzle(), new JSF(), new TwoSAT()};

	private Graph g;
	private Room[] rooms;
	private Room currentRoom;
	private Color squareColor;
	private long millisTime;
	private JFrame root;

	public Maze(JFrame frame, long seed) {
		root = frame;
		this.setPreferredSize(new Dimension(1080, 660));
		this.setFocusable(true);
		RNG.setRNGSeed(seed);
		g = Graph.makeRegularGraph(NUM_ROOMS, 4, true, true);
		ArrayList<Integer> l = new ArrayList<>(NUM_ROOMS);
		for (int i = 0; i < NUM_ROOMS; i++)
			l.add(i);
		l.remove(2);
		rooms = new Room[NUM_ROOMS];
		rooms[2] = new Monument(this, 2);
		for (int i = 0; i < 5; i++) {
			int roomNum = l.remove(RNG.randInt(0, l.size() - 1));
			rooms[roomNum] = new Room(this, roomNum, ALL_PUZZLES[i]);
		}
		for (int i = 0; i < NUM_ROOMS; i++)
			if (rooms[i] == null)
				rooms[i] = new Room(this, i, null);
		this.add(currentRoom = rooms[2]);
		squareColor = new Color(0, 0, 0, 0);
		this.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				currentRoom.requestFocusInWindow();
			}
		});
		millisTime = System.currentTimeMillis();
		currentRoom.enter();
	}
	public void changeRoom(int dir) {
		boolean[] currentKeys = currentRoom.getPressedKeys();
		currentRoom.leave();
		this.remove(currentRoom);
		currentRoom = rooms[g.getEdges(currentRoom.getRoomNum()).get(dir)];
		this.add(currentRoom);
		currentRoom.enter();
		currentRoom.requestFocusInWindow();
		this.revalidate();
		this.repaint();
		currentRoom.initKeys(currentKeys);
	}
	public void teleport() {
		boolean[] currentKeys = currentRoom.getPressedKeys();
		currentRoom.leave();
		this.remove(currentRoom);
		currentRoom = rooms[2];
		this.add(currentRoom);
		currentRoom.enter();
		currentRoom.requestFocusInWindow();
		this.revalidate();
		this.repaint();
		currentRoom.initKeys(currentKeys);
	}
	public void quit() {
		root.remove(this);
		root.add(new MainMenu(root));
		root.revalidate();
	}
	public void setSquareColor(Color c) {
		squareColor = c;
		currentRoom.updateSquare();
	}
	public long getStartTime() {
		return millisTime;
	}
	public Color getSquareColor() {
		return squareColor;
	}
	public Monument getMonument() {return (Monument) rooms[2];}
}
