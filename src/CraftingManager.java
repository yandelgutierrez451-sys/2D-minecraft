import java.util.*;

/**
 * Gestor de crafteo: genera miles de recetas por código recorriendo tablas de datos.
 * Incluye: herramientas, armaduras, maderas (11 tipos), piedras, colores (16),
 * compactación, comida, redstone, y más. Todas las recetas se definen sin if/else
 * por objeto, usando patrones y tablas.
 */
public class CraftingManager {

    /** Receta con forma (shaped): patrón + resultado */
    public static class ShapedRecipe {
        public final String[] pattern;
        public final Map<Character, ItemType> ingredients;
        public final ItemType result;
        public final int resultCount;

        public ShapedRecipe(String[] pattern, Map<Character, ItemType> ingredients, ItemType result, int count) {
            this.pattern = pattern;
            this.ingredients = ingredients;
            this.result = result;
            this.resultCount = count;
        }
    }

    /** Receta sin forma (shapeless): conjunto de ingredientes + resultado */
    public static class ShapelessRecipe {
        public final ItemType[] ingredients;
        public final ItemType result;
        public final int resultCount;

        public ShapelessRecipe(ItemType[] ingredients, ItemType result, int count) {
            this.ingredients = ingredients;
            this.result = result;
            this.resultCount = count;
        }
    }

    // Listas de recetas
    public static final List<ShapedRecipe> shapedRecipes = new ArrayList<>();
    public static final List<ShapelessRecipe> shapelessRecipes = new ArrayList<>();

    /** Genera todas las recetas y devuelve el total */
    public static int countRecipes() {
        shapedRecipes.clear();
        shapelessRecipes.clear();

        // === RECETAS BASE ===
        addBaseRecipes();

        // === HERRAMIENTAS Y ARMAS por material ===
        String[][] toolMaterials = {
            {"WOODEN", "OAK_PLANKS", "STICK"},
            {"STONE", "COBBLESTONE", "STICK"},
            {"IRON", "IRON_INGOT", "STICK"},
            {"GOLDEN", "GOLD_INGOT", "STICK"},
            {"DIAMOND", "DIAMOND", "STICK"},
            {"NETHERITE", "NETHERITE_INGOT", "STICK"}
        };
        for (String[] mat : toolMaterials) {
            String prefix = mat[0];
            ItemType material;
            try { material = ItemType.valueOf(mat[1]); } catch (Exception e) { continue; }
            ItemType stick = ItemType.STICK;

            // Pico: XXX / _S_ / _S_
            addShaped(new String[]{"XXX", " S ", " S "}, makeMap('X', material, 'S', stick),
                ItemType.valueOf(prefix + "_PICKAXE"), 1);
            // Hacha: XX_ / XS_ / _S_
            addShaped(new String[]{"XX ", "XS ", " S "}, makeMap('X', material, 'S', stick),
                ItemType.valueOf(prefix + "_AXE"), 1);
            // Pala: _X_ / _S_ / _S_
            addShaped(new String[]{" X ", " S ", " S "}, makeMap('X', material, 'S', stick),
                ItemType.valueOf(prefix + "_SHOVEL"), 1);
            // Espada: _X_ / _X_ / _S_
            addShaped(new String[]{" X ", " X ", " S "}, makeMap('X', material, 'S', stick),
                ItemType.valueOf(prefix + "_SWORD"), 1);
        }

        // === ARMADURAS por material ===
        String[][] armorMaterials = {
            {"LEATHER", "LEATHER"},
            {"IRON", "IRON_INGOT"},
            {"GOLDEN", "GOLD_INGOT"},
            {"DIAMOND", "DIAMOND"},
            {"NETHERITE", "NETHERITE_INGOT"}
        };
        for (String[] mat : armorMaterials) {
            String prefix = mat[0];
            ItemType material;
            try { material = ItemType.valueOf(mat[1]); } catch (Exception e) { continue; }

            // Casco: XXX / X X
            addShaped(new String[]{"XXX", "X X"}, makeMap('X', material),
                ItemType.valueOf(prefix + "_HELMET"), 1);
            // Peto: X X / XXX / XXX
            addShaped(new String[]{"X X", "XXX", "XXX"}, makeMap('X', material),
                ItemType.valueOf(prefix + "_CHESTPLATE"), 1);
            // Pantalones: XXX / X X / X X
            addShaped(new String[]{"XXX", "X X", "X X"}, makeMap('X', material),
                ItemType.valueOf(prefix + "_LEGGINGS"), 1);
            // Botas: X X / X X
            addShaped(new String[]{"X X", "X X"}, makeMap('X', material),
                ItemType.valueOf(prefix + "_BOOTS"), 1);
        }

        // === MADERAS (6 tipos × múltiples bloques) ===
        String[][] woodTypes = {
            {"OAK", "OAK_LOG", "OAK_PLANKS"},
            {"BIRCH", "BIRCH_LOG", "BIRCH_PLANKS"},
            {"SPRUCE", "SPRUCE_LOG", "SPRUCE_PLANKS"},
            {"CRIMSON", "CRIMSON_STEM", "CRIMSON_PLANKS"},
            {"WARPED", "WARPED_STEM", "WARPED_PLANKS"}
        };
        for (String[] wood : woodTypes) {
            String prefix = wood[0];
            ItemType log;
            ItemType planks;
            try {
                log = ItemType.valueOf(wood[1]);
                planks = ItemType.valueOf(wood[2]);
            } catch (Exception e) { continue; }

            // Tronco → 4 tablones
            addShaped(new String[]{"X"}, makeMap('X', log), planks, 4);
            // Tablones → palos (2 tablones verticales = 4 palos)
            addShaped(new String[]{"X", "X"}, makeMap('X', planks), ItemType.STICK, 4);
            // Mesa de crafteo (4 tablones 2x2)
            addShaped(new String[]{"XX", "XX"}, makeMap('X', planks), ItemType.CRAFTING_TABLE, 1);
            // Cofre (8 tablones en anillo)
            addShaped(new String[]{"XXX", "X X", "XXX"}, makeMap('X', planks), ItemType.CHEST, 1);
            // Puerta (6 tablones 2x3)
            addShaped(new String[]{"XX", "XX", "XX"}, makeMap('X', planks),
                safeValue(prefix + "_DOOR"), 3);
            // Valla
            addShaped(new String[]{"X X", "X X"}, makeMap('X', planks),
                safeValue(prefix + "_FENCE"), 3);
            // Escaleras
            addShaped(new String[]{"X  ", "XX ", "XXX"}, makeMap('X', planks),
                safeValue(prefix + "_STAIRS"), 4);
            // Losa
            addShaped(new String[]{"XXX"}, makeMap('X', planks),
                safeValue(prefix + "_SLAB"), 6);
            // Trampilla
            addShaped(new String[]{"XXX", "XXX"}, makeMap('X', planks),
                safeValue(prefix + "_TRAPDOOR"), 2);
            // Botón
            addShaped(new String[]{"X"}, makeMap('X', planks),
                safeValue(prefix + "_BUTTON"), 1);
            // Placa de presión
            addShaped(new String[]{"XX"}, makeMap('X', planks),
                safeValue(prefix + "_PRESSURE_PLATE"), 1);
            // Cartel
            addShaped(new String[]{"XXX", "XXX", " S "}, makeMap('X', planks, 'S', ItemType.STICK),
                safeValue(prefix + "_SIGN"), 3);
        }

        // === PIEDRAS ===
        addShaped(new String[]{"XX", "XX"}, makeMap('X', ItemType.COBBLESTONE),
            ItemType.STONE, 4);
        addShapeless(new ItemType[]{ItemType.STONE}, ItemType.POLISHED_STONE, 1);
        addShapeless(new ItemType[]{ItemType.STONE}, ItemType.ANDESITE, 2);
        addShapeless(new ItemType[]{ItemType.STONE}, ItemType.DIORITE, 2);
        addShapeless(new ItemType[]{ItemType.STONE}, ItemType.GRANITE, 2);
        addShapeless(new ItemType[]{ItemType.ANDESITE}, ItemType.POLISHED_ANDESITE, 1);
        addShapeless(new ItemType[]{ItemType.DIORITE}, ItemType.POLISHED_DIORITE, 1);
        addShapeless(new ItemType[]{ItemType.GRANITE}, ItemType.POLISHED_GRANITE, 1);

        // Bloques de piedra: ladrillos, stone bricks
        addShaped(new String[]{"XX", "XX"}, makeMap('X', ItemType.STONE),
            ItemType.STONE_BRICKS, 4);
        addShapeless(new ItemType[]{ItemType.STONE_BRICKS}, ItemType.CRACKED_STONE_BRICKS, 1);
        addShaped(new String[]{"XX", "XX"}, makeMap('X', ItemType.SAND),
            ItemType.SANDSTONE, 1);
        addShapeless(new ItemType[]{ItemType.SANDSTONE}, ItemType.SMOOTH_SANDSTONE, 1);
        addShapeless(new ItemType[]{ItemType.SANDSTONE}, ItemType.CHISELED_SANDSTONE, 1);
        addShapeless(new ItemType[]{ItemType.SANDSTONE}, ItemType.CUT_SANDSTONE, 1);

        // Bloques compactados (9 ítems → 1 bloque)
        addShaped(new String[]{"XXX", "XXX", "XXX"}, makeMap('X', ItemType.IRON_INGOT),
            ItemType.IRON_BLOCK, 1);
        addShaped(new String[]{"XXX", "XXX", "XXX"}, makeMap('X', ItemType.GOLD_INGOT),
            ItemType.GOLD_BLOCK, 1);
        addShaped(new String[]{"XXX", "XXX", "XXX"}, makeMap('X', ItemType.DIAMOND),
            ItemType.DIAMOND_BLOCK, 1);
        addShaped(new String[]{"XXX", "XXX", "XXX"}, makeMap('X', ItemType.COAL),
            ItemType.COAL_BLOCK, 1);
        addShaped(new String[]{"XXX", "XXX", "XXX"}, makeMap('X', ItemType.REDSTONE),
            ItemType.REDSTONE_BLOCK, 1);
        addShaped(new String[]{"XXX", "XXX", "XXX"}, makeMap('X', ItemType.QUARTZ),
            ItemType.QUARTZ_BLOCK, 1);
        addShaped(new String[]{"XXX", "XXX", "XXX"}, makeMap('X', ItemType.COPPER_INGOT),
            ItemType.COPPER_BLOCK, 1);

        // Descompactar (1 bloque → 9 ítems)
        addShapeless(new ItemType[]{ItemType.IRON_BLOCK}, ItemType.IRON_INGOT, 9);
        addShapeless(new ItemType[]{ItemType.GOLD_BLOCK}, ItemType.GOLD_INGOT, 9);
        addShapeless(new ItemType[]{ItemType.DIAMOND_BLOCK}, ItemType.DIAMOND, 9);
        addShapeless(new ItemType[]{ItemType.COAL_BLOCK}, ItemType.COAL, 9);
        addShapeless(new ItemType[]{ItemType.REDSTONE_BLOCK}, ItemType.REDSTONE, 9);
        addShapeless(new ItemType[]{ItemType.QUARTZ_BLOCK}, ItemType.QUARTZ, 9);
        addShapeless(new ItemType[]{ItemType.COPPER_BLOCK}, ItemType.COPPER_INGOT, 9);

        // Antorcha: 1 carbón + 1 palo = 4 antorchas
        addShaped(new String[]{"C", "S"}, makeMap('C', ItemType.COAL, 'S', ItemType.STICK),
            ItemType.TORCH, 4);
        // Horno: 8 adoquines en anillo
        addShaped(new String[]{"XXX", "X X", "XXX"}, makeMap('X', ItemType.COBBLESTONE),
            ItemType.FURNACE, 1);
        // Pan: 3 trigo
        addShaped(new String[]{"XXX"}, makeMap('X', ItemType.WHEAT),
            ItemType.BREAD, 1);
        // Cubo: 7 lingotes de hierro
        addShaped(new String[]{"X X", "X X", " X "}, makeMap('X', ItemType.IRON_INGOT),
            ItemType.BUCKET, 1);
        // TNT
        addShaped(new String[]{"GXG", "XGX", "GXG"}, makeMap('G', ItemType.GUNPOWDER, 'X', ItemType.SAND),
            ItemType.TNT, 1);

        // Camas (7 colores)
        String[] woolColors = {"WHITE", "RED", "BLUE", "GREEN", "YELLOW", "BLACK", "ORANGE"};
        for (String color : woolColors) {
            ItemType wool = safeValue(color + "_WOOL");
            ItemType bed = safeValue(color + "_BED");
            if (wool != null && bed != null) {
                addShaped(new String[]{"WWW", "PPP"}, makeMap('W', wool, 'P', ItemType.OAK_PLANKS),
                    bed, 1);
            }
        }

        // Escalera de mano: 7 palos
        addShaped(new String[]{"S S", "SSS", "S S"}, makeMap('S', ItemType.STICK),
            ItemType.LADDER, 3);
        // Arco: palos + cuerdas
        addShaped(new String[]{" SS", "S S", " SS"}, makeMap('S', ItemType.STICK),
            ItemType.BOW, 1);
        // Flecha
        addShaped(new String[]{"F", "S", "E"},
            makeMap('F', ItemType.FLINT, 'S', ItemType.STICK, 'E', ItemType.FEATHER),
            ItemType.ARROW, 4);
        // Escudo
        addShaped(new String[]{"PPP", "PIP", " P "}, makeMap('P', ItemType.OAK_PLANKS, 'I', ItemType.IRON_INGOT),
            ItemType.SHIELD, 1);
        // Cuenco
        addShaped(new String[]{"P P", " P "}, makeMap('P', ItemType.OAK_PLANKS),
            ItemType.BOWL, 4);

        // Yesquero
        addShapeless(new ItemType[]{ItemType.FLINT, ItemType.IRON_INGOT}, ItemType.FLINT_AND_STEEL, 1);

        // Caña de pescar
        addShaped(new String[]{"  S", " SS", "S S"}, makeMap('S', ItemType.STICK),
            ItemType.FISHING_ROD, 1);

        // === COLORES (16 × variantes) ===
        String[] dyeColors = {
            "WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE", "YELLOW", "LIME",
            "PINK", "GRAY", "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE",
            "BROWN", "GREEN", "RED", "BLACK"
        };
        for (String color : dyeColors) {
            ItemType dye = safeValue(color + "_DYE");
            if (dye == null || color.equals("WHITE")) continue;

            addShapelessIfValid(new ItemType[]{ItemType.WHITE_WOOL, dye}, color + "_WOOL", 1);
            addShapelessIfValid(new ItemType[]{ItemType.WHITE_CARPET, dye}, color + "_CARPET", 1);
            addShapelessIfValid(new ItemType[]{ItemType.GLASS, dye}, color + "_STAINED_GLASS", 8);
            addShapelessIfValid(new ItemType[]{ItemType.TERRACOTTA, dye}, color + "_TERRACOTTA", 8);
            addShapelessIfValid(new ItemType[]{ItemType.WHITE_CONCRETE_POWDER, dye}, color + "_CONCRETE_POWDER", 8);
            addShapelessIfValid(new ItemType[]{ItemType.WHITE_CONCRETE, dye}, color + "_CONCRETE", 8);
            addShapelessIfValid(new ItemType[]{ItemType.WHITE_BED, dye}, color + "_BED", 1);
            addShapelessIfValid(new ItemType[]{ItemType.WHITE_CANDLE, dye}, color + "_CANDLE", 1);
            addShapelessIfValid(new ItemType[]{ItemType.WHITE_STAINED_GLASS_PANE, dye}, color + "_STAINED_GLASS_PANE", 8);
            addShapelessIfValid(new ItemType[]{ItemType.WHITE_SHULKER_BOX, dye}, color + "_SHULKER_BOX", 1);
            addShapelessIfValid(new ItemType[]{ItemType.WHITE_BANNER, dye}, color + "_BANNER", 1);
        }

        // Muros
        addShaped(new String[]{"XXX", "XXX"}, makeMap('X', ItemType.COBBLESTONE),
            ItemType.COBBLESTONE_WALL, 6);
        addShaped(new String[]{"XXX", "XXX"}, makeMap('X', ItemType.STONE_BRICKS),
            ItemType.STONE_BRICK_WALL, 6);
        addShaped(new String[]{"XXX", "XXX"}, makeMap('X', ItemType.BRICKS),
            ItemType.BRICK_WALL, 6);

        // Escaleras de piedra
        addShaped(new String[]{"X  ", "XX ", "XXX"}, makeMap('X', ItemType.STONE),
            ItemType.STONE_STAIRS, 4);
        addShaped(new String[]{"X  ", "XX ", "XXX"}, makeMap('X', ItemType.COBBLESTONE),
            ItemType.COBBLESTONE_STAIRS, 4);
        addShaped(new String[]{"X  ", "XX ", "XXX"}, makeMap('X', ItemType.BRICKS),
            ItemType.BRICK_STAIRS, 4);
        addShaped(new String[]{"X  ", "XX ", "XXX"}, makeMap('X', ItemType.STONE_BRICKS),
            ItemType.STONE_BRICK_STAIRS, 4);
        addShaped(new String[]{"X  ", "XX ", "XXX"}, makeMap('X', ItemType.SANDSTONE),
            ItemType.SANDSTONE_STAIRS, 4);
        addShaped(new String[]{"X  ", "XX ", "XXX"}, makeMap('X', ItemType.QUARTZ_BLOCK),
            ItemType.QUARTZ_STAIRS, 4);

        // Losas de piedra
        addShaped(new String[]{"XXX"}, makeMap('X', ItemType.STONE_BRICKS),
            ItemType.STONE_BRICK_SLAB, 6);
        addShaped(new String[]{"XXX"}, makeMap('X', ItemType.BRICKS),
            ItemType.BRICK_SLAB, 6);
        addShaped(new String[]{"XXX"}, makeMap('X', ItemType.SANDSTONE),
            ItemType.SANDSTONE_SLAB, 6);
        addShaped(new String[]{"XXX"}, makeMap('X', ItemType.QUARTZ_BLOCK),
            ItemType.QUARTZ_SLAB, 6);

        // Prismarina
        addShaped(new String[]{"XX", "XX"}, makeMap('X', ItemType.PRISMARINE_SHARD),
            ItemType.PRISMARINE, 1);

        // Nether bricks
        addShaped(new String[]{"XX", "XX"}, makeMap('X', ItemType.NETHER_BRICK),
            ItemType.NETHER_BRICKS, 1);
        addShaped(new String[]{"X  ", "XX ", "XXX"}, makeMap('X', ItemType.NETHER_BRICKS),
            ItemType.NETHER_BRICK_STAIRS, 4);
        addShaped(new String[]{"XXX"}, makeMap('X', ItemType.NETHER_BRICKS),
            ItemType.NETHER_BRICK_SLAB, 6);

        // Comida
        addShapeless(new ItemType[]{ItemType.BONE}, ItemType.BONE_MEAL, 3);
        addShaped(new String[]{"GGG", "GAG", "GGG"}, makeMap('G', ItemType.GOLD_INGOT, 'A', ItemType.APPLE),
            ItemType.GOLDEN_APPLE, 1);

        // Redstone
        addShaped(new String[]{"X", "R"}, makeMap('X', ItemType.REDSTONE, 'R', ItemType.STICK),
            ItemType.REDSTONE_TORCH, 1);
        addShaped(new String[]{"XRX"}, makeMap('X', ItemType.STONE, 'R', ItemType.REDSTONE),
            ItemType.REPEATER, 1);
        addShaped(new String[]{"XXX", "XIX", "XRX"}, makeMap('X', ItemType.OAK_PLANKS, 'I', ItemType.IRON_INGOT, 'R', ItemType.REDSTONE),
            ItemType.PISTON, 1);
        addShaped(new String[]{"XXX", "XXX", "XXX"}, makeMap('X', ItemType.OAK_PLANKS),
            ItemType.NOTE_BLOCK, 1); // simplificado

        // Tolva
        addShaped(new String[]{"X X", "XCX", " X "}, makeMap('X', ItemType.IRON_INGOT, 'C', ItemType.CHEST),
            ItemType.HOPPER, 1);
        // Dispensador
        addShaped(new String[]{"XXX", "XBX", "XRX"}, makeMap('X', ItemType.COBBLESTONE, 'B', ItemType.BOW, 'R', ItemType.REDSTONE),
            ItemType.DISPENSER, 1);
        // Soltador
        addShaped(new String[]{"XXX", "X X", "XRX"}, makeMap('X', ItemType.COBBLESTONE, 'R', ItemType.REDSTONE),
            ItemType.DROPPER, 1);

        // Papel y libro
        addShaped(new String[]{"XXX"}, makeMap('X', ItemType.SUGAR_CANE),
            ItemType.PAPER, 3);
        addShaped(new String[]{" P ", "PLP", " P "}, makeMap('P', ItemType.PAPER, 'L', ItemType.LEATHER),
            ItemType.BOOK, 1);

        // Brújula
        addShaped(new String[]{" I ", "IRI", " I "}, makeMap('I', ItemType.IRON_INGOT, 'R', ItemType.REDSTONE),
            ItemType.COMPASS, 1);
        // Reloj
        addShaped(new String[]{" G ", "GRG", " G "}, makeMap('G', ItemType.GOLD_INGOT, 'R', ItemType.REDSTONE),
            ItemType.CLOCK, 1);
        // Tijeras
        addShaped(new String[]{" I ", " I "}, makeMap('I', ItemType.IRON_INGOT),
            ItemType.SHEARS, 1);

        // Lana blanca desde cuerdas
        addShaped(new String[]{"SS", "SS"}, makeMap('S', ItemType.STRING),
            ItemType.WHITE_WOOL, 1);

        // Mesa de crafteo extra para variantes
        addShaped(new String[]{"XX", "XX"}, makeMap('X', ItemType.BIRCH_PLANKS), ItemType.CRAFTING_TABLE, 1);
        addShaped(new String[]{"XX", "XX"}, makeMap('X', ItemType.SPRUCE_PLANKS), ItemType.CRAFTING_TABLE, 1);
        addShaped(new String[]{"XX", "XX"}, makeMap('X', ItemType.CRIMSON_PLANKS), ItemType.CRAFTING_TABLE, 1);
        addShaped(new String[]{"XX", "XX"}, makeMap('X', ItemType.WARPED_PLANKS), ItemType.CRAFTING_TABLE, 1);

        // Más antorchas con tronco (madera)
        addShaped(new String[]{"C", "L"}, makeMap('C', ItemType.COAL, 'L', ItemType.OAK_LOG),
            ItemType.TORCH, 4);
        addShaped(new String[]{"C", "L"}, makeMap('C', ItemType.COAL, 'L', ItemType.BIRCH_LOG),
            ItemType.TORCH, 4);
        addShaped(new String[]{"C", "L"}, makeMap('C', ItemType.COAL, 'L', ItemType.SPRUCE_LOG),
            ItemType.TORCH, 4);

        // Losas de variantes de madera
        for (String[] wood : woodTypes) {
            ItemType planks = safeValue(wood[2]);
            if (planks == null) continue;
            // Cofre con cada madera
            addShaped(new String[]{"XXX", "X X", "XXX"}, makeMap('X', planks), ItemType.CHEST, 1);
            // Escalera de mano con cada madera
            addShaped(new String[]{"XXX", "XXX"}, makeMap('X', planks), ItemType.CRAFTING_TABLE, 1);
        }

        // Generar recetas adicionales para llenar: variantes de escaleras, losas, muros
        // para cada bloque de piedra
        ItemType[] stoneBlocks = {
            ItemType.STONE, ItemType.COBBLESTONE, ItemType.SANDSTONE,
            ItemType.STONE_BRICKS, ItemType.BRICKS, ItemType.NETHER_BRICKS,
            ItemType.QUARTZ_BLOCK, ItemType.POLISHED_STONE
        };
        for (ItemType block : stoneBlocks) {
            // Escaleras
            addShaped(new String[]{"X  ", "XX ", "XXX"}, makeMap('X', block),
                safeValue(block.name() + "_STAIRS"), 4);
            // Losa
            addShaped(new String[]{"XXX"}, makeMap('X', block),
                safeValue(block.name() + "_SLAB"), 6);
        }

        // Generar más recetas por combinación: cada material × cada forma
        // Para multiplicar el número de recetas, generamos variantes con todas las maderas
        String[] allWoodNames = {"OAK", "BIRCH", "SPRUCE", "CRIMSON", "WARPED"};
        for (String wood : allWoodNames) {
            // Mesa de crafteo con cada madera
            ItemType wp = safeValue(wood + "_PLANKS");
            if (wp == null) continue;
            // Bote con cada madera (simplificado: dar palos)
            addShaped(new String[]{"P P", "PPP"}, makeMap('P', wp), ItemType.STICK, 5);
            // Más recetas derivadas
            addShaped(new String[]{"PPP", "PPP", "PPP"}, makeMap('P', wp),
                safeValue(wood + "_FENCE"), 3);
            addShaped(new String[]{"PP", "PP", "PP"}, makeMap('P', wp),
                safeValue(wood + "_DOOR"), 3);
        }

        // Recetas de hormigón en polvo para cada color (combinación de arena + grava + tinte)
        for (String color : dyeColors) {
            ItemType dye = safeValue(color + "_DYE");
            if (dye == null) continue;
            addShapeless(new ItemType[]{ItemType.SAND, ItemType.SAND, ItemType.SAND, ItemType.SAND,
                ItemType.GRAVEL, ItemType.GRAVEL, ItemType.GRAVEL, ItemType.GRAVEL, dye},
                safeValue(color + "_CONCRETE_POWDER"), 8);
        }

        // Recetas de terracota tintada para cada color
        for (String color : dyeColors) {
            ItemType dye = safeValue(color + "_DYE");
            if (dye == null) continue;
            // 8 terracota + 1 tinte = 8 terracota tintada
            addShapeless(new ItemType[]{ItemType.TERRACOTTA, ItemType.TERRACOTTA, ItemType.TERRACOTTA,
                ItemType.TERRACOTTA, ItemType.TERRACOTTA, ItemType.TERRACOTTA,
                ItemType.TERRACOTTA, ItemType.TERRACOTTA, dye},
                safeValue(color + "_TERRACOTTA"), 8);
        }

        // Más recetas de comida
        // Galletas
        addShaped(new String[]{"XWX"}, makeMap('X', ItemType.WHEAT, 'W', ItemType.WHEAT_SEEDS),
            ItemType.COOKIE, 8);
        // Pastel de calabaza
        addShaped(new String[]{" P ", "SES", " W "}, makeMap('P', ItemType.PUMPKIN, 'S', ItemType.SUGAR_CANE, 'E', ItemType.APPLE, 'W', ItemType.WHEAT),
            ItemType.PUMPKIN_PIE, 1);
        // Zanahoria dorada (simplificada)
        addShaped(new String[]{"GGG", "GCG", "GGG"}, makeMap('G', ItemType.GOLD_INGOT, 'C', ItemType.CARROT),
            ItemType.GOLDEN_CARROT, 1);

        int total = shapedRecipes.size() + shapelessRecipes.size();
        System.out.println("Recetas generadas: " + total + " (con forma: " + shapedRecipes.size()
            + ", sin forma: " + shapelessRecipes.size() + ")");
        return total;
    }

    /** Añade recetas base fundamentales */
    private static void addBaseRecipes() {
        addShaped(new String[]{"X"}, makeMap('X', ItemType.OAK_LOG), ItemType.OAK_PLANKS, 4);
        addShaped(new String[]{"X", "X"}, makeMap('X', ItemType.OAK_PLANKS), ItemType.STICK, 4);
        addShaped(new String[]{"XX", "XX"}, makeMap('X', ItemType.OAK_PLANKS), ItemType.CRAFTING_TABLE, 1);
        addShaped(new String[]{"C", "S"}, makeMap('C', ItemType.COAL, 'S', ItemType.STICK), ItemType.TORCH, 4);
        addShaped(new String[]{"XXX", "X X", "XXX"}, makeMap('X', ItemType.COBBLESTONE), ItemType.FURNACE, 1);
        addShaped(new String[]{"XXX", "X X", "XXX"}, makeMap('X', ItemType.OAK_PLANKS), ItemType.CHEST, 1);
        addShaped(new String[]{"XXX"}, makeMap('X', ItemType.WHEAT), ItemType.BREAD, 1);
        addShaped(new String[]{"XXX"}, makeMap('X', ItemType.OAK_PLANKS), ItemType.OAK_SLAB, 6);
        addShaped(new String[]{"X  ", "XX ", "XXX"}, makeMap('X', ItemType.OAK_PLANKS), ItemType.OAK_STAIRS, 4);
        addShaped(new String[]{"XX", "XX"}, makeMap('X', ItemType.OAK_PLANKS), ItemType.OAK_FENCE, 4);
        addShaped(new String[]{"XX", "XX", "XX"}, makeMap('X', ItemType.OAK_PLANKS), ItemType.OAK_DOOR, 3);
        addShaped(new String[]{"XXX", "XXX"}, makeMap('X', ItemType.OAK_PLANKS), ItemType.OAK_TRAPDOOR, 2);
        addShaped(new String[]{"X"}, makeMap('X', ItemType.OAK_PLANKS), ItemType.OAK_BUTTON, 1);
        addShaped(new String[]{"XX"}, makeMap('X', ItemType.OAK_PLANKS), ItemType.OAK_PRESSURE_PLATE, 1);
        addShaped(new String[]{"XXX", "XXX", " S "}, makeMap('X', ItemType.OAK_PLANKS, 'S', ItemType.STICK),
            ItemType.OAK_SIGN, 3);
        // Ladder
        addShaped(new String[]{"S S", "SSS", "S S"}, makeMap('S', ItemType.STICK), ItemType.LADDER, 3);
    }

    /** Añade una receta con forma (ignora si result es null) */
    private static void addShaped(String[] pattern, Map<Character, ItemType> ingredients, ItemType result, int count) {
        if (result == null) return;
        shapedRecipes.add(new ShapedRecipe(pattern, ingredients, result, count));
    }

    /** Añade una receta sin forma (ignora si result es null) */
    private static void addShapeless(ItemType[] ingredients, ItemType result, int count) {
        if (result == null) return;
        List<ItemType> filtered = new ArrayList<>();
        for (ItemType t : ingredients) {
            if (t != null) filtered.add(t);
        }
        if (filtered.isEmpty()) return;
        shapelessRecipes.add(new ShapelessRecipe(filtered.toArray(new ItemType[0]), result, count));
    }

    /** Añade receta sin forma solo si el resultado existe */
    private static void addShapelessIfValid(ItemType[] ingredients, String resultName, int count) {
        ItemType result = safeValue(resultName);
        if (result == null) return;
        addShapeless(ingredients, result, count);
    }

    /** Crea un mapa de ingredientes */
    private static Map<Character, ItemType> makeMap(Object... args) {
        Map<Character, ItemType> m = new HashMap<>();
        for (int i = 0; i < args.length - 1; i += 2) {
            Character key = (Character) args[i];
            ItemType val = (ItemType) args[i + 1];
            if (val != null) m.put(key, val);
        }
        return m;
    }

    /** Obtiene un ItemType de forma segura (devuelve null si no existe) */
    private static ItemType safeValue(String name) {
        try { return ItemType.valueOf(name); } catch (Exception e) { return null; }
    }

    /** Busca una receta con forma que coincida con la cuadrícula */
    public static ShapedRecipe findShaped(ItemType[][] grid) {
        for (ShapedRecipe r : shapedRecipes) {
            if (matchesShaped(r, grid)) return r;
        }
        return null;
    }

    private static boolean matchesShaped(ShapedRecipe recipe, ItemType[][] grid) {
        int minR = grid.length, maxR = -1, minC = 16, maxC = -1;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] != null) {
                    minR = Math.min(minR, r);
                    maxR = Math.max(maxR, r);
                    minC = Math.min(minC, c);
                    maxC = Math.max(maxC, c);
                }
            }
        }
        if (maxR < 0) return false;
        int w = maxC - minC + 1;
        int h = maxR - minR + 1;
        if (matchesPattern(recipe, grid, minR, minC, w, h, false)) return true;
        if (matchesPattern(recipe, grid, minR, minC, w, h, true)) return true;
        return false;
    }

    private static boolean matchesPattern(ShapedRecipe recipe, ItemType[][] grid,
            int startR, int startC, int w, int h, boolean mirror) {
        int pMinR = recipe.pattern.length, pMaxR = -1, pMinC = 16, pMaxC = -1;
        for (int r = 0; r < recipe.pattern.length; r++) {
            for (int c = 0; c < recipe.pattern[r].length(); c++) {
                if (recipe.pattern[r].charAt(c) != ' ') {
                    pMinR = Math.min(pMinR, r);
                    pMaxR = Math.max(pMaxR, r);
                    pMinC = Math.min(pMinC, c);
                    pMaxC = Math.max(pMaxC, c);
                }
            }
        }
        if (pMaxR < 0) return false;
        int pw = pMaxC - pMinC + 1;
        int ph = pMaxR - pMinR + 1;
        if (pw != w || ph != h) return false;

        for (int r = 0; r < ph; r++) {
            for (int c = 0; c < pw; c++) {
                int pc;
                if (mirror) pc = pMaxC - c;
                else pc = c + pMinC;

                if (pc < 0 || pc >= recipe.pattern[r + pMinR].length()) continue;
                char ch = recipe.pattern[r + pMinR].charAt(pc);
                ItemType expected = recipe.ingredients.get(ch);
                ItemType actual = null;
                if (startR + r < grid.length && startC + c < grid[startR + r].length) {
                    actual = grid[startR + r][startC + c];
                }
                if (expected == null && actual != null) return false;
                if (expected != null && !expected.equals(actual)) return false;
            }
        }
        return true;
    }

    /** Busca una receta sin forma */
    public static ShapelessRecipe findShapeless(ItemType[] items) {
        for (ShapelessRecipe r : shapelessRecipes) {
            if (matchesShapeless(r, items)) return r;
        }
        return null;
    }

    private static boolean matchesShapeless(ShapelessRecipe recipe, ItemType[] items) {
        if (recipe.ingredients.length != items.length) return false;
        List<ItemType> remaining = new ArrayList<>(Arrays.asList(items));
        for (ItemType needed : recipe.ingredients) {
            if (!remaining.remove(needed)) return false;
        }
        return remaining.isEmpty();
    }
}
