import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    // MARK: Variables
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    // Higher the number, the slower the game
    static final int INITIAL_DELAY = 100;
    // Coordinates of the body parts of the snake
    int x[] = new int[GAME_UNITS];
    int y[] = new int[GAME_UNITS];
    // Number of body parts
    int bodyParts = 6;
    int foodEaten;
    int foodX;
    int foodY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    // Constructor
    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(40, 42, 53));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newFood();
        running = true;
        timer = new Timer(INITIAL_DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        if(running) {
            /*
            Draw a grid for development purposes

            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            } 
            */

            // Draw the food
            g.setColor(new Color(238,128,195));
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            // Draw the snake
            for(int i = 0; i < bodyParts; i++) {
                Color headColor = new Color(133, 247, 137);
                Color bodyColor = foodEaten >= 25 ? new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)) : new Color(97, 180, 100);
                g.setColor(i == 0 ? headColor : bodyColor );
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            drawScore(g);
        } else {
            gameOver(g);
        }
        
    }

    public void drawScore(Graphics g) {
        g.setColor(new Color(248, 248, 243));
        g.setFont(new Font("Lora", Font.BOLD,24));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String message = "Score: " + foodEaten;
        g.drawString(message, (SCREEN_WIDTH - metrics.stringWidth(message)) / 2, g.getFont().getSize());
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void newFood() {
        foodX = (random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE - 2)) * UNIT_SIZE) + UNIT_SIZE;
        foodY = (random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE - 2)) * UNIT_SIZE) + UNIT_SIZE;
    }

    public void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
            bodyParts++;
            foodEaten++;
            // Speed up the game
            if (foodEaten % 5 == 0) {
                timer.setDelay(timer.getDelay() - 10);
            }
            newFood();
        }
    }

    public void checkCollisions() {
        // Check if head collides with body
        for(int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // Check if head touches any of the borders
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(new Color(248, 248, 243));
        g.setFont(new Font("Lora", Font.BOLD, 64));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String message = "Game Over";
        g.drawString(message, (SCREEN_WIDTH - metrics.stringWidth(message)) / 2, SCREEN_HEIGHT / 2);

        drawScore(g);

        // Draw the play again button
        g.setColor(new Color(133, 247, 137));
        g.setFont(new Font("Inter", 400, 24));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        String message2 = "Press 'Enter' to play again!";
        g.drawString(message2, (SCREEN_WIDTH - metrics2.stringWidth(message2)) / 2, SCREEN_HEIGHT / 2 + 50);
    }

    public void playAgain() {
        bodyParts = 6;
        x = new int[SCREEN_WIDTH * SCREEN_HEIGHT / UNIT_SIZE];
        y = new int[SCREEN_WIDTH * SCREEN_HEIGHT / UNIT_SIZE];
        foodEaten = 0;
        direction = 'R';
        running = true;
        timer.setDelay(INITIAL_DELAY);
        repaint();
        startGame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Timer
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }

        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
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
                case KeyEvent.VK_ENTER:
                    if (!running) {
                        playAgain();
                    }
                    break;
            }
        }
    }
    
}
