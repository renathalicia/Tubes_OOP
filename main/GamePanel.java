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
    public boolean isSelectingItemForGift = false; // Flag untuk mode pilih item hadiah
    public Entity npcForGifting = null;  
    private java.util.Scanner sharedScanner;

    // shipping bin
    public ArrayList<ItemStack> itemsToShip = new ArrayList<>();
    public boolean hasShippedToday = false;
    public boolean isTimePaused = false;

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
    public Store emilyStore; 
    // public ItemRepository ItemRepository;

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
    
    public final int shoppingState = 9; 
    public final int helpState = 8; // untuk help
    public final int fishingState = 10;
    public final int shippingBinState = 11;

    // fishing
    public Fish fishBeingFished = null;
    public int fishingTargetNumber = 0;
    public int fishingAttemptsLeft = 0;
    public int fishingMinRange = 0;
    public int fishingMaxRange = 0;
    public String currentFishingGuess = ""; // Untuk menampung input angka dari pemain
    public String fishingFeedbackMessage = "";

    public boolean tvInteractionPendingConfirmation = false;

    // untuk time
    public GameState gameStateSystem = new GameState();
    public EnvironmentManager envManager = new EnvironmentManager(this);

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
        aSetter.setObject();
        initializeNPCs();
        playMusic(0);
        gameState = titleState;
    }

    public void startFishingMinigame() {
        // Spesifikasi: Waktu dunia dihentikan, tambah 15 menit, baru berlanjut setelah selesai. [cite: 195]
        // Untuk implementasi awal, kita cukup majukan waktu. Penghentian waktu bisa ditambahkan nanti.
        gameStateSystem.advanceTimeByMinutes(15);
        System.out.println("GAMEPANEL: Waktu dimajukan 15 menit untuk memancing.");

        // A. Tentukan Lokasi Memancing (berdasarkan currentMap dan mungkin posisi player)
        // Untuk sekarang, kita asumsikan map 0 adalah satu-satunya tempat memancing yang valid.
        // Anda perlu Enum atau String untuk merepresentasikan lokasi.
        String currentLocationString = "Default_Location"; // Placeholder
        if (currentMap == 0) { // Asumsi map 0 punya area Pond, Forest River, atau Mountain Lake
            // Logika lebih detail untuk menentukan apakah ini Pond, Forest River, atau Mountain Lake
            // berdasarkan posisi player di map 0 atau jenis tile air yang dihadapi.
            // Untuk sekarang, kita hardcode satu.
            currentLocationString = "Pond"; // Contoh, atau "Mountain Lake" jika semua air di map 0 itu
        } else {
            // Logika untuk peta lain (Ocean, dll.) jika currentMap bukan 0
            // Untuk sekarang, kita hanya fokus map 0.
            System.out.println("GAMEPANEL: Saat ini hanya bisa memancing di map 0.");
            ui.currentDialogue = "Kamu hanya bisa memancing di area tertentu di peta utama.";
            gameState = dialogueState;
            return;
        }

        // B. Dapatkan Daftar Ikan yang Bisa Ditangkap
        List<Fish> catchableFishToday = new ArrayList<>();
        // Anda perlu mengakses ItemRepository atau FishDatabase Anda
        // Untuk contoh, kita asumsikan ada ItemRepository.getAllFish() yang mengembalikan List<Fish>
        // List<Fish> allFish = ItemRepository.getAllFishInstances(this); // Metode hipotetis
        // if (allFish == null) {
        //     System.err.println("GAMEPANEL: Daftar semua ikan belum diinisialisasi di ItemRepository!");
        //     ui.currentDialogue = "Ada masalah dengan data ikan...";
        //     gameState = dialogueState;
        //     return;
        // }

        // Filter ikan berdasarkan musim, waktu, cuaca, dan lokasi saat ini
        // Season currentSeasonEnum = Season.valueOf(gameStateSystem.getTimeManager().getSeason().toUpperCase()); // Konversi String ke Enum
        // Weather currentWeatherEnum = Weather.valueOf(gameStateSystem.getTimeManager().getWeather().toUpperCase());
        // FishingLocation currentFishingLocationEnum = FishingLocation.valueOf(currentLocationString.toUpperCase());
        // int currentHour = gameStateSystem.getTimeManager().getHour();

        // for (Fish fish : allFish) {
        //     if (fish.isCatchable(currentSeasonEnum, currentHour, currentWeatherEnum, currentFishingLocationEnum)) {
        //         catchableFishToday.add(fish);
        //     }
        // }

        // --- UNTUK SEMENTARA, GUNAKAN IKAN DUMMY AGAR BISA DIUJI ---
        // Ganti ini dengan logika filter ikan Anda yang sebenarnya nanti.
        // Pastikan Anda sudah menginisialisasi ikan di ItemRepository.
        if (ItemRepository.Bullhead != null) catchableFishToday.add(ItemRepository.Bullhead);
        if (ItemRepository.Carp != null) catchableFishToday.add(ItemRepository.Carp);
        if (ItemRepository.Largemouth_Bass != null) catchableFishToday.add(ItemRepository.Largemouth_Bass);
        // --- AKHIR BAGIAN DUMMY ---


        if (catchableFishToday.isEmpty()) {
            ui.currentDialogue = "Sepertinya tidak ada ikan yang aktif saat ini...";
            gameState = dialogueState;
            System.out.println("GAMEPANEL: Tidak ada ikan yang bisa ditangkap saat ini.");
            return;
        }

        // C. Pilih Satu Ikan Secara Acak
        java.util.Random random = new java.util.Random();
        fishBeingFished = catchableFishToday.get(random.nextInt(catchableFishToday.size()));
        System.out.println("GAMEPANEL: Ikan yang berpotensi ditangkap: " + fishBeingFished.name + " (" + fishBeingFished.getFishRarity() + ")");


        // D. Tentukan Rentang Angka dan Jumlah Percobaan
        String rarity = fishBeingFished.getFishRarity().toLowerCase(); // Ambil raritas dari Fish.java
        switch (rarity) {
            case "common":
                fishingMinRange = 1; fishingMaxRange = 10;
                fishingAttemptsLeft = 10; // Maks. 10 percobaan [cite: 195]
                break;
            case "regular":
                fishingMinRange = 1; fishingMaxRange = 100;
                fishingAttemptsLeft = 10; // Maks. 10 percobaan [cite: 195]
                break;
            case "legendary":
                fishingMinRange = 1; fishingMaxRange = 500;
                fishingAttemptsLeft = 7; // Maks. 7 percobaan [cite: 195]
                break;
            default: // Fallback jika raritas tidak dikenal
                System.err.println("GAMEPANEL: Rarias ikan tidak dikenal - " + rarity + ". Menggunakan default Common.");
                fishingMinRange = 1; fishingMaxRange = 10;
                fishingAttemptsLeft = 10;
                break;
        }
        fishingTargetNumber = random.nextInt(fishingMaxRange - fishingMinRange + 1) + fishingMinRange;
        currentFishingGuess = ""; // Kosongkan input tebakan
        fishingFeedbackMessage = "Tebak angka antara " + fishingMinRange + " dan " + fishingMaxRange + "!";

        System.out.println("GAMEPANEL: Minigame Fishing. Target Angka: " + fishingTargetNumber + ", Percobaan: " + fishingAttemptsLeft);

        // E. Pindah ke fishingState
        gameState = fishingState;
        // ui.commandNum = 0; // Tidak ada commandNum untuk fishingState ini, tapi input angka
        ui.setDialogue(fishingFeedbackMessage, "FISHING_MINIGAME"); // Set pesan awal dan mode
        System.out.println("GAMEPANEL: Pindah ke fishingState (" + fishingState + ")");
    }

    public void endFishingMinigame(boolean caughtFish, String finalMessage) {
        System.out.println("GAMEPANEL: Minigame Fishing Selesai. Berhasil: " + caughtFish + ". Pesan: " + finalMessage);

        ui.currentDialogue = finalMessage; // Tampilkan pesan hasil akhir
        // ui.setDialogueMode("SYSTEM_MESSAGE"); // Set mode dialog jika Anda menggunakannya
        gameState = dialogueState;         // Pindah ke dialogueState untuk menampilkan pesan

        // Reset variabel terkait memancing
        fishBeingFished = null;
        fishingTargetNumber = 0;
        fishingAttemptsLeft = 0;
        fishingMinRange = 0;
        fishingMaxRange = 0;
        currentFishingGuess = "";
        fishingFeedbackMessage = ""; // Kosongkan juga ini untuk sesi berikutnya

        // Lanjutkan waktu dunia (jika sebelumnya Anda mengimplementasikan stopTime())
        // gameStateSystem.resumeTime();
        // Untuk sekarang, kita asumsikan advanceTimeByMinutes(15) di startFishingMinigame sudah mencakup 'biaya waktu'.
        // Jika waktu benar-benar dihentikan, Anda perlu mekanisme untuk melanjutkannya.
    }

    public void finalizeAndExitShippingBin() {
        if (gameState != shippingBinState) return;
        System.out.println("GAMEPANEL: Finalisasi Shipping Bin. Jumlah item di bin: " + itemsToShip.size());
        isTimePaused = false;
        gameStateSystem.advanceTimeByMinutes(15);
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
        int mapNum = 0;

        npc[mapNum][0] = new NPC_1_MayorTadi(this);
        npc[mapNum][0].worldX = tileSize * 21;
        npc[mapNum][0].worldY = tileSize * 21; 

        npc[mapNum][1] = new NPC_2_Caroline(this);
        npc[mapNum][1].worldX = tileSize * 25; 
        npc[mapNum][1].worldY = tileSize * 21;

        npc[mapNum][2] = new NPC_3_Perry(this);
        npc[mapNum][2].worldX = tileSize * 28;
        npc[mapNum][2].worldY = tileSize * 21;

        npc[mapNum][3] = new NPC_4_Dasco(this);
        npc[mapNum][3].worldX = tileSize * 17;
        npc[mapNum][3].worldY = tileSize * 21;

        npc[mapNum][4] = new NPC_5_Emily(this);
        npc[mapNum][4].worldX = tileSize * 13;
        npc[mapNum][4].worldY = tileSize * 21;

        npc[mapNum][5] = new NPC_6_Abigail(this);
        npc[mapNum][5].worldX = tileSize * 23;
        npc[mapNum][5].worldY = tileSize * 25;

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
                autoSleepMessage = "Sudah terlalu larut, kamu otomatis tertidur...";
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

            if (keyH.fPressed) { // Atau bisa juga bagian dari logika Enter jika pemain menghadap air
                keyH.fPressed = false; // Konsumsi input
                System.out.println("GAMEPANEL (playState): Tombol F ditekan untuk memancing.");

                // 1. Cek apakah punya Fishing Rod
                boolean hasFishingRod = false;
                for (ItemStack stack : player.inventory) {
                    if (stack.getItem() != null && "Fishing Rod".equals(stack.getItem().getName())) {
                        hasFishingRod = true;
                        break;
                    }
                }

                if (!hasFishingRod) {
                    ui.currentDialogue = "Kamu membutuhkan Pancingan (Fishing Rod)!";
                    // ui.setDialogueMode("SYSTEM_MESSAGE"); // Jika Anda pakai sistem mode
                    gameState = dialogueState;
                    System.out.println("GAMEPANEL: Gagal memancing, tidak ada Fishing Rod.");
                } else {
                    // 2. Cek apakah pemain menghadap tile air yang valid (kode 12) di map saat ini
                    if (player.isFacingWaterTile()) { // Panggil metode dari Player.java
                        // 3. Cek Energi
                        if (player.consumeEnergy(5)) { // Biaya -5 energi per attempt [cite: 188]
                            System.out.println("GAMEPANEL: Memulai aksi Fishing...");
                            startFishingMinigame(); // Metode baru untuk memulai minigame
                        } else {
                            ui.currentDialogue = "Tidak cukup energi untuk memancing.";
                            // ui.setDialogueMode("SYSTEM_MESSAGE");
                            gameState = dialogueState;
                            System.out.println("GAMEPANEL: Gagal memancing, energi tidak cukup.");
                        }
                    } else {
                        ui.currentDialogue = "Kamu tidak bisa memancing di sini.";
                        // ui.setDialogueMode("SYSTEM_MESSAGE");
                        gameState = dialogueState;
                        System.out.println("GAMEPANEL: Gagal memancing, tidak menghadap air.");
                    }
                }
            }

            else if (keyH.enterPressed) {
                keyH.enterPressed = false; // Konsumsi Enter di awal untuk interaksi playState
                boolean enterWasPressedForThisInteraction = keyH.enterPressed;

                if (enterWasPressedForThisInteraction) {
                    System.out.println("GAMEPANEL (playState): Enter terdeteksi untuk interaksi.");
                    System.out.println("Player Pos: X=" + player.worldX / tileSize + ", Y=" + player.worldY / tileSize + ", Dir: " + player.direction);

                    boolean interactionHandled = false;

                    // 1. Prioritaskan Interaksi NPC
                    int npcIndex = cChecker.checkEntity(player, npc);
                    if (npcIndex != 999) {
                        System.out.println("GAMEPANEL: Interaksi dengan NPC[" + npcIndex + "] terdeteksi.");
                        player.interactNPC(npcIndex); // Ini akan set gameState = npcInteractionState
                        interactionHandled = true;
                    } else {
                        // 2. Jika tidak ada NPC, baru cek Interaksi Objek
                        System.out.println("GAMEPANEL: Tidak ada NPC terdeteksi. Mengecek objek...");
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
                                } else if ("Shipping Bin".equals(interactedObject.name)) { // <<<---- INI LOGIKA SHIPPING BIN
                                    System.out.println("GAMEPANEL: Objek adalah 'Shipping Bin'.");
                                    if (hasShippedToday) {
                                        ui.setDialogue("Kamu sudah menjual barang hari ini.", "SYSTEM_MESSAGE");
                                        gameState = dialogueState;
                                        System.out.println("GAMEPANEL: Sudah menjual hari ini.");
                                    } else {
                                        isTimePaused = true; // Hentikan waktu game
                                        gameState = shippingBinState; // Pindah ke state Shipping Bin
                                        ui.slotCol = 0; // Reset kursor inventory untuk layar bin
                                        ui.slotRow = 0;
                                        System.out.println("GAMEPANEL: Masuk shippingBinState. Waktu dihentikan.");
                                    }
                                    interactionHandled = true;
                                } else {
                                    System.out.println("GAMEPANEL: Objek BUKAN 'Televisi' ataupun 'Shipping Bin'. Nama objek: '" + interactedObject.name + "'");
                                    // Logika untuk interaksi objek lain jika ada
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

            if (enterIsCurrentlyPressed) { // Gunakan variabel lokal yang menangkap status Enter di awal frame
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
                    System.out.println("GAMEPANEL (dialogueState): Melanjutkan mode CHAT_NPC dengan " + player.currentInteractingNPC.name);
                    String nextLine = player.currentInteractingNPC.getNextChatLine();

                    if (nextLine != null) {
                        ui.setDialogue(nextLine, "CHAT_NPC"); 
                        System.out.println("GAMEPANEL (dialogueState): Menampilkan baris chat berikutnya: '" + nextLine + "'");
                    } else {
                        System.out.println("GAMEPANEL (dialogueState): Tidak ada baris chat berikutnya dari " + player.currentInteractingNPC.name + ". Chat selesai.");
                        ui.clearDialogueMode();
                        ui.currentDialogue = ""; 
                        gameState = playState;  
                        player.currentInteractingNPC = null; 
                        System.out.println("GAMEPANEL (dialogueState): Kembali ke playState. currentInteractingNPC direset.");
                    }
                }
                else if (pendingSleep) {
                    executeSleepSequence();
                } else if (gameState == shippingBinState) {
                    System.out.println("GAMEPANEL: Masuk shippingBinState. Nilai enterPressedThisFrame SAAT MASUK BLOK: " + enterPressedThisFrame);

                    if (enterPressedThisFrame) { // Gunakan status Enter yang sudah ditangkap
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
                                    // Logika reset kursor UI jika perlu
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("GAMEPANEL (dialogueState): Menangani dialog biasa/sistem lainnya.");
                    if (player.currentInteractingNPC != null && ui.isDialogueFromNpcAction()){ 
                        System.out.println("GAMEPANEL: Dialog hasil aksi NPC, kembali ke npcInteractionState.");
                        gameState = npcInteractionState;
                        ui.clearDialogueMode();
                        ui.commandNum = 0; 
                    } else {
                        System.out.println("GAMEPANEL: Dialog umum selesai, kembali ke playState.");
                        gameState = playState;
                        ui.clearDialogueMode();
                        if (player.currentInteractingNPC != null && !"CHAT_NPC".equals(ui.getCurrentDialogueMode())) {
                            System.out.println("GAMEPANEL: Interaksi dengan NPC berakhir, reset currentInteractingNPC.");
                            player.currentInteractingNPC = null; 
                        }
                    }
                }
            }
        } else if (gameState == inventoryState) {
            System.out.println("GAMEPANEL: Masuk inventoryState. Nilai enterIsCurrentlyPressed SAAT MASUK BLOK: " + enterIsCurrentlyPressed +
                            ", isSelectingItemForGift: " + isSelectingItemForGift);

            if (keyH.enterPressed) {
                keyH.enterPressed = false; // Selalu konsumsi Enter di awal pemrosesan
                System.out.println("GAMEPANEL (inventoryState): Enter DITEKAN.");

                int selectedItemIndex = ui.getSelectedItemIndex(); // Dapatkan indeks item yang dipilih dari UI

                if (isSelectingItemForGift && npcForGifting != null) { // Jika sedang memilih hadiah
                    System.out.println("GAMEPANEL (inventoryState): Mode MEMILIH HADIAH AKTIF.");
                    // ... (logika gifting Anda yang sudah ada, pastikan berfungsi) ...
                    // Setelah gifting, gameState akan ke dialogueState.
                    // isSelectingItemForGift dan npcForGifting direset saat dialog gifting ditutup atau inventory dibatalkan.
                } else {
                    // Bukan mode gifting, berarti pemain ingin MENGGUNAKAN atau MEMAKAN item
                    System.out.println("GAMEPANEL (inventoryState): Enter untuk penggunaan item biasa. Indeks: " + selectedItemIndex);

                    if (selectedItemIndex >= 0 && selectedItemIndex < player.inventory.size()) {
                        ItemStack stack = player.inventory.get(selectedItemIndex);
                        if (stack != null && stack.getItem() != null) {
                            Item selectedItem = stack.getItem();
                            System.out.println("GAMEPANEL (inventoryState): Item dipilih: " + selectedItem.getName());

                            // Cek apakah item ini bisa dimakan (berdasarkan kategorinya atau metode getEnergyValue())
                            // Kategori Edible: Fish, Crops, Food [cite: 171]
                            // Efek energi: Fish +1[cite: 160], Crop +3[cite: 169], Food bervariasi [cite: 174]
                            // Biaya waktu makan: -5 menit [cite: 213]
                            if (selectedItem.getCategory().equals("Fish") ||
                                selectedItem.getCategory().equals("Crop") || // Pastikan Crop punya getCategory()
                                selectedItem.getCategory().equals("Food")) { // Pastikan Food punya getCategory()

                                int energyGain = selectedItem.getEnergyValue(); // Panggil metode getEnergyValue()

                                if (energyGain > 0) { // Hanya proses jika memang memberi energi
                                    System.out.println("GAMEPANEL (inventoryState): Item '" + selectedItem.getName() + "' bisa dimakan. Energi: +" + energyGain);

                                    // Langsung panggil metode use() dari item tersebut
                                    // Metode use() di Fish, Crop, atau Food akan menangani:
                                    // 1. player.gainEnergy(energyGain)
                                    // 2. ui.showMessage(...)
                                    // 3. player.removeItem(...)
                                    // 4. gameStateSystem.advanceTimeByMinutes(5)
                                    selectedItem.use(); // Memanggil metode use() dari objek Item

                                    // Setelah makan, biasanya kembali ke playState atau inventory tetap terbuka
                                    // Tergantung desain Anda. Untuk sekarang, kita asumsikan kembali ke playState.
                                    // Jika item habis setelah dimakan dan stack kosong, item akan hilang dari inventory (ditangani di removeItem).
                                    // Jika ingin inventory tetap terbuka, jangan ubah gameState di sini.
                                    // Jika ingin inventory tertutup setelah makan:
                                    // gameState = playState;
                                    // ui.currentDialogue = ""; // Kosongkan dialog jika ada sisa

                                } else {
                                    System.out.println("GAMEPANEL (inventoryState): Item '" + selectedItem.getName() + "' adalah edible tapi tidak memberi energi (getEnergyValue() = 0). Tidak dimakan.");
                                    ui.showMessage("Kamu tidak bisa memakan " + selectedItem.getName() + " saat ini.");
                                    // Tetap di inventoryState
                                }
                            } else {
                                // Jika item bukan kategori edible atau aksi lain yang bisa dilakukan dari inventory
                                System.out.println("GAMEPANEL (inventoryState): Item '" + selectedItem.getName() + "' tidak bisa dimakan atau digunakan dengan Enter dari sini.");
                                ui.showMessage(selectedItem.getName() + " tidak bisa dimakan.");
                                // Tetap di inventoryState
                            }
                        }
                    } else {
                        System.out.println("GAMEPANEL (inventoryState): Tidak ada item valid di slot terpilih.");
                    }
                }
            }
            if (enterIsCurrentlyPressed) {
                System.out.println("GAMEPANEL (inventoryState): BLOK if (enterIsCurrentlyPressed) TERPANGGIL!");
                keyH.enterPressed = false; 

                if (isSelectingItemForGift && npcForGifting != null) {
                    System.out.println("GAMEPANEL (inventoryState): Mode GIFTING AKTIF. Memproses hadiah...");
                    int selectedItemIndex = ui.getSelectedItemIndex();
                    System.out.println("GAMEPANEL (inventoryState): selectedItemIndex dari UI: " + selectedItemIndex);

                    if (selectedItemIndex >= 0 && selectedItemIndex < player.inventory.size()) {
                        ItemStack itemToGift = player.inventory.get(selectedItemIndex);
                        if (itemToGift != null && itemToGift.getItem() != null) {
                            System.out.println("GAMEPANEL (inventoryState): Akan memberi hadiah '" + itemToGift.getItem().getName() + "' kepada " + npcForGifting.name);
                            if (player.consumeEnergy(5)) {
                                int heartPointsChange = npcForGifting.processGift(itemToGift.getItem().getName());
                                npcForGifting.updateHeartPoints(heartPointsChange);
                                player.removeItem(itemToGift.getItem().getName(), 1);
                                gameStateSystem.advanceTimeByMinutes(10);
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
                            System.out.println("GAMEPANEL (inventoryState): Pindah ke dialogueState ("+dialogueState+") untuk hasil gifting.");
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
            // Bisa untuk animasi tidur
            

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
                                System.out.println("NPC Interaction: Pindah ke inventoryState untuk memilih hadiah. Target: " + npcForGifting.name);
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
                                    } else {
                                        ui.currentDialogue = "Tidak cukup energi untuk upacara pernikahan.";
                                    }
                                    gameState = dialogueState;
                                } else if (!currentNpc.isProposedTo) {

                                    if (currentNpc.heartPoints >= 150) { 
                                        if (player.consumeEnergy(10)) { 
                                            currentNpc.isProposedTo = true;
                                            if (gameStateSystem != null) {
                                                gameStateSystem.advanceTimeByMinutes(60); 
                                            }
                                            ui.currentDialogue = "Ya, " + player.name + "! Aku mau menikah denganmu!";
                                        } else {
                                            ui.currentDialogue = "Tidak cukup energi untuk melamar saat ini.";
                                        }
                                    } else {
                                        if (player.consumeEnergy(20)) { 
                                            if (gameStateSystem != null) {
                                                gameStateSystem.advanceTimeByMinutes(60); 
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
                        } else if (selectedOption == 3) { // Asumsi opsi ke-2 adalah "Chat" (0: Gift, 1: Lamar, 2: Chat, 3: Batal)
                            System.out.println("GAMEPANEL: Memulai Chat dengan " + currentNpc.name);
                            if (player.consumeEnergy(10)) { 
                                gameStateSystem.advanceTimeByMinutes(10); 
                                currentNpc.updateHeartPoints(10); 

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
                        // --- AKHIR OPSI CHAT ---
                        else if (selectedOption == 4) { // "Batal"
                            gameState = playState;
                            player.currentInteractingNPC = null;
                        }
                    } else {
                        if (selectedOption == 0) { // "Beri Hadiah"
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
                                System.out.println("NPC Interaction: Pindah ke inventoryState untuk memilih hadiah. Target: " + npcForGifting.name);
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
                                                gameStateSystem.advanceTimeByMinutes(60); 
                                            }
                                            ui.currentDialogue = "Ya, " + player.name + "! Aku mau menikah denganmu!";
                                        } else {
                                            ui.currentDialogue = "Tidak cukup energi untuk melamar saat ini.";
                                        }
                                    } else {
                                        if (player.consumeEnergy(20)) { 
                                            if (gameStateSystem != null) {
                                                gameStateSystem.advanceTimeByMinutes(60); 
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
                        } else if (selectedOption == 2) { // Asumsi opsi ke-2 adalah "Chat" (0: Gift, 1: Lamar, 2: Chat, 3: Batal)
                            System.out.println("GAMEPANEL: Memulai Chat dengan " + currentNpc.name);
                            if (player.consumeEnergy(10)) { 
                                gameStateSystem.advanceTimeByMinutes(10); 
                                currentNpc.updateHeartPoints(10); 

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
                        // --- AKHIR OPSI CHAT ---
                        else if (selectedOption == 3) { // "Batal"
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
    int selectedIndexInView = ui.getSelectedItemIndexInSellableView(); // Dapatkan indeks dari daftar yang difilter

    if (selectedIndexInView != -1) { // Pastikan ada item valid yang dipilih
        // Dapatkan indeks asli item tersebut di player.inventory
        int originalInventoryIndex = ui.sellableItemsOriginalIndices.get(selectedIndexInView);
        ItemStack stackInPlayerInventory = player.inventory.get(originalInventoryIndex);

        // --- Logika menjual satu item (dari respons sebelumnya) ---
        if (stackInPlayerInventory != null && stackInPlayerInventory.getItem() != null && stackInPlayerInventory.getQuantity() > 0) {
            Item itemToSell = stackInPlayerInventory.getItem();

            // Pastikan lagi (meskipun sudah difilter) bahwa item punya harga jual
            if (itemToSell.getSellPrice() <= 0) {
                ui.showMessage("Item ini tidak bisa dijual.");
                // Tidak perlu return atau continue di sini karena seharusnya tidak terpilih
            } else {
                boolean itemProcessedForBin = false;
                boolean mergedWithExistingInBin = false;
                // ... (logika merge atau add ke itemsToShip seperti respons sebelumnya) ...
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
                    if (itemsToShip.size() < 16) { // Max 16 unique item types
                        itemsToShip.add(new ItemStack(itemToSell, 1));
                        itemProcessedForBin = true;
                        System.out.println("GAMEPANEL (shippingBinState): Menambah item baru '" + itemToSell.getName() + "' (x1) ke bin.");
                    } else {
                        ui.showMessage("Shipping Bin penuh untuk JENIS item baru!");
                    }
                }
                // --- Akhir logika merge atau add ---

                if (itemProcessedForBin) {
                    stackInPlayerInventory.removeQuantity(1);
                    System.out.println("GAMEPANEL (shippingBinState): Mengurangi 1 '" + itemToSell.getName() + "' dari inventory. Sisa: " + stackInPlayerInventory.getQuantity());
                    if (stackInPlayerInventory.getQuantity() <= 0) {
                        player.inventory.remove(originalInventoryIndex);
                        System.out.println("GAMEPANEL (shippingBinState): Stack '" + itemToSell.getName() + "' habis dan dihapus.");
                    }
                    // !!! PENTING: Setelah inventaris pemain berubah, filter ulang daftar di UI !!!
                    ui.filterSellableItemsForShipping(player);
                    // Kursor mungkin perlu disesuaikan lagi di sini jika item yang dipilih hilang
                    // Metode filterSellableItemsForShipping sudah mencoba menyesuaikan kursor.
                }
            }
        }
        // --- Akhir logika menjual satu item ---
    }
}
            // Logika navigasi (W/A/S/D/Escape) di shippingBinState tetap sama
        } else if (gameState == shoppingState) {
            if (keyH.enterPressed) {
            keyH.enterPressed = false; // Konsumsi input Enter

            int currentSelectionInShop = ui.getShopSelectedItemIndex(); 
            int numberOfItemsForSale = emilyStore.getItemsForSale().size();

            System.out.println("SHOPPING STATE - Enter Pressed. currentSelectionInShop: " + currentSelectionInShop +
                            ", numberOfItemsForSale: " + numberOfItemsForSale +
                            ", ui.CMD_SHOP_EXIT: " + ui.CMD_SHOP_EXIT);

            if (currentSelectionInShop == ui.CMD_SHOP_EXIT || currentSelectionInShop == numberOfItemsForSale) {
                System.out.println("SHOPPING STATE - Memilih KELUAR dari toko.");
                gameState = npcInteractionState; 
                ui.commandNum = 0;    
                ui.shopCommandNum = 0;  
            }
            else if (currentSelectionInShop < numberOfItemsForSale && currentSelectionInShop >= 0) {
                System.out.println("SHOPPING STATE - Memilih item di indeks: " + currentSelectionInShop);
                int quantityToBuy = ui.getShopSelectedQuantity();

                if (quantityToBuy > 0) {
                    emilyStore.processPurchase(player, currentSelectionInShop + 1, quantityToBuy);
                }
            } else {
                System.out.println("SHOPPING STATE - Pilihan tidak dikenali: " + currentSelectionInShop);
            }

        } else if (keyH.escapePressed) {
            keyH.escapePressed = false;
            System.out.println("SHOPPING STATE - Escape Pressed. Kembali ke menu interaksi Emily.");
            gameState = npcInteractionState;
            ui.commandNum = 0;
            ui.shopCommandNum = 0;
        }
        }
        
        else if (gameState == inventoryState) {
            if (keyH.enterPressed) {
                keyH.enterPressed = false; 
                System.out.println("GAMEPANEL (inventoryState): Enter DITEKAN.");

                int selectedItemIndex = ui.getSelectedItemIndex(); 
                if (isSelectingItemForGift && npcForGifting != null) {
                    System.out.println("GAMEPANEL (inventoryState): Mode MEMILIH HADIAH AKTIF untuk NPC: " + npcForGifting.name);
                    // selectedItemIndex sudah didapatkan di atas
                    System.out.println("GAMEPANEL (inventoryState): selectedItemIndex dari UI: " + selectedItemIndex +
                                    ", Ukuran inventory: " + player.inventory.size());

                    if (selectedItemIndex >= 0 && selectedItemIndex < player.inventory.size()) { 
                        ItemStack itemToGift = player.inventory.get(selectedItemIndex); 
                        if (itemToGift != null && itemToGift.getItem() != null) {
                            System.out.println("GAMEPANEL (inventoryState): Akan memberi hadiah '" + itemToGift.getItem().getName() + "' kepada " + npcForGifting.name);

                            if (player.consumeEnergy(5)) {
                                System.out.println("GAMEPANEL (inventoryState): Energi cukup. Player energy: " + player.energy);
                                int heartPointsChange = npcForGifting.processGift(itemToGift.getItem().getName());
                                npcForGifting.updateHeartPoints(heartPointsChange);
                                player.removeItem(itemToGift.getItem().getName(), 1);
                                gameStateSystem.advanceTimeByMinutes(10);

                                String reaction = "";
                                if (heartPointsChange > 15) reaction = " sangat senang!";
                                else if (heartPointsChange > 0) reaction = " terlihat senang.";
                                else if (heartPointsChange == 0) reaction = " menerimanya.";
                                else reaction = " terlihat tidak senang.";
                                ui.currentDialogue = npcForGifting.name + reaction + "\n(Hati: " + npcForGifting.heartPoints + "/" + npcForGifting.maxHeartPoints + ")";
                                System.out.println("GAMEPANEL (inventoryState): Hasil Gifting -> " + ui.currentDialogue);
                            } else {
                                ui.currentDialogue = "Tidak cukup energi untuk memberi hadiah.";
                                System.out.println("GAMEPANEL (inventoryState): Gifting gagal, energi tidak cukup.");
                            }
                            gameState = dialogueState;
                            System.out.println("GAMEPANEL (inventoryState): Pindah ke dialogueState ("+dialogueState+") untuk menampilkan hasil gifting.");
                        } else {
                            System.err.println("GAMEPANEL (inventoryState): Item yang dipilih di slot " + selectedItemIndex + " adalah null atau itemnya null.");
                        }
                    } else {
                        System.out.println("GAMEPANEL (inventoryState): Tidak ada item dipilih di slot atau indeks tidak valid (" + selectedItemIndex + ").");
                    }
                } else {
                    System.out.println("GAMEPANEL (inventoryState): Enter ditekan untuk penggunaan item biasa. selectedItemIndex: " + selectedItemIndex + " (belum diimplementasikan).");
                }
            }
            // Logika keluar dari inventory (Esc atau 'I') sudah ditangani di KeyHandler
        } else if (gameState == fishingState) {
            // Minigame memancing sedang berlangsung.
            // Input angka dari pemain akan di-handle oleh KeyHandler dan disimpan di gp.currentFishingGuess.
            // Saat pemain menekan Enter, kita proses tebakan mereka.

            if (keyH.enterPressed) {
                keyH.enterPressed = false; // Selalu konsumsi Enter setelah diproses

                if (fishBeingFished == null) { // Pengaman jika state ini tercapai tanpa ikan yang valid
                    System.err.println("GAMEPANEL (fishingState): Error - fishBeingFished adalah null!");
                    endFishingMinigame(false, "Terjadi kesalahan saat memancing."); // Keluar dari minigame
                    return;
                }

                if (currentFishingGuess.isEmpty()) {
                    fishingFeedbackMessage = "Masukkan tebakanmu dulu!";
                    System.out.println("FISHING_MINIGAME: Input tebakan kosong.");
                } else {
                    try {
                        int guess = Integer.parseInt(currentFishingGuess);
                        fishingAttemptsLeft--;
                        System.out.println("FISHING_MINIGAME: Pemain menebak: " + guess +
                                        ". Target: " + fishingTargetNumber +
                                        ". Sisa percobaan: " + fishingAttemptsLeft);

                        if (guess == fishingTargetNumber) {
                            fishingFeedbackMessage = "BERHASIL! Kamu menangkap " + fishBeingFished.name + "!";
                            System.out.println("FISHING_MINIGAME: Berhasil menangkap " + fishBeingFished.name);
                            // Tambahkan ikan ke inventory pemain
                            player.inventory.add(new ItemStack(fishBeingFished, 1)); // Asumsi Fish adalah Item
                            // gp.playSE(SUARA_BERHASIL_MANCING); // Mainkan suara jika ada
                            endFishingMinigame(true, fishingFeedbackMessage);
                        } else if (fishingAttemptsLeft <= 0) {
                            fishingFeedbackMessage = "GAGAL! Kesempatan habis. Angka sebenarnya: " + fishingTargetNumber + ".";
                            System.out.println("FISHING_MINIGAME: Gagal, kesempatan habis.");
                            // gp.playSE(SUARA_GAGAL_MANCING); // Mainkan suara jika ada
                            endFishingMinigame(false, fishingFeedbackMessage);
                        } else {
                            // Berikan petunjuk jika tebakan salah
                            if (guess < fishingTargetNumber) {
                                fishingFeedbackMessage = "Terlalu RENDAH! Sisa percobaan: " + fishingAttemptsLeft;
                            } else {
                                fishingFeedbackMessage = "Terlalu TINGGI! Sisa percobaan: " + fishingAttemptsLeft;
                            }
                            System.out.println("FISHING_MINIGAME: Feedback: " + fishingFeedbackMessage);
                        }
                    } catch (NumberFormatException e) {
                        fishingFeedbackMessage = "Input angka tidak valid! Coba lagi.";
                        System.err.println("FISHING_MINIGAME: NumberFormatException untuk input: " + currentFishingGuess);
                    }
                    currentFishingGuess = ""; // Reset input tebakan setelah diproses, siap untuk tebakan berikutnya atau akhir game
                }
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

    public java.util.Scanner getSharedScanner() {
        if (sharedScanner == null) {
            sharedScanner = new java.util.Scanner(System.in);
        }
        return sharedScanner;
    }

    public void executeSleepSequence() {
        System.out.println("GAMEPANEL: executeSleepSequence() dipanggil.");
        // ... (logika pemulihan energi Anda) ...
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
            System.out.println("GAMEPANEL: Memproses penjualan " + itemsToShip.size() + " jenis item dari bin...");
            for (ItemStack shippedStack : itemsToShip) {
                if (shippedStack != null && shippedStack.getItem() != null) {
                    int itemSellValue = shippedStack.getItem().getSellPrice() * shippedStack.getQuantity();
                    goldEarnedToday += itemSellValue;
                    System.out.println("  - Menjual " + shippedStack.getItem().getName() + " x" + shippedStack.getQuantity() + " seharga " + itemSellValue + "G");
                }
            }
            
            player.changeGold(goldEarnedToday);
            itemsToShip.clear(); // Kosongkan bin untuk hari berikutnya
        } else {
            System.out.println("GAMEPANEL: Tidak ada item yang dijual semalam.");
        }
        // --- AKHIR PROSES PENJUALAN ---

        hasShippedToday = false; // Reset untuk hari baru 

        gameStateSystem.advanceToNextMorning(); // Majukan ke pagi berikutnya
        
        String morningMessage = "Selamat pagi! Hari baru telah dimulai.";
        if (goldEarnedToday > 0) {
            morningMessage += "\nKamu mendapatkan " + goldEarnedToday + "G dari penjualan semalam!";
        }
        ui.setDialogue(morningMessage, "SYSTEM_MESSAGE");
        pendingSleep = false;
        gameState = dialogueState; // Tampilkan pesan pagi dulu
        System.out.println("GAMEPANEL (executeSleepSequence): Selesai. pendingSleep=false. gameState=" + gameState);
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
