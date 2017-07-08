package fim.uni_passau.de.countyourhits.model;

import org.opencv.core.Point;

/**
 * Created by ziaridoy20 on 10-Jun-17.
 */

public class DetectedCircle {

    private boolean isCircle = false;
    private Point cirCoordinate;
    private int cirRadius;
    private String cirImgPath;


    public DetectedCircle(){

    }

    public boolean isCircle() {
        return isCircle;
    }

    public void setCircle(boolean circle) {
        isCircle = circle;
    }

    public Point getCirCoordinate() {
        return cirCoordinate;
    }

    public void setCirCoordinate(Point cirCoordinate) {
        this.cirCoordinate = cirCoordinate;
    }

    public int getCirRadius() {
        return cirRadius;
    }

    public void setCirRadius(int cirRadius) {
        this.cirRadius = cirRadius;
    }

    public String getCirImgPath() {
        return cirImgPath;
    }

    public void setCirImgPath(String cirImgPath) {
        this.cirImgPath = cirImgPath;
    }
}