package com.tickethub.viewController;

import com.tickethub.controller.GestionEventos;
import com.tickethub.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistroController {
    @FXML private TextField txtNombre, txtEmail, txtTelefono;
    @FXML private PasswordField txtPass;
    @FXML private Label lblMsg;

    @FXML
    private void handleRegistro(ActionEvent event) {
        String n = txtNombre.getText(), e = txtEmail.getText(), t = txtTelefono.getText(), p = txtPass.getText();
        if (n.isBlank() || e.isBlank() || p.isBlank()) { lblMsg.setText("Completa todos los campos obligatorios."); return; }
        if (!e.contains("@")) { lblMsg.setText("Email inválido. Debe contener @."); return; }
        if (GestionEventos.getInstance().listarUsuarios().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(e))) {
            lblMsg.setText("Ya existe un usuario con ese email."); return;
        }
        Usuario u = GestionEventos.getInstance().registrarUsuario(n, e, t, p);
        lblMsg.setStyle("-fx-text-fill: #3fb950;");
        lblMsg.setText("¡Registro exitoso! ID: " + u.getIdUsuario() + ". Vuelve al login.");
    }

    @FXML
    private void handleVolver(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/tickethub/views/Login.fxml"));
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(new Scene(root, window.getWidth(), window.getHeight()));
    }
}
