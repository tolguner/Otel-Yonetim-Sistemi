package com.example.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class baseController {
    public void ilgiliSayfayaGit(String fxmlYolu, Button button) {
        try {
            System.out.println("Yüklenen FXML: " + fxmlYolu);
            System.out.println("Resource URL: " + getClass().getResource(fxmlYolu));
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlYolu));
            Scene scene = button.getScene();
            scene.setRoot(fxmlLoader.load());
        } catch (Exception e) {
            System.err.println("Sayfa yükleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void logAction(String message) {
        System.out.println("[LOG] " + message);
    }

    protected void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bilgilendirme");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void bekleVeYonlendir(String fxmlYolu, Button button, int saniye) {
        PauseTransition pause = new PauseTransition(Duration.seconds(saniye));
        pause.setOnFinished(_ -> ilgiliSayfayaGit(fxmlYolu, button));
        pause.play();
    }
}