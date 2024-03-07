package application;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
public class Controller implements Initializable {

    @FXML private VBox container ;
    @FXML private Label newGameLabel;
    @FXML private Label scoreboardLabel;
    @FXML private Label exitLabel;
    
    private Label[] menuItems;

    private int selectedIndex;

    TableView<Player> tableView;

    Popup popup;

    public Controller()  {
        
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        popup = new Popup();
        tableView = new TableView<>();
        createTableView();
        menuItems = new Label[]{newGameLabel, scoreboardLabel, exitLabel};
        selectedIndex = 0;
        updateSelection();
        for (Label menuItem : menuItems) {
            menuItem.setFocusTraversable(true);
        }
        menuItems[selectedIndex].requestFocus(); 
    }


    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            moveUP();
        } else if (event.getCode() == KeyCode.DOWN) {
            moveDOWN();
        } else if (event.getCode() == KeyCode.ENTER) {
            handleMenuItemAction(selectedIndex);
        }
    }


    private void moveUP() {

        selectedIndex = (selectedIndex -1) ;
        if (selectedIndex < 0) selectedIndex = menuItems.length -1 ;
        updateSelection();
        
    }


    private void moveDOWN() {
        
        selectedIndex = selectedIndex +1 ;
        if (selectedIndex >= menuItems.length) selectedIndex = 0;
        updateSelection();
        
    }


    private void updateSelection() {
        for (int i = 0; i < menuItems.length; i++) {
            if (i == selectedIndex) {
                menuItems[i].setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
            } else {
                menuItems[i].setStyle("-fx-text-fill: white;");
            }
        }
    }

    
    @FXML
    private void handleMenuItemAction(int selectedIndex) {
        switch (selectedIndex) {
            case 0:
            	addPlayer();
                break;
            case 1:
            	selectPlayer();
                break;
            case 2:
                Platform.exit();
            default:
                break;
        }

    }


    public void newGame(Player player) {

        Stage stage = (Stage) container.getScene().getWindow();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - Galaga.WIDTH) / 2;
        double centerY = (screenBounds.getHeight() - Galaga.HEIGHT) / 2;
        
        stage.setX(centerX);
        stage.setY(centerY);

        Galaga galaga = new Galaga(player);
        galaga.launch(stage);
        
    }


    public void addPlayer() {Popup popup = new Popup();

        TextField textField = new TextField();

        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                if (e.getCode() == KeyCode.ENTER) {
                    popup.hide();
                    Player player = new Player(textField.getText(), 0);
                    SaveData.write(player);
                    tableView.getItems().add(player);
                    newGame(player);
                }
            }
        });

        VBox popupLayout = new VBox(10); 
        popupLayout.setAlignment(Pos.CENTER);
        popupLayout.getChildren().addAll(textField);

        popupLayout.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                KeyCode key = e.getCode();
                if (key == KeyCode.ESCAPE || key == KeyCode.LEFT) {
                    popup.hide();
                }
            }
        });

        popup.getContent().add(popupLayout);

        popupLayout.setMinSize(120, 200);
        popupLayout.setMaxSize(200, 148);   
        popupLayout.setStyle("-fx-background-color: rgba(1, 3, 0, 0); -fx-background-radius: 5;");         
        popup.setAutoHide(true); 
        
        Stage stage = (Stage) container.getScene().getWindow();
        double ownerX = stage.getX();
        double ownerY = stage.getY();
        double ownerWidth = stage.getWidth();
        double ownerHeight = stage.getHeight();
        
        popup.show(stage, ownerX + ownerWidth / 1.55, ownerY + ownerHeight /2.1);

    }

    
    public void selectPlayer() {
    	
    	VBox popupLayout = new VBox(10); 
        popupLayout.setAlignment(Pos.CENTER);
        popupLayout.getChildren().addAll(tableView);

        popupLayout.requestFocus();

        popupLayout.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                KeyCode key = e.getCode();
                if (key == KeyCode.ESCAPE || key == KeyCode.LEFT) {
                    popup.hide();
                }
            }
        });

        popup.getContent().add(popupLayout);

        popupLayout.setMinSize(150, 50);
        popupLayout.setMaxSize(200, 148);   
        popupLayout.setStyle("-fx-background-color: rgba(1, 25, 100, 200); -fx-background-radius: 25;");         
        popup.setAutoHide(true); 
        
        Stage stage = (Stage) container.getScene().getWindow();
        double ownerX = stage.getX();
        double ownerY = stage.getY();
        double ownerWidth = stage.getWidth();
        double ownerHeight = stage.getHeight();
        
        popup.show(stage, ownerX + ownerWidth / 2, ownerY + ownerHeight / 2);

    }


    private void createTableView() {

        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Player, String> nameColumn = new TableColumn<>("NAME");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Player, Integer> scoreColumn = new TableColumn<>("SCORE");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        nameColumn.setStyle("-fx-alignment: CENTER; -fx-background-color: rgba(1, 2, 55, 0.6);");
        scoreColumn.setStyle("-fx-alignment: CENTER; -fx-background-color: rgba(1, 2, 55, 0.6);");

        tableView.getColumns().addAll(nameColumn, scoreColumn);

        Player[] playersArray =SaveData.read().entrySet().stream()
        .map(entry -> new Player(entry.getKey(), entry.getValue()))
        .toArray(Player[]::new);

        tableView.getItems().addAll(playersArray);

        tableView.getSelectionModel().selectFirst();

    }



    
}