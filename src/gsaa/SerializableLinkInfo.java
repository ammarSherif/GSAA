package gsaa;

import java.io.Serializable;

/**
 * this class is used to save the links so they could be recovered later
 * @author Ammar Sherif
 */
public class SerializableLinkInfo implements Serializable {
    private double cost;
    private int sourceIndex;
    private int destinationIndex;

    /**
     * this creates an object which could be saved
     * @param cost double indicates cost of the route
     * @param sourceIndex indicates the index of the first city in the Cities array
     * @param destinationIndex indicates the index of the second city in Cities array
     */
    public SerializableLinkInfo(double cost, int sourceIndex, int destinationIndex) {
        this.cost = cost;
        this.sourceIndex = sourceIndex;
        this.destinationIndex = destinationIndex;
    }
    
    /**
     * get cost of the link
     * @return double value indicates cost of the link
     */
    public double getCost() {
        return cost;
    }

    /**
     * set cost of the link
     * @param cost double value indicates cost of the link
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * get the index of the first city attached to this link in the Cities array
     * @return int value indicates the index of the first city attached to <br/>this link in the Cities array
     */
    public int getSourceIndex() {
        return sourceIndex;
    }
    
    /**
     * set the index of the first city attached to this link in the Cities array
     * @param sourceIndex int value indicates the index of the first city attached to <br/>this link in the Cities array
     */
    public void setSourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    /**
     * get the index of the second city attached to this link in the Cities array
     * @return int value indicates the index of the second city attached to <br/>this link in the Cities array
     */
    public int getDestinationIndex() {
        return destinationIndex;
    }

    /**
     * set the index of the second city attached to this link in the Cities array
     * @param destinationIndex int value indicates the index of the second city attached to <br/>this link in the Cities array
     */
    public void setDestinationIndex(int destinationIndex) {
        this.destinationIndex = destinationIndex;
    }
    
}
