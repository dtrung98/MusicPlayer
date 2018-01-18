package com.ldt.NewDefinitionMusicApp.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.*;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldt.NewDefinitionMusicApp.InternalTools.ImageEditor;
import com.ldt.NewDefinitionMusicApp.InternalTools.Motion;
import com.ldt.NewDefinitionMusicApp.InternalTools.helper;
import com.ldt.NewDefinitionMusicApp.MediaData.FormatDefinition.Album;
import com.ldt.NewDefinitionMusicApp.fragments.MainScreenFragment;
import com.ldt.NewDefinitionMusicApp.services.MediaService;
import com.ldt.NewDefinitionMusicApp.InternalTools.Tool;
import com.ldt.NewDefinitionMusicApp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.ldt.NewDefinitionMusicApp.activities.Choose_Playlist.Inform.bitm;
import static com.ldt.NewDefinitionMusicApp.activities.Choose_Playlist.Inform.bottom;
import static com.ldt.NewDefinitionMusicApp.activities.Choose_Playlist.Inform.left;
import static com.ldt.NewDefinitionMusicApp.activities.Choose_Playlist.Inform.right;
import static com.ldt.NewDefinitionMusicApp.activities.Choose_Playlist.Inform.top;

public class Choose_Playlist extends AppCompatActivity {
/*
  Guide about SurfaceView : sufaceview is able to be transparent if it is the y of everything, meaning that noone is over it.
  If Surface is the middle of Z, it cannot be transparent.
 */
    SurfaceView surfaceView = null ;
    SurfaceHolder surfaceHolder;
    private void setSurfaceView()
    {
        if(surfaceView!=null) return;
        surfaceView = new SurfaceView(Choose_Playlist.this);
        surfaceHolder = surfaceView.getHolder();

        relat(R.id.myRelatIsBackOfEverything).addView(surfaceView,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));
      //  surfaceView.setZOrderMediaOverlay(true);
   //     surfaceView.setZOrderOnTop(true);
      //  sendViewToBack(surfaceView);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback2() {
            @Override
            public void surfaceRedrawNeeded(SurfaceHolder holder) {
                Canvas surfaceCanvas = holder.lockCanvas();

                surfaceCanvas.drawBitmap(buffer, 0, 0, new Paint());

                holder.unlockCanvasAndPost(surfaceCanvas);
                Log.d("Is ","surfaceRedrawNeeded");
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
          //      Canvas surfaceCanvas = holder.lockCanvas();

          //      surfaceCanvas.drawBitmap(buffer, 0, 0, new Paint());

             //   holder.unlockCanvasAndPost(surfaceCanvas);
                Log.d("Is ","surfaceCreated");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //    Canvas surfaceCanvas = holder.lockCanvas();

               // surfaceCanvas.drawBitmap(buffer, 0, 0, new Paint());

              //  holder.unlockCanvasAndPost(surfaceCanvas);
                Log.d("Is ","surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
             //   Canvas surfaceCanvas = holder.lockCanvas();

              //  surfaceCanvas.drawBitmap(buffer, 0, 0, new Paint());

              //  holder.unlockCanvasAndPost(surfaceCanvas);
                Log.d("Is ","surfaceDestroyed");

            }
        });


    }
    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup)child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose__playlist);
        getAlbumList_AfterAskedPermission();
        setAlbumArrayList_ListView();
        statusHeight = Tool.getStatusHeight(getResources());
        setStatusDependOnVersion();
    }
    private void setStatusDependOnVersion() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            MainScreenFragment.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        }
        if (Build.VERSION.SDK_INT >= 21) {
            MainScreenFragment.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            (relat(R.id.choosePlaylist_root)).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
       
    }
    private Intent ex_service_intent=null;
    private LinearLayout linr(int id) {return (LinearLayout)findViewById(id);}
    private RelativeLayout relat(int id)
    {
        return (RelativeLayout)findViewById(id);
    }
int statusHeight=0;
    Bitmap buffer =null;
    int ScreenWidth=0,ScreenHeight=0;

    public void onClick_AllPlaylist(View view) {
        if(ex_service_intent!=null)
        stopService(ex_service_intent);
    }

    public static class Inform {
       public static float left,top,right,bottom;
       public static  Bitmap bitm=null;
    }

    private void drawBuffer(int zleft,int ztop,int zright,int zbottom,Bitmap zbitm)
    {
        if(buffer==null) {
            setSurfaceView();
            Display d = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            ScreenWidth = d.getWidth();
            ScreenHeight = d.getHeight();

            buffer = Bitmap.createBitmap(ScreenWidth, ScreenHeight, Bitmap.Config.ARGB_8888);

        }
        timing=0;
        bitm=zbitm ;
        left=zleft +25;
        bottom=zbottom-25;
        right=zright -25;
        top=ztop+25;
        ended=false;
        surfaceView.setVisibility(View.VISIBLE);
      ((ImageView)findViewById(R.id.buffer)).setVisibility(View.INVISIBLE);
        if(prev_buffer!=null) prev_buffer.recycle();
        if(buffer!=null) prev_buffer = buffer.copy(buffer.getConfig(),true);

        if(!started)

        start();


    }


    private boolean started = false;
    private Handler handler = new Handler();
    private Bitmap prev_buffer=null;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (started) {
                start();
            }
        }
    };
    public void start() {
        //  running code here
        started=true;
        running();

        handler.postDelayed(runnable, 0);
    }
    float timing=0;
    float deltaTime=0;
    float countTime=0;
    float timeMotion=0.75f;
  //  float timeMotion=6f;
    private void calculateDelta()
    {

        float now = System.nanoTime()/ 1000000000.0f;
        deltaTime= now - countTime;
        countTime=now;
        if(deltaTime<1)
        timing+=deltaTime;


    } public void stop() {
        started = false;
        handler.removeCallbacks(runnable);
    }
    boolean ended=false;
    private void running() {
        calculateDelta();



     //   float TiLe = DrawCanvas.easeOutQuad(timing, 0, 1, 0.45f);
        if(surfaceHolder.getSurface().isValid()){
            {
                surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
            }

     if(!ended)
        {
            //
            float TiLe = Motion.easeOutQuad(timing, 0, 1, timeMotion);
            if(timing+0.025f>timeMotion) {
                TiLe = 1;
                ended=true;
         //       surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
                Log.d("Block ! ","Capture");
            }
            else
            Log.d("Is Running : Tile = "+TiLe,"Capture");
            buffer.eraseColor(Color.TRANSPARENT);
            Canvas canvas_buffer = new Canvas(buffer);

            Canvas canvas_holder = surfaceHolder.lockCanvas();
            canvas_holder.drawColor(Color.WHITE);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
             Bitmap ex1 = ImageEditor.getRoundedCornerBitmap(bitm, 30-((int) (30 * TiLe)));


          //  paint.setAlpha(((int)(255*(1-TiLe))));
            if(false) {
                Bitmap prev_ex1 = ImageEditor.getRoundedCornerBitmap(prev_buffer, ((int) (30 * TiLe)));
                canvas_buffer.drawBitmap(prev_ex1, new Rect(0, 0, prev_ex1.getWidth(), prev_ex1.getHeight()), new RectF(ScreenWidth * (TiLe) / 2, ScreenHeight * (TiLe) / 2, ScreenWidth * (1 - TiLe / 2), ScreenHeight * (1 - TiLe / 2)), paint);
                prev_ex1.recycle();
            }
            else if(TiLe<1f)
            {

                 paint.setAlpha(((int)(255*(1f-TiLe))));
                canvas_buffer.drawBitmap(prev_buffer,0,0,paint);

            }

       //     buffer.eraseColor(Color.TRANSPARENT);
            paint.setAlpha(255);
            canvas_buffer.drawBitmap(ex1,new Rect(0,0, ex1.getWidth(),ex1.getHeight()),new RectF(left - left * TiLe, top - top * TiLe, right + (ScreenWidth - right) * TiLe, bottom + (ScreenHeight - bottom) * TiLe), paint);
            Bitmap ex2 = ImageEditor.getBlurredWithGoodPerformance(Choose_Playlist.this,buffer,1,20,1+6*TiLe); // Làm mờ hình ảnh chính, ảnh chính đã bị thu nhỏ

            canvas_buffer.drawBitmap(ex2,new Rect(0,0,ex2.getWidth(),ex2.getHeight()),new Rect(0,0,ScreenWidth,ScreenHeight),paint); // vẽ lên chính nó
            paint.setARGB(((int) (210 * TiLe)), 255, 255, 255);
              canvas_buffer.drawRect(0,0,ScreenWidth,ScreenHeight,paint);
            paint.setAlpha(255);
             canvas_holder.drawBitmap(buffer,0,0,paint);
            canvas_buffer.setBitmap(null);

            surfaceHolder.unlockCanvasAndPost(canvas_holder);
            if(ended) {

             ImageView view_buffer =   ((ImageView) findViewById(R.id.buffer));
                view_buffer.setImageBitmap(buffer);
                view_buffer.setVisibility(View.VISIBLE);
              //  surfaceView.setVisibility(View.GONE);
            }
            ex1.recycle();
            ex2.recycle();
        }
    }
    }
    public static float easeInOutQuad(float current,float start_value,float change_in_value,float duration) {
        current /= duration/2;
        if (current < 1) return change_in_value/2*current*current + start_value;
        current--;
        return -change_in_value/2 * (current*(current-2) - 1) + start_value;
    };
    private void setAlbumArrayList_ListView()
{
    ListView listView = (ListView)findViewById(R.id.choosePlaylist_listView);
    listView.setAdapter(new album_view_adapter());
    helper.getListViewSize(listView);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
     //       Toast.makeText(Choose_Playlist.this,"I am ListView "+posTop+"th",Toast.LENGTH_SHORT).show();

         //  List<View> lv =getAllViews(view);
  View IsimageView = ((ViewGroup)view).getChildAt(0);

           if(IsimageView instanceof ImageView)
           {
          //     Toast.makeText(Choose_Playlist.this,"ok",Toast.LENGTH_SHORT).show();
               int[] location = new int[2];
               IsimageView.getLocationOnScreen(location);
               int id_pos = ((myTag)(IsimageView.getTag())).Position;
               Bitmap bitm = albumArrayList.get(id_pos).getBitmap();
               drawBuffer(location[0],location[1],location[0]+ IsimageView.getWidth(),IsimageView.getHeight()+location[1],bitm);
           }
            final android.view.animation.Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_slow);
            MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 30);
            myAnim.setInterpolator(interpolator);
            view.startAnimation(myAnim);
        }
    });
}
    public ArrayList<Album> albumArrayList = new ArrayList<>();

    private void getAlbumList_AfterAskedPermission() {
        ContentResolver contentR = getContentResolver();
        Uri album = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor albumCursor = contentR.query(album, null, null, null, null);
        if (albumCursor != null && albumCursor.moveToFirst()) { // có chạy vòng đầu tiên hay không.
            albumArrayList.clear();  // Xóa hết phần tử cái đã.
            int art = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int title = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int artist = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);

            do {
                String art_path,title_text,artist_text;
                art_path= albumCursor.getString(art);
                title_text = albumCursor.getString(title);
                artist_text= albumCursor.getString(artist);
                Bitmap bitmap = BitmapFactory.decodeFile(art_path);
                if(bitmap==null) // khong ton tai art
                {
                    Bitmap bitmap1=  BitmapFactory.decodeResource(getResources(),R.drawable.default_image2);
                    if(ImageEditor.TrueIfBitmapBigger(bitmap1,300)) {
                        Log.d("Song","bigger");
                        bitmap = ImageEditor.getResizedBitmap(bitmap1, 300, 300);
                        bitmap1.recycle();
                    }
                    else bitmap = bitmap1;
                }
                albumArrayList.add(new Album(title_text,artist_text,bitmap));
            }
            while (albumCursor.moveToNext());
            }
        Collections.sort(albumArrayList, new Comparator<Album>() {
            public int compare(Album a,Album b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        }

    public void back_onclick(View view) {
        super.finish();
        overridePendingTransition(R.anim.keep_not_change,R.anim.fixed_slide_right_out);
    }
 public class myTag{
     int Position;
     public  myTag(int id_pos)
     {
         Position=id_pos;
     }
 }
    private class album_view_adapter extends BaseAdapter {
            @Override
            public int getCount() {
                return albumArrayList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View view, ViewGroup parent) {
               if(view==null)
                view = getLayoutInflater().inflate(R.layout.item_list_view, parent, false);
                final ImageView imageView = (ImageView) view.findViewById(R.id.item_album_view_image);
                TextView titleView = (TextView)view.findViewById(R.id.item_album_view_title);
                TextView artistView = (TextView)view.findViewById(R.id.item_album_view_aritst);
                final Album album = albumArrayList.get(position);
                imageView.setTag(new myTag(position));
                imageView.setImageBitmap(ImageEditor.GetBlurredBackground(Choose_Playlist.this,ImageEditor.getRoundedCornerBitmap(album.getBitmap(),15),25,25,25,25,-6,180,12,2));
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //  Toast.makeText(Choose_Playlist.this,"I am ImageView "+posTop+"th",Toast.LENGTH_LONG).show();
                        ImageView iv = (ImageView) v;
                        int[] location = new int[2];
                        iv.getLocationOnScreen(location);
                       int id_pos = ((myTag)(iv.getTag())).Position;
                        Bitmap bitm = albumArrayList.get(id_pos).getBitmap();
                        drawBuffer(location[0],location[1],location[0]+ iv.getWidth(),iv.getHeight()+location[1],bitm);
                        final android.view.animation.Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                      MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 30);
                       myAnim.setInterpolator(interpolator);
                        iv.startAnimation(myAnim);

                        if(ex_service_intent==null)
                        {
                            ex_service_intent = new Intent(Choose_Playlist.this,MediaService.class);

                        }
                        startService(ex_service_intent);
                    }
                });
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ImageView iv = (ImageView) v;
                        Toast.makeText(Choose_Playlist.this,"You long click on this ImageView!",Toast.LENGTH_SHORT).show();


                        return true;
                    }
                });
                titleView.setText(album.getTitle());
                artistView.setText(album.getArtist());
                Log.d("Vision ","ListView getView!");
                return view;
            }
        }
    static class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude = 1;
        private double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }

}


