/**
 * Define todos los tipos de bloque del juego.
 * Cada bloque tiene propiedades que controlan su comportamiento
 * sin necesidad de if/else por objeto: todo se lee de los campos.
 */
public enum BlockType {
    // Aire
    AIR("air", 0f, -1, false, false, false, true, false, false, 0),
    // Terreno
    STONE("stone", 1.5f, 1, true, false, false, false, false, false, 0),
    GRASS("grass", 0.6f, 2, true, false, false, false, false, false, 0),
    DIRT("dirt", 0.5f, 2, true, false, false, false, false, false, 0),
    COBBLESTONE("cobblestone", 2.0f, 1, true, false, false, false, false, false, 0),
    BEDROCK("bedrock", -1f, -1, true, false, false, false, false, false, 0),
    // Minerales
    COAL_ORE("coal_ore", 3.0f, 1, true, false, false, false, false, false, 0),
    IRON_ORE("iron_ore", 3.0f, 1, true, false, false, false, false, false, 0),
    GOLD_ORE("gold_ore", 3.0f, 2, true, false, false, false, false, false, 0),
    DIAMOND_ORE("diamond_ore", 3.0f, 2, true, false, false, false, false, false, 0),
    // Maderas
    OAK_LOG("oak_log", 2.0f, 0, true, false, false, false, true, false, 0),
    OAK_LEAVES("oak_leaves", 0.2f, -1, false, false, true, false, false, true, 0),
    OAK_PLANKS("oak_planks", 2.0f, 0, true, false, false, false, true, false, 0),
    // Líquidos
    WATER("water", 100f, -1, false, true, true, false, false, false, 0),
    LAVA("lava", 100f, -1, false, true, true, false, false, false, 15),
    // Arena y gravedad
    SAND("sand", 0.5f, 2, true, false, false, false, false, false, 0),
    GRAVEL("gravel", 0.6f, 2, true, false, false, false, false, false, 0),
    // Bloques varios
    CRAFTING_TABLE("crafting_table", 2.5f, 0, true, false, false, false, true, false, 0),
    FURNACE("furnace", 3.5f, 1, true, false, false, false, false, false, 0),
    TORCH("torch", 0f, -1, false, false, true, true, false, false, 14),
    GLASS("glass", 0.3f, -1, true, false, true, false, false, false, 0),
    SANDSTONE("sandstone", 0.8f, 1, true, false, false, false, false, false, 0),
    // Bloques de minerales compactos
    IRON_BLOCK("iron_block", 5.0f, 1, true, false, false, false, false, false, 0),
    GOLD_BLOCK("gold_block", 3.0f, 2, true, false, false, false, false, false, 0),
    DIAMOND_BLOCK("diamond_block", 5.0f, 2, true, false, false, false, false, false, 0),
    COAL_BLOCK("coal_block", 5.0f, 1, true, false, false, false, true, false, 0),
    // Obsidiana
    OBSIDIAN("obsidian", 50f, 2, true, false, false, false, false, false, 0),
    // Redstone
    REDSTONE_WIRE("redstone_wire", 0f, -1, false, false, true, true, false, false, 0),
    REDSTONE_TORCH("redstone_torch", 0f, -1, false, false, true, true, false, false, 7),
    REDSTONE_BLOCK("redstone_block", 5.0f, 1, true, false, false, false, false, false, 0),
    LEVER("lever", 0.5f, -1, false, false, true, true, false, false, 0),
    STONE_BUTTON("stone_button", 0.5f, -1, false, false, true, true, false, false, 0),
    REPEATER("repeater", 0f, -1, false, false, true, true, false, false, 0),
    PISTON("piston", 1.5f, 0, true, false, false, false, false, false, 0),
    REDSTONE_LAMP("redstone_lamp", 0.3f, -1, true, false, false, false, false, false, 0),
    NOTE_BLOCK("note_block", 0.8f, 0, true, false, false, false, true, false, 0),
    // Decoración / funcional
    TALL_GRASS("tall_grass", 0f, -1, false, false, true, true, false, true, 0),
    FLOWER("flower", 0f, -1, false, false, true, true, false, true, 0),
    SNOW_LAYER("snow_layer", 0.1f, -1, false, false, true, true, false, false, 0),
    ICE("ice", 0.5f, -1, true, false, true, false, false, false, 0),
    SLIME_BLOCK("slime_block", 0f, -1, true, false, true, false, false, false, 0),
    HONEY_BLOCK("honey_block", 0f, -1, true, false, true, false, false, false, 0),
    SOUL_SAND("soul_sand", 0.5f, 2, true, false, false, false, false, false, 0),
    CACTUS("cactus", 0.4f, -1, true, false, false, false, false, false, 0),
    WOOL("wool", 0.8f, -1, true, false, false, false, true, false, 0),
    FARMLAND("farmland", 0.6f, 2, true, false, false, false, false, false, 0),
    // Escaleras y losas
    OAK_STAIRS("oak_stairs", 2.0f, 0, true, false, true, false, true, false, 0),
    STONE_SLAB("stone_slab", 2.0f, 1, true, false, true, false, false, false, 0),
    COBBLESTONE_SLAB("cobblestone_slab", 2.0f, 1, true, false, true, false, false, false, 0),
    // Puertas, vallas, etc.
    OAK_DOOR("oak_door", 3.0f, 0, true, false, true, false, true, false, 0),
    OAK_FENCE("oak_fence", 2.0f, 0, true, false, true, false, true, false, 0),
    LADDER("ladder", 0.4f, -1, false, false, true, true, false, false, 0),
    CHEST("chest", 2.5f, -1, true, false, true, false, true, false, 0),
    // Bloques de tierra cultivable
    WHEAT("wheat", 0f, -1, false, false, true, true, false, true, 0),
    // Nether
    NETHERRACK("netherrack", 0.4f, 1, true, false, false, false, false, false, 0),
    NETHER_BRICKS("nether_bricks", 2.0f, 1, true, false, false, false, false, false, 0),
    // Piedras pulidas
    POLISHED_STONE("polished_stone", 1.5f, 1, true, false, false, false, false, false, 0),
    BRICKS("bricks", 2.0f, 1, true, false, false, false, false, false, 0),
    // Hopper, dispenser, dropper
    HOPPER("hopper", 3.0f, 1, true, false, false, false, false, false, 0),
    DISPENSER("dispenser", 3.5f, 1, true, false, false, false, false, false, 0),
    DROPPER("dropper", 3.5f, 1, true, false, false, false, false, false, 0),
    TNT("tnt", 0f, -1, true, false, false, false, true, false, 0),
    // Bloque de esponja
    SPONGE("sponge", 0.6f, -1, true, false, false, false, false, false, 0);

    // Campos
    public final String name;
    public final float hardness;
    public final int toolTier;       // -1 = mano, 0 = madera, 1 = piedra, 2 = hierro+
    public final boolean solid;
    public final boolean liquid;
    public final boolean transparent;
    public final boolean replaceable;
    public final boolean flammable;
    public final boolean fallable;   // arena, grava, yunque, etc.
    public final int lightEmission;  // 0-15

    BlockType(String name, float hardness, int toolTier, boolean solid, boolean liquid,
              boolean transparent, boolean replaceable, boolean flammable, boolean fallable,
              int lightEmission) {
        this.name = name;
        this.hardness = hardness;
        this.toolTier = toolTier;
        this.solid = solid;
        this.liquid = liquid;
        this.transparent = transparent;
        this.replaceable = replaceable;
        this.flammable = flammable;
        this.fallable = fallable;
        this.lightEmission = lightEmission;
    }

    /** Devuelve true si el bloque no es instantáneo de romper */
    public boolean isBreakable() {
        return hardness >= 0;
    }
}
