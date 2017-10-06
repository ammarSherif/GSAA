package gsaa;

import java.io.Serializable;

/**
 * this class is used to save the cities so they could be recovered later
 * @author Ammar Sherif
 */
public class SerializableCityInfo implements Serializable{
    private String cityName;
    private double heuristicValue;
    private double xMulBind;
    private double xDivBind;
    private double yMulBind;
    private double yDivBind;
    private double ySubBind;
    
    /**
     * 
     * @param cityName name of the city
     * @param heuristicValue its heuristic
     * @param xMulBind multiplication of width of the pane to be binded to indicating its position
     * @param xDivBind division of value of width of the pane to be binded to indicating its position 
     * @param yMulBind multiplication of height of the pane to be binded to indicating its position
     * @param yDivBind division of value of height of the pane to be binded to indicating its position
     * @param ySubBind subtraction of value of height of the pane to be binded to indicating its position
     */
    public SerializableCityInfo(String cityName, double heuristicValue, double xMulBind, double xDivBind, double yMulBind, double yDivBind, double ySubBind) {
        this.cityName = cityName;
        this.heuristicValue = heuristicValue;
        this.xMulBind = xMulBind;
        this.xDivBind = xDivBind;
        this.yMulBind = yMulBind;
        this.yDivBind = yDivBind;
        this.ySubBind = ySubBind;
    }
    /**
     * gets subtraction of value of height of the pane to be binded to indicating its position
     * @return double of subtraction value of the height of the pane to be binded to indicating its position
     */
    public double getySubBind() {
        return ySubBind;
    }
    
    /**
     * sets subtraction of value of height of the pane to be binded to indicating its position
     * @param ySubBind double of subtraction value of the height of the pane to be binded to indicating its position
     */
    public void setySubBind(double ySubBind) {
        this.ySubBind = ySubBind;
    }
    
    /**
     * gets string of the city name
     * @return string of the city name
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * sets the city name
     * @param cityName string of the city name
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * gets the heuristic value assigned to the city
     * @return double of the heuristic assigned to the city
     */
    public double getHeuristicValue() {
        return heuristicValue;
    }

    /**
     * sets the heuristic value assigned to the city
     * @param heuristicValue double indicates the heuristic value assigned to the city
     */
    public void setHeuristicValue(double heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    /**
     * gets multiplication value of width of the pane the city is binded to indicating its position
     * @return double indicates multiplication value of width of the pane <br/>the city is binded to indicating its position
     */
    public double getxMulBind() {
        return xMulBind;
    }
    /**
     * sets multiplication value of width of the pane the city is binded to indicating its position
     * @param xMulBind double indicates multiplication value of width of the pane <br/>the city is binded to indicating its position
     */
    public void setxMulBind(double xMulBind) {
        this.xMulBind = xMulBind;
    }

    /**
     * gets division value of width of the pane the city is binded to indicating its position
     * @return double indicates division value of width of the pane <br/>the city is binded to indicating its position
     */
    public double getxDivBind() {
        return xDivBind;
    }
    /**
     * sets division value of width of the pane the city is binded to indicating its position
     * @param xDivBind double indicates division value of width of the pane <br/>the city is binded to indicating its position
     */
    public void setxDivBind(double xDivBind) {
        this.xDivBind = xDivBind;
    }

    /**
     * gets multiplication value of height of the pane the city is binded to indicating its position
     * @return double indicates multiplication value of height of the pane <br/>the city is binded to indicating its position
     */
    public double getyMulBind() {
        return yMulBind;
    }

    /**
     * sets multiplication value of height of the pane the city is binded to indicating its position
     * @param yMulBind double indicates multiplication value of height of the pane <br/>the city is binded to indicating its position
     */
    public void setyMulBind(double yMulBind) {
        this.yMulBind = yMulBind;
    }

    /**
     * gets division value of height of the pane the city is binded to indicating its position
     * @return double indicates division value of height of the pane <br/>the city is binded to indicating its position
     */
    public double getyDivBind() {
        return yDivBind;
    }
    
    /**
     * sets division value of height of the pane the city is binded to indicating its position
     * @param yDivBind double indicates division value of height of the pane <br/>the city is binded to indicating its position
     */
    public void setyDivBind(double yDivBind) {
        this.yDivBind = yDivBind;
    }
    
}
