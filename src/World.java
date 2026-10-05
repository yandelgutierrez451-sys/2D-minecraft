import java.util.*;

/**
 * Mundo del juego: gestiona los chunks cargados, generación procedural y
 * las operaciones de bloque que cruzan límites de chunk.
 * Mundo infinito en horizontal, 256 bloques de alto.
 */
public class World {
    public static final int HEIGHT = 256;
    public static final int CHUNK_W = 16;
    public static final int SEA_LEVEL = 64;

    public final Map<Integer, Chunk> chunks = new HashMap<>();
    private final long seed;
    private final Random rng;

    // Cola de ticks programados: (tick, x, y, tipo)
    public final ScheduledTickQueue tickQueue = new ScheduledTickQueue();
    public int currentTick = 0;

    // Lista de bloques colocados que necesitan notificar vecinos
    private final List<int[]> pendingUpdates = new ArrayList<>();

    public World(long seed) {
        this.seed = seed;
        this.rng = new Random(seed);
    }

    public long getSeed() { return seed; }

    /** Devuelve el chunk que contiene la columna x del mundo */
    public Chunk getChunk(int cx) {
        return chunks.get(cx);
    }

    /** Obtiene o genera un chunk */
    public Chunk getOrGenerateChunk(int cx) {
        Chunk c = chunks.get(cx);
        if (c == null) {
            c = new Chunk(cx);
            generateChunk(c);
            chunks.put(cx, c);
        }
        return c;
    }

    /** Asegura que los chunks en un rango están cargados */
    public void ensureChunks(int worldX, int radius) {
        int minCx = (worldX - radius) >> 4;
        int maxCx = (worldX + radius) >> 4;
        for (int cx = minCx; cx <= maxCx; cx++) {
            getOrGenerateChunk(cx);
        }
    }

    /** Descarga chunks lejanos */
    public void unloadDistantChunks(int worldX, int keepRadius) {
        int minCx = (worldX - keepRadius) >> 4;
        int maxCx = (worldX + keepRadius) >> 4;
        chunks.keySet().removeIf(cx -> cx < minCx || cx > maxCx);
    }

    /** Obtiene el tipo de bloque en coords del mundo */
    public BlockType getBlock(int x, int y) {
        if (y < 0 || y >= HEIGHT) return BlockType.AIR;
        int cx = x >> 4;
        int lx = x & 15;
        Chunk c = chunks.get(cx);
        if (c == null) return BlockType.AIR;
        return c.getBlock(lx, y);
    }

    /** Obtiene el dato en coords del mundo */
    public short getData(int x, int y) {
        if (y < 0 || y >= HEIGHT) return 0;
        int cx = x >> 4;
        int lx = x & 15;
        Chunk c = chunks.get(cx);
        if (c == null) return 0;
        return c.getData(lx, y);
    }

    /** Coloca un bloque y notifica vecinos */
    public void setBlock(int x, int y, BlockType type) {
        if (y < 0 || y >= HEIGHT) return;
        int cx = x >> 4;
        int lx = x & 15;
        Chunk c = getOrGenerateChunk(cx);
        BlockType old = c.getBlock(lx, y);
        c.setBlock(lx, y, type);
        c.setData(lx, y, (short) 0);
        notifyNeighbors(x, y, old);
        // Si el bloque es fallable, programar tick
        if (type.fallable) {
            tickQueue.schedule(currentTick + 2, x, y, type);
        }
        // Si es líquido fuente, programar tick de flujo
        if (type.liquid) {
            tickQueue.schedule(currentTick + (type == BlockType.WATER ? 5 : 30), x, y, type);
        }
    }

    /** Coloca un bloque con dato */
    public void setBlock(int x, int y, BlockType type, short d) {
        if (y < 0 || y >= HEIGHT) return;
        int cx = x >> 4;
        int lx = x & 15;
        Chunk c = getOrGenerateChunk(cx);
        c.setBlock(lx, y, type, d);
        notifyNeighbors(x, y, BlockType.AIR);
    }

    /** Establece el nivel de líquido */
    public void setLevel(int x, int y, int level) {
        if (y < 0 || y >= HEIGHT) return;
        int cx = x >> 4;
        int lx = x & 15;
        Chunk c = chunks.get(cx);
        if (c == null) return;
        c.setLevel(lx, y, level);
    }

    /** Obtiene el nivel de líquido */
    public int getLevel(int x, int y) {
        if (y < 0 || y >= HEIGHT) return 0;
        int cx = x >> 4;
        int lx = x & 15;
        Chunk c = chunks.get(cx);
        if (c == null) return 0;
        return c.getLevel(lx, y);
    }

    /** Potencia de redstone */
    public int getPower(int x, int y) {
        if (y < 0 || y >= HEIGHT) return 0;
        int cx = x >> 4;
        int lx = x & 15;
        Chunk c = chunks.get(cx);
        if (c == null) return 0;
        return c.getPower(lx, y);
    }

    public void setPower(int x, int y, int power) {
        if (y < 0 || y >= HEIGHT) return;
        int cx = x >> 4;
        int lx = x & 15;
        Chunk c = getOrGenerateChunk(cx);
        c.setPower(lx, y, power);
    }

    /** Notifica a los 4 vecinos de un cambio */
    public void notifyNeighbors(int x, int y, BlockType source) {
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : dirs) {
            int nx = x + d[0], ny = y + d[1];
            BlockType neighbor = getBlock(nx, ny);
            if (neighbor.fallable) {
                tickQueue.schedule(currentTick + 2, nx, ny, neighbor);
            }
            // Notificar a líquidos vecinos para recalcular flujo
            if (neighbor.liquid) {
                tickQueue.schedule(currentTick + 1, nx, ny, neighbor);
            }
            // Antorcha sin apoyo se cae
            if (neighbor == BlockType.TORCH) {
                if (d[1] == 0) { // cambio lateral: la antorcha podría estar sobre este bloque
                    // verificar si el bloque de abajo de la antorcha es el que cambió
                    BlockType below = getBlock(nx, ny - 1);
                    if (!below.solid && getBlock(nx, ny) == BlockType.TORCH) {
                        setBlock(nx, ny, BlockType.AIR);
                    }
                }
            }
        }
    }

    /** Procesa los ticks programados del tick actual */
    public void processTicks(int maxUpdates) {
        int processed = 0;
        while (processed < maxUpdates) {
            ScheduledTickQueue.TickEntry entry = tickQueue.poll(currentTick);
            if (entry == null) break;
            BlockType type = getBlock(entry.x, entry.y);
            if (type != entry.blockType) continue; // el bloque ya cambió

            if (type.liquid) {
                simulateLiquid(entry.x, entry.y, type);
            } else if (type.fallable) {
                simulateFalling(entry.x, entry.y, type);
            }

            processed++;
        }
    }

    /** Simula el flujo de líquido (agua/lava) */
    private void simulateLiquid(int x, int y, BlockType type) {
        int level = getLevel(x, y);
        int tickDelay = type == BlockType.WATER ? 5 : 30;
        boolean isSource = level == 0;

        // (a) Si no es fuente, comprobar si sigue alimentado
        if (!isSource) {
            boolean fed = false;
            BlockType above = getBlock(x, y + 1);
            if (above == type) fed = true;
            if (!fed) {
                int leftLvl = (getBlock(x - 1, y) == type) ? getLevel(x - 1, y) : 99;
                int rightLvl = (getBlock(x + 1, y) == type) ? getLevel(x + 1, y) : 99;
                if (leftLvl < level || leftLvl == 0) fed = true;
                if (rightLvl < level || rightLvl == 0) fed = true;
            }
            if (!fed) {
                // Secar: subir nivel hasta desaparecer
                level++;
                if (level >= 8) {
                    setBlock(x, y, BlockType.AIR);
                    notifyNeighbors(x, y, type);
                    return;
                }
                setLevel(x, y, level);
                tickQueue.schedule(currentTick + tickDelay, x, y, type);
                return;
            }
        }

        // (b) Intentar caer
        BlockType below = getBlock(x, y - 1);
        if (!below.solid && below != type) {
            if (below.replaceable || below == BlockType.AIR) {
                setBlock(x, y - 1, type, (short) 8); // columna que cae
                if (!isSource) {
                    setLevel(x, y - 1, 8);
                }
                tickQueue.schedule(currentTick + tickDelay, x, y - 1, type);
                if (!isSource) {
                    level++;
                    if (level >= 8) {
                        setBlock(x, y, BlockType.AIR);
                    } else {
                        setLevel(x, y, level);
                        tickQueue.schedule(currentTick + tickDelay, x, y, type);
                    }
                } else {
                    tickQueue.schedule(currentTick + tickDelay, x, y, type);
                }
                notifyNeighbors(x, y, type);
                return;
            }
        }

        // Si hay agua+abajo, no seguir extendiendo
        if (below == type) {
            // solo extender horizontalmente
        }

        // (c) Extensión horizontal
        int maxLevel = type == BlockType.WATER ? 7 : 3;
        if (isSource) {
            // Fuente infinita: comprobar si tiene fuentes a ambos lados
            boolean leftSource = getBlock(x - 1, y) == type && getLevel(x - 1, y) == 0;
            boolean rightSource = getBlock(x + 1, y) == type && getLevel(x + 1, y) == 0;
            if (type == BlockType.WATER && leftSource && rightSource) {
                // Ya es fuente o convertirse en fuente
                setLevel(x, y, 0);
            }

            // Buscar huecos cercanos (preferencia por caer)
            boolean leftHole = hasHoleNearby(x - 1, y, type, 4);
            boolean rightHole = hasHoleNearby(x + 1, y, type, 4);

            if (leftHole && !rightHole) {
                spreadTo(x - 1, y, type, 1, maxLevel, tickDelay);
            } else if (rightHole && !leftHole) {
                spreadTo(x + 1, y, type, 1, maxLevel, tickDelay);
            } else {
                spreadTo(x - 1, y, type, 1, maxLevel, tickDelay);
                spreadTo(x + 1, y, type, 1, maxLevel, tickDelay);
            }
        } else if (level < maxLevel) {
            boolean leftHole = hasHoleNearby(x - 1, y, type, 4);
            boolean rightHole = hasHoleNearby(x + 1, y, type, 4);

            if (leftHole && !rightHole) {
                spreadTo(x - 1, y, type, level + 1, maxLevel, tickDelay);
            } else if (rightHole && !leftHole) {
                spreadTo(x + 1, y, type, level + 1, maxLevel, tickDelay);
            } else {
                spreadTo(x - 1, y, type, level + 1, maxLevel, tickDelay);
                spreadTo(x + 1, y, type, level + 1, maxLevel, tickDelay);
            }
        }

        // Fuente infinita de agua
        if (type == BlockType.WATER && !isSource) {
            boolean leftSource = getBlock(x - 1, y) == BlockType.WATER && getLevel(x - 1, y) == 0;
            boolean rightSource = getBlock(x + 1, y) == BlockType.WATER && getLevel(x + 1, y) == 0;
            BlockType b = getBlock(x, y - 1);
            if (leftSource && rightSource && (b.solid || b == BlockType.WATER)) {
                setLevel(x, y, 0); // convertirse en fuente
            }
        }

        // Interacciones agua+lava
        if (type == BlockType.WATER) {
            checkWaterLavaInteraction(x, y);
        } else if (type == BlockType.LAVA) {
            checkWaterLavaInteraction(x, y);
        }
    }

    /** Busca un hueco (bloque con vacío debajo) en un radio horizontal */
    private boolean hasHoleNearby(int startX, int startY, BlockType type, int radius) {
        int dir = startX < (startX & ~0xF) + 8 ? -1 : 1;
        for (int i = 0; i < radius; i++) {
            int cx = startX + dir * i;
            BlockType b = getBlock(cx, startY);
            if (!b.solid && b != type) {
                BlockType below = getBlock(cx, startY - 1);
                if (!below.solid) return true;
            }
        }
        return false;
    }

    /** Extiende el líquido a una celda */
    private void spreadTo(int x, int y, BlockType type, int level, int maxLevel, int tickDelay) {
        if (y < 0 || y >= HEIGHT) return;
        if (level > maxLevel) return;
        BlockType target = getBlock(x, y);
        if (target.solid) return;
        if (target == type && getLevel(x, y) <= level) return;

        // Interacciones agua+lava
        if (type == BlockType.WATER && target == BlockType.LAVA) {
            int lavaLevel = getLevel(x, y);
            if (lavaLevel == 0) {
                setBlock(x, y, BlockType.OBSIDIAN);
            } else {
                setBlock(x, y, BlockType.COBBLESTONE);
            }
            return;
        }
        if (type == BlockType.LAVA && target == BlockType.WATER) {
            setBlock(x, y, BlockType.STONE);
            return;
        }

        if (target.replaceable || target == BlockType.AIR) {
            setBlock(x, y, type);
            setLevel(x, y, level);
            tickQueue.schedule(currentTick + tickDelay, x, y, type);
        }
    }

    /** Comprueba interacciones agua-lava en los vecinos */
    private void checkWaterLavaInteraction(int x, int y) {
        BlockType here = getBlock(x, y);
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : dirs) {
            int nx = x + d[0], ny = y + d[1];
            BlockType neighbor = getBlock(nx, ny);
            if (here == BlockType.WATER && neighbor == BlockType.LAVA) {
                int lavaLevel = getLevel(nx, ny);
                if (lavaLevel == 0) {
                    setBlock(nx, ny, BlockType.OBSIDIAN);
                } else {
                    setBlock(nx, ny, BlockType.COBBLESTONE);
                }
                return;
            }
            if (here == BlockType.LAVA && neighbor == BlockType.WATER) {
                if (d[1] == -1) {
                    setBlock(nx, ny, BlockType.STONE);
                } else {
                    setBlock(nx, ny, BlockType.COBBLESTONE);
                }
                return;
            }
        }
    }

    /** Simula bloque con gravedad (arena, grava, etc.) */
    private void simulateFalling(int x, int y, BlockType type) {
        BlockType below = getBlock(x, y - 1);
        if (!below.solid && below != type) {
            if (below == BlockType.AIR || below == BlockType.WATER || below == BlockType.LAVA || below.replaceable) {
                // Convertir en "entidad que cae" → simplificado: mover bloque abajo
                BlockType landing = below;
                setBlock(x, y, BlockType.AIR);

                // Si cayó sobre agua, el hormigón se endurece
                if (landing == BlockType.WATER && type == BlockType.SAND) {
                    setBlock(x, y - 1, type);
                    return;
                }

                // Caer hasta encontrar suelo
                int fallY = y - 1;
                while (fallY > 0) {
                    BlockType next = getBlock(x, fallY - 1);
                    if (next.solid || next == type) break;
                    if (next == BlockType.WATER || next == BlockType.LAVA) break;
                    fallY--;
                }
                setBlock(x, fallY, type);
                notifyNeighbors(x, fallY, type);

                // Notificar al bloque de arriba por si también cae
                BlockType above = getBlock(x, y + 1);
                if (above.fallable) {
                    tickQueue.schedule(currentTick + 2, x, y + 1, above);
                }
            }
        }
    }

    /** Genera un chunk con terreno procedural */
    private void generateChunk(Chunk c) {
        if (c.generated) return;
        c.generated = true;

        Random chunkRng = new Random(seed ^ (c.chunkX * 0x5DEECE66DL));
        int baseX = c.chunkX * 16;

        for (int lx = 0; lx < 16; lx++) {
            int worldX = baseX + lx;
            // Terrain height con ruido simplex-like
            int height = getTerrainHeight(worldX, chunkRng);

            for (int y = 0; y < HEIGHT; y++) {
                BlockType block;
                if (y == 0) {
                    block = BlockType.BEDROCK;
                } else if (y < height - 4) {
                    block = BlockType.STONE;
                    // Minerales
                    if (y < 16 && chunkRng.nextInt(200) == 0) block = BlockType.DIAMOND_ORE;
                    else if (y < 32 && chunkRng.nextInt(100) == 0) block = BlockType.GOLD_ORE;
                    else if (y < 64 && chunkRng.nextInt(80) == 0) block = BlockType.IRON_ORE;
                    else if (y < 80 && chunkRng.nextInt(60) == 0) block = BlockType.COAL_ORE;
                } else if (y < height) {
                    block = BlockType.DIRT;
                } else if (y == height) {
                    if (height < SEA_LEVEL - 2) {
                        block = BlockType.SAND;
                    } else {
                        block = BlockType.GRASS;
                    }
                } else if (y <= SEA_LEVEL && y > height) {
                    block = BlockType.WATER;
                    c.setLevel(lx, y, 0); // fuente
                } else {
                    block = BlockType.AIR;
                }
                c.setBlock(lx, y, block);
            }

            // Árboles ocasionales
            if (height >= SEA_LEVEL && chunkRng.nextInt(20) == 0) {
                int treeHeight = 4 + chunkRng.nextInt(3);
                for (int ty = 1; ty <= treeHeight; ty++) {
                    if (height + ty < HEIGHT) c.setBlock(lx, height + ty, BlockType.OAK_LOG);
                }
                // Copa
                for (int dy = treeHeight - 1; dy <= treeHeight + 2; dy++) {
                    for (int dx = -2; dx <= 2; dx++) {
                        int tx = lx + dx, ty = height + dy;
                        if (tx >= 0 && tx < 16 && ty < HEIGHT && Math.abs(dx) + Math.abs(dy - treeHeight) < 4) {
                            if (c.getBlock(tx, ty) == BlockType.AIR) {
                                c.setBlock(tx, ty, BlockType.OAK_LEAVES);
                            }
                        }
                    }
                }
            }
        }
    }

    /** Genera la altura del terreno usando una función pseudo-aleatoria suave */
    private int getTerrainHeight(int worldX, Random rng) {
        // Combinación de senos para terreno variado pero suave
        double h = 64;
        h += Math.sin(worldX * 0.01 + seed * 0.001) * 15;
        h += Math.sin(worldX * 0.05 + seed * 0.003) * 6;
        h += Math.sin(worldX * 0.1 + seed * 0.007) * 3;

        // Cuevas simples
        // (se hacen al generar los bloques)

        return (int) Math.max(10, Math.min(HEIGHT - 10, h));
    }

    /** Tick del mundo: procesar cola de ticks programados */
    public void tick() {
        currentTick++;
        processTicks(2000); // máximo 2000 actualizaciones por tick
    }
}
