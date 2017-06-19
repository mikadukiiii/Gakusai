package it.poliba.sisinflab.physicalweb;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by giorgio on 08/04/15.
 */
public class Performances {
    //TODO: definire classe Timer, che abbia nome, valore temporale e stato, e utilizzare Performances come una lista di Timer

    public static final int EXPOSED_TIME_INDEX = 0;
    public static final int SEMANTIC_TIME_INDEX = 1;
    public static final int SITE_TIME_INDEX = 2;
    public static final int ICON_TIME_INDEX = 3;
    public static final int FULL_TIME_INDEX = 4;
    public static final int SCORE_TIME_INDEX = 5;

    private static final int STATE_READY = 0;
    private static final int STATE_START = 1;
    private static final int STATE_STOP = 2;

    private SemanticData sd;
    private long exposedTime;
    private long semanticTime;
    private long siteTime;
    private long iconTime;
    private long fullTime;
    private long scoreTime;
    private int upData;
    private int downData;

    private int exposedState;
    private int semanticState;
    private int siteState;
    private int fullState;
    private int iconState;
    private int scoreState;




    public Performances(){
        semanticTime = 0;
        siteTime = 0;
        fullTime = 0;
        iconTime = 0;
        siteTime = 0;
        scoreTime = 0;
        upData = 0;
        downData = 0;

        exposedState = STATE_READY;
        semanticState = STATE_READY;
        siteState = STATE_READY;
        iconState = STATE_READY;
        fullState = STATE_READY;
        scoreState = STATE_READY;
    }


    public void startTimer(int index){
        long curTime = System.nanoTime();
        switch(index){
            case EXPOSED_TIME_INDEX:
                exposedTime = curTime;
                exposedState = STATE_START;
                break;
            case SEMANTIC_TIME_INDEX:
                semanticTime = curTime;
                semanticState = STATE_START;
                break;
            case SITE_TIME_INDEX:
                siteTime = curTime;
                siteState = STATE_START;
                break;
            case ICON_TIME_INDEX:
                iconTime = curTime;
                iconState = STATE_START;
                break;
            case FULL_TIME_INDEX:
                fullTime = curTime;
                fullState = STATE_START;
                break;
            case SCORE_TIME_INDEX:
                scoreTime = curTime;
                scoreState = STATE_START;
                break;
        }
    }

    public void stopTimer(int index){
        long curTime = System.nanoTime();
        switch(index){
            case EXPOSED_TIME_INDEX:
                exposedTime = curTime - exposedTime;
                exposedState = STATE_STOP;
                break;
            case SEMANTIC_TIME_INDEX:
                semanticTime = curTime - semanticTime;
                semanticState = STATE_STOP;
                break;
            case SITE_TIME_INDEX:
                siteTime = curTime - siteTime;
                siteState = STATE_STOP;
                break;
            case ICON_TIME_INDEX:
                iconTime = curTime - iconTime;
                iconState = STATE_STOP;
                break;
            case FULL_TIME_INDEX:
                fullTime = curTime - fullTime;
                fullState = STATE_STOP;
                break;
            case SCORE_TIME_INDEX:
               scoreTime = curTime - scoreTime;
                scoreState = STATE_STOP;
                break;
        }
    }

    public ArrayList<String> getResults(){
        ArrayList<String> results = new ArrayList<String>();
        DecimalFormat df = new DecimalFormat("#.##");
        long sum = 0;
        if(exposedState == STATE_STOP)
            results.add("UriBeacon metadata: " + df.format((double) exposedTime / 1000000000.0) + " s");
            sum += exposedTime;
        if(semanticState == STATE_STOP)
            results.add("Semantic annotation: " + df.format((double) semanticTime / 1000000000.0) + " s");
            sum += semanticTime;
        if(siteState == STATE_STOP)
            results.add("Embedded URL metadata: " + df.format((double) siteTime / 1000000000.0) + " s");
            sum += siteTime;
        if(iconState == STATE_STOP)
            results.add("Icon download: " + df.format((double) iconTime / 1000000000.0) + " s");
        if(scoreState == STATE_STOP)
            results.add("Score computation: " + df.format((double) scoreTime / 1000000000.0) + " s");
            sum += scoreTime;
        if(fullState == STATE_STOP) {
            results.add("Full turnaround: " + df.format((double) fullTime / 1000000000.0) + " s");
            //results.add("Sum: " + df.format((double) sum / 1000000000.0) + " s");
        }
        return results;
    }

    public long getExposedTime() {
        return exposedTime;
    }

    public void setExposedTime(long exposedTime) {
        this.exposedTime = exposedTime;
    }

    public SemanticData getSd() {
        return sd;
    }

    public void setSd(SemanticData sd) {
        this.sd = sd;
    }

    public long getSemanticTime() {
        return semanticTime;
    }

    public void setSemanticTime(long semanticTime) {
        this.semanticTime = semanticTime;
    }

    public long getSiteTime() {
        return siteTime;
    }

    public void setSiteTime(long siteTime) {
        this.siteTime = siteTime;
    }

    public int getUpData() {
        return upData;
    }

    public void addUpData(int upData) {
        this.upData = upData;
    }

    public long getFullTime() {
        return fullTime;
    }

    public void setFullTime(long fullTime) {
        this.fullTime = fullTime;
    }

    public int getDownData() {
        return downData;
    }

    public void addDownData(int downData) {
        this.downData += downData;
    }

    public long getIconTime() {
        return iconTime;
    }

    public void setIconTime(long iconTime) {
        this.iconTime = iconTime;
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

 }
