/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gsaa;

import java.io.Serializable;

/**
 *
 * @author family
 */
public class SerializableCityInfo implements Serializable{
    private String cityName;
    private double heuristicValue;
    private double xMulBind;
    private double xDivBind;
    private double yMulBind;
    private double yDivBind;
    private double ySubBind;

    public SerializableCityInfo(String cityName, double heuristicValue, double xMulBind, double xDivBind, double yMulBind, double yDivBind, double ySubBind) {
        this.cityName = cityName;
        this.heuristicValue = heuristicValue;
        this.xMulBind = xMulBind;
        this.xDivBind = xDivBind;
        this.yMulBind = yMulBind;
        this.yDivBind = yDivBind;
        this.ySubBind = ySubBind;
    }

    public double getySubBind() {
        return ySubBind;
    }

    public void setySubBind(double ySubBind) {
        this.ySubBind = ySubBind;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(double heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public double getxMulBind() {
        return xMulBind;
    }

    public void setxMulBind(double xMulBind) {
        this.xMulBind = xMulBind;
    }

    public double getxDivBind() {
        return xDivBind;
    }

    public void setxDivBind(double xDivBind) {
        this.xDivBind = xDivBind;
    }

    public double getyMulBind() {
        return yMulBind;
    }

    public void setyMulBind(double yMulBind) {
        this.yMulBind = yMulBind;
    }

    public double getyDivBind() {
        return yDivBind;
    }

    public void setyDivBind(double yDivBind) {
        this.yDivBind = yDivBind;
    }
    
}
