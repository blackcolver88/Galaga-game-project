package application;

import javafx.scene.image.Image;

 public final class CollisionManager extends Ship {

    int SPEED = (Galaga.score / 8) + 1;

    public CollisionManager(int posX, int posY, int size, Image image) {
        super(posX, posY, size, image);
    }

    public void update() {
        super.update();
        if (!exploding && !destroyed) posY += SPEED;
        if (posY > Galaga.HEIGHT) destroyed = true;
    }
}