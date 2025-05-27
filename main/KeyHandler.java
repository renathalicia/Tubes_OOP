package main;

import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
    public boolean shiftPressed;
    boolean showDebugText = false;

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
                    // gp.playMusic(0);
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
        if(gp.gameState == gp.playState){
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
        else if(gp.gameState == gp.dialogueState){
            if(code == KeyEvent.VK_ENTER){
                // enterPressed
                gp.gameState = gp.playState;
            }
        }
        else if(gp.gameState == gp.inventoryState){
            handleInventoryKeys(code); // Handle inventory keys
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
