import static java.lang.System.out;
import java.util.Scanner;

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
    private int lives = 3;
    private double coins = 0;
    private int level = 0;
    private boolean preMadeMap;
    private int[] grid = new int[size];
    private int[] goombaFacing = new int[size];
    private int[] coverableObjects = new int[size];
    private int[] doorStats = new int[size];
    private int[] teleporterStats = new int[size];
    private int[] collectedCheckpoints = new int[size];
    private double collectedCoins = 0;
    private boolean updatingEnemies = false;
    private Thread thread;
    private boolean running = false;
    private Handler handler;
    private Mouse mouse = new Mouse();
    private Keyboard keyboard = new Keyboard();
    private Stats stats = new Stats();
    private Scanner input = new Scanner(System.in);

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
            if (running) 
                render();
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                prevFPS = fps;
                updateEnemies();
                updateCoverableObjects();
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
    // Zone -7: 2nd Teleporter
    // Zone -6: Door Key
    // Zone 1: Borders
    // Zone 2: Spawns
    // Zone 3: Checkpoints
    // Zone 4: Goomba Traps
    // Zone 5: Timer Traps
    // Zone 6: Door
    // Zone 7: 1st Teleporter
    // Zone 8: Coins
    // Zone 10: Goals
    // Zone 11: Begin Playing
    //
    // Event 0: Clicked
    // Event 1: Dragging
    // Event 2: Dragged
    //
    // Grid 0: Tile light gray
    // Grid 1: Border crimson
    // Grid 2: Spawn green
    // Grid 3: Checkpoint yellow
    // Grid 4: Goomba Trap hazel
    // Grid 5: Timer Traps NA
    // Grid 6: Door gray
    // Grid 7: Key light yellow
    // Grid 8: Teleporter dark purple
    // Grid 9: coin orange
    // Grid 10: Goal purple
    // Grid 11: Player black
    //
    // Ideas:
    // Key + Locked Door
    // Coins + map completion percentage
    // Teleporter
    // Lives

    public void begin() {
        level = 1;
        preMadeMap = false;
        System.out.println("Custom Map(1) or Pre-Made Levels(2)");
        System.out.print("> ");
        int mode = input.nextInt();
        if (mode == 2) {
            preMadeMap = true;
            System.out.println("Pick Start Level");
            System.out.print("> ");
            level = input.nextInt();
        }
        int keyboardClicks = 0, mouseClicks = 0, event = 0, key = 0, zone = 1, objectLoc, spawns = 0,
                doors = 0, keys = 0, teleporters = 0;
        boolean playing = true;
        if (!preMadeMap)
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
                            doors++;
                            zone = -6;
                            doorStats[objectLoc] = doors;
                            handler.replaceObject(objectLoc * 3, new DOOR((objectLoc % line) * tileSize,
                                    (objectLoc / line) * tileSize, ID.DOOR));
                        }
                    } else if (zone == -6) {
                        if (grid[objectLoc] == 0 && keys < doors) {
                            grid[objectLoc] = 7;
                            zone = 6;
                            keys++;
                            doorStats[objectLoc] = doors;
                            handler.replaceObject(objectLoc * 3, new KEY((objectLoc % line) * tileSize,
                                    (objectLoc / line) * tileSize, ID.KEY));
                        } else if (grid[objectLoc] == 7) {
                            keys--;
                            for (int i = 0; i != size; i++) {
                                if (doorStats[i] == doorStats[objectLoc] && i != objectLoc) {
                                    handler.replaceObject(objectLoc * 3, new TILE((objectLoc % line) * tileSize,
                                            (objectLoc / line) * tileSize, ID.TILE));
                                    handler.replaceObject(i * 3, new TILE((i % line) * tileSize,
                                            (i / line) * tileSize, ID.TILE));
                                    doors--;
                                    doorStats[i] = 0;
                                    grid[i] = 0;
                                    doorStats[objectLoc] = 0;
                                    grid[objectLoc] = 0;
                                    zone = 6;
                                    break;
                                }
                            }
                        }
                    } else if (zone == 7) {
                        if (grid[objectLoc] == 0) {
                            grid[objectLoc] = 8;
                            teleporters++;
                            coverableObjects[objectLoc] = 8;
                            zone = -7;
                            teleporterStats[objectLoc] = teleporters;
                            handler.replaceObject(objectLoc * 3, new TELEPORTER((objectLoc % line) * tileSize,
                                    (objectLoc / line) * tileSize, ID.TELEPORTER));
                        }
                    } else if (zone == -7) {
                        if (grid[objectLoc] == 0) {
                            grid[objectLoc] = 8;
                            zone = 7;
                            coverableObjects[objectLoc] = 8;
                            teleporterStats[objectLoc] = teleporters;
                            handler.replaceObject(objectLoc * 3, new TELEPORTER((objectLoc % line) * tileSize,
                                    (objectLoc / line) * tileSize, ID.TELEPORTER));
                        } else if (grid[objectLoc] == 8) {
                            teleporters--;
                            for (int i = 0; i != size; i++) {
                                if (teleporterStats[i] == teleporterStats[objectLoc] && i != objectLoc) {
                                    handler.replaceObject(objectLoc * 3, new TILE((objectLoc % line) * tileSize,
                                            (objectLoc / line) * tileSize, ID.TILE));
                                    handler.replaceObject(i * 3, new TILE((i % line) * tileSize,
                                            (i / line) * tileSize, ID.TILE));
                                    teleporterStats[i] = 0;
                                    coverableObjects[i] = 0;
                                    grid[i] = 0;
                                    teleporterStats[objectLoc] = 0;
                                    coverableObjects[objectLoc] = 0;
                                    grid[objectLoc] = 0;
                                    zone = 7;
                                    break;
                                }
                            }
                        }
                    } else if (zone == 8) {
                        if (grid[objectLoc] == 0) {
                            grid[objectLoc] = 9;
                            coins++;
                            handler.replaceObject(objectLoc * 3, new COIN((objectLoc % line) * tileSize,
                                    (objectLoc / line) * tileSize, ID.COIN));
                        } else if (grid[objectLoc] == 9) {
                            coins--;
                            grid[objectLoc] = 0;
                            handler.replaceObject(objectLoc * 3,
                                    new TILE((objectLoc % line) * tileSize, (objectLoc / line) * tileSize, ID.TILE));
                        }
                    } else if (zone == 10) {
                        if (grid[objectLoc] == 0) {
                            grid[objectLoc] = 10;
                            handler.replaceObject(objectLoc * 3,
                                    new GOAL((objectLoc % line) * tileSize, (objectLoc / line) * tileSize, ID.GOAL));
                        } else if (grid[objectLoc] == 10) {
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
                            } else if (zone == 8) {
                                if (grid[(currentY * line) + currentX] == 0) {
                                    grid[(currentY * line) + currentX] = 9;
                                    coins++;
                                    handler.replaceObject(((currentY * line) + currentX) * 3,
                                            new COIN(currentX * tileSize, currentY * tileSize, ID.COIN));
                                } else if (grid[(currentY * line) + currentX] == 9) {
                                    grid[(currentY * line) + currentX] = 0;
                                    handler.replaceObject(((currentY * line) + currentX) * 3,
                                            new TILE(currentX * tileSize, currentY * tileSize, ID.TILE));
                                }
                            } else if (zone == 10) {
                                if (grid[(currentY * line) + currentX] == 0) {
                                    grid[(currentY * line) + currentX] = 10;
                                    handler.replaceObject(((currentY * line) + currentX) * 3,
                                            new GOAL(currentX * tileSize, currentY * tileSize, ID.GOAL));
                                } else if (grid[(currentY * line) + currentX] == 10) {
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
                    if (zone == -6)
                        zone = 6;
                    zone++;
                    System.out.println("Current Zone: " + zone);
                }
            }
            if (zone == 11) {
                stats.SaveMap(grid, goombaFacing, doorStats, teleporterStats);
                out.println("Map Saved!");
                makingMaze = false;
            }
        }
        if (!preMadeMap)
            respawn();
        keyboardClicks = keyboard.getClicks();
        boolean firstRun = true;
        boolean playerHasBeenMoved = false;
        while (playing) {
            if (preMadeMap && firstRun) {
                collectedCoins = 0;
                coins = 0;
                for(int i = 0; i != size; i++){
                    collectedCheckpoints[i] = 0;
                }
                grid = Stats.ReadMap(level);
                goombaFacing = Stats.ReadGoombaStats(level);
                doorStats = Stats.ReadDoorStats(level);
                teleporterStats = Stats.ReadTeleporterStats(level);
                reload();
                respawn();
                firstRun = false;
            }
            while (keyboardClicks == keyboard.getClicks()) {
                out.print("");
            }
            playerHasBeenMoved = false;
            key = keyboard.getKey();
            keyboardClicks = keyboard.getClicks();
            if (key == 87) { // W
                if (pLOC - line > -1 && grid[pLOC - line] != 1 && grid[pLOC - line] != 6 && grid[pLOC - line] != 4) {
                    if (grid[pLOC - line] == 3) {
                        if (collectedCheckpoints[pLOC - line] == 0)
                            lives = 3;
                        collectedCheckpoints[pLOC - line] = 1;
                        spawnLocation = pLOC - line;
                    } else if (grid[pLOC - line] == 10) {
                        out.println("Victory!!!");
                        out.println("Completion: " + (collectedCoins / coins) * 100 + "%");
                        level++;
                        firstRun = true;
                        spawnLocation = originalSpawn;
                        respawn();
                    } else if (grid[pLOC - line] == 7) {
                        for (int i = 0; i != size; i++) {
                            if (doorStats[i] == doorStats[pLOC - line] && i != pLOC - line) {
                                handler.replaceObject((pLOC - line) * 3, new TILE(((pLOC - line) % line) * tileSize,
                                        ((pLOC - line) / line) * tileSize, ID.TILE));
                                handler.replaceObject(i * 3, new TILE((i % line) * tileSize,
                                        (i / line) * tileSize, ID.TILE));
                                doorStats[i] = 0;
                                grid[i] = 0;
                                doorStats[pLOC - line] = 0;
                                grid[pLOC - line] = 0;
                                break;
                            }
                        }
                    } else if (grid[pLOC - line] == 9) {
                        collectedCoins++;
                    } else if (grid[pLOC - line] == 8) {
                        movePlayer(pLOC, pLOC - line);
                        playerHasBeenMoved = true;
                        for (int i = 0; i != size; i++) {
                            if (teleporterStats[i] == teleporterStats[pLOC] && i != pLOC) {
                                movePlayer(pLOC, i);
                                break;
                            }
                        }
                    }
                    if (!playerHasBeenMoved) {
                        movePlayer(pLOC, pLOC - line);
                    }

                } else if (pLOC - line > -1 && grid[pLOC - line] == 4) {
                    respawn();
                    lives--;
                }
            } else if (key == 65) { // A
                if (pLOC % line != 0 && grid[pLOC - 1] != 1 && grid[pLOC - 1] != 4 && grid[pLOC - 1] != 6) {
                    if (grid[pLOC - 1] == 3) {
                        if (collectedCheckpoints[pLOC - 1] == 0)
                            lives = 3;
                        collectedCheckpoints[pLOC - 1] = 1;
                        spawnLocation = pLOC - 1;
                    } else if (grid[pLOC - 1] == 10) {
                        out.println("Victory!!!");
                        out.println("Completion: " + (collectedCoins / coins) * 100 + "%");
                        level++;
                        firstRun = true;
                        spawnLocation = originalSpawn;
                        respawn();
                    } else if (grid[pLOC - 1] == 7) {
                        for (int i = 0; i != size; i++) {
                            if (doorStats[i] == doorStats[pLOC - 1] && i != pLOC - 1) {
                                handler.replaceObject((pLOC - 1) * 3, new TILE(((pLOC - 1) % line) * tileSize,
                                        ((pLOC - 1) / line) * tileSize, ID.TILE));
                                handler.replaceObject(i * 3, new TILE((i % line) * tileSize,
                                        (i / line) * tileSize, ID.TILE));
                                doorStats[i] = 0;
                                grid[i] = 0;
                                doorStats[pLOC - 1] = 0;
                                grid[pLOC - 1] = 0;
                                break;
                            }
                        }
                    } else if (grid[pLOC - 1] == 9) {
                        collectedCoins++;
                    } else if (grid[pLOC - 1] == 8) {
                        movePlayer(pLOC, pLOC - 1);
                        playerHasBeenMoved = true;
                        for (int i = 0; i != size; i++) {
                            if (teleporterStats[i] == teleporterStats[pLOC] && i != pLOC) {
                                movePlayer(pLOC, i);
                                break;
                            }
                        }
                    }
                    if (!playerHasBeenMoved) {
                        movePlayer(pLOC, pLOC - 1);
                    }

                } else if (pLOC % line != 0 && grid[pLOC - 1] == 4) {
                    respawn();
                    lives--;
                }
            } else if (key == 83) { // S
                if (pLOC + line < size && grid[pLOC + line] != 1 && grid[pLOC + line] != 4 && grid[pLOC + line] != 6) {
                    if (grid[pLOC + line] == 3) {
                        spawnLocation = pLOC + line;
                        if (collectedCheckpoints[pLOC + line] == 0)
                            lives = 3;
                        collectedCheckpoints[pLOC + line] = 1;
                    } else if (grid[pLOC + line] == 10) {
                        out.println("Victory!!!");
                        out.println("Completion: " + (collectedCoins / coins) * 100 + "%");
                        level++;
                        firstRun = true;
                        spawnLocation = originalSpawn;
                        respawn();
                    } else if (grid[pLOC + line] == 7) {
                        for (int i = 0; i != size; i++) {
                            if (doorStats[i] == doorStats[pLOC + line] && i != pLOC + line) {
                                handler.replaceObject((pLOC + line) * 3, new TILE(((pLOC + line) % line) * tileSize,
                                        ((pLOC + line) / line) * tileSize, ID.TILE));
                                handler.replaceObject(i * 3, new TILE((i % line) * tileSize,
                                        (i / line) * tileSize, ID.TILE));
                                doorStats[i] = 0;
                                grid[i] = 0;
                                doorStats[pLOC + line] = 0;
                                grid[pLOC + line] = 0;
                                break;
                            }
                        }
                    } else if (grid[pLOC + line] == 9) {
                        collectedCoins++;
                    } else if (grid[pLOC + line] == 8) {
                        movePlayer(pLOC, pLOC + line);
                        playerHasBeenMoved = true;
                        for (int i = 0; i != size; i++) {
                            if (teleporterStats[i] == teleporterStats[pLOC] && i != pLOC) {
                                movePlayer(pLOC, i);
                                break;
                            }
                        }
                    }
                    if (!playerHasBeenMoved) {
                        movePlayer(pLOC, pLOC + line);
                    }

                } else if (pLOC + line > -1 && grid[pLOC + line] == 4) {
                    respawn();
                    lives--;
                }
            } else if (key == 68) { // D
                if ((pLOC - (line - 1)) % line != 0 && grid[pLOC + 1] != 1 && grid[pLOC + 1] != 4
                        && grid[pLOC + 1] != 6) {
                    if (grid[pLOC + 1] == 3) {
                        if (collectedCheckpoints[pLOC + 1] == 0)
                            lives = 3;
                        collectedCheckpoints[pLOC + 1] = 1;
                        spawnLocation = pLOC + 1;
                    } else if (grid[pLOC + 1] == 10) {
                        out.println("Victory!!!");
                        out.println("Completion: " + (collectedCoins / coins) * 100 + "%");
                        level++;
                        firstRun = true;
                        spawnLocation = originalSpawn;
                        respawn();
                    } else if (grid[pLOC + 1] == 7) {
                        for (int i = 0; i != size; i++) {
                            if (doorStats[i] == doorStats[pLOC + 1] && i != pLOC + 1) {
                                handler.replaceObject((pLOC + 1) * 3, new TILE(((pLOC + 1) % line) * tileSize,
                                        ((pLOC + 1) / line) * tileSize, ID.TILE));
                                handler.replaceObject(i * 3, new TILE((i % line) * tileSize,
                                        (i / line) * tileSize, ID.TILE));
                                doorStats[i] = 0;
                                grid[i] = 0;
                                doorStats[pLOC + 1] = 0;
                                grid[pLOC + 1] = 0;
                                break;
                            }
                        }
                    } else if (grid[pLOC + 1] == 9) {
                        collectedCoins++;
                    } else if (grid[pLOC + 1] == 8) {
                        movePlayer(pLOC, pLOC + 1);
                        playerHasBeenMoved = true;
                        for (int i = 0; i != size; i++) {
                            if (teleporterStats[i] == teleporterStats[pLOC] && i != pLOC) {
                                movePlayer(pLOC, i);
                                break;
                            }
                        }
                    }
                    if (!playerHasBeenMoved) {
                        movePlayer(pLOC, pLOC + 1);
                    }

                } else if ((pLOC - (line - 1)) % line != 0 && grid[pLOC + 1] == 4) {
                    respawn();
                    lives--;
                }
            }
        }
    }

    public void movePlayer(int prevLoc, int currentLoc) {
        while (updatingEnemies) {
            out.print("");
        }
        grid[prevLoc] = 0;
        grid[currentLoc] = 11;
        pLOC = currentLoc;
        handler.replaceObject(prevLoc * 3, new TILE((prevLoc % line) * tileSize, (prevLoc / line) * tileSize, ID.TILE));
        handler.replaceObject(currentLoc * 3,
                new PLAYER((currentLoc % line) * tileSize, (currentLoc / line) * tileSize, ID.PLAYER));
    }

    public void respawn() {
        if (lives == 1) {
            collectedCoins = 0;
            coins = 0;
            for (int i = 0; i != size; i++) {
                collectedCheckpoints[i] = 0;
            }
            spawnLocation = originalSpawn;
            out.println("Game Over...");
            grid = Stats.ReadMap(level);
            goombaFacing = Stats.ReadGoombaStats(level);
            doorStats = Stats.ReadDoorStats(level);
            teleporterStats = Stats.ReadTeleporterStats(level);
            reload();
            lives = 3;
            respawn();
        } else {
            if (!updatingEnemies) {
                grid[pLOC] = 0;
                handler.replaceObject(pLOC * 3, new TILE((pLOC % line) * tileSize, (pLOC / line) * tileSize, ID.TILE));
            }
            pLOC = spawnLocation;
            grid[pLOC] = 11;
            handler.replaceObject(pLOC * 3, new PLAYER((pLOC % line) * tileSize, (pLOC / line) * tileSize, ID.PLAYER));
        }
    }

    public void reload() {
        for (int i = 0; i != size; i++) {
            coverableObjects[i] = 0;
            if (grid[i] == 0) {
                handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
            } else if (grid[i] == 1) {
                handler.replaceObject(i * 3, new BORDER((i % line) * tileSize, (i / line) * tileSize, ID.BORDER));
            } else if (grid[i] == 2) {
                handler.replaceObject(i * 3, new SPAWN((i % line) * tileSize, (i / line) * tileSize, ID.SPAWN));
                originalSpawn = i;
                coverableObjects[i] = 2;
                pLOC = i;
                spawnLocation = pLOC;
            } else if (grid[i] == 3) {
                handler.replaceObject(i * 3,
                        new CHECKPOINT((i % line) * tileSize, (i / line) * tileSize, ID.CHECKPOINT));
                coverableObjects[i] = 3;
            } else if (grid[i] == 4) {
                handler.replaceObject(i * 3, new GOOMBA((i % line) * tileSize, (i / line) * tileSize, ID.GOOMBA));
            } else if (grid[i] == 6) {
                handler.replaceObject(i * 3, new DOOR((i % line) * tileSize, (i / line) * tileSize, ID.DOOR));
            } else if (grid[i] == 7) {
                handler.replaceObject(i * 3, new KEY((i % line) * tileSize, (i / line) * tileSize, ID.KEY));
            } else if (grid[i] == 8) {
                handler.replaceObject(i * 3,
                        new TELEPORTER((i % line) * tileSize, (i / line) * tileSize, ID.TELEPORTER));
                coverableObjects[i] = 8;
            } else if (grid[i] == 9) {
                handler.replaceObject(i * 3, new COIN((i % line) * tileSize, (i / line) * tileSize, ID.COIN));
                coins++;
            } else if (grid[i] == 10) {
                handler.replaceObject(i * 3, new GOAL((i % line) * tileSize, (i / line) * tileSize, ID.GOAL));
            } else if (grid[i] == 11) {
                handler.replaceObject(i * 3, new PLAYER((i % line) * tileSize, (i / line) * tileSize, ID.PLAYER));
            }
        }
    }

    public void updateCoverableObjects() {
        for (int i = 0; i != size; i++) {
            if (coverableObjects[i] != 0) {
                if (coverableObjects[i] == 2 && grid[i] == 0) {
                    handler.replaceObject(i * 3, new SPAWN((i % line) * tileSize, (i / line) * tileSize, ID.SPAWN));
                    grid[i] = 2;
                } else if (coverableObjects[i] == 3 && grid[i] == 0) {
                    handler.replaceObject(i * 3,
                            new CHECKPOINT((i % line) * tileSize, (i / line) * tileSize, ID.CHECKPOINT));
                    grid[i] = 3;
                } else if (coverableObjects[i] == 8 && grid[i] == 0) {
                    grid[i] = 8;
                    handler.replaceObject(i * 3,
                            new TELEPORTER((i % line) * tileSize, (i / line) * tileSize, ID.TELEPORTER));
                }
            }
        }
    }

    public void updateEnemies() {
        updatingEnemies = true;
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
                    } else if (i - line > -1 && grid[i - line] == 11) {
                        handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
                        grid[i] = 0;
                        grid[i - line] = 40;
                        goombaFacing[i] = 0;
                        goombaFacing[i - line] = 1;
                        handler.replaceObject((i - line) * 3,
                                new GOOMBA(((i - line) % line) * tileSize, ((i - line) / line) * tileSize, ID.GOOMBA));
                        respawn();
                        lives--;

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
                    } else if (i % line != 0 && grid[i - 1] == 11) {
                        handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
                        grid[i] = 0;
                        grid[i - 1] = 40;
                        goombaFacing[i] = 0;
                        goombaFacing[i - 1] = 2;
                        handler.replaceObject((i - 1) * 3,
                                new GOOMBA(((i - 1) % line) * tileSize, ((i - 1) / line) * tileSize, ID.GOOMBA));
                        respawn();
                        lives--;

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
                    } else if (i + line < size && grid[i + line] == 11) {
                        handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
                        grid[i] = 0;
                        grid[i + line] = 40;
                        goombaFacing[i] = 0;
                        goombaFacing[i + line] = 3;
                        handler.replaceObject((i + line) * 3,
                                new GOOMBA(((i + line) % line) * tileSize, ((i + line) / line) * tileSize, ID.GOOMBA));
                        respawn();
                        lives--;

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
                    } else if (i % line != 0 && grid[i + 1] == 11) {
                        handler.replaceObject(i * 3, new TILE((i % line) * tileSize, (i / line) * tileSize, ID.TILE));
                        grid[i] = 0;
                        grid[i + 1] = 40;
                        goombaFacing[i] = 0;
                        goombaFacing[i + 1] = 4;
                        handler.replaceObject((i + 1) * 3,
                                new GOOMBA(((i + 1) % line) * tileSize, ((i + 1) / line) * tileSize, ID.GOOMBA));
                        respawn();
                        lives--;

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
        updatingEnemies = false;
    }
}
