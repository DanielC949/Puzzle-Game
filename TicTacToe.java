import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TicTacToe implements Puzzle {

    private static final int SIZE = 5;
    public static final Color COLOR = Color.BLUE;

    private TicTacToeButton[][] grid;
    private PuzzleContainer container;
    private Room root;
    private JPanel field;
    private ColoredPanel overlay;
    private int turn, state;

    public TicTacToe() {
        grid = new TicTacToeButton[SIZE][SIZE];
        container = new PuzzleContainer();
        container.setBounds(0, 0, 500, 500);
        container.setPreferredSize(new Dimension(500, 500));

        overlay = new ColoredPanel();
        overlay.setBounds(0, 0, 500, 500);
        overlay.setColor(new Color(0, 0, 0, 160));
        overlay.setVisible(false);
        container.add(overlay, Integer.valueOf(1));

        field = new JPanel(new GridBagLayout());
        field.setBounds(0, 0, 500, 500);
        container.add(field, Integer.valueOf(0));

        state = 0;
    }
    public JComponent start(Room parent) {
	    root = parent;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                TicTacToeButton b = new TicTacToeButton(this);
                if (i == 0 || i == SIZE - 1 || j == 0 || j == SIZE - 1)
                    b.markHidden();
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = i;
                c.gridy = j;
                field.add(b, c);
                grid[i][j] = b;
            }
        }
        turn = 1;
        return container;
    }
    private void takeTurn() {
    	Mark m = bestMove(this.getBoardState());
    	grid[m.i + 1][m.j + 1].mark();
		nextTurn();
    }
    private Mark bestMove(int[][] b) {
    	int best = -1000;
    	Mark m = new Mark(-1, -1);
    	for (int i = 0; i < 3; i++) {
    		for (int j = 0; j < 3; j++) {
    			if (b[i][j] == 0) {
    				b[i][j] = -1;
    				int move = minimax(b, false);
    				b[i][j] = 0;
    				if (move > best) {
    					m.i = i;
    					m.j = j;
    					best = move;
				    }
			    }
		    }
	    }
    	return m;
    }
    private int minimax(int[][] b, boolean max) {
    	int score = eval(b);
    	if (score == 10 || score == -10) return score;
    	if (tied(b)) return 0;

    	int best = max ? -1000 : 1000;
    	for (int i = 0; i < 3; i++)
    		for (int j = 0; j < 3; j++)
    			if (b[i][j] == 0) {
    				b[i][j] = max ? -1 : 1;
    				best = max ? Math.max(best, minimax(b, !max)) : Math.min(best, minimax(b, !max));
    				b[i][j] = 0;
			    }
    	return best;
    }
    private int eval(int[][] b) {
    	for (int i = 0; i < 3; i++) {
    		if (b[i][0] == b[i][1] && b[i][1] == b[i][2]) {
			    if (b[i][0] == -1) return 10;
			    else if (b[i][0] == 1) return -10;
		    }
    		if (b[0][i] == b[1][i] && b[1][i] == b[2][i]) {
			    if (b[0][i] == -1) return 10;
			    else if (b[0][i] == 1) return -10;
		    }
	    }
    	if (b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
		    if (b[0][0] == -1) return 10;
		    else if (b[0][0] == 1) return -10;
	    }
    	if (b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
		    if (b[0][2] == -1) return 10;
		    else if (b[0][2] == 1) return -10;
	    }
    	return 0;
    }
    private int[][] getBoardState() {
    	int[][] out = new int[3][3];
    	for (int i = 1; i < 4; i++)
    		for (int j = 1; j < 4; j++)
    			out[i - 1][j - 1] = grid[i][j].getMark();
    	return out;
    }
    private boolean tied(int[][] state) {
    	for (int i = 0; i < 3; i++)
    		for (int j = 0; j < 3; j++)
    			if (state[i][j] == 0) return false;
    	return true;
    }
    private void checkWin() {
        if (state != 0) return;
        int open = 9;
        for (int i = 1; i < 4; i++)
        	for (int j = 1; j < 4; j++)
        		if (grid[i][j].getMark() != 0) open--;
        if (open == 0) {
        	tie();
        	return;
        }

        boolean xStraight = checkHorizontal() == 1 || checkVertical() == 1;
        boolean xDiagRight = checkDiagonal(2, 0, 1, 1) == 1 || checkDiagonal(1, 0, 1, 1) == 1 || checkDiagonal(0, 0, 1, 1) == 1 || checkDiagonal(0, 1, 1, 1) == 1 || checkDiagonal(0, 2, 1, 1) == 1;
        boolean xDiagLeft = checkDiagonal(2, 4, 1, -1) == 1 || checkDiagonal(1, 4, 1, -1) == 1 || checkDiagonal(0, 4, 1, -1) == 1 || checkDiagonal(0, 3, 1, -1) == 1 || checkDiagonal(0, 2, 1, -1) == 1;
        boolean oStraight = checkHorizontal() == -1 || checkVertical() == -1;
        boolean oDiagRight = checkDiagonal(2, 0, 1, 1) == -1 || checkDiagonal(1, 0, 1, 1) == -1 || checkDiagonal(0, 0, 1, 1) == -1 || checkDiagonal(0, 1, 1, 1) == -1 || checkDiagonal(0, 2, 1, 1) == -1;
        boolean oDiagLeft = checkDiagonal(2, 4, 1, -1) == -1 || checkDiagonal(1, 4, 1, -1) == -1 || checkDiagonal(0, 4, 1, -1) == -1 || checkDiagonal(0, 3, 1, -1) == -1 || checkDiagonal(0, 2, 1, -1) == -1;

        if (xStraight || xDiagLeft || xDiagRight) win();
        else if (oStraight || oDiagLeft || oDiagRight) lose();
    }
    private int checkHorizontal() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 1; j < SIZE - 1; j++) {
                if (grid[i][j].getMark() == 1 && grid[i][j - 1].getMark() == 1 && grid[i][j + 1].getMark() == 1) return 1;
                if (grid[i][j].getMark() == -1 && grid[i][j - 1].getMark() == -1 && grid[i][j + 1].getMark() == -1) return -1;
            }
        }
        return 0;
    }
    private int checkVertical() {
        for (int j = 0; j < SIZE; j++) {
            for (int i = 1; i < SIZE - 1; i++) {
                if (grid[i][j].getMark() == 1 && grid[i - 1][j].getMark() == 1 && grid[i + 1][j].getMark() == 1) return 1;
                if (grid[i][j].getMark() == -1 && grid[i - 1][j].getMark() == -1 && grid[i + 1][j].getMark() == -1) return -1;
            }
        }
        return 0;
    }
    private int checkDiagonal(int iOffset, int jOffset, int iChange, int jChange) {
        int x = 0, o = 0;
        for (int i = iOffset, j = jOffset; i >= 0 && i < SIZE && j >= 0 && j < SIZE;) {
            if (grid[i][j].getMark() == 1) {
                x++;
                o = 0;
            }
            if (grid[i][j].getMark() == -1) {
                o++;
                x = 0;
            }
            if (grid[i][j].getMark() == 0) {
                x = 0;
                o = 0;
            }
            i += iChange;
            j += jChange;
            if (x >= 3) return 1;
            if (o >= 3) return -1;
        }
        return 0;
    }
    private void win() {
        state = 1;

	    overlay.setVisible(true);
        overlay.setLayout(null);

        JButton youWon = new JButton("You won!");
        youWon.setBackground(Color.GRAY);
        youWon.setOpaque(true);
        youWon.setForeground(Color.GREEN);
        youWon.setFont(new Font("TimesRoman", Font.BOLD, 25));
        youWon.setBounds(overlay.getWidth() / 2 - 150 / 2, overlay.getHeight() / 2 - 50 / 2, 150, 50);
        youWon.addActionListener(e -> {
        	root.puzzleWin();
            root.remove(this.container);
            root.validate();
            root.repaint();
        });

        overlay.add(youWon);
    }
    private void lose() {
        state = -1;

        overlay.setVisible(true);
        overlay.setLayout(null);

        JButton youLost = new JButton("<html><center>You lost. Retry?</center></html>");
        youLost.setBackground(Color.GRAY);
        youLost.setOpaque(true);
        youLost.setForeground(Color.RED);
        youLost.setFont(new Font("TimesRoman", Font.BOLD, 25));
        youLost.setBounds(overlay.getWidth() / 2 - 150 / 2, overlay.getHeight() / 2 - 75 / 2, 150, 75);
        youLost.addActionListener(e -> {
            root.remove(this.container);
            root.add(new TicTacToe().start(root), Integer.valueOf(2));
            root.validate();
            root.repaint();
        });

        overlay.add(youLost);
    }
    private void tie() {
        state = -2;

        overlay.setVisible(true);
        overlay.setLayout(null);

        JButton youTied = new JButton("<html><center>You tied. Retry?</center></html>");
        youTied.setBackground(Color.GRAY);
        youTied.setOpaque(true);
        youTied.setForeground(Color.BLACK);
        youTied.setFont(new Font("TimesRoman", Font.BOLD, 25));
        youTied.setBounds(overlay.getWidth() / 2 - 150 / 2, overlay.getHeight() / 2 - 75 / 2, 150, 75);
        youTied.addActionListener(e -> {
            root.remove(this.container);
            root.add(new TicTacToe().start(root), Integer.valueOf(2));
            root.validate();
            root.repaint();
        });

        overlay.add(youTied);
    }
    public void close() {
	    root.remove(this.container);
	    root.validate();
	    root.repaint();
    }
    private void nextTurn() {
        turn *= -1;
    }
    private int getTurn() {
        return turn;
    }
    private JLayeredPane getContainer() {
        return container;
    }

    public Color getColor() {
    	return COLOR;
    }

    private static class TicTacToeButton extends JLabel {
        private int mark;
        private TicTacToe instance;
        public TicTacToeButton(TicTacToe t) {
            super("", SwingConstants.CENTER);
            this.instance = t;
            this.mark = 0;

            this.setOpaque(true);
            this.setPreferredSize(new Dimension(75, 75));
            this.setFont(new Font("TimesRoman", Font.BOLD, 70));
            this.setBackground(this.instance.getContainer().getBackground());
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
	                if (mark != 0) return;
	                mark();
	                instance.checkWin();
	                instance.nextTurn();
	                instance.takeTurn();
	                instance.checkWin();
                }
            });
        }
        public int getMark() {
            return mark;
        }
        public void markHidden() {
            this.setBorder(null);
        }
        public void mark() {
        	if (this.instance.state != 0) return;
            this.setText(this.instance.getTurn() == 1 ? "X" : "O");
            this.setForeground(this.instance.getTurn() == 1 ? Color.RED : Color.BLACK);
            this.mark = this.instance.getTurn();
            this.repaint();
        }
    }
    private static class Mark {
    	public int i, j;
    	public Mark(int a, int b) {
    		i = a;
    		j = b;
	    }
    }
}
