package application;

import javafx.scene.canvas.GraphicsContext;

public interface absolutebullet {
    void update();

    void draw(GraphicsContext gc);

    boolean colide(Ship ship);
}