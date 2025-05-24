package entity;

import main.GamePanel;
import main.KeyHandler;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Observer;
import java.util.Observable;

public class Player extends Entity{
   GamePanel gp;
   KeyHandler keyH;
   public final int screenX;
   public final int screenY;
    public int hasKey = 0;
    
    public Player(GamePanel gp, KeyHandler keyH){
        super(gp);
         this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth/2 - (gp.tileSize/2); //ini tampilan screen yang statis, 
        screenY = gp.screenHeight/2 - (gp.tileSize/2); // kalau mau kameranya engga ngikutin karakter hapus ini aja

        solidArea = new Rectangle(8,16,32,32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
    }
    public void setDefaultValues(){
        worldX= gp.tileSize * 23; // ini aslinya x, y aja, tapi ini mau diubah buat view nya bisa ditengah, nanti kita ubah aja 
        worldY= gp.tileSize * 21;
        speed = 2; //ngatur kecepatan jalan
        direction = "down";
    }
    public void getPlayerImage(){
        up1 = setup("up2");
        up2 = setup("up4");
        down1 = setup("down2");
        down2 = setup("down4");
        left1 = setup("left1");
        left2 = setup("left2");
        right1 = setup("right1");
        right2 = setup("right2");
    }

    public BufferedImage setup(String imageName){
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/res/player/" + imageName + ".PNG"));
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        }catch(IOException e){
            e.printStackTrace();
        }
        return image;
    }

    public void update(){

        // kalau misalkan mau karakter nya goyang terus, keluarkan aja command dari if yang pertama(if(keyH... == true || keyH... == true dll))
        if(keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true){
            if(keyH.upPressed == true){
                direction = "up";
                // worldY -= speed; // aslinya y aja
            }else if(keyH.downPressed == true){
                direction = "down";
                // worldY += speed;
            }else if(keyH.leftPressed == true){
                direction = "left";
                // worldX -= speed;
            }else if(keyH.rightPressed == true){
                direction = "right";
                // worldX += speed;
            }
            //CHECK TILE COLLISION
            collisionOn = false;
            gp.cChecker.CheckTile(this);

            // CHECK OBJECT COLLISION
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);


            // IF COLLISION IS FALSE, PLAYER CAN MOVE
            if(collisionOn == false){
                switch (direction) {
                    case "up":
                        worldY -= speed; // aslinya y aja
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
            if(spriteCounter > 10){
                if(spriteNum == 1){
                    spriteNum = 2;
                }else if(spriteNum == 2){
                    spriteNum = 1;
                } spriteCounter = 0;
            }
        }

    }
    public void pickUpObject(int i) {

        if (i != 999) {
            
            
            
        }
    }

    public void draw(Graphics2D g2){
       // g2.setColor(Color.white);
       // g2.fillRect(x, y, gp.tileSize, gp.tileSize); // (100,100) itu posisi awal, tilesize itu ukuran dari karakter nya
        BufferedImage image = null;
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
        g2.drawImage(image, screenX, screenY, null);
    }
}

