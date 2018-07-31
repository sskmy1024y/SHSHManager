/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shshmanager;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import library.Idevice;

/**
 *
 * @author sho
 */
public class addDeviceController implements Initializable {

    @FXML
    private ChoiceBox deviceTypeChoiceBox;
    @FXML
    private ChoiceBox deviceModelChoiceBox;

    @FXML private TextField nameField;
    @FXML
    private TextField ecidField;
    @FXML
    private TextField boardConfigField;
    @FXML
    private TextField identifierField;
    @FXML
    private TextField buildIDField;

    @FXML
    private CheckBox identifierCheckBox;

    @FXML
    private ImageView deviceImageView;
    
    @FXML private Button addButton;

    private boolean boardConfig = false;

    private File buildManifestPlist;
    private File tsschecker;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        final String[] devices = {"iPhone", "iPod", "iPad", "AppleTV"};

        final ObservableList iPhones = FXCollections.observableArrayList(Idevice.getDeviceNameLists(devices[0]));

        final ObservableList iPods = FXCollections.observableArrayList(Idevice.getDeviceNameLists(devices[1]));

        final ObservableList iPads = FXCollections.observableArrayList(Idevice.getDeviceNameLists(devices[2]));

        final ObservableList AppleTVs = FXCollections.observableArrayList(Idevice.getDeviceNameLists(devices[3]));

        //デバイスタイプをリストに設定
        deviceTypeChoiceBox.setItems(FXCollections.observableArrayList(devices));

        //デバイスタイプを設定した後に、デバイスを選択する
        deviceTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            deviceTypeChoiceBox.setEffect(null);
            if (newValue == null) {
                return;
            }
            final String v = (String) newValue;
            switch (v) {
                case "iPhone":
                    deviceModelChoiceBox.setItems(iPhones);
                    break;
                case "iPod":
                    deviceModelChoiceBox.setItems(iPods);
                    break;
                case "iPad":
                    deviceModelChoiceBox.setItems(iPads);
                    break;
                case "AppleTV":
                    deviceModelChoiceBox.setItems(AppleTVs);
                    break;
            }
        });

        //デバイスのモデルを選択後の動作
        deviceModelChoiceBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            deviceModelChoiceBox.setEffect(null);
            if (newValue == null) {
                boardConfigField.setEffect(null);
                boardConfig = false;
                boardConfigField.setText("");
                boardConfigField.setDisable(true);
                deviceImageView.setImage(null);
                return;
            }

            final String v = (String) newValue;
            final String m = Idevice.toIdentifier(v);

            if (m != null) {
                identifierField.setText(m);
                String path = "/resource/" + m + ".png";
                deviceImageView.setImage(new Image(path));

                //boardconfigの必要なデバイスの処理
                if (m.equals("iPhone8,1") || m.equals("iPhone8,2") || m.equals("iPhone8,4") || m.equals("iPad7,5") || m.equals("iPad7,6")) {
                    int depth = 20;
                    DropShadow borderGlow = new DropShadow();
                    borderGlow.setOffsetY(0f);
                    borderGlow.setOffsetX(0f);
                    borderGlow.setColor(Color.DARKCYAN);
                    borderGlow.setWidth(depth);
                    borderGlow.setHeight(depth);
                    boardConfigField.setEffect(borderGlow);
                    boardConfig = true;
                    boardConfigField.setDisable(false);
                } else {
                    boardConfigField.setEffect(null);
                    boardConfig = false;
                    boardConfigField.setText("");
                    boardConfigField.setDisable(true);
                }

            }

        });

        //自分でidentifierを設定する場合
        identifierField.textProperty().addListener((observable, oldValue, newValue) -> {
            identifierField.setEffect(null);
            
            final String m = (String) newValue;
            
            //ChoiceBoxを自動指定
            for (String device : devices) {
                if (m.indexOf(device) == 0) {
                    deviceTypeChoiceBox.setValue(device);
                    break;
                } else deviceTypeChoiceBox.setValue(null);
            }
            
            
            try{
                String path = "/resource/img/" + m + ".png";
                deviceImageView.setImage(new Image(path));
            }catch(Exception e){
                deviceImageView.setImage(null);
            }
            
            //boardconfigの必要なデバイスの処理
            if (m.equals("iPhone8,1") || m.equals("iPhone8,2") || m.equals("iPhone8,4") || m.equals("iPad7,5") || m.equals("iPad7,6")) {
                final int depth = 20;
                DropShadow borderGlow = new DropShadow();
                borderGlow.setOffsetY(0f);
                borderGlow.setOffsetX(0f);
                borderGlow.setColor(Color.DARKCYAN);
                borderGlow.setWidth(depth);
                borderGlow.setHeight(depth);
                boardConfigField.setEffect(borderGlow);
                boardConfig = true;
                boardConfigField.setDisable(false);
            } else {
                boardConfigField.setEffect(null);
                boardConfig = false;
                boardConfigField.setText("");
                boardConfigField.setDisable(true);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void identifierCheckBoxHandler() {
        if (identifierCheckBox.isSelected()) {
            identifierField.setDisable(false);
            int depth = 20;
            DropShadow borderGlow = new DropShadow();
            borderGlow.setOffsetY(0f);
            borderGlow.setOffsetX(0f);
            borderGlow.setColor(Color.DARKCYAN);
            borderGlow.setWidth(depth);
            borderGlow.setHeight(depth);
            identifierField.setEffect(borderGlow);
            deviceTypeChoiceBox.getSelectionModel().clearSelection();
            deviceModelChoiceBox.getSelectionModel().clearSelection();
            deviceTypeChoiceBox.setValue(null);
            deviceTypeChoiceBox.setDisable(true);
            deviceModelChoiceBox.setDisable(true);
            deviceTypeChoiceBox.setEffect(null);
            deviceModelChoiceBox.setEffect(null);
        } else {
            identifierField.setEffect(null);
            identifierField.setText("");
            identifierField.setDisable(true);
            deviceTypeChoiceBox.setDisable(false);
            deviceModelChoiceBox.setDisable(false);
        }
    }

    
    public void addDevice(){
        final String name = (String) nameField.getText();
        final String ecid = (String) ecidField.getText();
        final String identifier = (String) identifierField.getText();
        String boardconfig = null;
        if (this.boardConfig) boardconfig = boardConfigField.getText();
        
        String param[] = {
            name,
            ecid,
            identifier,
            boardconfig
        };
        
        Idevice idevice = new Idevice(param);
        idevice.saveDB();

        addButton.getScene().getWindow().hide();
    }
}
