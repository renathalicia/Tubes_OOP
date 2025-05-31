package main;

// Impor kelas objek yang akan Anda tempatkan
import object.OBJ_House;
import object.OBJ_ShippingBin;
import object.OBJ_Television; // Pastikan ini nama kelas TV Anda
import object.OBJ_Key;      // Contoh objek lain

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
        System.out.println("ASSETSETTER: Memulai setObject()...");
        int mapNum = 0;
        int i = 0;     

        // 1. Menempatkan OBJEK SHIPPING BIN
        try {
            if (mapNum < gp.obj.length && i < gp.obj[mapNum].length) {
                gp.obj[mapNum][i] = new OBJ_ShippingBin(gp);
                if (gp.obj[mapNum][i] != null) {
                    int binTileX = 36; 
                    int binTileY = 22; 
                    gp.obj[mapNum][i].worldX = binTileX * gp.tileSize;
                    gp.obj[mapNum][i].worldY = binTileY * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[mapNum][i].name + " ditempatkan di map " + mapNum + " index " + i +
                                       " pada tile (" + binTileX + "," + binTileY + ")");
                } else {
                    System.err.println("AssetSetter: Gagal membuat instance OBJ_ShippingBin.");
                }
                i++; 
            } else {
                System.err.println("AssetSetter: Indeks map atau objek di luar batas untuk Shipping Bin (i=" + i + ")");
            }
        } catch (Exception e) {
            System.err.println("ASSETSETTER: ERROR saat menempatkan Shipping Bin!");
            e.printStackTrace();
        }

        try {
            if (mapNum < gp.obj.length && i < gp.obj[mapNum].length) {
                gp.obj[mapNum][i] = new OBJ_House(gp);
                if (gp.obj[mapNum][i] != null) {
                    int tvTileX = 29;
                    int tvTileY = 22;
                    gp.obj[mapNum][i].worldX = tvTileX * gp.tileSize;
                    gp.obj[mapNum][i].worldY = tvTileY * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[mapNum][i].name + " ditempatkan di map " + mapNum + " index " + i +
                            " pada tile (" + tvTileX + "," + tvTileY + ")");
                } else {
                    System.err.println("AssetSetter: Gagal membuat instance OBJ_Television.");
                }
                i++;
            } else {
                System.err.println("AssetSetter: Indeks map atau objek di luar batas untuk Televisi (i=" + i + ")");
            }
        } catch (Exception e) {
            System.err.println("ASSETSETTER: ERROR saat menempatkan Televisi!");
            e.printStackTrace();
        }

        System.out.println("ASSETSETTER: Selesai setObject(). Total slot objek yang coba diisi di map " + mapNum + ": " + i);
    }
}