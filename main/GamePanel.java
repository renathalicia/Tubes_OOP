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

    private boolean pendingSleep = false;
    private String sleepTriggerMessage = "";

    public boolean isSelectingItemForGift = false; // Flag untuk mode pilih item hadiah
    public Entity npcForGifting = null;  

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

    public boolean tvInteractionPendingConfirmation = false;


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

    // Di dalam file GamePanel.java Anda

    public void update() {
        // Simpan status keyH.enterPressed di awal update untuk konsistensi dalam satu frame ini
        boolean enterIsCurrentlyPressed = keyH.enterPressed;

        // Jika enterIsCurrentlyPressed, kita akan mengonsumsinya di akhir blok state yang relevan
        // atau di akhir pemeriksaan input jika tidak ada state yang menangani.

        // System.out.println("--- FRAME START --- GameState: " + gameState + ", pendingSleep: " + pendingSleep + ", tvInteractionPendingConfirmation: " + tvInteractionPendingConfirmation + ", enterIsCurrentlyPressed: " + enterIsCurrentlyPressed);

        // BAGIAN 1: Deteksi Kondisi Tidur Otomatis
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
                // keyH.enterPressed TIDAK direset di sini, biarkan state dialogue yang menangani jika perlu
            }
        }

        // BAGIAN 2: Logika Update Berdasarkan Game State Saat Ini
        if (gameState == titleState) {
            if (enterIsCurrentlyPressed) {
                if (ui.commandNum == 0) { // New Game
                    gameState = playState;
                    playMusic(0);
                } // ... (opsi title lain) ...
                keyH.enterPressed = false; // Konsumsi Enter untuk title state
            }
        } else if (gameState == playState) {
            player.update(); // Update player (termasuk gerakan)
            for (int i = 0; i < npc[currentMap].length; i++) {
                if (npc[currentMap][i] != null) npc[currentMap][i].update();
            }

            if (enterIsCurrentlyPressed) { // Gunakan variabel lokal yang menangkap status Enter di awal frame
                System.out.println("GAMEPANEL (playState): Enter terdeteksi untuk interaksi.");
                int npcIndex = cChecker.checkEntity(player, npc);
                if (npcIndex != 999) {
                    player.interactNPC(npcIndex); // Akan set gameState ke npcInteractionState
                } else {
                    int objIndex = cChecker.checkObject(player, true);
                    if (objIndex != 999) {
                        SuperObject interactedObject = obj[currentMap][objIndex];
                        if (interactedObject != null && "Televisi".equals(interactedObject.name)) {
                            tvInteractionPendingConfirmation = true;
                            ui.currentDialogue = "Apakah kamu ingin menonton TV?";
                            ui.commandNum = 0;
                            gameState = dialogueState;
                        } // ... (objek lain) ...
                    }
                }
                keyH.enterPressed = false; // Konsumsi Enter setelah diproses di playState
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
                    // ... (Logika konfirmasi TV Anda yang sudah benar) ...
                    // Pastikan setelah memilih Ya/Tidak, tvInteractionPendingConfirmation direset ke false.
                    // Jika Ya dan menampilkan cuaca, UI.currentDialogue diisi pesan cuaca,
                    // dan ui.setDialogueMode("SYSTEM_MESSAGE"); // atau mode lain yang bukan CHAT_NPC
                }
                // --- PENANGANAN LANJUTAN CHAT ADA DI SINI ---
                else if (player.currentInteractingNPC != null && "CHAT_NPC".equals(ui.getCurrentDialogueMode())) {
                    System.out.println("GAMEPANEL (dialogueState): Melanjutkan mode CHAT_NPC dengan " + player.currentInteractingNPC.name);
                    String nextLine = player.currentInteractingNPC.getNextChatLine(); // Memanggil metode dari Entity

                    if (nextLine != null) {
                        // Masih ada baris chat berikutnya
                        ui.setDialogue(nextLine, "CHAT_NPC"); // Tetap dalam mode chat, tampilkan baris berikutnya
                        System.out.println("GAMEPANEL (dialogueState): Menampilkan baris chat berikutnya: '" + nextLine + "'");
                    } else {
                        // Tidak ada baris chat lagi, sesi chat selesai
                        System.out.println("GAMEPANEL (dialogueState): Tidak ada baris chat berikutnya dari " + player.currentInteractingNPC.name + ". Chat selesai.");
                        ui.clearDialogueMode();
                        ui.currentDialogue = ""; // Kosongkan dialog
                        gameState = playState;   // Kembali ke playState
                        player.currentInteractingNPC = null; // Selesaikan interaksi dengan NPC ini
                        System.out.println("GAMEPANEL (dialogueState): Kembali ke playState. currentInteractingNPC direset.");
                    }
                }
                // --- AKHIR PENANGANAN LANJUTAN CHAT ---
                else if (pendingSleep) {
                    executeSleepSequence();
                } else {
                    // Ini adalah dialog biasa lainnya (misal pesan cuaca TV selesai, pesan hasil gifting, dll.)
                    System.out.println("GAMEPANEL (dialogueState): Menangani dialog biasa/sistem lainnya.");
                    // Jika dialog ini adalah hasil dari aksi di npcInteractionState (misal gifting)
                    if (player.currentInteractingNPC != null && ui.isDialogueFromNpcAction()){ // Anda perlu implementasi ui.isDialogueFromNpcAction()
                        System.out.println("GAMEPANEL: Dialog hasil aksi NPC, kembali ke npcInteractionState.");
                        gameState = npcInteractionState; // Kembali ke menu pilihan NPC
                        ui.clearDialogueMode();
                        ui.commandNum = 0; // Reset pilihan menu NPC
                    } else {
                        // Untuk dialog sistem umum atau akhir dari segalanya
                        System.out.println("GAMEPANEL: Dialog umum selesai, kembali ke playState.");
                        gameState = playState;
                        ui.clearDialogueMode();
                        if (player.currentInteractingNPC != null && !"CHAT_NPC".equals(ui.getCurrentDialogueMode())) {
                            // Jika ini adalah dialog terakhir dari interaksi non-chat, mungkin reset NPC juga
                            // player.currentInteractingNPC = null; // Hati-hati dengan ini
                        }
                    }
                }
            }
        } else if (gameState == inventoryState) {
            System.out.println("GAMEPANEL: Masuk inventoryState. Nilai enterIsCurrentlyPressed SAAT MASUK BLOK: " + enterIsCurrentlyPressed +
                            ", isSelectingItemForGift: " + isSelectingItemForGift);

            if (enterIsCurrentlyPressed) {
                System.out.println("GAMEPANEL (inventoryState): BLOK if (enterIsCurrentlyPressed) TERPANGGIL!");
                keyH.enterPressed = false; // Langsung konsumsi/reset di sini setelah terdeteksi

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
                            // isSelectingItemForGift dan npcForGifting akan direset saat dialog hasil gifting ditutup
                            // atau saat keluar dari inventory (sudah ditangani KeyHandler).
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


                    if (selectedOption == 0) { // Asumsi 0 adalah "Beri Hadiah"
                        System.out.println("NPC Interaction: Memilih 'Beri Hadiah'.");
                        if (player.inventory.isEmpty()) {
                            ui.currentDialogue = "Inventory kosong. Tidak ada yang bisa diberikan.";
                            gameState = dialogueState; // Tampilkan pesan
                            // ui.setDialogueSourceNpcAction(true); // Jika Anda pakai sistem mode dialog
                        } else {
                            isSelectingItemForGift = true;
                            npcForGifting = currentNpc; // Simpan NPC target
                            gameState = inventoryState;   // Pindah ke layar inventory
                            ui.slotCol = 0; // Reset kursor inventory
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
                    } else if (selectedOption == 2) { // Asumsi opsi ke-2 adalah "Chat" (0: Gift, 1: Lamar, 2: Chat, 3: Batal)
                        System.out.println("GAMEPANEL: Memulai Chat dengan " + currentNpc.name);
                        if (player.consumeEnergy(10)) { // Efek energi [cite: 163]
                            gameStateSystem.advanceTimeByMinutes(10); // Efek waktu [cite: 163]
                            currentNpc.updateHeartPoints(10); // Efek heart points [cite: 163]

                            currentNpc.startChat(); // Reset indeks dialog chat NPC
                            String firstChatLine = currentNpc.getNextChatLine();
                            if (firstChatLine != null) {
                                ui.setDialogue(firstChatLine, "CHAT_NPC"); // Set dialog pertama dan mode
                                gameState = dialogueState;
                            } else {
                                ui.setDialogue(currentNpc.name + " tidak ingin banyak bicara saat ini.", "SYSTEM_MESSAGE");
                                gameState = dialogueState; // Tetap ke dialogueState untuk menampilkan pesan ini
                                // Mungkin langsung kembali ke playState setelah pesan ini
                                // player.currentInteractingNPC = null;
                            }
                        } else {
                            ui.setDialogue("Tidak cukup energi untuk mengobrol.", "SYSTEM_MESSAGE");
                            gameState = dialogueState;
                        }
                    }
                    // --- AKHIR OPSI CHAT ---
                    else if (selectedOption == 3) { // Asumsi opsi ke-3 adalah "Batal"
                        gameState = playState;
                        player.currentInteractingNPC = null;
                    }
                }
                keyH.enterPressed = false;
            }
        } else if (gameState == inventoryState) {
            // Debug print paling awal saat masuk state ini
            System.out.println("GAMEPANEL: Masuk inventoryState. Nilai keyH.enterPressed SAAT MASUK BLOK: " + keyH.enterPressed +
                            ", isSelectingItemForGift: " + isSelectingItemForGift +
                            ", npcForGifting: " + (npcForGifting != null ? npcForGifting.name : "null"));

            if (keyH.enterPressed) {
                keyH.enterPressed = false; // Selalu konsumsi Enter di awal pemrosesan
                System.out.println("GAMEPANEL (inventoryState): Enter DITEKAN.");

                // --- DEKLARASIKAN DAN DAPATKAN selectedItemIndex DI SINI ---
                int selectedItemIndex = ui.getSelectedItemIndex(); // Mendapatkan indeks dari UI

                if (isSelectingItemForGift && npcForGifting != null) {
                    System.out.println("GAMEPANEL (inventoryState): Mode MEMILIH HADIAH AKTIF untuk NPC: " + npcForGifting.name);
                    // selectedItemIndex sudah didapatkan di atas
                    System.out.println("GAMEPANEL (inventoryState): selectedItemIndex dari UI: " + selectedItemIndex +
                                    ", Ukuran inventory: " + player.inventory.size());

                    if (selectedItemIndex >= 0 && selectedItemIndex < player.inventory.size()) { // Ini baris error 408 Anda
                        ItemStack itemToGift = player.inventory.get(selectedItemIndex); // Ini baris error 409 Anda
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
                            // isSelectingItemForGift dan npcForGifting akan direset saat dialog hasil gifting ditutup
                            // atau saat keluar dari inventory melalui Esc/I.
                        } else {
                            // Ini adalah baris error 436 Anda jika selectedItemIndex digunakan di sini tanpa pengecekan itemToGift
                            System.err.println("GAMEPANEL (inventoryState): Item yang dipilih di slot " + selectedItemIndex + " adalah null atau itemnya null.");
                            // Tidak lakukan apa-apa, biarkan pemain memilih lagi atau keluar
                        }
                    } else {
                        System.out.println("GAMEPANEL (inventoryState): Tidak ada item dipilih di slot atau indeks tidak valid (" + selectedItemIndex + ").");
                        // Tidak lakukan apa-apa, biarkan pemain memilih lagi atau keluar
                    }
                } else {
                    // Pemain menekan Enter di inventory untuk tujuan lain (misal, menggunakan item)
                    System.out.println("GAMEPANEL (inventoryState): Enter ditekan untuk penggunaan item biasa. selectedItemIndex: " + selectedItemIndex + " (belum diimplementasikan).");
                    // Tambahkan logika penggunaan item di sini jika ada, menggunakan selectedItemIndex yang sudah dideklarasi di atas.
                }
            }
            // Logika keluar dari inventory (Esc atau 'I') sudah ditangani di KeyHandler
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
