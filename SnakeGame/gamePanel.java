package SnakeGame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import java.sql.Connection;
import java.sql.DriverManager;



public class gamePanel extends JPanel implements ActionListener{

    
    // Window size
    static final int SCREEN_WIDTH = 800;
    static final int SCREEN_HEIGHT = 800;
    // Size of each "Grid square"
    static final int UNIT_SIZE = 25;
    // Number of "Grid squares"
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    // Snake speed (Higher nummber = less speed)
    static final int DELAY = 75;
    // Snakes X and Y coordinates
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    // Snake parts
    int bodyParts = 6;
    // Score
    int applesEaten;
    // apples x and y possition
    int appleX;
    int appleY;
    // variable for snakes direction
    char direction = 'R';
    // is game runing
    boolean running = false;
    boolean gameOver = false;
    int highScore = 0;
    Timer timer;
    Random random;
    
    // connecting to my database
    public static Connection getConnection() throws Exception{
        try{
            String driver = "com.mysql.jdbc.Driver";
            // url/address of the database i want to connect to
            String url = "jdbc:mysql://localhost:3306/aarsoppgavespill";
            String username = "snakeGame";
            String password = "0RKp4TJdfCpoD2mB";
            Class.forName(driver);

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected");

            return conn;
        } catch(Exception e){
            System.out.println(e);
        }


        return null;
    } 

    // setting game panel properties
    gamePanel() throws Exception{
        random = new Random();
        getConnection();
        // Sets dimentions of the Window(Uses our previously defined variables)
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new myKeyAdapter());
        startGame();
    }
    // Method for starting the Game
    public void startGame(){
        spawnApple();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }

    public void reStart(){
        gameOver = false;
        direction = 'R';
        applesEaten = 0;
        bodyParts = 6;
        for(int i = 0; i < bodyParts; i++){
            if(i == 0){
                x[i] = (UNIT_SIZE);
                y[i] = (UNIT_SIZE);
            }
            else{
                x[i] = (UNIT_SIZE - UNIT_SIZE);
                y[i] = (UNIT_SIZE);
            }
        }
        running = true;
        spawnApple();
        timer.start();
    }

    // Method for paintign the different graphics
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    // Method for designing the graphics
    public void draw(Graphics g){
        
        if(running){
            for(int i = 0; i < (SCREEN_HEIGHT/UNIT_SIZE); i++){
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }
            for(int i = 0; i < (SCREEN_WIDTH/UNIT_SIZE); i++){
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
            }

            g.setColor(Color.GREEN);
            g.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            int colorCodeR = 255;
            int colorCodeG = 75;
            int colorCodeB = 255;

            for(int i = 0; i < bodyParts; i++){
                colorCodeB = (colorCodeB - (255/bodyParts));
                g.setColor(new Color(colorCodeR, colorCodeG, colorCodeB));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }
        else{
            gameOver(g);
        }
    }

    // Method for decideing where and when we want a apple to get created
    public void spawnApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
        for(int i = bodyParts; i > 0; i--){
            if((appleX == x[i]) && (appleY == y[i])){
                spawnApple();
            }
        }
    }

    // Method for changing the direction of the snake
    public void move(){
        for(int i = bodyParts; i > 0; i--){
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch(direction){
            // Moving snake up
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            // Moving snake down
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            // moving snake left
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            // Moving snake right
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }

    }

    // Method for checking when the apple gets eaten
    public void checkApple(){
        if((x[0] == appleX) && (y[0] == appleY)){
            applesEaten++;
            bodyParts++;
            spawnApple();
        }
    }

    // Method for checking when snake collides with wall or itself
    public void checkCollisions(){
        // checks if head of snake touches body of snake
        for(int i = bodyParts; i > 0; i--){
            if((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
            }
        }

        // checks if head of snake touches edge

        // left edge
        if(x[0] < 0){
            running = false;
        }

        // right edge
        if(x[0] > SCREEN_WIDTH - UNIT_SIZE){
            running = false;
        }

        // top edge
        if(y[0] < 0){
            running = false;
        }

        // bottom edge
        if(y[0] > SCREEN_HEIGHT - UNIT_SIZE){
            running = false;
        }

        if(!running){
            timer.stop();
        }
    }

    public void checkHighScore(){
        if(applesEaten > highScore){
            highScore = applesEaten;
        }
    }

    // Method for when we want to display the game over screen
    public void gameOver(Graphics g){
        // game over screen
        checkHighScore();
        gameOver = true;
        g.setColor(Color.RED);
        g.setFont(new Font("Monospaced",Font.PLAIN, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2 - 75);
        g.drawString("Score:" + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score:" + applesEaten))/2, SCREEN_HEIGHT/2);
        g.drawString("High score:" + highScore, (SCREEN_WIDTH - metrics.stringWidth("High score:" + applesEaten))/2, SCREEN_HEIGHT/2 + 75);
        g.setFont(new Font("Monospaced",Font.PLAIN, 18));
        FontMetrics metricsSmall = getFontMetrics(g.getFont());
        g.drawString("Press the *SPACE* key on the keyboard to restart",  (SCREEN_WIDTH - metricsSmall.stringWidth("Press the *SPACE* key on the keyboard to restart"))/2, SCREEN_HEIGHT/2 + 200);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
    
    // getting keyboard inputs
    public class myKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A:
                    if(direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D:
                    if(direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP, KeyEvent.VK_W:
                    if(direction != 'D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN, KeyEvent.VK_S:
                    if(direction != 'U'){
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if(gameOver){
                    draw(getGraphics());
                    reStart();
                }
            }
        }
    }
}
