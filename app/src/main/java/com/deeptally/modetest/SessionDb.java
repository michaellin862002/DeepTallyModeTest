package com.deeptally.modetest;
import android.content.*;import android.database.*;import android.database.sqlite.*;import java.util.*;
public class SessionDb extends SQLiteOpenHelper{
  public SessionDb(Context c){super(c,"deep_tally.db",null,1);}
  public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE sessions(id INTEGER PRIMARY KEY AUTOINCREMENT,start_ms INTEGER NOT NULL,end_ms INTEGER NOT NULL)");db.execSQL("CREATE INDEX idx_start ON sessions(start_ms)");}
  public void onUpgrade(SQLiteDatabase db,int o,int n){}
  public long add(long s,long e){ContentValues v=new ContentValues();v.put("start_ms",s);v.put("end_ms",e);return getWritableDatabase().insertOrThrow("sessions",null,v);}
  public void update(long id,long s,long e){ContentValues v=new ContentValues();v.put("start_ms",s);v.put("end_ms",e);getWritableDatabase().update("sessions",v,"id=?",new String[]{""+id});}
  public void delete(long id){getWritableDatabase().delete("sessions","id=?",new String[]{""+id});}
  public List<Session> between(long a,long b){ArrayList<Session>x=new ArrayList<>();Cursor c=getReadableDatabase().query("sessions",new String[]{"id","start_ms","end_ms"},"start_ms>=? AND start_ms<? AND (end_ms-start_ms)>=600000",new String[]{""+a,""+b},null,null,"start_ms DESC");try{while(c.moveToNext())x.add(new Session(c.getLong(0),c.getLong(1),c.getLong(2)));}finally{c.close();}return x;}
  public List<Session> all(){ArrayList<Session>x=new ArrayList<>();Cursor c=getReadableDatabase().query("sessions",new String[]{"id","start_ms","end_ms"},"(end_ms-start_ms)>=600000",null,null,null,"start_ms DESC");try{while(c.moveToNext())x.add(new Session(c.getLong(0),c.getLong(1),c.getLong(2)));}finally{c.close();}return x;}
}
