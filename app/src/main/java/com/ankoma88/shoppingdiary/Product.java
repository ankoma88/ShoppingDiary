package com.ankoma88.shoppingdiary;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

public class Product {

    private static final String JSON_ID = "id";
    private static final String JSON_PRODUCT_NAME = "product";
    private static final String JSON_DATE = "date";
    private static final String JSON_PURCHASED = "purchased";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_COMMENT = "comment";

    
    private UUID mId;
    private String mProductName;
    private Date mDate;
    private boolean mPurchased;
    private Photo mPhoto;
    private String mComment;

    
    public Product() {
        mId = UUID.randomUUID();
        mDate = new Date();
        mComment = "";
    }

    public Product(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        mProductName = json.getString(JSON_PRODUCT_NAME);
        mComment = json.getString(JSON_COMMENT);
        mPurchased = json.getBoolean(JSON_PURCHASED);
        mDate = new Date(json.getLong(JSON_DATE));
        if (json.has(JSON_PHOTO))
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_PRODUCT_NAME, mProductName);
        json.put(JSON_PURCHASED, mPurchased);
        json.put(JSON_DATE, mDate.getTime());
        json.put(JSON_COMMENT, mComment);
        if (mPhoto != null)
            json.put(JSON_PHOTO, mPhoto.toJSON());
        return json;
    }

    @Override
    public String toString() {
        return mProductName;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        mProductName = productName;
    }

    public UUID getId() {
        return mId;
    }

    public boolean isPurchased() {
        return mPurchased;
    }

    public void setPurchased(boolean purchased) {
        mPurchased = purchased;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }
    
    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo p) {
        mPhoto = p;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }
}
