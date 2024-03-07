package application;

import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

public class Bullet implements absolutebullet {

    public boolean toRemove;

    int posX, posY, speed = 8;
    static final int size = 1;

    public Bullet(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public void update() {
        posY -= speed;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.fillRect(posX - 2, posY - 10, size + 2, size + 10);
    }

    public boolean colide(Ship ship) {
        int distance = distance(this.posX + size / 2, this.posY + size / 2,
                ship.posX + ship.size / 2, ship.posY + ship.size / 2);
        return distance < ship.size / 2 + size / 2;
    }

    private int distance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }
}