/*
 * 
 */
package gsaa;

import java.io.Serializable;

/**
 *
 * @author Ammar Sherif
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
