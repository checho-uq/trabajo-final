package com.tickethub;

import com.tickethub.controller.GestionEventos;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Inicializar datos de prueba (RF-045)
        GestionEventos.getInstance().inicializarDatosPrueba();

        Parent root = FXMLLoader.load(getClass().getResource("/com/tickethub/views/Login.fxml"));
        Scene scene = new Scene(root, 1024, 700);
        scene.getStylesheets().add(getClass().getResource("/com/tickethub/css/styles.css").toExternalForm());

        primaryStage.setTitle("TicketHub - Plataforma de Gestión de Eventos");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
