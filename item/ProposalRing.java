package item;
import main.GamePanel;
public class ProposalRing extends Item {
    public ProposalRing(GamePanel gp) {
        super("Proposal Ring", 1000, 500, gp); 
        setImage("/res/item/equipment/proposalring"); 
    }

    @Override
    public String getCategory() {
        return "Misc"; 
    }
    @Override
    public void use() {
        // Implementasi penggunaan cincin, misalnya untuk melamar
        System.out.println("Anda menggunakan " + getName() + " untuk melamar.");
    }
}

// public class ProposalRing extends Equipment {
//     public ProposalRing() {
//         super("Proposal Ring", "Gunakan alat ini untuk menikahi NPC!");
//         setImage("/res/item/equipment/proposalring.png");
//     }
// }
