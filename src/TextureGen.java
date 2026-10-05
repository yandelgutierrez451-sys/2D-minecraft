import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Genera todas las texturas de 16x16 por código (pixel art).
 * No usa imágenes externas. Cada textura es determinista (sin suavizado).
 */
public class TextureGen {

    private static final int S = 16;

    /** Genera la textura de un ItemType y la devuelve como BufferedImage */
    public static BufferedImage gen(ItemType type) {
        BufferedImage img = new BufferedImage(S, S, BufferedImage.TYPE_INT_ARGB);
        // Semilla determinista por tipo para consistencia
        Random rng = new Random(type.ordinal() * 7919L + 42L);
        switch (type) {
            case STONE: drawStone(img); break;
            case GRASS: drawGrass(img); break;
            case DIRT: drawDirt(img); break;
            case COBBLESTONE: drawCobblestone(img); break;
            case COAL_ORE: drawCoalOre(img); break;
            case IRON_ORE: drawIronOre(img); break;
            case GOLD_ORE: drawGoldOre(img); break;
            case DIAMOND_ORE: drawDiamondOre(img); break;
            case OAK_LOG: drawOakLog(img); break;
            case OAK_LEAVES: drawOakLeaves(img); break;
            case OAK_PLANKS: drawOakPlanks(img); break;
            case SAND: drawSand(img); break;
            case GRAVEL: drawGravel(img); break;
            case CRAFTING_TABLE: drawCraftingTable(img); break;
            case FURNACE: drawFurnace(img); break;
            case TORCH: drawTorch(img); break;
            case GLASS: drawGlass(img); break;
            case SANDSTONE: drawSandstone(img); break;
            case IRON_BLOCK: drawIronBlock(img); break;
            case GOLD_BLOCK: drawGoldBlock(img); break;
            case DIAMOND_BLOCK: drawDiamondBlock(img); break;
            case COAL_BLOCK: drawCoalBlock(img); break;
            case OBSIDIAN: drawObsidian(img); break;
            case COAL: drawCoal(img); break;
            case RAW_IRON: drawRawIron(img); break;
            case IRON_INGOT: drawIronIngot(img); break;
            case RAW_GOLD: drawRawGold(img); break;
            case GOLD_INGOT: drawGoldIngot(img); break;
            case DIAMOND: drawDiamond(img); break;
            case STICK: drawStick(img); break;
            case WOODEN_PICKAXE: drawWoodenPickaxe(img); break;
            case WOODEN_AXE: drawWoodenAxe(img); break;
            case WOODEN_SHOVEL: drawWoodenShovel(img); break;
            case WOODEN_SWORD: drawWoodenSword(img); break;
            case STONE_PICKAXE: drawStonePickaxe(img); break;
            case STONE_AXE: drawStoneAxe(img); break;
            case STONE_SHOVEL: drawStoneShovel(img); break;
            case STONE_SWORD: drawStoneSword(img); break;
            case IRON_PICKAXE: drawIronPickaxe(img); break;
            case IRON_AXE: drawIronAxe(img); break;
            case IRON_SHOVEL: drawIronShovel(img); break;
            case IRON_SWORD: drawIronSword(img); break;
            case GOLDEN_PICKAXE: drawGoldenPickaxe(img); break;
            case GOLDEN_AXE: drawGoldenAxe(img); break;
            case GOLDEN_SHOVEL: drawGoldenShovel(img); break;
            case GOLDEN_SWORD: drawGoldenSword(img); break;
            case DIAMOND_PICKAXE: drawDiamondPickaxe(img); break;
            case DIAMOND_AXE: drawDiamondAxe(img); break;
            case DIAMOND_SHOVEL: drawDiamondShovel(img); break;
            case DIAMOND_SWORD: drawDiamondSword(img); break;
            case REDSTONE_WIRE: drawRedstoneWire(img); break;
            case REDSTONE_TORCH: drawRedstoneTorch(img); break;
            case REDSTONE_BLOCK: drawRedstoneBlock(img); break;
            case LEVER: drawLever(img); break;
            case STONE_BUTTON: drawStoneButton(img); break;
            case REPEATER: drawRepeater(img); break;
            case PISTON: drawPiston(img); break;
            case REDSTONE_LAMP: drawRedstoneLamp(img); break;
            case NOTE_BLOCK: drawNoteBlock(img); break;
            case BREAD: drawBread(img); break;
            case WHEAT_SEEDS: drawWheatSeeds(img); break;
            case WHEAT: drawWheat(img); break;
            case BUCKET: drawBucket(img); break;
            case WATER: drawWaterBucket(img); break;
            case LAVA: drawLavaBucket(img); break;
            case BONE: drawBone(img); break;
            case BONE_MEAL: drawBoneMeal(img); break;
            case APPLE: drawApple(img); break;
            case STRING: drawStringItem(img); break;
            case ARROW: drawArrow(img); break;
            case BOW: drawBow(img); break;
            case LEATHER: drawLeatherItem(img); break;
            case FEATHER: drawFeather(img); break;
            case SLIMEBALL: drawSlimeballItem(img); break;
            case FLINT: drawFlintItem(img); break;
            case PAPER: drawPaperItem(img); break;
            case BOOK: drawBookItem(img); break;
            case GUNPOWDER: drawGunpowderItem(img); break;
            case REDSTONE: drawRedstoneItem(img); break;
            case EMERALD: drawEmeraldItem(img); break;
            case LAPIS: drawLapisItem(img); break;
            case QUARTZ: drawQuartzItem(img); break;
            case ENDER_PEARL: drawEnderPearl(img); break;
            case GOLDEN_APPLE: drawGoldenApple(img); break;
            case GOLDEN_CARROT: drawGoldenCarrot(img); break;
            default: drawDefaultItem(img, type); break;
        }
        return img;
    }

    // ===== Utilidades de dibujo =====

    private static void fill(BufferedImage img, int color) {
        for (int y = 0; y < S; y++)
            for (int x = 0; x < S; x++)
                img.setRGB(x, y, color);
    }

    private static void setPix(BufferedImage img, int x, int y, int color) {
        if (x >= 0 && x < S && y >= 0 && y < S)
            img.setRGB(x, y, color);
    }

    private static int rgb(int r, int g, int b) {
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static int rgba(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static void noise(BufferedImage img, int baseColor, int variance, Random rng) {
        int br = (baseColor >> 16) & 0xFF, bg = (baseColor >> 8) & 0xFF, bb = baseColor & 0xFF;
        for (int y = 0; y < S; y++) {
            for (int x = 0; x < S; x++) {
                int v = (rng.nextInt(variance * 2 + 1)) - variance;
                int r = Math.max(0, Math.min(255, br + v));
                int g = Math.max(0, Math.min(255, bg + v));
                int b = Math.max(0, Math.min(255, bb + v));
                img.setRGB(x, y, rgb(r, g, b));
            }
        }
    }

    private static void rect(BufferedImage img, int x, int y, int w, int h, int color) {
        for (int dy = 0; dy < h; dy++)
            for (int dx = 0; dx < w; dx++)
                setPix(img, x + dx, y + dy, color);
    }

    // ===== Texturas de bloques =====

    private static void drawStone(BufferedImage img) {
        noise(img, rgb(128, 128, 128), 15, new Random(1));
        // Algunas manchas más oscuras
        for (int i = 0; i < 8; i++) {
            int x = (i * 5 + 3) % 14, y = (i * 7 + 2) % 14;
            img.setRGB(x, y, rgb(100, 100, 100));
            img.setRGB(x + 1, y, rgb(105, 105, 105));
        }
    }

    private static void drawGrass(BufferedImage img) {
        // Capa de tierra abajo
        for (int y = 3; y < S; y++)
            for (int x = 0; x < S; x++) {
                int v = ((x * 7 + y * 13) % 11) - 5;
                img.setRGB(x, y, rgb(134 + v, 96 + v, 67 + v));
            }
        // Capa de hierba arriba (3 píxeles)
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < S; x++) {
                int v = ((x * 3 + y * 5) % 7) - 3;
                img.setRGB(x, y, rgb(86 + v, 168 + v, 50 + v));
            }
        // Bordes irregulares entre hierba y tierra
        for (int x = 0; x < S; x++) {
            int h = 3 + ((x * 7 + 3) % 3);
            for (int y = 3; y < h; y++) {
                if ((x + y) % 3 == 0) img.setRGB(x, y, rgb(76, 153, 0));
            }
        }
    }

    private static void drawDirt(BufferedImage img) {
        noise(img, rgb(134, 96, 67), 10, new Random(2));
        for (int i = 0; i < 5; i++) {
            int x = (i * 4 + 2) % 14, y = (i * 5 + 1) % 14;
            img.setRGB(x, y, rgb(110, 78, 54));
        }
    }

    private static void drawCobblestone(BufferedImage img) {
        fill(img, rgb(128, 128, 128));
        // Patrón de piedras irregulares
        int[][] colors = {
            {rgb(140, 140, 140), rgb(110, 110, 110), rgb(120, 120, 120), rgb(135, 135, 135)},
            {rgb(105, 105, 105), rgb(130, 130, 130), rgb(115, 115, 115), rgb(145, 145, 145)}
        };
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                rect(img, x * 4, y * 4, 4, 4, colors[y % 2][x % 2]);
                // Bordes oscuros
                setPix(img, x * 4, y * 4, rgb(90, 90, 90));
            }
        }
    }

    private static void drawOreBase(BufferedImage img, int oreColor) {
        drawStone(img);
        // Puntos de mineral
        int[] positions = {2, 3, 5, 6, 8, 10, 11, 13, 3, 7, 12, 9, 14, 4, 6, 11};
        for (int i = 0; i < positions.length - 1; i += 2) {
            setPix(img, positions[i], positions[i + 1], oreColor);
            if (positions[i] + 1 < S) setPix(img, positions[i] + 1, positions[i + 1], oreColor);
        }
    }

    private static void drawCoalOre(BufferedImage img) {
        drawOreBase(img, rgb(30, 30, 30));
    }

    private static void drawIronOre(BufferedImage img) {
        drawOreBase(img, rgb(216, 175, 147));
    }

    private static void drawGoldOre(BufferedImage img) {
        drawOreBase(img, rgb(255, 215, 0));
    }

    private static void drawDiamondOre(BufferedImage img) {
        drawOreBase(img, rgb(80, 220, 240));
    }

    private static void drawOakLog(BufferedImage img) {
        // Corteza en los laterales, interior más claro
        for (int y = 0; y < S; y++) {
            for (int x = 0; x < S; x++) {
                int v = ((x * 3 + y * 7) % 9) - 4;
                if (x < 3 || x > 12) {
                    img.setRGB(x, y, rgb(85 + v, 65 + v, 35 + v));
                } else {
                    img.setRGB(x, y, rgb(160 + v, 130 + v, 80 + v));
                }
            }
        }
        // Líneas verticales de corteza
        for (int y = 0; y < S; y++) {
            setPix(img, 2, y, rgb(70, 50, 25));
            setPix(img, 13, y, rgb(70, 50, 25));
        }
    }

    private static void drawOakLeaves(BufferedImage img) {
        for (int y = 0; y < S; y++) {
            for (int x = 0; x < S; x++) {
                int v = ((x * 5 + y * 3 + x * y) % 11) - 5;
                int a = ((x + y) % 4 == 0) ? 200 : 255;
                img.setRGB(x, y, rgba(40 + v, 120 + v, 20 + v, a));
            }
        }
    }

    private static void drawOakPlanks(BufferedImage img) {
        for (int y = 0; y < S; y++) {
            for (int x = 0; x < S; x++) {
                int v = ((x * 3 + y * 2) % 7) - 3;
                int base = (y / 4) % 2 == 0 ? rgb(180 + v, 144 + v, 90 + v) : rgb(170 + v, 134 + v, 82 + v);
                img.setRGB(x, y, base);
            }
        }
        // Líneas horizontales de tablas
        for (int x = 0; x < S; x++) {
            setPix(img, x, 3, rgb(140, 110, 65));
            setPix(img, x, 7, rgb(140, 110, 65));
            setPix(img, x, 11, rgb(140, 110, 65));
            setPix(img, x, 15, rgb(140, 110, 65));
        }
    }

    private static void drawSand(BufferedImage img) {
        noise(img, rgb(219, 207, 163), 8, new Random(3));
    }

    private static void drawGravel(BufferedImage img) {
        noise(img, rgb(136, 126, 126), 20, new Random(4));
        for (int i = 0; i < 10; i++) {
            int x = (i * 3 + 1) % 15, y = (i * 4 + 2) % 15;
            img.setRGB(x, y, rgb(100 + (i * 7 % 30), 95 + (i * 5 % 20), 95 + (i * 3 % 20)));
        }
    }

    private static void drawCraftingTable(BufferedImage img) {
        drawOakPlanks(img);
        // Rejilla en la parte superior
        rect(img, 2, 1, 12, 1, rgb(100, 70, 40));
        rect(img, 2, 2, 1, 5, rgb(100, 70, 40));
        rect(img, 7, 2, 1, 5, rgb(100, 70, 40));
        rect(img, 12, 2, 1, 5, rgb(100, 70, 40));
        rect(img, 2, 4, 11, 1, rgb(100, 70, 40));
        rect(img, 2, 6, 11, 1, rgb(100, 70, 40));
    }

    private static void drawFurnace(BufferedImage img) {
        drawCobblestone(img);
        // Boca del horno
        rect(img, 4, 5, 8, 8, rgb(40, 40, 40));
        rect(img, 5, 6, 6, 6, rgb(60, 30, 10));
    }

    private static void drawTorch(BufferedImage img) {
        // Fondo transparente
        fill(img, rgba(0, 0, 0, 0));
        // Palo
        rect(img, 7, 6, 2, 10, rgb(140, 100, 50));
        // Fuego
        rect(img, 6, 2, 4, 4, rgb(255, 200, 50));
        rect(img, 7, 1, 2, 2, rgb(255, 255, 100));
        setPix(img, 7, 3, rgb(255, 150, 0));
        setPix(img, 8, 3, rgb(255, 150, 0));
    }

    private static void drawGlass(BufferedImage img) {
        fill(img, rgba(200, 220, 255, 80));
        // Bordes
        for (int x = 0; x < S; x++) {
            setPix(img, x, 0, rgb(180, 200, 240));
            setPix(img, x, 15, rgb(180, 200, 240));
        }
        for (int y = 0; y < S; y++) {
            setPix(img, 0, y, rgb(180, 200, 240));
            setPix(img, 15, y, rgb(180, 200, 240));
        }
    }

    private static void drawSandstone(BufferedImage img) {
        noise(img, rgb(216, 203, 160), 6, new Random(5));
        rect(img, 0, 0, 16, 1, rgb(195, 185, 145));
        rect(img, 0, 15, 16, 1, rgb(195, 185, 145));
    }

    private static void drawIronBlock(BufferedImage img) {
        noise(img, rgb(220, 220, 220), 8, new Random(6));
        rect(img, 0, 0, 16, 1, rgb(200, 200, 200));
        rect(img, 0, 15, 16, 1, rgb(180, 180, 180));
        rect(img, 0, 0, 1, 16, rgb(200, 200, 200));
        rect(img, 15, 0, 1, 16, rgb(180, 180, 180));
    }

    private static void drawGoldBlock(BufferedImage img) {
        noise(img, rgb(255, 215, 0), 10, new Random(7));
        rect(img, 0, 0, 16, 1, rgb(230, 195, 0));
        rect(img, 0, 15, 16, 1, rgb(200, 170, 0));
    }

    private static void drawDiamondBlock(BufferedImage img) {
        noise(img, rgb(80, 220, 240), 8, new Random(8));
        rect(img, 0, 0, 16, 1, rgb(60, 200, 220));
        rect(img, 0, 15, 16, 1, rgb(50, 180, 200));
    }

    private static void drawCoalBlock(BufferedImage img) {
        noise(img, rgb(30, 30, 30), 5, new Random(9));
    }

    private static void drawObsidian(BufferedImage img) {
        noise(img, rgb(20, 15, 40), 8, new Random(10));
        for (int i = 0; i < 6; i++) {
            int x = (i * 3 + 1) % 14, y = (i * 5 + 2) % 14;
            setPix(img, x, y, rgb(40, 20, 80));
        }
    }

    private static void drawRedstoneBlock(BufferedImage img) {
        noise(img, rgb(180, 30, 30), 10, new Random(11));
        rect(img, 0, 0, 16, 1, rgb(160, 20, 20));
        rect(img, 0, 15, 16, 1, rgb(140, 15, 15));
    }

    private static void drawRedstoneWire(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        // Cruz de redstone
        rect(img, 6, 6, 4, 4, rgb(200, 0, 0));
        rect(img, 0, 7, 6, 2, rgb(180, 0, 0));
        rect(img, 10, 7, 6, 2, rgb(180, 0, 0));
        rect(img, 7, 0, 2, 6, rgb(180, 0, 0));
        rect(img, 7, 10, 2, 6, rgb(180, 0, 0));
    }

    private static void drawRedstoneTorch(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 7, 6, 2, 10, rgb(140, 100, 50));
        rect(img, 6, 2, 4, 4, rgb(200, 0, 0));
        rect(img, 7, 1, 2, 2, rgb(255, 50, 50));
    }

    private static void drawLever(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 5, 10, 6, 4, rgb(120, 120, 120));
        rect(img, 7, 3, 2, 8, rgb(140, 100, 50));
        rect(img, 6, 2, 4, 2, rgb(180, 50, 50));
    }

    private static void drawStoneButton(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 4, 5, 8, 6, rgb(140, 140, 140));
        rect(img, 5, 6, 6, 4, rgb(160, 160, 160));
    }

    private static void drawRepeater(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 2, 2, 12, 12, rgb(130, 130, 130));
        rect(img, 4, 4, 8, 8, rgb(110, 110, 110));
        // Antorchas
        rect(img, 5, 5, 2, 2, rgb(200, 0, 0));
        rect(img, 9, 5, 2, 2, rgb(255, 50, 50));
    }

    private static void drawPiston(BufferedImage img) {
        rect(img, 0, 0, 16, 16, rgb(140, 140, 140));
        rect(img, 2, 2, 12, 12, rgb(170, 140, 60));
        rect(img, 5, 5, 6, 6, rgb(120, 120, 120));
    }

    private static void drawRedstoneLamp(BufferedImage img) {
        noise(img, rgb(150, 120, 60), 10, new Random(12));
        rect(img, 3, 3, 10, 10, rgb(180, 150, 80));
        rect(img, 5, 5, 6, 6, rgb(200, 180, 100));
    }

    private static void drawNoteBlock(BufferedImage img) {
        rect(img, 0, 0, 16, 16, rgb(100, 70, 40));
        rect(img, 2, 2, 12, 12, rgb(140, 100, 50));
        rect(img, 5, 5, 6, 6, rgb(60, 60, 60));
    }

    // ===== Texturas de ítems =====

    private static void drawCoal(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        for (int y = 4; y < 12; y++)
            for (int x = 4; x < 12; x++) {
                int v = ((x + y) % 3) * 10;
                img.setRGB(x, y, rgb(20 + v, 20 + v, 20 + v));
            }
    }

    private static void drawRawIron(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 3, 4, 10, 8, rgb(200, 170, 140));
        rect(img, 4, 5, 8, 6, rgb(216, 175, 147));
    }

    private static void drawIronIngot(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 3, 5, 10, 6, rgb(220, 220, 220));
        rect(img, 4, 4, 8, 2, rgb(200, 200, 200));
        rect(img, 4, 11, 8, 1, rgb(180, 180, 180));
    }

    private static void drawRawGold(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 3, 4, 10, 8, rgb(240, 200, 0));
        rect(img, 4, 5, 8, 6, rgb(255, 215, 0));
    }

    private static void drawGoldIngot(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 3, 5, 10, 6, rgb(255, 215, 0));
        rect(img, 4, 4, 8, 2, rgb(230, 195, 0));
        rect(img, 4, 11, 8, 1, rgb(200, 170, 0));
    }

    private static void drawDiamond(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        // Forma de diamante
        rect(img, 7, 2, 2, 2, rgb(80, 220, 240));
        rect(img, 5, 4, 6, 4, rgb(100, 240, 255));
        rect(img, 7, 8, 2, 3, rgb(80, 220, 240));
        setPix(img, 6, 5, rgb(200, 255, 255));
    }

    private static void drawStick(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        for (int i = 0; i < 10; i++) {
            setPix(img, 6 + i / 3, 13 - i, rgb(160, 120, 60));
            setPix(img, 7 + i / 3, 13 - i, rgb(140, 100, 40));
        }
    }

    private static void drawTool(BufferedImage img, int headColor, int handleColor, String type) {
        fill(img, rgba(0, 0, 0, 0));
        // Mango
        for (int i = 0; i < 7; i++) {
            setPix(img, 10 - i / 2, 14 - i, handleColor);
            setPix(img, 11 - i / 2, 14 - i, rgb(
                Math.max(0, ((handleColor >> 16) & 0xFF) - 20),
                Math.max(0, ((handleColor >> 8) & 0xFF) - 20),
                Math.max(0, (handleColor & 0xFF) - 20)));
        }
        // Cabeza según tipo
        switch (type) {
            case "pickaxe":
                rect(img, 2, 2, 8, 2, headColor);
                rect(img, 2, 4, 2, 2, headColor);
                rect(img, 8, 4, 2, 2, headColor);
                break;
            case "axe":
                rect(img, 2, 2, 4, 2, headColor);
                rect(img, 2, 4, 3, 2, headColor);
                rect(img, 2, 6, 2, 2, headColor);
                break;
            case "shovel":
                rect(img, 3, 1, 4, 5, headColor);
                rect(img, 4, 6, 2, 2, headColor);
                break;
            case "sword":
                rect(img, 4, 1, 2, 8, headColor);
                rect(img, 3, 1, 1, 2, headColor);
                rect(img, 6, 1, 1, 2, headColor);
                setPix(img, 4, 0, headColor);
                setPix(img, 5, 0, headColor);
                break;
        }
    }

    private static void drawWoodenPickaxe(BufferedImage img) { drawTool(img, rgb(160, 120, 60), rgb(120, 80, 30), "pickaxe"); }
    private static void drawWoodenAxe(BufferedImage img) { drawTool(img, rgb(160, 120, 60), rgb(120, 80, 30), "axe"); }
    private static void drawWoodenShovel(BufferedImage img) { drawTool(img, rgb(160, 120, 60), rgb(120, 80, 30), "shovel"); }
    private static void drawWoodenSword(BufferedImage img) { drawTool(img, rgb(160, 120, 60), rgb(120, 80, 30), "sword"); }
    private static void drawStonePickaxe(BufferedImage img) { drawTool(img, rgb(140, 140, 140), rgb(120, 80, 30), "pickaxe"); }
    private static void drawStoneAxe(BufferedImage img) { drawTool(img, rgb(140, 140, 140), rgb(120, 80, 30), "axe"); }
    private static void drawStoneShovel(BufferedImage img) { drawTool(img, rgb(140, 140, 140), rgb(120, 80, 30), "shovel"); }
    private static void drawStoneSword(BufferedImage img) { drawTool(img, rgb(140, 140, 140), rgb(120, 80, 30), "sword"); }
    private static void drawIronPickaxe(BufferedImage img) { drawTool(img, rgb(220, 220, 220), rgb(120, 80, 30), "pickaxe"); }
    private static void drawIronAxe(BufferedImage img) { drawTool(img, rgb(220, 220, 220), rgb(120, 80, 30), "axe"); }
    private static void drawIronShovel(BufferedImage img) { drawTool(img, rgb(220, 220, 220), rgb(120, 80, 30), "shovel"); }
    private static void drawIronSword(BufferedImage img) { drawTool(img, rgb(220, 220, 220), rgb(120, 80, 30), "sword"); }
    private static void drawGoldenPickaxe(BufferedImage img) { drawTool(img, rgb(255, 215, 0), rgb(120, 80, 30), "pickaxe"); }
    private static void drawGoldenAxe(BufferedImage img) { drawTool(img, rgb(255, 215, 0), rgb(120, 80, 30), "axe"); }
    private static void drawGoldenShovel(BufferedImage img) { drawTool(img, rgb(255, 215, 0), rgb(120, 80, 30), "shovel"); }
    private static void drawGoldenSword(BufferedImage img) { drawTool(img, rgb(255, 215, 0), rgb(120, 80, 30), "sword"); }
    private static void drawDiamondPickaxe(BufferedImage img) { drawTool(img, rgb(80, 220, 240), rgb(120, 80, 30), "pickaxe"); }
    private static void drawDiamondAxe(BufferedImage img) { drawTool(img, rgb(80, 220, 240), rgb(120, 80, 30), "axe"); }
    private static void drawDiamondShovel(BufferedImage img) { drawTool(img, rgb(80, 220, 240), rgb(120, 80, 30), "shovel"); }
    private static void drawDiamondSword(BufferedImage img) { drawTool(img, rgb(80, 220, 240), rgb(120, 80, 30), "sword"); }

    private static void drawBread(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 3, 6, 10, 5, rgb(200, 160, 60));
        rect(img, 4, 5, 8, 2, rgb(180, 140, 40));
    }

    private static void drawWheatSeeds(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        setPix(img, 7, 8, rgb(120, 160, 50));
        setPix(img, 8, 7, rgb(120, 160, 50));
        setPix(img, 6, 9, rgb(100, 140, 40));
    }

    private static void drawWheat(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        for (int y = 3; y < 14; y++) {
            setPix(img, 7, y, rgb(200, 180, 50));
            setPix(img, 8, y, rgb(190, 170, 40));
        }
        for (int y = 4; y < 10; y++) {
            setPix(img, 6, y, rgb(220, 200, 60));
            setPix(img, 9, y, rgb(220, 200, 60));
        }
    }

    private static void drawBucket(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 3, 4, 10, 10, rgb(180, 180, 180));
        rect(img, 4, 3, 8, 2, rgb(200, 200, 200));
        rect(img, 5, 5, 6, 8, rgb(160, 160, 160));
    }

    private static void drawWaterBucket(BufferedImage img) {
        drawBucket(img);
        rect(img, 5, 5, 6, 4, rgb(50, 100, 220));
    }

    private static void drawLavaBucket(BufferedImage img) {
        drawBucket(img);
        rect(img, 5, 5, 6, 4, rgb(220, 100, 0));
    }

    private static void drawBone(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 4, 7, 8, 2, rgb(230, 230, 210));
        rect(img, 3, 6, 2, 4, rgb(230, 230, 210));
        rect(img, 11, 6, 2, 4, rgb(230, 230, 210));
    }

    private static void drawBoneMeal(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 5, 5, 6, 6, rgb(240, 240, 230));
        rect(img, 6, 4, 4, 1, rgb(220, 220, 210));
    }

    private static void drawApple(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 5, 5, 6, 7, rgb(200, 30, 30));
        rect(img, 6, 4, 4, 2, rgb(220, 40, 40));
        setPix(img, 7, 3, rgb(100, 60, 20));
        setPix(img, 8, 2, rgb(60, 140, 30));
    }

    private static void drawStringItem(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        for (int i = 0; i < 8; i++) {
            setPix(img, 4 + i, 5 + (i % 3), rgb(220, 220, 220));
            setPix(img, 4 + i, 8 + (i % 2), rgb(200, 200, 200));
        }
    }

    private static void drawArrow(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 7, 2, 2, 10, rgb(140, 100, 50));
        rect(img, 6, 2, 4, 3, rgb(180, 180, 180));
        setPix(img, 7, 1, rgb(200, 200, 200));
        setPix(img, 8, 1, rgb(200, 200, 200));
    }

    private static void drawBow(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        for (int i = 0; i < 12; i++) {
            int x = 4 + (i < 6 ? i / 2 : (11 - i) / 2);
            setPix(img, x, 2 + i, rgb(140, 100, 40));
        }
        rect(img, 10, 3, 1, 10, rgb(200, 200, 200));
    }

    private static void drawLeatherItem(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 3, 3, 10, 10, rgb(160, 100, 40));
        rect(img, 4, 4, 8, 8, rgb(180, 120, 50));
    }

    private static void drawFeather(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        for (int i = 0; i < 10; i++) {
            setPix(img, 7 + i / 4, 2 + i, rgb(240, 240, 240));
            setPix(img, 8 + i / 4, 2 + i, rgb(220, 220, 220));
        }
    }

    private static void drawSlimeballItem(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 5, 5, 6, 6, rgba(80, 200, 80, 200));
        rect(img, 6, 4, 4, 8, rgba(80, 200, 80, 200));
    }

    private static void drawFlintItem(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 5, 4, 6, 8, rgb(100, 100, 100));
        rect(img, 6, 3, 4, 2, rgb(120, 120, 120));
    }

    private static void drawPaperItem(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 3, 3, 10, 10, rgb(240, 240, 230));
        for (int y = 5; y < 12; y += 2) rect(img, 4, y, 8, 1, rgb(200, 200, 190));
    }

    private static void drawBookItem(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 3, 2, 10, 12, rgb(140, 80, 30));
        rect(img, 4, 3, 8, 10, rgb(240, 240, 230));
        rect(img, 3, 2, 1, 12, rgb(120, 60, 20));
    }

    private static void drawGunpowderItem(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        for (int i = 0; i < 8; i++) {
            int x = 4 + (i * 3) % 8, y = 5 + (i * 2) % 6;
            setPix(img, x, y, rgb(80, 80, 80));
        }
    }

    private static void drawRedstoneItem(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        for (int i = 0; i < 6; i++) {
            int x = 5 + (i * 2) % 6, y = 6 + (i * 3) % 4;
            setPix(img, x, y, rgb(200, 0, 0));
            setPix(img, x + 1, y, rgb(180, 0, 0));
        }
    }

    private static void drawEmeraldItem(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 6, 3, 4, 10, rgb(30, 200, 60));
        rect(img, 5, 5, 6, 6, rgb(40, 220, 70));
        setPix(img, 7, 4, rgb(100, 255, 130));
    }

    private static void drawLapisItem(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        for (int i = 0; i < 6; i++) {
            int x = 5 + (i * 2) % 6, y = 5 + (i * 3) % 6;
            setPix(img, x, y, rgb(30, 50, 200));
            setPix(img, x + 1, y, rgb(40, 60, 220));
        }
    }

    private static void drawQuartzItem(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 5, 3, 6, 10, rgb(240, 230, 210));
        rect(img, 6, 2, 4, 2, rgb(230, 220, 200));
    }

    private static void drawEnderPearl(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 5, 5, 6, 6, rgb(0, 100, 80));
        rect(img, 6, 4, 4, 8, rgb(0, 120, 90));
        setPix(img, 7, 6, rgb(50, 180, 150));
    }

    private static void drawGoldenApple(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 5, 5, 6, 7, rgb(255, 215, 0));
        rect(img, 6, 4, 4, 2, rgb(230, 195, 0));
        setPix(img, 7, 3, rgb(100, 60, 20));
        setPix(img, 8, 2, rgb(60, 140, 30));
    }

    private static void drawGoldenCarrot(BufferedImage img) {
        fill(img, rgba(0, 0, 0, 0));
        rect(img, 6, 5, 4, 8, rgb(255, 180, 0));
        rect(img, 7, 4, 2, 2, rgb(255, 200, 50));
        setPix(img, 7, 3, rgb(60, 160, 30));
        setPix(img, 8, 3, rgb(40, 140, 20));
    }

    /** Dibujo genérico para ítems que aún no tienen textura específica */
    private static void drawDefaultItem(BufferedImage img, ItemType type) {
        fill(img, rgba(0, 0, 0, 0));
        int hash = type.name().hashCode();
        int r = Math.abs(hash % 150) + 50;
        int g = Math.abs((hash / 7) % 150) + 50;
        int b = Math.abs((hash / 13) % 150) + 50;
        rect(img, 3, 3, 10, 10, rgb(r, g, b));
        // Borde más oscuro
        rect(img, 3, 3, 10, 1, rgb(r - 40, g - 40, b - 40));
        rect(img, 3, 12, 10, 1, rgb(r - 40, g - 40, b - 40));
        rect(img, 3, 3, 1, 10, rgb(r - 40, g - 40, b - 40));
        rect(img, 12, 3, 1, 10, rgb(r - 40, g - 40, b - 40));
    }

    /** Genera la textura del jugador Steve */
    public static BufferedImage genSteve() {
        BufferedImage img = new BufferedImage(16, 32, BufferedImage.TYPE_INT_ARGB);
        // Cabeza (8x8 en 16x32, escalamos 2x)
        // Cara de Steve: piel marrón, ojos azules
        for (int y = 0; y < 10; y++) {
            for (int x = 3; x < 13; x++) {
                img.setRGB(x, y, rgb(180, 130, 80)); // piel
            }
        }
        // Pelo
        for (int x = 3; x < 13; x++) {
            img.setRGB(x, 0, rgb(60, 30, 10));
            img.setRGB(x, 1, rgb(60, 30, 10));
            img.setRGB(x, 2, rgb(60, 30, 10));
        }
        // Ojos
        img.setRGB(5, 4, rgb(60, 60, 200));
        img.setRGB(6, 4, rgb(60, 60, 200));
        img.setRGB(9, 4, rgb(60, 60, 200));
        img.setRGB(10, 4, rgb(60, 60, 200));
        // Boca
        img.setRGB(6, 7, rgb(140, 90, 60));
        img.setRGB(7, 7, rgb(140, 90, 60));
        img.setRGB(8, 7, rgb(140, 90, 60));
        img.setRGB(9, 7, rgb(140, 90, 60));
        // Cuerpo (camiseta cyan)
        for (int y = 10; y < 20; y++) {
            for (int x = 3; x < 13; x++) {
                img.setRGB(x, y, rgb(0, 180, 180));
            }
        }
        // Brazos
        for (int y = 10; y < 20; y++) {
            for (int x = 0; x < 3; x++) img.setRGB(x, y, rgb(0, 180, 180));
            for (int x = 13; x < 16; x++) img.setRGB(x, y, rgb(0, 180, 180));
        }
        // Manos
        for (int y = 18; y < 21; y++) {
            for (int x = 0; x < 3; x++) img.setRGB(x, y, rgb(180, 130, 80));
            for (int x = 13; x < 16; x++) img.setRGB(x, y, rgb(180, 130, 80));
        }
        // Pantalones (azul oscuro)
        for (int y = 20; y < 26; y++) {
            for (int x = 3; x < 13; x++) {
                img.setRGB(x, y, rgb(60, 60, 160));
            }
        }
        // Piernas
        for (int y = 20; y < 32; y++) {
            for (int x = 3; x < 7; x++) img.setRGB(x, y, rgb(60, 60, 160));
            for (int x = 9; x < 13; x++) img.setRGB(x, y, rgb(60, 60, 160));
        }
        // Zapatos
        for (int y = 30; y < 32; y++) {
            for (int x = 2; x < 7; x++) img.setRGB(x, y, rgb(80, 80, 80));
            for (int x = 9; x < 14; x++) img.setRGB(x, y, rgb(80, 80, 80));
        }
        return img;
    }
}
