import java.awt.Color;
import java.awt.Graphics;

public class GOOMBA extends GameObject{
    
    public GOOMBA(int x, int y, ID id){
        super(x, y, id);
    }

    public void tick(){

    }

    public void render(Graphics g) {
        g.setColor(new Color(123, 63, 0));
        g.fillRect(x, y, 15, 15);
    }
}
