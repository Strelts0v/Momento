package com.vg.momento.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.UUID;

public class Moment {

    private UUID mId;

    private String mTitle;

    private Date mDate;

    private boolean mIsFavorite;

    private String mPeople;

    public Moment() {
        mId = UUID.randomUUID();
        mDate = new Date();
        mTitle = "";
        mIsFavorite = false;
        mPeople = "";
    }

    public Moment(UUID id){
        mId = id;
        mDate = new Date();
        mTitle = "";
        mIsFavorite = false;
        mPeople = "";
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean favorite) {
        mIsFavorite = favorite;
    }

    public String getPeople() {
        return mPeople;
    }

    public void setPeople(String people) {
        mPeople = people;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Moment moment = (Moment) o;

        return new EqualsBuilder()
                .append(mIsFavorite, moment.mIsFavorite)
                .append(mId, moment.mId)
                .append(mTitle, moment.mTitle)
                .append(mDate, moment.mDate)
                .append(mPeople, moment.mPeople)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mId)
                .append(mTitle)
                .append(mDate)
                .append(mIsFavorite)
                .append(mPeople)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mId", mId)
                .append("mTitle", mTitle)
                .append("mDate", mDate)
                .append("mIsFavorite", mIsFavorite)
                .append("mPeople", mPeople)
                .toString();
    }
}
