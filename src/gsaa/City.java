/*
 *  
 */
package gsaa;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 *
 * @author Ammar Sherif
 */
public class City extends Button {

    public static Pane getPane() {
        return pane;
    }

    private String name;
    private double heuristicValue;
    private Stage myPrimaryStage;
    private Label labelName;
    private static ArrayList<Link> links = GSAA.getLinks();
    private long lastTime = 0;
//    private static City lastClickedCity;
    private static Pane pane;
    private double x, y;
    private boolean dragEntered;
//    private static int clickedCount = 0;

    /**
     *
     * @param name name of the City
     * @param heuristicValue the heuristic value specified to the city
     */
    public City(String name, double heuristicValue, Pane pane, Stage stg) {
        this.myPrimaryStage = stg;
        City.pane = pane;
        this.name = name;
        this.heuristicValue = heuristicValue;
        this.labelName = new Label(name);
        this.labelName.setStyle("-fx-background-color: #F5F5DC");
        this.labelName.layoutXProperty().bind(this.layoutXProperty().add(12));
        this.labelName.layoutYProperty().bind(this.layoutYProperty());
        this.labelName.prefHeight(-1);
        this.labelName.prefWidth(-1);
        pane.getChildren().add(this);
        pane.getChildren().add(this.labelName);
        this.getStylesheets().add("./StylingCSS/City.css");
        this.setId("defaultCity");
        this.setOnAction(e -> {
            if (!dragEntered) {
                inputInfo();
            }
        });
        this.setOnMousePressed(event -> {
            x = this.getLayoutX() - event.getSceneX();
            y = this.getLayoutY() - event.getSceneY();
        });
        this.setOnMouseDragged(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (GSAA.getStartCity() != null) {
                    GSAA.getStartCity().setDefaultShape();
                    GSAA.setStartCity(null);
                }
                if (GSAA.getTargetCity() != null) {
                    GSAA.getTargetCity().setDefaultShape();
                    GSAA.setTargetCity(null);
                }
                this.layoutXProperty().unbind();
                this.layoutYProperty().unbind();
                if ((event.getSceneX() + x) < pane.getWidth() && (event.getSceneX() + x) >= 0) {
                    this.setLayoutX(event.getSceneX() + x);
                }
                if ((event.getSceneY() + y) < pane.getHeight() && (event.getSceneY() + y) >= 0) {
                    this.setLayoutY(event.getSceneY() + y);
                }
                this.setCursor(Cursor.MOVE);
                dragEntered = true;
            }
        });
        this.setOnMouseReleased(event -> {
            if (dragEntered) {
                this.setCursor(Cursor.DEFAULT);
                this.layoutXProperty().bind(pane.widthProperty().divide(pane.getWidth())
                        .multiply(this.getLayoutX()));
                this.layoutYProperty().bind(pane.heightProperty().divide(pane.getHeight())
                        .multiply(this.getLayoutY()));
                dragEntered = false;
            }
        });
        this.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                if (GSAA.getStartCity() != null) {
                    GSAA.getStartCity().setDefaultShape();
                    GSAA.setStartCity(null);
                }
                if (GSAA.getTargetCity() != null) {
                    GSAA.getTargetCity().setDefaultShape();
                    GSAA.setTargetCity(null);
                }
                for (int i = 0; i < links.size(); i++) {
                    if (links.get(i).getKey().contains(this.name)) {
                        String[] names = links.get(i).getKey().split(",");
                        if (names[0].equals(this.name) || names[1].equals(this.name)) {
                            links.get(i).delete(pane);
                            i--;
                        }
                    }
                }
                pane.getChildren().remove(labelName);
                pane.getChildren().remove(this);
                GSAA.getCities().remove(this);
            }
        });
    }

    public String getName() {
        return name;
    }

    public double getHeuristicValue() {
        return heuristicValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the heuristic value of the City.
     *
     * @param heuristicValue
     */
    public void setHeuristic(double heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public void setDefaultShape() {
        this.setId("defaultCity");
    }
    //=============================

    private void inputInfo() {
        long current = System.currentTimeMillis();
        long dif = current - this.lastTime;
        if (dif <= 215) {                           //Double click
            if (GSAA.getStartCity() != null) {
                GSAA.getStartCity().setDefaultShape();
                GSAA.setStartCity(null);
            }
            if (GSAA.getTargetCity() != null) {
                GSAA.getTargetCity().setDefaultShape();
                GSAA.setTargetCity(null);
            }
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle(" Edit City ");
            // Set the button types.
            ButtonType enterButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(enterButtonType, ButtonType.CANCEL);

            GridPane gridPane = new GridPane();
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.setPadding(new Insets(10, 10, 10, 10));

            TextField name = new TextField();
            name.setPromptText(" Name");
            name.setText(this.name);
            TextField heuristicValue = new TextField();
            heuristicValue.setPromptText(" number >= 0");
            heuristicValue.setText(String.valueOf(this.heuristicValue));
            gridPane.add(new Label("City Name : "), 0, 0);
            gridPane.add(name, 1, 0);
            gridPane.add(new Label("Heuristic Value :"), 0, 1);
            gridPane.add(heuristicValue, 1, 1);
            gridPane.setStyle("-fx-background-color: #E8E8E8");
            ButtonBar btnBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
            btnBar.setStyle("-fx-background-color: #E8E8E8");
            btnBar.getButtons().forEach(b -> b.setStyle("-fx-background-color: "
                    + "#090a0c,"
                    + "linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),"
                    + "linear-gradient(#20262b, #191d22),"
                    + "radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));"
                    + "-fx-background-radius: 5,4,3,5;"
                    + "-fx-background-insets: 0,1,2,0;"
                    + "-fx-text-fill: white;"
                    + "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
                    + "-fx-font-family: \"Arial\";"
                    + "-fx-text-fill: linear-gradient(white, #d0d0d0);"
                    + "-fx-font-size: 12px;"
                    + "-fx-padding: 10 20 10 20;"
                    + "-fx-effect: dropshadow( one-pass-box , rgba(0,0,0,0.9) , 1, 0.0 , 0 , 1 );"));

            dialog.getDialogPane().setContent(gridPane);

            // Request focus on the City name field by default.
            Platform.runLater(() -> {
                heuristicValue.requestFocus();
                heuristicValue.selectAll();
            });

            Node enterButton = dialog.getDialogPane().lookupButton(enterButtonType);
            enterButton.setDisable(true);

            name.textProperty().addListener((observable, oldValue, newValue) -> {
                if (GSAA.isDouble(heuristicValue.getText().trim()) && Double.parseDouble(heuristicValue.getText().trim()) >= 0) {
                    enterButton.setDisable(newValue.trim().isEmpty() || GSAA.checkCity(newValue));
                }
            });
            heuristicValue.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!name.getText().trim().isEmpty()) {
                    enterButton.setDisable(!GSAA.isDouble(newValue.trim()) || Double.parseDouble(newValue.trim()) < 0);
                }
            });
            // Convert the result to a name and heuristtic pair when the enter button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == enterButtonType) {
                    name.setText(name.getText().trim());
                    return new Pair<>(name.getText().trim(), heuristicValue.getText().trim());
                }
                return null;

            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(pair -> {
                this.labelName.setText(pair.getKey());
                this.heuristicValue = Double.parseDouble(pair.getValue());
                this.name = pair.getKey();
            });
        } else if (GSAA.getStartCity() == this) {   // unmark it and make the target as a start
            this.setDefaultShape();
            if (GSAA.getTargetCity() != null) {
                GSAA.setStartCity(GSAA.getTargetCity());
                GSAA.setTargetCity(null);
            } else {
                GSAA.setStartCity(null);
            }
        } else if (GSAA.getTargetCity() == this) {  // it is the target just unmark it
            this.setDefaultShape();
            GSAA.setTargetCity(null);
        } else if (GSAA.getStartCity() != null && GSAA.getTargetCity() != null) {  // Third City not Allowed
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("More than two Cities");
            alert.setContentText("Sorry but there is already a target city\n"
                    + " If you want to change it, firstly uncheck it.");
            GridPane grid = (GridPane) alert.getDialogPane().lookup(".header-panel");
            grid.setStyle("-fx-background-color: #E8E8E8");
            ButtonBar btnBar = (ButtonBar) alert.getDialogPane().lookup(".button-bar");
            btnBar.setStyle("-fx-background-color: #E8E8E8");
            btnBar.getButtons().forEach(b -> b.setStyle("-fx-background-color: "
                    + "#090a0c,"
                    + "linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),"
                    + "linear-gradient(#20262b, #191d22),"
                    + "radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));"
                    + "-fx-background-radius: 5,4,3,5;"
                    + "-fx-background-insets: 0,1,2,0;"
                    + "-fx-text-fill: white;"
                    + "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
                    + "-fx-font-family: \"Arial\";"
                    + "-fx-text-fill: linear-gradient(white, #d0d0d0);"
                    + "-fx-font-size: 12px;"
                    + "-fx-padding: 10 20 10 20;"
                    + "-fx-effect: dropshadow( one-pass-box , rgba(0,0,0,0.9) , 1, 0.0 , 0 , 1 );"));
            alert.getDialogPane().setStyle("-fx-background-color: #E8E8E8");
            alert.showAndWait();
        } else if (GSAA.getStartCity() == null && GSAA.getTargetCity() == null) {  // no start => mark it
            GSAA.setStartCity(this);
            this.setId("markedCity");
        } else {          //There is some start and this is the target
            GSAA.setTargetCity(this);
            this.setId("markedCity");
            // Make new link or mark start or target?
            Alert alrt = new Alert(AlertType.CONFIRMATION, "You want to make a link?\n  No to specify start and end nodes",
                    ButtonType.YES, ButtonType.NO);
            alrt.setTitle("Confirm to make a link");
            alrt.setContentText("You want to make a link?\n  No to specify start and end nodes");
            GridPane gb = (GridPane) alrt.getDialogPane().lookup(".header-panel");
            gb.setStyle("-fx-background-color: #E8E8E8");
            ButtonBar btnBr = (ButtonBar) alrt.getDialogPane().lookup(".button-bar");
            btnBr.setStyle("-fx-background-color: #E8E8E8");
            btnBr.getButtons().forEach(b -> b.setStyle("-fx-background-color: "
                    + "#090a0c,"
                    + "linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),"
                    + "linear-gradient(#20262b, #191d22),"
                    + "radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));"
                    + "-fx-background-radius: 5,4,3,5;"
                    + "-fx-background-insets: 0,1,2,0;"
                    + "-fx-text-fill: white;"
                    + "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
                    + "-fx-font-family: \"Arial\";"
                    + "-fx-text-fill: linear-gradient(white, #d0d0d0);"
                    + "-fx-font-size: 12px;"
                    + "-fx-padding: 10 20 10 20;"
                    + "-fx-effect: dropshadow( one-pass-box , rgba(0,0,0,0.9) , 1, 0.0 , 0 , 1 );"));
            alrt.getDialogPane().setStyle("-fx-background-color: #E8E8E8");
            alrt.showAndWait();
            if (alrt.getResult() == ButtonType.YES) {     // make a link
                if (checkLink()) {                        // Link is already existed
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Existing Link");
                    alert.setContentText("Sorry but there is already a link between the cities");
                    GridPane grid = (GridPane) alert.getDialogPane().lookup(".header-panel");
                    grid.setStyle("-fx-background-color: #E8E8E8");
                    ButtonBar btnBar = (ButtonBar) alert.getDialogPane().lookup(".button-bar");
                    btnBar.setStyle("-fx-background-color: #E8E8E8");
                    btnBar.getButtons().forEach(b -> b.setStyle("-fx-background-color: "
                            + "#090a0c,"
                            + "linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),"
                            + "linear-gradient(#20262b, #191d22),"
                            + "radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));"
                            + "-fx-background-radius: 5,4,3,5;"
                            + "-fx-background-insets: 0,1,2,0;"
                            + "-fx-text-fill: white;"
                            + "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
                            + "-fx-font-family: \"Arial\";"
                            + "-fx-text-fill: linear-gradient(white, #d0d0d0);"
                            + "-fx-font-size: 12px;"
                            + "-fx-padding: 10 20 10 20;"
                            + "-fx-effect: dropshadow( one-pass-box , rgba(0,0,0,0.9) , 1, 0.0 , 0 , 1 );"));

                    alert.getDialogPane().setStyle("-fx-background-color: #E8E8E8");
                    alert.showAndWait();
                } else {        // Link is not existed, Create it
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Enter new path");
                    // Set the button types.
                    ButtonType enterButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().addAll(enterButtonType, ButtonType.CANCEL);

                    GridPane gridPane = new GridPane();
                    gridPane.setHgap(5);
                    gridPane.setVgap(5);
                    gridPane.setPadding(new Insets(10, 10, 10, 10));

                    TextField cost = new TextField();
                    cost.setPromptText(" number >= 0");
                    gridPane.add(new Label(" Cost : "), 0, 0);
                    gridPane.add(cost, 1, 0);
                    gridPane.setStyle("-fx-background-color: #E8E8E8");
                    ButtonBar btnBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
                    btnBar.setStyle("-fx-background-color: #E8E8E8");
                    btnBar.getButtons().forEach(b -> b.setStyle("-fx-background-color: "
                            + "#090a0c,"
                            + "linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),"
                            + "linear-gradient(#20262b, #191d22),"
                            + "radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));"
                            + "-fx-background-radius: 5,4,3,5;"
                            + "-fx-background-insets: 0,1,2,0;"
                            + "-fx-text-fill: white;"
                            + "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
                            + "-fx-font-family: \"Arial\";"
                            + "-fx-text-fill: linear-gradient(white, #d0d0d0);"
                            + "-fx-font-size: 12px;"
                            + "-fx-padding: 10 20 10 20;"
                            + "-fx-effect: dropshadow( one-pass-box , rgba(0,0,0,0.9) , 1, 0.0 , 0 , 1 );"));
//                      
                    dialog.getDialogPane().setContent(gridPane);

                    // Request focus on the cost field by default.
                    Platform.runLater(() -> cost.requestFocus());

                    Node enterButton = dialog.getDialogPane().lookupButton(enterButtonType);
                    enterButton.setDisable(true);

                    cost.textProperty().addListener((observable, oldValue, newValue) -> {

                        enterButton.setDisable(!GSAA.isDouble(newValue) || Double.parseDouble(newValue) < 0);
                    });

                    // Convert the result to String when the submit button is clicked.
                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == enterButtonType) {
                            cost.setText(cost.getText().trim());
                            return cost.getText().trim();
                        }
                        return null;

                    });

                    Optional<String> result = dialog.showAndWait();

                    result.ifPresent(pair -> {
                        int source = GSAA.findCityIndex(GSAA.getStartCity());
                        int destination = GSAA.findCityIndex(GSAA.getTargetCity());
                        if (source >= 0 && destination >= 0) {
                            GSAA.addOnOrder(links, GSAA.linkCompare, new Link(Double.parseDouble(pair),
                                    source, destination, City.pane));
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error occured");
                            alert.setContentText("Sorry but some error occured");
                            GridPane g = (GridPane) alert.getDialogPane().lookup(".header-panel");
                            g.setStyle("-fx-background-color: #E8E8E8");
                            ButtonBar btnBar2 = (ButtonBar) alert.getDialogPane().lookup(".button-bar");
                            btnBar2.setStyle("-fx-background-color: #E8E8E8");
                            btnBar2.getButtons().forEach(b -> b.setStyle("-fx-background-color: "
                                    + "#090a0c,"
                                    + "linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),"
                                    + "linear-gradient(#20262b, #191d22),"
                                    + "radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));"
                                    + "-fx-background-radius: 5,4,3,5;"
                                    + "-fx-background-insets: 0,1,2,0;"
                                    + "-fx-text-fill: white;"
                                    + "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
                                    + "-fx-font-family: \"Arial\";"
                                    + "-fx-text-fill: linear-gradient(white, #d0d0d0);"
                                    + "-fx-font-size: 12px;"
                                    + "-fx-padding: 10 20 10 20;"
                                    + "-fx-effect: dropshadow( one-pass-box , rgba(0,0,0,0.9) , 1, 0.0 , 0 , 1 );"));
                            alert.getDialogPane().setStyle("-fx-background-color: #E8E8E8");
                            alert.showAndWait();
                        }
                    });
                }
                GSAA.getStartCity().setDefaultShape();
                GSAA.getTargetCity().setDefaultShape();                 // There would be no marked cities
                GSAA.setStartCity(null);
                GSAA.setTargetCity(null);                               // There would be no marked cities
            } else if (alrt.getResult() == ButtonType.NO) {
                //Nothing to do as I setThe city as target before and marked it
            }
        }
        this.lastTime = current;
    }
    //=============================

    public boolean checkLink() {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).getKey().contains(GSAA.getStartCity().name) && links.get(i).getKey().contains(GSAA.getTargetCity().getName())) {
                return true;
            }
        }
        return false;
    }

    //=============================
    public double distance(City c) {
        double x = this.getLayoutX() - c.getLayoutX();
        double y = this.getLayoutY() - c.getLayoutY();
        x *= x;
        y *= y;
        return Math.sqrt((x + y));
    }

}
