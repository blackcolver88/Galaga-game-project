package application;
import java.util.Objects;
import javafx.animation.KeyFrame; 
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
public class Galaga{
    static final Random RAND = new Random();
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 700;
    public static final int PLAYER_SIZE = 60;
    static final Image PLAYER_IMG = new Image("/player.png");
    static final Image EXPLOSION_IMG = new Image("/exploooosion.png");
    static final Image HEART_IMG = new Image("/heart.png");
    private boolean shipRespawning = false;
    private long respawnStartTime = 0;
    private static final int RESPAWN_TIME = 1000;
    static final int EXPLOSION_W = 128;
    static final int EXPLOSION_ROWS = 3;
    static final int EXPLOSION_COL = 3;
    static final int EXPLOSION_H = 128;
    static final int EXPLOSION_STEPS = 15;
    static final Image BOMBS_IMG[] = {
            new Image("/13.png"),
            new Image("/14.png"),
            new Image("/15.png"),
            new Image("/16.png"),
            new Image("/17.png"),
            new Image("/18.png"),
            new Image("/19.png"),
            new Image("/20.png"),
            new Image("/21.png"),
            new Image("/22.png"),
    };
    final int MAX_BOMBS = 10, MAX_SHOTS = MAX_BOMBS * 2;
    static boolean gameOver = false;
    static int score = 0;
    private GraphicsContext gc;
    private List<HighScore> highScores = new ArrayList<>();
    Ship player;
    List<Bullet> shots;
    List<Display> univ;
    private List<ImageView> hearts;
    List<CollisionManager> bombs;
    private List<SpecialBullet> specialBullets;
    private double mouseX;
    Timeline timeline;
    Player p;
    public Galaga(Player p) {
    	this.p = p;
    }
    //panneau du jeu
    public void launch(Stage stage) {
    	Canvas canvas = new Canvas(WIDTH, HEIGHT);
    	stage.setScene(new Scene(new StackPane(canvas)));
    	stage.setTitle("Galaga");
        stage.show();
        gc = canvas.getGraphicsContext2D();
        music();
        timeline = new Timeline(new KeyFrame(Duration.millis(50), e ->{
            run(gc);
            if (shipRespawning ) {
                timeline.stop();
                new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    timeline.playFromStart();
                    return;
                })).play();
                return;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        canvas.setCursor(Cursor.MOVE);
        canvas.setOnMouseMoved(e -> mouseX = e.getX()-WIDTH/26);
        canvas.setOnMouseClicked(e -> handleMouseClick());
        canvas.setOnKeyReleased(e -> handleKeyPress(e.getCode()));
        canvas.setOnKeyPressed(e -> handleKeyPress(e.getCode()));
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        setup();
    }
    //music
    MediaPlayer mediaPlayer;
    public void music() {
      //exception
    	try {
            URL resource = getClass().getResource("/PWAAOST08-Investigation~Cornered.mp3");
            if (resource != null) {
                Media media = new Media(resource.toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setVolume(0.25);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.play();
            } 
        } catch (MediaException e) {
            e.printStackTrace();
        }
    }
    //intialisations des objects utilisé pour etablir le logique du jeu
    private void setup() {
        univ = new ArrayList<>();
        shots = new ArrayList<>();
        bombs = new ArrayList<>();
        specialBullets = new ArrayList<>();
        player = new Ship(WIDTH / 2, HEIGHT - PLAYER_SIZE, PLAYER_SIZE, PLAYER_IMG);
        score = 0;
        IntStream.range(0, MAX_BOMBS).mapToObj(i -> newCollisionManager()).forEach(bombs::add);
        hearts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ImageView heart = new ImageView(new WritableImage(HEART_IMG.getPixelReader(),
                    0, 0, (int) HEART_IMG.getWidth(), (int) HEART_IMG.getHeight()));
            heart.setFitWidth(30);
            heart.setFitHeight(30);
            heart.setLayoutX(10 + i * 35);
            heart.setLayoutY(HEIGHT - 40);
            hearts.add(heart);
        }
    }
    private int remainingLives = 3;//utiliser pour la suppression des 3 coeurs 
    private void resetPlayerShip() {
        player = new Ship(RAND.nextInt(WIDTH - PLAYER_SIZE), HEIGHT - PLAYER_SIZE, PLAYER_SIZE, PLAYER_IMG);
    }
    //mise à jour d'animation du jeu
    private void run(GraphicsContext gc) {
    	
        gc.setFill(Color.grayRgb(20));
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font(20));
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 60, 20);
        Iterator<SpecialBullet> iterator = specialBullets.iterator();
        while (iterator.hasNext()) {
            SpecialBullet specialBullet = iterator.next();
            specialBullet.update();
            specialBullet.draw(gc);

            if (specialBullet.shouldRemove()) {
                iterator.remove();
            }
        }
        
        
        if (gameOver && remainingLives == 0 && !shipRespawning) {
            gc.setFont(Font.font(35));
            gc.setFill(Color.BLUE);
            addHighScore(score);
            displayHighScores();
            gc.fillText("Game Over \n "  + "Score : " + score + " \n Click to play again \n " , WIDTH / 2, HEIGHT / 2.5);
        }
        

        univ.forEach(display -> display.draw(gc));
        
        if (!shipRespawning) {//condition d'avancement du jeu si vous n'avez pas perdu tout les 3 coeurs shipRespawning = False
        player.update();
        player.draw(gc);
        player.posX = (int) mouseX;
        //expression Lambda et utilisation du stream
        bombs.stream().peek(bomb -> {
            bomb.update();
            bomb.draw(gc);
        }).forEach(bomb -> {
            if (player.colide(bomb) && !player.exploding) {
                player.explode();
                remainingLives=remainingLives - 1;
                hearts.remove(hearts.size() - 1);
                if (remainingLives > 0) {
                    shipRespawning = true;
                    respawnStartTime = System.currentTimeMillis();
                }
              }
        } );
        }
        else {
            if (System.currentTimeMillis() - respawnStartTime > RESPAWN_TIME/4) {
                shipRespawning = false;
                resetPlayerShip();
            }
        }
        
        hearts.forEach(heart -> gc.drawImage(HEART_IMG, heart.getLayoutX(), heart.getLayoutY(), 30, 30));//utilisation des coeurs
        for (int i = shots.size() - 1; i >= 0; i--) {
            Bullet shot = shots.get(i);
            if (shot.posY < 0 || shot.toRemove) {
                shots.remove(i);
                continue;
            }
            shot.update();
            shot.draw(gc);
            for (CollisionManager bomb : bombs) {
                if (shot.colide(bomb) && !bomb.exploding) {
                    score++;// incrementation par 1 si vous utilisez la simple jaune balle 
                    bomb.explode();
                    shot.toRemove = true;
                }
            }
        }
        for (int i = specialBullets.size() - 1; i >= 0; i--) {
            SpecialBullet shot = specialBullets.get(i);
            if (shot.posY < 0 || shot.toRemove) {
                shots.remove(i);
                continue;
            }
            shot.update();
            shot.draw(gc);
            for (CollisionManager bomb : bombs) {
                if (shot.colide(bomb) && !bomb.exploding) {
                    score=score+10;// incrementation par 10 si vous utilisez la simple jaune balle 
                    bomb.explode();
                    shot.toRemove = true;
                }
            }
        }

        for (int i = bombs.size() - 1; i >= 0; i--) {
            if (bombs.get(i).destroyed) {
                bombs.set(i, newCollisionManager());
            }
        }
        
        gameOver = player.destroyed ;//condition du finalisation du jeu
        if (RAND.nextInt(10) > 2) {
            univ.add(new Display());
        }
        univ.removeIf(display -> display.posY > HEIGHT);
        
    }
    //appel du nouvelle balle jaune
    private void handleMouseClick() {
        if (shots.size() < MAX_SHOTS) shots.add(player.shoot());
        if (gameOver ) {
        	remainingLives = 3;
            gameOver = false;
            p.setScore(score);
            SaveData.update(p.getName(), p.getScore());
            setup();
        }
    }
    //l'appel du nouvelle balle blue
    private void handleKeyPress(KeyCode code) {
        if (code == KeyCode.SPACE && !gameOver) {
            specialBullets.add(new SpecialBullet(player.posX + player.size / 2 - SpecialBullet.size / 2,
                    player.posY - player.size / 2));
        }
      
    }
   
   //affichage du highscore
    private void displayHighScores() {
        try {
            gc.setFont(Font.font(25));
            gc.setFill(Color.BLUE);
            for (int i = 0; i < Math.min(1, highScores.size()); i++) {
                HighScore score = highScores.get(i);
                String scoreText =  " Highscore : " + score;
                gc.fillText(scoreText, WIDTH / 2, HEIGHT / 2 + 50 + i * 30);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //ajout du score dans la list highScore
    private void addHighScore(int newScore) {
        HighScore newHighScore = new HighScore(newScore);
        highScores.add(newHighScore);
        highScores.sort(Comparator.comparingInt(HighScore::score).reversed());
        if (highScores.size() > 10) {
            highScores.subList(10, highScores.size()).clear();
        }
    }

    
    //appel du nouvelle vaisseau ennemie
    CollisionManager newCollisionManager() {
        return new CollisionManager(50 + RAND.nextInt(WIDTH - 100), 0, PLAYER_SIZE, BOMBS_IMG[RAND.nextInt(BOMBS_IMG.length)]);
    }


}