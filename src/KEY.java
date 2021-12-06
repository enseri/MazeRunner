import java.awt.Color;
import java.awt.Graphics;

public class KEY extends GameObject{

    public KEY(int x, int y, ID id){
        super(x, y, id);
    }

    public void tick(){

    }

    public void render(Graphics g) {
        g.setColor(new Color(255,228,181));
        g.fillRect(x, y, 15, 15);
    }
}
