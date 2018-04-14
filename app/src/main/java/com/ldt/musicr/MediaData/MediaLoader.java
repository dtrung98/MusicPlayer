package com.ldt.musicr.MediaData;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.ldt.musicr.MediaData.FormatDefinition.Field;
import com.ldt.musicr.MediaData.FormatDefinition.SimpleSong;
import com.ldt.musicr.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by trung on 8/16/2017.
 */

public class MediaLoader {
    public static ArrayList<SimpleSong> simpleSongs = new ArrayList<>();
    private static Map<Integer,Map<String,Object>> Properties = new HashMap<>();
    private static Cursor songCursor,albumCursor,playlistCursor,artistCursor;
    public static void createNewSimpleSong(int whichProperty)
    {
        int WhichSong = whichProperty;
        int Id_Resource = R.drawable.default_image2;
        String Path_Bitmap = getStringFromStringKey(whichProperty,"Path Bitmap");
        String Data = getStringFromStringKey(whichProperty,"Data") ;
        String Title = getStringFromStringKey(whichProperty,"Title");
        String Artist = getStringFromStringKey(whichProperty,"Artist");
        simpleSongs.add(new SimpleSong(whichProperty,Id_Resource,Path_Bitmap,Data,Title,Artist));
    }
    public static Map<String,Object> getProperty(int whichProperty)
    {
        Boolean b = Properties.containsKey(whichProperty);
        if(!b) Properties.put(whichProperty,new HashMap<String,Object>());
        return Properties.get(whichProperty);
    }
    public static void removePropertyFromMap(int whichProperty) {
        Map<String,Object> Property = Properties.get(whichProperty);
        if(Property==null) return;
        Property.clear();
        Properties.remove(whichProperty);
    }
    public static  void removeAllProperties()
    {
        for(Map<String,Object> entry : Properties.values()) {

             entry.clear();
            Properties.remove(entry);
            // do what you have to do here
            // In your case, an other loop.
        }
        Properties.clear();
    }
    public static String getStringFromStringKey(int whichProperty,String key)
    {
        Object object = getProperty(whichProperty).get(key);
        if(object ==null) Properties.get(whichProperty).put(key,"");
        if(object instanceof String)
            return (String) object;
        return "";
    }
    public static int getIntFromStringKey(int whichProperty,String key)
    {
        Object object =  getProperty(whichProperty).get(key);
        if(object ==null) Properties.get(whichProperty).put(key,0);
        if(object instanceof Integer)
            return (int)object;
        return 0;
    }
    public static boolean getBooleanFromStringKey(int whichProperty,String key)
    {
           Object object =  getProperty(whichProperty).get(key);
        if(object ==null) Properties.get(whichProperty).put(key,false);
        if(object instanceof Boolean)
            return (boolean)object;
        return false;
    }
    public static void setStringFromStringKey(int whichProperty,String key,String value)
    {
        getProperty(whichProperty).put(key,value);
    }
    public static void setIntFromStringKey(int whichProperty,String key, Integer value)
    {
        getProperty(whichProperty).put(key,value);
    }
    public static void setBooleanFromStringKey(int whichProperty, String key, Boolean value)
    {
        getProperty(whichProperty).put(key,value);
    }
    public static int IncreaseIntFromStringKey(int whichProperty,String key)
    {
        Map<String,Object> Property = getProperty(whichProperty);

        Object value = Property.get(key);
        int result ;
        if(! (value instanceof Integer)) return -1;

        int value_int = (int)value;
        if(value == null) result =1;
        else result = value_int +1;
        Property.put(key,result);
        return result;
    }
    public static int  DecreaseIntFromStringKey(int whichProperty, String key)
    {
        Map<String,Object> Property = getProperty(whichProperty);
       Object value = Property.get(key);
        int result ;
        if(! (value instanceof Integer)) return -1;
        int value_int = (int)value;
        if(value == null) result =0;
       else if(value_int ==0) result= 0;
        else result = value_int -1;
        Property.put(key,result);
        return result;
    }
    public static int  CheckTypeThisStringKey (int whichProperty, String key)
    {
      Map<String,Object> Property = getProperty(whichProperty);
        Object object = Property.get("Love");
        if(object == null) return -1;
        if(object instanceof Integer ) return 0;
        if(object instanceof Boolean ) return 1;
        if(object instanceof String) return 2;
        return 3;
    }

    public static Map<Integer,Map<String,Object>> getProperties() {
        return Properties;
    }
        public static void Prepare2Refresh()
        {

        }
        private Map<String,String> get_AProperty_From_APropertiesMap(Map<Integer,Map<String,String>> mapOfMap, int id)
        {
            return  mapOfMap.get(id);
        }

        public static void StartToRefresh(ContentResolver CR)
        {
               Prepare2Refresh();
               RunAllLoader(CR);
        }
    private static Uri[] getUris()
    {
        Uri song = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri album = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Uri  playlist = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Uri  artist  = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        return new Uri[] {song,album,playlist,artist};
    }
    public static void RunAllLoader(ContentResolver CR)
    {
        runSongLoader(CR);
    }
    public static boolean SET_CURSORS = false;
    private static void setCursors(ContentResolver CR)
    {
        Uri[] uri = getUris();
        Uri songUri = uri[0];
        Uri albumUri = uri[1];
        Uri playlistUri = uri[2];
        Uri artistUri = uri[3];

        songCursor = CR.query(songUri, null, null, null, null);
        albumCursor = CR.query(albumUri,null,null,null,null);
        playlistCursor = CR.query(playlistUri,null,null,null,null);
        artistCursor = CR.query(artistUri,null,null,null,null);
    }
    private static void closeAllCursors()
    {
        songCursor.close();
        albumCursor.close();
        playlistCursor.close();
        artistCursor.close();
    }
    /*
    Hàm này mỗi lần được gọi sẽ dọn sạch danh sách phát bài hát,
     sau đó nạp lại vào danh sách mới lấy từ ContentResolver.
     Hàm này thực ra là hàm lấy properties của bài hát, 1 bài hát không được định nghĩa như vậy.
      Properties chứa 1 ID phân biệt, mỗi bài hát chứa 1 trường là ID đó, coi như đính properties đó bài hát
     */
  static int COUNT =0;
    public static void runSongLoader(ContentResolver CR)
    {
        if(!SET_CURSORS) setCursors(CR);  // Nếu chưa cài con trỏ thì cài nó trước.
        MS_AudioField.InitSongFields();   //  Kêu gọi tạo các trường Properties cho Bài Hát, hàm này nếu phát hiện đã làm rồi thì không làm nữa.
       removeAllProperties();
        simpleSongs.clear();
        Cursor cur = songCursor;   // lấy Con trỏ bài hát
        if (cur == null || !cur.moveToFirst()) return; // Nếu con trỏ Null , hoặc không thể đưa nó về vị trí đầu tiên, thì thoát luôn.

        do
        {  // loader

            int len = MS_AudioField.songFields.size();
            for( int i=0; i<len; i++) {
                Field sf = MS_AudioField.songFields.get(i);
                String field = sf.getField();
                String name = sf.getName();
                int element = cur.getColumnIndex(field);
                int type = cur.getType(element);
                String str=name+" : ";
           //     if(i==3||i==6||i==9) {
              //      byte[] s = cur.getBlob(element);
                 //   str+= Tool.convertByteArrayToString(s);
                //} else
                if(type==1)  str+=  cur.getInt(element);
                else if(type==2) str += cur.getFloat(element);
                else if(type==3) str += cur.getString(element);
                else str+="=<Unknown>, Type =["+type+"]";

                setStringFromStringKey(COUNT,name,str);
            }
            createNewSimpleSong(COUNT);
            COUNT++;
        }
        while (cur.moveToNext());
    }






}
