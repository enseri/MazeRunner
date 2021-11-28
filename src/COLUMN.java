import java.awt.Color;
import java.awt.Graphics;

public class COLUMN extends GameObject{
    
    public COLUMN(int x, int y, ID id){
        super(x, y, id);
    }

    public void tick(){

    }

    public void render(Graphics g) {
        g.setColor(Color.gray);
        g.fillRect(x, y, 1, 15);
    }
}
