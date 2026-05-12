package com.logistica.viewController;

import com.logistica.controller.GestionEventos;
import com.logistica.model.*;
import com.logistica.model.adapter.*;
import com.logistica.model.decorator.*;
import com.logistica.model.factory.EntradaNumeradaFactory;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EventDetailController {
    @FXML private Label lblNombre, lblInfo, lblDescripcion, lblLugar, lblFecha, lblPoliticas, lblAforo, lblPrecio, lblMensaje;
    @FXML private Label lblAsientosSeleccionados, lblTarima;
    @FXML private ComboBox<Zona> cmbZonas;
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private CheckBox chkVIP, chkSeguro, chkMerch;
    @FXML private VBox seatMapContainer;

    private Evento evento;
    private List<Asiento> asientosSeleccionados = new ArrayList<>();
    private Map<Asiento, Button> botonesAsiento = new HashMap<>();

    @FXML
    public void initialize() {
        evento = UserDashboardController.eventoSeleccionado;
        if (evento == null) return;

        lblNombre.setText(evento.getNombre());
        lblInfo.setText(evento.getCategoria() + " • " + evento.getCiudad() + " • " + evento.getEstado());
        lblDescripcion.setText(evento.getDescripcion());
        lblLugar.setText("📍 " + evento.getRecintoAsociado().getNombre() + " - " + evento.getRecintoAsociado().getDireccion());
        lblFecha.setText("📅 " + evento.getFecha().format(DateTimeFormatter.ofPattern("EEEE dd MMM yyyy, HH:mm")));
        if (evento.getPoliticas() != null) {
            lblPoliticas.setText("📋 " + evento.getPoliticas().getPoliticaCancelacion() + " | " + evento.getPoliticas().getPoliticaReembolso());
        }

        // Aforo (RF-004)
        Map<Zona, Integer> disp = evento.consultarDisponibilidad();
        int totalDisp = disp.values().stream().mapToInt(Integer::intValue).sum();
        int totalCap = evento.getRecintoAsociado().getZonas().stream().mapToInt(Zona::getCapacidad).sum();
        lblAforo.setText("🏟️ Aforo: " + totalDisp + " disponibles de " + totalCap + " totales");

        cmbZonas.setItems(FXCollections.observableArrayList(evento.getRecintoAsociado().getZonas()));
        cmbZonas.setOnAction(e -> {
            asientosSeleccionados.clear();
            botonesAsiento.clear();
            actualizarLabelSeleccion();
            actualizarPrecio();
            cargarSeatMap();
        });
        cmbMetodoPago.setItems(FXCollections.observableArrayList("Tarjeta de Crédito", "PayPal"));

        chkVIP.setOnAction(e -> actualizarPrecio());
        chkSeguro.setOnAction(e -> actualizarPrecio());
        chkMerch.setOnAction(e -> actualizarPrecio());

        actualizarLabelSeleccion();
    }

    private void cargarSeatMap() {
        seatMapContainer.getChildren().clear();
        Zona z = cmbZonas.getValue();
        if (z == null) return;

        // Organizar asientos por fila
        Map<String, List<Asiento>> filas = new LinkedHashMap<>();
        for (Asiento a : z.getAsientos()) {
            filas.computeIfAbsent(a.getFila(), k -> new ArrayList<>()).add(a);
        }

        // Crear GridPane para el mapa tipo cine
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(6);
        grid.setVgap(6);
        grid.setStyle("-fx-padding: 10;");

        int row = 0;
        for (Map.Entry<String, List<Asiento>> entry : filas.entrySet()) {
            // Etiqueta de fila
            Label lblFila = new Label("Fila " + entry.getKey());
            lblFila.getStyleClass().add("seat-row-label");
            lblFila.setMinWidth(70);
            lblFila.setAlignment(Pos.CENTER_RIGHT);
            grid.add(lblFila, 0, row);

            int col = 1;
            // Sort seats by number
            List<Asiento> asientos = entry.getValue();
            asientos.sort(Comparator.comparingInt(a -> {
                try { return Integer.parseInt(a.getNumero()); } catch (Exception e) { return 0; }
            }));

            for (Asiento a : asientos) {
                Button btn = new Button(a.getFila() + a.getNumero());
                btn.getStyleClass().add("seat-btn-cinema");

                // Tooltip with details
                Tooltip tip = new Tooltip("Fila " + a.getFila() + " | Asiento " + a.getNumero()
                        + "\nZona: " + z.getNombre()
                        + "\nPrecio: $" + String.format("%,.0f", z.calcularPrecioFinal())
                        + "\nEstado: " + a.getEstado());
                btn.setTooltip(tip);

                switch (a.getEstado()) {
                    case DISPONIBLE -> btn.getStyleClass().add("seat-available");
                    case VENDIDO -> btn.getStyleClass().add("seat-sold");
                    case RESERVADO -> btn.getStyleClass().add("seat-reserved");
                    case BLOQUEADO -> btn.getStyleClass().add("seat-blocked");
                }

                if (a.getEstado() == EstadoAsiento.DISPONIBLE) {
                    botonesAsiento.put(a, btn);
                    btn.setOnAction(e -> toggleAsientoSeleccion(a, btn));
                }

                grid.add(btn, col, row);
                col++;
            }
            row++;
        }

        seatMapContainer.getChildren().add(grid);
    }

    private void toggleAsientoSeleccion(Asiento a, Button btn) {
        if (asientosSeleccionados.contains(a)) {
            // Deselect
            asientosSeleccionados.remove(a);
            btn.getStyleClass().removeAll("seat-selected");
            btn.getStyleClass().add("seat-available");
        } else {
            // Select
            asientosSeleccionados.add(a);
            btn.getStyleClass().removeAll("seat-available");
            btn.getStyleClass().add("seat-selected");
        }
        actualizarLabelSeleccion();
        actualizarPrecio();
    }

    private void actualizarLabelSeleccion() {
        int count = asientosSeleccionados.size();
        if (count == 0) {
            lblAsientosSeleccionados.setText("0 asientos — haz clic en el mapa para seleccionar");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(count).append(count == 1 ? " asiento: " : " asientos: ");
            for (int i = 0; i < Math.min(count, 5); i++) {
                Asiento a = asientosSeleccionados.get(i);
                if (i > 0) sb.append(", ");
                sb.append(a.getFila()).append(a.getNumero());
            }
            if (count > 5) sb.append("...");
            lblAsientosSeleccionados.setText(sb.toString());
        }
    }

    private void actualizarPrecio() {
        Zona z = cmbZonas.getValue();
        if (z == null) { lblPrecio.setText("Total: $0"); return; }
        int cantidad = Math.max(asientosSeleccionados.size(), 1);
        double precioUnitario = z.calcularPrecioFinal();
        double extras = 0;
        if (chkVIP.isSelected()) extras += 50000;
        if (chkSeguro.isSelected()) extras += 15000;
        if (chkMerch.isSelected()) extras += 25000;
        double total = (precioUnitario + extras) * cantidad;
        lblPrecio.setText("Total: $" + String.format("%,.0f", total) + (cantidad > 1 ? " (" + cantidad + "x)" : ""));
    }

    @FXML
    private void handleComprar(ActionEvent event) {
        Zona z = cmbZonas.getValue();
        String metodo = cmbMetodoPago.getValue();
        if (z == null || metodo == null) {
            lblMensaje.getStyleClass().setAll("label", "label-error");
            lblMensaje.setText("Selecciona zona y método de pago.");
            return;
        }

        // If no seats selected from map, show error
        if (asientosSeleccionados.isEmpty()) {
            lblMensaje.getStyleClass().setAll("label", "label-error");
            lblMensaje.setText("Selecciona al menos un asiento en el mapa.");
            return;
        }

        // Factory Method - create one Entrada per selected seat
        EntradaNumeradaFactory factory = new EntradaNumeradaFactory();
        List<Entrada> entradas = new ArrayList<>();
        for (Asiento a : asientosSeleccionados) {
            Entrada entrada = factory.crearEntrada(z, a);
            // Decorator for each entry
            if (chkVIP.isSelected()) entrada = new AccesoVipDecorator(entrada);
            if (chkSeguro.isSelected()) entrada = new SeguroCancelacionDecorator(entrada);
            if (chkMerch.isSelected()) entrada = new MerchandisingDecorator(entrada);
            entradas.add(entrada);
        }

        // Facade - single Compra with all entries
        Usuario user = LoginController.getUsuarioLogueado();
        Compra compra = GestionEventos.getInstance().crearCompra(user, evento, entradas);

        // Adapter
        IPagoAdapter pago = metodo.contains("PayPal") ? new PayPalAdapter() : new TarjetaCreditoAdapter();
        boolean exito = pago.procesarPago(compra.getTotal());

        if (exito) {
            compra.pagar(); // State: CREADA -> PAGADA
            compra.pagar(); // State: PAGADA -> CONFIRMADA
            // Mark all seats as sold
            for (Asiento a : asientosSeleccionados) {
                a.cambiarEstado(EstadoAsiento.VENDIDO);
            }

            // Generar recibo (RF-007)
            generarReciboTxt(compra);

            lblMensaje.getStyleClass().setAll("label", "label-success");
            lblMensaje.setText("✅ ¡Compra exitosa! " + asientosSeleccionados.size() + " entrada(s) • Recibo: Recibo_" + compra.getIdCompra() + ".txt");

            // Reset selection and reload map
            asientosSeleccionados.clear();
            botonesAsiento.clear();
            actualizarLabelSeleccion();
            cargarSeatMap();
            actualizarPrecio();

            // Update aforo
            Map<Zona, Integer> disp = evento.consultarDisponibilidad();
            int totalDisp = disp.values().stream().mapToInt(Integer::intValue).sum();
            int totalCap = evento.getRecintoAsociado().getZonas().stream().mapToInt(Zona::getCapacidad).sum();
            lblAforo.setText("🏟️ Aforo: " + totalDisp + " disponibles de " + totalCap + " totales");
        }
    }

    private void generarReciboTxt(Compra c) {
        String fileName = "Recibo_" + c.getIdCompra() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("============================================");
            pw.println("         BOOKIT - RECIBO DE COMPRA          ");
            pw.println("============================================");
            pw.println("ID COMPRA:  " + c.getIdCompra());
            pw.println("FECHA:      " + c.getFechaCreacion().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pw.println("--------------------------------------------");
            pw.println("CLIENTE:    " + c.getUsuarioAsociado().getNombreCompleto());
            pw.println("EVENTO:     " + c.getEventoAsociado().getNombre());
            pw.println("LUGAR:      " + c.getEventoAsociado().getRecintoAsociado().getNombre());
            pw.println("CIUDAD:     " + c.getEventoAsociado().getCiudad());
            pw.println("--------------------------------------------");
            pw.println("DETALLE DE ENTRADAS:");
            for (Entrada e : c.getItemsCompra()) {
                pw.println("  - " + e.toString());
            }
            pw.println("--------------------------------------------");
            pw.println("TOTAL:    $" + String.format("%,.0f", c.getTotal()));
            pw.println("ESTADO:   " + c.getEstadoActual().getNombreEstado());
            pw.println("============================================");
            pw.println("   ¡Gracias por elegir BookIt!              ");
            pw.println("============================================");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleVolver(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/logistica/views/UserDashboard.fxml"));
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(new Scene(root, 1024, 700));
    }
}
