package edu.ucsb.cs.cs184.npoon.npoongeotweet;

import java.io.Serializable;

/**
 * Created by nickkpoon on 11/16/17.
 */

public class LocationPoint implements Serializable {
    private double longitude;
    private double latitude;
    private String author;
    private String content;
    private double timestamp;
    private int likes;
    private static LocationPointListener listener = null;

    public LocationPoint()
    {

    }

    public interface LocationPointListener
    {
        public void onObjectReady(String title);
        public void onDataLoaded(LocationPoint data);
        public void onDataRemoved(LocationPoint data);
        public void onDataChanged(LocationPoint data);
    }
    public void setLocationPointListener(LocationPointListener listener)
    {
        this.listener = listener;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getContent()
    {
        return content;
    }

    public double getTimestamp()
    {
        return timestamp;
    }

    public int getLikes()
    {
        return likes;
    }

    public static void loadData(LocationPoint LP)
    {
        if (listener != null)
        {
            listener.onDataLoaded(LP);
        }
    }

    public static void removeData(LocationPoint LP)
    {
        if (listener != null)
        {
            listener.onDataRemoved(LP);
        }
    }

    public static void changeData(LocationPoint LP)
    {
        if (listener != null)
        {
            listener.onDataChanged(LP);
        }
    }
}
