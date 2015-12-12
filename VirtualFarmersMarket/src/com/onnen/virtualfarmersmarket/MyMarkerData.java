package com.onnen.virtualfarmersmarket;

import android.graphics.Bitmap;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyMarkerData {
    private String mLabel;
    private Bitmap mIcon;
    private Double mLatitude;
    private Double mLongitude;

    public MyMarkerData(String label, Bitmap icon, Double latitude, Double longitude) {
        this.mLabel = label;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mIcon = icon;
    }
    
    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String mLabel) {
        this.mLabel = mLabel;
    }

    public Bitmap getIcon() {
        return mIcon;
    }

    public void setIcon(Bitmap icon) {
        this.mIcon = icon;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }
}
