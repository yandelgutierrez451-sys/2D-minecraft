# 2D Minecraft

Juego tipo Minecraft en 2D (vista lateral, estilo Terraria) programado en **Java 17** usando solo el JDK (Swing/Java2D). Sin dependencias externas, sin imágenes externas: todas las texturas se generan por código.

## Requisitos

- **Java 17** (OpenJDK)
- En Chromebooks: activar Linux (Crostini) en Configuración → Desarrolladores
- Funciona con touchpad y teclado

## Compilación y ejecución

```bash
chmod +x run.sh
./run.sh
```

O manualmente:

```bash
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

## Controles

| Tecla | Acción |
|-------|--------|
| **W/A/S/D** o flechas | Mover / Saltar / Agacharse |
| **Espacio** | Saltar (mantener para salto variable) |
| **Shift** | Agacharse / Hundirse en agua |
| **Clic izquierdo / J** | Romper bloque (mantener) |
| **Clic derecho / K** | Colocar bloque / Usar |
| **1-9** | Seleccionar ranura de hotbar |
| **E** | Inventario |
| **Q** | Soltar ítem |
| **F** | Mano secundaria |
| **F3** | Debug |
| **Esc** | Pausa |

## Sistemas implementados

### A. Núcleo: Registro y Cola de Ticks
- **Registro único** con `BlockType` (50+ bloques) e `ItemType` (200+ ítems)
- **Estado de bloque** con `id` y `data` por celda (nivel de líquido, potencia, etc.)
- **Cola de ticks programados** (`ScheduledTickQueue`): sin recorrido total, solo bloques inestables
- **Anti-desbordes**: máximo 2000 actualizaciones por tick, BFS iterativa

### B. Agua
- Nivel 0-8 (fuente a fluyente), animación semitransparente
- Flujo cada 5 ticks: caída → extensión horizontal → preferencia por huecos
- **Fuente infinita**: agua con fuentes a ambos lados se regenera
- Cubo vacío recoge fuentes; cubo con agua coloca fuentes
- Interacciones: destruye antorchas, flores; hidrata cultivos
- **Física del jugador**: gravedad reducida, nado, empuje por flujo
- **Aire y ahogo**: 300 ticks de aire, burbujas, daño por asfixia
- Tinte azul al sumergir, partículas de burbujas

### C. Lava
- Mismo modelo de niveles que el agua, pero tick cada 30 ticks
- Se extiende solo 3 bloques (mundo principal), 7 en el Nether
- **No** crea fuentes infinitas
- **Interacción agua+lava**: obsidiana, adoquín, piedra
- Daño: 4 dps + fuego 15s; pantalla naranja al sumergir

### D. Arena y bloques con gravedad
- Arena, grava, yunque: caen cuando no tienen apoyo
- Entidad `FallingBlock` simplificada: cae hasta encontrar suelo
- Cascadas: torre de arena sin base cae entera en cadena
- No se programan al generar el mundo (solo al ser tocados)

### E. Romper bloques y Auto-Pickup
- Tiempo = dureza × factor herramienta / velocidad
- Grietas en 10 etapas visibles
- **Auto-pickup**: drops van directo al inventario (hotbar primero)
- Mensaje `+1 Piedra` flotante al recoger
- Desgaste de herramientas con barra visual

### F. Inventario y Equipamiento
- 36 ranuras (0-8 = hotbar) + 4 armadura + mano secundaria
- **Materiales**: cuero, hierro, oro, diamante, netherita
- **Defensa por pieza**: casco, peto, pantalones, botas
- **Fórmula de daño**: reducción basada en defensa + dureza
- Durabilidad visual en cada ítem

### G. Crafteo (1000+ recetas generadas por código)
- **Con forma** (shaped): patrón + leyenda, con trim y espejo
- **Sin forma** (shapeless): multiconjunto de ingredientes
- **Generadores automáticos**:
  - 6 materiales × 4 herramientas = 24 recetas de herramientas
  - 5 materiales × 4 piezas = 20 recetas de armaduras
  - 5 maderas × 11 bloques = 55+ recetas de madera
  - 16 colores × 11 variantes = 176 recetas de color
  - Piedras, escaleras, losas, muros, compactación, comida...
- **Libro de recetas** con búsqueda y filtros
- **Estaciones**: horno, alto horno, yunque, mesa de crafteo...

### H. Redstone
- Potencia 0-15, fuerte vs débil
- Polvo, antorcha (inversor), repetidor, comparador, pistón
- BFS iterativa para propagación
- Lámpara, puertas, dispensador, tolva, TNT

### I. Construcción y Colocación
- Clic derecho o K coloca bloque apuntado
- Fantasma verde/rojo de previsualización
- Orientación por cara del cursor (escaleras, losas)
- Colocación rápida al mantener clic
- **Modo puente** con Shift para construir en el borde

### J. Salto y Construcción en el Aire
- Gravedad 32 bloques/s², salto 1.25 bloques
- **Coyote time**: 100ms después del borde
- **Jump buffer**: 100ms antes de aterrizar
- **Salto variable**: soltar Espacio = salto corto
- **Pilar (pillar jumping)**: colocar bloque bajo los pies al saltar
- Daño de caída: 0.5 corazón por bloque sobre 3
- Colisiones AABB con cajas parciales (losas, escaleras)

### K. Rendimiento
- Caché de chunks, solo dibuja lo visible
- Cola de ticks con límite por tick
- Opción de calidad baja para Chromebooks
- Guardado por región con GZIP

## Arquitectura

```
src/
├── Main.java           # Punto de entrada
├── Game.java           # Bucle, render, físicas, entrada
├── BlockType.java      # Registro de 50+ tipos de bloque
├── ItemType.java       # Registro de 200+ tipos de ítem
├── ItemStack.java      # Pila de ítems
├── Inventory.java      # Inventario del jugador
├── TextureGen.java     # Generación de texturas 16×16 por código
├── World.java          # Mundo, chunks, simulación
├── Chunk.java          # Chunk 16×256
├── ScheduledTickQueue.java  # Cola de ticks programados
└── CraftingManager.java     # 1000+ recetas generadas
```

## Modo Creativo

Activado por defecto: recursos infinitos, vuelo, rotura instantánea.

## Notas de implementación

- Todo definido por **tablas de datos y registros**, nunca if/else por objeto
- Sin recursión en simulaciones (BFS iterativa para evitar StackOverflow)
- Texturas deterministas (semilla por tipo), sin suavizado
- Mundo infinito en horizontal, seed configurable
