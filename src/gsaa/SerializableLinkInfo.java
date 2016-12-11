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
public class SerializableLinkInfo implements Serializable {
    private double cost;
    private int sourceIndex;
    private int destinationIndex;

    public SerializableLinkInfo(double cost, int sourceIndex, int destinationIndex) {
        this.cost = cost;
        this.sourceIndex = sourceIndex;
        this.destinationIndex = destinationIndex;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
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
