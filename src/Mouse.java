import java.awt.event.*;
import javax.swing.event.MouseInputListener;

public class Mouse implements MouseInputListener {
    private int initialX, initialY, x, y, event = -1, clicks, objectLoc, tileSize, line;

    public void setSizes(int x, int y) {
        tileSize = x;
        line = y;
    }

    public int getObjectLoc() {
        return objectLoc;
    }

    public int getEvent() {
        return event;
    }

    public int getClicks() {
        return clicks;
    }

    public int getDragLoc(int x) {
        switch (x) {
        case 1:
            return initialX;
        case 2:
            return initialY;
        case 3:
            return this.x;
        case 4:
            return y;
        default:
            return 0;
        }
    }

    public void mouseClicked(MouseEvent e) {
        event = 0;
        x = e.getX() / tileSize;
        y = e.getY() / tileSize;
        objectLoc = (y * line) + x;
        clicks++;
        e.consume();
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        initialX = e.getX();
        initialY = e.getY();
        e.consume();
    }

    public void mouseReleased(MouseEvent e) {
        if (event == 1) {
            event = 2;
            clicks++;
        }
        e.consume();
    }

    public void mouseDragged(MouseEvent e) {
        event = 1;
        x = e.getX();
        y = e.getY();
        e.consume();
    }

    public void mouseMoved(MouseEvent e) {

        e.consume();
    }
}
