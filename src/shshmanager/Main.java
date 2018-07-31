/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shshmanager;

import com.sun.javafx.PlatformUtil;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.*;

/**
 *
 * @author sho
 */
public class Main extends Application {

    private static final String version = "v1.0.0";




    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("shshmanager.fxml"));
        Parent root = loader.load();

        Controller controller = (Controller) loader.getController();
        controller.setOwnerStage(stage);

        //loader.setRoot(this);
        //Parent root = loader.getRoot();
        //Parent root = FXMLLoader.load(getClass().getResource("shshmanager.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Idevice.loadDB();

            if (PlatformUtil.isMac() || PlatformUtil.isWindows() || PlatformUtil.isLinux()) {
                launch(args);
            } else {
                int result = javax.swing.JOptionPane.showOptionDialog(null, "Cannot detect the OS. Assuming it is Linux. Continue?",
                        "Warning", javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE, null, null, null);
                if (result == javax.swing.JOptionPane.CANCEL_OPTION) {
                    System.exit(0);
                }
                launch(args);
            }

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
