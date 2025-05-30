package main;

import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, fPressed;
    public boolean shiftPressed;
    boolean showDebugText = false;
    public int lastPresseedDirectionKey = 0; 

    public KeyHandler(GamePanel gp){
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Tidak digunakan
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP ||
           code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN ||
           code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT ||
           code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            lastPresseedDirectionKey = code; 
        }
        // TITLE STATE
        if (gp.gameState == gp.titleState) {
            if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) {
                    gp.ui.commandNum = 3;
                }
            }
            if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 3) {
                    gp.ui.commandNum = 0;
                }
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) {
                    gp.gameState = gp.playState;
                }
                if(gp.ui.commandNum == 1) {
                    // add later
                }
                if(gp.ui.commandNum == 2) {
                    gp.gameState = gp.helpState;
                }

                if(gp.ui.commandNum == 3) {
                    System.exit(0);
                }
            }
        }

        // help state
        else if (gp.gameState == gp.helpState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.titleState;
            }
        }

        // PLAY STATE
        else if(gp.gameState == gp.playState){
            if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){ upPressed = true; }
            if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){ downPressed = true; } 
            if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){ leftPressed = true; }
            if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){ rightPressed = true; }
            if(code == KeyEvent.VK_ESCAPE){ gp.gameState = gp.pauseState; }
            if(code == KeyEvent.VK_ENTER){ enterPressed = true; }
            if(code == KeyEvent.VK_SHIFT){shiftPressed = true; }
            if (code == KeyEvent.VK_F) {fPressed = true; }
            if(code == KeyEvent.VK_I){
                gp.gameState = gp.inventoryState; // Pindah ke inventory state
                gp.playSE(5);
            }

            // debug
            if(code == KeyEvent.VK_T){
                if(showDebugText == false){
                    showDebugText = true;
                }
                else if(showDebugText == true){
                    showDebugText = false;
                }
            }
        }

        //PAUSE STATE
        else if(gp.gameState == gp.pauseState){
            if(code == KeyEvent.VK_ESCAPE){
                gp.gameState = gp.playState;
            }
        }
        
        //DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = true;
            }
        }

        else if (gp.gameState == gp.inventoryState) {
            if (code == KeyEvent.VK_I || code == KeyEvent.VK_ESCAPE) { // nutup inventory
                if (gp.isSelectingItemForGift) {
                    gp.isSelectingItemForGift = false;
                    gp.npcForGifting = null;
                    gp.gameState = gp.npcInteractionState; // balik ke menu npc
                    System.out.println("KEYHANDLER: Batal memilih hadiah, kembali ke menu NPC.");
                } else {
                    // Jika inventory biasa, kembali ke playState
                    gp.gameState = gp.playState;
                }
                // gp.playSE(5); // Suara jika ada
            } else if (code == KeyEvent.VK_ENTER) {
                System.out.println("DEBUG KEYHANDLER: InventoryState - Enter DITEKAN, enterPressed akan di-set true.");
                enterPressed = true; // GamePanel akan memproses pemilihan item
            } else if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                if (gp.ui.slotRow > 0) { gp.ui.slotRow--; /* gp.playSE(5); */ }
            } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                if (gp.ui.slotRow < gp.ui.inventoryMaxRow - 1) { gp.ui.slotRow++; /* gp.playSE(5); */ }
            } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                if (gp.ui.slotCol > 0) { gp.ui.slotCol--; /* gp.playSE(5); */ }
            } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                if (gp.ui.slotCol < gp.ui.inventoryMaxCol - 1) { gp.ui.slotCol++; /* gp.playSE(5); */ }
            }
        }

        // NPC INTERACTION STATE
        else if (gp.gameState == gp.npcInteractionState) {
            npcInteractionState(code);
        }

        // FISHING STATE
        else if (gp.gameState == gp.fishingState) {
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = true; // GamePanel akan memproses tebakan
                System.out.println("KEYHANDLER (fishingState): Enter ditekan.");
            } else if (code == KeyEvent.VK_BACK_SPACE) { // Untuk menghapus karakter terakhir
                if (gp.currentFishingGuess != null && !gp.currentFishingGuess.isEmpty()) {
                    gp.currentFishingGuess = gp.currentFishingGuess.substring(0, gp.currentFishingGuess.length() - 1);
                    System.out.println("KEYHANDLER (fishingState): Backspace. Tebakan sekarang: " + gp.currentFishingGuess);
                }
            } else if (code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9) { // Hanya terima input angka 0-9
                // Batasi panjang input angka, misalnya maksimal 3 digit jika angka terbesar adalah 500
                int maxInputLength = 3;
                if (gp.fishingMaxRange >= 1000) maxInputLength = 4; // Sesuaikan jika ada rentang lebih besar

                if (gp.currentFishingGuess.length() < maxInputLength) {
                    gp.currentFishingGuess += (char) code; // Tambahkan digit ke string tebakan
                    System.out.println("KEYHANDLER (fishingState): Angka '" + (char)code + "' ditekan. Tebakan sekarang: " + gp.currentFishingGuess);
                }
            } else if (code == KeyEvent.VK_ESCAPE) { // Opsi untuk membatalkan memancing
                System.out.println("KEYHANDLER (fishingState): Escape ditekan, membatalkan memancing.");
                // GamePanel akan menangani logika pembatalan jika diperlukan,
                // atau kita bisa langsung panggil endFishingMinigame dari sini.
                // Untuk konsistensi, biarkan GamePanel yang memproses jika ada flag khusus untuk Escape.
                // Tapi untuk sederhana, kita bisa langsung akhiri:
                gp.fishingFeedbackMessage = "Kamu berhenti memancing.";
                gp.endFishingMinigame(false, gp.fishingFeedbackMessage); // Langsung akhiri jika Escape ditekan
            }
        }

        // tilling and planting
        if (code == KeyEvent.VK_SPACE) {
            if (gp.gameState == gp.dialogueState) {
                gp.gameState = gp.playState; // keluar dari dialog
            } else if (gp.gameState == gp.playState) {
                // Coba tanam, kalau gagal (belum dibajak / tidak ada seed), coba bajak
                boolean planted = gp.player.plantSeed();
                if (!planted) {
                    gp.player.tileLand();
                }
            }
        }


        // next day
        if (code == KeyEvent.VK_N) {
            gp.gameStateSystem.getTimeManager().advanceToNextMorning();
        }

    }
    

    public void npcInteractionState(int code) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                // Menu interaksi NPC memiliki 3 opsi (0: Gift, 1: Propose/Marry, 2: Chat, 3: Cancel)
                if (gp.ui.commandNum < 0) {
                    gp.ui.commandNum = 3; // Kembali ke opsi terakhir (Cancel)
                }
        
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                // Menu interaksi NPC memiliki 3 opsi
                if (gp.ui.commandNum > 3) {
                    gp.ui.commandNum = 0; // Kembali ke opsi pertama (Gift)
                }
            }
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = true; // GamePanel akan menangani aksi berdasarkan commandNum
            }
            if (code == KeyEvent.VK_ESCAPE) { // Opsional: Tombol Escape untuk membatalkan
                gp.gameState = gp.playState;
                if(gp.player != null) { // Pastikan player tidak null
                    gp.player.currentInteractingNPC = null; // Selesai interaksi
                }
            }
        }

    public void handleInventoryKeys(int code) {
        if(code == KeyEvent.VK_I){
            gp.gameState = gp.playState; // Kembali ke play state
            gp.playSE(5); // Suara untuk menutup inventory
        }
        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
            if(gp.ui.slotRow > 0){
                gp.ui.slotRow--;
                gp.playSE(5);
            }
        }
        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
            if(gp.ui.slotRow < gp.ui.inventoryMaxRow - 1){ 
                gp.ui.slotRow++;
                gp.playSE(5);
            }
        }   
        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
            if(gp.ui.slotCol > 0){
                gp.ui.slotCol--;
                gp.playSE(5);
            }
        }
        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
            if(gp.ui.slotCol < gp.ui.inventoryMaxCol - 1){ 
                gp.ui.slotCol++;
                gp.playSE(5);
            } 
        } 
        if(code == KeyEvent.VK_ENTER){
            // // Logika untuk memilih item
            // int selectedItem = gp.ui.getSelectedItem();
            // if(selectedItem != -1) {
            //     gp.player.useItem(selectedItem);
            //     gp.playSE(5); // Suara untuk menggunakan item
            // }
            enterPressed = true; 
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
            upPressed = false;
        }
        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
            downPressed = false;
        }
        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
            leftPressed = false;
        }
        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
            rightPressed = false;
        }
        if(code == KeyEvent.VK_SHIFT){
            shiftPressed = false;
        }
        if (code == KeyEvent.VK_F) {
            fPressed = false;
        }
    }
}
