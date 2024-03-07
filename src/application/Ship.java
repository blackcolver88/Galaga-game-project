package application;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;

public sealed class Ship permits CollisionManager {
    int posX, posY, size;
    boolean exploding, destroyed;
    Image img;
    int explosionStep = 0;

    public Ship(int posX, int posY, int size, Image image) {
        this.posX = posX;
        this.posY = posY;
        this.size = size;
        img = image;
    }

    public Bullet shoot() {
        return new Bullet(posX + size / 2 - Bullet.size / 2, posY - Bullet.size);
    }
    public SpecialBullet shoot2() {
    	return new SpecialBullet(posX + size / 2 - SpecialBullet.size / 2, posY - SpecialBullet.size);
    }

    public void update() {
        if (exploding) explosionStep++;
        destroyed = explosionStep > Galaga.EXPLOSION_STEPS;
    }

    public void draw(GraphicsContext gc) {
        if (exploding) {
            gc.drawImage(Galaga.EXPLOSION_IMG, explosionStep % Galaga.EXPLOSION_COL * Galaga.EXPLOSION_W,
                    (explosionStep / Galaga.EXPLOSION_ROWS) * Galaga.EXPLOSION_H + 1,
                    Galaga.EXPLOSION_W, Galaga.EXPLOSION_H,
                    posX, posY, size, size);
        } else {
            gc.drawImage(img, posX, posY, size, size);
        }
    }

    public boolean colide(Ship other) {
        int d = distance(this.posX + size / 2, this.posY + size / 2,
                other.posX + other.size / 2, other.posY + other.size / 2);
        return d < other.size / 2 + this.size / 2;
    }

    public void explode() {
        exploding = true;
        explosionStep = -1;
    }

    private int distance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }
   
}