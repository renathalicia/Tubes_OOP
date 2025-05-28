package item;

public class ProposalRing extends Item {
    public ProposalRing() {
        super("Proposal Ring", 1000, 500, "Sebuah cincin indah untuk melamar."); // Harga beli/jual bisa disesuaikan
        // setImage("/res/items/proposal_ring.png"); // Ganti dengan path gambar yang benar jika ada
    }

    @Override
    public String getCategory() {
        return "Misc"; // Atau "Equipment" atau kategori lain yang sesuai
    }
}