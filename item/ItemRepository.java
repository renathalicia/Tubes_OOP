package item;
import java.awt.image.CropImageFilter;
import java.security.PublicKey;

import item.Crop;
import item.Equipment;
import item.Fish;
import item.Food;
import item.Misc;
import item.Seed;
import main.GamePanel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ItemRepository {
    public static Crop Parsnip;
    public static Crop Cauliflower;
    public static Crop Potato;
    public static Crop Wheat;
    public static Crop Blueberry;
    public static Crop Tomato;
    public static Crop Hot_Pepper;
    public static Crop Melon;
    public static Crop Cranberry;
    public static Crop Pumpkin;
    public static Crop Grape;
    public static Food Fish_n_Chips;
    public static Food Baguette;
    public static Food Sashimi;
    public static Food Fugu;
    public static Food Wine;
    public static Food Pumpkin_Pie;
    public static Food Veggie_Soup;
    public static Food Fish_Stew;
    public static Food Spakbor_Salad;
    public static Food Fish_Sandwich;
    public static Food The_Legends_of_Spakbor;
    public static Food Cooked_Pigs_Head;
    public static Equipment Hoe;;
    public static Equipment Pickaxe;
    public static Equipment Fishing_Rod;
    public static Equipment Watering_Can;
    public static Equipment ProposalRing;
    public static Misc Coal;
    public static Misc Firewood;
    public static Seed Parsnip_Seeds;
    public static Seed Cauliflower_Seeds;
    public static Seed Potato_Seeds;
    public static Seed Wheat_Seeds_Spring;
    public static Seed Wheat_Seeds_Fall;
    public static Seed Blueberry_Seeds;
    public static Seed Tomato_Seeds;
    public static Seed Hot_Pepper_Seeds;
    public static Seed Melon_Seeds;
    public static Seed Cranberry_Seeds;
    public static Seed Pumpkin_Seeds;
    public static Seed Grape_Seeds;
    public static Fish Largemouth_Bass;
    public static Fish Rainbow_Trout;
    public static Fish Sturgeon;
    public static Fish Midnight_Carp;
    public static Fish Flounder;
    public static Fish Halibut;
    public static Fish Octopus;
    public static Fish Pufferfish;
    public static Fish Sardine;
    public static Fish Super_Cucumber;
    public static Fish Catfish;
    public static Fish Salmon;
    public static Fish Bullhead;
    public static Fish Carp;
    public static Fish Chub;
    public static Fish Legend;
    public static Fish Angler;
    public static Fish Crimsonfish;
    public static Fish Glacierfish;

    public static void initFish(GamePanel gp) {
        // Common Fish
        Bullhead = new Fish("Bullhead", gp, "Ikan lele umum yang hidup di danau.", "/res/item/fish/common/bullhead",
                Set.of("Any"), // Seasons
                List.of(), // Time (Any - ditangani khusus di kalkulasi harga jika list kosong)
                Set.of("Any"), // Weather
                Set.of("Mountain Lake"), 
                "Common");

        Carp = new Fish("Carp", gp, "Ikan air tawar yang umum di danau dan kolam.", "/res/item/fish/common/carp",
                Set.of("Any"),
                List.of(), // Time (Any)
                Set.of("Any"),
                Set.of("Mountain Lake", "Pond"),
                "Common");

        Chub = new Fish("Chub", gp, "Ikan kecil yang sering ditemui di sungai dan danau.", "/res/item/fish/common/chub",
                Set.of("Any"),
                List.of(), // Time (Any)
                Set.of("Any"),
                Set.of("Forest River", "Mountain Lake"),
                "Common");

        // Regular Fish
        Largemouth_Bass = new Fish("Largemouth Bass", gp, "Ikan predator danau.", "/res/item/fish/regular/largemouthbass",
                Set.of("Any"), // Seasons
                List.of(new Fish.TimeWindow(6, 18)), // 06.00-18.00
                Set.of("Any"), // Weather
                Set.of("Mountain Lake"), 
                "Regular");

        Rainbow_Trout = new Fish("Rainbow Trout", gp, "Ikan trout berwarna-warni yang muncul di musim panas saat cerah", "/res/item/fish/regular/rainbowtrout",
                Set.of("Summer"), // Seasons
                List.of(new Fish.TimeWindow(6, 18)), // 06.00-18.00
                Set.of("Sunny"), // Weather
                Set.of("Forest River", "Mountain Lake"), 
                "Regular");
        Sturgeon = new Fish("Sturgeon", gp, "Ikan besar yang hidup di danau pada musim panas dan dingin.", "/items/fish/sturgeon",
                Set.of("Summer", "Winter"), // Seasons: Summer, Winter [cite: 124]
                List.of(new Fish.TimeWindow(6, 18)), // Time: 06.00-18.00 [cite: 124]
                Set.of("Any"), // Weather [cite: 124]
                Set.of("Mountain Lake"), // Locations [cite: 124]
                "Regular");

        Midnight_Carp = new Fish("Midnight Carp", gp, "Jenis ikan mas yang aktif di malam hari pada musim gugur dan dingin.", "/items/fish/midnight_carp",
                Set.of("Winter", "Fall"), // Seasons: Winter, Fall [cite: 124]
                List.of(new Fish.TimeWindow(20, 2)), // Time: 20.00-02.00 [cite: 124]
                Set.of("Any"), // Weather [cite: 124]
                Set.of("Mountain Lake", "Pond"), // Locations [cite: 124]
                "Regular");

        Flounder = new Fish("Flounder", gp, "Ikan pipih yang bisa ditemukan di laut saat musim semi dan panas.", "/items/fish/flounder",
                Set.of("Spring", "Summer"), // Seasons: Spring, Summer [cite: 124]
                List.of(new Fish.TimeWindow(6, 22)), // Time: 06.00-22.00 [cite: 124]
                Set.of("Any"), // Weather [cite: 124]
                Set.of("Ocean"), // Locations [cite: 124]
                "Regular");

        Halibut = new Fish("Halibut", gp, "Ikan pipih besar dari laut, aktif pada jam-jam tertentu.", "/items/fish/halibut",
                Set.of("Any"), // Seasons [cite: 124]
                List.of(new Fish.TimeWindow(6, 11), new Fish.TimeWindow(19, 2)), // Time: 06.00-11.00, 19.00-02.00 [cite: 124]
                Set.of("Any"), // Weather [cite: 124]
                Set.of("Ocean"), // Locations [cite: 124]
                "Regular");

        Octopus = new Fish("Octopus", gp, "Gurita yang cerdas, ditemukan di laut saat musim panas.", "/items/fish/octopus",
                Set.of("Summer"), // Season: Summer [cite: 124]
                List.of(new Fish.TimeWindow(6, 22)), // Time: 06.00-22.00 [cite: 124]
                Set.of("Any"), // Weather [cite: 124]
                Set.of("Ocean"), // Locations [cite: 124]
                "Regular");

        Pufferfish = new Fish("Pufferfish", gp, "Ikan buntal yang mengembang, hanya muncul di laut saat musim panas dan cerah.", "/items/fish/pufferfish",
                Set.of("Summer"), // Season: Summer [cite: 124]
                List.of(new Fish.TimeWindow(0, 16)), // Time: 00.00-16.00 [cite: 124]
                Set.of("Sunny"), // Weather: Sunny [cite: 124]
                Set.of("Ocean"), // Locations [cite: 124]
                "Regular");

        Sardine = new Fish("Sardine", gp, "Ikan kecil bergerombol yang bisa ditemukan di laut.", "/items/fish/sardine",
                Set.of("Any"), // Seasons [cite: 124]
                List.of(new Fish.TimeWindow(6, 18)), // Time: 06.00-18.00 [cite: 124]
                Set.of("Any"), // Weather [cite: 124]
                Set.of("Ocean"), // Locations [cite: 124]
                "Regular");

        Super_Cucumber = new Fish("Super Cucumber", gp, "Teripang super yang langka, aktif di laut pada malam hari.", "/items/fish/super_cucumber",
                Set.of("Summer", "Fall", "Winter"), // Seasons: Summer, Fall, Winter [cite: 124]
                List.of(new Fish.TimeWindow(18, 2)), // Time: 18.00-02.00 [cite: 124]
                Set.of("Any"), // Weather [cite: 124]
                Set.of("Ocean"), // Locations [cite: 124]
                "Regular");

        Catfish = new Fish("Catfish", gp, "Ikan lele yang menyukai cuaca hujan.", "/items/fish/catfish",
                Set.of("Spring", "Summer", "Fall"), // Seasons: Spring, Summer, Fall [cite: 124]
                List.of(new Fish.TimeWindow(6, 22)), // Time: 06.00-22.00 [cite: 124]
                Set.of("Rainy"), // Weather: Rainy [cite: 124]
                Set.of("Forest River", "Pond"), // Locations [cite: 124]
                "Regular");

        Salmon= new Fish("Salmon", gp, "Ikan yang bermigrasi ke sungai pada musim gugur.", "/items/fish/salmon",
                Set.of("Fall"), // Season: Fall [cite: 124]
                List.of(new Fish.TimeWindow(6, 18)), // Time: 06.00-18.00 [cite: 124]
                Set.of("Any"), // Weather [cite: 124]
                Set.of("Forest River"), // Locations [cite: 124]
                "Regular");

        // Legendary Fish
        ANGLER = new Fish("Angler", gp, "Ikan aneh yang menggunakan umpannya untuk menarik mangsa di kedalaman kolam.", "/items/fish/angler",
            Set.of("Fall"), // Season: Fall
            List.of(new Fish.TimeWindow(8, 20)), // Time: 08.00-20.00
            Set.of("Any"), // Weather: Any
            Set.of("Pond"), // Locations: Pond
            "Legendary");

        CRIMSONFISH = new Fish("Crimsonfish", gp, "Ikan berwarna merah menyala yang hidup di perairan laut hangat saat musim panas.", "/items/fish/crimsonfish",
                Set.of("Summer"), // Season: Summer
                List.of(new Fish.TimeWindow(8, 20)), // Time: 08.00-20.00
                Set.of("Any"), // Weather: Any
                Set.of("Ocean"), // Locations: Ocean
                "Legendary");

        GLACIERFISH = new Fish("Glacierfish", gp, "Ikan yang sangat langka, ditemukan di perairan dingin hutan saat musim dingin.", "/items/fish/glacierfish",
                Set.of("Winter"), // Season: Winter
                List.of(new Fish.TimeWindow(8, 20)), // Time: 08.00-20.00
                Set.of("Any"), // Weather: Any
                Set.of("Forest River"), // Locations: Forest River
                "Legendary");

        Legend = new Fish("Legend", gp, "Ikan legendaris yang sangat sulit ditangkap!", "/res/item/fish/legendary/legend",
                Set.of("Spring"), // Seasons
                List.of(new Fish.TimeWindow(8, 20)), // 08.00-20.00
                Set.of("Rainy"), // Weather
                Set.of("Mountain Lake"), 
                "Legendary");
    }

    public static void initSeeds(GamePanel gp) {
        Parsnip_Seeds = new Seed("Parsnip Seeds", 20, 10, gp,
                "Bibit Parsnip. Tanam di musim semi. Panen dalam 1 hari.",
                1, "Parsnip", "Spring", "/res/item/seeds/parsnip1");

        Cauliflower_Seeds = new Seed("Cauliflower Seeds", 80, 40, gp,
                "Bibit Cauliflower. Tanam di musim semi. Panen dalam 5 hari.",
                5, "Cauliflower", "Spring", "/res/item/seeds/cauliflower1");

        Potato_Seeds = new Seed("Potato Seeds", 50, 25, gp,
                "Bibit Kentang. Tanam di musim semi. Panen dalam 3 hari.",
                3, "Potato", "Spring", "/res/item/seeds/potato1");

        Wheat_Seeds_Spring = new Seed("Wheat Seeds", 60, 30, gp,
                "Bibit Gandum. Tanam di musim semi atau gugur. Panen dalam 1 hari.",
                1, "Wheat", "Spring", "/res/item/seeds/wheat1");

        // Summer Seeds
        Blueberry_Seeds = new Seed("Blueberry Seeds", 80, 40, gp,
                "Bibit Blueberry. Tanam di musim panas. Panen dalam 7 hari.",
                7, "Blueberry", "Summer", "/res/item/seeds/blueberry1");

        Tomato_Seeds = new Seed("Tomato Seeds", 50, 25, gp,
                "Bibit Tomat. Tanam di musim panas. Panen dalam 3 hari.",
                3, "Tomato", "Summer", "/res/item/seeds/tomato1");

        Hot_Pepper_Seeds = new Seed("Hot Pepper Seeds", 40, 20, gp,
                "Bibit Cabai Pedas. Tanam di musim panas. Panen dalam 1 hari.",
                1, "Hot Pepper", "Summer", "/res/item/seeds/hotpepper1");

        Melon_Seeds = new Seed("Melon Seeds", 80, 40, gp,
                "Bibit Melon. Tanam di musim panas. Panen dalam 4 hari.",
                4, "Melon", "Summer", "/res/item/seeds/melon1");

        // Fall Seeds
        Cranberry_Seeds = new Seed("Cranberry Seeds", 100, 50, gp,
                "Bibit Cranberry. Tanam di musim gugur. Panen dalam 2 hari.",
                2, "Cranberry", "Fall", "/res/item/seeds/cranberry1");

        Pumpkin_Seeds = new Seed("Pumpkin Seeds", 150, 75, gp,
                "Bibit Labu. Tanam di musim gugur. Panen dalam 7 hari.",
                7, "Pumpkin", "Fall", "/res/item/seeds/pumpkin1");

        // Wheat untuk Fall 
        Wheat_Seeds_Fall = new Seed("Wheat Seeds", 60, 30, gp, 
                "Bibit Gandum. Tanam di musim semi atau gugur. Panen dalam 1 hari.",
                1, "Wheat", "Fall", "/res/item/seeds/wheat1");


        Grape_Seeds = new Seed("Grape Seeds", 60, 30, gp,
                "Bibit Anggur. Tanam di musim gugur. Panen dalam 3 hari.",
                3, "Grape", "Fall", "/res/item/seeds/grape1");

        // Winter: "Tidak ada seed yang dapat tumbuh saat winter"
    }
    public static void initMisc(GamePanel gp) {
        Coal = new Misc("Coal",
                50, 
                20, 
                gp,
                "Bongkahan arang, bahan bakar yang efisien.",
                "/res/item/misc/coal"); 

        Firewood = new Misc("Firewood",
                30, 
                10, 
                gp,
                "Potongan kayu bakar, cocok untuk api unggun atau memasak sederhana.",
                "/res/item/misc/firewood"); 
    }
    public static void initEquipment(GamePanel gp) {
        Hoe = new Equipment("Hoe",
                0,
                0, 
                gp,
                "Cangkul standar untuk membajak tanah.",
                "/res/item/equipment/hoe"); 

        Pickaxe = new Equipment("Pickaxe",
                0, 0, gp,
                "Kapak beliung untuk memecah batu atau membersihkan lahan.",
                "/res/item/equipment/pickaxe");

        Watering_Can = new Equipment("Watering Can",
                0, 0, gp,
                "Alat untuk menyiram tanaman agar tetap subur.",
                "/res/item/equipment/wateringcan"); 

        Fishing_Rod = new Equipment("Fishing Rod",
                0, 0, gp,
                "Pancingan untuk menangkap ikan di berbagai perairan.",
                "/res/item/equipment/fishingrod"); 
        
        ProposalRing = new Equipment("Proposal Ring",
                1000, 500, gp,
                "Cincin lamaran untuk KAWIN.",
                "/res/item/equipment/proposalring");
    }

    public static void initFood(GamePanel gp) {
        Fish_n_Chips = new Food("Fish n' Chips", 150, 135, gp,
                "Ikan goreng tepung renyah dengan kentang goreng.", 50,
                "/res/item/food/fishnchips");

        Baguette = new Food("Baguette", 100, 80, gp,
                "Roti Prancis panjang yang renyah di luar dan lembut di dalam.", 25,
                "/res/item/food/baguette");

        Sashimi = new Food("Sashimi", 300, 275, gp,
                "Irisan ikan segar mentah, disajikan dengan kecap asin.", 70,
                "/res/item/food/sashimi");

        Fugu = new Food("Fugu", 0, 135, gp, // Harga beli tidak ada di tabel, diasumsikan 0
                "Ikan buntal yang disiapkan dengan hati-hati, bisa berbahaya jika salah olah!", 50,
                "/res/item/food/fugu");

        Wine = new Food("Wine", 100, 90, gp,
                "Minuman fermentasi dari anggur, menghangatkan tubuh.", 20,
                "/res/item/food/wine");

        Pumpkin_Pie = new Food("Pumpkin Pie", 120, 100, gp,
                "Pai labu manis dengan bumbu rempah, hidangan penutup klasik.", 35,
                "/res/item/food/pumpkinpie");

        Veggie_Soup = new Food("Veggie Soup", 140, 120, gp,
                "Sup sayuran hangat yang menyehatkan.", 40,
                "/res/item/food/veggiesoup");

        Fish_Stew = new Food("Fish Stew", 280, 260, gp,
                "Rebusan ikan dengan berbagai sayuran dan bumbu kaya rasa.", 70,
                "/res/item/food/fishstew");

        Spakbor_Salad = new Food("Spakbor Salad", 0, 250, gp, 
                "Salad khas dengan saus spesial Pak Lurah Spakbor.", 70,
                "/res/item/food/spakborsalad");

        Fish_Sandwich = new Food("Fish Sandwich", 200, 180, gp,
                "Sandwich berisi fillet ikan goreng dan sayuran segar.", 50,
                "/res/item/food/fishsandwich");

        The_Legends_of_Spakbor = new Food("The Legends of Spakbor", 0, 2000, gp, 
                "Hidangan legendaris yang mengembalikan banyak energi!", 100,
                "/res/item/food/thelegendsofspakbor");

        Cooked_Pigs_Head = new Food("Cooked Pig's Head", 1000, 0, gp, // Harga jual 0g
                "Kepala babi utuh yang dimasak, hidangan pesta yang mengenyangkan.", 100,
                "/res/item/food/cookedpigshead");
    }

    public static void initCrops(GamePanel gp) {
        Parsnip = new Crop("Parsnip", 50, 35, gp,
                "Sayuran akar musim semi yang renyah dan manis.", 1,
                "/res/item/crops/parsnip");

        Cauliflower = new Crop("Cauliflower", 200, 150, gp,
                "Bunga besar berwarna putih yang serbaguna untuk dimasak.", 1,
                "/res/item/crops/cauliflower");

        Potato = new Crop("Potato", 0, 80, gp,
                "Umbi-umbian pokok yang bisa diolah menjadi berbagai hidangan.", 1,
                "/res/item/crops/potato");

        Wheat = new Crop("Wheat", 50, 30, gp,
                "Biji-bijian serbaguna, bahan dasar tepung.", 3,
                "/res/item/crops/wheat"); 

        Blueberry = new Crop("Blueberry", 150, 40, gp,
                "Buah beri kecil berwarna biru yang manis dan sedikit asam.", 3,
                "/res/item/crops/blueberry"); 

        Tomato = new Crop("Tomato", 90, 60, gp,
                "Buah merah berair yang sering dianggap sayuran.", 1,
                "/res/item/crops/tomato"); 

        Hot_Pepper = new Crop("Hot Pepper", 0, 40, gp,
                "Cabai pedas yang membara, menambah semangat pada masakan.", 1,
                "/res/item/crops/hotpepper"); 

        Melon = new Crop("Melon", 0, 250, gp,
                "Buah musim panas yang besar, manis, dan menyegarkan.", 1,
                "/res/item/crops/melon"); 

        Cranberry = new Crop("Cranberry", 0, 25, gp,
                "Buah beri merah asam yang sering diolah menjadi saus atau jus.", 10,
                "/res/item/crops/cranberry"); 

        Pumpkin = new Crop("Pumpkin", 300, 250, gp,
                "Labu besar berwarna oranye, identik dengan musim gugur.", 1,
                "/res/item/crops/pumpkin"); 

        Grape = new Crop("Grape", 100, 10, gp,
                "Buah kecil manis yang tumbuh bergerombol, bisa diolah menjadi wine.", 20,
                "/res/item/crops/grape");
    }

    public static void initializeAllItems(GamePanel gp) {
        initCrops(gp);
        initFood(gp);
        initEquipment(gp);
        initSeeds(gp);
        initFish(gp);
    }
}