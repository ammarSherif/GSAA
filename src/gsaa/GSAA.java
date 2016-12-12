/*
 *
 */
package gsaa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 *
 * @author Ammar Sherif
 */
public class GSAA extends Application {

    private static City start;
    private static City target;
    private static String lastChoice = " ";
    private boolean stopFlag;
    private Thread runThread;
    private long lastTime = 0;
    private long currentTime = 0;

    private final ArrayList<Path> paths = new ArrayList<Path>();
    private final ArrayList<String> extendedCities = new ArrayList<String>();
    private final static ArrayList<City> cities = new ArrayList<City>(20);
    private ArrayList<SerializableCityInfo> serializedCities = new ArrayList<SerializableCityInfo>(20);
    private ArrayList<SerializableLinkInfo> serializedLinks = new ArrayList<SerializableLinkInfo>(20);
    private final static ArrayList<Link> links = new ArrayList<Link>(23);
    public final static Comparator<Link> linkCompare = new Comparator<Link>() {
        @Override
        public int compare(Link o1, Link o2) {
            return new Double(o1.getSlope()).compareTo(new Double(o2.getSlope()));
        }
    };
    public final static Comparator<Path> pathCompare = new Comparator<Path>() {
        @Override
        public int compare(Path o1, Path o2) {
            return new Double(o1.getLastHeuristic()).compareTo(new Double(o2.getLastHeuristic()));
        }
    };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        stopFlag = false;

        primaryStage.setTitle("Graph Search Algorithm Analyzer [GSAA]");
        final Label algChoose = new Label("Choose the Algorithm : ");
        final ComboBox algorithmType = new ComboBox();
        algorithmType.getItems().setAll("A Start [A*]", "Depth First Search [DFS]",
                "Depth Limited Search [DLS]", "Breadth First Search [BFS]", "Uniform Cost [Cheapest First]",
                "Beam Search", "Hill Climbing Search");
        Button buttonSolve = new Button("Solve");
        GridPane.setConstraints(buttonSolve, 2, 0);

        Button buttonNew = new Button("New Graph");
        GridPane.setConstraints(buttonNew, 3, 0);

        Button buttonSave = new Button("Save Graph");
        GridPane.setConstraints(buttonSave, 4, 0);

        Button buttonOpen = new Button("Open Graph");
        GridPane.setConstraints(buttonOpen, 5, 0);

        Button buttonPause = new Button("Pause");
        GridPane.setConstraints(buttonPause, 6, 0);
        buttonPause.setDisable(true);

        Button buttonStop = new Button("Stop");
        GridPane.setConstraints(buttonStop, 7, 0);
        buttonStop.setDisable(true);

        Button buttonGenerateH = new Button("Generate Heuristic");
        GridPane.setConstraints(buttonGenerateH, 8, 0);

        algorithmType.getSelectionModel().select(0);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(10);
        GridPane.setConstraints(algChoose, 0, 0);
        GridPane.setConstraints(algorithmType, 1, 0);

        grid.getChildren().setAll(algChoose, algorithmType, buttonSolve, buttonNew, buttonSave, buttonOpen,
                buttonPause, buttonStop, buttonGenerateH);
        TitledPane algorithmControl = new TitledPane();
        algorithmControl.setText("Controls ");
        algorithmControl.setContent(grid);

        TitledPane infoSection = new TitledPane();
        infoSection.setText("Information while running the algorithm");
        //AlgorithmInfo.setStyle(">.title{-fx-background-color : #3e3e3e}");
        infoSection.setExpanded(false);
        //infoSection.setCollapsible(false);

        BorderPane mainLayout = new BorderPane();
        Pane pane = new Pane();
        mainLayout.setTop(algorithmControl);
        mainLayout.setCenter(pane);
        mainLayout.setBottom(infoSection);
        //mainLayout.addColumn(0,AlgorithmInfo,pane );
        Scene scene = new Scene(mainLayout, 1098, 600);
        buttonSolve.setOnAction(event -> {
            if (GSAA.getStartCity() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No start City!");
                alert.setContentText("Sorry but There is no start City\n"
                        + " Please Specify it first by clicking it");
                GridPane g = (GridPane) alert.getDialogPane().lookup(".header-panel");
                g.setStyle("-fx-background-color: #E8E8E8");
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
                return;
            }
            switch (lastChoice) {
                case "A*":
                    if (getMinPath() != null) {
                        getMinPath().leave();
                    }
                    break;
                case "DFS":
                    if (paths.size() >= 1) {
                        paths.get(0).leave();
                    }
                    break;
                case "DLS":
                    if (paths.size() >= 1) {
                        paths.get(0).leave();
                    }
                    break;
                case "BFS":
                    if (paths.size() >= 1) {
                        paths.get(paths.size() - 1).leave();
                    }
                    break;
                case "UCost":
                    if (getMinPath() != null) {
                        getMinPath().leave();
                    }
                    break;
                case "Beam":
                    if (paths.size() >= 1) {
                        paths.get(paths.size() - 1).leave();
                    }
                    break;
                case "Hill":
                    if (paths.size() >= 1) {
                        paths.get(paths.size() - 1).leave();
                    }
                    break;
                default:
                    break;
            }
            if (!paths.isEmpty() && !stopFlag) {
                paths.clear();
            }
            if (!extendedCities.isEmpty() && !stopFlag) {
                extendedCities.clear();
            }
            stopFlag = false;
            buttonNew.setDisable(true);
            buttonSave.setDisable(true);
            buttonOpen.setDisable(true);
            buttonPause.setDisable(false);
            buttonStop.setDisable(false);
            buttonGenerateH.setDisable(true);
            buttonSolve.setDisable(true);
            algorithmType.setDisable(true);
            pane.setDisable(true);
            if (algorithmType.getValue().toString().contains("A*")) {   //A*
                lastChoice = "A*";
                runThread = new Thread(() -> {
                    costSearch(true, true);
                    if (!stopFlag) {
                        pane.setDisable(false);
                        buttonNew.setDisable(false);
                        buttonSave.setDisable(false);
                        buttonOpen.setDisable(false);
                        buttonPause.setDisable(true);
                        buttonStop.setDisable(true);
                        buttonGenerateH.setDisable(false);
                        buttonSolve.setDisable(false);
                        algorithmType.setDisable(false);
                    }
                });
                runThread.start();
            } else if (algorithmType.getValue().toString().contains("DFS")) {   //Depth first 
                if (GSAA.getTargetCity() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("No target City!");
                    alert.setContentText("Sorry but There is no target City\n"
                            + " Please Specify it first by clicking it");
                    GridPane g = (GridPane) alert.getDialogPane().lookup(".header-panel");
                    g.setStyle("-fx-background-color: #E8E8E8");
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
                    return;
                }
                lastChoice = "DFS";
                runThread = new Thread(() -> {
                    DepthAndBreadth(0, true, 0);
                    if (!stopFlag) {
                        pane.setDisable(false);
                        buttonNew.setDisable(false);
                        buttonSave.setDisable(false);
                        buttonOpen.setDisable(false);
                        buttonPause.setDisable(true);
                        buttonStop.setDisable(true);
                        buttonGenerateH.setDisable(false);
                        buttonSolve.setDisable(false);
                        algorithmType.setDisable(false);
                    }
                });
                runThread.start();
            } else if (algorithmType.getValue().toString().contains("DLS")) {   // Depth limited
                if (GSAA.getTargetCity() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("No target City!");
                    alert.setContentText("Sorry but There is no target City\n"
                            + " Please Specify it first by clicking it");
                    GridPane g = (GridPane) alert.getDialogPane().lookup(".header-panel");
                    g.setStyle("-fx-background-color: #E8E8E8");
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
                    return;
                }
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Enter The level");
                // Set the button types.
                ButtonType enterButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(enterButtonType, ButtonType.CANCEL);

                GridPane gridPane = new GridPane();
                gridPane.setHgap(5);
                gridPane.setVgap(5);
                gridPane.setPadding(new Insets(10, 10, 10, 10));

                TextField cost = new TextField();
                cost.setPromptText(" Level number > 0");
                cost.selectAll();
                gridPane.add(new Label(" Max level : "), 0, 0);
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
                dialog.getDialogPane().setContent(gridPane);

                // Request focus on the cost field by default.
                Platform.runLater(() -> cost.requestFocus());

                Node enterButton = dialog.getDialogPane().lookupButton(enterButtonType);
                enterButton.setDisable(true);

                cost.textProperty().addListener((observable, oldValue, newValue) -> {

                    enterButton.setDisable(!GSAA.isInteger(newValue) || Integer.parseInt(newValue) < 1);
                });

                // Convert the result to String when the login button is clicked.
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == enterButtonType) {
                        return cost.getText();
                    }
                    return null;
                });

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(pair -> {
                    lastChoice = "DLS";
                    runThread = new Thread(() -> {
                        DepthAndBreadth(Integer.parseInt(pair), true, 0);
                        if (!stopFlag) {
                            pane.setDisable(false);
                            buttonNew.setDisable(false);
                            buttonSave.setDisable(false);
                            buttonOpen.setDisable(false);
                            buttonPause.setDisable(true);
                            buttonStop.setDisable(true);
                            buttonGenerateH.setDisable(false);
                            buttonSolve.setDisable(false);
                            algorithmType.setDisable(false);
                        }
                    });
                    runThread.start();
                });
            } else if (algorithmType.getValue().toString().contains("BFS")) {   //Breadth
                if (GSAA.getTargetCity() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("No target City!");
                    alert.setContentText("Sorry but There is no target City\n"
                            + " Please Specify it first by clicking it");
                    GridPane g = (GridPane) alert.getDialogPane().lookup(".header-panel");
                    g.setStyle("-fx-background-color: #E8E8E8");
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
                    return;
                }
                lastChoice = "BFS";
                runThread = new Thread(() -> {
                    DepthAndBreadth(0, false, 0);  //false to specify Breadth
                    if (!stopFlag) {
                        pane.setDisable(false);
                        buttonNew.setDisable(false);
                        buttonSave.setDisable(false);
                        buttonOpen.setDisable(false);
                        buttonPause.setDisable(true);
                        buttonStop.setDisable(true);
                        buttonGenerateH.setDisable(false);
                        buttonSolve.setDisable(false);
                        algorithmType.setDisable(false);
                    }
                });
                runThread.start();
            } else if (algorithmType.getValue().toString().contains("Cost")) {  //Cheapest
                if (GSAA.getTargetCity() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("No target City!");
                    alert.setContentText("Sorry but There is no target City\n"
                            + " Please Specify it first by clicking it");
                    GridPane g = (GridPane) alert.getDialogPane().lookup(".header-panel");
                    g.setStyle("-fx-background-color: #E8E8E8");
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
                    return;
                }
                lastChoice = "UCost";
                runThread = new Thread(() -> {
                    costSearch(true, false);
                    if (!stopFlag) {
                        pane.setDisable(false);
                        buttonNew.setDisable(false);
                        buttonSave.setDisable(false);
                        buttonOpen.setDisable(false);
                        buttonPause.setDisable(true);
                        buttonStop.setDisable(true);
                        buttonGenerateH.setDisable(false);
                        buttonSolve.setDisable(false);
                        algorithmType.setDisable(false);
                    }
                });
                runThread.start();
            } else if (algorithmType.getValue().toString().contains("Beam")) {  //Beam
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Enter The beam size");
                // Set the button types.
                ButtonType enterButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(enterButtonType, ButtonType.CANCEL);

                GridPane gridPane = new GridPane();
                gridPane.setHgap(5);
                gridPane.setVgap(5);
                gridPane.setPadding(new Insets(10, 10, 10, 10));

                TextField cost = new TextField();
                cost.setPromptText(" size number > 1");
                cost.selectAll();
                gridPane.add(new Label(" Beam size : "), 0, 0);
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
                dialog.getDialogPane().setContent(gridPane);
                // Request focus on the cost field by default.
                Platform.runLater(() -> cost.requestFocus());

                Node enterButton = dialog.getDialogPane().lookupButton(enterButtonType);
                enterButton.setDisable(true);

                cost.textProperty().addListener((observable, oldValue, newValue) -> {

                    enterButton.setDisable(!GSAA.isInteger(newValue) || Integer.parseInt(newValue) < 2);
                });

                // Convert the result to String when the login button is clicked.
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == enterButtonType) {
                        return cost.getText();
                    }
                    return null;
                });

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(pair -> {
                    lastChoice = "Beam";
                    runThread = new Thread(() -> {
                        DepthAndBreadth(0, false, Integer.parseInt(pair));
                        if (!stopFlag) {
                            pane.setDisable(false);
                            buttonNew.setDisable(false);
                            buttonSave.setDisable(false);
                            buttonOpen.setDisable(false);
                            buttonPause.setDisable(true);
                            buttonStop.setDisable(true);
                            buttonGenerateH.setDisable(false);
                            buttonSolve.setDisable(false);
                            algorithmType.setDisable(false);
                        }
                    });
                    runThread.start();
                });
            } else if (algorithmType.getValue().toString().contains("Hill")) {  // Hill
                lastChoice = "Hill";
                runThread = new Thread(() -> {
                    DepthAndBreadth(0, false, 1);
                    if (!stopFlag) {
                        pane.setDisable(false);
                        buttonNew.setDisable(false);
                        buttonSave.setDisable(false);
                        buttonOpen.setDisable(false);
                        buttonPause.setDisable(true);
                        buttonStop.setDisable(true);
                        buttonGenerateH.setDisable(false);
                        buttonSolve.setDisable(false);
                        algorithmType.setDisable(false);
                    }
                });
                runThread.start();
            }
        });

        buttonStop.setOnAction(event -> {
            stopFlag = true;
            buttonStop.setDisable(true);
            buttonPause.setDisable(true);
            buttonPause.setText("Pause");
            new Thread(() -> {
                while (runThread.isAlive());
                stopFlag = false;
                pane.setDisable(false);
                buttonNew.setDisable(false);
                buttonSave.setDisable(false);
                buttonOpen.setDisable(false);
                buttonGenerateH.setDisable(false);
                buttonSolve.setDisable(false);
                algorithmType.setDisable(false);
            }).start();
        });

        buttonPause.setOnAction(event -> {
            if (stopFlag) {
                buttonSolve.setDisable(false);
                Platform.runLater(() -> buttonSolve.setDisable(true));
                buttonSolve.fire();
                buttonPause.setText("Pause");
            } else {
                stopFlag = true;
                buttonPause.setText("Resume");
                buttonPause.setDisable(true);
                new Thread(() -> {
                    while (runThread.isAlive());
                    buttonPause.setDisable(false);
                }).start();
            }
        });

        buttonNew.setOnAction((ActionEvent event) -> {
            pane.getChildren().clear();
            cities.clear();
            serializedCities.clear();
            extendedCities.clear();
            links.clear();
            GSAA.setStartCity(null);
            GSAA.setTargetCity(null);
        });

        buttonSave.setOnAction((ActionEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter ext = new FileChooser.ExtensionFilter("GSAA files (*.gsaa)", "*.gsaa");
            fileChooser.getExtensionFilters().add(ext);

            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                initializeSerialization(pane);
                ArrayList<ArrayList> array = new ArrayList<ArrayList>();
                array.add(serializedCities);
                array.add(serializedLinks);
                try {
                    String path = (file.getCanonicalPath().contains(".gsaa")) ? file.getCanonicalPath() : file.getCanonicalPath() + ".gsaa";
                    FileOutputStream fileOut = new FileOutputStream(path);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(array);
                    out.close();
                    fileOut.close();
                } catch (FileNotFoundException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error happened");
                    alert.setContentText("Sorry but some error happened during the operation");
                    GridPane g = (GridPane) alert.getDialogPane().lookup(".header-panel");
                    g.setStyle("-fx-background-color: #E8E8E8");
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
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error happened");
                    alert.setContentText("Sorry but some error happened during the operation");
                    GridPane g = (GridPane) alert.getDialogPane().lookup(".header-panel");
                    g.setStyle("-fx-background-color: #E8E8E8");
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
                }
            }
        });

        buttonOpen.setOnAction(event -> {

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter ext = new FileChooser.ExtensionFilter("GSAA files (*.gsaa)", "*.gsaa");
            fileChooser.getExtensionFilters().add(ext);

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                Object obj;
                ArrayList<ArrayList> array;
                try {
                    FileInputStream fileIn = new FileInputStream(file.getCanonicalPath());
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    obj = in.readObject();
                    in.close();
                    fileIn.close();

                    buttonNew.fire();

                    array = (ArrayList<ArrayList>) obj;
                    serializedCities = (ArrayList<SerializableCityInfo>) array.get(0);
                    serializedLinks = (ArrayList<SerializableLinkInfo>) array.get(1);

                    for (int i = 0; i < serializedCities.size(); i++) {
                        cities.add(new City(serializedCities.get(i).getCityName(), serializedCities.get(i).getHeuristicValue(),
                                pane, primaryStage));
                    }
                    for (int i = 0; i < cities.size(); i++) {
                        cities.get(i).layoutXProperty().bind(pane.widthProperty().divide(serializedCities.get(i).getxDivBind())
                                .multiply(serializedCities.get(i).getxMulBind()));
                        cities.get(i).layoutYProperty().bind(pane.heightProperty().subtract(serializedCities.get(i).getySubBind())
                                .divide(serializedCities.get(i).getyDivBind()).multiply(serializedCities.get(i).getyMulBind()));
                    }
                    for (int i = 0; i < serializedLinks.size(); i++) {
                        links.add(new Link(serializedLinks.get(i).getCost(), serializedLinks.get(i).getSourceIndex(),
                                serializedLinks.get(i).getDestinationIndex(), pane));
                    }
                } catch (FileNotFoundException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Corrupted File");
                    alert.setContentText("Sorry but the file was corrupted");
                    GridPane g = (GridPane) alert.getDialogPane().lookup(".header-panel");
                    g.setStyle("-fx-background-color: #E8E8E8");
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
                } catch (IOException | ClassNotFoundException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Corrupted File");
                    alert.setContentText("Sorry but the file was corrupted");
                    GridPane g = (GridPane) alert.getDialogPane().lookup(".header-panel");
                    g.setStyle("-fx-background-color: #E8E8E8");
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
                }
            }

        });

        buttonGenerateH.setOnAction(event -> {
            Alert alrt = new Alert(Alert.AlertType.WARNING, "", ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);
            alrt.setHeight(60);
            alrt.setTitle("Generate Heuristics to Cities");
            alrt.setContentText("Note all Heuristics will be cleared and another one would be set"
                    + "\n Also There is no guarantee that the generated heuristic"
                    + "\n  would be neither admissible nor consistent"
                    + "\n The heuristic is based on the euclidean distance"
                    + "\n  multiplied by some const generated according to\n  your graph."
                    + "\n Note 'no' to enter all heuristics one by one");
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
            alrt.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alrt.showAndWait();
            if (alrt.getResult() == ButtonType.YES) {
                if (GSAA.getTargetCity() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("No target");
                    alert.setContentText("Sorry but there is no target city\n"
                            + " I could not specify heuristic to no city.");
                    GridPane g = (GridPane) alert.getDialogPane().lookup(".header-panel");
                    g.setStyle("-fx-background-color: #E8E8E8");
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
                } else {
                    for (int i = 0; i < cities.size(); i++) {
                        cities.get(i).setHeuristic(cities.get(i).distance(GSAA.getTargetCity()) * minScale());
                    }
                }
            } else if (alrt.getResult() == ButtonType.NO) {
                if (GSAA.getStartCity() != null) {
                    GSAA.getStartCity().setDefaultShape();
                    GSAA.setStartCity(null);
                }
                if (GSAA.getTargetCity() != null) {
                    GSAA.getTargetCity().setDefaultShape();
                    GSAA.setTargetCity(null);
                }
                for (int i = 0; i < cities.size(); i++) {
                    cities.get(i).fire();
                    cities.get(i).fire();
                }
            }
        });

        pane.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                long dif;
                currentTime = System.currentTimeMillis();
                if (lastTime != 0) {
                    dif = currentTime - lastTime;
                    if (dif <= 215) {
                        // Create a dialog.
                        Dialog<Pair<String, String>> dialog = new Dialog<>();
                        dialog.setTitle("New City");
                        // Set the button types.
                        ButtonType enterButtonType = new ButtonType("Submit", ButtonData.OK_DONE);
                        dialog.getDialogPane().getButtonTypes().addAll(enterButtonType, ButtonType.CANCEL);

                        GridPane gridPane = new GridPane();
                        gridPane.setHgap(5);
                        gridPane.setVgap(5);
                        gridPane.setPadding(new Insets(10, 10, 10, 10));

                        TextField name = new TextField();
                        name.setPromptText(" Name");
                        TextField heuristicValue = new TextField();
                        heuristicValue.setPromptText(" number >= 0");

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
//                      
                        dialog.getDialogPane().setContent(gridPane);

                        // Request focus on the City name field by default.
                        Platform.runLater(() -> name.requestFocus());

                        Node enterButton = dialog.getDialogPane().lookupButton(enterButtonType);
                        enterButton.setDisable(true);

                        name.textProperty().addListener((observable, oldValue, newValue) -> {
                            if (isDouble(heuristicValue.getText().trim()) && Double.parseDouble(heuristicValue.getText().trim()) >= 0) {
                                enterButton.setDisable(newValue.trim().isEmpty() || checkCity(newValue));
                            }
                        });
                        heuristicValue.textProperty().addListener((observable, oldValue, newValue) -> {
                            if (!name.getText().trim().isEmpty()) {
                                enterButton.setDisable(!isDouble(newValue.trim()) || Double.parseDouble(newValue.trim()) < 0);
                            }
                        });
                        // Convert the result to a name and heuristtic pair when the enter button is clicked.
                        dialog.setResultConverter(dialogButton -> {
                            if (dialogButton == enterButtonType) {
                                return new Pair<>(name.getText().trim(), heuristicValue.getText().trim());
                            }
                            return null;

                        });

                        Optional<Pair<String, String>> result = dialog.showAndWait();

                        result.ifPresent(pair -> {
                            cities.add(new City(pair.getKey(), Double.parseDouble(pair.getValue()), pane, primaryStage));
                            cities.get(cities.size() - 1).layoutXProperty().bind(pane.widthProperty().divide(pane.getWidth())
                                    .multiply(event.getX()));
                            cities.get(cities.size() - 1).layoutYProperty().bind(pane.heightProperty().divide(pane.getHeight())
                                    .multiply(event.getY()));
                        });
                    }
                }
                lastTime = currentTime;
            }

        });

        this.intitialization(primaryStage, pane, scene);

        final Stage st = primaryStage;
        final Pane pn = pane;
        final Scene sc = scene;

    }

    private void intitialization(Stage primaryStage, Pane pane, Scene scene) {

        final Label reporter = new Label("Outside the layout");
        //=====================================================

        //Label monitored = createMonitoredLabel(reporter);
        //Make your layout buttons + basic layout elements
        //=====================================================
        cities.add(new City("Arad", 366, pane, primaryStage));
        cities.add(new City("Timisoara", 329, pane, primaryStage));
        cities.add(new City("Zerind", 374, pane, primaryStage));
        cities.add(new City("Sibiu", 253, pane, primaryStage));
        cities.add(new City("Oradea", 380, pane, primaryStage));
        cities.add(new City("Fagaras", 176, pane, primaryStage));
        cities.add(new City("Riminicu Vilcea", 193, pane, primaryStage));
        cities.add(new City("Lugoj", 244, pane, primaryStage));
        cities.add(new City("Mehadia", 241, pane, primaryStage));
        cities.add(new City("Craiova", 160, pane, primaryStage));
        cities.add(new City("Dobreta", 242, pane, primaryStage));
        cities.add(new City("Pitesti", 100, pane, primaryStage));
        cities.add(new City("Bucharest", 0, pane, primaryStage));

        cities.add(new City("Giurgiu", 77, pane, primaryStage));
        cities.add(new City("Urziceni", 80, pane, primaryStage));
        cities.add(new City("Hirsova", 151, pane, primaryStage));
        cities.add(new City("Vaslui", 199, pane, primaryStage));
        cities.add(new City("Eforie", 161, pane, primaryStage));
        cities.add(new City("Iasi", 226, pane, primaryStage));
        cities.add(new City("Neamt", 234, pane, primaryStage));

        cities.get(0).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(47));
        cities.get(0).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(110).multiply(31));

        cities.get(1).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(50));
        cities.get(1).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(118));

        cities.get(2).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(62));
        cities.get(2).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(33));

        cities.get(3).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(145));
        cities.get(3).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(87));

        cities.get(4).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(82));
        cities.get(4).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(5));

        cities.get(5).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(228));
        cities.get(5).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(94));

        cities.get(6).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(167));
        cities.get(6).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(120));

        cities.get(7).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(110));
        cities.get(7).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(142));

        cities.get(8).layoutXProperty().bind(pane.widthProperty().divide(950).multiply(223));
        cities.get(8).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(168));

        cities.get(9).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(182));
        cities.get(9).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(205));

        cities.get(10).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(110));
        cities.get(10).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(196));

        cities.get(11).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(240));
        cities.get(11).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(150));

        cities.get(12).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(305));
        cities.get(12).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(180));

        cities.get(13).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(283));
        cities.get(13).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(217));

        cities.get(14).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(353));
        cities.get(14).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(162));

        cities.get(15).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(418));
        cities.get(15).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(164));

        cities.get(16).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(400));
        cities.get(16).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(98));

        cities.get(17).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(440));
        cities.get(17).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(203));

        cities.get(18).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(372));
        cities.get(18).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(54));

        cities.get(19).layoutXProperty().bind(pane.widthProperty().divide(475).multiply(315));
        cities.get(19).layoutYProperty().bind(pane.heightProperty().subtract(10).divide(220).multiply(33));

        addOnOrder(links, linkCompare, new Link(75, 0, 2, pane));
        addOnOrder(links, linkCompare, new Link(118, 0, 1, pane));
        addOnOrder(links, linkCompare, new Link(140, 0, 3, pane));
        addOnOrder(links, linkCompare, new Link(71, 2, 4, pane));
        addOnOrder(links, linkCompare, new Link(111, 1, 7, pane));
        addOnOrder(links, linkCompare, new Link(70, 7, 8, pane));
        addOnOrder(links, linkCompare, new Link(75, 8, 10, pane));
        addOnOrder(links, linkCompare, new Link(151, 4, 3, pane));
        addOnOrder(links, linkCompare, new Link(80, 3, 6, pane));
        addOnOrder(links, linkCompare, new Link(97, 6, 11, pane));
        addOnOrder(links, linkCompare, new Link(99, 3, 5, pane));
        addOnOrder(links, linkCompare, new Link(120, 10, 9, pane));
        addOnOrder(links, linkCompare, new Link(146, 6, 9, pane));
        addOnOrder(links, linkCompare, new Link(138, 9, 11, pane));
        addOnOrder(links, linkCompare, new Link(101, 11, 12, pane));
        addOnOrder(links, linkCompare, new Link(211, 5, 12, pane));
        addOnOrder(links, linkCompare, new Link(90, 12, 13, pane));
        addOnOrder(links, linkCompare, new Link(85, 12, 14, pane));
        addOnOrder(links, linkCompare, new Link(98, 14, 15, pane));
        addOnOrder(links, linkCompare, new Link(86, 15, 17, pane));
        addOnOrder(links, linkCompare, new Link(142, 14, 16, pane));
        addOnOrder(links, linkCompare, new Link(92, 16, 18, pane));
        addOnOrder(links, linkCompare, new Link(87, 18, 19, pane));

        //#FFE4C4 OR F5F5DC
        pane.getStylesheets().add("./StylingCSS/styles.css");
        pane.setId("background");
        pane.getChildren().add(reporter);
        primaryStage.setMinHeight(308);
        primaryStage.setMinWidth(665);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //=============================
    private void costSearch(boolean realCost, boolean heuristic) {
        Path p = getMinPath();
        if (stopFlag) {
            if (p != null) {
                p.visit();
            }
            return;
        }
        if (GSAA.getTargetCity() != null && p != null) {
            if (GSAA.getTargetCity().getName().equals(p.getLastCity())) {
                p.visit();
                return;
            }
        } else if (GSAA.getTargetCity() == null && p != null) {
            if (p.checkReach()) {
                p.visit();
                return;
            }
        }
        if (p != null && !paths.isEmpty() && checkExtended(p.getLastCity())) {
            paths.remove(p);
            costSearch(realCost, heuristic);
        } else {
            if (p != null) {
                p.visit();
            }
            if (expandPath(p, realCost, heuristic)) {
                if (p != null) {
                    p.leave();
                }
                costSearch(realCost, heuristic);
            } else {
                if (p != null) {
                    p.leave();
                }
                if (paths.size() < 1) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Can not reach");
                        alert.setContentText("Sorry but I am stuck here\n"
                                + " I think the target is not reachable ");
                        GridPane g = (GridPane) alert.getDialogPane().lookup(".header-panel");
                        g.setStyle("-fx-background-color: #E8E8E8");
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
                        alert.show();
                    });
                    return;
                }
                paths.remove(p);
                costSearch(realCost, heuristic);
            }
        }
    }

    //=============================
    private void DepthAndBreadth(int level, boolean front, int numberExpanded) {
        Path p = null;
        Path p2 = null;
        do {
            if (paths.size() >= 1) {
                p = paths.get(0);
                if (numberExpanded == 1 && !front) {//Hill climbing
                    p2 = new Path(p);
                }
                if (front) {
                    paths.get(0).visit();
                    if (GSAA.getTargetCity().getName().equals(p.getLastCity())) {
                        return;
                    }
                }
            }

            if (stopFlag) {
                return;
            }
            int res = expandPath(p, front, numberExpanded);
            if (res == 0) {
                return;
            } else if (res == -1 && front) {
                if (paths.size() >= 1) {
                    paths.remove(0);
                }
            } else if (!front && numberExpanded == 1 && p2 != null) {
                if (p2.getLastHeuristic() < paths.get(0).getLastHeuristic()) {
                    return;
                }
            }
            if (level > 0) {
                cleanPaths(level);
            }
        } while (paths.size() >= 1);

    }

    //=============================
    private boolean checkExtended(String city) {
        for (int i = 0; i < extendedCities.size(); i++) {
            if (extendedCities.get(i).equals(city)) {
                return true;
            }
        }
        return false;
    }

    //=============================
    private void cleanPaths(int level) {
        for (int i = 0; i < paths.size(); i++) {
            if (paths.get(i).getPath().size() >= level && !paths.get(i).getLastCity().equals(GSAA.getTargetCity().getName())) {
                paths.get(i).visit();
                if (i != 0) {
                    visitLink(paths.get(i).getPath().get(paths.get(i).getPath().size() - 1));
                }
                paths.get(i).leave();
                paths.remove(i);
                i--;
            }
        }
    }

    //=============================
    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NullPointerException | NumberFormatException e) {
            return false;
        }
        return true;
    }

    //=============================
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NullPointerException | NumberFormatException e) {
            return false;
        }
        return true;
    }

    //=============================
    public void initializeSerialization(Pane pane) {
        serializedCities.clear();
        serializedLinks.clear();
        for (int i = 0; i < cities.size(); i++) {
            serializedCities.add(new SerializableCityInfo(cities.get(i).getName(), cities.get(i).getHeuristicValue(),
                    cities.get(i).getLayoutX(), pane.getWidth(), cities.get(i).getLayoutY(), pane.getHeight(), 0));
        }
        for (int i = 0; i < links.size(); i++) {
            serializedLinks.add(new SerializableLinkInfo(links.get(i).getCost(), links.get(i).getSourceIndex(),
                    links.get(i).getDestinationIndex()));
        }
    }
    //=============================

    private void visitLink(Link l) {
        l.visitLink();
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ex) {
            l.leaveLink();
        }
        l.leaveLink();
    }

    //=============================
    private Path getMinPath() {
        if (paths.size() < 1) {
            return null;
        } else {
            Path p = paths.get(0);
            for (int i = 1; i < paths.size(); i++) {
                if (p.getTotalCost() > paths.get(i).getTotalCost()) {
                    p = paths.get(i);
                }
            }
            return p;
        }
    }

    //=============================
    private boolean expandPath(Path p, boolean realCost, boolean heuristic) {    // Cost based expansion
        boolean edited = false;
        if (p != null) {
            Path p2 = new Path(p);
            for (int i = 0; i < links.size(); i++) {
                if (links.get(i).getKey().contains(p2.getLastCity())) {
                    int pos = 0;    //specify the city name to which the last city is linked
                    String[] linkCities = links.get(i).getKey().split(",");
                    if (!linkCities[0].equals(p2.getLastCity()) && !linkCities[1].equals(p2.getLastCity())) {
                        continue;
                    }
                    if (linkCities[0].equals(p2.getLastCity())) {
                        pos = 1;
                    }
                    if (!checkExtended(linkCities[pos])) {
                        if (!edited) {
                            if (realCost && heuristic) {
                                p.addLink(links.get(i), linkCities[pos]);
                            } else if (realCost && !heuristic) {
                                p.addLink(links.get(i), linkCities[pos]);
                                p.setTotalCost((p.getTotalCost() - p.getLastHeuristic()));
                                p.setLastHeuristic(0);
                            }
                            edited = true;
                            extendedCities.add(p2.getLastCity());
                        } else {
                            Path p3 = new Path(p2);
                            if (realCost && heuristic) {
                                p3.addLink(links.get(i), linkCities[pos]);
                            } else if (realCost && !heuristic) {
                                p3.addLink(links.get(i), linkCities[pos]);
                                p3.setTotalCost((p3.getTotalCost() - p3.getLastHeuristic()));
                                p3.setLastHeuristic(0);
                            }
                            paths.add(p3);
                        }
                        visitLink(links.get(i));
                    }
                }
            }
            p2 = null;
            return edited;
        } else {
            for (int i = 0; i < links.size(); i++) {
                if (links.get(i).getKey().contains(GSAA.getStartCity().getName())) {
                    int pos = 0;    //specify the city name to which the last city is linked
                    String[] linkCities = links.get(i).getKey().split(",");
                    if (linkCities[0].equals(GSAA.getStartCity().getName())) {
                        pos = 1;
                    }
                    if (!linkCities[0].equals(GSAA.getStartCity().getName()) && !linkCities[1].equals(GSAA.getStartCity().getName())) {
                        continue;
                    }
                    edited = true;
                    Path p3 = new Path();
                    if (realCost && heuristic) {
                        p3.addLink(links.get(i), linkCities[pos]);
                    } else if (realCost && !heuristic) {
                        p3.addLink(links.get(i), linkCities[pos]);
                        p3.setTotalCost((p3.getTotalCost() - p3.getLastHeuristic()));
                        p3.setLastHeuristic(0);
                    }
                    paths.add(p3);
                    visitLink(links.get(i));
                }
            }
            if (edited) {
                extendedCities.add(GSAA.getStartCity().getName());
            }
            return edited;
        }
    }

    //=============================
    private int expandPath(Path p, boolean front, int numberExpanded) {     // Front true -> for Depth otherwise it is breadth
        ArrayList<Path> sortedPaths = new ArrayList<Path>();                // numberExpanded for beam and Hill searches
        int edited = -1;
        if (p != null) {
            Path p2;
            if (front) {
                p2 = new Path(p);
            } else {            //Breadth First
                p2 = new Path(p);
                paths.remove(0);    //remove the first path
                p2.visit();
            }
            for (int i = 0; i < links.size(); i++) {
                if (links.get(i).getKey().contains(p2.getLastCity())) {
                    int pos = 0;    //specify the city name to which the last city is linked
                    String[] linkCities = links.get(i).getKey().split(",");
                    if (!linkCities[0].equals(p2.getLastCity()) && !linkCities[1].equals(p2.getLastCity())) {
                        continue;
                    }
                    if (linkCities[0].equals(p2.getLastCity())) {
                        pos = 1;
                    }
                    if (!checkExtended(linkCities[pos])) {
                        if (front && edited == -1) {
                            p.addLink(links.get(i), linkCities[pos]);
                            visitLink(links.get(i));
                            edited = 1;
                            extendedCities.add(p2.getLastCity());
                            if (GSAA.getTargetCity().getName().equals(p.getLastCity())) {
                                p.visit();
                                return 0;
                            }
                        } else if (front && edited == 1) {
                            Path p3 = new Path(p2);
                            p3.addLink(links.get(i), linkCities[pos]);
                            paths.add(1, p3);
                        } else if (!front && numberExpanded <= 0) {
                            edited = 1;
                            Path p3 = new Path(p2);
                            p3.addLink(links.get(i), linkCities[pos]);
                            paths.add(p3);
                            extendedCities.add(linkCities[pos]);        // mark as visited
                            if (GSAA.getTargetCity().getName().equals(p3.getLastCity())) {
                                p3.visit();
                                return 0;
                            }
                            visitLink(links.get(i));
                        } else if (!front && numberExpanded > 0) {
                            edited = 1;
                            Path p3 = new Path(p2);
                            p3.addLink(links.get(i), linkCities[pos]);
                            addOnOrder(sortedPaths, pathCompare, p3);
                        }
                    }
                }
            }
            if (numberExpanded > 0 && edited == 1) {
                extendedCities.add(p2.getLastCity());
                for (int i = 0; i < sortedPaths.size() && i < numberExpanded; i++) {
                    paths.add(sortedPaths.get(i));
                    if (numberExpanded != 1) {
                        visitLink(sortedPaths.get(i).getPath().get(sortedPaths.get(i).getPath().size() - 1));
                    }else if(numberExpanded ==1 && p2.getLastHeuristic() >= paths.get(paths.size() -1).getLastHeuristic()){
                        visitLink(sortedPaths.get(i).getPath().get(sortedPaths.get(i).getPath().size() - 1));
                    }
                    if (GSAA.getTargetCity() != null && GSAA.getTargetCity().getName().equals(sortedPaths.get(i).getLastCity())) {
                        sortedPaths.get(i).visit();
                        return 0;
                    } else if (GSAA.getTargetCity() == null && sortedPaths.get(i).getLastHeuristic() == 0) {
                        sortedPaths.get(i).visit();
                        return 0;
                    }
                }
            }
            p2 = null;
            p.leave();
            return edited;
        } else {
            for (int i = 0; i < links.size(); i++) {
                if (links.get(i).getKey().contains(GSAA.getStartCity().getName())) {
                    int pos = 0;    //specify the city name to which the last city is linked
                    String[] linkCities = links.get(i).getKey().split(",");
                    if (linkCities[0].equals(GSAA.getStartCity().getName())) {
                        pos = 1;
                    }
                    if (!linkCities[0].equals(GSAA.getStartCity().getName()) && !linkCities[1].equals(GSAA.getStartCity().getName())) {
                        continue;
                    }
                    Path p3 = new Path();
                    p3.addLink(links.get(i), linkCities[pos]);
                    if (edited == -1 || !front) {   //first path added or breadth add at last
                        if (numberExpanded > 0) {
                            addOnOrder(sortedPaths, pathCompare, p3);
                        } else {
                            paths.add(p3);
                            visitLink(links.get(i));
                        }
                        if (numberExpanded <= 0 && !front) {                 // add visited cities not just extended
                            extendedCities.add(linkCities[pos]);
                        }
                        edited = 1;
                    } else {
                        paths.add(1, p3);
                    }
                    if (numberExpanded <= 0 && GSAA.getTargetCity().getName().equals(p3.getLastCity())) {
                        return 0;
                    }
                }
            }
            if (edited == 1) {
                extendedCities.add(GSAA.getStartCity().getName());
            }
            if (numberExpanded > 0) {
                for (int i = 0; i < sortedPaths.size() && i < numberExpanded; i++) {
                    paths.add(sortedPaths.get(i));
                    visitLink(sortedPaths.get(i).getPath().get(sortedPaths.get(i).getPath().size() - 1));
                    if (GSAA.getTargetCity() != null && GSAA.getTargetCity().getName().equals(sortedPaths.get(i).getLastCity())) {
                        sortedPaths.get(i).visit();
                        return 0;
                    } else if (GSAA.getTargetCity() == null && sortedPaths.get(i).getLastHeuristic() == 0) {
                        sortedPaths.get(i).visit();
                        return 0;
                    }
                }
            }
            if (paths.size() >= 1 && front) {
                paths.get(0).leave();
            }
            return edited;
        }
    }

    //=============================
    public static <T> int indexBinarySearch(ArrayList<T> array, Comparator<T> c, T obj) {
        Random random = new Random();
        int leftIndex = 0;
        int rightIndex = array.size() - 1;
        int pivot = 0;
        int comparevalue = 0;
        if (c.compare(obj, array.get(leftIndex)) < 0 || rightIndex < leftIndex) {
            return leftIndex;
        } else if (c.compare(obj, array.get(rightIndex)) > 0) {
            return rightIndex + 1;
        } else {
            while (true) {
                if (rightIndex - leftIndex == 1) {
                    pivot = leftIndex + 1;
                } else {
                    pivot = random.nextInt(rightIndex - leftIndex - 1) + leftIndex + 1;//Random number between left+1, right-1
                }
                comparevalue = c.compare(obj, array.get(pivot));		// the best for my algorithm
                if (comparevalue == 0) //Note for the range Min = 0+left+1
                {							//max = right-left-1-1+1+left=right-1
                    //System.out.println("Some error happened, please call the support");
                    //return -1 * pivot;
                    return pivot;
                } else if (comparevalue > 0) {
                    leftIndex = pivot;
                } else {
                    rightIndex = pivot;
                }
                if ((rightIndex - leftIndex) == 1) {
                    return rightIndex;
                }
            }
        }
    }

    //=============================
    public static <T> int addOnOrder(ArrayList<T> array, Comparator<T> c, T obj) {
        if (!array.isEmpty()) {
            int index = indexBinarySearch(array, c, obj);
            if (index < 0) {
                return index;
            }
            array.add(index, obj);
            return index;
        } else {
            array.add(obj);
        }
        return 0;
    }
    //=============================

    public static boolean checkCity(String name) {
        for (int i = 0; i < cities.size(); i++) {
            if (cities.get(i).getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    //=============================

    public static ArrayList<City> getCities() {
        return cities;
    }
    //=============================

    public static ArrayList<Link> getLinks() {
        return GSAA.links;
    }

    //=============================
    private double averageCosts() {
        double average = 0;
        for (int i = 0; i < links.size(); i++) {
            average += links.get(i).getCost();
        }
        average /= links.size();
        return average;
    }

    //=============================
    private double averageScale() {
        double average = 0;
        for (int i = 0; i < links.size(); i++) {
            average += (links.get(i).getCost() / distanceLink(links.get(i)));
        }
        average /= links.size();
        return average;
    }

    //=============================
    private double minScale() {
        double min = (links.get(0).getCost() / distanceLink(links.get(0)));
        for (int i = 1; i < links.size(); i++) {
            if (min > (links.get(i).getCost() / distanceLink(links.get(i)))) {
                min = (links.get(i).getCost() / distanceLink(links.get(i)));
            }
        }
        return min;
    }

    //=============================
    private double distanceLink(Link l) {
        double x = cities.get(l.getDestinationIndex()).getLayoutX() - cities.get(l.getSourceIndex()).getLayoutX();
        double y = cities.get(l.getDestinationIndex()).getLayoutY() - cities.get(l.getSourceIndex()).getLayoutY();
        x *= x;
        y *= y;
        return Math.sqrt((x + y));
    }

    //=============================
    public static int findCityIndex(City c) {
        for (int i = 0; i < cities.size(); i++) {
            if (c == cities.get(i)) {
                return i;
            }
        }
        return -1;
    }

    //=============================
    public static int findCityIndex(String c) {
        for (int i = 0; i < cities.size(); i++) {
            if (c.equals(cities.get(i).getName())) {
                return i;
            }
        }
        return -1;
    }

    //=============================
    public static void setTargetCity(City c) {
        GSAA.target = c;
    }

    //=============================
    public static void setStartCity(City c) {
        GSAA.start = c;
    }

//=============================
    public static City getTargetCity() {
        return GSAA.target;
    }

    //=============================
    public static City getStartCity() {
        return GSAA.start;
    }
}
