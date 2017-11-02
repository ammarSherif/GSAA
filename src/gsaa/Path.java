package gsaa;

import java.util.ArrayList;

/**
 * This class represents a path which constitutes of collection of links between
 * cities and include the last city where the path stops, and also represents the
 * total cost of the path with/without heuristic.
 * @author Ammar Sherif
 */
public class Path {

    private final ArrayList<Link> path;
    private double totalCost;
    private double lastHeuristic;
    private String lastCity;

    /**
     * creates an object and initialize its cost to 0
     */
    public Path() {
        totalCost = 0;
        lastHeuristic = 0;
        path = new ArrayList<Link>();
    }
    
    /**
     * adds a link to the path representing a move from the previous city to another one
     * @param l the link to be added
     * @param destination the destination city name
     */
    public void addLink(Link l, String destination) {
        lastCity = destination;
        totalCost -= lastHeuristic;
        path.add(l);
        for (int i = 0; i < GSAA.getCities().size(); i++) {
            if (GSAA.getCities().get(i).getName().equals(destination)) {
                lastHeuristic = GSAA.getCities().get(i).getHeuristicValue();
                break;
            }
        }
        totalCost += l.getCost() + lastHeuristic;
    }
    
    /**
     * gets the total cost of the path
     * @return total cost of the path
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * checks whether we reached our destination or not
     * @return true when arrived
     */
    public boolean checkReach() {
        return (lastHeuristic == 0);
    }

    /**
     * gets the sequence of moves between cities in this path
     * @return an array list of Links as a sequence representing the path
     */
    public ArrayList<Link> getPath() {
        return path;
    }

    /**
     * a constructor that copies a path, <i>hard copy</i>
     * @param p the path to be copied into this object
     */
    public Path(Path p) {
        this.path = new ArrayList<Link>();
        this.totalCost = p.getTotalCost();
        this.lastHeuristic = p.getLastHeuristic();
        this.lastCity = p.getLastCity();
        this.copyPath(p.getPath());
    }
    
    /**
     * gets the heuristic of the las city where the path stopped
     * @return a double value indicating the heuristic of the last city
     */
    public double getLastHeuristic() {
        return lastHeuristic;
    }

    /**
     * copies a list of links to the object's array list
     * @param p the links list to be copied
     */
    public void copyPath(ArrayList<Link> p) {
        for (int i = 0; i < p.size(); i++) {
            this.path.add(p.get(i));
        }
    }
    
    /**
     * method copies a path, <i>hard copy</i>
     * @param p the path to be copied into this object
     */
    public void copy(Path p) {
        this.path.clear();
        this.totalCost = p.getTotalCost();
        this.lastHeuristic = p.getLastHeuristic();
        this.lastCity = p.getLastCity();
        this.copyPath(p.getPath());
    }

    /**
     * visit all the links of the path, i.e., all moves
     */
    public void visit() {
        for (int i = 0; i < this.path.size(); i++) {
            path.get(i).visitLink();
        }
    }
    
    /**
     * leaves all the links of the path, i.e., all moves
     */
    public void leave() {
        for (int i = 0; i < this.path.size(); i++) {
            path.get(i).leaveLink();
        }
    }

    /**
     * gets the last city name where the path stopped in
     * @return string of the last city's name
     */
    public String getLastCity() {
        return lastCity;
    }

    /**
     * modifies the total cost of the path
     * @param totalCost the new cost to be set
     */
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * modifies the heuristic of the last city of the path
     * @param lastHeuristic the new heuristic which represents the last city's heuristic
     */
    public void setLastHeuristic(double lastHeuristic) {
        this.lastHeuristic = lastHeuristic;
    }
}
