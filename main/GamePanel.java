package main;


import java.awt.Color;
import javax.swing.JPanel;
import entity.NPC_1;
import entity.NPC_2;
import entity.NPC_3;
import entity.NPC_4;
import entity.Entity;
import entity.Player;
import object.SuperObject;
import tile.TileManager;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel implements Runnable{
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 16;
    public final int screenWidth= tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // world settings
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    // FPS
    int FPS = 60;

    // System
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    Thread gameThread;

    // entity and object
    public Player player = new Player(this, keyH);
    public SuperObject obj[] = new SuperObject[10]; // 10 untuk slot yang dapat digunakan untuk menaruh objek, boleh ditambah.
    public Entity npc[] = new Entity[10];

    // GAME STATE
    public int gameState;
    public final int playState = 1;
    public final int pauseState = 2; // ini gunanya untuk mengatur aksi lain di screen pada waktu yang bersamaan, misalnya swing a sword with enter key, dimana di saat yang bersamaan enter bisa untuk melakukan aksi lain
    public final int dialogueState = 3;
    public final int titleState = 0;
    //int playerX = 100;
    //int playerY = 100;
    //int playerSpeed = 4;


    
    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setUpGame() {
        aSetter.setObject();
        initializeNPCs();
        // playMusic(0);
        gameState = titleState;
    }

    public void initializeNPCs() {
        // NPC 1 (yang sudah ada)
        npc[0] = new NPC_1(this);
        npc[0].worldX = tileSize * 21; // Contoh posisi X
        npc[0].worldY = tileSize * 21; // Contoh posisi Y

        // NPC 2 (baru)
        npc[1] = new NPC_2(this);
        npc[1].worldX = tileSize * 25; // Atur posisi yang berbeda
        npc[1].worldY = tileSize * 21;

        // NPC 3 (baru)
        npc[2] = new NPC_3(this);
        npc[2].worldX = tileSize * 28;
        npc[2].worldY = tileSize * 21;

        // NPC 4 (baru)
        npc[3] = new NPC_4(this);
        npc[3].worldX = tileSize * 30;
        npc[3].worldY = tileSize * 21;

        // Pastikan posisi worldX dan worldY ini berada dalam batas peta Anda
        // (maxWorldCol * tileSize dan maxWorldRow * tileSize)
        // dan tidak bertabrakan dengan tile yang solid secara default.
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void run(){
        double drawInterval = 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;
        
        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime)/ drawInterval;
            timer += (currentTime - lastTime);
           // System.out.println("current time" + currentTime);
            lastTime = currentTime;
            //timer = 0;
            //drawCount = 0;

            if(delta >= 1){
                update(); //update posisi karakter
                repaint();//gambar ulang bagian yang tadinya ditempati oleh karakter
                delta--;
            }
         
            if(timer >= 1000000000 ){
                System.out.println("FPS: " + FPS);
                drawCount = 0;
                timer = 0;
            }

            try{
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime/1000000;
                if(remainingTime < 0){
                    remainingTime = 0;
                }
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            
        }
    }
    public void update(){
        if(gameState == playState){
            player.update(); //player
            for(int i = 0; i < npc.length; i++){
                if(npc[i] != null){
                    npc[i].update();
                }
            }
        }
        if(gameState == pauseState){
            //gadak
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        // debug
        long drawStart = 0;
        if (keyH.checkDrawTime == true){
            drawStart = System.nanoTime();
        }

        // TITLE SCREEN
        if (gameState == titleState) {
            ui.draw(g2);
        }

        // OTHERS
        else {

            tileM.draw(g2); // tile

            for(int i = 0; i < obj.length; i++){
                if(obj[i] != null){
                    obj[i].draw(g2, this);
                }
            }

            // npc
            for(int i=0; i< npc.length; i++){
                if(npc[i] != null){
                    npc[i].draw(g2);
                }
            }
            player.draw(g2); // player

            // debug
            if(keyH.checkDrawTime == true){
                long drawEnd = System.nanoTime();
                long passed = drawEnd - drawStart;
                g2.setColor(Color.white);
                g2.drawString("Draw Time: " + passed, 10, 400);
                System.out.println("Draw Time: " + passed);
            }

            // ui
            ui.draw(g2);
        }

        g2.dispose();
    }

    public void playMusic(int i) {
        
        music.setFile(i);
        music.play();
        music.loop();
    }
    public void stopMusic() {
        
        music.stop();
    }
    public void playSE(int i) {

        se.setFile(i);
        se.play();
    }

}
