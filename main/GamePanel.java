package main;

import java.util.Scanner;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import data.SaveLoad;
import entity.*;
import object.SuperObject;
import tile.TileManager;
import environment.GameState;
import item.ItemStack;
import item.Fish;
import item.Item;
import item.ItemRepository;
import environment.EnvironmentManager;
import item.Store;
import item.Recipe;
import item.RecipeRepository;


import main.StatisticsManager;

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
    public boolean isSelectingItemForGift = false;
    public Entity npcForGifting = null;  
    public java.util.Scanner sharedScanner;
    public boolean isCookingInProgress = false;
    public Recipe cookingRecipe = null;
    public long cookingStartTime = 0;
    public final long cookingDuration = 60*1000;

    // shipping bin
    public ArrayList<ItemStack> itemsToShip = new ArrayList<>();
    public boolean hasShippedToday = false;
    public boolean isTimePaused = false;

    // world settings
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    //Map Settings (untuk pindah Map)
    public final int maxMap = 12; // jumlah map yang ada
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
    private final int timeUpdateInterval = 1000; 


    //Untuk save & load
    SaveLoad saveLoad = new SaveLoad(this);

    // entity and object
    public Player player = new Player(this, keyH);
    public SuperObject obj[][] = new SuperObject[maxMap][10]; 
    public Entity npc[][] = new Entity[maxMap][10];
    public Store emilyStore; 

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2; 
    public final int dialogueState = 3;
    public final int sleepState = 4; 
    public final int transitionState = 5;
    public final int inventoryState = 6;
    public final int npcInteractionState = 7;
    public final int helpState = 8;
    public final int shoppingState = 9; 
    public final int fishingState = 10;
    public final int shippingBinState = 11;
    public final int characterCreationState = 12;
    public final int endGameStatisticsState = 13;
    public final int cookingState = 14; 

    // fishing
    public Fish fishBeingFished = null;
    public int fishingTargetNumber = 0;
    public int fishingAttemptsLeft = 0;
    public int fishingMinRange = 0;
    public int fishingMaxRange = 0;
    public String currentFishingGuess = ""; 
    public String fishingFeedbackMessage = "";

    public boolean tvInteractionPendingConfirmation = false;

    // untuk time
    public GameState gameStateSystem = new GameState();
    public EnvironmentManager envManager = new EnvironmentManager(this);

    // end game
    public StatisticsManager statsManager = new StatisticsManager();
    

    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        ItemRepository.initializeAllItems(this);
        emilyStore = new Store(this); 
        player.setInitialInventoryItems();
    }

    public void setUpGame() {
        ItemRepository.initializeAllItems(this);
        RecipeRepository.initializeRecipes();
        aSetter.setObject();
        initializeNPCs();
        if (statsManager != null && npc != null) {
            for (int mapIdx = 0; mapIdx < npc.length; mapIdx++) {
                if (npc[mapIdx] != null) {
                    for (int i = 0; i < npc[mapIdx].length; i++) {
                        if (npc[mapIdx][i] != null && npc[mapIdx][i].name != null) {
                            statsManager.updateNpcRelationshipStatus(npc[mapIdx][i].name, "Single");
                            statsManager.npcChattingFrequency.put(npc[mapIdx][i].name, 0);
                            statsManager.npcGiftingFrequency.put(npc[mapIdx][i].name, 0);
                            statsManager.npcVisitingFrequency.put(npc[mapIdx][i].name, 0); // Inisialisasi
                        }
                    }
                }
            }
        }
        playMusic(0);
        gameState = titleState;
    }

    public void startFishingMinigame() {
    String currentFishingLocation = "Unknown";
    boolean canFishAtCurrentSpot = false;

    // 0. DEFINISIKAN INDEKS PETA ANDA DENGAN BENAR
    final int FARM_MAP_INDEX = 0; // Sesuaikan jika indeks peta Farm Anda berbeda
    final int MOUNTAIN_LAKE_MAP_INDEX = 3; // Anda memberi nilai 3 di kode GamePanel
    final int FOREST_RIVER_MAP_INDEX = 4;  // Anda memberi nilai 4 di kode GamePanel
    final int OCEAN_MAP_INDEX = 1;         // Anda memberi nilai 1 di kode GamePanel

    // 1. PENGECEKAN SYARAT AWAL UMUM (Alat Pancing & Energi)
    //    Dilakukan sebelum menentukan spot agar lebih efisien.
    boolean hasFishingRod = false;
    for (ItemStack stack : player.inventory) {
        if (stack.getItem() != null && "Fishing Rod".equals(stack.getItem().getName())) {
            hasFishingRod = true;
            break;
        }
    }
    if (!hasFishingRod) {
        ui.setDialogue("Kamu membutuhkan Pancingan (Fishing Rod)!", "SYSTEM_MESSAGE");
        gameState = dialogueState;
        System.out.println("GAMEPANEL: Gagal memulai memancing, tidak ada Fishing Rod.");
        return;
    }

    if (!player.consumeEnergy(5)) { // consumeEnergy akan menampilkan dialog jika energi kurang
        System.out.println("GAMEPANEL: Gagal memulai memancing, energi tidak cukup.");
        return; // Keluar jika energi tidak cukup (consumeEnergy sudah set gameState ke dialogueState)
    }

    // 2. TENTUKAN APAKAH PEMAIN MENGHADAP TEMPAT MEMANCING YANG VALID
    // Prioritaskan pengecekan objek "Pond" jika di Farm Map
    if (currentMap == FARM_MAP_INDEX) {
        int objIndex = cChecker.checkObject(player, true); // Cek objek di depan pemain
        if (objIndex != 999 && obj[currentMap][objIndex] != null) {
            String objectName = obj[currentMap][objIndex].name;
            // Pastikan nama objek Pond Anda konsisten
            if (objectName.equals("Pond") || objectName.equals("OBJ_Pond") || objectName.equals("Kolam")) { // Sesuaikan
                currentFishingLocation = "Pond";
                canFishAtCurrentSpot = true;
                System.out.println("GAMEPANEL: Menghadap objek " + objectName + " (Pond) di Farm Map.");
            }
        }
    }

    // Jika bukan objek Pond (atau tidak di Farm Map), cek tile di depan pemain
    if (!canFishAtCurrentSpot) {
        int tileNumInFront = player.getTileInFront(); // Metode dari Player.java
        System.out.println("GAMEPANEL: Tile di depan pemain: " + tileNumInFront + " di peta: " + currentMap);

        if (currentMap == MOUNTAIN_LAKE_MAP_INDEX && (tileNumInFront == 259 || tileNumInFront == 260 || tileNumInFront == 261 || tileNumInFront == 262 || tileNumInFront == 263 || tileNumInFront == 264)) {
            currentFishingLocation = "Mountain Lake";
            canFishAtCurrentSpot = true;
        } else if (currentMap == FOREST_RIVER_MAP_INDEX && (tileNumInFront == 271 || tileNumInFront == 272 || tileNumInFront == 273 || tileNumInFront == 275 || tileNumInFront == 276 || tileNumInFront == 277)) {
            currentFishingLocation = "Forest River";
            canFishAtCurrentSpot = true;
        } else if (currentMap == OCEAN_MAP_INDEX && (tileNumInFront == 247 || tileNumInFront == 248 || tileNumInFront == 249 || tileNumInFront == 250 || tileNumInFront == 251)) {
            currentFishingLocation = "Ocean";
            canFishAtCurrentSpot = true;
        }
        // Anda bisa menambahkan 'else if' untuk tile air umum (misal 37) di Farm Map jika Pond tidak terdeteksi
        // else if (currentMap == FARM_MAP_INDEX && tileNumInFront == 37) {
        //     currentFishingLocation = "FarmWater"; // Atau nama lokasi air umum di farm
        //     canFishAtCurrentSpot = true;
        // }
    }

    // Jika setelah semua pengecekan tidak ada spot memancing valid
    if (!canFishAtCurrentSpot) {
        ui.setDialogue("Kamu tidak bisa memancing di sini atau tidak menghadap tempat yang benar.", "SYSTEM_MESSAGE");
        gameState = dialogueState;
        player.gainEnergy(5); // Kembalikan energi karena aksi memancing tidak jadi dimulai
        System.out.println("GAMEPANEL: Gagal memulai memancing, tidak ada spot valid. Energi dikembalikan.");
        return;
    }

    // 3. MAJUKAN WAKTU (setelah semua syarat terpenuhi dan spot valid)
    gameStateSystem.advanceTimeByMinutes(15, this.statsManager);
    System.out.println("GAMEPANEL: Memancing dimulai di " + currentFishingLocation + ". Waktu dimajukan 15 menit.");


    // 4. PENGECEKAN BATASAN WAKTU MEMANCING PER LOKASI
    int currentHour = gameStateSystem.getTimeManager().getHour();
    boolean canFishAtThisTimeAndLocation = false;
    switch (currentFishingLocation) {
        case "Mountain Lake": // Bisa dipancing hingga pukul 19:00 (sebelum jam 7 malam)
        case "Forest River":  // Bisa dipancing hingga pukul 19:00
            if (currentHour >= 6 && currentHour < 19) {
                canFishAtThisTimeAndLocation = true;
            }
            break;
        case "Ocean": // Bisa dipancing hingga pukul 17:00 (sebelum jam 5 sore)
            if (currentHour >= 6 && currentHour < 17) {
                canFishAtThisTimeAndLocation = true;
            }
            break;
        case "Pond": // Bisa kapan saja
            canFishAtThisTimeAndLocation = true;
            break;
        // case "FarmWater": // Jika Anda menambahkan lokasi air umum di farm
        //     canFishAtThisTimeAndLocation = true; // Atau beri batasan waktu sendiri
        //     break;
        default: // Seharusnya tidak terjadi karena sudah divalidasi di atas
            System.err.println("GAMEPANEL: Lokasi memancing tidak dikenal saat pengecekan waktu: " + currentFishingLocation);
            break;
    }

    if (!canFishAtThisTimeAndLocation) {
        ui.setDialogue("Kamu tidak bisa memancing di " + currentFishingLocation + " pada jam " + String.format("%02d:00", currentHour) + ".", "SYSTEM_MESSAGE");
        gameState = dialogueState;
        // Waktu sudah dimajukan, energi sudah dikonsumsi. Mungkin ini adalah konsekuensi.
        // Atau, jika ingin lebih ramah, batalkan pemajuan waktu dan kembalikan energi jika di sini gagal.
        System.out.println("GAMEPANEL: Waktu tidak valid (" + currentHour + "h) untuk memancing di " + currentFishingLocation);
        return;
    }

    // 5. DAPATKAN DAN FILTER DAFTAR IKAN
    List<Fish> allFishInGame = ItemRepository.getAllFishInstances(this); // Pastikan metode ini ada dan mengembalikan List<Fish>
    List<Fish> catchableFishToday = new ArrayList<>();
    String currentSeason = gameStateSystem.getTimeManager().getSeason().toString();
    String currentWeather = gameStateSystem.getTimeManager().getWeather().toString(); // Asumsi Enum: "SUNNY", "RAINY"

    if (allFishInGame == null || allFishInGame.isEmpty()) {
        ui.setDialogue("Ada masalah dengan data ikan...", "SYSTEM_MESSAGE");
        gameState = dialogueState;
        System.err.println("GAMEPANEL: Tidak ada data ikan di ItemRepository!");
        return;
    }

    System.out.println("GAMEPANEL: Filtering ikan untuk Lokasi: " + currentFishingLocation +
                       ", Jam: " + currentHour + ", Musim: " + currentSeason + ", Cuaca: " + currentWeather);

    for (Fish fish : allFishInGame) {
        // Asumsi metode isCatchable di Fish.java menerima parameter ini dan memiliki logika pengecekan internal
        if (fish.isCatchable(currentFishingLocation, currentHour, currentSeason, currentWeather)) {
            catchableFishToday.add(fish);
        }
    }

    if (catchableFishToday.isEmpty()) {
        ui.setDialogue("Sepertinya tidak ada ikan yang aktif di " + currentFishingLocation + " saat ini...", "SYSTEM_MESSAGE");
        gameState = dialogueState;
        System.out.println("GAMEPANEL: Tidak ada ikan yang bisa ditangkap di " + currentFishingLocation + " (Jam: " + currentHour + ", Musim: " + currentSeason + ", Cuaca: " + currentWeather + ")");
        return;
    }

    // 6. SISA LOGIKA MINIGAME (pilih ikan acak, tentukan raritas, angka target, dll.)
    java.util.Random random = new java.util.Random();
    fishBeingFished = catchableFishToday.get(random.nextInt(catchableFishToday.size()));
    System.out.println("GAMEPANEL: Ikan yang berpotensi ditangkap: " + fishBeingFished.name + " (" + fishBeingFished.getFishRarity() + ")");

    // Tentukan Rentang Angka dan Jumlah Percobaan berdasarkan raritas
    String rarity = fishBeingFished.getFishRarity().toLowerCase();
    switch (rarity) {
        case "common":
            fishingMinRange = 1; fishingMaxRange = 10;
            fishingAttemptsLeft = 10;
            break;
        case "regular":
            fishingMinRange = 1; fishingMaxRange = 100;
            fishingAttemptsLeft = 10;
            break;
        case "legendary":
            fishingMinRange = 1; fishingMaxRange = 500;
            fishingAttemptsLeft = 7;
            break;
        default:
            System.err.println("GAMEPANEL: Rarias ikan tidak dikenal - " + rarity + ". Menggunakan default Common.");
            fishingMinRange = 1; fishingMaxRange = 10;
            fishingAttemptsLeft = 10;
            break;
    }
    fishingTargetNumber = random.nextInt(fishingMaxRange - fishingMinRange + 1) + fishingMinRange;
    currentFishingGuess = "";
    fishingFeedbackMessage = "Tebak angka antara " + fishingMinRange + " dan " + fishingMaxRange + "!";
    System.out.println("GAMEPANEL: Minigame Fishing. Target Angka: " + fishingTargetNumber + ", Percobaan: " + fishingAttemptsLeft);

    gameState = fishingState; // Pindah ke fishingState
    ui.setDialogue(fishingFeedbackMessage, "FISHING_MINIGAME"); // Mode untuk UI
    System.out.println("GAMEPANEL: Pindah ke fishingState (" + fishingState + ")");
}

    public void endFishingMinigame(boolean caughtFish, String finalMessage) {
        System.out.println("GAMEPANEL: Minigame Fishing Selesai. Berhasil: " + caughtFish + ". Pesan: " + finalMessage);

        if (caughtFish && fishBeingFished != null) {

            if (statsManager != null) { // Periksa null
                statsManager.incrementFishCaught(fishBeingFished.getFishRarity());
            }
            System.out.println("STATISTICS: Fish caught: " + fishBeingFished.name + ", Rarity: " + fishBeingFished.getFishRarity());
        }
        ui.currentDialogue = finalMessage;
        gameState = dialogueState;         
        fishBeingFished = null;
        fishingTargetNumber = 0;
        fishingAttemptsLeft = 0;
        fishingMinRange = 0;
        fishingMaxRange = 0;
        currentFishingGuess = "";
        fishingFeedbackMessage = ""; 
    }

    public void finalizeAndExitShippingBin() {
        if (gameState != shippingBinState) return;
        System.out.println("GAMEPANEL: Finalisasi Shipping Bin. Jumlah item di bin: " + itemsToShip.size());
        isTimePaused = false;
        gameStateSystem.advanceTimeByMinutes(15, this.statsManager);
        hasShippedToday = true;
        if (!itemsToShip.isEmpty()) {
            ui.setDialogue("Semua item telah ditempatkan di Shipping Bin.", "SYSTEM_MESSAGE");
        } else {
            ui.setDialogue("Kamu tidak memasukkan item apapun ke Shipping Bin.", "SYSTEM_MESSAGE");
        }
        gameState = dialogueState;
        System.out.println("GAMEPANEL: Keluar dari shippingBinState. Waktu maju 15 menit. hasShippedToday=true.");
    }

    public void initializeNPCs() {

        // NPC 1 (yang sudah ada)
        npc[7][0] = new NPC_1_MayorTadi(this);
        npc[7][0].worldX = tileSize * 19; // Contoh posisi X
        npc[7][0].worldY = tileSize * 14; // Contoh posisi Y

        // NPC 2 (baru)
        npc[8][1] = new NPC_2_Caroline(this);
        npc[8][1].worldX = tileSize * 19; // Atur posisi yang berbeda
        npc[8][1].worldY = tileSize * 14;

        // NPC 3 (baru)
        npc[9][2] = new NPC_3_Perry(this);
        npc[9][2].worldX = tileSize * 19;
        npc[9][2].worldY = tileSize * 14;

        // NPC 4 (baru)
        npc[2][3] = new NPC_4_Dasco(this);
        npc[2][3].worldX = tileSize * 19;
        npc[2][3].worldY = tileSize * 14;

        npc[6][4] = new NPC_5_Emily(this);
        npc[6][4].worldX = tileSize * 15;
        npc[6][4].worldY = tileSize * 10;

        npc[10][5] = new NPC_6_Abigail(this);
        npc[10][5].worldX = tileSize * 19;
        npc[10][5].worldY = tileSize * 14;

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

            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
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
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void update() {
        boolean enterPressedThisFrame = keyH.enterPressed;
        boolean enterIsCurrentlyPressed = keyH.enterPressed;

        if (gameState == playState && !pendingSleep && !tvInteractionPendingConfirmation) {
            
            boolean autoSleepConditionMet = false;
            String autoSleepMessage = "";
            if (player.energy <= -20) {
                autoSleepMessage = "Kamu pingsan karena kelelahan!";
                autoSleepConditionMet = true;
            } else if (gameStateSystem.getTimeManager().getHour() == 2) {
                autoSleepMessage = "Sudah terlalu larut, kamu otomatis tertidur";
                autoSleepConditionMet = true;
            }
            if (autoSleepConditionMet) {
                System.out.println("GAMEPANEL: Kondisi tidur otomatis. Pesan: " + autoSleepMessage);
                pendingSleep = true;
                ui.currentDialogue = autoSleepMessage;
                gameState = dialogueState;
            }
        }

        if (gameState == titleState) {
            if (enterIsCurrentlyPressed) {
                if (ui.commandNum == 0) { 
                    gameState = playState;
                    playMusic(0);
                } 
                keyH.enterPressed = false; 
            }
        } else if (gameState == playState) {
            player.update();
            for (int i = 0; i < npc[currentMap].length; i++) {
                if (npc[currentMap][i] != null) npc[currentMap][i].update();
            }

            if (keyH.fPressed) {
                keyH.fPressed = false; // Konsumsi input
                System.out.println("GAMEPANEL (playState): Tombol F ditekan, memanggil startFishingMinigame().");
                startFishingMinigame(); // Langsung panggil, semua validasi ada di dalamnya
            }

            else if (keyH.enterPressed) {
                keyH.enterPressed = false; 
                boolean enterWasPressedForThisInteraction = keyH.enterPressed;

                if (enterWasPressedForThisInteraction) {
                    System.out.println("GAMEPANEL (playState): Enter terdeteksi untuk interaksi.");
                    System.out.println("Player Pos: X=" + player.worldX / tileSize + ", Y=" + player.worldY / tileSize + ", Dir: " + player.direction);

                    boolean interactionHandled = false;

           
                    int npcIndex = cChecker.checkEntity(player, npc);
                    if (npcIndex != 999) {
                        System.out.println("GAMEPANEL: Interaksi dengan NPC[" + npcIndex + "] terdeteksi.");
                        player.interactNPC(npcIndex); 
                        interactionHandled = true;
                    } else {
                
                        System.out.println("GAMEPANEL: Tidak ada NPC terdeteksi. Mengecek objek");
                        int objIndex = cChecker.checkObject(player, true);
                        System.out.println("GAMEPANEL: cChecker.checkObject mengembalikan objIndex: " + objIndex);

                        if (objIndex != 999) {
                            SuperObject interactedObject = obj[currentMap][objIndex];
                            if (interactedObject != null) {
                                System.out.println("GAMEPANEL: Berinteraksi dengan objek bernama: '" + interactedObject.name + "'");

                                if ("Televisi".equals(interactedObject.name)) {
                                    System.out.println("GAMEPANEL: Objek adalah 'Televisi'. Memulai konfirmasi.");
                                    tvInteractionPendingConfirmation = true;
                                    ui.setDialogue("Apakah kamu ingin menonton TV?", "TV_CONFIRM");
                                    ui.commandNum = 0;
                                    gameState = dialogueState;
                                    interactionHandled = true;
                                } else if ("Shipping Bin".equals(interactedObject.name)) { 
                                    System.out.println("GAMEPANEL: Objek adalah 'Shipping Bin'.");
                                    if (hasShippedToday) {
                                        ui.setDialogue("Kamu sudah menjual barang hari ini.", "SYSTEM_MESSAGE");
                                        gameState = dialogueState;
                                        System.out.println("GAMEPANEL: Sudah menjual hari ini.");
                                    } else {
                                        isTimePaused = true; 
                                        gameState = shippingBinState; 
                                        ui.slotCol = 0; 
                                        ui.slotRow = 0;
                                        System.out.println("GAMEPANEL: Masuk shippingBinState. Waktu dihentikan.");
                                    }
                                    interactionHandled = true;
                                } 
                                else if ("Stove".equals(interactedObject.name) || "KomporDapur".equals(interactedObject.name)) { // <<<---- INTEGRASI STOVE DI SINI
                                    // Anda bisa menggunakan "Stove" atau "KomporDapur", sesuaikan dengan nama objek Anda
                                    System.out.println("GAMEPANEL (playState): Berinteraksi dengan '" + interactedObject.name + "'. Memulai sesi memasak.");
                                    
                                    // Sesuai spesifikasi, memulai memasak butuh -10 energi (Source 217)
                                    if (player.consumeEnergy(10)) { // Anda perlu metode consumeEnergy(int cost) di Player
                                        gameState = cookingState; // Ganti ke cookingState
                                        ui.commandNum = 0; // Reset pilihan menu resep di UI (jika menggunakan commandNum)
                                                        // atau ui.cookingCommandNum = 0; jika Anda punya variabel terpisah untuk UI masak
                                        System.out.println("GAMEPANEL (playState): Masuk cookingState. Energi terkonsumsi.");
                                    } else {
                                        ui.showMessage("Tidak cukup energi untuk mulai memasak (-10 energi dibutuhkan)."); // Tampilkan pesan ke UI
                                        System.out.println("GAMEPANEL (playState): Gagal memasak, energi tidak cukup.");
                                    }
                                    interactionHandled = true;
                                }
                                
                                else {
                                    System.out.println("GAMEPANEL: Objek BUKAN 'Televisi' ataupun 'Shipping Bin'. Nama objek: '" + interactedObject.name + "'");
             
                                }
                            } else {
                                System.err.println("GAMEPANEL: ERROR - interactedObject adalah null meskipun objIndex (" + objIndex + ") bukan 999.");
                            }
                        } else {
                            System.out.println("GAMEPANEL: Tidak ada objek yang terdeteksi untuk interaksi pada posisi player saat ini.");
                        }
                    }
                }
            }

            if (enterIsCurrentlyPressed) { 
                System.out.println("GAMEPANEL (playState): Enter terdeteksi untuk interaksi.");
                int npcIndex = cChecker.checkEntity(player, npc);
                if (npcIndex != 999) {
                    player.interactNPC(npcIndex); 
                } else {
                    int objIndex = cChecker.checkObject(player, true);
                    if (objIndex != 999) {
                        SuperObject interactedObject = obj[currentMap][objIndex];
                        if (interactedObject != null && "Televisi".equals(interactedObject.name)) {
                            tvInteractionPendingConfirmation = true;
                            ui.currentDialogue = "Apakah kamu ingin menonton TV?";
                            ui.commandNum = 0;
                            gameState = dialogueState;
                        } 
                    }
                }
                keyH.enterPressed = false; 
            }
        } else if (gameState == dialogueState) {
            if (keyH.enterPressed) {
                keyH.enterPressed = false;
                System.out.println("GAMEPANEL (dialogueState): Enter DITEKAN.");
                System.out.println("                        Mode Dialog UI: '" + ui.getCurrentDialogueMode() + "'");
                System.out.println("                        NPC yang berinteraksi: " + (player.currentInteractingNPC != null ? player.currentInteractingNPC.name : "Tidak Ada"));
                System.out.println("                        Pending Sleep: " + pendingSleep);
                System.out.println("                        TV Confirm: " + tvInteractionPendingConfirmation);

                if (tvInteractionPendingConfirmation) {

                }
                else if (player.currentInteractingNPC != null && "CHAT_NPC".equals(ui.getCurrentDialogueMode())) {
                    String nextLine = player.currentInteractingNPC.getNextChatLine();

                    if (nextLine != null) {
                        ui.setDialogue(nextLine, "CHAT_NPC"); 
                    } else {
                        ui.clearDialogueMode();
                        ui.currentDialogue = ""; 
                        gameState = playState;  
                        player.currentInteractingNPC = null; 
                    }
                }
                else if (pendingSleep) {
                    executeSleepSequence();
                } else if (gameState == shippingBinState) {
                    System.out.println("GAMEPANEL: Masuk shippingBinState. Nilai enterPressedThisFrame SAAT MASUK BLOK: " + enterPressedThisFrame);

                    if (enterPressedThisFrame) { 
                        System.out.println("GAMEPANEL (shippingBinState): BLOK if (enterPressedThisFrame) TERPANGGIL!");

                        if (itemsToShip.size() >= 16) {
                            ui.showMessage("Shipping Bin sudah penuh (maks 16 slot)!");
                        } else {
                            int selectedItemIndex = ui.getSelectedItemIndex();
                            if (selectedItemIndex >= 0 && selectedItemIndex < player.inventory.size()) {
                                ItemStack stackToSell = player.inventory.get(selectedItemIndex);
                                if (stackToSell != null && stackToSell.getItem() != null) {
                                    itemsToShip.add(new ItemStack(stackToSell.getItem(), stackToSell.getQuantity()));
                                    System.out.println("GAMEPANEL (shippingBinState): Item '" + stackToSell.getItem().getName() +
                                                    "' x" + stackToSell.getQuantity() + " ditambahkan ke bin.");
                                    player.inventory.remove(selectedItemIndex);
                
                                }
                            }
                        }
                    }
                } else {
                    if (player.currentInteractingNPC != null && ui.isDialogueFromNpcAction()){ 
                        gameState = npcInteractionState;
                        ui.clearDialogueMode();
                        ui.commandNum = 0; 
                    } else {
                        gameState = playState;
                        ui.clearDialogueMode();
                        if (player.currentInteractingNPC != null && !"CHAT_NPC".equals(ui.getCurrentDialogueMode())) {
                            player.currentInteractingNPC = null; 
                        }
                    }
                }
            }
        } else if (gameState == inventoryState) {
            if (keyH.enterPressed) {
                keyH.enterPressed = false;
                System.out.println("GAMEPANEL (inventoryState): Enter DITEKAN.");

                int selectedItemIndex = ui.getSelectedItemIndex(); 

                if (isSelectingItemForGift && npcForGifting != null) { 
                    System.out.println("GAMEPANEL (inventoryState): Mode MEMILIH HADIAH AKTIF.");

                } else {
             
                    System.out.println("GAMEPANEL (inventoryState): Enter untuk penggunaan item biasa. Indeks: " + selectedItemIndex);

                    if (selectedItemIndex >= 0 && selectedItemIndex < player.inventory.size()) {
                        ItemStack stack = player.inventory.get(selectedItemIndex);
                        if (stack != null && stack.getItem() != null) {
                            Item selectedItem = stack.getItem();
                            System.out.println("INVENTORY - ENTER: Item dipilih: " + selectedItem.getName() + ", Kategori: " + selectedItem.getCategory()); // DEBUG

        
                            if (selectedItem.getCategory().equals("Fish") ||
                                selectedItem.getCategory().equals("Crop") || 
                                selectedItem.getCategory().equals("Food")) { 

                                int energyGain = selectedItem.getEnergyValue(); 

                                if (energyGain > 0) { 
                                    System.out.println("GAMEPANEL (inventoryState): Item '" + selectedItem.getName() + "' bisa dimakan. Energi: +" + energyGain);

                                    selectedItem.use(); 


                                } else {
                                    System.out.println("GAMEPANEL (inventoryState): Item '" + selectedItem.getName() + "' adalah edible tapi tidak memberi energi (getEnergyValue() = 0). Tidak dimakan.");
                                    ui.showMessage("Kamu tidak bisa memakan " + selectedItem.getName() + " saat ini.");
                      
                                }
                            } else {
                           
                                System.out.println("GAMEPANEL (inventoryState): Item '" + selectedItem.getName() + "' tidak bisa dimakan atau digunakan dengan Enter dari sini.");
                                ui.showMessage(selectedItem.getName() + " tidak bisa dimakan.");

                            }
                        } else {
                            System.out.println("INVENTORY - ENTER: ItemStack atau Item di dalamnya null pada indeks " + selectedItemIndex); // DEBUG
                            ui.showMessage("Slot yang dipilih kosong atau item bermasalah.");
                        }
                    } else {
                        ui.showMessage("Tidak ada item yang dipilih.");
                        System.out.println("INVENTORY - ENTER: selectedItemIndex tidak valid: " + selectedItemIndex); // DEBUG
                    }
                }
            }
            if (enterIsCurrentlyPressed) {
                keyH.enterPressed = false; 
                if (isSelectingItemForGift && npcForGifting != null) {
                    int selectedItemIndex = ui.getSelectedItemIndex();
                    if (selectedItemIndex >= 0 && selectedItemIndex < player.inventory.size()) {
                        ItemStack itemToGift = player.inventory.get(selectedItemIndex);
                        if (itemToGift != null && itemToGift.getItem() != null) {
                            if (player.consumeEnergy(5)) {
                                statsManager.incrementGiftingFrequency(npcForGifting.name);
                                int heartPointsChange = npcForGifting.processGift(itemToGift.getItem().getName());
                                npcForGifting.updateHeartPoints(heartPointsChange);
                                player.removeItem(itemToGift.getItem().getName(), 1);
                                gameStateSystem.advanceTimeByMinutes(10, this.statsManager);
                                String reaction = "";
                                if (heartPointsChange > 15) reaction = " sangat senang!";
                                else if (heartPointsChange > 0) reaction = " terlihat senang.";
                                else if (heartPointsChange == 0) reaction = " menerimanya.";
                                else reaction = " terlihat tidak senang.";
                                ui.currentDialogue = npcForGifting.name + reaction + "\n(Hati: " + npcForGifting.heartPoints + "/" + npcForGifting.maxHeartPoints + ")";
                            } else {
                                ui.currentDialogue = "Tidak cukup energi untuk memberi hadiah.";
                            }
                            gameState = dialogueState;
                        } else {
                            System.err.println("GAMEPANEL (inventoryState): Item yang dipilih di slot " + selectedItemIndex + " adalah null atau itemnya null.");
                        }
                    } else {
                        System.out.println("GAMEPANEL (inventoryState): Indeks item tidak valid (" + selectedItemIndex + ").");
                    }
                } else {
                    System.out.println("GAMEPANEL (inventoryState): Enter ditekan, TAPI BUKAN mode gifting ATAU npcForGifting null.");
                }
            }
        } else if (gameState == sleepState) {
            

        } else if (gameState == characterCreationState) {
    if (keyH.enterPressed) { 
        keyH.enterPressed = false; 

        System.out.println("GamePanel: Finalizing character creation.");

        // Terapkan data yang dikumpulkan dari UI ke objek player
        player.name = ui.tempPlayerName.isEmpty() ? "Petani" : ui.tempPlayerName;
        player.gender = (ui.tempGenderSelection == 0) ? "Laki-laki" : "Perempuan"; // Asumsi 0=Laki, 1=Perempuan
        player.farmName = ui.tempFarmName.isEmpty() ? "Kebunku" : ui.tempFarmName;
        if (player.favoriteItem != null) { 
            player.favoriteItem = ui.tempFavoriteItem.isEmpty() ? "None" : ui.tempFavoriteItem; 
        }

        System.out.println("Pembuatan Karakter Selesai:");
        System.out.println("Nama: " + player.name);
        System.out.println("Gender: " + player.gender);
        System.out.println("Nama Kebun: " + player.farmName);
        if (player.favoriteItem != null) {
            System.out.println("Item Favorit: " + player.favoriteItem);
        }

        ui.activeInputField = 0; 
    }
    
} else if (gameState == npcInteractionState) { 
            Entity currentNpc = player.currentInteractingNPC;
            if (keyH.enterPressed) {
                if (currentNpc != null) {
                    int selectedOption = ui.commandNum; 
                    if (currentNpc.name.equals("Emily")) {
                        // Opsi khusus untuk Emily
                        if (selectedOption == 0) { // "Belanja"
                            gameState = shoppingState; 
                        }
                        else if (selectedOption == 1) { // "Beri Hadiah"
                            if (player.inventory.isEmpty()) {
                                ui.currentDialogue = "Inventory kosong. Tidak ada yang bisa diberikan.";
                                gameState = dialogueState; 
                            } else {
                                isSelectingItemForGift = true;
                                npcForGifting = currentNpc; 
                                gameState = inventoryState;   
                                ui.slotCol = 0; 
                                ui.slotRow = 0;
                            }
                        } else if (selectedOption == 2) { // Propose/Marry
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
                                    if (player.consumeEnergy(80)) { 
                                        currentNpc.isMarriedTo = true;
                                        player.partner = currentNpc.name;
                                        if (gameStateSystem != null) {
                                            gameStateSystem.setTime(22, 0); 
                                        }
                                        ui.currentDialogue = "Kita akhirnya menikah, " + player.name + "!\nAku sangat bahagia!";
                                        statsManager.updateNpcRelationshipStatus(currentNpc.name, "Spouse");
                                    } else {
                                        ui.currentDialogue = "Tidak cukup energi untuk upacara pernikahan.";
                                    }
                                    gameState = dialogueState;
                                } else if (!currentNpc.isProposedTo) {

                                    if (currentNpc.heartPoints >= 150) { 
                                        if (player.consumeEnergy(10)) { 
                                            currentNpc.isProposedTo = true;
                                            if (gameStateSystem != null) {
                                                gameStateSystem.advanceTimeByMinutes(60, this.statsManager); 
                                            }
                                            ui.currentDialogue = "Ya, " + player.name + "! Aku mau menikah denganmu!";
                                            statsManager.updateNpcRelationshipStatus(currentNpc.name, "Fiance");
                                        } else {
                                            ui.currentDialogue = "Tidak cukup energi untuk melamar saat ini.";
                                        }
                                    } else {
                                        if (player.consumeEnergy(20)) { 
                                            if (gameStateSystem != null) {
                                                gameStateSystem.advanceTimeByMinutes(60, this.statsManager); 
                                            }
                                            ui.currentDialogue = "Maaf, " + player.name + " Aku belum siap.\n(Butuh 150 hati, kini: " + currentNpc.heartPoints + ")";
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
                        } else if (selectedOption == 3) { // "Chat"
                            if (player.consumeEnergy(10)) { 
                                gameStateSystem.advanceTimeByMinutes(10, this.statsManager); 
                                currentNpc.updateHeartPoints(10); 
                                currentNpc.startChat();
                                statsManager.incrementChatFrequency(currentNpc.name);
                                String firstChatLine = currentNpc.getNextChatLine();
                                if (firstChatLine != null) {
                                    ui.setDialogue(firstChatLine, "CHAT_NPC"); 
                                    gameState = dialogueState;
                                } else {
                                    ui.setDialogue(currentNpc.name + " tidak ingin banyak bicara saat ini.", "SYSTEM_MESSAGE");
                                    gameState = dialogueState; 
                                }
                            } else {
                                ui.setDialogue("Tidak cukup energi untuk mengobrol.", "SYSTEM_MESSAGE");
                                gameState = dialogueState;
                            }
                        }
                        else if (selectedOption == 4) { 
                            gameState = playState;
                            player.currentInteractingNPC = null;
                        }
                    } else {
                        if (selectedOption == 0) { 
                            System.out.println("NPC Interaction: Memilih 'Beri Hadiah'.");
                            if (player.inventory.isEmpty()) {
                                ui.currentDialogue = "Inventory kosong. Tidak ada yang bisa diberikan.";
                                gameState = dialogueState; 
                            } else {
                                isSelectingItemForGift = true;
                                npcForGifting = currentNpc; 
                                gameState = inventoryState;   
                                ui.slotCol = 0; 
                                ui.slotRow = 0;
                            }
                        } else if (selectedOption == 1) { 
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
                                    if (player.consumeEnergy(80)) { 
                                        currentNpc.isMarriedTo = true;
                                        player.partner = currentNpc.name;
                                        if (gameStateSystem != null) {
                                            gameStateSystem.setTime(22, 0); 
                                        }
                                        ui.currentDialogue = "Kita akhirnya menikah, " + player.name + "!\nAku sangat bahagia!";
                                    } else {
                                        ui.currentDialogue = "Tidak cukup energi untuk upacara pernikahan.";
                                    }
                                    gameState = dialogueState;
                                } else if (!currentNpc.isProposedTo) {

                                    if (currentNpc.heartPoints >= 150) { 
                                        if (player.consumeEnergy(10)) { 
                                            currentNpc.isProposedTo = true;
                                            if (gameStateSystem != null) {
                                                gameStateSystem.advanceTimeByMinutes(60, this.statsManager); 
                                            }
                                            ui.currentDialogue = "Ya, " + player.name + "! Aku mau menikah denganmu!";
                                        } else {
                                            ui.currentDialogue = "Tidak cukup energi untuk melamar saat ini.";
                                        }
                                    } else {
                                        if (player.consumeEnergy(20)) { 
                                            if (gameStateSystem != null) {
                                                gameStateSystem.advanceTimeByMinutes(60, this.statsManager); 
                                            }
                                            ui.currentDialogue = "Maaf, " + player.name + " Aku belum siap.\n(Butuh 150 hati, kini: " + currentNpc.heartPoints + ")";
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
                            System.out.println("GAMEPANEL: Memulai Chat dengan " + currentNpc.name);
                            if (player.consumeEnergy(10)) { 
                                gameStateSystem.advanceTimeByMinutes(10, this.statsManager); 
                                currentNpc.updateHeartPoints(10); 
                                statsManager.incrementChatFrequency(currentNpc.name);

                                currentNpc.startChat();
                                String firstChatLine = currentNpc.getNextChatLine();
                                if (firstChatLine != null) {
                                    ui.setDialogue(firstChatLine, "CHAT_NPC"); 
                                    gameState = dialogueState;
                                } else {
                                    ui.setDialogue(currentNpc.name + " tidak ingin banyak bicara saat ini.", "SYSTEM_MESSAGE");
                                    gameState = dialogueState; 
                                }
                            } else {
                                ui.setDialogue("Tidak cukup energi untuk mengobrol.", "SYSTEM_MESSAGE");
                                gameState = dialogueState;
                            }
                        }
                        else if (selectedOption == 3) { 
                            gameState = playState;
                            player.currentInteractingNPC = null;
                        }
                    }
                }
                keyH.enterPressed = false; 
            } else if (keyH.escapePressed) {
                keyH.escapePressed = false;
                gameState = playState;
                player.currentInteractingNPC = null;
                ui.commandNum = 0; 
            }
        } else if (gameState == shippingBinState) {
            System.out.println("GAMEPANEL: Di dalam shippingBinState. keyH.enterPressed=" + keyH.enterPressed);

            if (keyH.enterPressed) {
                keyH.enterPressed = false;
                int selectedIndexInView = ui.getSelectedItemIndexInSellableView(); 

                if (selectedIndexInView != -1) { 
                    int originalInventoryIndex = ui.sellableItemsOriginalIndices.get(selectedIndexInView);
                    ItemStack stackInPlayerInventory = player.inventory.get(originalInventoryIndex);

                    if (stackInPlayerInventory != null && stackInPlayerInventory.getItem() != null && stackInPlayerInventory.getQuantity() > 0) {
                        Item itemToSell = stackInPlayerInventory.getItem();

                
                        if (itemToSell.getSellPrice() <= 0) {
                            ui.showMessage("Item ini tidak bisa dijual.");
            
                        } else {
                            boolean itemProcessedForBin = false;
                            boolean mergedWithExistingInBin = false;
                
                            for (ItemStack stackInBin : itemsToShip) {
                                if (stackInBin.getItem().getName().equals(itemToSell.getName())) {
                                    stackInBin.addQuantity(1);
                                    mergedWithExistingInBin = true;
                                    itemProcessedForBin = true;
                                    System.out.println("GAMEPANEL (shippingBinState): Menambah jumlah '" + itemToSell.getName() + "' di bin.");
                                    break;
                                }
                            }
                            if (!mergedWithExistingInBin) {
                                if (itemsToShip.size() < 16) { 
                                    itemsToShip.add(new ItemStack(itemToSell, 1));
                                    itemProcessedForBin = true;
                                    System.out.println("GAMEPANEL (shippingBinState): Menambah item baru '" + itemToSell.getName() + "' (x1) ke bin.");
                                } else {
                                    ui.showMessage("Shipping Bin penuh untuk JENIS item baru!");
                                }
                            }
                

                            if (itemProcessedForBin) {
                                stackInPlayerInventory.removeQuantity(1);
                                System.out.println("GAMEPANEL (shippingBinState): Mengurangi 1 '" + itemToSell.getName() + "' dari inventory. Sisa: " + stackInPlayerInventory.getQuantity());
                                if (stackInPlayerInventory.getQuantity() <= 0) {
                                    player.inventory.remove(originalInventoryIndex);
                                    System.out.println("GAMEPANEL (shippingBinState): Stack '" + itemToSell.getName() + "' habis dan dihapus.");
                                }
                            
                                ui.filterSellableItemsForShipping(player);
                        
                            }
                        }
                    }
                }
            }
        } else if (gameState == shoppingState) {
            if (keyH.enterPressed) {
                keyH.enterPressed = false; // Konsumsi input Enter

                int currentSelectionInShop = ui.getShopSelectedItemIndex(); 
                int numberOfItemsForSale = emilyStore.getItemsForSale().size();
                if (currentSelectionInShop == ui.CMD_SHOP_EXIT || currentSelectionInShop == numberOfItemsForSale) {
                    gameState = npcInteractionState; 
                    ui.commandNum = 0;    
                    ui.shopCommandNum = 0;  
                }
                else if (currentSelectionInShop < numberOfItemsForSale && currentSelectionInShop >= 0) {
                    int quantityToBuy = ui.getShopSelectedQuantity();
                    if (quantityToBuy < 1) {
                        ui.currentDialogue = "Jumlah pembelian harus lebih dari 0.";
                        return;
                    }
                    if (quantityToBuy > 0) {
                        emilyStore.processPurchase(player, currentSelectionInShop + 1, quantityToBuy);
                    }
                }

            } else if (keyH.escapePressed) {
                keyH.escapePressed = false;
                gameState = npcInteractionState;
                ui.commandNum = 0;
                ui.shopCommandNum = 0;
            }
        }
        
        else if (gameState == cookingState) {
            if (isCookingInProgress) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - cookingStartTime >= cookingDuration) {
                    Item foodResult = ItemRepository.getItemByName(cookingRecipe.getOutputFoodName());
                    if (foodResult != null) {
                        player.addItemToInventory(foodResult, cookingRecipe.getOutputFoodQuantity());
                        ui.showMessage(cookingRecipe.getOutputFoodName() + " berhasil dimasak!");
                        gameStateSystem.advanceTimeByMinutes(60, statsManager); // Advance time by 1 hour
                        System.out.println("COOKING - " + cookingRecipe.getOutputFoodName() + " selesai dimasak!");
                    } else {
                        ui.showMessage("Error: Makanan hasil resep tidak ditemukan!");
                        System.err.println("COOKING ERROR: Output food '" + cookingRecipe.getOutputFoodName() + "' not found.");
                    }
                    isCookingInProgress = false;
                    cookingRecipe = null;
                    gameState = playState;
                } else {
                    // Show progress in UI
                    double progress = (double) (currentTime - cookingStartTime) / cookingDuration;
                    ui.showMessage("Memasak " + cookingRecipe.getOutputFoodName() + "... (" + (int) (progress * 100) + "%)");
                }
            } else if (keyH.enterPressed) {
                keyH.enterPressed = false;

                List<Recipe> knownRecipes = new ArrayList<>();
                for (String recipeId : player.getLearnedRecipeIds()) {
                    Recipe r = RecipeRepository.getRecipeById(recipeId);
                    if (r != null) knownRecipes.add(r);
                }

                int selectedOption = ui.commandNum;
                if (knownRecipes.isEmpty()) {
                    if (selectedOption == 0) { 
                        gameState = playState;
                        System.out.println("COOKING - Kembali ke playState (tidak ada resep).");
                    }
                } else {
                    if (selectedOption < knownRecipes.size()) { // Pilih resep
                        Recipe recipeToCook = knownRecipes.get(selectedOption);
                        System.out.println("COOKING - Mencoba memasak: " + recipeToCook.getOutputFoodName());
                        String ingredientCheckMessage = player.checkEnoughIngredients(recipeToCook);
                        if (ingredientCheckMessage != null) {
                            ui.showMessage(ingredientCheckMessage);
                        } else {
                            String availableFuel = player.getAvailableFuel();
                            if (availableFuel == null) {
                                ui.showMessage("Tidak ada bahan bakar (Kayu/Arang)!");
                            } else {
                                // Start cooking
                                player.consumeIngredients(recipeToCook);
                                player.consumeFuel(availableFuel);
                                isCookingInProgress = true;
                                cookingRecipe = recipeToCook;
                                cookingStartTime = System.currentTimeMillis();
                                ui.showMessage("Memulai memasak " + recipeToCook.getOutputFoodName() + "...");
                                System.out.println("COOKING - Memulai memasak: " + recipeToCook.getOutputFoodName());
                            }
                        }
                    } else if (selectedOption == knownRecipes.size()) { // Kembali
                        gameState = playState;
                        System.out.println("COOKING - Kembali ke playState.");
                    }
                }
            } else if (keyH.escapePressed) {
                keyH.escapePressed = false;
                if (isCookingInProgress) {
                    ui.showMessage("Memasak dibatalkan!");
                    isCookingInProgress = false;
                    cookingRecipe = null;
                }
                gameState = playState;
                ui.commandNum = 0;
                System.out.println("COOKING - Escape, kembali ke playState.");
            }
}

        else if (gameState == inventoryState) {
            if (keyH.enterPressed) {
                keyH.enterPressed = false; 

                int selectedItemIndex = ui.getSelectedItemIndex(); 
                if (isSelectingItemForGift && npcForGifting != null) {
                    if (selectedItemIndex >= 0 && selectedItemIndex < player.inventory.size()) { 
                        ItemStack itemToGift = player.inventory.get(selectedItemIndex); 
                        if (itemToGift != null && itemToGift.getItem() != null) {
                            if (player.consumeEnergy(5)) {
                                int heartPointsChange = npcForGifting.processGift(itemToGift.getItem().getName());
                                npcForGifting.updateHeartPoints(heartPointsChange);
                                player.removeItem(itemToGift.getItem().getName(), 1);
                                gameStateSystem.advanceTimeByMinutes(10, this.statsManager);

                                String reaction = "";
                                if (heartPointsChange > 15) reaction = " sangat senang!";
                                else if (heartPointsChange > 0) reaction = " terlihat senang.";
                                else if (heartPointsChange == 0) reaction = " menerimanya.";
                                else reaction = " terlihat tidak senang.";
                                ui.currentDialogue = npcForGifting.name + reaction + "\n(Hati: " + npcForGifting.heartPoints + "/" + npcForGifting.maxHeartPoints + ")";
                            } else {
                                ui.currentDialogue = "Tidak cukup energi untuk memberi hadiah.";
                            }
                            gameState = dialogueState;
                        } 
                    }
                } 
            }
        } else if (gameState == fishingState) {
            if (keyH.enterPressed) {
                keyH.enterPressed = false; 
                if (fishBeingFished == null) {
                    endFishingMinigame(false, "Terjadi kesalahan saat memancing."); // Keluar dari minigame
                    return;
                }
                if (currentFishingGuess.isEmpty()) {
                    fishingFeedbackMessage = "Masukkan tebakanmu dulu!";
                } else {
                    try {
                        int guess = Integer.parseInt(currentFishingGuess);
                        fishingAttemptsLeft--;
                        if (guess == fishingTargetNumber) {
                            fishingFeedbackMessage = "BERHASIL! Kamu menangkap " + fishBeingFished.name + "!";
                            player.inventory.add(new ItemStack(fishBeingFished, 1)); 
                            endFishingMinigame(true, fishingFeedbackMessage);
                        } else if (fishingAttemptsLeft <= 0) {
                            fishingFeedbackMessage = "GAGAL! Kesempatan habis. Angka sebenarnya: " + fishingTargetNumber + ".";
                            endFishingMinigame(false, fishingFeedbackMessage);
                        } else {
                            if (guess < fishingTargetNumber) {
                                fishingFeedbackMessage = "Terlalu RENDAH! Sisa percobaan: " + fishingAttemptsLeft;
                            } else {
                                fishingFeedbackMessage = "Terlalu TINGGI! Sisa percobaan: " + fishingAttemptsLeft;
                            }
                        }
                    } catch (NumberFormatException e) {
                        fishingFeedbackMessage = "Input angka tidak valid! Coba lagi.";
                    }
                    currentFishingGuess = ""; 
                }
            }
        }
        
        else if (gameState == cookingState) {
            List<Recipe> learnedRecipes = new ArrayList<>();
            for (String recipeId : player.getLearnedRecipeIds()) {
                Recipe recipe = RecipeRepository.getRecipeById(recipeId);
                if (recipe != null) {
                    learnedRecipes.add(recipe);
                }
            }

            ui.slotCol = 0;
            final int maxRowsOnScreen = 5; 
            int totalRows = learnedRecipes.size();

            if (keyH.upPressed) {
                keyH.upPressed = false;
                ui.slotRow--;
                if (ui.slotRow < 0) {
                    ui.slotRow = 0;
                }
                if (ui.slotRow < ui.recipeScrollOffset) {
                    ui.recipeScrollOffset = ui.slotRow;
                }
                System.out.println("COOKING: Pilih resep ke atas, slotRow: " + ui.slotRow + ", scrollOffset: " + ui.recipeScrollOffset);
            }
            if (keyH.downPressed) {
                keyH.downPressed = false;
                ui.slotRow++;
                if (ui.slotRow >= totalRows) {
                    ui.slotRow = totalRows - 1;
                }
                if (ui.slotRow >= ui.recipeScrollOffset + maxRowsOnScreen) {
                    ui.recipeScrollOffset = ui.slotRow - maxRowsOnScreen + 1;
                }
                System.out.println("COOKING: Pilih resep ke bawah, slotRow: " + ui.slotRow + ", scrollOffset: " + ui.recipeScrollOffset);
            }
            if (keyH.enterPressed) {
                keyH.enterPressed = false;
                if (learnedRecipes.isEmpty()) {
                    ui.currentDialogue = "Kamu belum tahu resep apa pun!";
                    gameState = dialogueState;
                    System.out.println("COOKING: Tidak ada resep yang dipelajari.");
                    return;
                }

                int selectedIndex = ui.slotRow;
                if (selectedIndex >= 0 && selectedIndex < learnedRecipes.size()) {
                    Recipe selectedRecipe = learnedRecipes.get(selectedIndex);
                    System.out.println("COOKING: Resep dipilih: " + selectedRecipe.getOutputFoodName());

                    String ingredientCheck = player.checkEnoughIngredients(selectedRecipe);
                    if (ingredientCheck != null) {
                        ui.currentDialogue = ingredientCheck;
                        gameState = dialogueState;
                        System.out.println("COOKING: Bahan tidak cukup - " + ingredientCheck);
                        return;
                    }
                    String fuelAvailable = player.getAvailableFuel();
                    if (!player.canCookWithCurrentFuel()) {
                        ui.currentDialogue = "Kamu tidak punya bahan bakar (Firewood atau Coal)!";
                        gameState = dialogueState;
                        System.out.println("COOKING: Tidak ada bahan bakar.");
                        return;
                    }
                    if (!player.consumeEnergy(10)) {
                        ui.currentDialogue = "Energi tidak cukup untuk memasak!";
                        gameState = dialogueState;
                        System.out.println("COOKING: Energi tidak cukup.");
                        return;
                    }
                    player.consumeIngredients(selectedRecipe);
                    player.consumeFuel(fuelAvailable);
                    Item cookedItem = ItemRepository.getItemByName(selectedRecipe.getOutputFoodName());
                    if (cookedItem != null) {
                        player.addItemToInventory(cookedItem, 1);
                        ui.currentDialogue = "Kamu memasak " + selectedRecipe.getOutputFoodName() + "!";
                        System.out.println("COOKING: Berhasil memasak: " + selectedRecipe.getOutputFoodName());
                    } else {
                        ui.currentDialogue = "Error: Item masakan tidak ditemukan!";
                        System.out.println("COOKING: Error - Item masakan tidak ditemukan: " + selectedRecipe.getOutputFoodName());
                    }
                    gameState = dialogueState;
                }
            }
            if (keyH.escapePressed) {
                keyH.escapePressed = false;
                gameState = playState;
                ui.slotRow = 0;
                ui.recipeScrollOffset = 0;
                System.out.println("COOKING: Keluar dari cookingState, kembali ke playState.");
            }
        }

        if (gameState == playState) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTimeUpdate >= timeUpdateInterval) {
                gameStateSystem.tickTime(5, statsManager);
                lastTimeUpdate = currentTime;
            }
        }

        if (gameState == playState && !statsManager.endGameStatsShown) { // Hanya cek jika statistik belum ditampilkan
            boolean milestoneReached = false;
            if (player.gold >= 17209) { // [cite: 196]
                milestoneReached = true;
                System.out.println("GAMEPANEL: Milestone Gold Tercapai!");
            }
            // Asumsi Player memiliki field 'partner' (String) atau 'isMarried' (boolean)
            // dan diupdate saat menikah.
            if (player.partner != null && !player.partner.equalsIgnoreCase("None") && !player.partner.isEmpty()) { // [cite: 196]
                // Atau jika Anda punya boolean player.isMarried
                milestoneReached = true;
                System.out.println("GAMEPANEL: Milestone Menikah Tercapai!");
            }

            if (milestoneReached) {
                System.out.println("GAMEPANEL: Salah satu milestone tercapai. Menampilkan End Game Statistics.");
                statsManager.endGameStatsShown = true; // Tandai agar tidak ditampilkan lagi
                // Simpan state sebelumnya jika perlu kembali setelah statistik
                // int previousGameState = gameState; // Mungkin tidak perlu jika selalu kembali ke playState
                gameState = endGameStatisticsState; // Pindah ke state baru untuk menampilkan statistik
                // Anda mungkin ingin menghentikan musik playState dan memainkan musik khusus
                // stopMusic();
                // playMusic(MUSIK_STATISTIK_END_GAME);
            }
        }
        // ...

        // Tambahkan penanganan untuk state baru
        else if (gameState == endGameStatisticsState) {
            // Logika untuk state ini (misalnya, menunggu input untuk kembali ke playState)
            if (keyH.enterPressed || keyH.escapePressed) {
                keyH.enterPressed = false;
                keyH.escapePressed = false; // Jika Anda pakai flag escape
                System.out.println("GAMEPANEL: Keluar dari End Game Statistics. Kembali ke PlayState.");
                gameState = playState; // Kembali ke permainan
                // playMusic(MUSIK_PLAYSTATE_UTAMA); // Mainkan kembali musik playState
            }
        }
    }

    public java.util.Scanner getSharedScanner() {
        if (sharedScanner == null) {
            sharedScanner = new java.util.Scanner(System.in);
        }
        return sharedScanner;
    }

    public void executeSleepSequence() {
        int currentEnergy = player.energy;
        int maxEnergy = player.maxEnergy;
        int restoredEnergy;
        if (currentEnergy < (maxEnergy * 0.10f)) { restoredEnergy = maxEnergy / 2; }
        else { restoredEnergy = maxEnergy; }
        player.energy = restoredEnergy;
        if (player.energy > maxEnergy) player.energy = maxEnergy;

        // --- PROSES PENJUALAN DARI SHIPPING BIN --- 
        int goldEarnedToday = 0;
        if (!itemsToShip.isEmpty()) {
            System.out.println("GAMEPANEL: Memproses penjualan " + itemsToShip.size() + " jenis item dari bin");
            for (ItemStack shippedStack : itemsToShip) {
                if (shippedStack != null && shippedStack.getItem() != null) {
                    int itemSellValue = shippedStack.getItem().getSellPrice() * shippedStack.getQuantity();
                    goldEarnedToday += itemSellValue;
                    System.out.println("  - Menjual " + shippedStack.getItem().getName() + " x" + shippedStack.getQuantity() + " seharga " + itemSellValue + "G");
                }
            }
            
            player.changeGold(goldEarnedToday);
            itemsToShip.clear(); 
        } else {
            System.out.println("GAMEPANEL: Tidak ada item yang dijual semalam.");
        }

        hasShippedToday = false; 

        String seasonBeforeSleep = gameStateSystem.getTimeManager().getSeason().toString(); // Menggunakan toString()

        gameStateSystem.getTimeManager().advanceToNextMorning(this.statsManager); // Teruskan statsManager ke advanceToNextMorning

        statsManager.incrementDaysPlayed();
        
        String morningMessage = "Selamat pagi! Hari baru telah dimulai.";
        if (goldEarnedToday > 0) {
            statsManager.addIncome(goldEarnedToday);
            morningMessage += "\nKamu mendapatkan " + goldEarnedToday + "G dari penjualan semalam!";
        }
        ui.setDialogue(morningMessage, "SYSTEM_MESSAGE");
        pendingSleep = false;
        gameState = dialogueState;
        System.out.println("GAMEPANEL (executeSleepSequence): Selesai. pendingSleep=false. gameState=" + gameState);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        // Gambar objek HANYA untuk map yang sedang aktif
        // Pastikan currentMap tidak melebihi batas array gp.obj
        if (currentMap >= 0 && currentMap < obj.length && obj[currentMap] != null) {
            for (int i = 0; i < obj[currentMap].length; i++) {
                if (obj[currentMap][i] != null) {
                    obj[currentMap][i].draw(g2, this);
                }
            }
        }

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

        // DEBUG
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
            return;
        }
        if (musicIsPlaying) {
            music.stop();
        }
        music.setFile(i);
        music.play();
        music.loop();
        currentlyPlayingMusicIndex = i;
        musicIsPlaying = true;
    }

    public void stopMusic() {
        music.stop();
        currentlyPlayingMusicIndex = -1;
        musicIsPlaying = false; 
    }

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }
}
