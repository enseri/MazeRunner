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
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            if (running)
                render();
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

    public void begin() {

    }
}
