package com.techdev.dcart.APICalls;

public class Flight {

    public String  airLines;
    public String  fromLocation;
    public String  toLocation;
    public String  startTime;
    public String  reachTime;
    public String  cost;

    public Flight(String airLines, String fromLocation, String toLocation, String startTime, String reachTime, String cost) {
        this.airLines           = airLines;
        this.fromLocation       = fromLocation;
        this.toLocation         = toLocation;
        this.startTime          = startTime;
        this.reachTime          = reachTime;
        this.cost               = cost;

    }


}
