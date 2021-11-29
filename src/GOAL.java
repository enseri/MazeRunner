import java.awt.Color;
import java.awt.Graphics;

public class GOAL extends GameObject{
    
    public GOAL(int x, int y, ID id){
        super(x, y, id);
    }

    public void tick(){

    }

    public void render(Graphics g) {
        g.setColor(Color.magenta);
        g.fillRect(x, y, 15, 15);
    }
}
