// Di dalam file AssetSetter.java
package main;

import object.*;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
        System.out.println("ASSETSETTER: Memulai setObject()...");
        int mapNum = 0; // Semua objek akan ditempatkan di map 0 untuk saat ini
        int i = 0;      // Indeks untuk array objek gp.obj[mapNum]

        // 1. Menempatkan OBJEK SHIPPING BIN
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

        // 2. Menempatkan OBJEK TELEVISI
        try {
            if (mapNum < gp.obj.length && i < gp.obj[mapNum].length) {
                gp.obj[mapNum][i] = new OBJ_Television(gp); // Pastikan nama kelas ini benar
                if (gp.obj[mapNum][i] != null) {
                    // Tentukan koordinat X dan Y (DALAM SATUAN TILE) untuk TV
                    int tvTileX = 23; // GANTI DENGAN KOORDINAT X TV YANG DIINGINKAN (CONTOH)
                    int tvTileY = 27; // GANTI DENGAN KOORDINAT Y TV YANG DIINGINKAN (CONTOH)
                    gp.obj[mapNum][i].worldX = tvTileX * gp.tileSize;
                    gp.obj[mapNum][i].worldY = tvTileY * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[mapNum][i].name + " ditempatkan di map " + mapNum + " index " + i +
                                       " pada tile (" + tvTileX + "," + tvTileY + ")");
                } else {
                    System.err.println("AssetSetter: Gagal membuat instance OBJ_Television.");
                }
                i++; // Naikkan indeks untuk objek berikutnya
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