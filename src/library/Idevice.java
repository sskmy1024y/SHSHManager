package library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author sho
 */
public class Idevice {

    private static List<Idevice> deviceList = new ArrayList<>();
    private static Map<String, String> iPhones = new LinkedHashMap<String, String>() {
        {
            put("iPhone 3G[S]", "iPhone2,1");
            put("iPhone 4 (GSM)", "iPhone3,1");
            put("iPhone 4 (GSM 2012)", "iPhone3,2");
            put("iPhone 4 (CDMA)", "iPhone3,3");
            put("iPhone 4[S]", "iPhone4,1");
            put("iPhone 5 (GSM)", "iPhone5,1");
            put("iPhone 5 (Global)", "iPhone5,2");
            put("iPhone 5c (GSM)", "iPhone5,3");
            put("iPhone 5c (Global)", "iPhone5,4");
            put("iPhone 5s (GSM)", "iPhone6,1");
            put("iPhone 5s (Global)", "iPhone6,2");
            put("iPhone 6+", "iPhone7,1");
            put("iPhone 6", "iPhone7,2");
            put("iPhone 6s", "iPhone8,1");
            put("iPhone 6s+", "iPhone8,2");
            put("iPhone SE", "iPhone8,4");
            put("iPhone 7 (Global)(iPhone9,1)", "iPhone9,1");
            put("iPhone 7+ (Global)(iPhone9,2)", "iPhone9,2");
            put("iPhone 7 (GSM)(iPhone9,3)", "iPhone9,3");
            put("iPhone 7+ (GSM)(iPhone9,4)", "iPhone9,4");
            put("iPhone 8 (iPhone10,1)", "iPhone10,1");
            put("iPhone 8+ (iPhone10,2)", "iPhone10,2");
            put("iPhone X (iPhone10,3)", "iPhone10,3");
            put("iPhone 8 (iPhone10,4)", "iPhone10,4");
            put("iPhone 8+ (iPhone10,5)", "iPhone10,5");
            put("iPhone X (iPhone10,6)", "iPhone10,6");
        }
    };

    private static Map<String, String> iPads = new LinkedHashMap<String, String>() {
        {
            put("iPad 1", "iPad1,1");
            put("iPad 2 (WiFi)", "iPad2,1");
            put("iPad 2 (GSM)", "iPad2,2");
            put("iPad 2 (CDMA)", "iPad2,3");
            put("iPad 2 (Mid 2012)", "iPad2,4");
            put("iPad Mini (Wifi)", "iPad2,5");
            put("iPad Mini (GSM)", "iPad2,6");
            put("iPad Mini (Global)", "iPad2,7");
            put("iPad 3 (WiFi)", "iPad3,1");
            put("iPad 3 (CDMA)", "iPad3,2");
            put("iPad 3 (GSM)", "iPad3,3");
            put("iPad 4 (WiFi)", "iPad3,4");
            put("iPad 4 (GSM)", "iPad3,5");
            put("iPad 4 (Global)", "iPad3,6");
            put("iPad Air (Wifi)", "iPad4,1");
            put("iPad Air (Cellular)", "iPad4,2");
            put("iPad Air (China)", "iPad4,3");
            put("iPad Mini 2 (WiFi)", "iPad4,4");
            put("iPad Mini 2 (Cellular)", "iPad4,5");
            put("iPad Mini 2 (China)", "iPad4,6");
            put("iPad Mini 3 (WiFi)", "iPad4,7");
            put("iPad Mini 3 (Cellular)", "iPad4,8");
            put("iPad Mini 3 (China)", "iPad4,9");
            put("iPad Mini 4 (Wifi)", "iPad5,1");
            put("iPad Mini 4 (Cellular)", "iPad5,2");
            put("iPad Air 2 (WiFi)", "iPad5,3");
            put("iPad Air 2 (Cellular)", "iPad5,4");
            put("iPad Pro 9.7 (Wifi)", "iPad6,3");
            put("iPad Pro 9.7 (Cellular)", "iPad6,4");
            put("iPad Pro 12.9 (WiFi)", "iPad6,7");
            put("iPad Pro 12.9 (Cellular)", "iPad6,8");
            put("iPad 5 (Wifi)", "iPad6,11");
            put("iPad 5 (Cellular)", "iPad6,12");
            put("iPad Pro 12.9 (2Gen) (WiFi)", "iPad7,1");
            put("iPad Pro 12.9 (2Gen) (Cellular)", "iPad7,2");
            put("iPad Pro 10.5 (WiFi)", "iPad7,3");
            put("iPad 10.5 (Cellular)", "iPad7,4");
            put("iPad 6 (WiFi)", "iPad7,5");
            put("iPad 6 (Cellular)", "iPad7,6");
        }
    };

    private static Map<String, String> iPods = new LinkedHashMap<String, String>() {
        {
            put("iPod Touch 3", "iPod3,1");
            put("iPod Touch 4", "iPod4,1");
            put("iPod Touch 5", "iPod5,1");
            put("iPod Touch 6", "iPod7,1");
        }
    };

    private static Map<String, String> AppleTVs = new LinkedHashMap<String, String>() {
        {
            put("Apple TV 2G", "AppleTV2,1");
            put("Apple TV 3", "AppleTV3,1");
            put("Apple TV 3 (2013)", "AppleTV3,2");
            put("Apple TV 4 (2015)", "AppleTV5,3");
            put("Apple TV 4K", "AppleTV6,2");
        }
    };

    
    private String name;
    private String ECID;
    private String identifier;
    private String boardConfig;
    private List<Shsh> shshList;
    
    /**
     * iDevice initialize no name(auto initialize name)
     *
     * @param ECID need param
     * @param identifier need param
     */
    private Idevice(String ECID, String identifier) {
        this.ECID = ECID;
        this.identifier = identifier;
        this.name = this.identifier;

        deviceList.add(this);
    }

    private Idevice(String name, String ECID, String identifier) {
        this.name = name;
        this.ECID = ECID.toUpperCase();
        this.identifier = identifier;

        deviceList.add(this);
    }

    private Idevice(String name, String ECID, String identifier, String boardConfig) {
        this.name = name;
        this.ECID = ECID.toUpperCase();
        this.identifier = identifier;
        this.boardConfig = boardConfig.toUpperCase();

        deviceList.add(this);
    }

    public Idevice(String[] data) {
        this.name = data[0];
        this.ECID = data[1].toUpperCase();
        this.identifier = data[2];
        this.boardConfig = data[3];
        if (this.boardConfig != null) this.boardConfig = this.boardConfig.toUpperCase();

        deviceList.add(this);
    }
    
    /**
     * デバイスのユーザー名前（自由に設定できる名前）を返します。
     * @return e.g. shoのiPhone
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * デバイスのECIDを返します。
     * @return String型のECID
     */
    public String getECID() {
        return this.ECID;
    }
    
    
    /**
     * デバイスのモデル名を返します。
     * @return e.g. iPhone3,1
     */
    public String getIdentifier(){
        return this.identifier;
    }
    
    /**
     * デバイスのboardConfigを返します。
     * @return 
     */
    public String getBoardConfig(){
        return this.boardConfig;
    }
    
    /**
     * 同じ名前のデバイスが被らない様に、個々の情報を付与した名前を返します。
     * @return name ({ECID})
     */
    public String toUniqueName(){
        return this.name +" ("+this.ECID+")"; 
    }
    
    
    public void setShsh(List<Shsh> shshList){
        this.shshList = shshList;
    }
    
    public void addShsh(Shsh shsh){
        this.shshList.add(shsh);
    }
    
    
        
    /**
     * 新しいデバイスを追加します。
     */
    public void saveDB(){
        Database.putSQLite(this);
    }
    
    
    public void removeDB(){
        Database.deleteSQLite(this);
        deviceList.remove(this);
    }
    
    
    
    
    /**
     * ユニークネームを基に、デバイスがリストに存在するかどうかを判定
     * @param uniqueName
     * @return 
     */
    public static boolean isEnableUniqueName(String uniqueName){
        for (Idevice idevice : deviceList) {
            if (uniqueName.equals(idevice.name +" ("+idevice.ECID+")")){
                return true;
            }
        }
        return false;
    }
    
    /**
     * ユニークネームを基に、デバイスリストからデバイスを返す
     * @param uniqueName
     * @return 
     */
    public static Idevice toIdevice(String uniqueName){
        for (Idevice idevice : deviceList) {
            if (uniqueName.equals(idevice.name +" ("+idevice.ECID+")")){
                return idevice;
            }
        }
        return null;
    }
    

    
    /**
     * デバイス名のリスト(iPhoneXなど)を返します。
     * 
     * @param deviceType デバイスタイプを指定します。
     * @return Stringの配列
     */
    public static String[] getDeviceNameLists(String deviceType){
        List<String> tmpList = new LinkedList<>();
        switch(deviceType){
            case "iPhone":
                for (String key : iPhones.keySet()) {
                    tmpList.add(key);
                }
                break;
            case "iPad":
                for (String key : iPads.keySet()) {
                    tmpList.add(key);
                }
                break;
            case "iPod":
                for (String key : iPods.keySet()) {
                    tmpList.add(key);
                }
                break;
            case "AppleTV":
                for (String key : AppleTVs.keySet()) {
                    tmpList.add(key);
                }
                break;
        }
        return tmpList.toArray(new String[tmpList.size()]);
    };
    
    /**
     * デバイス名からmodel番号を返します。
     * @param name
     * @return exp:iPhone2,1
     */
    public static String toIdentifier(String name){
        if (name.indexOf("iPhone") > -1 && iPhones.containsKey(name)){
            return iPhones.get(name);
        } else if (name.indexOf("iPad") > -1 && iPads.containsKey(name)) {
            return iPads.get(name);
        } else if (name.indexOf("iPod") > -1 && iPods.containsKey(name)) {
            return iPods.get(name);
        } else if (name.indexOf("AppleTV") > -1 && AppleTVs.containsKey(name)) {
            return AppleTVs.get(name);
        } else {
            return null;
        }
    };
    
    
    public static String toDeviceName(String identifier){
        Map<String, String> tmp;
        if (identifier.indexOf("iPhone") == 0){
            tmp = iPhones;
        } else if (identifier.indexOf("iPad") == 0) {
            tmp = iPads;
        } else if (identifier.indexOf("iPod") == 0) {
            tmp = iPods;
        } else if (identifier.indexOf("AppleTV") == 0) {
            tmp = AppleTVs;
        } else {
            return null;
        }
        
        for(String val : tmp.values()){
            if (val.equals(identifier)) return val; 
        }
        return null;
    };
    
    
    
    
   
    
    public static List<Idevice> getDeviceList(){
        return deviceList;
    };
    
    
    
    
    
    
    public static void setDeviceInfo(String path){
        
    };
    
    
    
    public static void loadDB() throws Exception{
        List<Idevice> ids = Database.loadSQLite();
    }
    
    public static void loadCSV(String path) {
        try {
            File csv = new File(path); // CSVデータファイル

            BufferedReader br = new BufferedReader(new FileReader(csv));

            // 最終行まで読み込む
            String line = "";
            int num = 0;
            while ((line = br.readLine()) != null) {
                if (num <= 0) {
                    num++;
                } else {
                    // 1行をデータの要素に分割
                    if (line.indexOf("\",\"") != -1) line = line.replaceAll("\",\"", "&#44;");
                    StringTokenizer st = new StringTokenizer(line, ",");
                    int length = st.countTokens();
                    String[] datas = new String[length];
                    for (int i = 0; i < length; i++) {
                        datas[i] = st.nextToken();
                        if (datas[i].indexOf("&#44;") != -1) datas[i] = datas[i].replaceAll("&#44;", ",");
                    }
                    
                    Idevice device = new Idevice(datas);
                }
            }
            br.close();

        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
        }
    }

    public static void writeCSV(String path) {
        try {
            File csv = new File(path); // CSVデータファイル
            // 追記モード
            BufferedWriter bw
                    = new BufferedWriter(new FileWriter(csv, true));
            // 新たなデータ行の追加
            bw.write("name, ECID, identifier, boardConfig");
            bw.newLine();
            for (Idevice idevice : deviceList) {
                String data = idevice.name + "&#44;" + idevice.ECID + "&#44;" + idevice.identifier + "&#44;" + idevice.boardConfig;
                if (data.indexOf(",") != -1) data = data.replaceAll(",", "\",\"");
                data = data.replaceAll("&#44;", ",");
                bw.write(data);
                bw.newLine();
            }

            bw.close();

        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedWriterオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }
    }
    
    

}
