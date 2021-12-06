import java.awt.Color;
import java.awt.Graphics;

public class DOOR extends GameObject{
    
    public DOOR(int x, int y, ID id){
        super(x, y, id);
    }

    public void tick(){

    }

    public void render(Graphics g) {
        g.setColor(Color.gray);
        g.fillRect(x, y, 15, 15);
    }
}
