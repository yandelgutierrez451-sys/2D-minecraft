import java.util.*;

/**
 * Cola de prioridad para ticks programados.
 * Sin duplicados por (celda, tipo): si se reprograma, se actualiza el tick.
 * Usa BFS iterativa (nunca recursión) para evitar StackOverflowError.
 */
public class ScheduledTickQueue {

    public static class TickEntry {
        public int tick;
        public int x, y;
        public BlockType blockType;

        public TickEntry(int tick, int x, int y, BlockType type) {
            this.tick = tick;
            this.x = x;
            this.y = y;
            this.blockType = type;
        }
    }

    // Cola ordenada por tick
    private final PriorityQueue<TickEntry> queue = new PriorityQueue<>(
        (a, b) -> {
            if (a.tick != b.tick) return Integer.compare(a.tick, b.tick);
            if (a.y != b.y) return Integer.compare(a.y, b.y);
            return Integer.compare(a.x, b.x);
        }
    );

    // Set para evitar duplicados: clave = "x,y,typeId"
    private final Set<Long> pending = new HashSet<>();

    private static long key(int x, int y, BlockType type) {
        return ((long)(x & 0xFFFFFFF) << 36) | ((long)(y & 0xFFF) << 24) | (type.ordinal() & 0xFFFFFF);
    }

    /** Programa un tick. Si ya hay uno para esa celda+tipo, se actualiza al menor tick. */
    public void schedule(int tick, int x, int y, BlockType type) {
        long k = key(x, y, type);
        if (pending.contains(k)) {
            // Ya está programado; no duplicar (el anterior se ejecutará)
            return;
        }
        pending.add(k);
        queue.add(new TickEntry(tick, x, y, type));
    }

    /** Devuelve y elimina la siguiente entrada cuyo tick ≤ maxTick, o null */
    public TickEntry poll(int currentTick) {
        while (!queue.isEmpty()) {
            TickEntry top = queue.peek();
            if (top.tick > currentTick) return null;
            queue.poll();
            long k = key(top.x, top.y, top.blockType);
            if (pending.contains(k)) {
                pending.remove(k);
                return top;
            }
            // Entrada obsoleta (ya fue procesada o reemplazada)
        }
        return null;
    }

    /** Limpia la cola */
    public void clear() {
        queue.clear();
        pending.clear();
    }

    public int size() {
        return queue.size();
    }
}
