// Di dalam file AssetSetter.java
package main;

import object.OBJ_ShippingBin;
import object.OBJ_Television; 
import object.OBJ_Key;  
import object.OBJ_Stove; 

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
        System.out.println("ASSETSETTER: Memulai setObject()...");
        int mapNum = 0; // Semua objek akan ditempatkan di map 0 untuk saat ini
        int i = 0;      

        try {
            if (mapNum < gp.obj.length && i < gp.obj[mapNum].length) {
                gp.obj[mapNum][i] = new OBJ_ShippingBin(gp);
                if (gp.obj[mapNum][i] != null) {
                    // Tentukan koordinat X dan Y (DALAM SATUAN TILE) untuk Shipping Bin
                    // Spesifikasi: Shipping bin terletak di sebelah kanan rumah pemain. 
                    int binTileX = 23; // GANTI DENGAN KOORDINAT X YANG SESUAI (CONTOH)
                    int binTileY = 40; // GANTI DENGAN KOORDINAT Y YANG SESUAI (CONTOH)
                    gp.obj[mapNum][i].worldX = binTileX * gp.tileSize;
                    gp.obj[mapNum][i].worldY = binTileY * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[mapNum][i].name + " ditempatkan di map " + mapNum + " index " + i +
                                       " pada tile (" + binTileX + "," + binTileY + ")");
                } else {
                    System.err.println("AssetSetter: Gagal membuat instance OBJ_ShippingBin.");
                }
                i++; // Naikkan indeks untuk objek berikutnya
            } else {
                System.err.println("AssetSetter: Indeks map atau objek di luar batas untuk Shipping Bin (i=" + i + ")");
            }
        } catch (Exception e) {
            System.err.println("ASSETSETTER: ERROR saat menempatkan Shipping Bin!");
            e.printStackTrace();
        }
        try {
            if (mapNum < gp.obj.length && i < gp.obj[mapNum].length) {
                gp.obj[mapNum][i] = new OBJ_Television(gp); // Pastikan nama kelas ini benar
                if (gp.obj[mapNum][i] != null) {
                    int tvTileX = 23; 
                    int tvTileY = 27; 
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
        try {
            if (mapNum < gp.obj.length && i < gp.obj[mapNum].length) {
                // Asumsi Anda punya kelas seperti object.OBJ_KitchenStove
                gp.obj[mapNum][i] = new object.OBJ_Stove(gp); // Pastikan nama kelas ini benar
                if (gp.obj[mapNum][i] != null) {
                    // Tentukan koordinat untuk KomporDapur di dalam rumah
                    int stoveTileX = 23; 
                    int stoveTileY = 29;
                    gp.obj[mapNum][i].worldX = stoveTileX * gp.tileSize;
                    gp.obj[mapNum][i].worldY = stoveTileY * gp.tileSize;
                    // gp.obj[mapNum][i].name = "KomporDapur"; // Pastikan nama diset di konstruktor objeknya
                    System.out.println("AssetSetter: " + gp.obj[mapNum][i].name + " ditempatkan di map " + mapNum + " index " + i +
                                    " pada tile (" + stoveTileX + "," + stoveTileY + ")");
                } else {
                    System.err.println("AssetSetter: Gagal membuat instance OBJ_Stove.");
                }
                i++;
            } else {
                System.err.println("AssetSetter: Indeks map atau objek di luar batas untuk KomporDapur (i=" + i + ")");
            }
        } catch (Exception e) {
            System.err.println("ASSETSETTER: ERROR saat menempatkan KomporDapur!");
            e.printStackTrace();
        }

        System.out.println("ASSETSETTER: Selesai setObject(). Total slot objek yang coba diisi di map " + mapNum + ": " + i);
    }
}