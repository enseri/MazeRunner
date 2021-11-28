import java.awt.event.*;

public class Keyboard implements KeyListener {
    private int key = 0;
    private int types = 0;
    public int getTypes(){
        return types;
    }
    public int getKey(){
        return key;
    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {
        key = e.getKeyCode();
        types++;
    }

    public void keyTyped(KeyEvent e) {
        
    }

}
