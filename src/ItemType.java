import java.awt.image.BufferedImage;

/**
 * Define todos los tipos de ítem del juego.
 * Todo bloque tiene un ítem asociado, pero no todo ítem es bloque.
 */
public enum ItemType {
    // Bloques como ítems
    STONE(BlockType.STONE, 64),
    GRASS(BlockType.GRASS, 64),
    DIRT(BlockType.DIRT, 64),
    COBBLESTONE(BlockType.COBBLESTONE, 64),
    COAL_ORE(BlockType.COAL_ORE, 64),
    IRON_ORE(BlockType.IRON_ORE, 64),
    GOLD_ORE(BlockType.GOLD_ORE, 64),
    DIAMOND_ORE(BlockType.DIAMOND_ORE, 64),
    OAK_LOG(BlockType.OAK_LOG, 64),
    OAK_LEAVES(BlockType.OAK_LEAVES, 64),
    OAK_PLANKS(BlockType.OAK_PLANKS, 64),
    SAND(BlockType.SAND, 64),
    GRAVEL(BlockType.GRAVEL, 64),
    CRAFTING_TABLE(BlockType.CRAFTING_TABLE, 64),
    FURNACE(BlockType.FURNACE, 64),
    TORCH(BlockType.TORCH, 64),
    GLASS(BlockType.GLASS, 64),
    SANDSTONE(BlockType.SANDSTONE, 64),
    IRON_BLOCK(BlockType.IRON_BLOCK, 64),
    GOLD_BLOCK(BlockType.GOLD_BLOCK, 64),
    DIAMOND_BLOCK(BlockType.DIAMOND_BLOCK, 64),
    COAL_BLOCK(BlockType.COAL_BLOCK, 64),
    OBSIDIAN(BlockType.OBSIDIAN, 64),
    REDSTONE_WIRE(BlockType.REDSTONE_WIRE, 64),
    REDSTONE_TORCH(BlockType.REDSTONE_TORCH, 64),
    REDSTONE_BLOCK(BlockType.REDSTONE_BLOCK, 64),
    LEVER(BlockType.LEVER, 64),
    STONE_BUTTON(BlockType.STONE_BUTTON, 64),
    REPEATER(BlockType.REPEATER, 64),
    PISTON(BlockType.PISTON, 64),
    REDSTONE_LAMP(BlockType.REDSTONE_LAMP, 64),
    NOTE_BLOCK(BlockType.NOTE_BLOCK, 64),
    TALL_GRASS(BlockType.TALL_GRASS, 64),
    FLOWER(BlockType.FLOWER, 64),
    ICE(BlockType.ICE, 64),
    SLIME_BLOCK(BlockType.SLIME_BLOCK, 64),
    HONEY_BLOCK(BlockType.HONEY_BLOCK, 64),
    SOUL_SAND(BlockType.SOUL_SAND, 64),
    CACTUS(BlockType.CACTUS, 64),
    WOOL(BlockType.WOOL, 64),
    FARMLAND(BlockType.FARMLAND, 64),
    OAK_STAIRS(BlockType.OAK_STAIRS, 64),
    OAK_SLAB(null, 64),
    OAK_TRAPDOOR(null, 64),
    OAK_BUTTON(null, 64),
    OAK_PRESSURE_PLATE(null, 64),
    OAK_SIGN(null, 16),
    STONE_SLAB(BlockType.STONE_SLAB, 64),
    COBBLESTONE_SLAB(BlockType.COBBLESTONE_SLAB, 64),
    OAK_DOOR(BlockType.OAK_DOOR, 64),
    OAK_FENCE(BlockType.OAK_FENCE, 64),
    LADDER(BlockType.LADDER, 64),
    CHEST(BlockType.CHEST, 64),
    WHEAT(BlockType.WHEAT, 64),
    NETHERRACK(BlockType.NETHERRACK, 64),
    POLISHED_STONE(BlockType.POLISHED_STONE, 64),
    BRICKS(BlockType.BRICKS, 64),
    HOPPER(BlockType.HOPPER, 64),
    DISPENSER(BlockType.DISPENSER, 64),
    DROPPER(BlockType.DROPPER, 64),
    TNT(BlockType.TNT, 64),
    SPONGE(BlockType.SPONGE, 64),
    WATER(null, 16),     // cubo de agua como ítem
    LAVA(null, 16),      // cubo de lava como ítem

    // Ítems que no son bloques
    COAL(null, 64),
    RAW_IRON(null, 64),
    IRON_INGOT(null, 64),
    RAW_GOLD(null, 64),
    GOLD_INGOT(null, 64),
    DIAMOND(null, 64),
    STICK(null, 64),
    WOODEN_PICKAXE(null, 1),
    WOODEN_AXE(null, 1),
    WOODEN_SHOVEL(null, 1),
    WOODEN_SWORD(null, 1),
    STONE_PICKAXE(null, 1),
    STONE_AXE(null, 1),
    STONE_SHOVEL(null, 1),
    STONE_SWORD(null, 1),
    IRON_PICKAXE(null, 1),
    IRON_AXE(null, 1),
    IRON_SHOVEL(null, 1),
    IRON_SWORD(null, 1),
    GOLDEN_PICKAXE(null, 1),
    GOLDEN_AXE(null, 1),
    GOLDEN_SHOVEL(null, 1),
    GOLDEN_SWORD(null, 1),
    DIAMOND_PICKAXE(null, 1),
    DIAMOND_AXE(null, 1),
    DIAMOND_SHOVEL(null, 1),
    DIAMOND_SWORD(null, 1),
    BREAD(null, 64),
    WHEAT_SEEDS(null, 64),
    BONE(null, 64),
    BONE_MEAL(null, 64),
    BUCKET(null, 16),
    WATER_BUCKET(null, 1),
    LAVA_BUCKET(null, 1),
    APPLE(null, 64),
    GOLDEN_APPLE(null, 64),
    STRING(null, 64),
    ARROW(null, 64),
    BOW(null, 1),
    LEATHER_HELMET(null, 1),
    LEATHER_CHESTPLATE(null, 1),
    LEATHER_LEGGINGS(null, 1),
    LEATHER_BOOTS(null, 1),
    IRON_HELMET(null, 1),
    IRON_CHESTPLATE(null, 1),
    IRON_LEGGINGS(null, 1),
    IRON_BOOTS(null, 1),
    GOLDEN_HELMET(null, 1),
    GOLDEN_CHESTPLATE(null, 1),
    GOLDEN_LEGGINGS(null, 1),
    GOLDEN_BOOTS(null, 1),
    DIAMOND_HELMET(null, 1),
    DIAMOND_CHESTPLATE(null, 1),
    DIAMOND_LEGGINGS(null, 1),
    DIAMOND_BOOTS(null, 1),
    BIRCH_LOG(null, 64),
    SPRUCE_LOG(null, 64),
    BIRCH_PLANKS(null, 64),
    SPRUCE_PLANKS(null, 64),
    BIRCH_DOOR(null, 64),
    SPRUCE_DOOR(null, 64),
    BIRCH_FENCE(null, 64),
    SPRUCE_FENCE(null, 64),
    BIRCH_STAIRS(null, 64),
    SPRUCE_STAIRS(null, 64),
    BIRCH_SLAB(null, 64),
    SPRUCE_SLAB(null, 64),
    BIRCH_TRAPDOOR(null, 64),
    SPRUCE_TRAPDOOR(null, 64),
    BIRCH_BUTTON(null, 64),
    SPRUCE_BUTTON(null, 64),
    BIRCH_PRESSURE_PLATE(null, 64),
    SPRUCE_PRESSURE_PLATE(null, 64),
    BIRCH_SIGN(null, 16),
    SPRUCE_SIGN(null, 16),
    ANVIL(null, 64),
    SHIELD(null, 1),
    BOWL(null, 64),
    MUSHROOM_STEW(null, 1),
    FLINT_AND_STEEL(null, 1),
    COMPASS(null, 64),
    CLOCK(null, 64),
    FISHING_ROD(null, 1),
    CROSSBOW(null, 1),
    SHEARS(null, 1),
    // Materiales de netherita
    NETHERITE_INGOT(null, 64),
    NETHERITE_SCRAP(null, 64),
    ANCIENT_DEBRIS(null, 64),
    NETHERITE_BLOCK(null, 64),
    NETHERITE_PICKAXE(null, 1),
    NETHERITE_AXE(null, 1),
    NETHERITE_SHOVEL(null, 1),
    NETHERITE_SWORD(null, 1),
    NETHERITE_HELMET(null, 1),
    NETHERITE_CHESTPLATE(null, 1),
    NETHERITE_LEGGINGS(null, 1),
    NETHERITE_BOOTS(null, 1),
    EMERALD(null, 64),
    LAPIS(null, 64),
    REDSTONE(null, 64),
    GUNPOWDER(null, 64),
    ENDER_PEARL(null, 16),
    NETHER_STAR(null, 64),
    EXPERIENCE_BOTTLE(null, 64),
    SUGAR_CANE(null, 64),
    PUMPKIN(null, 64),
    MELON(null, 64),
    POTATO(null, 64),
    CARROT(null, 64),
    BEETROOT(null, 64),
    COOKED_BEEF(null, 64),
    RAW_BEEF(null, 64),
    COOKED_PORKCHOP(null, 64),
    RAW_PORKCHOP(null, 64),
    RAW_CHICKEN(null, 64),
    COOKED_CHICKEN(null, 64),
    RAW_COD(null, 64),
    COOKED_COD(null, 64),
    COOKIE(null, 64),
    CAKE(null, 1),
    PUMPKIN_PIE(null, 64),
    GOLDEN_CARROT(null, 64),
    SPIDER_EYE(null, 64),
    ROTTEN_FLESH(null, 64),
    LEATHER(null, 64),
    FEATHER(null, 64),
    SLIMEBALL(null, 64),
    HONEYCOMB(null, 64),
    FLINT(null, 64),
    PRISMARINE_SHARD(null, 64),
    QUARTZ(null, 64),
    BLAZE_ROD(null, 64),
    GHAST_TEAR(null, 64),
    PHANTOM_MEMBRANE(null, 64),
    SCUTE(null, 64),
    SADDLE(null, 1),
    BOOK(null, 64),
    ENCHANTED_BOOK(null, 1),
    WRITABLE_BOOK(null, 16),
    PAPER(null, 64),
    MAP(null, 64),
    // Colores de lana/tinte
    WHITE_DYE(null, 64),
    ORANGE_DYE(null, 64),
    MAGENTA_DYE(null, 64),
    LIGHT_BLUE_DYE(null, 64),
    YELLOW_DYE(null, 64),
    LIME_DYE(null, 64),
    PINK_DYE(null, 64),
    GRAY_DYE(null, 64),
    LIGHT_GRAY_DYE(null, 64),
    CYAN_DYE(null, 64),
    PURPLE_DYE(null, 64),
    BLUE_DYE(null, 64),
    BROWN_DYE(null, 64),
    GREEN_DYE(null, 64),
    RED_DYE(null, 64),
    BLACK_DYE(null, 64),
    WHITE_WOOL(null, 64),
    ORANGE_WOOL(null, 64),
    MAGENTA_WOOL(null, 64),
    LIGHT_BLUE_WOOL(null, 64),
    YELLOW_WOOL(null, 64),
    LIME_WOOL(null, 64),
    PINK_WOOL(null, 64),
    GRAY_WOOL(null, 64),
    LIGHT_GRAY_WOOL(null, 64),
    CYAN_WOOL(null, 64),
    PURPLE_WOOL(null, 64),
    BLUE_WOOL(null, 64),
    BROWN_WOOL(null, 64),
    GREEN_WOOL(null, 64),
    RED_WOOL(null, 64),
    BLACK_WOOL(null, 64),
    WHITE_CARPET(null, 64),
    ORANGE_CARPET(null, 64),
    YELLOW_CARPET(null, 64),
    BLUE_CARPET(null, 64),
    RED_CARPET(null, 64),
    GREEN_CARPET(null, 64),
    BLACK_CARPET(null, 64),
    GRAY_CARPET(null, 64),
    WHITE_BED(null, 1),
    RED_BED(null, 1),
    BLUE_BED(null, 1),
    GREEN_BED(null, 1),
    YELLOW_BED(null, 1),
    BLACK_BED(null, 1),
    ORANGE_BED(null, 1),
    // Piedras variantes
    ANDESITE(null, 64),
    DIORITE(null, 64),
    GRANITE(null, 64),
    POLISHED_ANDESITE(null, 64),
    POLISHED_DIORITE(null, 64),
    POLISHED_GRANITE(null, 64),
    MOSSY_COBBLESTONE(null, 64),
    STONE_BRICKS(null, 64),
    CRACKED_STONE_BRICKS(null, 64),
    CHISELED_STONE_BRICKS(null, 64),
    // Arenisca
    SMOOTH_SANDSTONE(null, 64),
    CHISELED_SANDSTONE(null, 64),
    CUT_SANDSTONE(null, 64),
    // Cuarzo
    QUARTZ_BLOCK(null, 64),
    SMOOTH_QUARTZ(null, 64),
    CHISELED_QUARTZ_BLOCK(null, 64),
    // Prismarina
    PRISMARINE(null, 64),
    PRISMARINE_BRICKS(null, 64),
    DARK_PRISMARINE(null, 64),
    // Terracota
    TERRACOTTA(null, 64),
    WHITE_TERRACOTTA(null, 64),
    RED_TERRACOTTA(null, 64),
    BLUE_TERRACOTTA(null, 64),
    YELLOW_TERRACOTTA(null, 64),
    GREEN_TERRACOTTA(null, 64),
    BLACK_TERRACOTTA(null, 64),
    ORANGE_TERRACOTTA(null, 64),
    // Vidrio tintado
    WHITE_STAINED_GLASS(null, 64),
    RED_STAINED_GLASS(null, 64),
    BLUE_STAINED_GLASS(null, 64),
    YELLOW_STAINED_GLASS(null, 64),
    GREEN_STAINED_GLASS(null, 64),
    BLACK_STAINED_GLASS(null, 64),
    ORANGE_STAINED_GLASS(null, 64),
    // Bloques de hormigón
    WHITE_CONCRETE(null, 64),
    RED_CONCRETE(null, 64),
    BLUE_CONCRETE(null, 64),
    YELLOW_CONCRETE(null, 64),
    GREEN_CONCRETE(null, 64),
    BLACK_CONCRETE(null, 64),
    ORANGE_CONCRETE(null, 64),
    WHITE_CONCRETE_POWDER(null, 64),
    RED_CONCRETE_POWDER(null, 64),
    BLUE_CONCRETE_POWDER(null, 64),
    YELLOW_CONCRETE_POWDER(null, 64),
    GREEN_CONCRETE_POWDER(null, 64),
    BLACK_CONCRETE_POWDER(null, 64),
    ORANGE_CONCRETE_POWDER(null, 64),
    // Velas
    WHITE_CANDLE(null, 64),
    RED_CANDLE(null, 64),
    BLUE_CANDLE(null, 64),
    YELLOW_CANDLE(null, 64),
    GREEN_CANDLE(null, 64),
    BLACK_CANDLE(null, 64),
    ORANGE_CANDLE(null, 64),
    // Paneles de vidrio
    WHITE_STAINED_GLASS_PANE(null, 64),
    RED_STAINED_GLASS_PANE(null, 64),
    BLUE_STAINED_GLASS_PANE(null, 64),
    YELLOW_STAINED_GLASS_PANE(null, 64),
    GREEN_STAINED_GLASS_PANE(null, 64),
    BLACK_STAINED_GLASS_PANE(null, 64),
    ORANGE_STAINED_GLASS_PANE(null, 64),
    // Cofres de shulker tintados
    WHITE_SHULKER_BOX(null, 1),
    RED_SHULKER_BOX(null, 1),
    BLUE_SHULKER_BOX(null, 1),
    YELLOW_SHULKER_BOX(null, 1),
    GREEN_SHULKER_BOX(null, 1),
    BLACK_SHULKER_BOX(null, 1),
    ORANGE_SHULKER_BOX(null, 1),
    // Estandartes
    WHITE_BANNER(null, 16),
    RED_BANNER(null, 16),
    BLUE_BANNER(null, 16),
    YELLOW_BANNER(null, 16),
    GREEN_BANNER(null, 16),
    BLACK_BANNER(null, 16),
    ORANGE_BANNER(null, 16),
    // Ladrillos del Nether
    NETHER_BRICK(null, 64),
    NETHER_BRICKS(null, 64),
    RED_NETHER_BRICKS(null, 64),
    CRIMSON_PLANKS(null, 64),
    WARPED_PLANKS(null, 64),
    CRIMSON_STEM(null, 64),
    WARPED_STEM(null, 64),
    // Muros
    COBBLESTONE_WALL(null, 64),
    STONE_BRICK_WALL(null, 64),
    BRICK_WALL(null, 64),
    // Más escaleras, losas
    STONE_STAIRS(null, 64),
    COBBLESTONE_STAIRS(null, 64),
    BRICK_STAIRS(null, 64),
    STONE_BRICK_STAIRS(null, 64),
    SANDSTONE_STAIRS(null, 64),
    NETHER_BRICK_STAIRS(null, 64),
    QUARTZ_STAIRS(null, 64),
    STONE_BRICK_SLAB(null, 64),
    BRICK_SLAB(null, 64),
    SANDSTONE_SLAB(null, 64),
    NETHER_BRICK_SLAB(null, 64),
    QUARTZ_SLAB(null, 64),
    // Cobre
    COPPER_ORE(null, 64),
    RAW_COPPER(null, 64),
    COPPER_INGOT(null, 64),
    COPPER_BLOCK(null, 64),
    // Lingotes de netherita
    NETHERITE_SCRAP_ITEM(null, 64);

    public final BlockType blockType;  // null si no es bloque
    public final int maxStack;

    ItemType(BlockType blockType, int maxStack) {
        this.blockType = blockType;
        this.maxStack = maxStack;
    }

    /** Devuelve true si este ítem representa un bloque colocable */
    public boolean isBlock() {
        return blockType != null && blockType != BlockType.AIR;
    }

    /** Devuelve true si es herramienta (pico, hacha, pala, espada) */
    public boolean isTool() {
        String n = name();
        return n.endsWith("_PICKAXE") || n.endsWith("_AXE") || n.endsWith("_SHOVEL") || n.endsWith("_SWORD")
                || n.equals("SHEARS") || n.equals("FLINT_AND_STEEL") || n.equals("FISHING_ROD")
                || n.equals("BOW") || n.equals("CROSSBOW");
    }

    /** Devuelve true si es armadura */
    public boolean isArmor() {
        String n = name();
        return n.endsWith("_HELMET") || n.endsWith("_CHESTPLATE") || n.endsWith("_LEGGINGS") || n.endsWith("_BOOTS");
    }

    /** Devuelve el slot de armadura: 0=casco, 1=peto, 2=pantalones, 3=botas */
    public int armorSlot() {
        String n = name();
        if (n.endsWith("_HELMET")) return 0;
        if (n.endsWith("_CHESTPLATE")) return 1;
        if (n.endsWith("_LEGGINGS")) return 2;
        if (n.endsWith("_BOOTS")) return 3;
        return -1;
    }

    /** Devuelve el tier de herramienta: 0=madera, 1=piedra, 2=hierro, 3=oro, 4=diamante, 5=netherita */
    public int toolTier() {
        String n = name();
        if (n.startsWith("WOODEN")) return 0;
        if (n.startsWith("STONE")) return 1;
        if (n.startsWith("IRON")) return 2;
        if (n.startsWith("GOLDEN")) return 3;
        if (n.startsWith("DIAMOND")) return 4;
        if (n.startsWith("NETHERITE")) return 5;
        return -1;
    }

    /** Devuelve el tipo de herramienta: "pickaxe", "axe", "shovel", "sword" */
    public String toolType() {
        String n = name();
        if (n.endsWith("_PICKAXE")) return "pickaxe";
        if (n.endsWith("_AXE")) return "axe";
        if (n.endsWith("_SHOVEL")) return "shovel";
        if (n.endsWith("_SWORD")) return "sword";
        return "";
    }

    /** Velocidad de minería según el tier */
    public float miningSpeed() {
        int tier = toolTier();
        switch (tier) {
            case 0: return 2f;
            case 1: return 4f;
            case 2: return 6f;
            case 3: return 12f;
            case 4: return 8f;
            case 5: return 9f;
            default: return 1f;
        }
    }

    /** Durabilidad de herramienta o armadura */
    public int durability() {
        // Herramientas
        if (isTool()) {
            int tier = toolTier();
            switch (tier) {
                case 0: return 60;
                case 1: return 132;
                case 2: return 251;
                case 3: return 33;
                case 4: return 1562;
                case 5: return 2031;
            }
        }
        // Armadura: durabilidad por pieza × factor material
        if (isArmor()) {
            int slot = armorSlot();
            int base;
            switch (slot) {
                case 0: base = 11; break;
                case 1: base = 16; break;
                case 2: base = 15; break;
                case 3: base = 13; break;
                default: base = 10;
            }
            String n = name();
            int factor;
            if (n.startsWith("LEATHER")) factor = 5;
            else if (n.startsWith("IRON")) factor = 15;
            else if (n.startsWith("GOLDEN")) factor = 7;
            else if (n.startsWith("DIAMOND")) factor = 33;
            else if (n.startsWith("NETHERITE")) factor = 37;
            else factor = 10;
            return base * factor;
        }
        return 0;
    }

    /** Puntos de defensa de armadura */
    public int armorDefense() {
        String n = name();
        boolean helmet = n.endsWith("_HELMET");
        boolean chest = n.endsWith("_CHESTPLATE");
        boolean legs = n.endsWith("_LEGGINGS");
        boolean boots = n.endsWith("_BOOTS");
        if (!helmet && !chest && !legs && !boots) return 0;

        if (n.startsWith("LEATHER")) {
            if (helmet) return 1; if (chest) return 3; if (legs) return 2; return 1;
        }
        if (n.startsWith("IRON")) {
            if (helmet) return 2; if (chest) return 6; if (legs) return 5; return 2;
        }
        if (n.startsWith("GOLDEN")) {
            if (helmet) return 2; if (chest) return 5; if (legs) return 3; return 1;
        }
        if (n.startsWith("DIAMOND")) {
            if (helmet) return 3; if (chest) return 8; if (legs) return 6; return 3;
        }
        if (n.startsWith("NETHERITE")) {
            if (helmet) return 3; if (chest) return 8; if (legs) return 6; return 3;
        }
        return 0;
    }
}
