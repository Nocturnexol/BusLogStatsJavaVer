package com.bs;

import java.util.Date;

public class BusDataStats {
    private String lineCode;
    private Direction direction;
    private int stationNum;
    private String stationCode;
    private String vehCode;
    private Date firstEstimatedTime;
    private Date lastEstimatedTime;
    private Date firstProcessTime;
    private Date lastProcessTime;

    public long getTimeDiffMin() {

        long res = (lastProcessTime.getTime() - firstEstimatedTime.getTime()) / (1000 * 60);
        return -(Math.abs(res) > 1000 ? res < 0 ? res + 1440 : res > 0 ? res - 1440 : res : res);
    }

    public long getActualTotalMin() {
        long res = (lastProcessTime.getTime() - firstProcessTime.getTime()) / (1000 * 60);
        return res < -1000 ? res + 1440 : res;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setStationNum(int stationNum) {
        this.stationNum = stationNum;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public void setVehCode(String vehCode) {
        this.vehCode = vehCode;
    }

    public void setFirstEstimatedTime(Date firstEstimatedTime) {
        this.firstEstimatedTime = firstEstimatedTime;
    }

    public void setLastEstimatedTime(Date lastEstimatedTime) {
        this.lastEstimatedTime = lastEstimatedTime;
    }

    public void setFirstProcessTime(Date firstProcessTime) {
        this.firstProcessTime = firstProcessTime;
    }

    public void setLastProcessTime(Date lastProcessTime) {
        this.lastProcessTime = lastProcessTime;
    }
}
