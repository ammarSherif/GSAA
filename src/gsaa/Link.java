/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gsaa;

import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 *
 * @author ammar
 */
public class Link extends Line {

    private double cost;
    private int sourceIndex;
    private int destinationIndex;
    private static ArrayList<City> cities = GSAA.getCities();
    private final String key;
    private final Label costLabel;

    public Link(double cost, int sourceIndex, int destinationIndex, Pane pane) {
        this.cost = cost;
        this.sourceIndex = sourceIndex;
        this.destinationIndex = destinationIndex;
        this.key = cities.get(sourceIndex).getName() + "," + cities.get(destinationIndex).getName();
        this.bindPosition(cities.get(sourceIndex), cities.get(destinationIndex));
        costLabel = new Label(String.valueOf(cost));
        costLabel.setStyle("-fx-background-color: #F5F5DC");
        costLabel.layoutXProperty().bind(this.startXProperty().add(this.endXProperty()).divide(2));
        costLabel.layoutYProperty().bind(this.startYProperty().add(this.endYProperty()).divide(2));
        pane.getChildren().add(0, costLabel);
        pane.getChildren().add(0, this);
        this.setStrokeLineCap(StrokeLineCap.ROUND);
        this.setStrokeLineJoin(StrokeLineJoin.ROUND);
        this.setStroke(Color.CORNFLOWERBLUE);
        this.setSmooth(true);
        this.setFill(Color.FLORALWHITE);
        this.setOnMousePressed((MouseEvent e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                inputInfo();
            } else if (e.getButton().equals(MouseButton.SECONDARY)) {
                pane.getChildren().remove(costLabel);
                pane.getChildren().remove(this);
                GSAA.getLinks().remove(this);
            }
        });
        this.leaveLink();
    }

    public void visitLink() {
        this.setStrokeWidth(6);
    }

    public void delete(Pane pane) {
        pane.getChildren().remove(costLabel);
        pane.getChildren().remove(this);
        GSAA.getLinks().remove(this);
    }

    public void leaveLink() {
        this.setStrokeWidth(2);
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getKey() {
        return key;
    }

    public double getSlope() {
        if (this.getStartX() > this.getEndX()) {
            return (this.getStartY() - this.getEndY()) / (this.getStartX() - this.getEndX());
        } else {
            return (this.getEndY() - this.getStartY()) / (this.getEndX() - this.getStartX());
        }
    }

    private void inputInfo() {

        // Create a custom dialog.
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Enter Cost");
        // Set the button types.
        ButtonType enterButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(enterButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        TextField cost = new TextField();
        cost.setPromptText(" number >= 0");
        cost.setText(String.valueOf(this.cost));
        cost.selectAll();
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

        // Convert the result to String when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == enterButtonType) {
                cost.setText(cost.getText());
                return cost.getText();
            }
            return null;

        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            this.costLabel.setText(pair);
            this.cost = Double.parseDouble(pair);
        });
    }

    private void bindPosition(final City start, final City end) {
        this.startXProperty().bind(start.layoutXProperty().add(5));
        this.startYProperty().bind(start.layoutYProperty().add(5));
        this.endXProperty().bind(end.layoutXProperty().add(5));
        this.endYProperty().bind(end.layoutYProperty().add(5));
    }

    public int getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    public int getDestinationIndex() {
        return destinationIndex;
    }

    public void setDestinationIndex(int destinationIndex) {
        this.destinationIndex = destinationIndex;
    }
}
