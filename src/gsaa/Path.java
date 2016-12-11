/*
 * 
 */
package gsaa;

import java.util.ArrayList;

/**
 *
 * @author Ammar Sherif
 */
public class Path {

    private ArrayList<Link> path;
    private double totalCost;
    private double lastHeuristic;
    private String lastCity;

    public Path() {
        totalCost = 0;
        lastHeuristic = 0;
        path = new ArrayList<Link>();
    }

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

    public double getTotalCost() {
        return totalCost;
    }

    public boolean checkReach() {
        return (lastHeuristic == 0);
    }

    public ArrayList<Link> getPath() {
        return path;
    }

    public Path(Path p) {
        this.path = new ArrayList<Link>();
        this.totalCost = p.getTotalCost();
        this.lastHeuristic = p.getLastHeuristic();
        this.lastCity = p.getLastCity();
        this.copyPath(p.getPath());
    }

    public double getLastHeuristic() {
        return lastHeuristic;
    }

    public void copyPath(ArrayList<Link> p) {
        for (int i = 0; i < p.size(); i++) {
            this.path.add(p.get(i));
        }
    }

    public void copy(Path p) {
        this.path.clear();
        this.totalCost = p.getTotalCost();
        this.lastHeuristic = p.getLastHeuristic();
        this.lastCity = p.getLastCity();
        this.copyPath(p.getPath());
    }

    public void visit() {
        for (int i = 0; i < this.path.size(); i++) {
            path.get(i).visitLink();
        }
    }

    public void leave() {
        for (int i = 0; i < this.path.size(); i++) {
            path.get(i).leaveLink();
        }
    }

    public String getLastCity() {
        return lastCity;
    }
}
