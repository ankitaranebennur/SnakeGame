import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {

    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS =
            (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 100;

    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];

    private int bodyParts = 6;
    private int appleX;
    private int appleY;

    private char direction = 'R';
    private boolean running = false;

    private Timer timer;
    private final Random random = new Random();

    public SnakeGame() {

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        // Initial snake position
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 300 - (i * UNIT_SIZE);
            y[i] = 300;
        }

        addKeyListener(new MyKeyAdapter());

        startGame();
    }

    public void startGame() {

        newApple();

        running = true;

        timer = new Timer(DELAY, this);
        timer.start();

        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGame(g);
    }

    private void drawGame(Graphics g) {

        if (running) {

            // Draw Apple
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw Snake
            for (int i = 0; i < bodyParts; i++) {

                g.setColor(Color.RED);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Draw Score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + (bodyParts - 6), 10, 25);

        } else {
            gameOver(g);
        }
    }

    private void newApple() {

        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    private void move() {

        // Move body
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Move head
        switch (direction) {

            case 'U':
                y[0] -= UNIT_SIZE;
                break;

            case 'D':
                y[0] += UNIT_SIZE;
                break;

            case 'L':
                x[0] -= UNIT_SIZE;
                break;

            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    private void checkApple() {

        if (x[0] == appleX && y[0] == appleY) {

            bodyParts++;
            newApple();
        }
    }

    private void checkCollisions() {

        // Snake hits its own body
        for (int i = bodyParts; i > 0; i--) {

            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        // Wall collision
        if (x[0] < 0 ||
                x[0] >= SCREEN_WIDTH ||
                y[0] < 0 ||
                y[0] >= SCREEN_HEIGHT) {

            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    private void gameOver(Graphics g) {

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));

        FontMetrics metrics = getFontMetrics(g.getFont());

        String gameOverText = "GAME OVER";

        g.drawString(
                gameOverText,
                (SCREEN_WIDTH - metrics.stringWidth(gameOverText)) / 2,
                SCREEN_HEIGHT / 2
        );

        g.setFont(new Font("Arial", Font.BOLD, 25));

        String scoreText = "Score: " + (bodyParts - 6);

        FontMetrics scoreMetrics = getFontMetrics(g.getFont());

        g.drawString(
                scoreText,
                (SCREEN_WIDTH - scoreMetrics.stringWidth(scoreText)) / 2,
                SCREEN_HEIGHT / 2 + 50
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (running) {

            move();
            checkApple();
            checkCollisions();
        }

        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {

                case KeyEvent.VK_LEFT:

                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT:

                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:

                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:

                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Snake Game");

            SnakeGame game = new SnakeGame();

            frame.add(game);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            game.requestFocusInWindow();
        });
    }
}