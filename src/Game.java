import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Clase principal del juego: ventana, bucle de juego (20 ticks/s lógica, 60 FPS render),
 * físicas del jugador, entrada de teclado/ratón, rotura y colocación de bloques,
 * cámara y renderizado del mundo.
 */
public class Game extends JPanel implements ActionListener, MouseMotionListener, MouseListener, KeyListener {

    // Buffer de renderizado para doble-buffering
    private BufferedImage buffer;

    // Constantes
    public static final int TILE_SIZE = 16;
    public static final int VIEW_W = 800;
    public static final int VIEW_H = 600;
    public static final int TICKS_PER_SEC = 20;
    public static final int FPS = 60;
    public static final long TICK_INTERVAL = 1000 / TICKS_PER_SEC;

    // Ventana
    private JFrame frame;

    // Mundo
    private World world;
    private long worldSeed = System.currentTimeMillis();

    // Jugador
    private double playerX, playerY;       // posición en bloques (flotante)
    private double playerVX, playerVY;     // velocidad en bloques/s
    private double playerW = 0.6;          // ancho de la caja
    private double playerH = 1.8;          // alto de la caja
    private boolean onGround = false;
    private boolean inWater = false;
    private boolean inLava = false;
    private int airSupply = 300;           // ticks de aire (15s)
    private double health = 20.0;          // vida (10 corazones)
    private int fireTicks = 0;             // ticks de fuego
    private double fallStartY = -1;        // y donde empezó la caída
    private boolean wasFalling = false;

    // Salto: calidad de vida
    private int coyoteTimer = 0;           // ticks restantes de coyote time
    private int jumpBufferTimer = 0;       // ticks restantes de jump buffer
    private boolean spaceHeld = false;
    private boolean shiftHeld = false;
    private boolean autoJump = true;

    // Cámara
    private double cameraX, cameraY;

    // Texturas
    private BufferedImage[] itemTextures;
    private BufferedImage steveTexture;

    // Inventario
    private Inventory inventory;
    private boolean inventoryOpen = false;
    private boolean creativeMode = true;

    // Entrada
    private boolean[] keys = new boolean[512];
    private int mouseX, mouseY;
    private boolean mouseLeft, mouseRight;
    private boolean mouseClickedLeft, mouseClickedRight;

    // Rotura de bloques
    private int breakingX = -1, breakingY = -1;
    private float breakProgress = 0;
    private static final int REACH = 5;

    // Colocación rápida
    private int placeCooldown = 0;

    // Tiempo
    private long lastTickTime;
    private long lastFrameTime;
    private int tickAccumulator = 0;
    private int dayTime = 6000; // 0-24000, 6000 = mediodía

    // Mensajes flotantes de pickup
    private final java.util.List<float[]> pickupMessages = new ArrayList<>(); // {x, y, timer, text}
    private final java.util.List<String> pickupTexts = new ArrayList<>();

    // Stats
    private int recipeCount = 0;

    /** Inicializa y arranca el juego */
    public void iniciar() {
        frame = new JFrame("2D Minecraft");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        this.setPreferredSize(new Dimension(VIEW_W, VIEW_H));
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Generar texturas
        itemTextures = new BufferedImage[ItemType.values().length];
        for (ItemType t : ItemType.values()) {
            itemTextures[t.ordinal()] = TextureGen.gen(t);
        }
        steveTexture = TextureGen.genSteve();

        // Mundo
        world = new World(worldSeed);
        world.ensureChunks(0, 10);

        // Jugador: encontrar posición de spawn
        playerX = 8;
        for (int y = World.HEIGHT - 1; y >= 0; y--) {
            BlockType b = world.getBlock(8, y);
            if (b.solid) {
                playerY = y + 1;
                break;
            }
        }

        // Inventario inicial
        inventory = new Inventory();
        // Dar ítems iniciales en creativo
        inventory.main[0] = new ItemStack(ItemType.WOODEN_PICKAXE, 1);
        inventory.main[1] = new ItemStack(ItemType.WOODEN_AXE, 1);
        inventory.main[2] = new ItemStack(ItemType.WOODEN_SHOVEL, 1);
        inventory.main[3] = new ItemStack(ItemType.STONE, 64);
        inventory.main[4] = new ItemStack(ItemType.OAK_PLANKS, 64);
        inventory.main[5] = new ItemStack(ItemType.DIRT, 64);
        inventory.main[6] = new ItemStack(ItemType.COBBLESTONE, 64);
        inventory.main[7] = new ItemStack(ItemType.TORCH, 64);
        inventory.main[8] = new ItemStack(ItemType.SAND, 64);

        // Contar recetas (simulado, se genera en CraftingManager)
        recipeCount = CraftingManager.countRecipes();

        // Iniciar bucle
        lastTickTime = System.currentTimeMillis();
        lastFrameTime = lastTickTime;
        buffer = new BufferedImage(VIEW_W, VIEW_H, BufferedImage.TYPE_INT_RGB);

        Timer timer = new Timer(1000 / FPS, this);
        timer.start();
        System.out.println("2D Minecraft iniciado. Seed: " + worldSeed);
        System.out.println("Recetas cargadas: " + recipeCount);
    }

    /** Sobrescribe paintComponent para dibujar el buffer */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (buffer != null) {
            g.drawImage(buffer, 0, 0, null);
        }
    }

    /** Bucle principal llamado por el Timer a ~60 FPS */
    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.currentTimeMillis();

        // Procesar ticks de lógica (20/s)
        while (now - lastTickTime >= TICK_INTERVAL) {
            gameTick();
            lastTickTime += TICK_INTERVAL;
        }

        // Renderizar
        render();
        lastFrameTime = now;

        // Reset clicks (solo se procesan una vez)
        mouseClickedLeft = false;
        mouseClickedRight = false;
    }

    /** Un tick de lógica del juego (20 veces por segundo) */
    private void gameTick() {
        // Asegurar chunks cargados
        world.ensureChunks((int) playerX, 6);
        world.unloadDistantChunks((int) playerX, 12);

        // Tick del mundo (líquidos, arena, redstone)
        world.tick();

        // Día/noche
        dayTime = (dayTime + 1) % 24000;

        // Físicas del jugador
        updatePlayerPhysics();

        // Agua/lava: aire y daño
        updateBreathing();
        updateFireAndDamage();

        // Cooldowns
        if (placeCooldown > 0) placeCooldown--;
        if (coyoteTimer > 0) coyoteTimer--;
        if (jumpBufferTimer > 0) jumpBufferTimer--;

        // Rotura de bloque
        if (mouseLeft) {
            updateBreaking();
        } else {
            breakProgress = 0;
            breakingX = -1;
            breakingY = -1;
        }

        // Colocación continua
        if (mouseRight && placeCooldown <= 0) {
            placeBlock();
            placeCooldown = 4; // cada 4 ticks
        }

        // Mensajes de pickup
        pickupMessages.removeIf(m -> {
            m[2] -= 1;
            return m[2] <= 0;
        });
    }

    /** Físicas del jugador: gravedad, movimiento, colisiones, agua */
    private void updatePlayerPhysics() {
        double gravity = 32.0; // bloques/s²
        double maxFall = 78.0;
        double friction = 0.85;
        double moveSpeed = 4.3;
        double jumpVel = 8.4;

        // Comprobar si está en agua/lava
        BlockType bodyBlock = world.getBlock((int) playerX, (int) (playerY + 0.5));
        BlockType headBlock = world.getBlock((int) playerX, (int) (playerY + 1.5));
        inWater = bodyBlock == BlockType.WATER || headBlock == BlockType.WATER;
        inLava = bodyBlock == BlockType.LAVA || headBlock == BlockType.LAVA;

        if (inWater) {
            gravity = 3.2;
            maxFall = 3.0;
            friction = 0.8;
            moveSpeed = 2.5;
            jumpVel = 4.0;
        }
        if (inLava) {
            gravity = 16.0;
            maxFall = 5.0;
            friction = 0.5;
            moveSpeed = 1.5;
            jumpVel = 3.0;
        }

        // Input horizontal
        double targetVX = 0;
        if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) targetVX = -moveSpeed;
        if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) targetVX = moveSpeed;
        playerVX += (targetVX - playerVX) * (inWater ? 0.15 : 0.25);

        // Salto
        boolean jumpPressed = keys[KeyEvent.VK_SPACE];
        if (jumpPressed && !spaceHeld) {
            jumpBufferTimer = 6; // 300ms = 6 ticks
        }
        spaceHeld = jumpPressed;

        if (onGround || coyoteTimer > 0) {
            if (jumpPressed || jumpBufferTimer > 0) {
                playerVY = -jumpVel;
                onGround = false;
                coyoteTimer = 0;
                jumpBufferTimer = 0;
                fallStartY = playerY;
            }
        }

        // En agua: nadar
        if (inWater && jumpPressed) {
            playerVY = -2.0;
        }
        if (inLava && jumpPressed) {
            playerVY = -1.5;
        }

        // Shift: hundirse o agacharse
        shiftHeld = keys[KeyEvent.VK_SHIFT];
        if (shiftHeld && inWater) {
            playerVY = 2.0;
        }

        // Salto variable: soltar espacio corta el salto
        if (!jumpPressed && playerVY < -2) {
            playerVY *= 0.5;
        }

        // Gravedad
        playerVY += gravity / TICKS_PER_SEC;
        if (playerVY > maxFall) playerVY = maxFall;

        // Movimiento con colisiones (eje X luego Y)
        double newX = playerX + playerVX / TICKS_PER_SEC;
        if (!collides(newX, playerY)) {
            playerX = newX;
        } else {
            playerVX = 0;
            // Auto-salto de 1 bloque
            if (autoJump && onGround && !collides(newX, playerY - 1.01)) {
                BlockType above = world.getBlock((int) newX, (int) (playerY + 1));
                if (!above.solid) {
                    playerX = newX;
                    playerY -= 1.01;
                }
            }
        }

        double newY = playerY + playerVY / TICKS_PER_SEC;
        if (!collides(playerX, newY)) {
            playerY = newY;
            if (playerVY > 0 && !wasFalling) {
                fallStartY = playerY;
                wasFalling = true;
            }
            onGround = false;
            coyoteTimer = 0;
        } else {
            if (playerVY > 0) {
                // Aterrizar: alinear al tope del bloque
                playerY = Math.floor(playerY + playerH) - playerH + 0.001;
                onGround = true;
                coyoteTimer = 5; // 250ms = 5 ticks de coyote time

                // Daño de caída
                if (wasFalling && fallStartY >= 0) {
                    double fallDist = playerY - fallStartY;
                    if (fallDist > 3) {
                        double damage = (fallDist - 3) * 0.5;
                        health -= damage;
                    }
                }
                wasFalling = false;
                fallStartY = -1;
            } else if (playerVY < 0) {
                // Golpe en techo
                playerY = Math.ceil(playerY);
            }
            playerVY = 0;
        }

        // Fricción
        if (onGround) {
            playerVX *= friction;
        }

        // Shift impide caer del borde
        if (shiftHeld && onGround) {
            // Simplificado: no hacer nada especial por ahora
        }

        // Cámara sigue al jugador
        cameraX = playerX - VIEW_W / (2.0 * TILE_SIZE);
        cameraY = playerY - VIEW_H / (2.0 * TILE_SIZE);
    }

    /** Comprueba si la caja del jugador colisiona con bloques sólidos */
    private boolean collides(double px, double py) {
        int minX = (int) Math.floor(px - playerW / 2);
        int maxX = (int) Math.floor(px + playerW / 2);
        int minY = (int) Math.floor(py);
        int maxY = (int) Math.floor(py + playerH);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                BlockType b = world.getBlock(x, y);
                if (b.solid) return true;
            }
        }
        return false;
    }

    /** Actualiza el suministro de aire */
    private void updateBreathing() {
        BlockType head = world.getBlock((int) playerX, (int) (playerY + 1.5));
        if (head == BlockType.WATER) {
            airSupply--;
            if (airSupply <= 0) {
                // Daño por ahogo cada segundo
                if (world.currentTick % 20 == 0) {
                    health -= 2;
                }
            }
        } else {
            airSupply = Math.min(300, airSupply + 4);
        }
    }

    /** Actualiza fuego y daño de lava */
    private void updateFireAndDamage() {
        if (inLava) {
            health -= 4.0 / TICKS_PER_SEC; // 4 dps
            fireTicks = 300; // 15s
        }
        if (fireTicks > 0) {
            fireTicks--;
            if (world.currentTick % 20 == 0) {
                health -= 1; // 1 dps de fuego
            }
            // El agua apaga el fuego
            if (inWater) fireTicks = 0;
        }
        if (health <= 0) {
            // Respawn simplificado
            health = 20;
            playerX = 8;
            for (int y = World.HEIGHT - 1; y >= 0; y--) {
                if (world.getBlock(8, y).solid) { playerY = y + 1; break; }
            }
        }
    }

    /** Actualiza la rotura de bloque */
    private void updateBreaking() {
        int targetX = (int) Math.floor(cameraX + mouseX / (double) TILE_SIZE);
        int targetY = (int) Math.floor(cameraY + mouseY / (double) TILE_SIZE);

        double dist = Math.sqrt(Math.pow(targetX + 0.5 - playerX, 2) + Math.pow(targetY + 0.5 - (playerY + 0.9), 2));
        if (dist > REACH) return;

        BlockType target = world.getBlock(targetX, targetY);
        if (target == BlockType.AIR || target == BlockType.BEDROCK) return;

        if (targetX != breakingX || targetY != breakingY) {
            breakingX = targetX;
            breakingY = targetY;
            breakProgress = 0;
        }

        if (creativeMode) {
            world.setBlock(targetX, targetY, BlockType.AIR);
            return;
        }

        // Calcular tiempo de rotura
        float speed = 1.0f;
        ItemStack held = inventory.getHeld();
        if (!held.isEmpty() && held.type.isTool()) {
            String toolType = held.type.toolType();
            // Herramienta correcta acelera
            boolean correctTool = (target.hardness > 0 && toolType.equals("pickaxe") && isStoneLike(target))
                || (toolType.equals("axe") && target.flammable)
                || (toolType.equals("shovel") && (target == BlockType.DIRT || target == BlockType.SAND || target == BlockType.GRAVEL));
            if (correctTool) {
                speed = held.type.miningSpeed();
            }
        }

        float breakTime = target.hardness * (target.toolTier >= 0 ? 1.5f : 5.0f) / speed;
        if (breakTime <= 0) breakTime = 0.05f;
        float progressPerTick = 1.0f / (breakTime * TICKS_PER_SEC);
        breakProgress += progressPerTick;

        if (breakProgress >= 1.0f) {
            // Romper bloque
            world.setBlock(targetX, targetY, BlockType.AIR);
            breakProgress = 0;
            breakingX = -1;

            // Auto-pickup: dar el ítem al inventario
            ItemType drop = getBlockDrop(target, held);
            if (drop != null) {
                int remaining = inventory.insert(new ItemStack(drop, 1));
                if (remaining > 0) {
                    // Soltar como entidad (simplificado: no se implementa por ahora)
                }
                // Mensaje de pickup
                pickupMessages.add(new float[]{targetX, targetY, 40, 0});
                pickupTexts.add("+1 " + formatName(drop.name()));
            }

            // Desgaste de herramienta
            if (!held.isEmpty() && held.type.isTool()) {
                if (held.useDurability()) {
                    inventory.main[inventory.selectedSlot] = new ItemStack(null, 0);
                }
            }
        }
    }

    /** Devuelve el ítem que suelta un bloque */
    private ItemType getBlockDrop(BlockType block, ItemStack tool) {
        switch (block) {
            case STONE:
                if (tool.type != null && tool.type.toolType().equals("pickaxe")) return ItemType.COBBLESTONE;
                return null;
            case GRASS: return ItemType.DIRT;
            case COAL_ORE:
                if (tool.type != null && tool.type.toolType().equals("pickaxe")) return ItemType.COAL;
                return null;
            case IRON_ORE:
                if (tool.type != null && tool.type.toolTier() >= 1) return ItemType.RAW_IRON;
                return null;
            case GOLD_ORE:
                if (tool.type != null && tool.type.toolTier() >= 2) return ItemType.RAW_GOLD;
                return null;
            case DIAMOND_ORE:
                if (tool.type != null && tool.type.toolTier() >= 2) return ItemType.DIAMOND;
                return null;
            case DIAMOND_BLOCK: return ItemType.DIAMOND_BLOCK;
            case OAK_LOG: return ItemType.OAK_LOG;
            case OAK_PLANKS: return ItemType.OAK_PLANKS;
            case SAND: return ItemType.SAND;
            case GRAVEL: return ItemType.GRAVEL;
            case CRAFTING_TABLE: return ItemType.CRAFTING_TABLE;
            case FURNACE: return ItemType.FURNACE;
            case TORCH: return ItemType.TORCH;
            case GLASS: return null; // se rompe sin soltar
            case REDSTONE_WIRE: return ItemType.REDSTONE;
            case REDSTONE_TORCH: return ItemType.REDSTONE_TORCH;
            case LEVER: return ItemType.LEVER;
            default:
                // Buscar ItemType por nombre correspondiente
                try {
                    return ItemType.valueOf(block.name());
                } catch (Exception e) {
                    return null;
                }
        }
    }

    private boolean isStoneLike(BlockType b) {
        return b == BlockType.STONE || b == BlockType.COBBLESTONE || b == BlockType.SANDSTONE
            || b == BlockType.OBSIDIAN || b == BlockType.COAL_ORE || b == BlockType.IRON_ORE
            || b == BlockType.GOLD_ORE || b == BlockType.DIAMOND_ORE;
    }

    /** Coloca un bloque */
    private void placeBlock() {
        int targetX = (int) Math.floor(cameraX + mouseX / (double) TILE_SIZE);
        int targetY = (int) Math.floor(cameraY + mouseY / (double) TILE_SIZE);

        double dist = Math.sqrt(Math.pow(targetX + 0.5 - playerX, 2) + Math.pow(targetY + 0.5 - (playerY + 0.9), 2));
        if (dist > REACH) return;

        BlockType target = world.getBlock(targetX, targetY);
        if (!target.replaceable && target != BlockType.AIR) return;

        ItemStack held = inventory.getHeld();
        if (held.isEmpty()) return;

        BlockType toPlace;
        if (held.type.isBlock()) {
            toPlace = held.type.blockType;
        } else if (held.type == ItemType.WATER_BUCKET) {
            toPlace = BlockType.WATER;
        } else if (held.type == ItemType.LAVA_BUCKET) {
            toPlace = BlockType.LAVA;
        } else {
            return;
        }

        // No colocar dentro del jugador
        int pMinX = (int) Math.floor(playerX - playerW / 2);
        int pMaxX = (int) Math.floor(playerX + playerW / 2);
        int pMinY = (int) Math.floor(playerY);
        int pMaxY = (int) Math.floor(playerY + playerH);
        if (targetX >= pMinX && targetX <= pMaxX && targetY >= pMinY && targetY <= pMaxY) {
            if (toPlace.solid) return;
        }

        // En supervivencia, debe tener apoyo adyacente
        if (!creativeMode) {
            boolean hasSupport = false;
            int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
            for (int[] d : dirs) {
                BlockType adj = world.getBlock(targetX + d[0], targetY + d[1]);
                if (adj.solid) { hasSupport = true; break; }
            }
            if (!hasSupport) return;
        }

        world.setBlock(targetX, targetY, toPlace);

        // Consumir ítem en supervivencia
        if (!creativeMode) {
            if (held.type == ItemType.WATER_BUCKET) {
                inventory.main[inventory.selectedSlot] = new ItemStack(ItemType.BUCKET, 1);
            } else if (held.type == ItemType.LAVA_BUCKET) {
                inventory.main[inventory.selectedSlot] = new ItemStack(ItemType.BUCKET, 1);
            } else {
                inventory.consumeOne();
                if (inventory.getHeld().isEmpty()) {
                    inventory.main[inventory.selectedSlot] = new ItemStack(null, 0);
                }
            }
        }
    }

    /** Formatea un nombre de ítem legible */
    private String formatName(String name) {
        String[] words = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(words[i].substring(0, 1).toUpperCase()).append(words[i].substring(1).toLowerCase());
        }
        return sb.toString();
    }

    // ===== Renderizado =====

    /** Renderiza un frame completo */
    private void render() {
        if (buffer == null) buffer = new BufferedImage(VIEW_W, VIEW_H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffer.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Fondo (cielo)
        int skyBrightness = getSkyBrightness();
        int skyR = (int)(135 * skyBrightness / 255f);
        int skyG = (int)(206 * skyBrightness / 255f);
        int skyB = (int)(235 * skyBrightness / 255f);
        g.setColor(new Color(skyR, skyG, skyB));
        g.fillRect(0, 0, VIEW_W, VIEW_H);

        // Renderizar chunks visibles
        int startChunkX = (int) Math.floor(cameraX / 16) - 1;
        int endChunkX = (int) Math.floor((cameraX + VIEW_W / (double) TILE_SIZE) / 16) + 1;
        int startY = Math.max(0, (int) Math.floor(cameraY) - 1);
        int endY = Math.min(World.HEIGHT - 1, (int) Math.ceil(cameraY + VIEW_H / (double) TILE_SIZE) + 1);

        for (int cx = startChunkX; cx <= endChunkX; cx++) {
            Chunk chunk = world.getChunk(cx);
            if (chunk == null) continue;
            int baseX = cx * 16;
            for (int lx = 0; lx < 16; lx++) {
                int wx = baseX + lx;
                for (int y = startY; y <= endY; y++) {
                    BlockType block = chunk.getBlock(lx, y);
                    if (block == BlockType.AIR) continue;

                    int screenX = (int) ((wx - cameraX) * TILE_SIZE);
                    int screenY = (int) ((y - cameraY) * TILE_SIZE);

                    drawBlock(g, block, screenX, screenY, wx, y);
                }
            }
        }

        // Renderizar jugador
        drawPlayer(g);

        // HUD
        drawHUD(g);

        // Indicador de rotura
        if (breakingX >= 0 && breakProgress > 0) {
            int screenX = (int) ((breakingX - cameraX) * TILE_SIZE);
            int screenY = (int) ((breakingY - cameraY) * TILE_SIZE);
            g.setColor(new Color(0, 0, 0, (int)(breakProgress * 150)));
            g.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
            // Grietas
            int stage = (int)(breakProgress * 10);
            g.setColor(new Color(0, 0, 0, 100 + stage * 15));
            for (int i = 0; i < stage; i++) {
                int gx = screenX + (i * 3) % TILE_SIZE;
                int gy = screenY + (i * 5) % TILE_SIZE;
                g.drawLine(gx, gy, gx + 3, gy + 3);
            }
        }

        // Cursor: contorno del bloque apuntado
        int cursorBX = (int) Math.floor(cameraX + mouseX / (double) TILE_SIZE);
        int cursorBY = (int) Math.floor(cameraY + mouseY / (double) TILE_SIZE);
        int csx = (int) ((cursorBX - cameraX) * TILE_SIZE);
        int csy = (int) ((cursorBY - cameraY) * TILE_SIZE);
        g.setColor(new Color(255, 255, 255, 100));
        g.drawRect(csx, csy, TILE_SIZE - 1, TILE_SIZE - 1);

        g.dispose();
        // Solicitar redraw de Swing
        repaint();
    }

    /** Dibuja un bloque en pantalla */
    private void drawBlock(Graphics2D g, BlockType block, int sx, int sy, int wx, int wy) {
        ItemType itemType = null;
        try { itemType = ItemType.valueOf(block.name()); } catch (Exception e) {}

        if (block.liquid) {
            int level = world.getLevel(wx, wy);
            int alpha = level == 0 ? 180 : 120;
            if (block == BlockType.WATER) {
                g.setColor(new Color(50, 100, 220, alpha));
                int topOffset = level == 0 ? 0 : (8 - level);
                g.fillRect(sx, sy + topOffset * 2, TILE_SIZE, TILE_SIZE - topOffset * 2);
            } else {
                g.setColor(new Color(220, 100, 0, alpha));
                g.fillRect(sx, sy, TILE_SIZE, TILE_SIZE);
            }
            return;
        }

        if (itemType != null && itemTextures[itemType.ordinal()] != null) {
            if (block.transparent && block == BlockType.TORCH) {
                // Dibujar antorcha con transparencia
                g.drawImage(itemTextures[itemType.ordinal()], sx, sy, TILE_SIZE, TILE_SIZE, null);
            } else if (block.transparent && !block.solid) {
                g.drawImage(itemTextures[itemType.ordinal()], sx, sy, TILE_SIZE, TILE_SIZE, null);
            } else {
                g.drawImage(itemTextures[itemType.ordinal()], sx, sy, TILE_SIZE, TILE_SIZE, null);
            }
        } else {
            // Color de respaldo
            g.setColor(new Color(200, 100, 200));
            g.fillRect(sx, sy, TILE_SIZE, TILE_SIZE);
        }
    }

    /** Dibuja al jugador (Steve) */
    private void drawPlayer(Graphics2D g) {
        int screenX = (int) ((playerX - 0.4 - cameraX) * TILE_SIZE);
        int screenY = (int) ((playerY - cameraY) * TILE_SIZE);
        int w = (int) (playerW * TILE_SIZE);
        int h = (int) (playerH * TILE_SIZE);

        // Escalar Steve (16x32) al tamaño de pantalla
        g.drawImage(steveTexture, screenX, screenY, w + 6, h, null);

        // Indicador de fuego
        if (fireTicks > 0) {
            g.setColor(new Color(255, 100, 0, 100));
            g.fillRect(screenX - 1, screenY - 1, w + 8, h + 2);
        }

        // Ítem en la mano
        ItemStack held = inventory.getHeld();
        if (!held.isEmpty()) {
            int handX = screenX + w + 2;
            int handY = screenY + h / 2 - 4;
            if (itemTextures[held.type.ordinal()] != null) {
                g.drawImage(itemTextures[held.type.ordinal()], handX, handY, 12, 12, null);
            }
        }
    }

    /** Dibuja el HUD: hotbar, vida, aire, mensajes */
    private void drawHUD(Graphics2D g) {
        // Hotbar
        int hotbarX = VIEW_W / 2 - 9 * 20;
        int hotbarY = VIEW_H - 44;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(hotbarX - 2, hotbarY - 2, 9 * 40 + 4, 44);
        for (int i = 0; i < 9; i++) {
            int sx = hotbarX + i * 40;
            g.setColor(i == inventory.selectedSlot ? new Color(200, 200, 200) : new Color(80, 80, 80));
            g.fillRect(sx, hotbarY, 38, 38);
            g.setColor(new Color(40, 40, 40));
            g.drawRect(sx, hotbarY, 38, 38);

            ItemStack stack = inventory.main[i];
            if (!stack.isEmpty()) {
                drawItemIcon(g, stack, sx + 3, hotbarY + 3, 32);
                // Cantidad
                if (stack.count > 1) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("SansSerif", Font.BOLD, 12));
                    g.drawString(String.valueOf(stack.count), sx + 26, hotbarY + 36);
                }
                // Durabilidad
                if (stack.durability > 0 && stack.type.durability() > 0) {
                    float pct = (float) stack.durability / stack.type.durability();
                    int barW = 30;
                    g.setColor(new Color(0, 0, 0, 150));
                    g.fillRect(sx + 4, hotbarY + 32, barW, 3);
                    Color barColor = pct > 0.5f ? Color.GREEN : pct > 0.25f ? Color.YELLOW : Color.RED;
                    g.setColor(barColor);
                    g.fillRect(sx + 4, hotbarY + 32, (int)(barW * pct), 3);
                }
            }
        }

        // Vida (corazones)
        int heartsX = VIEW_W / 2 - 9 * 9;
        int heartsY = VIEW_H - 54;
        for (int i = 0; i < 10; i++) {
            int hx = heartsX + i * 18;
            float heartHP = (float)(health / 2.0 - i);
            if (heartHP >= 1) {
                g.setColor(Color.RED);
                g.fillOval(hx, heartsY, 14, 14);
            } else if (heartHP > 0) {
                g.setColor(new Color(100, 0, 0));
                g.fillOval(hx, heartsY, 14, 14);
                g.setColor(Color.RED);
                g.fillOval(hx, heartsY, 7, 14);
            } else {
                g.setColor(new Color(60, 60, 60));
                g.fillOval(hx, heartsY, 14, 14);
            }
        }

        // Aire (burbujas)
        if (airSupply < 300) {
            int bubblesX = VIEW_W / 2 - 9 * 9;
            int bubblesY = VIEW_H - 70;
            int bubbleCount = (int)(airSupply / 30.0);
            for (int i = 0; i < 10; i++) {
                int bx = bubblesX + i * 18;
                if (i < bubbleCount) {
                    g.setColor(new Color(100, 150, 255));
                } else {
                    g.setColor(new Color(60, 60, 80));
                }
                g.fillOval(bx, bubblesY, 12, 12);
            }
        }

        // Tinte azul si está bajo el agua
        BlockType headBlock = world.getBlock((int) playerX, (int) (playerY + 1.5));
        if (headBlock == BlockType.WATER) {
            g.setColor(new Color(0, 50, 150, 60));
            g.fillRect(0, 0, VIEW_W, VIEW_H);
        }
        if (headBlock == BlockType.LAVA) {
            g.setColor(new Color(200, 80, 0, 80));
            g.fillRect(0, 0, VIEW_W, VIEW_H);
        }

        // Mensajes de pickup
        for (int i = 0; i < pickupMessages.size() && i < pickupTexts.size(); i++) {
            float[] msg = pickupMessages.get(i);
            int alpha = (int)(msg[2] / 40f * 255);
            g.setColor(new Color(255, 255, 100, alpha));
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString(pickupTexts.get(i), 10, 30 + i * 18);
        }

        // Info de debug
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString(String.format("XYZ: %.1f / %.1f | Chunks: %d | Tick: %d",
            playerX, playerY, world.chunks.size(), world.currentTick), 10, 15);
        g.drawString("Recetas: " + recipeCount, 10, 30);

        // Modo
        if (creativeMode) {
            g.drawString("CREATIVO", VIEW_W - 80, 15);
        }
    }

    /** Dibuja el icono de un ítem */
    private void drawItemIcon(Graphics2D g, ItemStack stack, int x, int y, int size) {
        if (stack.type == null) return;
        BufferedImage tex = itemTextures[stack.type.ordinal()];
        if (tex != null) {
            g.drawImage(tex, x, y, size, size, null);
        }
    }

    /** Brillo del cielo según la hora del día (0-24000) */
    private float getSkyBrightness() {
        // 6000 = mediodía, 18000 = medianoche
        float t = dayTime / 24000f;
        if (t < 0.25f) return 0.2f + t * 3.2f; // amanecer
        if (t < 0.75f) return 1.0f; // día
        return 1.0f - (t - 0.75f) * 3.2f; // atardecer/noche
    }

    // ===== Entrada =====

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < keys.length) keys[code] = true;

        // Selección de hotbar con teclas 1-9
        if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_9) {
            inventory.selectedSlot = code - KeyEvent.VK_1;
        }

        // E = inventario (toggle)
        if (code == KeyEvent.VK_E) {
            inventoryOpen = !inventoryOpen;
        }

        // Q = soltar ítem
        if (code == KeyEvent.VK_Q) {
            ItemStack held = inventory.getHeld();
            if (!held.isEmpty()) {
                held.count--;
                if (held.count <= 0) {
                    inventory.main[inventory.selectedSlot] = new ItemStack(null, 0);
                }
            }
        }

        // F3 = debug (no implementado completamente)
        // Esc = pausa
        if (code == KeyEvent.VK_ESCAPE) {
            // Simplificado: no pausar
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < keys.length) keys[code] = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseLeft = true;
            mouseClickedLeft = true;
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            mouseRight = true;
            mouseClickedRight = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) mouseLeft = false;
        if (e.getButton() == MouseEvent.BUTTON3) mouseRight = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
