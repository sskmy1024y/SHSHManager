package library;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author sho
 */
public class Database {
    
    private static File dbFile = new File(System.getProperty("user.home") + "/.sh/.shshmanager/userdata.db");
    
    /**
     * SQLiteドライバの登録
     */
    public static void dbInit() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 現在時刻をミリ秒にして返す。
     * @return long
     */
    private static long onDateSet(){
        return System.currentTimeMillis();
    }
        
    /**
     * ミリ秒を文字列にして返す。
     * @param ms
     * @return 
     */
    public static String msToString(long ms){
        String str;
        Date date = new Date(ms);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        str = formatter.format(date);
        return str;
    }
    
    /**
     * ideviceに関するデータベース情報を取得する。
     * 
     * @param dbFile
     * @return
     * @throws Exception 
     */
    public static List<Idevice> loadSQLite() throws Exception{
        String dbHeader = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        PreparedStatement pstmt;
        List<Idevice> idevices = new ArrayList<>();//適当なリスト
        dbInit();
        try (Connection conn = DriverManager.getConnection(dbHeader)) {
            pstmt = conn.prepareStatement("SELECT * FROM devices");
            pstmt.setFetchSize(1000);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String param[] = new String[4];
                param[0] = rs.getString("name");
                param[1] = rs.getString("ecid");
                param[2] = rs.getString("identifier");
                param[3] = rs.getString("boardconfig");
                Idevice idevice = new Idevice(param);
                idevices.add(idevice);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("[error]");
        }
        return idevices;
    }

    /**
     * データベースに追加する処理
     * synchronizedを入れないとマルチスレッドでガンガン書き込んだときにdeadlockを起こす
     *
     * @param idevice Ideviceクラスのオブジェクト
     */
    public static synchronized void putSQLite (Idevice idevice) {
        Statement stmt;
        String dbHeader = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        PreparedStatement pstmt;
        dbInit();
        try (Connection conn = DriverManager.getConnection(dbHeader)) { //try-with-resources
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            //データベースが無かったら作成
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS devices(ecid TEXT PRIMARY KEY, name TEXT, identifier TEXT, boardconfig TEXT)" );
           
            pstmt = conn.prepareStatement("INSERT OR IGNORE INTO devices VALUES (?, ?, ?, ?)");
            
            pstmt.setString(1, idevice.getECID());
            pstmt.setString(2, idevice.getName());
            pstmt.setString(3, idevice.getIdentifier());
            pstmt.setString(4, idevice.getBoardConfig());
            
            pstmt.execute();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 指定したIdeviceクラスの情報を削除
     * @param idevice
     */
    public static synchronized void deleteSQLite (Idevice idevice) {
        Statement stmt;
        String dbHeader = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        PreparedStatement pstmt;
        dbInit();
        try (Connection conn = DriverManager.getConnection(dbHeader)) { //try-with-resources
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement("DELETE FROM devices WHERE ecid = ?");
            
            pstmt.setString(1, idevice.getECID());
            
            pstmt.execute();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * データベースから指定したideviceのSHSH情報を取得
     * @param idevice　Ideviceクラスのオブジェクトを指定
     * @return List型の配列を返す
     */
    public static List<Shsh> loadShshfromSQLite(Idevice idevice) {
        String dbHeader = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        PreparedStatement pstmt;
        List<Shsh> shshs = new ArrayList<>();//適当なリスト
        String ecid = idevice.getECID();
        dbInit();
        try (Connection conn = DriverManager.getConnection(dbHeader)) {
            pstmt = conn.prepareStatement("SELECT * FROM shsh where ecid =?");
            pstmt.setString(1, ecid);
            pstmt.setFetchSize(1000);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String param[] = new String[7];
                param[0] = rs.getString("filename");
                param[1] = rs.getString("ecid");
                param[2] = rs.getString("ios");
                param[3] = rs.getString("build");
                param[4] = rs.getString("apnonce");
                param[5] = rs.getString("type");
                param[6] = rs.getString("created_at");
                Shsh shsh = new Shsh(param);
                shshs.add(shsh);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("[Error] cant load shsh");
        }
        return shshs;
    }
    
    
    public static synchronized void putSQLite (Shsh shsh) {
        Statement stmt;
        String dbHeader = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        PreparedStatement pstmt;
        dbInit();
        try (Connection conn = DriverManager.getConnection(dbHeader)) { //try-with-resources
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            //データベースが無かったら作成
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS shsh (ecid TEXT, filename TEXT, ios TEXT, build TEXT, apnonce TEXT, type TEXT, created_at INTEGER)" );
           
            pstmt = conn.prepareStatement("INSERT OR IGNORE INTO shsh VALUES (?, ?, ?, ?, ?, ?, ?)");
            
            pstmt.setString(1, shsh.getECID());
            pstmt.setString(2, shsh.getFileName());
            pstmt.setString(3, shsh.getIOS());
            pstmt.setString(4, shsh.getBuild());
            pstmt.setString(5, shsh.getApnonce());
            pstmt.setString(6, shsh.getType());
            pstmt.setLong(7, onDateSet());
            
            pstmt.execute();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[Error] cant set database");
        }
    }
    
    
    
}
