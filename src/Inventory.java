/**
 * Inventario del jugador: 36 ranuras (0-8 = hotbar) + 4 armadura + mano secundaria.
 * Implementa la lógica de inserción, extracción y movimiento de stacks.
 */
public class Inventory {
    public ItemStack[] main = new ItemStack[36];     // 0-8 = hotbar
    public ItemStack[] armor = new ItemStack[4];     // 0=casco, 1=peto, 2=pantalones, 3=botas
    public ItemStack offhand;
    public int selectedSlot = 0;

    public Inventory() {
        for (int i = 0; i < main.length; i++) main[i] = new ItemStack(null, 0);
        for (int i = 0; i < armor.length; i++) armor[i] = new ItemStack(null, 0);
        offhand = new ItemStack(null, 0);
    }

    /** Inserta un stack en el inventario. Devuelve el sobrante. */
    public int insert(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        int remaining = stack.count;

        // Primero intentar apilar en stacks existentes (hotbar primero)
        for (int pass = 0; pass < 2 && remaining > 0; pass++) {
            int start = pass == 0 ? 0 : 9;
            int end = pass == 0 ? 9 : 36;
            for (int i = start; i < end && remaining > 0; i++) {
                ItemStack slot = main[i];
                if (!slot.isEmpty() && slot.canStack(stack) && slot.count < slot.getMaxStack()) {
                    int canAdd = Math.min(remaining, slot.getMaxStack() - slot.count);
                    slot.count += canAdd;
                    remaining -= canAdd;
                }
            }
        }

        // Luego poner en ranuras vacías (hotbar primero)
        for (int i = 0; i < 36 && remaining > 0; i++) {
            if (main[i].isEmpty()) {
                int toPlace = Math.min(remaining, stack.getMaxStack());
                main[i] = new ItemStack(stack.type, toPlace);
                remaining -= toPlace;
            }
        }

        return remaining;
    }

    /** Intenta insertar un ítem. Devuelve true si cabe todo. */
    public boolean canInsert(ItemType type, int count) {
        int remaining = count;
        // Contar espacio en stacks existentes
        for (int i = 0; i < 36; i++) {
            if (!main[i].isEmpty() && main[i].type == type) {
                remaining -= (main[i].getMaxStack() - main[i].count);
            }
        }
        if (remaining <= 0) return true;
        // Contar ranuras vacías
        for (int i = 0; i < 36; i++) {
            if (main[i].isEmpty()) {
                remaining -= type.maxStack;
                if (remaining <= 0) return true;
            }
        }
        return remaining <= 0;
    }

    /** Devuelve el stack de la ranura activa */
    public ItemStack getHeld() {
        return main[selectedSlot];
    }

    /** Resta 1 del stack activo; devuelve el ItemType si queda vacío */
    public boolean consumeOne() {
        ItemStack held = main[selectedSlot];
        if (held.isEmpty()) return false;
        return held.remove(1);
    }

    /** Cuenta cuántos ítems de un tipo hay en el inventario */
    public int countOf(ItemType type) {
        int total = 0;
        for (int i = 0; i < 36; i++) {
            if (!main[i].isEmpty() && main[i].type == type) total += main[i].count;
        }
        return total;
    }

    /** Elimina count ítems del tipo dado. Devuelve true si se pudo. */
    public boolean removeItems(ItemType type, int count) {
        if (countOf(type) < count) return false;
        for (int i = 0; i < 36 && count > 0; i++) {
            if (!main[i].isEmpty() && main[i].type == type) {
                int take = Math.min(count, main[i].count);
                main[i].count -= take;
                count -= take;
                if (main[i].count <= 0) main[i] = new ItemStack(null, 0);
            }
        }
        return true;
    }

    /** Devuelve la defensa total de la armadura equipada */
    public int getArmorDefense() {
        int total = 0;
        for (int i = 0; i < 4; i++) {
            if (!armor[i].isEmpty()) total += armor[i].type.armorDefense();
        }
        return total;
    }

    /** True si el inventario está lleno */
    public boolean isFull() {
        for (int i = 0; i < 36; i++) {
            if (main[i].isEmpty()) return false;
            if (main[i].count < main[i].getMaxStack()) return false;
        }
        return true;
    }

    /** Devuelve true si tiene al menos count del tipo */
    public boolean has(ItemType type, int count) {
        return countOf(type) >= count;
    }
}
