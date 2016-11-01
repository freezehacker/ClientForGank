package com.sysu.sjk.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by sjk on 16-10-21.
 */
public class Gank implements Serializable, Parcelable {

    private String _id;
    private String createdAt;
    private String desc;
    private String publishedAt;
    private String source;
    private String type;
    private String url;
    private boolean used;
    private String who;
    private String content; // the html of the url...

    public Gank(String id) {
        _id = id;
    }

    public Gank(String _id, String createdAt, String desc, String publishedAt, String source, String type, String url, boolean used, String who) {
        this._id = _id;
        this.createdAt = createdAt;
        this.desc = desc;
        this.publishedAt = publishedAt;
        this.source = source;
        this.type = type;
        this.url = url;
        this.used = used;
        this.who = who;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        String strTemplate = "Gank(id=%s): { title:%s, author:%s, contentLength:%d }";
        return String.format(strTemplate, _id, desc, who, (content == null ? 0 : content.length()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(this._id);
        out.writeString(this.url);
        out.writeString(this.desc);
        out.writeString(this.content);
        out.writeString(this.createdAt);
        out.writeString(this.publishedAt);
        out.writeString(this.who);
        out.writeString(this.source);
        out.writeString(this.type);
        out.writeByte(this.used ? (byte) 'T' : (byte) 'F');
    }

    public static final Creator<Gank> CREATOR = new Creator<Gank>() {

        // Must read in the order that it's been written.
        @Override
        public Gank createFromParcel(Parcel in) {
            String id = in.readString();
            Gank gank = new Gank(id);
            gank.url = in.readString();
            gank.desc = in.readString();
            gank.content = in.readString();
            gank.createdAt = in.readString();
            gank.publishedAt = in.readString();
            gank.who = in.readString();
            gank.source = in.readString();
            gank.type = in.readString();
            gank.used = ((char) in.readByte() == 'T');
            return gank;
        }

        @Override
        public Gank[] newArray(int size) {
            return new Gank[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            Gank other = (Gank) obj;
            return this._id.equals(other.get_id());
        }
    }
}
