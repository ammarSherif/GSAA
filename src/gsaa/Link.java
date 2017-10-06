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
 * The Link class is represented as Lines, representing routes, that can be pressed,
 * Also it keeps its cost and associated label to the link.
 * @author Ammar Sherif
 */
public class Link extends Line {

    private double cost;
    private int firstIndex;
    private int secondIndex;
    // array of all cities in the program
    private final static ArrayList<City> CITIES = GSAA.getCities();
    // key is a distinct parameter of each link consists of names of the two CITIES
    // separated by comma "," to distinguish links and know if somelink already exitsed
    private final String key;
    private final Label costLabel;
    
    /**
     * this creates a new link between two CITIES 
     * providing the ability to be deleted and linking the line to the specified CITIES
     * @param cost cost of that route if taken
     * @param firstIndex the index of the first city in array CITIES
     * @param secondIndex the index of the second city 
     * @param pane the pane to which the link would be added 
     */
    public Link(double cost, int firstIndex, int secondIndex, Pane pane) {
        this.cost = cost;
        this.firstIndex = firstIndex;
        this.secondIndex = secondIndex;
        // set the key = firstCityName,secondCityName"
        this.key = CITIES.get(firstIndex).getName() + "," + CITIES.get(secondIndex).getName();
        // bind the link between the two cities, i.e. attach its start and end points to associated cities
        this.bindPosition(CITIES.get(firstIndex), CITIES.get(secondIndex));
        costLabel = new Label(String.valueOf(cost));
        this.costLabel.getStylesheets().add("./StylingCSS/styles.css");
        this.costLabel.setId("background");
        // put it in the middle
        costLabel.layoutXProperty().bind(this.startXProperty().add(this.endXProperty()).divide(2));
        costLabel.layoutYProperty().bind(this.startYProperty().add(this.endYProperty()).divide(2));
        // add the label and the link note cost is added first 
        // so it would be above the link
        pane.getChildren().add(0, costLabel);
        pane.getChildren().add(0, this);
        // styling
        this.setStrokeLineCap(StrokeLineCap.ROUND);
        this.setStrokeLineJoin(StrokeLineJoin.ROUND);
        this.setStroke(Color.CORNFLOWERBLUE);
        this.setSmooth(true);
        this.setFill(Color.FLORALWHITE);
        
        this.setOnMousePressed((MouseEvent e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                // left click to modify the info
                inputInfo();
            } else if (e.getButton().equals(MouseButton.SECONDARY)) {
                // right click remove this link and its label
                this.delete(pane);
            }
        });
        // modify the width
        this.leaveLink();
    }
    /**
     * increase the width of the line to indicate visiting it
     */
    public void visitLink() {
        this.setStrokeWidth(6);
    }
    /**
     * delete this link and the associated label of the cost  from the pane
     * @param pane the pane contains this link
     */
    public void delete(Pane pane) {
        pane.getChildren().remove(costLabel);
        pane.getChildren().remove(this);
        GSAA.getLinks().remove(this);
    }
    /**
     * put the width to its normal value indicating leaving the link
     */
    public void leaveLink() {
        this.setStrokeWidth(2);
    }
    /**
     * returns the cost of this link
     * @return double value of the cost of this link
     */
    public double getCost() {
        return cost;
    }
    /**
     * sets the cost of the link
     * @param cost double value indicating cost of the route
     */
    public void setCost(double cost) {
        this.cost = cost;
    }
    /**
     * gets the key that distinguish this specific link on the form <i>firstCityName,secondCityName</i>
     * @return string value of the key which contains the cities which this line links separated by comma
     */
    public String getKey() {
        return key;
    }
    /**
     * get the slope of the link
     * @return double value indicating link's slope
     */
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
        // selects text for modification
        cost.selectAll();
        gridPane.add(new Label(" Cost : "), 0, 0);
        gridPane.add(cost, 1, 0);
        /* Styling
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
       */               
        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the cost field by default.
        Platform.runLater(() -> cost.requestFocus());

        Node enterButton = dialog.getDialogPane().lookupButton(enterButtonType);
        enterButton.setDisable(true);
        // eneable enter only when appropriate
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
    /**
     * attach the link to the associated cities so when they move,<br/>
     * the link moves with them
     * @param first denotes the first city
     * @param second denotes the second city
     */
    private void bindPosition(final City first, final City second) {
        this.startXProperty().bind(first.layoutXProperty().add(5));
        this.startYProperty().bind(first.layoutYProperty().add(5));
        this.endXProperty().bind(second.layoutXProperty().add(5));
        this.endYProperty().bind(second.layoutYProperty().add(5));
    }
    
    /**
     * get the index of the first city within the Cities array
     * @return the index of the first city of this link in the cities array
     */
    public int getFirstIndex() {
        return firstIndex;
    }
    
    /**
     * get the index of the second city within the Cities array
     * @return the index of the second city of this link in the cities array
     */
    public int getSecondIndex() {
        return secondIndex;
    }
}
