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
    public boolean isSelectingItemForGift = false; 
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
    public final int transitionState = 5; //untuk transisi pindah map yang lebih halus
    public final int inventoryState = 6;
    public final int npcInteractionState = 7;
    
    public final int shoppingState = 9; 
    public final int helpState = 8; 
    public final int fishingState = 10;
    public final int shippingBinState = 11;
    public final int characterCreationState = 12;

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

        gameStateSystem.advanceTimeByMinutes(15);
        System.out.println("GAMEPANEL: Waktu dimajukan 15 menit untuk memancing.");

        String currentLocationString = "Default_Location"; 
        if (currentMap == 0) { 
            currentLocationString = "Pond"; 
        } else {
            System.out.println("GAMEPANEL: Saat ini hanya bisa memancing di map 0.");
            ui.currentDialogue = "Kamu hanya bisa memancing di area tertentu di peta utama.";
            gameState = dialogueState;
            return;
        }

        List<Fish> catchableFishToday = new ArrayList<>();

        if (ItemRepository.Bullhead != null) catchableFishToday.add(ItemRepository.Bullhead);
        if (ItemRepository.Carp != null) catchableFishToday.add(ItemRepository.Carp);
        if (ItemRepository.Largemouth_Bass != null) catchableFishToday.add(ItemRepository.Largemouth_Bass);

        if (catchableFishToday.isEmpty()) {
            ui.currentDialogue = "Sepertinya tidak ada ikan yang aktif saat ini";
            gameState = dialogueState;
            System.out.println("GAMEPANEL: Tidak ada ikan yang bisa ditangkap saat ini.");
            return;
        }

        java.util.Random random = new java.util.Random();
        fishBeingFished = catchableFishToday.get(random.nextInt(catchableFishToday.size()));
        System.out.println("GAMEPANEL: Ikan yang berpotensi ditangkap: " + fishBeingFished.name + " (" + fishBeingFished.getFishRarity() + ")");


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

        gameState = fishingState;

        ui.setDialogue(fishingFeedbackMessage, "FISHING_MINIGAME"); 
        System.out.println("GAMEPANEL: Pindah ke fishingState (" + fishingState + ")");
    }

    public void endFishingMinigame(boolean caughtFish, String finalMessage) {
        System.out.println("GAMEPANEL: Minigame Fishing Selesai. Berhasil: " + caughtFish + ". Pesan: " + finalMessage);

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
                keyH.fPressed = false; 
                System.out.println("GAMEPANEL (playState): Tombol F ditekan untuk memancing.");

                boolean hasFishingRod = false;
                for (ItemStack stack : player.inventory) {
                    if (stack.getItem() != null && "Fishing Rod".equals(stack.getItem().getName())) {
                        hasFishingRod = true;
                        break;
                    }
                }

                if (!hasFishingRod) {
                    ui.currentDialogue = "Kamu membutuhkan Pancingan (Fishing Rod)!";
            
                    gameState = dialogueState;
                    System.out.println("GAMEPANEL: Gagal memancing, tidak ada Fishing Rod.");
                } else {
         
                    if (player.isFacingWaterTile()) { 
          
                        if (player.consumeEnergy(5)) {
                            System.out.println("GAMEPANEL: Memulai aksi Fishing");
                            startFishingMinigame(); 
                        } else {
                            ui.currentDialogue = "Tidak cukup energi untuk memancing.";
                  
                            gameState = dialogueState;
                            System.out.println("GAMEPANEL: Gagal memancing, energi tidak cukup.");
                        }
                    } else {
                        ui.currentDialogue = "Kamu tidak bisa memancing di sini.";
           
                        gameState = dialogueState;
                        System.out.println("GAMEPANEL: Gagal memancing, tidak menghadap air.");
                    }
                }
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
                                } else {
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
                            System.out.println("GAMEPANEL (inventoryState): Item dipilih: " + selectedItem.getName());

        
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
                    System.out.println("GAMEPANEL (inventoryState): Mode GIFTING AKTIF. Memproses hadiah");
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
            

        } else if (gameState == characterCreationState) {
    if (keyH.enterPressed) { // Flag ini sekarang HANYA true jika Enter ditekan pada tombol "Selesai"
        keyH.enterPressed = false; // Selalu konsumsi flag setelah diproses

        System.out.println("GamePanel: Finalizing character creation.");

        // Terapkan data yang dikumpulkan dari UI ke objek player
        player.name = ui.tempPlayerName.isEmpty() ? "Petani" : ui.tempPlayerName;
        player.gender = (ui.tempGenderSelection == 0) ? "Laki-laki" : "Perempuan"; // Asumsi 0=Laki, 1=Perempuan
        player.farmName = ui.tempFarmName.isEmpty() ? "Kebunku" : ui.tempFarmName;
        if (player.favoriteItem != null) { // Pastikan field favoriteItem ada di Player
            player.favoriteItem = ui.tempFavoriteItem.isEmpty() ? "None" : ui.tempFavoriteItem; // Beri default jika kosong
        }

        System.out.println("Pembuatan Karakter Selesai:");
        System.out.println("Nama: " + player.name);
        System.out.println("Gender: " + player.gender);
        System.out.println("Nama Kebun: " + player.farmName);
        if (player.favoriteItem != null) {
            System.out.println("Item Favorit: " + player.favoriteItem);
        }

        ui.activeInputField = 0; // Reset fokus untuk penggunaan berikutnya jika ada
        gameState = playState; // Pindah ke playState
        // playMusic(0); // Mainkan musik game utama
    }
    // Tidak ada 'else if (keyH.enterPressed)' lagi di sini, karena navigasi field ditangani KeyHandler
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
                        } else if (selectedOption == 3) { 
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
                                System.out.println("NPC Interaction: Pindah ke inventoryState untuk memilih hadiah. Target: " + npcForGifting.name);
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
            keyH.enterPressed = false; 

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
     
        } else if (gameState == fishingState) {

            if (keyH.enterPressed) {
                keyH.enterPressed = false;

                if (fishBeingFished == null) { 
                    System.err.println("GAMEPANEL (fishingState): Error - fishBeingFished adalah null!");
                    endFishingMinigame(false, "Terjadi kesalahan saat memancing."); 
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
                   
                            player.inventory.add(new ItemStack(fishBeingFished, 1)); 
                         
                            endFishingMinigame(true, fishingFeedbackMessage);
                        } else if (fishingAttemptsLeft <= 0) {
                            fishingFeedbackMessage = "GAGAL! Kesempatan habis. Angka sebenarnya: " + fishingTargetNumber + ".";
                            System.out.println("FISHING_MINIGAME: Gagal, kesempatan habis.");
                 
                            endFishingMinigame(false, fishingFeedbackMessage);
                        } else {
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
                    currentFishingGuess = "";
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
        //  (logika pemulihan energi) 
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

        gameStateSystem.advanceToNextMorning(); 
        
        String morningMessage = "Selamat pagi! Hari baru telah dimulai.";
        if (goldEarnedToday > 0) {
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

        if (currentMap >= 0 && currentMap < obj.length && obj[currentMap] != null) {
            // HANYA iterasi objek di dalam currentMap
            for (int i = 0; i < obj[currentMap].length; i++) {
                // Jika ada objek di indeks ini, gambar!
                if (obj[currentMap][i] != null) {
                    obj[currentMap][i].draw(g2, this);
                }
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
