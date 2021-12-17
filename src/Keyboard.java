import java.awt.event.*;

public class Keyboard implements KeyListener {
    int key, clicks;
    int[] arr = new int[3];
    boolean pause;
    int x = 0, y = -1;

    public int getKey() {
        return key;
    }

    public int getClicks() {
        return clicks;
    }

    public boolean getPause() {
        return pause;
    }

    public void keyPressed(KeyEvent e) {
        x++;
        y++;
        if(x % 3 == 0){
        key = e.getKeyCode();
        clicks++;
        }
        arr[y] = e.getKeyCode();
        if(e.getKeyCode() != 17 && e.getKeyCode() != 16 && e.getKeyCode() != 80) {
            for(int i = 0; i != 3; i++)
                arr[i] = 0;
            y = -1;
        }
        if(y == 2) {
            if(arr[0] == 17 && arr[1] == 16 && arr[2] == 80) {
                if(pause)
                    pause = false;
                else
                    pause = true;

            }
            for(int i = 0; i != 3; i++)
                arr[i] = 0;
            y = -1;
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
