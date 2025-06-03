package com.Spakborhills.item;

import com.Spakborhills.main.GamePanel;
import com.Spakborhills.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Store {
    private GamePanel gp;
    private List<Item> itemsForSale; 

    public Store(GamePanel gp) {
        this.gp = gp;
        this.itemsForSale = new ArrayList<>();
        populateStoreWithPricedItems();
        
    }

    private void populateStoreWithPricedItems() {
            if (ItemRepository.Parsnip_Seeds != null) itemsForSale.add(ItemRepository.Parsnip_Seeds);
            if (ItemRepository.Cauliflower_Seeds != null) itemsForSale.add(ItemRepository.Cauliflower_Seeds);
            if (ItemRepository.Potato_Seeds != null) itemsForSale.add(ItemRepository.Potato_Seeds);
            if (ItemRepository.Wheat_Seeds_Spring != null) itemsForSale.add(ItemRepository.Wheat_Seeds_Spring);
            if (ItemRepository.Blueberry_Seeds != null) itemsForSale.add(ItemRepository.Blueberry_Seeds);
            if (ItemRepository.Tomato_Seeds != null) itemsForSale.add(ItemRepository.Tomato_Seeds);
            if (ItemRepository.Hot_Pepper_Seeds != null) itemsForSale.add(ItemRepository.Hot_Pepper_Seeds);
            if (ItemRepository.Melon_Seeds != null) itemsForSale.add(ItemRepository.Melon_Seeds);
            if (ItemRepository.Cranberry_Seeds != null) itemsForSale.add(ItemRepository.Cranberry_Seeds);
            if (ItemRepository.Pumpkin_Seeds != null) itemsForSale.add(ItemRepository.Pumpkin_Seeds);
            if (ItemRepository.Wheat_Seeds_Fall != null) itemsForSale.add(ItemRepository.Wheat_Seeds_Fall);
            if (ItemRepository.Grape_Seeds != null) itemsForSale.add(ItemRepository.Grape_Seeds);

            // Hasil Panen (Crops) yang bisa dibeli
            if (ItemRepository.Parsnip != null && ItemRepository.Parsnip.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Parsnip);
            if (ItemRepository.Cauliflower != null && ItemRepository.Cauliflower.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Cauliflower);
            if (ItemRepository.Wheat != null && ItemRepository.Wheat.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Wheat);
            if (ItemRepository.Blueberry != null && ItemRepository.Blueberry.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Blueberry);
            if (ItemRepository.Tomato != null && ItemRepository.Tomato.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Tomato);
            if (ItemRepository.Pumpkin != null && ItemRepository.Pumpkin.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Pumpkin);
            if (ItemRepository.Grape != null && ItemRepository.Grape.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Grape);

            // Makanan (Food) yang bisa dibeli
            if (ItemRepository.Fish_n_Chips != null && ItemRepository.Fish_n_Chips.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Fish_n_Chips);
            if (ItemRepository.Baguette != null && ItemRepository.Baguette.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Baguette);
            if (ItemRepository.Sashimi != null && ItemRepository.Sashimi.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Sashimi);
            if (ItemRepository.Wine != null && ItemRepository.Wine.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Wine);
            if (ItemRepository.Pumpkin_Pie != null && ItemRepository.Pumpkin_Pie.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Pumpkin_Pie);
            if (ItemRepository.Veggie_Soup != null && ItemRepository.Veggie_Soup.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Veggie_Soup);
            if (ItemRepository.Fish_Stew != null && ItemRepository.Fish_Stew.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Fish_Stew);
            if (ItemRepository.Fish_Sandwich != null && ItemRepository.Fish_Sandwich.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Fish_Sandwich);
            if (ItemRepository.Cooked_Pigs_Head != null && ItemRepository.Cooked_Pigs_Head.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Cooked_Pigs_Head);

            // Item Misc yang bisa dibeli
            if (ItemRepository.Coal != null && ItemRepository.Coal.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Coal);
            if (ItemRepository.Firewood != null && ItemRepository.Firewood.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Firewood);
            if (ItemRepository.Egg != null && ItemRepository.Egg.getBuyPrice() > 0) itemsForSale.add(ItemRepository.Egg);

            // Proposal ring
            if (ItemRepository.ProposalRing != null && ItemRepository.ProposalRing.getBuyPrice() > 0) {
                itemsForSale.add(ItemRepository.ProposalRing);
            }
    }

    public void displayItemsForSale() {
        gp.ui.showMessage("Selamat datang di Toko! Lihat-lihat dulu barangnya:");
        System.out.println("===== BARANG TOKO =====");
        if (itemsForSale.isEmpty()) {
            System.out.println("Toko masih kosong hari ini.");
            return;
        }
        for (int i = 0; i < itemsForSale.size(); i++) {
            Item item = itemsForSale.get(i);
            System.out.printf("%d. %s - Harga: %dg [%s]%n", 
                    i + 1,
                    item.getName(),
                    item.getBuyPrice(), 
                    item.getCategory());
        }
        System.out.println("0. Keluar");
        System.out.println("======================");
    }

    public List<Item> getItemsForSale() {
        return itemsForSale;
    }

    public void processPurchase(Player player, int itemChoiceNomor, int quantity) {
        if (itemChoiceNomor < 1 || itemChoiceNomor > itemsForSale.size()) {
            gp.ui.showMessage("Pilihan tidak ada dalam daftar.");
            return;
        }
        if (quantity <= 0) {
            gp.ui.showMessage("Jumlah pembelian tidak valid.");
            return;
        }

        Item selectedItem = itemsForSale.get(itemChoiceNomor - 1);
        int pricePerUnit = selectedItem.getBuyPrice(); 
        int totalCost = pricePerUnit * quantity;

        if (player.gold < totalCost) {
            return;
        }

        player.changeGold(-totalCost); 

        boolean successfullyAdded = player.addItemToInventory(selectedItem, quantity);

        if (successfullyAdded) {
            gp.ui.showMessage("Anda membeli " + quantity + " " + selectedItem.getName() + " seharga " + totalCost + "g.");
            System.out.println("Transaksi berhasil: " + quantity + "x " + selectedItem.getName() + " (-" + totalCost + "g)");
        } else {
            player.changeGold(totalCost);
            gp.ui.showMessage("Gagal menambahkan " + selectedItem.getName() + " ke inventory (mungkin penuh?). Transaksi dibatalkan.");
            System.out.println("Transaksi gagal (inventory penuh?). Gold dikembalikan.");
        }
    }

    public void openStoreInterface(Player player) {
        Scanner scanner = gp.getSharedScanner(); 
        boolean isShopping = true;

        while (isShopping) {
            displayItemsForSale();
            System.out.print("Pilih nomor item untuk dibeli (0 untuk keluar): ");
            int itemNumberChoice = -1;
            if (scanner.hasNextInt()) {
                itemNumberChoice = scanner.nextInt();
                scanner.nextLine(); 
            } else {
                scanner.nextLine(); 
                gp.ui.showMessage("Input nomor tidak valid.");
                continue;
            }

            if (itemNumberChoice == 0) {
                isShopping = false;
                gp.ui.showMessage("Terima kasih telah berkunjung!");
            } else if (itemNumberChoice > 0 && itemNumberChoice <= itemsForSale.size()) {
                System.out.print("Berapa banyak yang ingin Anda beli?: ");
                int quantityToBuy = -1;
                 if (scanner.hasNextInt()) {
                    quantityToBuy = scanner.nextInt();
                    scanner.nextLine(); 
                } else {
                    scanner.nextLine(); 
                    gp.ui.showMessage("Input jumlah tidak valid.");
                    continue;
                }

                if (quantityToBuy > 0) {
                    processPurchase(player, itemNumberChoice, quantityToBuy);
                } else {
                    gp.ui.showMessage("Jumlah harus lebih dari nol.");
                }
                System.out.println("\nTekan Enter untuk kembali ke daftar toko...");
                scanner.nextLine();
            } else {
                gp.ui.showMessage("Nomor item tidak ada di daftar.");
            }
        }
    }
}