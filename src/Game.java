import static java.lang.System.out;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferStrategy;

import java.lang.Math;

public class Game extends Canvas implements Runnable {
    private int line = 0;
    private int WIDTH = 0;
    private int HEIGHT = 0;
    private int size = 30 * 30;
    private int tileSize = 15;
    private int pLOC = 0;
    private boolean makingMaze;
    private int spawnLocation = 0;
    private int originalSpawn = 0;
    private int[] grid = new int[size];
    private int[] goombaFacing = new int[size];
    private int[] coverableObjects = new int[size];
    private Thread thread;
    private boolean running = false;
    private Handler handler;
    private Mouse mouse = new Mouse();
    private Keyboard keyboard = new Keyboard();

    public Game() {
        while (line != Math.sqrt(size)) {
            line++;
        }
        WIDTH = tileSize * line;
        HEIGHT = tileSize * line;
        new Window(WIDTH, HEIGHT, "MazeRunner", this);

        handler = new Handler();
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
        this.addKeyListener(keyboard);

        int f = 0;
        int d = 0;
        int g = 1;
        int h = 0;
        while (d != size) {
            handler.addObject(new TILE(0 + (f * tileSize), 0 + (h * tileSize), ID.TILE));
            handler.addObject(new COLUMN(0 + (f * tileSize), 0 + (h * tileSize), ID.COLUMN));
            handler.addObject(new ROW(0 + (f * tileSize), 0 + (h * tileSize), ID.ROW));
            f++;
            d++;
            g++;
            if (g == line + 1) {
                g = 1;
                f = 0;
                h++;
            }
        }
        mouse.setSizes(tileSize, line);
        begin();
    }

    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop() {
        try {
            thread.join();
            running = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int prevFPS = 0;
        int fps = 0;
        int x = 0;
        long timer = System.currentTimeMillis();
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                fps++;
                tick();
                delta--;
            }
            if (running) {
                render();
                x++;
                if (x >= prevFPS / 6 && !makingMaze) {
                    updateEnemies();
                    updateCoverableObjects();
                    x = 0;
                }
            }
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                prevFPS = fps;
                fps = 0;
            }
        }
        stop();
    }

    public void tick() {
        handler.tick();
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.green);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        handler.render(g);

        g.dispose();
        bs.show();
    }

    public static void main(String[] args) throws Exception {
        new Game();
    }

    // Zone 1: Borders
    // Zone 2: Spawns
    // Zone 3: Checkpoints
    // Zone 4: Goomba Traps
    // Zone 5: Timer Traps
    // Zone 6: Goals
    // Zone 7: Begin Playing
    //
    // Event 0: Clicked
    // Event 1: Dragging
    // Event 2: Dragged
    //
    // Grid 0: Tile
    // Grid 1: Border
    // Grid 2: Spawn
    // Grid 3: Checkpoint
    // Grid 4: Goomba Trap
    // Grid 5: Timer Traps
    // Grid 6: Goal
    // Grid 7: Player

    public void begin() {
        int keyboardClicks = 0, mouseClicks = 0, event = 0, key = 0, zone = 1, objectLoc, spawns = 0;
        boolean playing = true;
        makingMaze = true;
        while (makingMaze) {
            while (mouseClicks == mouse.getClicks() && keyboardClicks == keyboard.getClicks()) {
                out.print("");
            }
            if (mouseClicks != mouse.getClicks()) {
                mouseClicks = mouse.getClicks();
                objectLoc = mouse.getObjectLoc();
                event = mouse.getEvent();
                if (event == 0) {
                    if (zone == 1) {
                        if (grid[objectLoc] == 0) {
                            grid[objectLoc] = 1;
                            handler.replaceObject(objectLoc * 3, new BORDER((objectLoc % line) * tileSize,
                                    (objectLoc / line) * tileSize, ID.BORDER));
                        } else if (grid[objectLoc] == 1) {
                            grid[objectLoc] = 0;
                            handler.replaceObject(objectLoc * 3,
                                    new TILE((objectLoc % line) * tileSize, (objectLoc / line) * tileSize, ID.TILE));
                        }
                    } else if (zone == 2) {
                        if (grid[objectLoc] == 0 && spawns == 0) {
                            spawnLocation = objectLoc;
                            originalSpawn = objectLoc;
                            spawns++;
                            grid[objectLoc] = 2;
                            coverableObjects[objectLoc] = 2;
                            handler.replaceObject(objectLoc * 3,
                                    new SPAWN((objectLoc % line) * tileSize, (objectLoc / line) * tileSize, ID.SPAWN));
                        } else if (grid[objectLoc] == 2) {
                            spawns--;
                            grid[objectLoc] = 0;
                            coverableObjects[objectLoc] = 0;
                            handler.replaceObject(objectLoc * 3,
                                    new TILE((objectLoc % line) * tileSize, (objectLoc / line) * tileSize, ID.TILE));
                        }
                    } else if (zone == 3) {
                        if (grid[objectLoc] == 0) {
                            grid[objectLoc] = 3;
                            coverableObjects[objectLoc] = 3;
                            handler.replaceObject(objectLoc * 3, new CHECKPOINT((objectLoc % line) * tileSize,
                                    (objectLoc / line) * tileSize, ID.CHECKPOINT));
                        } else if (grid[objectLoc] == 3) {
                            grid[objectLoc] = 0;
                            coverableObjects[objectLoc] = 0;
                            handler.replaceObject(objectLoc * 3,
                                    new TILE((objectLoc % line) * tileSize, (objectLoc / line) * tileSize, ID.TILE));
                        }
                    } else if (zone == 4) {
                        if (grid[objectLoc] == 0) {
                            grid[objectLoc] = 4;
                            goombaFacing[objectLoc] = 1;
                            handler.replaceObject(objectLoc * 3, new GOOMBA((objectLoc % line) * tileSize,
                                    (objectLoc / line) * tileSize, ID.GOOMBA));
                        } else if (grid[objectLoc] == 4) {
                            if (goombaFacing[objectLoc] == 4) {
                                grid[objectLoc] = 0;
                                handler.replaceObject(objectLoc * 3, new TILE((objectLoc % line) * tileSize,
                                        (objectLoc / line) * tileSize, ID.TILE));
                            } else {
                                out.println("Goomba turned");
                                goombaFacing[objectLoc]++;
                            }
                        }
                    } else if (zone == 6) {
                        if (grid[objectLoc] == 0) {
                            grid[objectLoc] = 6;
                            handler.replaceObject(objectLoc * 3,
                                    new GOAL((objectLoc % line) * tileSize, (objectLoc / line) * tileSize, ID.GOAL));
                        } else if (grid[objectLoc] == 6) {
                            grid[objectLoc] = 0;
                            handler.replaceObject(objectLoc * 3,
                                    new TILE((objectLoc % line) * tileSize, (objectLoc / line) * tileSize, ID.TILE));
                        }
                    }
                } else if (event == 2) {
                    int tilesPerColumn = (mouse.getDragLoc(4) / tileSize) - (mouse.getDragLoc(2) / tileSize);
                    int tilesInRow = (mouse.getDragLoc(3) / tileSize) - (mouse.getDragLoc(1) / tileSize);
                    int rateOfY = 1;
                    int rateOfX = 1;
                    int initialY = mouse.getDragLoc(2) / tileSize;
                    int initialX = mouse.getDragLoc(1) / tileSize;
                    int currentY = 0;
                    int currentX = 0;
                    if (tilesPerColumn < 0)
                        rateOfY = -1;
                    if (tilesInRow < 0)
                        rateOfX = -1;
                    tilesPerColumn += rateOfY;
                    tilesInRow += rateOfX;
                    for (int a = 0; a != tilesInRow; a += rateOfX) {
                        currentX = initialX + a;
                        for (int i = 0; i != tilesPerColumn; i += rateOfY) {
                            currentY = initialY + i;
                            if (zone == 1) {
                                if (grid[(currentY * line) + currentX] == 0) {
                                    grid[(currentY * line) + currentX] = 1;
                                    handler.replaceObject(((currentY * line) + currentX) * 3,
                                            new BORDER(currentX * tileSize, currentY * tileSize, ID.BORDER));
                                } else if (grid[(currentY * line) + currentX] == 1) {
                                    grid[(currentY * line) + currentX] = 0;
                                    handler.replaceObject(((currentY * line) + currentX) * 3,
                                            new TILE(currentX * tileSize, currentY * tileSize, ID.TILE));
                                }
                            } else if (zone == 3) {
                                if (grid[(currentY * line) + currentX] == 0) {
                                    grid[(currentY * line) + currentX] = 3;
                                    handler.replaceObject(((currentY * line) + currentX) * 3,
                                            new CHECKPOINT(currentX * tileSize, currentY * tileSize, ID.CHECKPOINT));
                                } else if (grid[(currentY * line) + currentX] == 3) {
                                    grid[(currentY * line) + currentX] = 0;
                                    handler.replaceObject(((currentY * line) + currentX) * 3,
                                            new TILE(currentX * tileSize, currentY * tileSize, ID.TILE));
                                }
                            } else if (zone == 4) {
                                if (grid[(currentY * line) + currentX] == 0) {
                                    grid[(currentY * line) + currentX] = 4;
                                    handler.replaceObject(((currentY * line) + currentX) * 3,
                                            new GOOMBA(currentX * tileSize, currentY * tileSize, ID.GOOMBA));
                                } else if (grid[(currentY * line) + currentX] == 4) {
                                    grid[(currentY * line) + currentX] = 0;
                                    handler.replaceObject(((currentY * line) + currentX) * 3,
                                            new TILE(currentX * tileSize, currentY * tileSize, ID.TILE));
                                }
                            } else if (zone == 6) {
                                if (grid[(currentY * line) + currentX] == 0) {
                                    grid[(currentY * line) + currentX] = 6;
                                    handler.replaceObject(((currentY * line) + currentX) * 3,
                                            new GOAL(currentX * tileSize, currentY * tileSize, ID.GOAL));
                                } else if (grid[(currentY * line) + currentX] == 6) {
                                    grid[(currentY * line) + currentX] = 0;
                                    handler.replaceObject(((currentY * line) + currentX) * 3,
                                            new TILE(currentX * tileSize, currentY * tileSize, ID.TILE));
                                }
                            }
                        }
                    }
                }
            } else if (keyboardClicks != keyboard.getClicks()) {
                keyboardClicks = keyboard.getClicks();
                key = keyboard.getKey();
                if (key == 10) {
                    zone++;
                    System.out.println("Current Zone: " + zone);
                }
            }
            if (zone == 7)
                makingMaze = false;
        }
        respawn();
        keyboardClicks = keyboard.getClicks();
        while (playing) {
            while (keyboardClicks == keyboard.getClicks()) {
                out.print("");
            }
            key = keyboard.getKey();
            keyboardClicks = keyboard.getClicks();
            if (key == 87) { // W
                if (pLOC - line > -1 && grid[pLOC - line] != 1 && grid[pLOC - line] != 4) {
                    if (grid[pLOC - line] == 2 || grid[pLOC - line] == 3) {
                        spawnLocation = pLOC - line;
                    } else if (grid[pLOC - line] == 6) {
                        out.println("Victory!!!");
                        spawnLocation = originalSpawn;
                        respawn();
                    }
                    movePlayer(pLOC, pLOC - line);

                } else if (pLOC - line > -1 && grid[pLOC - line] == 4) {
                    respawn();
                }
            } else if (key == 65) { // A
                if (pLOC % line != 0 && grid[pLOC - 1] != 1 && grid[pLOC - 1] != 4) {
                    if (grid[pLOC - 1] == 2 || grid[pLOC - 1] == 3) {
                        spawnLocation = pLOC - 1;
                    } else if (grid[pLOC - 1] == 6) {
                        out.println("Victory!!!");
                        spawnLocation = originalSpawn;
                        respawn();
                    }
                    movePlayer(pLOC, pLOC - 1);

                } else if (pLOC % line != 0 && grid[pLOC - 1] == 4) {
                    respawn();
                }
            } else if (key == 83) { // S
                if (pLOC + line < size && grid[pLOC + line] != 1 && grid[pLOC + line] != 4) {
                    if (grid[pLOC + line] == 2 || grid[pLOC + line] == 3) {
                        spawnLocation = pLOC + line;
                    } else if (grid[pLOC + line] == 6) {
                        out.println("Victory!!!");
                        spawnLocation = originalSpawn;
                        respawn();
                    }
                    movePlayer(pLOC, pLOC + line);

                } else if (pLOC + line > -1 && grid[pLOC + line] == 4) {
                    respawn();
                }
            } else if (key == 68) { // D
                if ((pLOC - (line - 1)) % line != 0 && grid[pLOC + 1] != 1 && grid[pLOC + 1] != 4) {
                    if (grid[pLOC + 1] == 2 || grid[pLOC + 1] == 3) {
                        spawnLocation = pLOC + 1;
                    } else if (grid[pLOC + 1] == 6) {
                        out.println("Victory!!!");
                        spawnLocation = originalSpawn;
                        respawn();
                    }
                    movePlayer(pLOC, pLOC + 1);

                } else if ((pLOC - (line - 1)) % line != 0 && grid[pLOC + 1] == 4) {
                    respawn();
                }
            }
        }
    }

    public void movePlayer(int prevLoc, int currentLoc) {
        grid[prevLoc] = 0;
        grid[currentLoc] = 7;
        pLOC = currentLoc;
        handler.replaceObject(prevLoc * 3, new TILE((prevLoc % line) * tileSize, (prevLoc / line) * tileSize, ID.TILE));
        handler.replaceObject(currentLoc * 3,
                new PLAYER((currentLoc % line) * tileSize, (currentLoc / line) * tileSize, ID.PLAYER));
    }

    public void respawn() {
        grid[pLOC] = 0;
        handler.replaceObject(pLOC * 3, new TILE((pLOC % line) * tileSize, (pLOC / line) * tileSize, ID.TILE));
        pLOC = spawnLocation;
        grid[pLOC] = 7;
        handler.replaceObject(pLOC * 3, new PLAYER((pLOC % line) * tileSize, (pLOC / line) * tileSize, ID.PLAYER));
    }

    public void updateCoverableObjects() {
        for (int i = 0; i != size; i++) {
            if (coverableObjects[i] != 0) {
                if (coverableObjects[i] == 2 && grid[i] == 0) {
                    handler.replaceObject(i * 3, new SPAWN((i % line) * tileSize, (i / line) * tileSize, ID.SPAWN));
                } else if (coverableObjects[i] == 3 && grid[i] == 0) {
                    handler.replaceObject(i * 3,
                            new CHECKPOINT((i % line) * tileSize, (i / line) * tileSize, ID.CHECKPOINT));
                }
            }
        }
    }

    public void updateEnemies() {
        for (int i = 0; i != size; i++) {
            if (grid[i] == 4) {
                if (goombaFacing[i] == 1) { // up
                    if (i - line > -1 && grid[i - line] == 0) {
                        handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
                        grid[i] = 0;
                        grid[i - line] = 40;
                        goombaFacing[i] = 0;
                        goombaFacing[i - line] = 1;
                        handler.replaceObject((i - line) * 3,
                                new GOOMBA(((i - line) % line) * tileSize, ((i - line) / line) * tileSize, ID.GOOMBA));
                    } else if (i - line > -1 && grid[i - line] == 7) {
                        respawn();
                        handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
                        grid[i] = 0;
                        grid[i - line] = 40;
                        goombaFacing[i] = 0;
                        goombaFacing[i - line] = 1;
                        handler.replaceObject((i - line) * 3,
                                new GOOMBA(((i - line) % line) * tileSize, ((i - line) / line) * tileSize, ID.GOOMBA));

                    } else {
                        goombaFacing[i] = 3;
                    }
                }
                if (goombaFacing[i] == 2) { // left
                    if (i % line != 0 && grid[i - 1] == 0) {
                        handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
                        grid[i] = 0;
                        grid[i - 1] = 40;
                        goombaFacing[i] = 0;
                        goombaFacing[i - 1] = 2;
                        handler.replaceObject((i - 1) * 3,
                                new GOOMBA(((i - 1) % line) * tileSize, ((i - 1) / line) * tileSize, ID.GOOMBA));
                    } else if (i % line != 0 && grid[i - 1] == 7) {
                        respawn();
                        handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
                        grid[i] = 0;
                        grid[i - 1] = 40;
                        goombaFacing[i] = 0;
                        goombaFacing[i - 1] = 2;
                        handler.replaceObject((i - 1) * 3,
                                new GOOMBA(((i - 1) % line) * tileSize, ((i - 1) / line) * tileSize, ID.GOOMBA));

                    } else {
                        goombaFacing[i] = 4;
                    }
                } else if (goombaFacing[i] == 3) { // down
                    if (i + line < size && grid[i + line] == 0) {
                        handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
                        grid[i] = 0;
                        grid[i + line] = 40;
                        goombaFacing[i] = 0;
                        goombaFacing[i + line] = 3;
                        handler.replaceObject((i + line) * 3,
                                new GOOMBA(((i + line) % line) * tileSize, ((i + line) / line) * tileSize, ID.GOOMBA));
                    } else if (i + line > -1 && grid[i + line] == 7) {
                        respawn();
                        handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
                        grid[i] = 0;
                        grid[i + line] = 40;
                        goombaFacing[i] = 0;
                        goombaFacing[i + line] = 3;
                        handler.replaceObject((i + line) * 3,
                                new GOOMBA(((i + line) % line) * tileSize, ((i + line) / line) * tileSize, ID.GOOMBA));

                    } else {
                        goombaFacing[i] = 1;
                    }
                }
                if (goombaFacing[i] == 4) { // right
                    if ((i - (line - 1)) % line != 0 && grid[i + 1] == 0) {
                        handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
                        grid[i] = 0;
                        grid[i + 1] = 40;
                        goombaFacing[i] = 0;
                        goombaFacing[i + 1] = 4;
                        handler.replaceObject((i + 1) * 3,
                                new GOOMBA(((i + 1) % line) * tileSize, ((i + 1) / line) * tileSize, ID.GOOMBA));
                    } else if (i % line != 0 && grid[i + 1] == 7) {
                        respawn();
                        handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
                        grid[i] = 0;
                        grid[i + 1] = 40;
                        goombaFacing[i] = 0;
                        goombaFacing[i + 1] = 4;
                        handler.replaceObject((i + 1) * 3,
                                new GOOMBA(((i + 1) % line) * tileSize, ((i + 1) / line) * tileSize, ID.GOOMBA));

                    } else {
                        goombaFacing[i] = 2;
                    }
                }
            }
        }
        for (int i = 0; i != size; i++) {
            if (grid[i] == 40)
                grid[i] /= 10;
        }
    }
}
