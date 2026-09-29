package com.deeptally.modetest;
public class Session {
  public final long id,startMs,endMs;
  public Session(long id,long s,long e){this.id=id;startMs=s;endMs=e;}
  public long durationMs(){return Math.max(0,endMs-startMs);}
  public boolean valid(){return durationMs()>=TimeUtils.THRESHOLD_MS;}
}
