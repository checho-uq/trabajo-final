package com.logistica.viewController;

import com.logistica.controller.GestionEventos;
import com.logistica.model.*;
import com.logistica.model.proxy.ReporteConfig;
import com.logistica.model.proxy.ReporteProxy;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class AdminDashboardController {
    // Eventos tab
    @FXML private TableView<Evento> tblEventos;
    @FXML private TableColumn<Evento, String> colEvId, colEvNombre, colEvCategoria, colEvCiudad, colEvFecha, colEvEstado, colEvRecinto;
    @FXML private TextField txtEvNombre, txtEvCategoria, txtEvCiudad, txtEvDescripcion, txtEvPolCancel, txtEvPolReembolso;
    @FXML private DatePicker dpEvFecha;
    @FXML private ComboBox<Recinto> cmbEvRecinto;

    // Usuarios tab
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, String> colUsId, colUsNombre, colUsEmail, colUsTel, colUsRol, colUsCompras;
    @FXML private TextField txtUsNombre, txtUsEmail, txtUsTel;
    @FXML private PasswordField txtUsPassword;
    @FXML private CheckBox chkUsAdmin;

    // Compras tab
    @FXML private TableView<Compra> tblCompras;
    @FXML private TableColumn<Compra, String> colCmpId, colCmpCliente, colCmpEvento, colCmpTotal, colCmpEstado, colCmpFecha;

    // Recintos tab
    @FXML private TableView<Recinto> tblRecintos;
    @FXML private TableColumn<Recinto, String> colRecId, colRecNombre, colRecCiudad, colRecDir, colRecZonas;
    @FXML private TextField txtRecNombre, txtRecDir, txtRecCiudad;

    // Zonas tab
    @FXML private TableView<Zona> tblZonas;
    @FXML private TableColumn<Zona, String> colZonaId, colZonaNombre, colZonaCapacidad, colZonaPrecio, colZonaRecinto, colZonaAsientos;
    @FXML private ComboBox<Recinto> cmbZonaRecinto;
    @FXML private TextField txtZonaNombre, txtZonaCapacidad, txtZonaPrecio;

    // Asientos tab
    @FXML private TableView<Asiento> tblAsientos;
    @FXML private TableColumn<Asiento, String> colAsId, colAsFila, colAsNumero, colAsEstado;
    @FXML private ComboBox<Recinto> cmbAsientoRecinto;
    @FXML private ComboBox<Zona> cmbAsientoZona;

    // Métricas tab
    @FXML private BarChart<String, Number> chartVentas;
    @FXML private StackPane panelGraficas;
    @FXML private Label lblTotalCompras, lblTotalIngresos, lblTasaCancelacion, lblTotalEventos, lblTotalServicios;
    @FXML private ListView<String> lstIncidencias, lstTopEventos;
    @FXML private ComboBox<Evento> cmbMetricaEvento;
    @FXML private DatePicker dpIncidenciaInicio, dpIncidenciaFin;
    @FXML private TextField txtFiltroTipoIncidencia;

    private GestionEventos gestion = GestionEventos.getInstance();

    @FXML
    public void initialize() {
        configurarTablas();
        cargarDatos();
        configurarSelecciones();
    }

    private void configurarTablas() {
        // Eventos
        colEvId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdEvento()));
        colEvNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colEvCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria()));
        colEvCiudad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCiudad()));
        colEvFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        colEvEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().name()));
        colEvRecinto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRecintoAsociado().getNombre()));

        // Usuarios
        colUsId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdUsuario()));
        colUsNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colUsEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colUsTel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefono()));
        colUsRol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isEsAdmin() ? "Admin" : "Cliente"));
        colUsCompras.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getHistorialCompras().size())));

        // Compras
        colCmpId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdCompra()));
        colCmpCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuarioAsociado().getNombreCompleto()));
        colCmpEvento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEventoAsociado().getNombre()));
        colCmpTotal.setCellValueFactory(c -> new SimpleStringProperty("$" + String.format("%,.0f", c.getValue().getTotal())));
        colCmpEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstadoActual().getNombreEstado()));
        colCmpFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        // Recintos
        colRecId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdRecinto()));
        colRecNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colRecCiudad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCiudad()));
        colRecDir.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDireccion()));
        colRecZonas.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getZonas().size())));

        // Zonas
        colZonaId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdZona()));
        colZonaNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colZonaCapacidad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCapacidad())));
        colZonaPrecio.setCellValueFactory(c -> new SimpleStringProperty("$" + String.format("%,.0f", c.getValue().getPrecioBase())));
        colZonaRecinto.setCellValueFactory(c -> {
            // Find recinto for this zona
            for (Recinto r : gestion.listarRecintos()) {
                if (r.getZonas().contains(c.getValue())) return new SimpleStringProperty(r.getNombre());
            }
            return new SimpleStringProperty("-");
        });
        colZonaAsientos.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAsientos().size())));

        // Asientos
        colAsId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdAsiento()));
        colAsFila.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFila()));
        colAsNumero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumero()));
        colAsEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().name()));
    }

    private void configurarSelecciones() {
        // Al seleccionar un evento en la tabla, llenar el formulario
        tblEventos.getSelectionModel().selectedItemProperty().addListener((obs, old, ev) -> {
            if (ev != null) {
                txtEvNombre.setText(ev.getNombre());
                txtEvCategoria.setText(ev.getCategoria());
                txtEvCiudad.setText(ev.getCiudad());
                txtEvDescripcion.setText(ev.getDescripcion());
                dpEvFecha.setValue(ev.getFecha().toLocalDate());
                cmbEvRecinto.setValue(ev.getRecintoAsociado());
                if (ev.getPoliticas() != null) {
                    txtEvPolCancel.setText(ev.getPoliticas().getPoliticaCancelacion());
                    txtEvPolReembolso.setText(ev.getPoliticas().getPoliticaReembolso());
                }
            }
        });

        // Al seleccionar usuario en la tabla, llenar formulario
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, u) -> {
            if (u != null) {
                txtUsNombre.setText(u.getNombreCompleto());
                txtUsEmail.setText(u.getEmail());
                txtUsTel.setText(u.getTelefono());
                chkUsAdmin.setSelected(u.isEsAdmin());
            }
        });

        // Al seleccionar recinto en tabla, llenar formulario
        tblRecintos.getSelectionModel().selectedItemProperty().addListener((obs, old, r) -> {
            if (r != null) {
                txtRecNombre.setText(r.getNombre());
                txtRecDir.setText(r.getDireccion());
                txtRecCiudad.setText(r.getCiudad());
            }
        });

        // Al seleccionar zona en tabla, llenar formulario
        tblZonas.getSelectionModel().selectedItemProperty().addListener((obs, old, z) -> {
            if (z != null) {
                txtZonaNombre.setText(z.getNombre());
                txtZonaCapacidad.setText(String.valueOf(z.getCapacidad()));
                txtZonaPrecio.setText(String.valueOf(z.getPrecioBase()));
            }
        });
    }

    private void cargarDatos() {
        // Tablas principales
        tblEventos.setItems(FXCollections.observableArrayList(gestion.listarEventos()));
        tblEventos.refresh();
        tblUsuarios.setItems(FXCollections.observableArrayList(gestion.listarUsuarios()));
        tblCompras.setItems(FXCollections.observableArrayList(gestion.getCompras()));
        tblRecintos.setItems(FXCollections.observableArrayList(gestion.listarRecintos()));

        // Combo recintos
        cmbEvRecinto.setItems(FXCollections.observableArrayList(gestion.listarRecintos()));
        cmbZonaRecinto.setItems(FXCollections.observableArrayList(gestion.listarRecintos()));
        cmbAsientoRecinto.setItems(FXCollections.observableArrayList(gestion.listarRecintos()));

        // Tabla zonas (todas las zonas de todos los recintos)
        java.util.List<Zona> todasZonas = new java.util.ArrayList<>();
        for (Recinto r : gestion.listarRecintos()) {
            todasZonas.addAll(r.getZonas());
        }
        tblZonas.setItems(FXCollections.observableArrayList(todasZonas));

        // Incidencias
        lstIncidencias.setItems(FXCollections.observableArrayList(
                gestion.getIncidencias().stream().map(i -> "[" + i.getTipo() + "] " + i.getDescripcion()).toList()));

        // Métricas cards
        lblTotalCompras.setText(String.valueOf(gestion.getCompras().size()));
        lblTotalIngresos.setText("$" + String.format("%,.0f", gestion.obtenerTotalIngresos()));
        lblTasaCancelacion.setText(String.format("%.1f%%", gestion.obtenerTasaCancelacion()));
        lblTotalEventos.setText(String.valueOf(gestion.listarEventos().size()));

        // Total servicios adicionales
        double totalServicios = 0;
        for (Evento ev : gestion.listarEventos()) {
            Map<String, Double> servicios = gestion.obtenerIngresosServiciosAdicionales(ev);
            totalServicios += servicios.values().stream().mapToDouble(Double::doubleValue).sum();
        }
        lblTotalServicios.setText("$" + String.format("%,.0f", totalServicios));

        // ComboBox métricas
        cmbMetricaEvento.setItems(FXCollections.observableArrayList(gestion.listarEventos()));

        // Top eventos
        List<Evento> top = gestion.obtenerTopEventos();
        lstTopEventos.setItems(FXCollections.observableArrayList(
                top.stream().map(e -> {
                    long ventas = gestion.getCompras().stream()
                            .filter(c -> c.getEventoAsociado().getIdEvento().equals(e.getIdEvento())).count();
                    return "🏅 " + e.getNombre() + " (" + ventas + " compras)";
                }).toList()));

        // Gráfica por defecto: ventas por evento
        mostrarGraficaVentas(null);
    }

    // ============= ACCIONES EVENTOS =============
    @FXML private void crearEvento(ActionEvent event) {
        if (txtEvNombre.getText().isEmpty() || cmbEvRecinto.getValue() == null) return;
        String id = "E" + String.format("%03d", gestion.listarEventos().size() + 1);
        LocalDateTime fecha = dpEvFecha.getValue() != null ?
                dpEvFecha.getValue().atTime(LocalTime.of(20, 0)) : LocalDateTime.now().plusDays(30);
        PoliticasEvento pol = new PoliticasEvento(
                txtEvPolCancel.getText().isEmpty() ? "Cancelación estándar" : txtEvPolCancel.getText(),
                txtEvPolReembolso.getText().isEmpty() ? "Reembolso del 80%" : txtEvPolReembolso.getText());
        EventoBuilder builder = new EventoBuilder()
                .conId(id).conNombre(txtEvNombre.getText())
                .conCategoria(txtEvCategoria.getText().isEmpty() ? "General" : txtEvCategoria.getText())
                .conDescripcion(txtEvDescripcion.getText())
                .enCiudad(txtEvCiudad.getText().isEmpty() ? "Bogotá" : txtEvCiudad.getText())
                .enFecha(fecha).conRecinto(cmbEvRecinto.getValue()).conPoliticas(pol);
        gestion.crearEvento(builder);
        limpiarFormularioEvento();
        cargarDatos();
    }

    @FXML private void actualizarEvento(ActionEvent event) {
        Evento ev = tblEventos.getSelectionModel().getSelectedItem();
        if (ev == null) return;
        if (!txtEvNombre.getText().isEmpty()) ev.setNombre(txtEvNombre.getText());
        if (!txtEvCategoria.getText().isEmpty()) ev.setCategoria(txtEvCategoria.getText());
        if (!txtEvCiudad.getText().isEmpty()) ev.setCiudad(txtEvCiudad.getText());
        if (!txtEvDescripcion.getText().isEmpty()) ev.setDescripcion(txtEvDescripcion.getText());
        if (dpEvFecha.getValue() != null) ev.setFecha(dpEvFecha.getValue().atTime(LocalTime.of(20, 0)));
        if (cmbEvRecinto.getValue() != null) ev.setRecintoAsociado(cmbEvRecinto.getValue());
        gestion.actualizarEvento(ev);
        cargarDatos();
    }

    @FXML private void publicarEvento(ActionEvent event) {
        Evento ev = tblEventos.getSelectionModel().getSelectedItem();
        if (ev != null) { gestion.publicarEvento(ev); cargarDatos(); }
    }
    @FXML private void pausarEvento(ActionEvent event) {
        Evento ev = tblEventos.getSelectionModel().getSelectedItem();
        if (ev != null) { gestion.pausarEvento(ev); cargarDatos(); }
    }
    @FXML private void cancelarEvento(ActionEvent event) {
        Evento ev = tblEventos.getSelectionModel().getSelectedItem();
        if (ev != null) { gestion.cancelarEvento(ev); cargarDatos(); }
    }
    @FXML private void eliminarEvento(ActionEvent event) {
        Evento ev = tblEventos.getSelectionModel().getSelectedItem();
        if (ev != null) { gestion.eliminarEvento(ev.getIdEvento()); cargarDatos(); }
    }

    private void limpiarFormularioEvento() {
        txtEvNombre.clear(); txtEvCategoria.clear(); txtEvCiudad.clear();
        txtEvDescripcion.clear(); txtEvPolCancel.clear(); txtEvPolReembolso.clear();
        dpEvFecha.setValue(null); cmbEvRecinto.setValue(null);
    }

    // ============= ACCIONES USUARIOS =============
    @FXML private void crearUsuario(ActionEvent event) {
        if (txtUsNombre.getText().isEmpty() || txtUsEmail.getText().isEmpty()) return;
        Usuario u = gestion.registrarUsuario(txtUsNombre.getText(), txtUsEmail.getText(),
                txtUsTel.getText(), txtUsPassword.getText().isEmpty() ? "1234" : txtUsPassword.getText());
        u.setEsAdmin(chkUsAdmin.isSelected());
        limpiarFormularioUsuario();
        cargarDatos();
    }

    @FXML private void actualizarUsuario(ActionEvent event) {
        Usuario u = tblUsuarios.getSelectionModel().getSelectedItem();
        if (u == null) return;
        if (!txtUsNombre.getText().isEmpty()) u.setNombreCompleto(txtUsNombre.getText());
        if (!txtUsEmail.getText().isEmpty()) u.setEmail(txtUsEmail.getText());
        if (!txtUsTel.getText().isEmpty()) u.setTelefono(txtUsTel.getText());
        u.setEsAdmin(chkUsAdmin.isSelected());
        gestion.actualizarUsuario(u);
        cargarDatos();
    }

    @FXML private void eliminarUsuario(ActionEvent event) {
        Usuario u = tblUsuarios.getSelectionModel().getSelectedItem();
        if (u != null) { gestion.eliminarUsuario(u.getIdUsuario()); cargarDatos(); }
    }

    private void limpiarFormularioUsuario() {
        txtUsNombre.clear(); txtUsEmail.clear(); txtUsTel.clear();
        txtUsPassword.clear(); chkUsAdmin.setSelected(false);
    }

    // ============= ACCIONES COMPRAS =============
    @FXML private void cancelarCompraAdmin(ActionEvent event) {
        Compra c = tblCompras.getSelectionModel().getSelectedItem();
        if (c != null) { gestion.cancelarCompra(c); cargarDatos(); }
    }
    @FXML private void registrarReembolso(ActionEvent event) {
        Compra c = tblCompras.getSelectionModel().getSelectedItem();
        if (c != null) { gestion.registrarReembolso(c); cargarDatos(); }
    }

    // ============= ACCIONES RECINTOS =============
    @FXML private void crearRecintoAction(ActionEvent event) {
        if (txtRecNombre.getText().isEmpty()) return;
        String id = "R" + String.format("%03d", gestion.listarRecintos().size() + 1);
        Recinto r = new Recinto(id, txtRecNombre.getText(),
                txtRecDir.getText().isEmpty() ? "Sin dirección" : txtRecDir.getText(),
                txtRecCiudad.getText().isEmpty() ? "Bogotá" : txtRecCiudad.getText());
        gestion.crearRecinto(r);
        limpiarFormularioRecinto();
        cargarDatos();
    }

    @FXML private void actualizarRecintoAction(ActionEvent event) {
        Recinto r = tblRecintos.getSelectionModel().getSelectedItem();
        if (r == null) return;
        if (!txtRecNombre.getText().isEmpty()) r.setNombre(txtRecNombre.getText());
        if (!txtRecDir.getText().isEmpty()) r.setDireccion(txtRecDir.getText());
        if (!txtRecCiudad.getText().isEmpty()) r.setCiudad(txtRecCiudad.getText());
        gestion.actualizarRecinto(r);
        cargarDatos();
    }

    @FXML private void eliminarRecintoAction(ActionEvent event) {
        Recinto r = tblRecintos.getSelectionModel().getSelectedItem();
        if (r != null) { gestion.eliminarRecinto(r.getIdRecinto()); cargarDatos(); }
    }

    private void limpiarFormularioRecinto() {
        txtRecNombre.clear(); txtRecDir.clear(); txtRecCiudad.clear();
    }

    // ============= ACCIONES ZONAS =============
    @FXML private void crearZonaAction(ActionEvent event) {
        Recinto r = cmbZonaRecinto.getValue();
        if (r == null || txtZonaNombre.getText().isEmpty()) return;
        String id = "Z" + String.format("%03d", r.getZonas().size() + 100);
        int cap = 100;
        double precio = 50000;
        try { cap = Integer.parseInt(txtZonaCapacidad.getText()); } catch (Exception ignored) {}
        try { precio = Double.parseDouble(txtZonaPrecio.getText()); } catch (Exception ignored) {}
        Zona z = new Zona(id, txtZonaNombre.getText(), cap, precio);
        // Create seats for the zone
        for (int i = 1; i <= Math.min(cap, 20); i++) {
            z.addAsiento(new Asiento("A-" + id + "-" + i, txtZonaNombre.getText().substring(0, Math.min(2, txtZonaNombre.getText().length())).toUpperCase(), String.valueOf(i)));
        }
        gestion.crearZona(r, z);
        limpiarFormularioZona();
        cargarDatos();
    }

    @FXML private void actualizarZonaAction(ActionEvent event) {
        Zona z = tblZonas.getSelectionModel().getSelectedItem();
        if (z == null) return;
        if (!txtZonaNombre.getText().isEmpty()) z.setNombre(txtZonaNombre.getText());
        try { z.definirCapacidad(Integer.parseInt(txtZonaCapacidad.getText())); } catch (Exception ignored) {}
        try { z.definirPrecioBase(Double.parseDouble(txtZonaPrecio.getText())); } catch (Exception ignored) {}
        gestion.actualizarZona(z);
        cargarDatos();
    }

    @FXML private void eliminarZonaAction(ActionEvent event) {
        Zona z = tblZonas.getSelectionModel().getSelectedItem();
        if (z != null) { gestion.eliminarZona(z.getIdZona()); cargarDatos(); }
    }

    private void limpiarFormularioZona() {
        txtZonaNombre.clear(); txtZonaCapacidad.clear(); txtZonaPrecio.clear();
    }

    // ============= ACCIONES ASIENTOS =============
    @FXML private void cargarZonasAsiento(ActionEvent event) {
        Recinto r = cmbAsientoRecinto.getValue();
        if (r != null) {
            cmbAsientoZona.setItems(FXCollections.observableArrayList(r.getZonas()));
        }
    }

    @FXML private void cargarAsientosAdmin(ActionEvent event) {
        Zona z = cmbAsientoZona.getValue();
        if (z != null) {
            tblAsientos.setItems(FXCollections.observableArrayList(z.getAsientos()));
        }
    }

    @FXML private void habilitarAsiento(ActionEvent event) {
        Asiento a = tblAsientos.getSelectionModel().getSelectedItem();
        if (a != null) { a.cambiarEstado(EstadoAsiento.DISPONIBLE); cargarAsientosAdmin(null); tblAsientos.refresh(); }
    }

    @FXML private void bloquearAsiento(ActionEvent event) {
        Asiento a = tblAsientos.getSelectionModel().getSelectedItem();
        if (a != null) { a.cambiarEstado(EstadoAsiento.BLOQUEADO); cargarAsientosAdmin(null); tblAsientos.refresh(); }
    }

    @FXML private void liberarAsiento(ActionEvent event) {
        Asiento a = tblAsientos.getSelectionModel().getSelectedItem();
        if (a != null) { a.cambiarEstado(EstadoAsiento.DISPONIBLE); cargarAsientosAdmin(null); tblAsientos.refresh(); }
    }

    // ============= MÉTRICAS AVANZADAS =============
    @FXML private void cambiarEventoMetrica(ActionEvent event) {
        // Refresh gráficas cuando se selecciona un evento
        mostrarGraficaOcupacion(null);
    }

    @FXML private void mostrarGraficaVentas(ActionEvent event) {
        panelGraficas.getChildren().clear();
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle("Ingresos por Evento");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ingresos ($)");
        for (Evento ev : gestion.listarEventos()) {
            double ventas = gestion.getCompras().stream()
                    .filter(c -> c.getEventoAsociado().getIdEvento().equals(ev.getIdEvento()))
                    .mapToDouble(Compra::getTotal).sum();
            if (ventas > 0) {
                String label = ev.getNombre().length() > 18 ? ev.getNombre().substring(0, 18) + "…" : ev.getNombre();
                series.getData().add(new XYChart.Data<>(label, ventas));
            }
        }
        if (!series.getData().isEmpty()) chart.getData().add(series);
        chart.setAnimated(true);
        panelGraficas.getChildren().add(chart);
    }

    @FXML private void mostrarGraficaOcupacion(ActionEvent event) {
        panelGraficas.getChildren().clear();
        Evento ev = cmbMetricaEvento.getValue();
        if (ev == null) {
            List<Evento> publicados = gestion.explorarEventos(new Filtros());
            if (!publicados.isEmpty()) ev = publicados.get(0);
            else return;
        }
        PieChart pie = new PieChart();
        pie.setTitle("Ocupación por Zona: " + ev.getNombre());
        Map<String, Double> ocup = gestion.obtenerOcupacionPorZona(ev);
        for (Map.Entry<String, Double> e : ocup.entrySet()) {
            pie.getData().add(new PieChart.Data(e.getKey() + " (" + String.format("%.0f", e.getValue()) + "%)", Math.max(e.getValue(), 1)));
        }
        pie.setAnimated(true);
        panelGraficas.getChildren().add(pie);
    }

    @FXML private void mostrarGraficaServicios(ActionEvent event) {
        panelGraficas.getChildren().clear();
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle("Ingresos por Servicios Adicionales");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ingresos ($)");
        double totalVip = 0, totalSeguro = 0, totalMerch = 0;
        for (Evento ev : gestion.listarEventos()) {
            Map<String, Double> servicios = gestion.obtenerIngresosServiciosAdicionales(ev);
            totalVip += servicios.getOrDefault("Acceso VIP", 0.0);
            totalSeguro += servicios.getOrDefault("Seguro Cancelación", 0.0);
            totalMerch += servicios.getOrDefault("Merchandising", 0.0);
        }
        series.getData().add(new XYChart.Data<>("Acceso VIP", totalVip));
        series.getData().add(new XYChart.Data<>("Seguro Cancelación", totalSeguro));
        series.getData().add(new XYChart.Data<>("Merchandising", totalMerch));
        chart.getData().add(series);
        chart.setAnimated(true);
        panelGraficas.getChildren().add(chart);
    }

    @FXML private void mostrarGraficaTop(ActionEvent event) {
        panelGraficas.getChildren().clear();
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle("Top 5 Eventos Más Vendidos");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Compras");
        for (Evento ev : gestion.obtenerTopEventos()) {
            long count = gestion.getCompras().stream()
                    .filter(c -> c.getEventoAsociado().getIdEvento().equals(ev.getIdEvento())).count();
            String label = ev.getNombre().length() > 18 ? ev.getNombre().substring(0, 18) + "…" : ev.getNombre();
            series.getData().add(new XYChart.Data<>(label, count));
        }
        if (!series.getData().isEmpty()) chart.getData().add(series);
        chart.setAnimated(true);
        panelGraficas.getChildren().add(chart);
    }

    @FXML private void mostrarGraficaCancelacion(ActionEvent event) {
        panelGraficas.getChildren().clear();
        PieChart pie = new PieChart();
        pie.setTitle("Tasa de Cancelación");
        long canceladas = gestion.getCompras().stream()
                .filter(c -> c.getEstadoActual().getNombreEstado().equals("CANCELADA") ||
                        c.getEstadoActual().getNombreEstado().equals("REEMBOLSADA")).count();
        long activas = gestion.getCompras().size() - canceladas;
        pie.getData().add(new PieChart.Data("Activas (" + activas + ")", Math.max(activas, 1)));
        pie.getData().add(new PieChart.Data("Canceladas (" + canceladas + ")", Math.max(canceladas, 1)));
        pie.setAnimated(true);
        panelGraficas.getChildren().add(pie);
    }

    @FXML private void mostrarGraficaLinea(ActionEvent event) {
        panelGraficas.getChildren().clear();
        LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle("Tendencia de Ventas por Evento");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ingresos ($)");
        for (Evento ev : gestion.listarEventos()) {
            double ventas = gestion.getCompras().stream()
                    .filter(c -> c.getEventoAsociado().getIdEvento().equals(ev.getIdEvento()))
                    .mapToDouble(Compra::getTotal).sum();
            String label = ev.getNombre().length() > 15 ? ev.getNombre().substring(0, 15) + "…" : ev.getNombre();
            series.getData().add(new XYChart.Data<>(label, ventas));
        }
        if (!series.getData().isEmpty()) chart.getData().add(series);
        chart.setAnimated(true);
        panelGraficas.getChildren().add(chart);
    }

    @FXML private void filtrarIncidencias(ActionEvent event) {
        Filtros f = new Filtros();
        if (dpIncidenciaInicio.getValue() != null) f.setFechaInicio(dpIncidenciaInicio.getValue());
        if (dpIncidenciaFin.getValue() != null) f.setFechaFin(dpIncidenciaFin.getValue());
        List<Incidencia> incidenciasFiltradas = gestion.consultarIncidencias(f);
        if (txtFiltroTipoIncidencia.getText() != null && !txtFiltroTipoIncidencia.getText().isBlank()) {
            String tipoFiltro = txtFiltroTipoIncidencia.getText().toLowerCase();
            incidenciasFiltradas = incidenciasFiltradas.stream()
                    .filter(i -> i.getTipo().toLowerCase().contains(tipoFiltro))
                    .toList();
        }
        lstIncidencias.setItems(FXCollections.observableArrayList(
                incidenciasFiltradas.stream()
                        .map(i -> "[" + i.getTipo() + "] " + i.getDescripcion() + " (" + i.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yy")) + ")")
                        .toList()));
    }

    @FXML private void limpiarFiltroIncidencias(ActionEvent event) {
        dpIncidenciaInicio.setValue(null);
        dpIncidenciaFin.setValue(null);
        txtFiltroTipoIncidencia.clear();
        lstIncidencias.setItems(FXCollections.observableArrayList(
                gestion.getIncidencias().stream()
                        .map(i -> "[" + i.getTipo() + "] " + i.getDescripcion())
                        .toList()));
    }

    // ============= REPORTES =============
    @FXML private void generarReporteEjecutivo(ActionEvent event) {
        Usuario user = LoginController.getUsuarioLogueado();
        if (user == null || !user.isEsAdmin()) {
            mostrarErrorLogica("Acceso denegado: solo administradores pueden generar este reporte.");
            return;
        }
        ReporteProxy proxy = new ReporteProxy(user);
        String fileName = "Reporte_Ejecutivo_" + System.currentTimeMillis() + ".pdf";
        proxy.generarReporteInteligente(ReporteConfig.ejecutivo(), fileName);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reporte Generado");
        alert.setHeaderText("Reporte Ejecutivo Exitoso");
        alert.setContentText("El reporte se ha generado correctamente en la raíz del proyecto:\n" + fileName);
        alert.showAndWait();
    }

    @FXML private void generarPDF(ActionEvent event) {
        Usuario user = LoginController.getUsuarioLogueado();
        if (user == null || !user.isEsAdmin()) {
            mostrarErrorLogica("Acceso denegado: solo administradores pueden generar reportes.");
            return;
        }
        new ReporteProxy(user).generarReportePDF(new Filtros(), "Reporte_BookIt.pdf");
        mostrarInfoLogica("Reporte básico PDF generado exitosamente.");
    }
    @FXML private void generarCSV(ActionEvent event) {
        Usuario user = LoginController.getUsuarioLogueado();
        if (user == null || !user.isEsAdmin()) {
            mostrarErrorLogica("Acceso denegado: solo administradores pueden generar reportes.");
            return;
        }
        new ReporteProxy(user).generarReporteCSV(new Filtros(), "Reporte_BookIt.csv");
        mostrarInfoLogica("Reporte CSV generado exitosamente.");
    }

    private void mostrarErrorLogica(String m) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error de Permisos");
        alert.setContentText(m);
        alert.showAndWait();
    }
    
    private void mostrarInfoLogica(String m) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Operación Exitosa");
        alert.setContentText(m);
        alert.showAndWait();
    }

    @FXML private void logout(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/logistica/views/Login.fxml"));
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(new Scene(root, 1024, 700));
    }
}
