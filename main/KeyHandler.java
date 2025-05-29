package main;

import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
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
                    gp.ui.commandNum = 2;
                }
            }
            if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 2) {
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
                    System.exit(0);
                }
            }
        }

        // PLAY STATE
        else if(gp.gameState == gp.playState){
            if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){ upPressed = true; }
            if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){ downPressed = true; } 
            if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){ leftPressed = true; }
            if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){ rightPressed = true; }
            if(code == KeyEvent.VK_P){ gp.gameState = gp.pauseState; }
            if(code == KeyEvent.VK_ENTER){ enterPressed = true; }
            if(code == KeyEvent.VK_SHIFT){shiftPressed = true; }

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
            if(code == KeyEvent.VK_P){
                gp.gameState = gp.playState;
            }
        }
        
        //DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = true; // PENTING: Set flag ini agar GamePanel bisa memprosesnya
                // HAPUS BARIS INI: gp.gameState = gp.playState;
            }
        }

        else if(gp.gameState == gp.inventoryState){
            handleInventoryKeys(code); // Handle inventory keys
        }

        // NPC INTERACTION STATE
        else if (gp.gameState == gp.npcInteractionState) {
            npcInteractionState(code);
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
                // Menu interaksi NPC memiliki 3 opsi (0: Gift, 1: Propose/Marry, 2: Cancel)
                if (gp.ui.commandNum < 0) {
                    gp.ui.commandNum = 2; // Kembali ke opsi terakhir (Cancel)
                }
        
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                // Menu interaksi NPC memiliki 3 opsi
                if (gp.ui.commandNum > 2) {
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
    }
}
