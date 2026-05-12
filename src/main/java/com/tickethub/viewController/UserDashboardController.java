package com.tickethub.viewController;

import com.tickethub.controller.GestionEventos;
import com.tickethub.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserDashboardController {
    @FXML private FlowPane flowEventos;
    @FXML private TextField txtFiltroCiudad, txtFiltroCategoria, txtFiltroPrecio;
    @FXML private DatePicker dpFiltroFechaInicio, dpFiltroFechaFin;

    private GestionEventos gestion = GestionEventos.getInstance();
    public static Evento eventoSeleccionado;

    @FXML
    public void initialize() {
        mostrarNotificacionesPendientes();
        filtrarEventos(null);
    }

    private void mostrarNotificacionesPendientes() {
        Usuario user = LoginController.getUsuarioLogueado();
        if (user == null) return;
        List<String> notis = user.getNotificacionesPendientes();
        if (!notis.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notificaciones");
            alert.setHeaderText("📬 Tienes " + notis.size() + " notificación(es) nueva(s)");
            alert.setContentText(String.join("\n", notis));
            alert.showAndWait();
            user.limpiarNotificaciones();
        }
    }

    @FXML
    private void filtrarEventos(ActionEvent event) {
        Filtros f = new Filtros();
        if (txtFiltroCiudad != null && !txtFiltroCiudad.getText().isBlank()) f.setCiudad(txtFiltroCiudad.getText());
        if (txtFiltroCategoria != null && !txtFiltroCategoria.getText().isBlank()) f.setCategoria(txtFiltroCategoria.getText());
        if (txtFiltroPrecio != null && !txtFiltroPrecio.getText().isBlank()) {
            try { f.setPrecioMax(Double.parseDouble(txtFiltroPrecio.getText())); } catch (Exception ignored) {}
        }
        if (dpFiltroFechaInicio != null && dpFiltroFechaInicio.getValue() != null) f.setFechaInicio(dpFiltroFechaInicio.getValue());
        if (dpFiltroFechaFin != null && dpFiltroFechaFin.getValue() != null) f.setFechaFin(dpFiltroFechaFin.getValue());
        List<Evento> eventos = gestion.explorarEventos(f);
        flowEventos.getChildren().clear();
        for (Evento ev : eventos) {
            flowEventos.getChildren().add(crearCardEvento(ev));
        }
    }

    @FXML
    private void limpiarFiltros(ActionEvent event) {
        txtFiltroCiudad.clear();
        txtFiltroCategoria.clear();
        txtFiltroPrecio.clear();
        if (dpFiltroFechaInicio != null) dpFiltroFechaInicio.setValue(null);
        if (dpFiltroFechaFin != null) dpFiltroFechaFin.setValue(null);
        filtrarEventos(null);
    }

    private VBox crearCardEvento(Evento ev) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPrefWidth(260);
        card.setPadding(new Insets(16));

        String cat = ev.getCategoria();
        String emoji = cat.equals("Música") ? "🎵" : cat.equals("Teatro") ? "🎭" : cat.equals("Conferencia") ? "💻" : cat.equals("Comedia") ? "😂" : "🎪";

        Label lblEmoji = new Label(emoji);
        lblEmoji.setStyle("-fx-font-size: 36;");

        Label lblNombre = new Label(ev.getNombre());
        lblNombre.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: white; -fx-wrap-text: true;");
        lblNombre.setWrapText(true);

        Label lblCat = new Label(ev.getCategoria() + " • " + ev.getCiudad());
        lblCat.setStyle("-fx-text-fill: #58a6ff; -fx-font-size: 12;");

        Label lblFecha = new Label("📅 " + ev.getFecha().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        lblFecha.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12;");

        Label lblRecinto = new Label("📍 " + ev.getRecintoAsociado().getNombre());
        lblRecinto.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11;");

        double minPrice = ev.getRecintoAsociado().getZonas().stream().mapToDouble(Zona::getPrecioBase).min().orElse(0);
        Label lblPrecio = new Label("Desde $" + String.format("%.0f", minPrice));
        lblPrecio.setStyle("-fx-text-fill: #3fb950; -fx-font-weight: bold; -fx-font-size: 14;");

        Button btnVer = new Button("Ver Detalles →");
        btnVer.getStyleClass().add("btn-primary");
        btnVer.setMaxWidth(Double.MAX_VALUE);
        btnVer.setOnAction(e -> {
            eventoSeleccionado = ev;
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/com/tickethub/views/EventDetail.fxml"));
                Stage window = (Stage) btnVer.getScene().getWindow();
                window.setScene(new Scene(root, window.getWidth(), window.getHeight()));
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        card.getChildren().addAll(lblEmoji, lblNombre, lblCat, lblFecha, lblRecinto, lblPrecio, btnVer);
        return card;
    }

    @FXML private void verPerfil(ActionEvent event) { navegar(event, "/com/tickethub/views/Perfil.fxml"); }
    @FXML private void verCompras(ActionEvent event) { navegar(event, "/com/tickethub/views/MisCompras.fxml"); }
    @FXML private void logout(ActionEvent event) { navegar(event, "/com/tickethub/views/Login.fxml"); }

    private void navegar(ActionEvent event, String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(new Scene(root, window.getWidth(), window.getHeight()));
        } catch (Exception e) { e.printStackTrace(); }
    }
}
