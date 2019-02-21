package com.ldt.musicr.ui.main;

public class Attr {

    private LayerControllerV2 mController;
    public LayerControllerV2 getLayerController() {
        return  mController;
    }

    public void setLayerController(LayerControllerV2 c) {
        mController = c;
    }

    public Attr() {
        mUpInterpolator = mDownInterpolator = 4;
        mUpDuration = 400;
        mDownDuration = 500;
        mInitDuration = 1000;
        mPercent = 0;
    }

    public int getId() {
        return mId;
    }

    public Attr setId(int id) {
        mId = id;
        return this;
    }

    int mId;

    int mInitY;
    int mInitX;
    int mDestY;
    int mDestX;

    public float getPercent() {
        return mPercent;
    }

    /**
     * save the percent of the distance layer move
     * @param percent percentage
     * @return the current position with that percent
     */
    public int setPercent(float percent) {
        mPercent = percent;
        int cur =  (int) (mInitY + (mDestY - mInitY)*mPercent);
        if(mController!=null) mController.postMyPercent(this,percent);

        return cur;
    }
    public void updatePercent(float percent) {
        mPercent = percent;
    }

    float mPercent =0;

    int mUpInterpolator;
    int mDownInterpolator;
    int mUpDuration;
    int mDownDuration;
    int mInitDuration;


    public int getInitY() {
        return mInitY;
    }

    public Attr setInitY(int initY) {
        mInitY = initY;
        return this;
    }

    public int getInitX() {
        return mInitX;
    }

    public Attr setInitX(int initX) {
        mInitX = initX;
        return this;
    }

    public int getDestY() {
        return mDestY;
    }

    public Attr setDestY(int destY) {
        mDestY = destY;
        return this;
    }

    public int getDestX() {
        return mDestX;
    }

    public Attr setDestX(int destX) {
        mDestX = destX;
        return this;
    }

    public int getUpInterpolator() {
        return mUpInterpolator;
    }

    public Attr setUpInterpolator(int upInterpolator) {
        mUpInterpolator = upInterpolator;
        return this;
    }

    public int getDownInterpolator() {
        return mDownInterpolator;
    }

    public Attr setDownInterpolator(int downInterpolator) {
        mDownInterpolator = downInterpolator;
        return this;
    }

    public int getUpDuration() {
        return mUpDuration;
    }

    public Attr setUpDuration(int upDuration) {
        mUpDuration = upDuration;
        return this;
    }

    public int getDownDuration() {
        return mDownDuration;
    }

    public Attr setDownDuration(int downDuration) {
        mDownDuration = downDuration;
        return this;
    }

    public int getInitDuration() {
        return mInitDuration;
    }

    public Attr setInitDuration(int initDuration) {
        mInitDuration = initDuration;
        return this;
    }

}
