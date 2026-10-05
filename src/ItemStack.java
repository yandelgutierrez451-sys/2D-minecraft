/**
 * Pila de ítems: tipo, cantidad, durabilidad restante.
 * Es la unidad básica del inventario y los drops.
 */
public class ItemStack {
    public ItemType type;
    public int count;
    public int durability; // -1 si no tiene durabilidad

    public ItemStack(ItemType type, int count) {
        this.type = type;
        this.count = count;
        this.durability = (type != null && type.durability() > 0) ? type.durability() : -1;
    }

    public ItemStack(ItemType type) {
        this(type, 1);
    }

    public boolean isEmpty() {
        return type == null || count <= 0;
    }

    public int getMaxStack() {
        return type == null ? 0 : type.maxStack;
    }

    /** Añade hasta maxStack y devuelve cuánto sobra */
    public int add(int amount) {
        int canAdd = Math.min(amount, getMaxStack() - count);
        count += canAdd;
        return amount - canAdd;
    }

    /** Resta cantidad; devuelve true si queda vacío */
    public boolean remove(int amount) {
        count -= amount;
        return count <= 0;
    }

    /** Usa 1 de durabilidad; devuelve true si se rompió */
    public boolean useDurability() {
        if (durability <= 0) return true;
        if (durability == -1) return false;
        durability--;
        if (durability <= 0) {
            count--;
            return true;
        }
        return false;
    }

    public ItemStack copy() {
        ItemStack s = new ItemStack(type, count);
        s.durability = durability;
        return s;
    }

    /** True si es el mismo ítem y se puede apilar */
    public boolean canStack(ItemStack other) {
        return other != null && other.type == this.type && !other.isEmpty();
    }
}
