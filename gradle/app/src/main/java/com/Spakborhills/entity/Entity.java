package com.Spakborhills.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import com.Spakborhills.main.GamePanel;
import com.Spakborhills.main.UtilityTool;

public class Entity {
    public GamePanel gp;
    public int worldX, worldY;
    public int x, y;
    public int speed;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;
    public int spriteCounter = 0;
    public int spriteNum = 1;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    public String dialogues[] = new String[20];
    public int dialogueIndex = 0;
    public int SolidAreaY;
    public int SolidAreaX;
    
    public final int maxHeartPoints = 150;
    public int heartPoints = 0;
    public boolean isProposedTo = false;
    public boolean isMarriedTo = false;
    public String name = "NPC"; 

    public String[] chatDialogues = new String[5]; 
    public int currentChatLineIndex = 0;

    public List<String> lovedItems = new ArrayList<>();
    public List<String> likedItems = new ArrayList<>();
    public List<String> hatedItems = new ArrayList<>(
        
    );
    // CHARACTER STATUS
    public int maxLife;
    public int life;

    public void startChat() {
        currentChatLineIndex = 0;
    }

    public String getNextChatLine() {
        if (chatDialogues == null || currentChatLineIndex >= chatDialogues.length || chatDialogues[currentChatLineIndex] == null) {
            return null; 
        }
        String line = chatDialogues[currentChatLineIndex];
        currentChatLineIndex++;
        return line;
    }

    public void setDialogue() {

    }

    public void setChatDialogues() {

    }

    public Entity(GamePanel gp){
        this.gp = gp;
    }

    public void speak(){
        if(dialogues[dialogueIndex] == null){
            dialogueIndex = 0;
        }
        gp.ui.currentDialogue = dialogues[dialogueIndex];
        dialogueIndex++;
        switch (gp.player.direction) {
            case "up":
                direction = "down";               
                break;
            case "down":
                direction = "up";               
                break;
            case "left":
                direction = "right";               
                break;
            case "right":
                direction = "left";               
                break;
        }
    }

    public void update(){
        collisionOn = false;
        gp.cChecker.checkTile(this);
        if(collisionOn == false){
            switch (direction) {
                case "up":
                    worldY -= speed;
                    break;
                case "down":
                    worldY += speed;
                    break;
                case "left":
                    worldX -= speed;
                    break;
                case "right":
                    worldX += speed;
                    break;
                default:
                    break;
            }
        }
        spriteCounter++;
        if(spriteCounter > 15){
            if(spriteNum == 1){
                spriteNum = 2;
            }else if(spriteNum == 2){
                spriteNum = 1;
            } spriteCounter = 0;
        }
    }

    public int processGift(String itemName) {
        if (lovedItems.contains(itemName)) {
            return 25;
        } else if (likedItems.contains(itemName)) {
            return 20;
        } else if (hatedItems.contains(itemName)) {
            return -25;
        }
        return 0; 
    }

    public void updateHeartPoints(int amount) {
        this.heartPoints += amount;
        if (this.heartPoints > maxHeartPoints) { 
            this.heartPoints = maxHeartPoints;
        }
        if (this.heartPoints < 0) {
            this.heartPoints = 0;
        }
    }

    public void draw(Graphics2D g2){
        BufferedImage image = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY  + gp.player.screenY;

        if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY){
            switch(direction){
        case "up":
            if(spriteNum == 1){
                image = up1;
            }
            if(spriteNum == 2){
                image = up2;
            }
            break;
        case "down":
            if(spriteNum == 1){
                image = down1;
            }
            if(spriteNum == 2){
                image = down2;
            }
            break;
        case "left":
            if(spriteNum == 1){
                image = left1;
            }
            if(spriteNum == 2){
                image = left2;
            }
            break;
        case "right":
            if(spriteNum == 1){
                image = right1;
            }
            if(spriteNum == 2){
                image = right2;
            }
            break;
        }
            g2.drawImage( image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }

    public BufferedImage setup(String imagePath){
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;
        try{
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".PNG"));
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        }catch(IOException e){
            e.printStackTrace();
        }
        return image;
    }
}
