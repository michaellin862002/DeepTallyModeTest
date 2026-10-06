package com.deeptally.modetest;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.net.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.json.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;

public class MainActivity extends Activity {
  private static final String PREF="deep_tally", KSTART="timer_start", KGOAL="goal_min",
    KBIO1="bio1",KBIO2="bio2",KD1="draft1",KD2="draft2",KUP="bio_updated";
  private static final long MIN_RECORD_MS=10L*60L*1000L, UNDO_NOT_SAVED=-2L;
  private static final int REQ_EXPORT_BACKUP=801, REQ_IMPORT_BACKUP=802;
  private static final String BACKUP_FORMAT="deep-tally-backup";
  private static final int BACKUP_VERSION=1;
  public static final String ACTION_DEEP_WORK_START="com.deeptally.DEEP_WORK_START";
  public static final String ACTION_DEEP_WORK_END="com.deeptally.DEEP_WORK_END";
  private SharedPreferences p; private SessionDb db; private FrameLayout content,root; private LinearLayout nav;
  private int tab=0; private CircleTimerView timer; private final Handler h=new Handler(Looper.getMainLooper());
  private boolean editingBio=false; private EditText b1,b2; private TextView undo;
  private long undoId=-1,undoStart=-1;
  private LocalDate shownWeek=TimeUtils.monday(LocalDate.now());
  private int weekSelectedDay=-1;
  private int historyYear=LocalDate.now().getYear();

  private final Runnable tick=new Runnable(){public void run(){long s=p.getLong(KSTART,0);if(s>0&&timer!=null){timer.setState(true,System.currentTimeMillis()-s);h.postDelayed(this,1000);}}};

  public void onCreate(Bundle b){super.onCreate(b);p=getSharedPreferences(PREF,MODE_PRIVATE);db=new SessionDb(this);if(!p.contains(KGOAL))p.edit().putInt(KGOAL,360).apply();shell();show(0);}
  protected void onDestroy(){h.removeCallbacksAndMessages(null);db.close();super.onDestroy();}

  private void shell(){
    root=new FrameLayout(this);root.setBackgroundColor(Color.rgb(247,248,249));
    LinearLayout col=new LinearLayout(this);col.setOrientation(LinearLayout.VERTICAL);
    content=new FrameLayout(this);col.addView(content,new LinearLayout.LayoutParams(-1,0,1));
    nav=new LinearLayout(this);nav.setOrientation(LinearLayout.HORIZONTAL);nav.setBackgroundColor(Color.WHITE);
    String[] n={"Today","Week","History","我的自傳"};
    for(int i=0;i<4;i++){final int x=i;TextView v=Ui.text(this,n[i],13,Color.GRAY,false);v.setGravity(Gravity.CENTER);v.setOnClickListener(z->{Runnable go=()->openTabFromNav(x);if(editingBio&&x!=3)unsaved(go);else go.run();});nav.addView(v,new LinearLayout.LayoutParams(0,Ui.dp(this,58),1));}
    col.addView(nav);root.addView(col,new FrameLayout.LayoutParams(-1,-1));
    undo=Ui.text(this,"Session saved · UNDO",15,Color.WHITE,true);undo.setGravity(Gravity.CENTER);undo.setBackground(Ui.bg(this,Color.rgb(32,42,46),18));undo.setVisibility(View.GONE);undo.setOnClickListener(v->undoStop());
    FrameLayout.LayoutParams u=new FrameLayout.LayoutParams(-1,Ui.dp(this,50),Gravity.BOTTOM);u.setMargins(Ui.dp(this,18),0,Ui.dp(this,18),Ui.dp(this,68));root.addView(undo,u);setContentView(root);
  }

  private void openTabFromNav(int x){
    if(x==1){shownWeek=TimeUtils.monday(LocalDate.now());weekSelectedDay=-1;}
    show(x);
  }

  private void show(int x){tab=x;editingBio=false;h.removeCallbacks(tick);timer=null;content.removeAllViews();for(int i=0;i<4;i++){TextView v=(TextView)nav.getChildAt(i);v.setTextColor(i==x?Color.rgb(27,105,84):Color.rgb(110,118,122));v.setTypeface(i==x?android.graphics.Typeface.DEFAULT_BOLD:android.graphics.Typeface.DEFAULT);}if(x==0)today();else if(x==1)week();else if(x==2)history();else bio();}
  private ScrollView page(LinearLayout[] out){ScrollView s=Ui.scroll(this);LinearLayout q=Ui.page(this);s.addView(q);out[0]=q;return s;}
  private PeriodScrollView periodPage(LinearLayout[] out){PeriodScrollView s=new PeriodScrollView(this);s.setFillViewport(true);s.setClipToPadding(false);s.setBackgroundColor(Color.rgb(247,248,249));LinearLayout q=Ui.page(this);s.addView(q);out[0]=q;return s;}

  private void today(){
    LinearLayout[] o=new LinearLayout[1];ScrollView s=page(o);LinearLayout q=o[0];
    LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);TextView name=Ui.text(this,"Deep Tally",24,Color.rgb(27,32,35),true);top.addView(name,new LinearLayout.LayoutParams(0,-2,1));TextView set=Ui.text(this,"⚙",25,Color.DKGRAY,false);set.setPadding(Ui.dp(this,12),0,Ui.dp(this,8),0);set.setOnClickListener(v->settings());top.addView(set);q.addView(top);
    Ui.space(q,this,16);q.addView(Ui.text(this,"Today",18,Color.rgb(101,110,115),false));
    LocalDate d=LocalDate.now();long dayA=TimeUtils.start(d),dayB=TimeUtils.start(d.plusDays(1));List<Session> xs=db.overlapping(dayA,dayB);long total=TimeUtils.totalWithin(xs,dayA,dayB);q.addView(Ui.text(this,TimeUtils.natural(total),31,Color.rgb(25,31,34),true));Ui.space(q,this,20);
    timer=new CircleTimerView(this);long st=p.getLong(KSTART,0);timer.setState(st>0,st>0?System.currentTimeMillis()-st:0);timer.setOnClickListener(v->{hapticClick();if(p.getLong(KSTART,0)>0)stop();else start();});
    LinearLayout hold=new LinearLayout(this);hold.setGravity(Gravity.CENTER);hold.addView(timer,new LinearLayout.LayoutParams(Ui.dp(this,276),Ui.dp(this,276)));q.addView(hold);
    if(st>0){TextView x=Ui.text(this,"Completed today · "+TimeUtils.natural(total),13,Color.GRAY,false);x.setGravity(Gravity.CENTER);q.addView(x);h.post(tick);}
    Ui.space(q,this,28);
    LinearLayout sh=new LinearLayout(this);sh.setGravity(Gravity.CENTER_VERTICAL);
    sh.addView(Ui.text(this,"TODAY'S SESSIONS",13,Color.GRAY,true),new LinearLayout.LayoutParams(0,-2,1));
    sh.addView(addSessionButton(d),new LinearLayout.LayoutParams(Ui.dp(this,38),Ui.dp(this,38)));q.addView(sh);Ui.space(q,this,6);
    if(xs.isEmpty())q.addView(Ui.text(this,"No completed sessions yet.",15,Color.GRAY,false));else for(Session z:xs)q.addView(sessionRow(z,dayA,dayB));
    content.addView(s);
  }

  private void hapticClick(){
    try{
      if(Build.VERSION.SDK_INT>=31){
        VibratorManager vm=(VibratorManager)getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        if(vm!=null){Vibrator vib=vm.getDefaultVibrator();if(vib!=null&&vib.hasVibrator())vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));}
      }else{
        Vibrator vib=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        if(vib!=null&&vib.hasVibrator()){
          if(Build.VERSION.SDK_INT>=29)vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
          else if(Build.VERSION.SDK_INT>=26)vib.vibrate(VibrationEffect.createOneShot(18,VibrationEffect.DEFAULT_AMPLITUDE));
        }
      }
    }catch(Throwable ignored){}
  }

  private void sendDeepWorkSignal(String action){
    try{
      Intent i=new Intent(action);
      i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
      i.putExtra("source","Deep Tally");
      i.putExtra("timestamp",System.currentTimeMillis());
      sendBroadcast(i);
    }catch(Throwable ignored){}
  }

  private void start(){long now=System.currentTimeMillis();p.edit().putLong(KSTART,now).apply();sendDeepWorkSignal(ACTION_DEEP_WORK_START);show(0);}
  private void stop(){long st=p.getLong(KSTART,0),en=System.currentTimeMillis();if(st<=0||en<=st)return;long dur=en-st;undoStart=st;if(dur>=MIN_RECORD_MS){undoId=db.add(st,en);undo.setText("Session saved · UNDO");}else{undoId=UNDO_NOT_SAVED;undo.setText("Under 10 mins · not saved · UNDO");}p.edit().remove(KSTART).apply();sendDeepWorkSignal(ACTION_DEEP_WORK_END);undo.setVisibility(View.VISIBLE);h.postDelayed(()->{undo.setVisibility(View.GONE);undoId=-1;undoStart=-1;},5000);show(0);}
  private void undoStop(){if(undoId==-1)return;if(undoId>=0)db.delete(undoId);p.edit().putLong(KSTART,undoStart).apply();sendDeepWorkSignal(ACTION_DEEP_WORK_START);undo.setVisibility(View.GONE);undoId=-1;undoStart=-1;show(0);}

  private View sessionRow(Session s){return sessionRow(s,s.startMs,s.endMs);}

  private View sessionRow(Session s,long a,long b){
    long ss=Math.max(s.startMs,a),ee=Math.min(s.endMs,b);if(ee<=ss)return new Space(this);
    LinearLayout c=new LinearLayout(this);c.setGravity(Gravity.CENTER_VERTICAL);c.setPadding(Ui.dp(this,16),Ui.dp(this,14),Ui.dp(this,16),Ui.dp(this,14));c.setBackground(Ui.bg(this,Color.WHITE,17));c.setOnClickListener(v->editSession(s));
    TextView left=Ui.text(this,TimeUtils.segmentRange(s,a,b),16,s.valid()?Color.rgb(38,45,49):Color.GRAY,false);c.addView(left,new LinearLayout.LayoutParams(0,-2,1));
    TextView dur=Ui.text(this,TimeUtils.compact(ee-ss),16,s.valid()?Color.rgb(38,45,49):Color.GRAY,true);dur.setGravity(Gravity.END);dur.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);
    if(!s.valid()){dur.setPaintFlags(dur.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);c.setAlpha(.72f);}
    c.addView(dur,new LinearLayout.LayoutParams(Ui.dp(this,76),-2));
    LinearLayout wrap=new LinearLayout(this);wrap.setPadding(0,Ui.dp(this,4),0,Ui.dp(this,4));wrap.addView(c,new LinearLayout.LayoutParams(-1,-2));return wrap;
  }

  private TextView addSessionButton(LocalDate day){
    TextView v=Ui.text(this,"＋",22,Color.rgb(61,106,92),true);v.setGravity(Gravity.CENTER);
    v.setBackground(Ui.bg(this,Color.rgb(234,242,239),11));v.setOnClickListener(x->addSession(day));return v;
  }

  private void addSession(LocalDate day){
    if(p.getLong(KSTART,0)>0){
      new AlertDialog.Builder(this).setTitle("Timer is running").setMessage("Finish the current session before adding a manual session.").setPositiveButton("OK",null).show();return;
    }
    if(day.isAfter(LocalDate.now())){
      new AlertDialog.Builder(this).setTitle("Future sessions aren't allowed").setMessage("Choose today or an earlier date.").setPositiveButton("OK",null).show();return;
    }

    ZoneId z=ZoneId.systemDefault();int[] sh={9,0},eh={10,0};
    if(day.equals(LocalDate.now())){
      ZonedDateTime now=ZonedDateTime.now(z).withSecond(0).withNano(0),st=now.minusHours(1);
      if(!st.toLocalDate().equals(day))st=day.atStartOfDay(z);
      sh[0]=st.getHour();sh[1]=st.getMinute();eh[0]=now.getHour();eh[1]=now.getMinute();
    }

    LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(Ui.dp(this,20),0,Ui.dp(this,20),0);
    TextView date=Ui.text(this,TimeUtils.fullDay(day),15,Color.GRAY,false);box.addView(date);
    Ui.space(box,this,8);Button sb=new Button(this),eb=new Button(this);TextView duration=Ui.text(this,"",15,Color.rgb(61,106,92),true);
    Runnable labels=()->{
      int sm=sh[0]*60+sh[1],em=eh[0]*60+eh[1];boolean next=em<sm,equal=em==sm;long mins=equal?0:(next?em+1440-sm:em-sm);
      sb.setText(String.format(Locale.US,"Start   %02d:%02d",sh[0],sh[1]));
      eb.setText(String.format(Locale.US,"End     %02d:%02d%s",eh[0],eh[1],next?" (+1 day)":""));
      duration.setText(equal?"Duration   —":"Duration   "+TimeUtils.compact(mins*60000L));
    };labels.run();
    sb.setOnClickListener(v->new TimePickerDialog(this,(w,h,m)->{sh[0]=h;sh[1]=m;labels.run();},sh[0],sh[1],true).show());
    eb.setOnClickListener(v->new TimePickerDialog(this,(w,h,m)->{eh[0]=h;eh[1]=m;labels.run();},eh[0],eh[1],true).show());
    box.addView(sb);box.addView(eb);Ui.space(box,this,8);box.addView(duration);

    AlertDialog d=new AlertDialog.Builder(this).setTitle("Add session").setView(box).setPositiveButton("Add",null).setNegativeButton("Cancel",null).create();
    d.setOnShowListener(x->d.getButton(-1).setOnClickListener(v->{
      int sm=sh[0]*60+sh[1],em=eh[0]*60+eh[1];
      if(sm==em){new AlertDialog.Builder(this).setTitle("Invalid time").setMessage("Start and end can't be the same.").setPositiveButton("OK",null).show();return;}
      ZonedDateTime ns=day.atTime(sh[0],sh[1]).atZone(z),ne=day.atTime(eh[0],eh[1]).atZone(z);if(em<sm)ne=ne.plusDays(1);
      long nsm=ns.toInstant().toEpochMilli(),nem=ne.toInstant().toEpochMilli(),dur=nem-nsm;
      if(nem>System.currentTimeMillis()){new AlertDialog.Builder(this).setTitle("Future sessions aren't allowed").setMessage("The session must have already ended.").setPositiveButton("OK",null).show();return;}
      if(dur<MIN_RECORD_MS){new AlertDialog.Builder(this).setTitle("Too short").setMessage("Sessions under 10 minutes are not recorded.").setPositiveButton("OK",null).show();return;}
      if(db.overlaps(nsm,nem,-1)){new AlertDialog.Builder(this).setTitle("Overlapping session").setMessage("This time overlaps an existing session.").setPositiveButton("OK",null).show();return;}
      Runnable save=()->{db.add(nsm,nem);d.dismiss();show(tab);};
      if(dur<TimeUtils.THRESHOLD_MS)new AlertDialog.Builder(this).setTitle("Short session").setMessage("This session will be saved but won't count toward Deep Work totals.").setPositiveButton("Add",(a,b)->save.run()).setNegativeButton("Cancel",null).show();
      else save.run();
    }));d.show();
  }

  private void editSession(Session s){
    ZoneId z=ZoneId.systemDefault();ZonedDateTime a=Instant.ofEpochMilli(s.startMs).atZone(z),b=Instant.ofEpochMilli(s.endMs).atZone(z);int[] sh={a.getHour(),a.getMinute()},eh={b.getHour(),b.getMinute()};
    LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(Ui.dp(this,20),0,Ui.dp(this,20),0);Button sb=new Button(this),eb=new Button(this);
    Runnable labels=()->{int sm=sh[0]*60+sh[1],em=eh[0]*60+eh[1];sb.setText(String.format(Locale.US,"Start   %02d:%02d",sh[0],sh[1]));eb.setText(String.format(Locale.US,"End     %02d:%02d%s",eh[0],eh[1],em<sm?" (+1 day)":""));};labels.run();
    sb.setOnClickListener(v->new TimePickerDialog(this,(w,h,m)->{sh[0]=h;sh[1]=m;labels.run();},sh[0],sh[1],true).show());
    eb.setOnClickListener(v->new TimePickerDialog(this,(w,h,m)->{eh[0]=h;eh[1]=m;labels.run();},eh[0],eh[1],true).show());box.addView(sb);box.addView(eb);
    AlertDialog d=new AlertDialog.Builder(this).setTitle("Edit session").setView(box).setPositiveButton("Save",null).setNeutralButton("Delete",null).setNegativeButton("Cancel",null).create();
    d.setOnShowListener(x->{
      d.getButton(-1).setOnClickListener(v->{
        int sm=sh[0]*60+sh[1],em=eh[0]*60+eh[1];
        if(sm==em){new AlertDialog.Builder(this).setTitle("Invalid time").setMessage("Start and end can't be the same.").setPositiveButton("OK",null).show();return;}
        LocalDate day=TimeUtils.date(s.startMs);ZonedDateTime ns=day.atTime(sh[0],sh[1]).atZone(z),ne=day.atTime(eh[0],eh[1]).atZone(z);if(em<sm)ne=ne.plusDays(1);
        long nsm=ns.toInstant().toEpochMilli(),nem=ne.toInstant().toEpochMilli();
        if(nem>System.currentTimeMillis()){new AlertDialog.Builder(this).setTitle("Future sessions aren't allowed").setMessage("The session must have already ended.").setPositiveButton("OK",null).show();return;}
        if(nem-nsm<MIN_RECORD_MS){new AlertDialog.Builder(this).setTitle("Too short").setMessage("Sessions under 10 minutes are not recorded.").setPositiveButton("OK",null).show();return;}
        if(db.overlaps(nsm,nem,s.id)){new AlertDialog.Builder(this).setTitle("Overlapping session").setMessage("This time overlaps another session.").setPositiveButton("OK",null).show();return;}
        db.update(s.id,nsm,nem);d.dismiss();show(tab);
      });
      d.getButton(-3).setTextColor(Color.rgb(180,50,50));d.getButton(-3).setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Delete session?").setPositiveButton("Delete",(y,w)->{db.delete(s.id);d.dismiss();show(tab);}).setNegativeButton("Cancel",null).show());
    });d.show();
  }

  private void week(){
    LinearLayout[] o=new LinearLayout[1];PeriodScrollView s=periodPage(o);LinearLayout q=o[0];
    LocalDate today=LocalDate.now(),currentWeek=TimeUtils.monday(today);
    if(shownWeek.isAfter(currentWeek))shownWeek=currentWeek;
    final LocalDate m=shownWeek;boolean isCurrent=m.equals(currentWeek);

    TextView title=Ui.text(this,isCurrent?"This week  ▾":"Week "+TimeUtils.isoWeek(m)+"  ▾",26,Color.rgb(25,31,34),true);
    title.setPadding(0,0,Ui.dp(this,8),0);title.setOnClickListener(v->showWeekPicker());q.addView(title);
    q.addView(Ui.text(this,TimeUtils.weekWithYear(m),15,Color.GRAY,false));

    long[] totals=new long[7];@SuppressWarnings("unchecked") List<Session>[] days=new List[7];long sum=0;
    for(int i=0;i<7;i++){LocalDate d=m.plusDays(i);long a=TimeUtils.start(d),b=TimeUtils.start(d.plusDays(1));days[i]=db.overlapping(a,b);totals[i]=TimeUtils.totalWithin(days[i],a,b);sum+=totals[i];}

    Ui.space(q,this,12);
    long goal=p.getInt(KGOAL,360)*60000L;
    LinearLayout totalRow=new LinearLayout(this);totalRow.setGravity(Gravity.CENTER_VERTICAL);
    totalRow.addView(Ui.text(this,TimeUtils.natural(sum),32,Color.rgb(25,31,34),true),new LinearLayout.LayoutParams(0,-2,1));
    TextView star=Ui.text(this,sum>=goal&&goal>0?"★":"☆",30,sum>=goal&&goal>0?Color.rgb(61,106,92):Color.rgb(125,132,136),false);star.setGravity(Gravity.END|Gravity.CENTER_VERTICAL);
    totalRow.addView(star,new LinearLayout.LayoutParams(Ui.dp(this,52),-2));q.addView(totalRow);
    q.addView(Ui.text(this,"Goal · "+TimeUtils.compact(goal),14,Color.GRAY,false));Ui.space(q,this,12);

    if(weekSelectedDay<0||weekSelectedDay>6){
      if(isCurrent)weekSelectedDay=Math.max(0,Math.min(6,(int)ChronoUnit.DAYS.between(m,today)));
      else{weekSelectedDay=0;for(int i=0;i<7;i++){if(!days[i].isEmpty()){weekSelectedDay=i;break;}}}
    }
    int todayIndex=isCurrent?Math.max(0,Math.min(6,(int)ChronoUnit.DAYS.between(m,today))):-1;
    int[] sel={weekSelectedDay};
    WeekChartView chart=new WeekChartView(this);chart.set(totals,m,sel[0],todayIndex);q.addView(chart,new LinearLayout.LayoutParams(-1,Ui.dp(this,344)));

    LinearLayout det=new LinearLayout(this);det.setOrientation(LinearLayout.VERTICAL);q.addView(det);
    Runnable draw=()->{
      det.removeAllViews();LocalDate d=m.plusDays(sel[0]);long a=TimeUtils.start(d),b=TimeUtils.start(d.plusDays(1));det.addView(daySummaryLine(d,totals[sel[0]]));Ui.space(det,this,8);
      if(days[sel[0]].isEmpty())det.addView(Ui.text(this,"No sessions.",15,Color.GRAY,false));else for(Session x:days[sel[0]])det.addView(sessionRow(x,a,b));
    };
    draw.run();
    chart.listener(i->{sel[0]=i;weekSelectedDay=i;draw.run();});

    s.setPeriodGestures(
      ()->{shownWeek=shownWeek.minusWeeks(1);weekSelectedDay=-1;show(1);},
      ()->{LocalDate n=shownWeek.plusWeeks(1);if(!n.isAfter(currentWeek)){shownWeek=n;weekSelectedDay=-1;show(1);}},
      ()->{shownWeek=currentWeek;weekSelectedDay=-1;show(1);}
    );
    content.addView(s);
  }

  private void history(){
    LinearLayout[] o=new LinearLayout[1];ScrollView s=page(o);LinearLayout q=o[0];
    q.setPadding(Ui.dp(this,14),Ui.dp(this,24),Ui.dp(this,4),Ui.dp(this,34));
    int currentYear=LocalDate.now().getYear();
    TextView title=Ui.text(this,historyYear==currentYear?"This year · "+currentYear+"  ▾":historyYear+"  ▾",26,Color.rgb(25,31,34),true);
    title.setPadding(0,0,Ui.dp(this,8),0);title.setOnClickListener(v->showHistoryYearPicker());q.addView(title);
    q.addView(Ui.text(this,"Weekly totals · newest first",14,Color.GRAY,false));Ui.space(q,this,14);

    LocalDate currentWeek=TimeUtils.monday(LocalDate.now());
    long yearA=TimeUtils.start(LocalDate.of(historyYear,1,1)),yearB=TimeUtils.start(LocalDate.of(historyYear+1,1,1));
    List<Session> yearSessions=db.overlapping(yearA,yearB);LocalDate firstRecordedWeek=null;
    for(Session x:yearSessions){
      if(!x.valid())continue;
      long clipped=Math.max(x.startMs,yearA);LocalDate w=TimeUtils.monday(TimeUtils.date(clipped));
      if(firstRecordedWeek==null||w.isBefore(firstRecordedWeek))firstRecordedWeek=w;
    }
    if(firstRecordedWeek==null){
      q.addView(Ui.text(this,"No deep-work sessions in "+historyYear+".",16,Color.GRAY,false));content.addView(s);return;
    }

    YearMonth firstMonth=YearMonth.from(firstRecordedWeek);
    LocalDate first=firstMonth.atDay(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
    LocalDate last;
    if(historyYear==currentYear)last=currentWeek;
    else last=LocalDate.of(historyYear,12,31).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

    int count=(int)ChronoUnit.WEEKS.between(first,last)+1;
    LocalDate[] weeks=new LocalDate[count];long[] totals=new long[count];
    for(int i=0;i<count;i++){LocalDate w=last.minusWeeks(i);weeks[i]=w;long a=TimeUtils.start(w),b=TimeUtils.start(w.plusWeeks(1));totals[i]=TimeUtils.totalWithin(db.overlapping(a,b),a,b);}

    long[] monthTotals=new long[12];
    for(int mo=1;mo<=12;mo++){
      YearMonth ym=YearMonth.of(historyYear,mo);long a=TimeUtils.start(ym.atDay(1)),b=TimeUtils.start(ym.plusMonths(1).atDay(1));
      monthTotals[mo-1]=TimeUtils.totalWithin(db.overlapping(a,b),a,b);
    }
    long goal=p.getInt(KGOAL,360)*60000L;
    HistoryTimelineView chart=new HistoryTimelineView(this);chart.set(weeks,totals,goal,monthTotals);
    chart.listener(w->{shownWeek=w;weekSelectedDay=-1;show(1);});
    q.addView(chart,new LinearLayout.LayoutParams(-1,-2));
    content.addView(s);
  }

  private View summaryLine(String label,long ms,boolean bold){
    LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(0,Ui.dp(this,4),0,Ui.dp(this,4));
    TextView left=Ui.text(this,label,19,Color.rgb(34,41,45),bold);row.addView(left,new LinearLayout.LayoutParams(0,-2,1));
    TextView right=Ui.text(this,TimeUtils.compact(ms),16,Color.rgb(61,106,92),true);right.setGravity(Gravity.END);right.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);
    row.addView(right,new LinearLayout.LayoutParams(Ui.dp(this,76),-2));return row;
  }

  private View daySummaryLine(LocalDate day,long ms){
    LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(0,Ui.dp(this,4),0,Ui.dp(this,4));
    TextView left=Ui.text(this,TimeUtils.day(day),19,Color.rgb(34,41,45),true);row.addView(left,new LinearLayout.LayoutParams(0,-2,1));
    LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(Ui.dp(this,36),Ui.dp(this,36));bp.setMargins(Ui.dp(this,8),0,Ui.dp(this,10),0);row.addView(addSessionButton(day),bp);
    TextView right=Ui.text(this,TimeUtils.compact(ms),16,Color.rgb(61,106,92),true);right.setGravity(Gravity.END);right.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);row.addView(right,new LinearLayout.LayoutParams(Ui.dp(this,76),-2));return row;
  }

  private void showWeekPicker(){showWeekPickerYear(TimeUtils.isoWeekYear(shownWeek));}

  private void showWeekPickerYear(int year){
    LinearLayout outer=new LinearLayout(this);outer.setOrientation(LinearLayout.VERTICAL);outer.setPadding(Ui.dp(this,12),0,Ui.dp(this,12),Ui.dp(this,8));
    LinearLayout yr=new LinearLayout(this);yr.setGravity(Gravity.CENTER_VERTICAL);
    Button prev=new Button(this);prev.setText("‹");TextView label=Ui.text(this,String.valueOf(year),19,Color.DKGRAY,true);label.setGravity(Gravity.CENTER);Button next=new Button(this);next.setText("›");
    yr.addView(prev,new LinearLayout.LayoutParams(Ui.dp(this,64),-2));yr.addView(label,new LinearLayout.LayoutParams(0,-2,1));yr.addView(next,new LinearLayout.LayoutParams(Ui.dp(this,64),-2));outer.addView(yr);
    ScrollView sc=new ScrollView(this);LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);sc.addView(list);outer.addView(sc,new LinearLayout.LayoutParams(-1,Ui.dp(this,430)));
    AlertDialog[] ref={null};LocalDate current=TimeUtils.monday(LocalDate.now());int currentYear=TimeUtils.isoWeekYear(current);
    ArrayList<LocalDate> weeks=new ArrayList<>();LocalDate w=TimeUtils.monday(LocalDate.of(year,1,4));
    while(TimeUtils.isoWeekYear(w)==year){if(!w.isAfter(current))weeks.add(w);w=w.plusWeeks(1);}
    Collections.reverse(weeks);
    for(LocalDate x:weeks){
      String txt="Week "+TimeUtils.isoWeek(x)+"  ·  "+TimeUtils.weekWithYear(x);
      TextView r=Ui.text(this,txt,15,Color.rgb(45,52,56),x.equals(shownWeek));r.setPadding(Ui.dp(this,14),Ui.dp(this,12),Ui.dp(this,14),Ui.dp(this,12));
      if(x.equals(shownWeek))r.setBackground(Ui.bg(this,Color.rgb(231,241,237),12));
      r.setOnClickListener(v->{shownWeek=x;weekSelectedDay=-1;if(ref[0]!=null)ref[0].dismiss();show(1);});list.addView(r,new LinearLayout.LayoutParams(-1,-2));
    }
    prev.setOnClickListener(v->{if(ref[0]!=null)ref[0].dismiss();showWeekPickerYear(year-1);});
    next.setEnabled(year<currentYear);next.setOnClickListener(v->{if(year<currentYear){if(ref[0]!=null)ref[0].dismiss();showWeekPickerYear(year+1);}});
    ref[0]=new AlertDialog.Builder(this).setTitle("Choose week").setView(outer).setNegativeButton("Cancel",null).create();ref[0].show();
  }

  private void showHistoryYearPicker(){
    TreeSet<Integer> years=new TreeSet<>(Collections.reverseOrder());years.add(LocalDate.now().getYear());
    for(Session x:db.allStored()){years.add(TimeUtils.date(x.startMs).getYear());years.add(TimeUtils.date(Math.max(x.startMs,x.endMs-1)).getYear());}
    Integer[] ys=years.toArray(new Integer[0]);String[] labels=new String[ys.length];
    for(int i=0;i<ys.length;i++)labels[i]=ys[i]==LocalDate.now().getYear()?"This year · "+ys[i]:String.valueOf(ys[i]);
    new AlertDialog.Builder(this).setTitle("Choose year").setItems(labels,(d,which)->{historyYear=ys[which];show(2);}).setNegativeButton("Cancel",null).show();
  }

  private void bio(){
    LinearLayout[] o=new LinearLayout[1];ScrollView s=page(o);LinearLayout q=o[0];LinearLayout hd=new LinearLayout(this);TextView t=Ui.text(this,"我的自傳",26,Color.rgb(25,31,34),true);hd.addView(t,new LinearLayout.LayoutParams(0,-2,1));TextView ed=Ui.text(this,"Edit",16,Color.rgb(27,105,84),true);ed.setOnClickListener(v->bioEdit());hd.addView(ed);q.addView(hd);Ui.space(q,this,18);q.addView(bioCard("我想要的人生",p.getString(KBIO1,""),"用過去式寫下你想完成的成就、克服的阻礙，以及最後成為怎樣的人。"));Ui.space(q,this,16);q.addView(bioCard("我不想走向的人生",p.getString(KBIO2,""),"寫下如果長期放棄專注、重要事情持續被擱置，你不希望生活逐漸變成什麼樣子。"));Ui.space(q,this,20);long up=p.getLong(KUP,0);q.addView(Ui.text(this,up==0?"Not saved yet":"Last updated · "+TimeUtils.hist(TimeUtils.date(up)),13,Color.GRAY,false));content.addView(s);
  }

  private View bioCard(String title,String body,String hint){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18));c.setBackground(Ui.bg(this,Color.WHITE,20));c.addView(Ui.text(this,title,19,Color.DKGRAY,true));Ui.space(c,this,8);boolean empty=body.trim().isEmpty();TextView b=Ui.text(this,empty?hint:body,16,empty?Color.GRAY:Color.rgb(56,63,67),false);b.setLineSpacing(0,1.35f);c.addView(b);return c;}

  private void bioEdit(){
    editingBio=true;content.removeAllViews();LinearLayout[] o=new LinearLayout[1];ScrollView s=page(o);LinearLayout q=o[0];LinearLayout hd=new LinearLayout(this);TextView ca=Ui.text(this,"Cancel",16,Color.GRAY,true);ca.setOnClickListener(v->cancelBio());hd.addView(ca);TextView tt=Ui.text(this,"編輯我的自傳",22,Color.DKGRAY,true);tt.setGravity(Gravity.CENTER);hd.addView(tt,new LinearLayout.LayoutParams(0,-2,1));TextView sv=Ui.text(this,"Save",16,Color.rgb(27,105,84),true);sv.setOnClickListener(v->saveBio());hd.addView(sv);q.addView(hd);
    String f1=p.getString(KBIO1,""),f2=p.getString(KBIO2,"");b1=edit(p.getString(KD1,f1),"例如：我完成了……我克服了……我成為了一個……");b2=edit(p.getString(KD2,f2),"如果我持續讓注意力被日常瑣事切碎……幾年後我的生活可能會……");Ui.space(q,this,18);q.addView(Ui.text(this,"我想要的人生",18,Color.DKGRAY,true));q.addView(b1,new LinearLayout.LayoutParams(-1,Ui.dp(this,230)));Ui.space(q,this,18);q.addView(Ui.text(this,"我不想走向的人生",18,Color.DKGRAY,true));q.addView(b2,new LinearLayout.LayoutParams(-1,Ui.dp(this,230)));Ui.space(q,this,10);q.addView(Ui.text(this,"草稿會自動暫存；只有按 Save 才會更新正式版本。",13,Color.GRAY,false));
    b1.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int c){}public void onTextChanged(CharSequence s,int a,int b,int c){p.edit().putString(KD1,s.toString()).apply();}public void afterTextChanged(android.text.Editable e){}});b2.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int c){}public void onTextChanged(CharSequence s,int a,int b,int c){p.edit().putString(KD2,s.toString()).apply();}public void afterTextChanged(android.text.Editable e){}});
    content.addView(s);
  }
  private EditText edit(String v,String hint){EditText e=new EditText(this);e.setText(v);e.setHint(hint);e.setTextSize(16);e.setGravity(Gravity.TOP);e.setPadding(Ui.dp(this,15),Ui.dp(this,15),Ui.dp(this,15),Ui.dp(this,15));e.setBackground(Ui.bg(this,Color.WHITE,18));return e;}
  private void saveBio(){String x=b1.getText().toString(),y=b2.getText().toString();p.edit().putString(KBIO1,x).putString(KBIO2,y).putString(KD1,x).putString(KD2,y).putLong(KUP,System.currentTimeMillis()).apply();editingBio=false;Ui.hideKeyboard(this);show(3);}
  private void cancelBio(){p.edit().putString(KD1,p.getString(KBIO1,"")).putString(KD2,p.getString(KBIO2,"")).apply();editingBio=false;Ui.hideKeyboard(this);show(3);}
  private void unsaved(Runnable after){new AlertDialog.Builder(this).setTitle("尚未儲存修改").setMessage("要先儲存這次修改嗎？").setPositiveButton("儲存",(d,w)->{saveBio();after.run();}).setNegativeButton("放棄",(d,w)->{cancelBio();after.run();}).setNeutralButton("取消",null).show();}

  private String appVersion(){
    try{
      String v=getPackageManager().getPackageInfo(getPackageName(),0).versionName;
      return v==null?"":v;
    }catch(Exception e){return "";}
  }

  private void settings(){
    String[] items={"Weekly deep-work goal","匯出備份","還原備份"};
    String v=appVersion();
    String title=v.isEmpty()?"Settings":"Settings · v"+v;
    new AlertDialog.Builder(this).setTitle(title).setItems(items,(d,which)->{
      if(which==0)weeklyGoalDialog();
      else if(which==1)startBackupExport();
      else if(which==2)startBackupImport();
    }).show();
  }

  private void weeklyGoalDialog(){
    int g=p.getInt(KGOAL,360);LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER);NumberPicker h=new NumberPicker(this),m=new NumberPicker(this);h.setMinValue(0);h.setMaxValue(40);h.setValue(g/60);m.setMinValue(0);m.setMaxValue(11);String[] lab=new String[12];for(int i=0;i<12;i++)lab[i]=""+(i*5);m.setDisplayedValues(lab);m.setValue(Math.min(11,Math.round((g%60)/5f)));row.addView(h);row.addView(m);new AlertDialog.Builder(this).setTitle("Weekly deep-work goal").setMessage("Hours + minutes").setView(row).setPositiveButton("Save",(d,w)->{p.edit().putInt(KGOAL,h.getValue()*60+m.getValue()*5).apply();if(tab==1)show(1);}).setNegativeButton("Cancel",null).show();
  }

  private boolean ensureIdleForDataAction(){
    if(p.getLong(KSTART,0)>0){
      new AlertDialog.Builder(this).setTitle("目前正在計時").setMessage("請先結束這次深度工作，再進行備份或還原。").setPositiveButton("OK",null).show();
      return false;
    }
    return true;
  }

  private void startBackupExport(){
    if(!ensureIdleForDataAction())return;
    Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);
    i.addCategory(Intent.CATEGORY_OPENABLE);
    i.setType("application/json");
    i.putExtra(Intent.EXTRA_TITLE,"DeepTally-backup-"+LocalDate.now()+".json");
    startActivityForResult(i,REQ_EXPORT_BACKUP);
  }

  private void startBackupImport(){
    if(!ensureIdleForDataAction())return;
    Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);
    i.addCategory(Intent.CATEGORY_OPENABLE);
    i.setType("application/json");
    startActivityForResult(i,REQ_IMPORT_BACKUP);
  }

  protected void onActivityResult(int requestCode,int resultCode,Intent data){
    super.onActivityResult(requestCode,resultCode,data);
    if(resultCode!=RESULT_OK||data==null||data.getData()==null)return;
    Uri uri=data.getData();
    if(requestCode==REQ_EXPORT_BACKUP){
      try(OutputStream out=getContentResolver().openOutputStream(uri,"wt");Writer w=new OutputStreamWriter(out,StandardCharsets.UTF_8)){
        w.write(buildBackupJson().toString(2));
        Toast.makeText(this,"備份完成",Toast.LENGTH_SHORT).show();
      }catch(Exception e){
        new AlertDialog.Builder(this).setTitle("備份失敗").setMessage(e.getMessage()==null?"無法寫入備份檔。":e.getMessage()).setPositiveButton("OK",null).show();
      }
    }else if(requestCode==REQ_IMPORT_BACKUP){
      try(InputStream in=getContentResolver().openInputStream(uri);BufferedReader r=new BufferedReader(new InputStreamReader(in,StandardCharsets.UTF_8))){
        StringBuilder b=new StringBuilder();String line;while((line=r.readLine())!=null)b.append(line).append('\n');
        JSONObject root=new JSONObject(b.toString());
        validateBackup(root);
        JSONArray sessions=root.getJSONArray("sessions");
        new AlertDialog.Builder(this).setTitle("還原備份？")
          .setMessage("這會以備份內容取代目前的深度工作紀錄與設定。\n\n備份內共有 "+sessions.length()+" 筆 session。")
          .setPositiveButton("還原",(d,w)->applyBackup(root))
          .setNegativeButton("取消",null).show();
      }catch(Exception e){
        new AlertDialog.Builder(this).setTitle("無法讀取備份").setMessage(e.getMessage()==null?"檔案格式不正確或已損壞。":e.getMessage()).setPositiveButton("OK",null).show();
      }
    }
  }

  private JSONObject buildBackupJson() throws JSONException{
    JSONObject root=new JSONObject();
    root.put("format",BACKUP_FORMAT);
    root.put("backup_version",BACKUP_VERSION);
    root.put("app_version",appVersion());
    root.put("created_at",System.currentTimeMillis());

    JSONObject prefs=new JSONObject();
    prefs.put(KGOAL,p.getInt(KGOAL,360));
    prefs.put(KBIO1,p.getString(KBIO1,""));
    prefs.put(KBIO2,p.getString(KBIO2,""));
    prefs.put(KD1,p.getString(KD1,p.getString(KBIO1,"")));
    prefs.put(KD2,p.getString(KD2,p.getString(KBIO2,"")));
    prefs.put(KUP,p.getLong(KUP,0));
    root.put("preferences",prefs);

    JSONArray sessions=new JSONArray();
    for(Session s:db.allStored()){
      JSONObject x=new JSONObject();
      x.put("start_ms",s.startMs);
      x.put("end_ms",s.endMs);
      sessions.put(x);
    }
    root.put("sessions",sessions);
    return root;
  }

  private void validateBackup(JSONObject root) throws JSONException{
    if(!BACKUP_FORMAT.equals(root.optString("format")))throw new JSONException("這不是 Deep Tally 備份檔。");
    if(root.optInt("backup_version",-1)!=BACKUP_VERSION)throw new JSONException("不支援的備份版本。");
    if(!root.has("preferences")||!root.has("sessions"))throw new JSONException("備份內容不完整。");
  }

  private void applyBackup(JSONObject root){
    try{
      validateBackup(root);
      JSONObject prefs=root.getJSONObject("preferences");
      JSONArray arr=root.getJSONArray("sessions");
      ArrayList<Session> restored=new ArrayList<>();
      for(int i=0;i<arr.length();i++){
        JSONObject x=arr.getJSONObject(i);
        long s=x.getLong("start_ms"),e=x.getLong("end_ms");
        if(s<=0||e<=s||e-s<MIN_RECORD_MS)throw new JSONException("第 "+(i+1)+" 筆 session 資料無效。");
        restored.add(new Session(0,s,e));
      }
      db.replaceAll(restored);
      p.edit().clear()
        .putInt(KGOAL,prefs.optInt(KGOAL,360))
        .putString(KBIO1,prefs.optString(KBIO1,""))
        .putString(KBIO2,prefs.optString(KBIO2,""))
        .putString(KD1,prefs.optString(KD1,prefs.optString(KBIO1,"")))
        .putString(KD2,prefs.optString(KD2,prefs.optString(KBIO2,"")))
        .putLong(KUP,prefs.optLong(KUP,0))
        .apply();
      Toast.makeText(this,"還原完成 · "+restored.size()+" 筆 session",Toast.LENGTH_LONG).show();
      show(tab);
    }catch(Exception e){
      new AlertDialog.Builder(this).setTitle("還原失敗").setMessage(e.getMessage()==null?"無法套用備份。":e.getMessage()).setPositiveButton("OK",null).show();
    }
  }
}
