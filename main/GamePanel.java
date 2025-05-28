package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import data.SaveLoad;
import entity.Entity;
import entity.NPC_1_MayorTadi;
import entity.NPC_2_Caroline;
import entity.NPC_3_Perry;
import entity.NPC_4_Dasco;
import entity.NPC_5_Emily;
import entity.NPC_6_Abigail;
import entity.Player;
import object.SuperObject;
import tile.TileManager;
import environment.GameState;
import item.ItemStack;
import environment.EnvironmentManager;


public class GamePanel extends JPanel implements Runnable{
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 16;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    private int currentlyPlayingMusicIndex = -1;
    private boolean musicIsPlaying = false;

    // world settings
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    //Map Settings (untuk pindah Map)
    public final int maxMap = 5; // jumlah map yang ada
    public int currentMap = 0;

    // FPS
    int FPS = 45;
    int drawCount = 0;

    // System
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public EventHandler eHandler = new EventHandler(this);
    Thread gameThread;
    // Timer untuk update waktu game
    private long lastTimeUpdate = System.currentTimeMillis();
    private final int timeUpdateInterval = 1000; // tiap 1 detik (1000 ms)

    //Untuk save & load
    SaveLoad saveLoad = new SaveLoad(this);


    // entity and object
    public Player player = new Player(this, keyH);
    public SuperObject obj[][] = new SuperObject[maxMap][10]; // 10 untuk slot yang dapat digunakan untuk menaruh objek, boleh ditambah.
    public Entity npc[][] = new Entity[maxMap][10];

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2; // ini gunanya untuk mengatur aksi lain di screen pada waktu yang bersamaan, misalnya swing a sword with enter key, dimana di saat yang bersamaan enter bisa untuk melakukan aksi lain
    public final int dialogueState = 3;
    public final int sleepState = 4; // untuk tidur di malam hari
    public final int transitionState = 5; //untuk transisi pindah map yang lebih halus
    public final int inventoryState = 6; 
    public final int npcInteractionState = 7;

  
    //int playerX = 100;
    //int playerY = 100;
    //int playerSpeed = 4;

    // untuk time
    public GameState gameStateSystem = new GameState();
    public EnvironmentManager envManager = new EnvironmentManager(this);




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
        playMusic(0);
        gameState = titleState;
    }

    public void initializeNPCs() {

        int mapNum = 0;
        
        // NPC 1 (yang sudah ada)
        npc[mapNum][0] = new NPC_1_MayorTadi(this);
        npc[mapNum][0].worldX = tileSize * 21; // Contoh posisi X
        npc[mapNum][0].worldY = tileSize * 21; // Contoh posisi Y

        // NPC 2 (baru)
        npc[mapNum][1] = new NPC_2_Caroline(this);
        npc[mapNum][1].worldX = tileSize * 25; // Atur posisi yang berbeda
        npc[mapNum][1].worldY = tileSize * 21;

        // NPC 3 (baru)
        npc[mapNum][2] = new NPC_3_Perry(this);
        npc[mapNum][2].worldX = tileSize * 28;
        npc[mapNum][2].worldY = tileSize * 21;

        // NPC 4 (baru)
        npc[mapNum][3] = new NPC_4_Dasco(this);
        npc[mapNum][3].worldX = tileSize * 17;
        npc[mapNum][3].worldY = tileSize * 21;
        
        npc[mapNum][4] = new NPC_5_Emily(this);
        npc[mapNum][4].worldX = tileSize * 13;
        npc[mapNum][4].worldY = tileSize * 21;
        
        npc[mapNum][5] = new NPC_6_Abigail(this);
        npc[mapNum][5].worldX = tileSize * 23;
        npc[mapNum][5].worldY = tileSize * 25;

        // Pastikan posisi worldX dan worldY ini berada dalam batas peta Anda
        // (maxWorldCol * tileSize dan maxWorldRow * tileSize)
        // dan tidak bertabrakan dengan tile yang solid secara default.
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run()   {
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
            for(int i = 0; i < npc[currentMap].length; i++){
                if(npc[currentMap][i] != null){
                    npc[currentMap][i].update();
                }
            }
        }
        else if(gameState == pauseState){
            //gadak
        }
        else if(gameState == inventoryState){
            // update inventory
            // ui.updateInventory();
        }
        else if(gameState == dialogueState){
            // update dialogue
            // ui.updateDialogue();
        }
        else if(gameState == sleepState){
            // update sleep state
            // ui.updateSleep();
        }
        else if(gameState == npcInteractionState) {
            // Di sini, kita akan memproses input dari menu interaksi NPC
            if (keyH.enterPressed) {
                Entity currentNpc = player.currentInteractingNPC; 
                if (currentNpc != null) {
                    int selectedOption = ui.commandNum; 

                    if (selectedOption == 0) { // Asumsi 0 adalah "Gifting"
                        // --- AWAL LOGIKA GIFTING ---
                        if (player.inventory.isEmpty()) {
                            ui.currentDialogue = "Inventory kosong. Tidak ada yang bisa diberikan.";
                            gameState = dialogueState;
                        } else {
                            // TAHAP INI MASIH MEMBUTUHKAN UI PEMILIHAN ITEM DARI INVENTORY.
                            // Untuk sekarang, kita ambil item PERTAMA dari inventory sebagai placeholder.
                            ItemStack itemToGiftStack = player.inventory.get(0); // Ambil item pertama.

                            if (itemToGiftStack == null || itemToGiftStack.getItem() == null) {
                                ui.currentDialogue = "Item yang dipilih tidak valid.";
                                gameState = dialogueState;
                                keyH.enterPressed = false; // Reset key press
                                return; // Keluar dari blok if gifting
                            }
                            
                            String itemName = itemToGiftStack.getItem().getName(); // Cara yang benar mendapatkan nama item

                            if (player.consumeEnergy(5)) { // Biaya energi -5 [cite: 163]
                                int heartPointsChange = currentNpc.processGift(itemName);
                                currentNpc.updateHeartPoints(heartPointsChange);

                                player.removeItem(itemName, 1); // Hapus 1 item dari inventory

                                // Kurangi waktu dalam game -10 menit [cite: 163]
                                gameStateSystem.tickTime(10); // Asumsi tickTime(X) memajukan X menit

                                String reaction = "";
                                if (heartPointsChange > 15) reaction = " sangat senang!"; // Loved: +25 [cite: 163]
                                else if (heartPointsChange > 0) reaction = " terlihat senang."; // Liked: +20 [cite: 163]
                                else if (heartPointsChange == 0) reaction = " menerimanya."; // Neutral: 0 [cite: 163]
                                else reaction = " terlihat tidak senang."; // Hated: -25 [cite: 163]
                                
                                ui.currentDialogue = currentNpc.name + reaction + "\n(Hati: " + currentNpc.heartPoints + "/" + currentNpc.maxHeartPoints + ")";
                                gameState = dialogueState;
                            } else {
                                ui.currentDialogue = "Energi tidak cukup untuk memberi hadiah."; // [cite: 58]
                                gameState = dialogueState;
                            }
                        }
                    } else if (selectedOption == 1) { // OPSI UNTUK "PROPOSE" / "MARRY"

                        // Langkah 1: Selalu cek apakah Player memiliki "Proposal Ring"
                        boolean hasProposalRing = false;
                        for (ItemStack stack : player.inventory) {
                            // Pastikan item dan nama item tidak null sebelum membandingkan
                            if (stack != null && stack.getItem() != null && stack.getItem().getName() != null &&
                                stack.getItem().getName().equals("Proposal Ring")) {
                                hasProposalRing = true;
                                break;
                            }
                        }

                        if (!hasProposalRing) {
                            ui.currentDialogue = "Kamu membutuhkan Cincin Lamaran untuk ini!";
                            gameState = dialogueState;
                        } else {
                            // Player memiliki cincin, lanjutkan dengan logika melamar atau menikah

                            // KASUS 1: MENIKAHI NPC YANG SUDAH MENJADI TUNANGAN (FIANCE)
                            if (currentNpc.isProposedTo && !currentNpc.isMarriedTo) {
                                // TODO: Implementasi syarat "Marry dapat dilakukan paling cepat satu hari setelah NPC tersebut menjadi fiance." [cite: 163]
                                //       Ini memerlukan penyimpanan tanggal lamaran dan perbandingan dengan tanggal saat ini.
                                //       Untuk sekarang, kita abaikan syarat ini demi kesederhanaan.

                                if (player.consumeEnergy(80)) { // Biaya energi -80 untuk menikah [cite: 163]
                                    currentNpc.isMarriedTo = true;
                                    player.partner = currentNpc.name; // Set partner pemain

                                    // Time skip ke 22.00 [cite: 163]
                                    if (gameStateSystem != null) { // Pastikan gameStateSystem tidak null
                                        gameStateSystem.setTime(22, 0); // Anda mungkin perlu membuat metode ini di GameState.java
                                    }

                                    // TODO: Implementasi "Player dikembalikan ke rumah." [cite: 163]
                                    //       Ini berarti mengubah player.worldX dan player.worldY ke koordinat rumah.
                                    //       Contoh: player.worldX = gp.tileSize * 10; player.worldY = gp.tileSize * 10; // Ganti dgn koordinat rumah

                                    ui.currentDialogue = "Kita akhirnya menikah, " + player.name + "!\nAku sangat bahagia!";
                                } else {
                                    ui.currentDialogue = "Tidak cukup energi untuk upacara pernikahan.";
                                }
                                gameState = dialogueState;

                            // KASUS 2: MELAMAR NPC (PROPOSING)
                            } else if (!currentNpc.isProposedTo) {
                                if (currentNpc.heartPoints >= 150) { // NPC harus memiliki heartPoints maksimal (150) [cite: 163]
                                    // Lamaran diterima
                                    if (player.consumeEnergy(10)) { // Biaya energi -10 jika lamaran diterima [cite: 163]
                                        currentNpc.isProposedTo = true; // NPC menjadi tunangan (fiance)

                                        // Mengurangi 1 jam in-game [cite: 163]
                                        if (gameStateSystem != null) {
                                            // Jika 1x tickTime = 5 menit, maka 1 jam = 12x tickTime
                                            // for(int i=0; i<12; i++) { gameStateSystem.tickTime(5); }
                                            // Atau lebih baik buat metode:
                                            gameStateSystem.advanceTimeByMinutes(60); // Anda mungkin perlu membuat metode ini
                                        }
                                        ui.currentDialogue = "Ya, " + player.name + "! Aku mau menikah denganmu!";
                                    } else {
                                        ui.currentDialogue = "Tidak cukup energi untuk melamar saat ini.";
                                    }
                                } else { // Heart points tidak cukup
                                    // Lamaran ditolak
                                    if (player.consumeEnergy(20)) { // Biaya energi -20 jika lamaran ditolak [cite: 163]
                                        // Mengurangi 1 jam in-game [cite: 163]
                                        if (gameStateSystem != null) {
                                            gameStateSystem.advanceTimeByMinutes(60); // Anda mungkin perlu membuat metode ini
                                        }
                                        ui.currentDialogue = "Maaf, " + player.name + "... Aku belum siap.\n(Butuh 150 hati, kini: " + currentNpc.heartPoints + ")";
                                    } else {
                                        ui.currentDialogue = "Tidak cukup energi (dan hati juga belum cukup).";
                                    }
                                }
                                gameState = dialogueState;

                            // KASUS 3: SUDAH MENIKAH DENGAN NPC INI
                            } else if (currentNpc.isMarriedTo) {
                                ui.currentDialogue = "Kita sudah menikah, sayangku " + player.name + "!";
                                gameState = dialogueState;
                            } else {
                                // Kondisi lain yang mungkin (misalnya, sudah tunangan tapi memilih opsi ini lagi sebelum menikah)
                                // Anda bisa tambahkan dialog spesifik atau biarkan default.
                                ui.currentDialogue = currentNpc.name + " tersenyum padamu.";
                                gameState = dialogueState;
                            }
                        }
                    // keyH.enterPressed = false; // Ini sudah ada di luar blok selectedOption
                    } else if (selectedOption == 2) { 
                        gameState = playState;
                        player.currentInteractingNPC = null; 
                    }
                }
                keyH.enterPressed = false; 
            }
        }
        // Update waktu otomatis setiap 1 detik
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTimeUpdate >= timeUpdateInterval) {
            gameStateSystem.tickTime(5);
            lastTimeUpdate = currentTime;
        }

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        // debug
        long drawStart = 0;
        if (keyH.showDebugText == true){
            drawStart = System.nanoTime();
        }

        // TITLE SCREEN
        if (gameState == titleState) {
            ui.draw(g2);
        }

        tileM.draw(g2); // tile

        for(int i = 0; i < obj[currentMap].length; i++){
            if(obj[currentMap][i] != null){
                obj[currentMap][i].draw(g2, this);
            }
        }

        // npc
        for(int i=0; i< npc[currentMap].length; i++){
            if(npc[currentMap][i] != null){
                npc[currentMap][i].draw(g2);
            }
        }
        
        player.draw(g2); // player

        // ui
        ui.draw(g2);

         // debug
        if(keyH.showDebugText == true){
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.setColor(Color.white);
            int x = 10; int y = 400; int lineHeight = 20;
            g2.drawString("WorldX" + player.worldX, x, y); y += lineHeight;
            g2.drawString("WorldX" + player.worldX, x, y); y += lineHeight;
            g2.drawString("Col" + (player.worldX + player.SolidAreaX)/tileSize, x, y); y += lineHeight;
            g2.drawString("Row" + (player.worldY+ player.SolidAreaY)/tileSize, x, y); y += lineHeight;
            g2.drawString("Draw Time: " + passed, x, y);
            System.out.println("Draw Time: " + passed);
        }
        // lighting/overlay
        envManager.draw(g2);

        // ui
        ui.draw(g2);

        //DEBUG
        if (keyH.showDebugText == true){
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            g2.setColor(Color.white);
            g2.drawString("Draw Time: " + passed, 10, 400);
            System.out.println("Draw Time: " + passed);
        }
        g2.dispose();
    }

    public void playMusic(int i) {
        System.out.println("DEBUG: playMusic dipanggil dengan indeks: " + i + " dari gameState: " + gameState);
        if (currentlyPlayingMusicIndex == i && musicIsPlaying) {
            // Jika musik yang diminta adalah musik yang sama dan sedang diputar, jangan lakukan apa-apa
            return;
        }
        // Jika ada musik lain yang sedang berjalan atau musik yang sama tapi ingin di-restart, hentikan dulu
        if (musicIsPlaying) {
            music.stop();
        }

        music.setFile(i);
        music.play();
        music.loop();
        currentlyPlayingMusicIndex = i;
        musicIsPlaying = true; // Tandai bahwa musik sedang diputar
    }

    public void stopMusic() {
        music.stop();
        currentlyPlayingMusicIndex = -1; // Reset pelacak
        musicIsPlaying = false; // Tandai bahwa musik berhenti
    }

    public void playSE(int i) {

        se.setFile(i);
        se.play();
    }

}
