import java.awt.event.*;
import javax.swing.event.MouseInputListener;

public class Mouse implements MouseInputListener {
    private int clicks;
    private int x = 0;
    private int y = 0;
    private int initialX = 0;
    private int initialY = 0;
    private int objectLocation;
    private boolean initialDrag = true;
    private int status = -1;

    public int getX(){
        return (x / 100) * 100;
    }
    public int getY(){
        return (y / 100) * 100;
    }
    public int getClicks() {
        return clicks;
    }
    public void setClicks(int x) {
        clicks = x;
    }
    public int getObjectLoc(){
        return objectLocation;
    }

    public void mouseClicked(MouseEvent e) {
        status = 2;
        x = e.getX();
        y = e.getY();
        objectLocation = ((y / 15) * 30) + (x / 15);
        clicks++; 
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {
        if(status == 0)
            status = 1;
        initialDrag = true;
        System.out.println(initialX + " " + initialY + " | " + x + " " + y);
    }

    public void mouseDragged(MouseEvent e) {
        status = 0;
        if(initialDrag){
            initialX = e.getX() / 15;
            initialY = e.getY() / 15;
            clicks++;
        }
        x = e.getX() / 15;
        y = e.getY() / 15;
        initialDrag = false;

        e.consume();
    }

    public void mouseMoved(MouseEvent e) {

        e.consume();
    }
    public int getStatus(){
        return status;
    }
    public void setStatus(int x){
        status = x;
    }
    public int getDragLocations(int type){
        switch(type){
            case 0:
                return initialX;
            case 1:
                return initialY;
            case 2:
                return x;
            case 3:
                return y;
        }
        return 0;
    }
}
