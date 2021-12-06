import java.awt.event.*;

public class Keyboard implements KeyListener {
    int key, clicks;

    public int getKey() {
        return key;
    }

    public int getClicks() {
        return clicks;
    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {
        key = e.getKeyCode();
        clicks++;
        e.consume();
    }

    public void keyTyped(KeyEvent e) {

    }

}
