import java.awt.Color;
import java.awt.Graphics;

public class TELEPORTER extends GameObject{
    
    public TELEPORTER(int x, int y, ID id){
        super(x, y, id);
    }

    public void tick(){

    }

    public void render(Graphics g) {
        g.setColor(new Color(177, 156, 217));
        g.fillRect(x, y, 15, 15);
    }
}
