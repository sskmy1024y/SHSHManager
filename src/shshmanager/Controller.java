/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shshmanager;

import com.sun.javafx.PlatformUtil;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;
import library.Idevice;
import library.Shsh;

/**
 *
 * @author sho
 */
public class Controller implements Initializable {

    @FXML
    private Label label;
    @FXML
    private Label infoDeviceModel;
    @FXML
    private Label infoDeviceECID;
    @FXML
    private Label infoDeviceBoardConfig;
    @FXML
    private TextField apnonceField;
    @FXML
    private TextField versionField;
    @FXML
    private TextField buildIDField;
    @FXML
    private CheckBox apnonceCheckBox;
    @FXML
    private CheckBox versionCheckBox;
    @FXML
    private CheckBox betaCheckBox;
    @FXML
    private ImageView infoDeviceImage;
    @FXML
    private ListView deviceList;
    @FXML
    private TableView<ShshList> shshListTable = new TableView<>();
    @FXML
    private TableColumn<ShshList, String> deviceCol;
    @FXML
    private TableColumn<ShshList, String> buildCol;
    @FXML
    private TableColumn<ShshList, String> versionCol;
    @FXML
    private TableColumn<ShshList, String> boardconfigCol;
    @FXML
    private TableColumn<ShshList, String> apnonceCol;
    @FXML
    private TableColumn<ShshList, String> dateCol;
    @FXML
    private TableColumn<ShshList, String> typeCol;
    @FXML
    private Button getSHSH;

    private static final String savePath = System.getProperty("user.home") + "/.sh/.shshmanager/shsh/";

    private static File buildManifestPlist;
    private static File tsschecker;

    private static Idevice selectedIdevice;

    private ButtonType githubIssue = new ButtonType("Create Issue on Github");
    private URI githubIssueURI;
    private DropShadow errorBorder = new DropShadow();

    Stage ownerStage;

    //=========================
    // initialize method
    //=========================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resetListView();

        //クリックに関するイベント設定
        deviceList.setOnMouseClicked((MouseEvent event) -> {
            ListView<String> l = (ListView) event.getSource();
            String s = (String) l.getSelectionModel().getSelectedItem();
            if (s == null || !Idevice.isEnableUniqueName(s)) {
                return;
            } else {
                selectedIdevice = Idevice.toIdevice(s);
                if (selectedIdevice != null) {
                    infoDeviceModel.setText(selectedIdevice.getIdentifier());
                    infoDeviceECID.setText(selectedIdevice.getECID());
                    infoDeviceBoardConfig.setText(selectedIdevice.getBoardConfig());
                    try {
                        infoDeviceImage.setImage(new Image("/resource/img/" + selectedIdevice.getIdentifier() + ".png"));
                    } catch (Exception e) {
                        infoDeviceImage.setImage(null);
                    }

                    //shshのリストを取得
                    List<Shsh> shshList = Shsh.getDB(selectedIdevice);
                    resetSHSHListView(selectedIdevice, shshList);
                }
            }
            return;
        });

        //右クリック
        ContextMenu context = new ContextMenu();
        MenuItem[] items = new MenuItem[2];
        items[0] = new MenuItem("Change Device Name");
        items[1] = new MenuItem("Delete");

        deviceList.setOnContextMenuRequested((ContextMenuEvent event) -> {
            ListView<String> l = (ListView) event.getSource();
            items[0].setOnAction(e -> {
                l.setEditable(true);
            });
            items[1].setOnAction(e -> {
                String s = (String) l.getSelectionModel().getSelectedItem();
                if (!s.equals(null) && Idevice.isEnableUniqueName(s)) {
                    Idevice sd = Idevice.toIdevice(s);
                    sd.removeDB();
                    resetListView();
                }
            });
            context.getItems().addAll(items);
            context.show(deviceList, event.getScreenX(), event.getScreenY());
            //event.consume();
        });

        // ShshTable
        shshListTable.setOnContextMenuRequested((ContextMenuEvent event) -> {

        });

        // PropertyValueFactoryで"materialID"を指定すると、TableRecord#materialIDProperty()が呼ばれる。
        versionCol.setCellValueFactory(new PropertyValueFactory<>("version"));
        buildCol.setCellValueFactory(new PropertyValueFactory<>("build"));
        apnonceCol.setCellValueFactory(new PropertyValueFactory<>("apnonce"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        errorBorder.setOffsetY(0f);
        errorBorder.setOffsetX(0f);
        errorBorder.setColor(Color.RED);
        errorBorder.setWidth(20);
        errorBorder.setHeight(20);

    }

    public void versionCheckBoxHandler() {
        if (versionCheckBox.isSelected()) {
            versionField.setDisable(true);
            versionField.setEffect(null);
            versionField.setText("");
        } else {
            int depth = 20;
            DropShadow borderGlow = new DropShadow();
            borderGlow.setOffsetY(0f);
            borderGlow.setOffsetX(0f);
            borderGlow.setColor(Color.DARKCYAN);
            borderGlow.setWidth(depth);
            borderGlow.setHeight(depth);
            versionField.setEffect(borderGlow);

            versionField.setDisable(false);
        }
    }

    public void apnonceCheckBoxHandler() {
        if (apnonceCheckBox.isSelected()) {
            apnonceField.setDisable(false);
            int depth = 20;
            DropShadow borderGlow = new DropShadow();
            borderGlow.setOffsetY(0f);
            borderGlow.setOffsetX(0f);
            borderGlow.setColor(Color.DARKCYAN);
            borderGlow.setWidth(depth);
            borderGlow.setHeight(depth);
            apnonceField.setEffect(borderGlow);
        } else {
            apnonceField.setEffect(null);
            apnonceField.setText("");
            apnonceField.setDisable(true);
        }
    }

    public void betaCheckBoxHandler() {
        if (betaCheckBox.isSelected()) {
            //ipswField.setDisable(false);
            int depth = 20;
            DropShadow borderGlow = new DropShadow();
            borderGlow.setOffsetY(0f);
            borderGlow.setOffsetX(0f);
            borderGlow.setColor(Color.DARKCYAN);
            borderGlow.setWidth(depth);
            borderGlow.setHeight(depth);
            //ipswField.setEffect(borderGlow);
            buildIDField.setDisable(false);
            buildIDField.setEffect(borderGlow);
            if (versionCheckBox.isSelected()) {
                versionCheckBox.fire();
            }
            versionCheckBox.setDisable(true);
        } else {
            //ipswField.setEffect(null);
            //ipswField.setText("");
            //ipswField.setDisable(true);
            buildIDField.setEffect(null);
            buildIDField.setText("");
            buildIDField.setDisable(true);
            versionCheckBox.setDisable(false);
        }
    }

    /**
     * 最初に呼び出されるメソッド。デバイスリストをリセットする。
     */
    public void resetListView() {
        List<Idevice> devices = Idevice.getDeviceList();
        if (devices == null) {
            return;
        }
        String[] names = new String[devices.size()];
        int i = 0;
        for (Idevice device : devices) {
            names[i] = device.toUniqueName();
            i++;
        }
        deviceList.setItems(FXCollections.observableArrayList(names));
    }

    public void resetSHSHListView(Idevice idevice, List<Shsh> shshs) {
        if (shshs == null) {
            return;
        }
        ObservableList<ShshList> tableRecord = FXCollections.observableArrayList();
        String[] list = new String[shshs.size()];
        int i = 0;
        for (Shsh shsh : shshs) {
            tableRecord.add(new ShshList(idevice, shsh));
        }
        if (tableRecord != null) {
            shshListTable.setItems(tableRecord);
        }
        shshListTable.layout();
    }

    public static class ShshList {

        private StringProperty version;
        private StringProperty build;
        private StringProperty apnonce;
        private StringProperty date;
        private StringProperty type;

        public ShshList(Idevice device, Shsh shsh) {
            this.version = new SimpleStringProperty(shsh.getIOS());
            this.build = new SimpleStringProperty(shsh.getBuild());
            this.apnonce = new SimpleStringProperty(shsh.getApnonce());
            this.date = new SimpleStringProperty(shsh.getTime());
            this.type = new SimpleStringProperty("");
        }

        public StringProperty versionProperty() {
            return version;
        }

        public StringProperty buildProperty() {
            return build;
        }

        public StringProperty apnonceProperty() {
            return apnonce;
        }

        public StringProperty dateProperty() {
            return date;
        }

        public StringProperty typeProperty() {
            return type;
        }
    }

    public void setOwnerStage(Stage stage) {
        ownerStage = stage;
    }

    //=========================
    // menu method
    //=========================
    /**
     * デバイスを追加するウィンドウを出す
     *
     * @param e
     */
    @FXML
    protected void addDeviceMenuBtn(ActionEvent e) {
        try {
            Scene newScene = new Scene(FXMLLoader.load(getClass().getResource("adddevice.fxml")));
            Stage newStage = new Stage();
            newStage.initOwner(ownerStage); // 親のStageを登録
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle("add New Device");
            newStage.setScene(newScene);
            newStage.showingProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == true && newValue == false) {
                    resetListView();
                }
            });
            newStage.show(); // 表示
            newStage.toFront(); // ウインドウを前面に移動
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //=========================
    // assist method
    //=========================
    public void go() {
        List<Idevice> devices = new ArrayList<>();
        if (selectedIdevice != null) {
            devices.add(selectedIdevice);
        } else {
            devices = Idevice.getDeviceList();
        }

        run(devices);
    }

    public void fetchAll() {
        selectedIdevice = null;
        go();
    }

    private void run(List<Idevice> devices) {
        try {
            InputStream input;
            if (PlatformUtil.isWindows()) {
                input = getClass().getResourceAsStream("/resource/tsschecker_windows.exe");
                tsschecker = File.createTempFile("tsschecker_windows", ".tmp.exe");
            } else if (PlatformUtil.isMac()) {
                input = getClass().getResourceAsStream("/resource/tsschecker_macos");
                tsschecker = File.createTempFile("tsschecker_macos", ".tmp");
            } else {
                input = getClass().getResourceAsStream("/resource/tsschecker_linux");
                tsschecker = File.createTempFile("tsschecker_linux", ".tmp");
            }
            OutputStream out = new FileOutputStream(tsschecker);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        tsschecker.deleteOnExit();

        if (!tsschecker.setExecutable(true, false)) {
            newReportableError("There was an error setting tsschecker as executable.");
            deleteTempFiles();
            return;
        }

        // ============================
        // 取得中表示のためのwindow
        // ============================
        // 新しいウインドウを生成
        Stage downloadingStage = new Stage();
        // モーダルウインドウに設定
        downloadingStage.initModality(Modality.WINDOW_MODAL);
        // オーナーを設定
        downloadingStage.initOwner(ownerStage);

        // 新しいウインドウ内に配置するコンテンツを生成
        HBox hbox = new HBox();
        Label label = new Label("");
        label.setFont(new Font(20d));
        hbox.getChildren().add(label);

        // プログレスバーを表示
        ProgressBar bar = new ProgressBar();
        bar.setProgress(0.0);
        bar.setPrefWidth(180);

        downloadingStage.setScene(new Scene(hbox));

        // 新しいウインドウを表示
        downloadingStage.show();

        double per = 100 / 5 / devices.size();

        for (Idevice device : devices) {

            //==============================
            // 保存先フォルダの指定
            //==============================
            String dirPath = savePath + device.getECID() + "/";
            File locationToSaveBlobs = new File(dirPath);
            locationToSaveBlobs.mkdirs();


            //==============================
            // TSSCheckerの引数を設定
            //==============================
            ArrayList<String> args;
            args = new ArrayList<>(Arrays.asList(tsschecker.getPath(), "-d", device.getIdentifier(), "-s", "-e", device.getECID(), "--save-path", dirPath));

            System.out.println(device.getBoardConfig());
            if (device.getBoardConfig() != null) {
                //Boardconfigが設定されていれば
                args.add("--boardconfig");
                args.add(device.getBoardConfig().toLowerCase());
            }

            //apnonceの処理
            if (!apnonceField.getText().equals("")) {
                args.add("--apnonce");
                args.add(apnonceField.getText());
            }

            //versionの処理
            if (!versionField.getText().equals("")) {
                args.add("--ios");
                args.add(versionField.getText());

                //ベータ指定されている場合、ビルドIDを指定
                if (betaCheckBox.isSelected()) {
                    args.add("--buildid");
                    args.add(versionField.getText());
                }

            } else {
                args.add("-l");
            }

            //==============================
            // フォルダ内のスナップショット
            //==============================
            Shsh.getBeforeFiles(dirPath);

            //==============================
            // 動作開始させる
            //==============================
            Process proc;
            try {
                System.out.println("Running: " + args.toString());
                proc = new ProcessBuilder(args).start();
            } catch (IOException e) {
                newReportableError("There was an error starting tsschecker.", e.toString());
                e.printStackTrace();
                deleteTempFiles();
                return;
            }
            //エラー等のログを書き込む
            String tsscheckerLog;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                StringBuilder logBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line + "\n");
                    logBuilder.append(line).append("\n");
                }
                tsscheckerLog = logBuilder.toString();
            } catch (IOException e) {
                newReportableError("There was an error getting the tsschecker result", e.toString());
                e.printStackTrace();
                deleteTempFiles();
                return;
            }

            if (tsscheckerLog.contains("Saved shsh blobs")) {
                //shshの取得が完了したら
                String ios = "";
                String build = "";
                String apnonce = "";
                String type = "";
                String ecid = device.getECID();
                if (tsscheckerLog.contains("[TSSC] got firmwareurl for iOS ")) {
                    String stword = "[TSSC] got firmwareurl for iOS ";
                    String edword = " build ";
                    String splitWord = tsscheckerLog.substring(tsscheckerLog.lastIndexOf(stword));
                    splitWord = splitWord.substring(0, splitWord.indexOf("\n"));

                    ios = splitWord.substring(stword.length(), splitWord.indexOf(edword));
                    build = splitWord.substring(splitWord.indexOf(edword) + edword.length());

                    System.out.println(ios);
                    System.out.println(build);
                }

                File shsh2 = Shsh.getFilesDiff();

                //DBへの保存処理
                Shsh shsh = new Shsh(shsh2, ecid, ios, build, apnonce, type);
                shsh.saveDB();

                continue;
            } else if (tsscheckerLog.contains("[Error] [TSSC] manually specified ecid=" + device.getECID() + ", but parsing failed")) {
                newUnreportableError("\"" + device.getECID() + "\"" + " is not a valid ECID. Try getting it from iTunes");
            } else if (tsscheckerLog.contains("[Error] [TSSC] device " + device.getIdentifier() + " could not be found in devicelist")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "tsschecker could not find device: \"" + device.getIdentifier()
                        + "\"\n\nPlease create a new Github issue if you used the dropdown menu.", githubIssue, ButtonType.CANCEL);
                alert.showAndWait();
                reportError(alert);
            } else if (tsscheckerLog.contains("[Error] [TSSC] ERROR: could not get url for device " + device + " on iOS " + versionField.getText())) {
                newUnreportableError("Could not find device \"" + device + "\" on iOS/tvOS " + versionField.getText()
                        + "\n\nThe version doesn't exist or isn't compatible with the device");
                versionField.setEffect(errorBorder);
            } else if (tsscheckerLog.contains("[Error] [TSSC] manually specified apnonce=" + apnonceField.getText() + ", but parsing failed")) {
                newUnreportableError("\"" + apnonceField.getText() + "\" is not a valid apnonce");
                apnonceField.setEffect(errorBorder);
            } else if (tsscheckerLog.contains("[WARNING] [TSSC] could not get id0 for installType=Erase. Using fallback installType=Update since user did not specify installType manually")
                    && tsscheckerLog.contains("[Error] [TSSR] Error: could not get id0 for installType=Update")
                    && tsscheckerLog.contains("[Error] [TSSR] faild to build tssrequest")
                    && tsscheckerLog.contains("Error] [TSSC] checking tss status failed!")) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Saving blobs failed. Check the board configuration or try again later.\n\nIf this doesn't work, please create a new issue on Github. The log has been copied to your clipboard.",
                        githubIssue, ButtonType.OK);
                alert.showAndWait();
                reportError(alert, tsscheckerLog);
            } else if (tsscheckerLog.contains("[Error] ERROR: TSS request failed: Could not resolve host:")) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Saving blobs failed. Check your internet connection.\n\nIf your internet is working and you can connect to apple.com in your browser, please create a new issue on Github. The log has been copied to your clipboard.",
                        githubIssue, ButtonType.OK);
                alert.showAndWait();
                reportError(alert, tsscheckerLog);
            } else if (tsscheckerLog.contains("[Error] [Error] can't save shsh at " + dirPath)) {
                newUnreportableError("\'" + dirPath + "\' is not a valid path");
            } else if (tsscheckerLog.contains("iOS " + versionField.getText() + " for device " + device + " IS NOT being signed!")) {
                newUnreportableError("iOS/tvOS " + versionField.getText() + " is not being signed for device " + device);
                versionField.setEffect(errorBorder);
                /*
            } else if (tsscheckerLog.contains("[Error] [TSSC] failed to load manifest")) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Failed to load manifest.\n\n \"" + ipswField.getText() + "\" might not be a valid URL.\n\nMake sure it starts with \"http://\" or \"https://\", has \"apple\" in it, and ends with \".ipsw\"\n\nIf the URL is fine, please create a new issue on Github. The log has been copied to your clipboard",
                        githubIssue, ButtonType.OK);
                alert.showAndWait();
                reportError(alert, tsscheckerLog);*/
            } else if (tsscheckerLog.contains("[Error]")) {
                newReportableError("Saving blobs failed.", tsscheckerLog);
            } else {
                newReportableError("Unknown result.", tsscheckerLog);
            }

            try {
                proc.waitFor();
            } catch (InterruptedException e) {
                newReportableError("The tsschecker process was interrupted.", e.toString());
            }

        }

        resetSHSHListView(selectedIdevice, Shsh.getDB(selectedIdevice));


        downloadingStage.hide();
        downloadingStage.close();
        deleteTempFiles();

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteTempFiles() {
        try {
            if (tsschecker.exists()) {
                tsschecker.delete();
            }
            if (buildManifestPlist.exists()) {
                buildManifestPlist.delete();
            }
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * GithubのIssueへ送信
     */
    public void newGithubIssue() {
        if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(githubIssueURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void reportError(Alert alert) {
        if (alert.getResult().equals(githubIssue)) {
            newGithubIssue();
        }
    }

    private void reportError(Alert alert, String toCopy) {
        StringSelection stringSelection = new StringSelection(toCopy);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        reportError(alert);
    }

    private void newReportableError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg + "\n\nPlease create a new issue on Github.", githubIssue, ButtonType.CANCEL);
        alert.showAndWait();
        reportError(alert);
    }

    private void newReportableError(String msg, String toCopy) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg + "\n\nPlease create a new issue on Github. The log has been copied to your clipboard.", githubIssue, ButtonType.CANCEL);
        alert.showAndWait();
        reportError(alert, toCopy);
    }

    private void newUnreportableError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

}
