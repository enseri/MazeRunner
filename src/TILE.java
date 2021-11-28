import java.awt.Color;
import java.awt.Graphics;

public class TILE extends GameObject{
    
    public TILE(int x, int y, ID id){
        super(x, y, id);
    }

    public void tick(){

    }

    public void render(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(x, y, 15, 15);
    }
}
