package main;

import javax.swing.JPanel;
import java.awt.*;

import entity.Player;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable{
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; //16*3 = 48
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 16;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    //FPS game
    int fps = 60;

    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    Player player = new Player(this, keyH);

    /* Posisi default player */
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / fps;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null){
//          System.out.println("mulai"); -> buat ngetes aja udah ngerun atau belum
            currentTime = System.nanoTime();

            delta = delta + (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update(){
        player.update();

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        /* Perhatikan urutan draw, berlaku seperti layer. Pastikan draw tile dulu baru player agar player berada di atas tile */
        tileM.draw(g2);
        player.draw(g2);

        g2.dispose();
    }

}
