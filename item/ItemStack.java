package item;

public class ItemStack {
    public Item item;
    public int quantity;

    public ItemStack(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (this.quantity < 0) { 
            this.quantity = 0;
        }
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    public void removeQuantity(int amount) {
        this.quantity -= amount;
        if (this.quantity < 0) {
            this.quantity = 0;
        }
    }

    public String getDisplayName() {
        if (item != null) {
            return item.getName() + (quantity > 1 ? " x" + quantity : "");
        }
        return "Item Kosong";
    }
}