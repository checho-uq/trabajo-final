package com.tickethub.viewController;

import com.tickethub.controller.GestionEventos;
import com.tickethub.model.*;
import com.tickethub.model.adapter.*;
import com.tickethub.model.decorator.*;
import com.tickethub.model.factory.EntradaNumeradaFactory;
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
import javafx.scene.layout.Region;
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
    @FXML private CheckBox chkVIP, chkSeguro, chkMerch, chkParqueadero, chkAccesoPref;
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
        chkParqueadero.setOnAction(e -> actualizarPrecio());
        chkAccesoPref.setOnAction(e -> actualizarPrecio());

        actualizarLabelSeleccion();
    }

    private String getFilaKey(String idAsiento) {
        if (idAsiento == null || idAsiento.length() < 3) return "?";
        String raw = idAsiento.substring(2);
        StringBuilder letras = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (Character.isLetter(c)) letras.append(c);
            else break;
        }
        return letras.length() > 0 ? letras.toString() : raw.substring(0, Math.min(2, raw.length()));
    }

    private void cargarSeatMap() {
        seatMapContainer.getChildren().clear();
        Recinto recinto = evento.getRecintoAsociado();
        if (recinto == null || recinto.getZonas().isEmpty()) return;

        List<Zona> zonas = recinto.getZonas();
        zonas.sort((a, b) -> Double.compare(b.getPrecioBase(), a.getPrecioBase()));

        Map<String, List<Asiento>> filas = new LinkedHashMap<>();
        Map<String, Zona> zonaPorFila = new LinkedHashMap<>();
        Map<String, String> colorPorZona = new LinkedHashMap<>();
        Map<String, String> labelZona = new LinkedHashMap<>();

        String[] colores = {"#f59e0b", "#3b82f6", "#10b981", "#8b5cf6", "#ec4899", "#14b8a6"};
        int ci = 0;
        for (Zona z : zonas) {
            String zonaColor = colores[ci % colores.length];
            colorPorZona.put(z.getNombre(), zonaColor);
            labelZona.put(z.getNombre(), z.getNombre());
            for (Asiento a : z.getAsientos()) {
                String filaKey = getFilaKey(a.getIdAsiento());
                if (filaKey.equals("?")) filaKey = a.getFila();
                filas.computeIfAbsent(filaKey, k -> new ArrayList<>()).add(a);
                zonaPorFila.put(filaKey, z);
            }
            ci++;
        }

        List<String> filasOrdenadas = new ArrayList<>(filas.keySet());
        filasOrdenadas.sort((f1, f2) -> {
            Zona z1 = zonaPorFila.get(f1);
            Zona z2 = zonaPorFila.get(f2);
            int cmp = Double.compare(z2 != null ? z2.getPrecioBase() : 0, z1 != null ? z1.getPrecioBase() : 0);
            if (cmp != 0) return cmp;
            try { return Integer.compare(Integer.parseInt(f1.replaceAll("\\D+","")), Integer.parseInt(f2.replaceAll("\\D+",""))); }
            catch (Exception e) { return f1.compareTo(f2); }
        });

        VBox mainContainer = new VBox(6);
        mainContainer.setAlignment(Pos.CENTER);

        // Zone color legend
        HBox zoneLegend = new HBox(16);
        zoneLegend.setAlignment(Pos.CENTER);
        zoneLegend.setStyle("-fx-padding: 4 0 10 0;");
        for (Zona z : zonas) {
            String color = colorPorZona.get(z.getNombre());
            HBox item = new HBox(6);
            item.setAlignment(Pos.CENTER);
            Region colorBox = new Region();
            colorBox.setMinSize(14, 14);
            colorBox.setMaxSize(14, 14);
            colorBox.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3; -fx-border-radius: 3;");
            Label lbl = new Label(z.getNombre() + " ($" + String.format("%,.0f", z.getPrecioBase()) + ")");
            lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: 600;");
            item.getChildren().addAll(colorBox, lbl);
            zoneLegend.getChildren().add(item);
        }
        mainContainer.getChildren().add(zoneLegend);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setStyle("-fx-padding: 8;");

        int maxSeats = filasOrdenadas.stream().mapToInt(fk -> filas.get(fk).size()).max().orElse(10);

        for (int rowIdx = 0; rowIdx < filasOrdenadas.size(); rowIdx++) {
            String filaKey = filasOrdenadas.get(rowIdx);
            List<Asiento> asientos = filas.get(filaKey);
            Zona zonaFila = zonaPorFila.get(filaKey);
            String zonaColor = zonaFila != null ? colorPorZona.get(zonaFila.getNombre()) : "#10b981";

            asientos.sort(Comparator.comparingInt(a -> {
                try { return Integer.parseInt(a.getNumero().replaceAll("\\D+","")); }
                catch (Exception e) { return 0; }
            }));

            Label lblFila = new Label(filaKey);
            lblFila.getStyleClass().add("seat-row-label");
            lblFila.setMinWidth(40);
            lblFila.setAlignment(Pos.CENTER_RIGHT);
            lblFila.setStyle("-fx-text-fill: " + zonaColor + "; -fx-font-weight: bold; -fx-font-size: 11px;");
            grid.add(lblFila, 0, rowIdx);

            int padding = (maxSeats - asientos.size()) / 2;
            for (int i = 0; i < padding; i++) {
                Label spacer = new Label("");
                spacer.setMinWidth(44);
                spacer.setMinHeight(36);
                grid.add(spacer, 1 + i, rowIdx);
            }

            for (int s = 0; s < asientos.size(); s++) {
                Asiento a = asientos.get(s);
                Button btn = new Button(filaKey + a.getNumero().replaceAll("\\D+",""));
                btn.getStyleClass().add("seat-btn-cinema");

                double precio = zonaFila != null ? zonaFila.calcularPrecioFinal() : 0;
                Tooltip tip = new Tooltip(
                    "Asiento " + filaKey + a.getNumero().replaceAll("\\D+","") +
                    "\nZona: " + (zonaFila != null ? zonaFila.getNombre() : "?") +
                    "\nPrecio: $" + String.format("%,.0f", precio) +
                    "\nEstado: " + a.getEstado()
                );
                btn.setTooltip(tip);

                switch (a.getEstado()) {
                    case DISPONIBLE -> btn.getStyleClass().add("seat-available");
                    case VENDIDO -> btn.getStyleClass().add("seat-sold");
                    case RESERVADO -> btn.getStyleClass().add("seat-reserved");
                    case BLOQUEADO -> btn.getStyleClass().add("seat-blocked");
                }

                if ("VIP".equalsIgnoreCase(zonaFila != null ? zonaFila.getNombre() : "")) {
                    btn.setStyle((btn.getStyle() != null ? btn.getStyle() : "") + "-fx-border-color: #f59e0b; -fx-border-width: 1.5;");
                }

                if (a.getEstado() == EstadoAsiento.DISPONIBLE) {
                    int seatIndex = s;
                    botonesAsiento.put(a, btn);
                    btn.setOnAction(e -> {
                        if (cmbZonas.getValue() == null || !cmbZonas.getValue().equals(zonaFila)) {
                            asientosSeleccionados.clear();
                            botonesAsiento.values().forEach(b -> {
                                b.getStyleClass().removeAll("seat-selected");
                                if (!b.getStyleClass().contains("seat-available"))
                                    b.getStyleClass().add("seat-available");
                            });
                            cmbZonas.setValue(zonaFila);
                        }
                        toggleAsientoSeleccion(a, btn);
                    });
                }

                grid.add(btn, 1 + padding + s, rowIdx);
            }
        }

        mainContainer.getChildren().add(grid);
        seatMapContainer.getChildren().add(mainContainer);
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

    private Zona findZonaForSeat(Asiento a) {
        Recinto recinto = evento.getRecintoAsociado();
        if (recinto == null) return null;
        for (Zona z : recinto.getZonas()) {
            if (z.getAsientos().contains(a)) return z;
        }
        return null;
    }

    private void actualizarPrecio() {
        if (asientosSeleccionados.isEmpty()) {
            lblPrecio.setText("Total: $0");
            return;
        }
        double totalBase = 0;
        for (Asiento a : asientosSeleccionados) {
            Zona z = findZonaForSeat(a);
            totalBase += z != null ? z.calcularPrecioFinal() : 0;
        }
        double extras = 0;
        if (chkVIP.isSelected()) extras += 50000;
        if (chkSeguro.isSelected()) extras += 15000;
        if (chkMerch.isSelected()) extras += 25000;
        if (chkParqueadero.isSelected()) extras += 20000;
        if (chkAccesoPref.isSelected()) extras += 35000;
        double total = totalBase + extras * asientosSeleccionados.size();
        int count = asientosSeleccionados.size();
        lblPrecio.setText("Total: $" + String.format("%,.0f", total) + (count > 1 ? " (" + count + " asientos)" : ""));
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
            if (chkParqueadero.isSelected()) entrada = new ParqueaderoDecorator(entrada);
            if (chkAccesoPref.isSelected()) entrada = new AccesoPreferencialDecorator(entrada);
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
            pw.println("         TicketHub - RECIBO DE COMPRA          ");
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
            pw.println("   ¡Gracias por elegir TicketHub!              ");
            pw.println("============================================");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleVolver(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/tickethub/views/UserDashboard.fxml"));
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(new Scene(root, window.getWidth(), window.getHeight()));
    }
}
