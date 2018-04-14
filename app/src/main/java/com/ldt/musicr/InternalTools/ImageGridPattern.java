package com.ldt.musicr.InternalTools;

/**
 * Created by trung on 12/9/2017.
 */

public class ImageGridPattern {

        // Vị trí mốc cho bức ảnh
    private float OriginalRealPos[] ;

      // Kích thước thật của bức ảnh
    private float RealSize[];

      // Kích thước xem trước
    private float PreSize[];
    private boolean HasSet[];
    private boolean HasSetAll;
    private float Unit[];
     public ImageGridPattern() {
         HasSet = new boolean[3];
         OriginalRealPos = new float[] {0,0};
     }
     public void setHasSet(int id) {
         HasSet[id] = true;
         if(!HasSetAll&&HasSet[1]&&HasSet[2])
         {
             HasSetAll = true;

             float unitX = (float)RealSize[0] / PreSize[0];
             float unitY = (float) RealSize[1] / PreSize[1];

             Unit = new float [] {unitX,unitY};
         }
     }
    public float[] getOriginalRealPos()
    {
         return OriginalRealPos;
    }

    public void setOriginalRealPos(float[] originalRealPos) {
        OriginalRealPos = originalRealPos;
        setHasSet(0);
    }
    public void setOriginalRealPos(float x, float y ) {
         OriginalRealPos = new float[] {x,y};
        setHasSet(0);
    }

    public float[] getRealSize() {
        return RealSize;
    }

    public void setRealSize(float[] realSize) {
        RealSize = realSize;
        setHasSet(1);
    }
    public void setPreSize(float w,float h) {
         PreSize = new float[] {w,h};
        setHasSet(2);
    }

    public void setRealSize(float w,float h) {
        RealSize = new float[] {w,h};
        setHasSet(1);
    }
    public float[] getPreSize() {
        return PreSize;
    }

    public void setPreSize(float[] preSize) {
        PreSize = preSize;
        setHasSet(2);

    }

    public void checkSet() throws Exception {
         if(!HasSetAll) throw new Exception("Some Properties haven't set");
    }
    public int getXReal(float xInPreview) throws Exception {
         checkSet();
         return (int)((float)OriginalRealPos[0] + (float)xInPreview*Unit[0]);
    }
    public int getYReal(float yInPreview) throws Exception {
         checkSet();
        return (int)((float)OriginalRealPos[1] + (float)yInPreview*Unit[1]);
    }

    public float[] points;
    public int[] realPoint;
    public int numberPoints = 0;
    private void calculateRealPoint() {

    }
    public void setMultiplePoint(float[] multiplePoints) {
        points = multiplePoints;
        numberPoints = points.length;
        calculateRealPoint();

    }
}
