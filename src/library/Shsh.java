/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library;

import com.sun.javafx.PlatformUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author sho
 */
public class Shsh { 
    private File file;
    private String filename;
    private String ECID;
    private String iOS;
    private String build;
    private String apnonce;
    private String type;
    private Long date;
    
    private static String dirPath;
    private static List<File> dirFiles;
    
    public Shsh(File file, String ECID, String iOS, String build, String apnonce, String type){
        this.file = file;
        this.filename = file.getName();
        this.ECID = ECID;
        this.iOS = iOS;
        this.build = build;
        this.apnonce = apnonce;
        this.type = type;
    }
    
    public Shsh(String[] param) {
        if (param.length != 7) return;
        this.filename = param[0];
        this.ECID = param[1];
        this.iOS = param[2];
        this.build = param[3];
        this.apnonce = param[4];
        this.type = param[5];
        this.date = Long.parseLong(param[6]);
    }
    
    public Shsh(String filename, String ECID, String OS, String apnonce, String date, String type){
        
    }
    
    public String getFileName(){
        return this.filename;
    }
    
    public String getECID() {
        return this.ECID;
    }
    
    public String getIOS() {
        return this.iOS;
    }
    
    public String getBuild(){
        return this.build;
    }
    
    public String getApnonce(){
        return this.apnonce;
    }
    
    public String getType(){
        return this.type;
    }
    
    public String getTime(){
        return Database.msToString(this.date);
    }
    
    
    public static void getBeforeFiles(String path){
        dirPath = path;
        File dir = new File(dirPath);
        
        //listFilesメソッドを使用して一覧を取得する
        File[] list = dir.listFiles();
        if (list != null){
            dirFiles = new ArrayList<>();
            for (File file : list) {
                dirFiles.add(file);
            }
        }
    }
    
    public static File getFilesDiff(){
        if (dirFiles == null) return null;
        
        File dir = new File(dirPath);
        
        //listFilesメソッドを使用して一覧を取得する
        File[] list = dir.listFiles();
        if (list != null){
            List<File> after = new ArrayList<>();
            for (File file : list) {
                after.add(file);
            }
            
            List<File> sub = subtract(after, dirFiles);
            for (File file : sub) {
                if (file.getName().contains(".shsh2")){
                    return file;
                }
            }
            
        }
        return null;
    }
    
    private static List<File> subtract(List<File> list1, List<File> list2) {
        final HashSet<File> list2Set = new HashSet<>(list2);
        final List<File> resultList = list1.stream()
                .filter(p -> {
                    return (! list2Set.contains(p));
                })
                .collect(Collectors.toList());
        return resultList;
    }
    
    
    public void saveDB(){
        Database.putSQLite(this);
    }
    
    
    public static List<Shsh> getDB(Idevice idevice){
        return Database.loadShshfromSQLite(idevice);
    }
    
}
