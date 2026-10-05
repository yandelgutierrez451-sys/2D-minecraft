/**
 * Chunk de 16×256 bloques. Almacena el id (short) y data (short) de cada celda.
 * Los datos guardan nivel de líquido, orientación, etapa de cultivo, potencia de redstone, etc.
 */
public class Chunk {
    public static final int W = 16;
    public static final int H = 256;
    public static final int AIR_ID = 0;

    public final int chunkX;
    public final short[] blocks;  // id del bloque (índice en BlockType.values())
    public final short[] data;    // datos extra por celda

    public Chunk(int chunkX) {
        this.chunkX = chunkX;
        blocks = new short[W * H];
        data = new short[W * H];
    }

    /** Índice lineal de la celda (x local, y) */
    public static int idx(int x, int y) {
        return y * W + x;
    }

    /** Obtiene el tipo de bloque en coords locales */
    public BlockType getBlock(int x, int y) {
        if (x < 0 || x >= W || y < 0 || y >= H) return BlockType.AIR;
        int id = blocks[idx(x, y)];
        if (id < 0 || id >= BlockType.values().length) return BlockType.AIR;
        return BlockType.values()[id];
    }

    /** Obtiene el dato de la celda */
    public short getData(int x, int y) {
        if (x < 0 || x >= W || y < 0 || y >= H) return 0;
        return data[idx(x, y)];
    }

    /** Coloca un bloque */
    public void setBlock(int x, int y, BlockType type) {
        if (x < 0 || x >= W || y < 0 || y >= H) return;
        blocks[idx(x, y)] = (short) type.ordinal();
    }

    /** Coloca un bloque con dato */
    public void setBlock(int x, int y, BlockType type, short d) {
        if (x < 0 || x >= W || y < 0 || y >= H) return;
        blocks[idx(x, y)] = (short) type.ordinal();
        data[idx(x, y)] = d;
    }

    /** Establece el dato */
    public void setData(int x, int y, short d) {
        if (x < 0 || x >= W || y < 0 || y >= H) return;
        data[idx(x, y)] = d;
    }

    /** Devuelve el nivel de líquido (0-8) de la celda */
    public int getLevel(int x, int y) {
        return getData(x, y) & 0xF;
    }

    /** Devuelve la potencia de redstone (0-15) de la celda */
    public int getPower(int x, int y) {
        return (getData(x, y) >> 4) & 0xF;
    }

    /** Establece el nivel de líquido */
    public void setLevel(int x, int y, int level) {
        short d = getData(x, y);
        d = (short) ((d & 0xFFF0) | (level & 0xF));
        setData(x, y, d);
    }

    /** Establece la potencia de redstone */
    public void setPower(int x, int y, int power) {
        short d = getData(x, y);
        d = (short) ((d & 0xFF0F) | ((power & 0xF) << 4));
        setData(x, y, d);
    }

    /** True si el chunk ya fue generado (se marca al generarlo) */
    public boolean generated = false;
}
