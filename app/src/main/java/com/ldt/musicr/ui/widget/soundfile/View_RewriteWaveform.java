package com.ldt.musicr.ui.widget.soundfile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ldt.musicr.util.Tool;
import com.ldt.musicr.R;

/**
 * Created by trung on 9/14/2017.
 */

public class View_RewriteWaveform extends View {

    public View_RewriteWaveform(Context context, AttributeSet attrs) {
        super(context,attrs);
        sizeEachDPs = Tool.getOneDps(context);

        widthOfLine*=sizeEachDPs;
        runFirstTime();
    }
    enum Status {
        Non_Initialize,  // Chưa đính file nhạc
        Initializing,  // vừa đính file nhạc và đang chờ xử lý dữ liệu
        Initialized, // Render Sound Wave
        Pausing, // Tạm ngừng
        Deleting // Đã xóa file nhạc
    }
    enum onGesture {

    }
    public Status status;
    public String filePath;
    public int total_time;
    public int now_time;
    protected Paint mSelectedLinePaint;
    protected Paint mSelectedBelowLinePaint;
    protected Paint mUnselectedLinePaint;
    protected Paint mUnselectedBelowLinePaint;

    protected int mSampleRate;
    protected int mSamplesPerFrame;

    protected CheapSoundFile mSoundFile;

    public void setStatus_BySafeWay(Status status_bySafeWay)
    {
        status = status_bySafeWay;
    }
    private void runFirstTime()
    {
        mSelectedLinePaint = new Paint();
        mSelectedLinePaint.setAntiAlias(false);
        mSelectedLinePaint.setColor(getResources().getColor(R.color.waveform_selected));
        mSelectedBelowLinePaint= new Paint();
        mSelectedBelowLinePaint.setAntiAlias(false);
        mSelectedBelowLinePaint.setColor(getResources().getColor(R.color.waveform_selected));
        mSelectedBelowLinePaint.setAlpha(150);
        mSelectedLinePaint.setAlpha(210);
        mUnselectedLinePaint = new Paint();
        mUnselectedLinePaint.setAntiAlias(false);
        mUnselectedLinePaint.setColor(getResources().getColor(R.color.waveform_unselected));
        mUnselectedBelowLinePaint = new Paint();
        mUnselectedBelowLinePaint.setAntiAlias(false);
        mUnselectedBelowLinePaint.setColor(getResources().getColor(R.color.waveform_unselected));
        mUnselectedBelowLinePaint.setAlpha(150);
        mUnselectedLinePaint.setAlpha(210);

        status = Status.Non_Initialize;
    }

    public boolean hasSoundFile() {
        return mSoundFile != null;
    }

    public void setSoundFile(CheapSoundFile soundFile) {
        mSoundFile = soundFile;
        mSampleRate = mSoundFile.getSampleRate();
        mSamplesPerFrame = mSoundFile.getSamplesPerFrame();
    }

    public boolean isInitialized() {
       return status!=Status.Non_Initialize;
    }
    private int widthOfLine = 5;
    private float sizeEachDPs=1;
    protected void drawWaveformLine(Canvas canvas, int x, int y0, int y1, Paint paint) {
        paint.setStrokeWidth(widthOfLine*2/3);
        canvas.drawLine(x, y0, x, y1, paint);
        //  canvas.drawRect(x,y1,x+widthOfLine,y0,paint);
        drawWaveformLine_New(canvas,x,y0,y1,paint);
    }
    protected void drawWaveformLine_New(Canvas canvas,int x,int y0,int y1,Paint paint)
    {
        //  canvas.drawRect(x,y1,x+1,y0,paint);
    }

}
