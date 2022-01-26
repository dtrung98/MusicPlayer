package com.ldt.musicr.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.ldt.musicr.R;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.utils.ArtworkUtils;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PlaylistArtworkGenerator {
    public static Bitmap getBitmap(Context context, List<Song> songPlaylist, boolean round, boolean blur) {
        if (songPlaylist == null) return null;
        // lấy toàn bộ album id, loại bỏ trùng nhau
        final List<Song> uniqueAlbumSongList = new ArrayList<>();
        final Set<Integer> uniqueAlbumSet = new HashSet<>();

        for (Song song : songPlaylist) {
            if(!uniqueAlbumSet.contains(song.albumId)) {
                uniqueAlbumSet.add(song.albumId);
                uniqueAlbumSongList.add(song);
            }
        }

        // lấy toàn bộ art tồn tại
        ArrayList<Bitmap> art = new ArrayList<>();
        for (Song song : uniqueAlbumSongList) {
            Bitmap bitmap = getArtworkBitmap(context, song);
            if (bitmap != null) {
                art.add(bitmap);
            }
            if (art.size() == 6) {
                break;
            }
        }

        Bitmap ret;
        switch (art.size()) {
            // lấy hình mặc định
            case 0:
                ret = getDefaultBitmap(context, round).copy(Bitmap.Config.ARGB_8888, false);
                break;
            // dùng hình duy nhất
            case 1:
                if (round)
                    ret = BitmapEditor.getRoundedCornerBitmap(art.get(0), art.get(0).getWidth() / 40);
                else ret = art.get(0);
                break;
            // từ 2 trở lên ta cần vẽ canvas
            default:
                ret = getBitmapCollection(art, round);
        }
        int w = ret.getWidth();
        if (blur)
            return BitmapEditor.GetRoundedBitmapWithBlurShadow(context, ret, w / 24, w / 24, w / 24, w / 24, 0, 200, w / 40, 1);

        return ret;
    }

    private static Bitmap getBitmapCollection(ArrayList<Bitmap> art, boolean round) {
        // lấy kích thước là kích thước của bitmap lớn nhất
        int maxSize = art.get(0).getWidth();
        int maxSizeDivideTwo = maxSize / 2;
        for (Bitmap b : art) if (maxSize < b.getWidth()) maxSize = b.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(maxSize, maxSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(maxSize / 100f);
        paint.setColor(0xffffffff);
        switch (art.size()) {
            case 2:
                canvas.drawBitmap(art.get(1), null, new Rect(0, 0, maxSize, maxSize), null);
                canvas.drawBitmap(art.get(0), null, new Rect(-maxSize / 2, 0, maxSize / 2, maxSize), null);
                canvas.drawLine(maxSizeDivideTwo, 0, maxSizeDivideTwo, maxSize, paint);
                break;
            case 3:
                canvas.drawBitmap(art.get(0), null, new Rect(-maxSize / 4, 0, 3 * maxSize / 4, maxSize), null);
                canvas.drawBitmap(art.get(1), null, new Rect(maxSizeDivideTwo, 0, maxSize, maxSize / 2), null);
                canvas.drawBitmap(art.get(2), null, new Rect(maxSizeDivideTwo, maxSizeDivideTwo, maxSize, maxSize), null);
                canvas.drawLine(maxSizeDivideTwo, 0, maxSizeDivideTwo, maxSize, paint);
                canvas.drawLine(maxSizeDivideTwo, maxSizeDivideTwo, maxSize, maxSizeDivideTwo, paint);
                break;
            case 4:
                canvas.drawBitmap(art.get(0), null, new Rect(0, 0, maxSize / 2, maxSize / 2), null);
                canvas.drawBitmap(art.get(1), null, new Rect(maxSize / 2, 0, maxSize, maxSize / 2), null);
                canvas.drawBitmap(art.get(2), null, new Rect(0, maxSize / 2, maxSize / 2, maxSize), null);
                canvas.drawBitmap(art.get(3), null, new Rect(maxSize / 2, maxSize / 2, maxSize, maxSize), null);
                canvas.drawLine(maxSizeDivideTwo, 0, maxSizeDivideTwo, maxSize, paint);
                canvas.drawLine(0, maxSizeDivideTwo, maxSize, maxSizeDivideTwo, paint);
                break;
            // default: canvas.drawBitmap(art.get(0),null,new Rect(0,0,max_width,max_width),null);
            default:

                // độ rộng của des bitmap
                float w = (float) (Math.sqrt(2) / 2 * maxSize);
                float b = (float) (maxSize / Math.sqrt(5));
                // khoảng cách định nghĩa, dùng để tính vị trí tâm của 4 bức hình xung quanh
                float d = (float) (maxSize * (0.5f - 1 / Math.sqrt(10)));
                float deg = 45;

                for (int i = 0; i < 5; i++) {
                    canvas.save();
                    switch (i) {
                        case 0:
                            canvas.translate(maxSizeDivideTwo, maxSizeDivideTwo);
                            canvas.rotate(deg);
                            // b = (float) (max_width*Math.sqrt(2/5f));
                            canvas.drawBitmap(art.get(0), null, new RectF(-b / 2, -b / 2, b / 2, b / 2), null);
                            break;
                        case 1:
                            canvas.translate(d, 0);
                            canvas.rotate(deg);
                            canvas.drawBitmap(art.get(i), null, new RectF(-w / 2, -w / 2, w / 2, w / 2), null);
                            paint.setAntiAlias(true);
                            canvas.drawLine(w / 2, -w / 2, w / 2, w / 2, paint);
                            break;
                        case 2:
                            canvas.translate(maxSize, d);
                            canvas.rotate(deg);
                            canvas.drawBitmap(art.get(i), null, new RectF(-w / 2, -w / 2, w / 2, w / 2), null);
                            paint.setAntiAlias(true);
                            canvas.drawLine(-w / 2, w / 2, w / 2, w / 2, paint);
                            break;
                        case 3:
                            canvas.translate(maxSize - d, maxSize);
                            canvas.rotate(deg);
                            canvas.drawBitmap(art.get(i), null, new RectF(-w / 2, -w / 2, w / 2, w / 2), null);
                            paint.setAntiAlias(true);
                            canvas.drawLine(-w / 2, -w / 2, -w / 2, w / 2, paint);
                            break;
                        case 4:
                            canvas.translate(0, maxSize - d);
                            canvas.rotate(deg);
                            canvas.drawBitmap(art.get(i), null, new RectF(-w / 2, -w / 2, w / 2, w / 2), null);
                            paint.setAntiAlias(true);
                            canvas.drawLine(-w / 2, -w / 2, w / 2, -w / 2, paint);
                            break;
                    }
                    canvas.restore();
                }


        }
        if (round)
            return BitmapEditor.getRoundedCornerBitmap(bitmap, bitmap.getWidth() / 40);
        else return bitmap;
    }

    private static Bitmap getArtworkBitmap(@NonNull Context context, Song song) {
        try {
            return ArtworkUtils.getBitmapRequestBuilder(context, song).submit().get();
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getDefaultBitmap(@NonNull Context context, boolean round) {
        if (round)
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image_round);
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image2);
    }

}
