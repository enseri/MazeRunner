import java.awt.event.*;

public class Keyboard implements KeyListener {
    int key, clicks;
    int x = 0;

    public int getKey() {
        return key;
    }

    public int getClicks() {
        return clicks;
    }

    public void keyPressed(KeyEvent e) {
        x++;
        if(x % 3 == 0){
        key = e.getKeyCode();
        clicks++;
        }
    }

    public void keyReleased(KeyEvent e) {
        x = 0;
        key = e.getKeyCode();
        clicks++;
        e.consume();
    }

    public void keyTyped(KeyEvent e) {

    }

}
