package com.ghasemi.retrofitest;


import android.os.Parcel;
import android.os.Parcelable;
 
public class Download  implements Parcelable{
 
    public Download(){
 
    }
//    We send this Download model class object in a Intent for broadcasting.
//    Inorder to send this in Intent Extra we need to implement the Parcelable interface.
//    The describeContents() and writeToParcel() methods should be overrided.
//    It also has a static Parcelable CREATOR field which implements Parcelable.Creator interface.
    private int progress;
    private int currentFileSize;
    private int totalFileSize;
 
    public int getProgress() {
        return progress;
    }
 
    public void setProgress(int progress) {
        this.progress = progress;
    }
 
    public int getCurrentFileSize() {
        return currentFileSize;
    }
 
    public void setCurrentFileSize(int currentFileSize) {
        this.currentFileSize = currentFileSize;
    }
 
    public int getTotalFileSize() {
        return totalFileSize;
    }
 
    public void setTotalFileSize(int totalFileSize) {
        this.totalFileSize = totalFileSize;
    }
 
    @Override
    public int describeContents() {
        return 0;
    }
 
    @Override
    public void writeToParcel(Parcel dest, int flags) {
 
        dest.writeInt(progress);
        dest.writeInt(currentFileSize);
        dest.writeInt(totalFileSize);
    }
 
    private Download(Parcel in) {
 
        progress = in.readInt();
        currentFileSize = in.readInt();
        totalFileSize = in.readInt();
    }
 
    public static final Parcelable.Creator<Download> CREATOR = new Parcelable.Creator<Download>() {
        public Download createFromParcel(Parcel in) {
            return new Download(in);
        }
 
        public Download[] newArray(int size) {
            return new Download[size];
        }
    };
}