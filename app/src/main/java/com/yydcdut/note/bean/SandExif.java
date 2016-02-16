package com.yydcdut.note.bean;

/**
 * Created by yuyidong on 15/11/5.
 */
public class SandExif {
    private final int orientation;
    private final String latitude;
    private final String longitude;
    private final int whiteBalance;
    private final int flash;
    private final int imageLength;
    private final int imageWidth;
    private final String make;
    private final String model;

    public SandExif(int orientation, String latitude, String longitude, int whiteBalance, int flash,
                    int imageLength, int imageWidth, String make, String model) {
        this.orientation = orientation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.whiteBalance = whiteBalance;
        this.flash = flash;
        this.imageLength = imageLength;
        this.imageWidth = imageWidth;
        this.make = make;
        this.model = model;
    }

    public int getOrientation() {
        return orientation;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getWhiteBalance() {
        return whiteBalance;
    }

    public int getFlash() {
        return flash;
    }

    public int getImageLength() {
        return imageLength;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "SandExif{" +
                "orientation=" + orientation +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", whiteBalance=" + whiteBalance +
                ", flash=" + flash +
                ", imageLength=" + imageLength +
                ", imageWidth=" + imageWidth +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
