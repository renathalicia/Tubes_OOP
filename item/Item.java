package item;

import javax.imageio.ImageIO;
import main.UtilityTool;    
import main.GamePanel;

import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class Item {
    public String name;
    public int buyPrice;
    public int sellPrice;
    public BufferedImage image;
    public String description= "";
    public boolean stackable = false;
    public int amount = 1;
    protected GamePanel gp;
    public int maxStackAmount = 99; 

    //konstruktor
    public Item(String name, int buyPrice, int sellPrice, GamePanel gp) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.gp = gp;
    }

    //getter & setter
    public String getName(){ return name; }

    public int getBuyPrice() { return buyPrice; }

    public int getSellPrice() { return sellPrice; }

    public abstract String getCategory();

    public int getEnergyValue() {
        return 0; 
    }

    public BufferedImage setUpImage(String imagePath) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage scaledImage = null;  
        try {
            String fullPath = imagePath + ".png";
            var is = getClass().getResourceAsStream(fullPath);
            if (is == null) {
                System.out.println("GAGAL LOAD IMAGE: " + fullPath);
                throw new IllegalArgumentException("File tidak ditemukan: " + fullPath);
            }

            BufferedImage originalImage = ImageIO.read(is);
            scaledImage = uTool.scaleImage(originalImage, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading image: " + imagePath);
        }
        return scaledImage;
    }


    public void use(){
        System.out.println("Using item: " + name);
        gp.ui.showMessage("Anda menggunakan " + name + ".");
    }
    
}
