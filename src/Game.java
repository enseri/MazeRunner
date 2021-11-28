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
    private boolean isMakingMaze;
    private int spawnLocation = 0;
    private int playerSpawnLocation = 0;
    private int pLOC = 0;
    private int[] checkpoints = new int[size];
    private int[] grid = new int[size + 3000];
    private Thread thread;
    private boolean running = false;
    private Handler handler;
    private Mouse mouse = new Mouse();
    private Keyboard keyboard = new Keyboard();

    public Game() {
        while (line != Math.sqrt(size)) {
            line++;
        }
        WIDTH = 15 * line;
        HEIGHT = 15 * line;
        new Window(WIDTH, HEIGHT, "TICTACTOE", this);

        handler = new Handler();
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
        this.addKeyListener(keyboard);

        int f = 0;
        int d = 0;
        int g = 1;
        int h = 0;
        while (d != size) {
            handler.addObject(new TILE(0 + (f * 15), 0 + (h * 15), ID.TILE));
            handler.addObject(new COLUMN(0 + (f * 15), 0 + (h * 15), ID.COLUMN));
            handler.addObject(new ROW(0 + (f * 15), 0 + (h * 15), ID.ROW));
            f++;
            d++;
            g++;
            if (g == line + 1) {
                g = 1;
                f = 0;
                h++;
            }
        }
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
        long timer = System.currentTimeMillis();
        int x = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            if (running) {
                render();
                x++;
            }
            if (x == 10) {
                x = 0;
                if (!isMakingMaze) {
                    updateGoombas();
                    generateSpawnAndCheckpoint();
                    for(int i = 0; i != size; i++){
                        if(grid[i] == 7 && i != pLOC){
                            grid[i] = 0;
                        }
                    }
                }
            }
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
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

    /*
     * Zones: Zone 0 - Setting up border Zone 1 - Setting spawn Zone 2 - Setting
     * checkpoints Zone 3 - Goomnba trap Zone 4 - Trap 2 Zone 5 - Trap 3 Zone 6 -
     * Playing
     *
     * Status: Status -1 - Idle Status 0 - Dragging mouse Status 1 - Finished
     * dragging Status 2 - Clicked mouse Status 3 - Keyboard key pressed
     * 
     * Grid: Grid 0 - Normal Tile Grid 1 - Border Tile Grid 2 - Spawn Tile Grid 3 -
     * Checkpoint Tile Grid 4 - Goomba Enemy Grid 7 - Player
     */
    public void begin() {
        int clicks = mouse.getClicks();
        int types = 0;
        int zone = 0;
        int spawns = 0;
        isMakingMaze = true;
        while (isMakingMaze) {
            while (clicks == mouse.getClicks() && types == keyboard.getTypes()) {
                out.print("");
            }
            if (keyboard.getTypes() != types && keyboard.getKey() == 10) {
                zone++;
                System.out.println("Current Zone: " + zone);
                mouse.setStatus(3);
            }
            clicks = mouse.getClicks();
            types = keyboard.getTypes();
            while (mouse.getStatus() == 0) {
                out.print("");
            }
            if (mouse.getStatus() == 1 && zone == 0) {
                int tempX = mouse.getDragLocations(0);
                int tempY = mouse.getDragLocations(1);
                int amountTilesPerColumn = 0;
                boolean yIsDownwards = false;
                if (tempY < mouse.getDragLocations(3)) {
                    while (tempY <= mouse.getDragLocations(3)) {
                        tempY++;
                        amountTilesPerColumn++;
                    }
                    yIsDownwards = true;
                } else if (tempY > mouse.getDragLocations(3)) {
                    while (tempY >= mouse.getDragLocations(3)) {
                        tempY--;
                        amountTilesPerColumn++;
                    }
                    yIsDownwards = false;
                }
                if (amountTilesPerColumn == 0)
                    amountTilesPerColumn++;
                if (tempX <= mouse.getDragLocations(2)) {
                    while (tempX <= mouse.getDragLocations(2)) {
                        tempX++;
                        if (yIsDownwards) {
                            for (int i = mouse.getDragLocations(1); i != amountTilesPerColumn
                                    + mouse.getDragLocations(1); i++) {
                                if (grid[((tempX - 1) + (i * 30))] == 0) {
                                    handler.replaceObject(((tempX - 1) + (i * 30)) * 3,
                                            new BORDER((tempX - 1) * 15, i * 15, ID.BORDER));
                                    grid[((tempX - 1) + (i * 30))] = 1;
                                } else if (grid[((tempX - 1) + (i * 30))] == 1) {
                                    handler.replaceObject(((tempX - 1) + (i * 30)) * 3,
                                            new TILE((tempX - 1) * 15, i * 15, ID.TILE));
                                    grid[((tempX - 1) + (i * 30))] = 0;
                                }
                            }
                        } else if (!yIsDownwards) {
                            for (int i = mouse.getDragLocations(1); i != mouse.getDragLocations(1)
                                    - amountTilesPerColumn; i--) {
                                if (grid[((tempX - 1) + (i * 30))] == 0) {
                                    handler.replaceObject(((tempX - 1) + (i * 30)) * 3,
                                            new BORDER((tempX - 1) * 15, i * 15, ID.BORDER));
                                    grid[((tempX - 1) + (i * 30))] = 1;
                                } else if (grid[((tempX - 1) + (i * 30))] == 1) {
                                    handler.replaceObject(((tempX - 1) + (i * 30)) * 3,
                                            new TILE((tempX - 1) * 15, i * 15, ID.TILE));
                                    grid[((tempX - 1) + (i * 30))] = 0;
                                }
                            }
                        }
                    }
                } else if (tempX >= mouse.getDragLocations(2)) {
                    while (tempX >= mouse.getDragLocations(2)) {
                        tempX--;
                        if (yIsDownwards) {
                            for (int i = mouse.getDragLocations(1); i != amountTilesPerColumn
                                    + mouse.getDragLocations(1); i++) {
                                if (grid[((tempX - 1) + (i * 30))] == 0) {
                                    handler.replaceObject(((tempX + 1) + (i * 30)) * 3,
                                            new BORDER((tempX + 1) * 15, i * 15, ID.BORDER));
                                    grid[((tempX - 1) + (i * 30))] = 1;
                                } else if (grid[((tempX - 1) + (i * 30))] == 1) {
                                    handler.replaceObject(((tempX + 1) + (i * 30)) * 3,
                                            new TILE((tempX + 1) * 15, i * 15, ID.TILE));
                                    grid[((tempX - 1) + (i * 30))] = 0;
                                }
                            }
                        } else if (!yIsDownwards) {
                            for (int i = mouse.getDragLocations(1); i != mouse.getDragLocations(1)
                                    - amountTilesPerColumn; i--) {
                                if (grid[((tempX - 1) + (i * 30))] == 0) {
                                    handler.replaceObject(((tempX + 1) + (i * 30)) * 3,
                                            new BORDER((tempX + 1) * 15, i * 15, ID.BORDER));
                                    grid[((tempX - 1) + (i * 30))] = 1;
                                } else if (grid[((tempX - 1) + (i * 30))] == 1) {
                                    handler.replaceObject(((tempX + 1) + (i * 30)) * 3,
                                            new TILE((tempX + 1) * 15, i * 15, ID.TILE));
                                    grid[((tempX - 1) + (i * 30))] = 0;
                                }
                            }
                        }
                    }
                }
            } else if (mouse.getStatus() == 2) {
                int objectLoc = mouse.getObjectLoc();
                if (zone == 0) {
                    if (grid[objectLoc] == 0) {
                        grid[objectLoc] = 1;
                        handler.replaceObject(objectLoc * 3,
                                new BORDER((objectLoc % 30) * 15, (objectLoc / 30) * 15, ID.BORDER));
                    } else if (grid[objectLoc] == 1) {
                        grid[objectLoc] = 0;
                        handler.replaceObject(objectLoc * 3,
                                new TILE((objectLoc % 30) * 15, (objectLoc / 30) * 15, ID.TILE));
                    }
                } else if (zone == 1) {
                    if (grid[objectLoc] == 0 && spawns == 0) {
                        grid[objectLoc] = 2;
                        spawns++;
                        spawnLocation = objectLoc;
                        handler.replaceObject(objectLoc * 3,
                                new SPAWN((objectLoc % 30) * 15, (objectLoc / 30) * 15, ID.SPAWN));
                    } else if (grid[objectLoc] == 2) {
                        spawns--;
                        grid[objectLoc] = 0;
                        handler.replaceObject(objectLoc * 3,
                                new TILE((objectLoc % 30) * 15, (objectLoc / 30) * 15, ID.TILE));
                    }
                } else if (zone == 2) {
                    if (grid[objectLoc] == 0) {
                        grid[objectLoc] = 3;
                        checkpoints[objectLoc] = 1;
                        handler.replaceObject(objectLoc * 3,
                                new CHECKPOINT((objectLoc % 30) * 15, (objectLoc / 30) * 15, ID.CHECKPOINT));
                    } else if (grid[objectLoc] == 3) {
                        grid[objectLoc] = 0;
                        checkpoints[objectLoc] = 0;
                        handler.replaceObject(objectLoc * 3,
                                new TILE((objectLoc % 30) * 15, (objectLoc / 30) * 15, ID.TILE));
                    }
                } else if (zone == 3) {
                    if (grid[objectLoc] == 0) {
                        // 1(default) = vertical movement 2 = horizontal movement
                        out.println("Goomba created");
                        grid[objectLoc] = 4;
                        grid[objectLoc + 1000] = 1;
                        grid[objectLoc + 2000] = 0;

                        handler.replaceObject(objectLoc * 3,
                                new GOOMBA((objectLoc % 30) * 15, (objectLoc / 30) * 15, ID.GOOMBA));
                    } else if (grid[objectLoc] == 4) {
                        if (grid[objectLoc + 2000] != 1) {
                            grid[objectLoc + 2000]++;
                            grid[objectLoc + 1000] = 2;
                            out.println("Goomba turned");
                        } else if (grid[objectLoc + 2000] == 1) {
                            out.println("Goomba deleted");
                            grid[objectLoc] = 0;
                            grid[objectLoc + 2000] = 0;
                            grid[objectLoc + 1000] = 0;
                            grid[objectLoc] = 0;
                            handler.replaceObject(objectLoc * 3,
                                    new TILE((objectLoc % 30) * 15, (objectLoc / 30) * 15, ID.TILE));
                        }
                    }
                }
            } else if (zone == 6) {
                isMakingMaze = false;
            }
        }
        boolean isPlaying = true;
        types = keyboard.getTypes();
        // W: 87
        // A: 65
        // S: 83
        // D: 68
        pLOC = spawnLocation;
        playerSpawnLocation = pLOC;
        grid[pLOC] = 7;
        handler.replaceObject(pLOC * 3, new PLAYER((pLOC % 30) * 15, (pLOC / 30) * 15, ID.PLAYER));
        while (isPlaying) {
            while (types == keyboard.getTypes()) {
                out.print("");
            }
            types = keyboard.getTypes();
            if (keyboard.getKey() == 87) { // up
                if (pLOC - 30 > -1 && grid[pLOC - 30] != 1) {
                    if (grid[pLOC - 30] == 4 || grid[pLOC - 30] == 5 || grid[pLOC - 30] == 6) {
                        handler.replaceObject(pLOC * 3, new TILE((pLOC % 30) * 15, (pLOC / 30) * 15, ID.TILE));
                        respawn();
                    } else if (grid[pLOC - 30] == 3 || grid[pLOC - 30] == 2) {
                        grid[pLOC] = 0;
                        handler.replaceObject(pLOC * 3, new TILE((pLOC % 30) * 15, (pLOC / 30) * 15, ID.TILE));
                        pLOC -= 30;
                        playerSpawnLocation = pLOC;
                        grid[pLOC] = 7;
                        handler.replaceObject(pLOC * 3, new PLAYER((pLOC % 30) * 15, (pLOC / 30) * 15, ID.PLAYER));

                    } else if (grid[pLOC - 30] == 0) {
                        grid[pLOC] = 0;
                        handler.replaceObject(pLOC * 3, new TILE((pLOC % 30) * 15, (pLOC / 30) * 15, ID.TILE));
                        pLOC -= 30;
                        grid[pLOC] = 7;
                        handler.replaceObject(pLOC * 3, new PLAYER((pLOC % 30) * 15, (pLOC / 30) * 15, ID.PLAYER));
                    }
                }
            } else if (keyboard.getKey() == 65) { // left
                if (pLOC % 30 != 0 && grid[pLOC - 1] != 1) {
                    if (grid[pLOC - 1] == 4 || grid[pLOC - 1] == 5 || grid[pLOC - 1] == 6) {
                        handler.replaceObject(pLOC * 3, new TILE((pLOC % 30) * 15, (pLOC / 30) * 15, ID.TILE));
                        respawn();
                    } else if (grid[pLOC - 1] == 3 || grid[pLOC - 1] == 2) {
                        grid[pLOC] = 0;
                        handler.replaceObject(pLOC * 3, new TILE((pLOC % 30) * 15, (pLOC / 30) * 15, ID.TILE));
                        pLOC -= 1;
                        playerSpawnLocation = pLOC;
                        grid[pLOC] = 7;
                        handler.replaceObject(pLOC * 3, new PLAYER((pLOC % 30) * 15, (pLOC / 30) * 15, ID.PLAYER));

                    } else if (grid[pLOC - 1] == 0) {
                        grid[pLOC] = 0;
                        handler.replaceObject(pLOC * 3, new TILE((pLOC % 30) * 15, (pLOC / 30) * 15, ID.TILE));
                        pLOC -= 1;
                        grid[pLOC] = 7;
                        handler.replaceObject(pLOC * 3, new PLAYER((pLOC % 30) * 15, (pLOC / 30) * 15, ID.PLAYER));
                    }
                }
            } else if (keyboard.getKey() == 83) { // down
                if (pLOC + 30 > -1 && grid[pLOC + 30] != 1) {
                    if (grid[pLOC + 30] == 4 || grid[pLOC + 30] == 5 || grid[pLOC + 30] == 6) {
                        handler.replaceObject(pLOC * 3, new TILE((pLOC % 30) * 15, (pLOC / 30) * 15, ID.TILE));
                        respawn();
                    } else if (grid[pLOC + 30] == 3 || grid[pLOC + 30] == 2) {
                        grid[pLOC] = 0;
                        handler.replaceObject(pLOC * 3, new TILE((pLOC % 30) * 15, (pLOC / 30) * 15, ID.TILE));
                        pLOC += 30;
                        playerSpawnLocation = pLOC;
                        grid[pLOC] = 7;
                        handler.replaceObject(pLOC * 3, new PLAYER((pLOC % 30) * 15, (pLOC / 30) * 15, ID.PLAYER));

                    } else if (grid[pLOC + 30] == 0) {
                        grid[pLOC] = 0;
                        handler.replaceObject(pLOC * 3, new TILE((pLOC % 30) * 15, (pLOC / 30) * 15, ID.TILE));
                        pLOC += 30;
                        grid[pLOC] = 7;
                        handler.replaceObject(pLOC * 3, new PLAYER((pLOC % 30) * 15, (pLOC / 30) * 15, ID.PLAYER));
                    }
                }
            } else if (keyboard.getKey() == 68) { // right
                if (pLOC + 1 > -1 && grid[pLOC + 1] != 1) {
                    if (grid[pLOC + 1] == 4 || grid[pLOC + 1] == 5 || grid[pLOC + 1] == 6) {
                        handler.replaceObject(pLOC * 3, new TILE((pLOC % 30) * 15, (pLOC / 30) * 15, ID.TILE));
                        respawn();
                    } else if (grid[pLOC + 1] == 3 || grid[pLOC + 1] == 2) {
                        grid[pLOC] = 0;
                        handler.replaceObject(pLOC * 3, new TILE((pLOC % 30) * 15, (pLOC / 30) * 15, ID.TILE));
                        pLOC += 1;
                        playerSpawnLocation = pLOC;
                        grid[pLOC] = 7;
                        handler.replaceObject(pLOC * 3, new PLAYER((pLOC % 30) * 15, (pLOC / 30) * 15, ID.PLAYER));

                    } else if (grid[pLOC + 1] == 0) {
                        grid[pLOC] = 0;
                        handler.replaceObject(pLOC * 3, new TILE((pLOC % 30) * 15, (pLOC / 30) * 15, ID.TILE));
                        pLOC += 1;
                        grid[pLOC] = 7;
                        handler.replaceObject(pLOC * 3, new PLAYER((pLOC % 30) * 15, (pLOC / 30) * 15, ID.PLAYER));
                    }
                }
            }
        }
    }

    public void respawn() {
        int i = playerSpawnLocation;
        pLOC = playerSpawnLocation;
        handler.replaceObject(i * 3, new PLAYER((i % 30) * 15, (i / 30) * 15, ID.PLAYER));
    }

    // Goomba Stats: Goomba is present
    // Goomba Stats + 1000: Goomba facing
    // Goomba Stats + 2000: Goomba Delete Threshold
    // Goomba stats + 3000: Goomba Current Trajectory
    // trajectories:
    // 1 - upwards
    // 2 - left
    // 3 - down
    // 4 - right
    public void updateGoombas() {
        for (int i = 0; i != size; i++) {
            if (grid[i] == 4) {
                if (grid[i + 3000] == 0) {
                    if (grid[i + 1000] == 1) {
                        if (i - 30 > -1 && grid[i - 30] != 1 && grid[i - 30] != 4 && grid[i - 30] != 50) {
                            if (grid[i - 30] == 7) {
                                respawn();
                            }
                            grid[i] = 0;
                            grid[i - 30] = 50;// 50 = temp goomba
                            grid[i + 1000 - 30] = grid[i + 1000]; // trajectory
                            grid[i + 1000] = 0;
                            grid[i + 2000 - 30] = grid[i + 2000]; // deletion threshold
                            grid[i + 2000] = 0;
                            grid[i + 3000 - 30] = 1; // current facing
                            grid[i + 3000] = 0;
                        } else if (i + 30 < size && grid[i + 30] != 1 && grid[i + 30] != 4 && grid[i + 30] != 50) {
                            if (grid[i + 30] == 7) {
                                respawn();
                            }
                            grid[i] = 0;
                            grid[i + 30] = 50;// 50 = temp goomba
                            grid[i + 1000 + 30] = grid[i + 1000]; // trajectory
                            grid[i + 1000] = 0;
                            grid[i + 2000 + 30] = grid[i + 2000]; // deletion threshold
                            grid[i + 2000] = 0;
                            grid[i + 3000 + 30] = 3; // current facing
                            grid[i + 3000] = 0;
                        }
                    } else if (grid[i + 1000] == 2) {
                        if (i % 30 != 0 && grid[i - 1] != 1 && grid[i - 1] != 4 && grid[i - 1] != 50) {
                            if (grid[i - 1] == 7) {
                                respawn();
                            }
                            grid[i] = 0;
                            grid[i - 1] = 50;// 50 = temp goomba
                            grid[i + 1000 - 1] = grid[i + 1000]; // trajectory
                            grid[i + 1000] = 0;
                            grid[i + 2000 - 1] = grid[i + 2000]; // deletion threshold
                            grid[i + 2000] = 0;
                            grid[i + 3000 - 1] = 2; // current facing
                            grid[i + 3000] = 0;
                        } else if ((i - 29) % 30 != 0 && grid[i + 1] != 1 && grid[i + 1] != 4 && grid[i + 1] != 50) {
                            if (grid[i + 1] == 7) {
                                respawn();
                            }
                            grid[i] = 0;
                            grid[i + 1] = 50;// 50 = temp goomba
                            grid[i + 1000 + 1] = grid[i + 1000]; // trajectory
                            grid[i + 1000] = 0;
                            grid[i + 2000 + 1] = grid[i + 2000]; // deletion threshold
                            grid[i + 2000] = 0;
                            grid[i + 3000 + 1] = 4; // current facing
                            grid[i + 3000] = 0;
                        }
                    }
                } else if (grid[i + 3000] != 0) {
                    if (grid[i + 3000] == 1 && i - 30 > -1 && grid[i - 30] != 1 && grid[i - 30] != 4
                            && grid[i - 30] != 50) {
                        if (grid[i - 30] == 7) {
                            respawn();
                        }
                        grid[i] = 0;
                        grid[i - 30] = 50;// 50 = temp goomba
                        grid[i + 1000 - 30] = grid[i + 1000]; // trajectory
                        grid[i + 1000] = 0;
                        grid[i + 2000 - 30] = grid[i + 2000]; // deletion threshold
                        grid[i + 2000] = 0;
                        grid[i + 3000 - 30] = 1; // current facing
                        grid[i + 3000] = 0;
                    } else if ((grid[i + 3000] == 3 || grid[i + 3000] == 1) && i + 30 < size && grid[i + 30] != 1
                            && grid[i + 30] != 4 && grid[i + 30] != 50) {
                        if (grid[i + 30] == 7) {
                            respawn();
                        }
                        grid[i] = 0;
                        grid[i + 30] = 50;// 50 = temp goomba
                        grid[i + 1000 + 30] = grid[i + 1000]; // trajectory
                        grid[i + 1000] = 0;
                        grid[i + 2000 + 30] = grid[i + 2000]; // deletion threshold
                        grid[i + 2000] = 0;
                        grid[i + 3000 + 30] = 3; // current facing
                        grid[i + 3000] = 0;
                    } else if ((grid[i + 3000] == 1 || grid[i + 3000] == 3) && i - 30 > -1 && grid[i - 30] != 1
                            && grid[i - 30] != 4 && grid[i - 30] != 50) {
                        if (grid[i - 30] == 7) {
                            respawn();
                        }
                        grid[i] = 0;
                        grid[i - 30] = 50;// 50 = temp goomba
                        grid[i + 1000 - 30] = grid[i + 1000]; // trajectory
                        grid[i + 1000] = 0;
                        grid[i + 2000 - 30] = grid[i + 2000]; // deletion threshold
                        grid[i + 2000] = 0;
                        grid[i + 3000 - 30] = 1; // current facing
                        grid[i + 3000] = 0;
                    } else if (grid[i + 3000] == 2 && i % 30 != 0 && grid[i - 1] != 1 && grid[i - 1] != 4
                            && grid[i - 1] != 50) {
                        if (grid[i - 1] == 7) {
                            respawn();
                        }
                        grid[i] = 0;
                        grid[i - 1] = 50;// 50 = temp goomba
                        grid[i + 1000 - 1] = grid[i + 1000]; // trajectory
                        grid[i + 1000] = 0;
                        grid[i + 2000 - 1] = grid[i + 2000]; // deletion threshold
                        grid[i + 2000] = 0;
                        grid[i + 3000 - 1] = 2; // current facing
                        grid[i + 3000] = 0;
                    } else if ((grid[i + 3000] == 4 || grid[i + 3000] == 2) && (i - 29) % 30 != 0 && grid[i + 1] != 1
                            && grid[i + 1] != 4 && grid[i + 1] != 50) {
                        if (grid[i + 1] == 7) {
                            respawn();
                        }
                        grid[i] = 0;
                        grid[i + 1] = 50;// 50 = temp goomba
                        grid[i + 1000 + 1] = grid[i + 1000]; // trajectory
                        grid[i + 1000] = 0;
                        grid[i + 2000 + 1] = grid[i + 2000]; // deletion threshold
                        grid[i + 2000] = 0;
                        grid[i + 3000 + 1] = 4; // current facing
                        grid[i + 3000] = 0;
                    } else if ((grid[i + 3000] == 2 || grid[i + 3000] == 4) && i % 30 != 0 && grid[i - 1] != 1
                            && grid[i - 1] != 4 && grid[i - 1] != 50) {
                        if (grid[i - 1] == 7) {
                            respawn();
                        }
                        grid[i] = 0;
                        grid[i - 1] = 50;// 50 = temp goomba
                        grid[i + 1000 - 1] = grid[i + 1000]; // trajectory
                        grid[i + 1000] = 0;
                        grid[i + 2000 - 1] = grid[i + 2000]; // deletion threshold
                        grid[i + 2000] = 0;
                        grid[i + 3000 - 1] = 2; // current facing
                        grid[i + 3000] = 0;
                    }
                }
            }
        }
        for (int i = 0; i != size + 3000; i++) {
            if (grid[i] == 50) {
                grid[i] = 4;
            }
        }
        for (int i = 0; i != size; i++) {
            switch (grid[i]) {
            case 0:
                handler.replaceObject(i * 3, new TILE((i % 30) * 15, (i / 30) * 15, ID.TILE));
                break;
            case 1:
                handler.replaceObject(i * 3, new BORDER((i % 30) * 15, (i / 30) * 15, ID.BORDER));
                break;
            case 2:
                handler.replaceObject(i * 3, new SPAWN((i % 30) * 15, (i / 30) * 15, ID.SPAWN));
                break;
            case 3:
                handler.replaceObject(i * 3, new CHECKPOINT((i % 30) * 15, (i / 30) * 15, ID.CHECKPOINT));
                break;
            case 4:
                handler.replaceObject(i * 3, new GOOMBA((i % 30) * 15, (i / 30) * 15, ID.GOOMBA));
                break;
            }
        }
    }

    public void generateSpawnAndCheckpoint() {
        for (int i = 0; i != size; i++) {
            if (checkpoints[i] == 1 && grid[i] == 0) {
                handler.replaceObject(i * 3, new CHECKPOINT((i % 30) * 15, (i / 30) * 15, ID.CHECKPOINT));
            }
            if (spawnLocation == i && grid[i] == 0) {
                handler.replaceObject(i * 3, new SPAWN((i % 30) * 15, (i / 30) * 15, ID.SPAWN));
            }
        }
    }
}
