package com.Spakborhills.main;

import com.Spakborhills.object.OBJ_House;
import com.Spakborhills.object.OBJ_HouseLake00;
import com.Spakborhills.object.OBJ_HouseLake01;
import com.Spakborhills.object.OBJ_HouseOcean;
import com.Spakborhills.object.OBJ_HouseRiver00;
import com.Spakborhills.object.OBJ_HouseRiver01;
import com.Spakborhills.object.OBJ_Pond;
import com.Spakborhills.object.OBJ_ShippingBin;
import com.Spakborhills.object.OBJ_StoreOcean;
import com.Spakborhills.object.OBJ_Stove;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
        System.out.println("ASSETSETTER: Memulai setObject()...");
        int i = 0;   

        try {
            if (0 < gp.obj.length && i < gp.obj[0].length) {
                gp.obj[0][i] = new OBJ_ShippingBin(gp);
                if (gp.obj[0][i] != null) {
                    int binTileX = 36;
                    int binTileY = 22;
                    gp.obj[0][i].worldX = binTileX * gp.tileSize;
                    gp.obj[0][i].worldY = binTileY * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[0][i].name + " ditempatkan di map " + 0 + " index " + i +
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
            if (0 < gp.obj.length && i < gp.obj[0].length) {
                gp.obj[0][i] = new OBJ_House(gp);
                if (gp.obj[0][i] != null) {
                    int houseTileX = 29;
                    int houseTileY = 22;
                    gp.obj[0][i].worldX = houseTileX * gp.tileSize;
                    gp.obj[0][i].worldY = houseTileY * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[0][i].name + " ditempatkan di map " + 0 + " index " + i +
                            " pada tile (" + houseTileX + "," + houseTileY + ")");
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
            if (0 < gp.obj.length && i < gp.obj[0].length) {
                gp.obj[5][i] = new OBJ_Stove(gp); 
                if (gp.obj[5][i] != null) {
                    int stoveTileX = 14; 
                    int stoveTileY = 5;
                    gp.obj[5][i].worldX = stoveTileX * gp.tileSize;
                    gp.obj[5][i].worldY = stoveTileY * gp.tileSize;
                    // gp.obj[0][i].name = "KomporDapur"; // Pastikan nama diset di konstruktor objeknya
                    System.out.println("AssetSetter: " + gp.obj[5][i].name + " ditempatkan di map " + 5 + " index " + i +
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

        try {
            if (0 < gp.obj.length && i < gp.obj[0].length) {
                gp.obj[0][i] = new OBJ_Pond(gp);
                if (gp.obj[0][i] != null) {
                    int pondTileX = 20;
                    int pondTileY = 34;
                    gp.obj[0][i].worldX = pondTileX * gp.tileSize;
                    gp.obj[0][i].worldY = pondTileY * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[0][i].name + " ditempatkan di map " + 0 + " index " + i +
                            " pada tile (" + pondTileX + "," + pondTileY + ")");
                } else {
                    System.err.println("AssetSetter: Gagal membuat instance OBJ_Television.");
                }
                i++;
            } else {
                System.err.println("AssetSetter: Indeks map atau objek di luar batas untuk Televisi (i=" + i + ")");
            }
        } catch (Exception e) {
            System.err.println("ASSETSETTER: ERROR saat menempatkan Pond!");
            e.printStackTrace();
        }

        try {
            if (1 < gp.obj.length && i < gp.obj[0].length) {
                gp.obj[1][i] = new OBJ_HouseOcean(gp);
                if (gp.obj[1][i] != null) {
                    int houseOceanTileX = 25;
                    int houseOceanTileY = 35;
                    gp.obj[1][i].worldX = houseOceanTileX * gp.tileSize;
                    gp.obj[1][i].worldY = houseOceanTileY * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[1][i].name + " ditempatkan di map " + 1 + " index " + i +
                            " pada tile (" + houseOceanTileX + "," + houseOceanTileY + ")");
                } else {
                    System.err.println("AssetSetter: Gagal membuat instance OBJ_Television.");
                }
                i++;
            } else {
                System.err.println("AssetSetter: Indeks map atau objek di luar batas untuk Televisi (i=" + i + ")");
            }
        } catch (Exception e) {
            System.err.println("ASSETSETTER: ERROR saat menempatkan Pond!");
            e.printStackTrace();
        }

        try {
            if (1 < gp.obj.length && i < gp.obj[0].length) {
                gp.obj[3][i] = new OBJ_HouseLake00(gp);
                if (gp.obj[3][i] != null) {
                    int houseLake00X = 13;
                    int houseLake00Y = 38;
                    gp.obj[3][i].worldX = houseLake00X * gp.tileSize;
                    gp.obj[3][i].worldY = houseLake00Y * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[3][i].name + " ditempatkan di map " + 3 + " index " + i +
                            " pada tile (" + houseLake00X + "," + houseLake00Y + ")");
                } else {
                    System.err.println("AssetSetter: Gagal membuat instance OBJ_Television.");
                }
                i++;
            } else {
                System.err.println("AssetSetter: Indeks map atau objek di luar batas untuk Televisi (i=" + i + ")");
            }
        } catch (Exception e) {
            System.err.println("ASSETSETTER: ERROR saat menempatkan Pond!");
            e.printStackTrace();
        }

        try {
            if (1 < gp.obj.length && i < gp.obj[0].length) {
                gp.obj[3][i] = new OBJ_HouseLake01(gp);
                if (gp.obj[3][i] != null) {
                    int houseLake01X = 42;
                    int houseLake01Y = 38;
                    gp.obj[3][i].worldX = houseLake01X * gp.tileSize;
                    gp.obj[3][i].worldY = houseLake01Y * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[3][i].name + " ditempatkan di map " + 3 + " index " + i +
                            " pada tile (" + houseLake01X + "," + houseLake01Y + ")");
                } else {
                    System.err.println("AssetSetter: Gagal membuat instance OBJ_Television.");
                }
                i++;
            } else {
                System.err.println("AssetSetter: Indeks map atau objek di luar batas untuk Televisi (i=" + i + ")");
            }
        } catch (Exception e) {
            System.err.println("ASSETSETTER: ERROR saat menempatkan Pond!");
            e.printStackTrace();
        }

        try {
            if (1 < gp.obj.length && i < gp.obj[0].length) {
                gp.obj[4][i] = new OBJ_HouseRiver00(gp);
                if (gp.obj[4][i] != null) {
                    int houseRiver00X = 13;
                    int houseRiver00Y = 38;
                    gp.obj[4][i].worldX = houseRiver00X * gp.tileSize;
                    gp.obj[4][i].worldY = houseRiver00Y * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[4][i].name + " ditempatkan di map " + 4 + " index " + i +
                            " pada tile (" + houseRiver00X + "," + houseRiver00Y + ")");
                } else {
                    System.err.println("AssetSetter: Gagal membuat instance OBJ_Television.");
                }
                i++;
            } else {
                System.err.println("AssetSetter: Indeks map atau objek di luar batas untuk Televisi (i=" + i + ")");
            }
        } catch (Exception e) {
            System.err.println("ASSETSETTER: ERROR saat menempatkan Pond!");
            e.printStackTrace();
        }

        try {
            if (1 < gp.obj.length && i < gp.obj[0].length) {
                gp.obj[4][i] = new OBJ_HouseRiver01(gp);
                if (gp.obj[4][i] != null) {
                    int houseRiver01X = 42;
                    int houseRiver011Y = 38;
                    gp.obj[4][i].worldX = houseRiver01X * gp.tileSize;
                    gp.obj[4][i].worldY = houseRiver011Y * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[4][i].name + " ditempatkan di map " + 4 + " index " + i +
                            " pada tile (" + houseRiver01X + "," + houseRiver011Y + ")");
                } else {
                    System.err.println("AssetSetter: Gagal membuat instance OBJ_Television.");
                }
                i++;
            } else {
                System.err.println("AssetSetter: Indeks map atau objek di luar batas untuk Televisi (i=" + i + ")");
            }
        } catch (Exception e) {
            System.err.println("ASSETSETTER: ERROR saat menempatkan Pond!");
            e.printStackTrace();
        }

        try {
            if (1 < gp.obj.length && i < gp.obj[0].length) {
                gp.obj[1][i] = new OBJ_StoreOcean(gp);
                if (gp.obj[1][i] != null) {
                    int storeOceanX = 30;
                    int storeOceanY = 35;
                    gp.obj[1][i].worldX = storeOceanX * gp.tileSize;
                    gp.obj[1][i].worldY = storeOceanY * gp.tileSize;
                    System.out.println("AssetSetter: " + gp.obj[1][i].name + " ditempatkan di map " + 1 + " index " + i +
                            " pada tile (" + storeOceanX + "," + storeOceanY + ")");
                } else {
                    System.err.println("AssetSetter: Gagal membuat instance OBJ_Television.");
                }
                i++;
            } else {
                System.err.println("AssetSetter: Indeks map atau objek di luar batas untuk Televisi (i=" + i + ")");
            }
        } catch (Exception e) {
            System.err.println("ASSETSETTER: ERROR saat menempatkan Pond!");
            e.printStackTrace();
        }

        System.out.println("ASSETSETTER: Selesai setObject(). Total slot objek yang coba diisi di map " + 0 + ": " + i);
    }
}