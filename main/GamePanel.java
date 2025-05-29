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
import item.ItemRepository;
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

    private boolean pendingSleep = false;
    private String sleepTriggerMessage = "";

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
    public ItemRepository ItemRepository; // untuk mengelola item
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
    public GameState gameStateSystem = new GameState();
    public EnvironmentManager envManager = new EnvironmentManager(this);

    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        ItemRepository.initializeAllItems(this); 
        player.setInitialInventoryItems();
    }

    public void setUpGame() {
        aSetter.setObject();
        initializeNPCs();
        // playMusic(0);
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

    // Di dalam file GamePanel.java Anda

    public void update() {
        if (gameState == playState && !pendingSleep) {
            boolean autoSleepConditionMet = false;
            String autoSleepMessage = "";

            if (player.energy <= -20) {
                autoSleepMessage = "Kamu pingsan karena kelelahan!";
                autoSleepConditionMet = true;
            } else if (gameStateSystem.getTimeManager().getHour() == 2) {
                autoSleepMessage = "Sudah terlalu larut, kamu otomatis tertidur...";
                autoSleepConditionMet = true;
            }

            if (autoSleepConditionMet) {
                System.out.println(">>> AUTO-SLEEP DETECTED! Pesan: " + autoSleepMessage);
                pendingSleep = true;
                ui.currentDialogue = autoSleepMessage;
                gameState = dialogueState;
                keyH.enterPressed = false;
                System.out.println(">>> AUTO-SLEEP: pendingSleep di-set TRUE, gameState di-set ke dialogueState (" + dialogueState + ")");
            }
        }

        if (gameState == titleState) {
            if (keyH.enterPressed) {
                gameState = playState;
                keyH.enterPressed = false;
                playMusic(0);
                System.out.println(">>> TITLESCREEN: Enter ditekan, pindah ke playState (" + playState + ")");
            }
        } else if (gameState == playState) {
            player.update();
            for (int i = 0; i < npc[currentMap].length; i++) {
                if (npc[currentMap][i] != null) {
                    npc[currentMap][i].update();
                }
            }
        } else if (gameState == pauseState) {
            // Tidak ada update logika game saat pause
        } else if (gameState == inventoryState) {
            // Logika update saat inventory terbuka
        } else if (gameState == dialogueState) {
            System.out.println("--- In dialogueState --- keyH.enterPressed: " + keyH.enterPressed + ", pendingSleep: " + pendingSleep);
            if (keyH.enterPressed) {
                keyH.enterPressed = false;
                System.out.println(">>> DIALOGUE_STATE: Enter DITEKAN. Nilai pendingSleep saat ini: " + pendingSleep);
                if (pendingSleep) {
                    System.out.println(">>> DIALOGUE_STATE: Kondisi 'if (pendingSleep)' adalah TRUE. Memanggil executeSleepSequence().");
                    executeSleepSequence();
                } else {

                    if (player.currentInteractingNPC != null ) {
                        System.out.println(">>> DIALOGUE_STATE: Dialog biasa dari NPC, kembali ke npcInteractionState (" + npcInteractionState + ")");
                        gameState = npcInteractionState;
                    } else {
                        System.out.println(">>> DIALOGUE_STATE: Dialog biasa sistem, kembali ke playState (" + playState + ")");
                        gameState = playState;
                    }
                }
            }
        } else if (gameState == sleepState) {
            // Bisa untuk animasi tidur

        } else if (gameState == npcInteractionState) {
            Entity currentNpc = player.currentInteractingNPC;


            if (keyH.enterPressed) {

                if (currentNpc != null) {
                    int selectedOption = ui.commandNum;


                    if (selectedOption == 0) { // Gifting

                        if (player.inventory.isEmpty()) {

                            ui.currentDialogue = "Inventory kosong. Tidak ada yang bisa diberikan.";
                            gameState = dialogueState;
                        } else {
                            ItemStack itemToGiftStack = player.inventory.get(0);

                            if (itemToGiftStack == null || itemToGiftStack.getItem() == null) {
                                ui.currentDialogue = "Item yang dipilih tidak valid.";
                                gameState = dialogueState;
                            } else {
                                String itemName = itemToGiftStack.getItem().getName();

                                if (player.consumeEnergy(5)) { // [cite: 163]

                                    int heartPointsChange = currentNpc.processGift(itemName);
                                    currentNpc.updateHeartPoints(heartPointsChange);
                                    player.removeItem(itemName, 1);
                                    gameStateSystem.tickTime(10); // [cite: 163]
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
                        }
                    } else if (selectedOption == 1) { // Propose/Marry

                        boolean hasProposalRing = false;
                        for (ItemStack stack : player.inventory) {
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
                            if (currentNpc.isProposedTo && !currentNpc.isMarriedTo) {

                                if (player.consumeEnergy(80)) { // [cite: 163]
                                    currentNpc.isMarriedTo = true;
                                    player.partner = currentNpc.name;
                                    if (gameStateSystem != null) {
                                        gameStateSystem.setTime(22, 0); // [cite: 163]
                                    }
                                    ui.currentDialogue = "Kita akhirnya menikah, " + player.name + "!\nAku sangat bahagia!";
                                } else {
                                    ui.currentDialogue = "Tidak cukup energi untuk upacara pernikahan.";
                                }
                                gameState = dialogueState;
                            } else if (!currentNpc.isProposedTo) {

                                if (currentNpc.heartPoints >= 150) { // [cite: 163]
                                    if (player.consumeEnergy(10)) { // [cite: 163]
                                        currentNpc.isProposedTo = true;
                                        if (gameStateSystem != null) {
                                            gameStateSystem.advanceTimeByMinutes(60); // [cite: 163]
                                        }
                                        ui.currentDialogue = "Ya, " + player.name + "! Aku mau menikah denganmu!";
                                    } else {
                                        ui.currentDialogue = "Tidak cukup energi untuk melamar saat ini.";
                                    }
                                } else {
                                    if (player.consumeEnergy(20)) { // [cite: 163]
                                        if (gameStateSystem != null) {
                                            gameStateSystem.advanceTimeByMinutes(60); // [cite: 163]
                                        }
                                        ui.currentDialogue = "Maaf, " + player.name + "... Aku belum siap.\n(Butuh 150 hati, kini: " + currentNpc.heartPoints + ")";
                                    } else {
                                        ui.currentDialogue = "Tidak cukup energi (dan hati juga belum cukup).";
                                    }
                                }
                                gameState = dialogueState;
                            } else if (currentNpc.isMarriedTo) {

                                ui.currentDialogue = "Kita sudah menikah, sayangku " + player.name + "!";
                                gameState = dialogueState;
                            } else {
                                ui.currentDialogue = currentNpc.name + " tersenyum padamu.";
                                gameState = dialogueState;
                            }
                        }
                    } else if (selectedOption == 2) {
                        System.out.println("NPC INTERACTION: Opsi 'Batal' dipilih. Mengubah gameState ke playState.");
                        gameState = playState;
                        player.currentInteractingNPC = null;
                    } else {
                        System.out.println("NPC INTERACTION: selectedOption tidak dikenal: " + selectedOption + ". Tidak ada aksi.");
                    }
                }
                keyH.enterPressed = false;
            }
        }

        if (gameState == playState) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTimeUpdate >= timeUpdateInterval) {
                gameStateSystem.tickTime(5);
                lastTimeUpdate = currentTime;
            }
        }
    }

    public void executeSleepSequence() {
        System.out.println("GAME PANEL: executeSleepSequence() dipanggil.");

        int currentEnergy = player.energy;
        int maxEnergy = player.maxEnergy;
        int restoredEnergy;

        if (currentEnergy < (maxEnergy * 0.10f)) { // [cite: 163]
            restoredEnergy = maxEnergy / 2; // [cite: 163]
        } else {
            restoredEnergy = maxEnergy; // [cite: 163]
        }
        player.energy = restoredEnergy;
        if (player.energy > maxEnergy) player.energy = maxEnergy;
        System.out.println("GAME PANEL (executeSleepSequence): Energi dipulihkan menjadi: " + player.energy);

        gameStateSystem.advanceToNextMorning();
        System.out.println("GAME PANEL (executeSleepSequence): Waktu dimajukan. Info Waktu Baru: " +
                        "Hari ke-" + gameStateSystem.getTimeManager().getDay() +
                        ", Pukul " + gameStateSystem.getTimeManager().getFormattedTime());

        ui.currentDialogue = "Selamat pagi! Hari baru telah dimulai.";

        pendingSleep = false;
        System.out.println("GAME PANEL (executeSleepSequence): pendingSleep direset ke false.");

        gameState = playState;
        System.out.println("GAME PANEL (executeSleepSequence): gameState diubah ke playState.");
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
