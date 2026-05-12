package com.tickethub.viewController;

import com.tickethub.controller.GestionEventos;
import com.tickethub.model.*;
import com.tickethub.model.proxy.ReporteConfig;
import com.tickethub.model.proxy.ReporteProxy;
import com.tickethub.model.strategy.TarifaEstandar;
import com.tickethub.model.strategy.TarifaPreventa;
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
import javafx.scene.layout.*;
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
    @FXML private TextField txtEvNombre, txtEvCiudad, txtEvDescripcion, txtEvPolCancel, txtEvPolReembolso;
    @FXML private ComboBox<String> cmbEvCategoria;
    @FXML private DatePicker dpEvFecha;
    @FXML private ComboBox<Recinto> cmbEvRecinto;
    @FXML private Label lblTotalEventosCount, lblPublicadosCount, lblBorradoresCount, lblCanceladosCount;

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
    @FXML private ComboBox<String> cmbZonaNombre;
    @FXML private TextField txtZonaCapacidad, txtZonaAsientos, txtZonaPrecio;
    @FXML private ComboBox<String> cmbZonaEstrategia;

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
        colEvId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdEvento()));
        colEvNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colEvCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria()));
        colEvCiudad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCiudad()));
        colEvFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        colEvEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().name()));
        colEvRecinto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRecintoAsociado().getNombre()));

        colUsId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdUsuario()));
        colUsNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colUsEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colUsTel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefono()));
        colUsRol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isEsAdmin() ? "Admin" : "Cliente"));
        colUsCompras.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getHistorialCompras().size())));

        colCmpId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdCompra()));
        colCmpCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuarioAsociado().getNombreCompleto()));
        colCmpEvento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEventoAsociado().getNombre()));
        colCmpTotal.setCellValueFactory(c -> new SimpleStringProperty("$" + String.format("%,.0f", c.getValue().getTotal())));
        colCmpEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstadoActual().getNombreEstado()));
        colCmpFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        colRecId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdRecinto()));
        colRecNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colRecCiudad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCiudad()));
        colRecDir.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDireccion()));
        colRecZonas.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getZonas().size())));

        colZonaId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdZona()));
        colZonaNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colZonaCapacidad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCapacidad())));
        colZonaPrecio.setCellValueFactory(c -> new SimpleStringProperty("$" + String.format("%,.0f", c.getValue().getPrecioBase())));
        colZonaRecinto.setCellValueFactory(c -> {
            for (Recinto r : gestion.listarRecintos()) {
                if (r.getZonas().contains(c.getValue())) return new SimpleStringProperty(r.getNombre());
            }
            return new SimpleStringProperty("-");
        });
        colZonaAsientos.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAsientos().size())));

        colAsId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdAsiento()));
        colAsFila.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFila()));
        colAsNumero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumero()));
        colAsEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().name()));
    }

    // ============= NAVEGACIÓN =============
    @FXML private void abrirGestionRecintos(ActionEvent event) {
        if (cmbEvRecinto.getScene() != null) {
            TabPane tabPane = (TabPane) ((BorderPane) cmbEvRecinto.getScene().getRoot()).getCenter();
            if (tabPane != null && tabPane.getTabs().size() > 3) {
                tabPane.getSelectionModel().select(3);
            }
        }
    }

    private void configurarSelecciones() {
        tblEventos.getSelectionModel().selectedItemProperty().addListener((obs, old, ev) -> {
            if (ev != null) {
                txtEvNombre.setText(ev.getNombre());
                cmbEvCategoria.setValue(ev.getCategoria());
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

        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, u) -> {
            if (u != null) {
                txtUsNombre.setText(u.getNombreCompleto());
                txtUsEmail.setText(u.getEmail());
                txtUsTel.setText(u.getTelefono());
                chkUsAdmin.setSelected(u.isEsAdmin());
            }
        });

        tblRecintos.getSelectionModel().selectedItemProperty().addListener((obs, old, r) -> {
            if (r != null) {
                txtRecNombre.setText(r.getNombre());
                txtRecDir.setText(r.getDireccion());
                txtRecCiudad.setText(r.getCiudad());
            }
        });

        tblZonas.getSelectionModel().selectedItemProperty().addListener((obs, old, z) -> {
            if (z != null) {
                cmbZonaNombre.setValue(z.getNombre());
                txtZonaCapacidad.setText(String.valueOf(z.getCapacidad()));
                txtZonaAsientos.setText(String.valueOf(z.getAsientos().size()));
                txtZonaPrecio.setText(String.valueOf(z.getPrecioBase()));
                // Set strategy combo
                if (z.getEstrategiaTarifa() instanceof TarifaPreventa) {
                    cmbZonaEstrategia.setValue("Preventa (20% descuento)");
                } else {
                    cmbZonaEstrategia.setValue("Estándar");
                }
                // Find and select the recinto for this zona
                for (Recinto r : gestion.listarRecintos()) {
                    if (r.getZonas().contains(z)) {
                        cmbZonaRecinto.setValue(r);
                        break;
                    }
                }
            }
        });
    }

    private void cargarDatos() {
        List<Evento> eventos = gestion.listarEventos();
        List<Usuario> usuarios = gestion.listarUsuarios();
        List<Compra> compras = gestion.getCompras();
        List<Recinto> recintos = gestion.listarRecintos();

        tblEventos.setItems(FXCollections.observableArrayList(eventos));
        tblUsuarios.setItems(FXCollections.observableArrayList(usuarios));
        tblCompras.setItems(FXCollections.observableArrayList(compras));
        tblRecintos.setItems(FXCollections.observableArrayList(recintos));

        // Summary cards for Events tab
        long publicados = eventos.stream().filter(e -> e.getEstado() == EstadoEvento.PUBLICADO).count();
        long borradores = eventos.stream().filter(e -> e.getEstado() == EstadoEvento.BORRADOR).count();
        long cancelados = eventos.stream().filter(e -> e.getEstado() == EstadoEvento.CANCELADO).count();
        lblTotalEventosCount.setText(String.valueOf(eventos.size()));
        lblPublicadosCount.setText(String.valueOf(publicados));
        lblBorradoresCount.setText(String.valueOf(borradores));
        lblCanceladosCount.setText(String.valueOf(cancelados));

        // Combos
        cmbEvCategoria.setItems(FXCollections.observableArrayList(
                "Música", "Teatro", "Comedia", "Conferencia", "Deportes", "Festival", "Exposición", "Infantil"));
        cmbZonaNombre.setItems(FXCollections.observableArrayList(
                "VIP", "Preferencial", "General", "Palco", "Platea", "Balcón", "Sol", "Sombra", "Piso", "Gradería"));
        cmbZonaEstrategia.setItems(FXCollections.observableArrayList("Estándar", "Preventa (20% descuento)"));
        cmbEvRecinto.setItems(FXCollections.observableArrayList(recintos));
        cmbZonaRecinto.setItems(FXCollections.observableArrayList(recintos));
        cmbAsientoRecinto.setItems(FXCollections.observableArrayList(recintos));

        // All zonas
        java.util.List<Zona> todasZonas = new java.util.ArrayList<>();
        for (Recinto r : recintos) { todasZonas.addAll(r.getZonas()); }
        tblZonas.setItems(FXCollections.observableArrayList(todasZonas));

        // Incidencias
        lstIncidencias.setItems(FXCollections.observableArrayList(
                gestion.getIncidencias().stream().map(i -> "[" + i.getTipo() + "] " + i.getDescripcion()).toList()));

        // Métricas cards
        lblTotalCompras.setText(String.valueOf(compras.size()));
        lblTotalIngresos.setText("$" + String.format("%,.0f", gestion.obtenerTotalIngresos()));
        lblTasaCancelacion.setText(String.format("%.1f%%", gestion.obtenerTasaCancelacion()));
        lblTotalEventos.setText(String.valueOf(eventos.size()));

        double totalServicios = 0;
        for (Evento ev : eventos) {
            Map<String, Double> servicios = gestion.obtenerIngresosServiciosAdicionales(ev);
            totalServicios += servicios.values().stream().mapToDouble(Double::doubleValue).sum();
        }
        lblTotalServicios.setText("$" + String.format("%,.0f", totalServicios));

        cmbMetricaEvento.setItems(FXCollections.observableArrayList(eventos));

        List<Evento> top = gestion.obtenerTopEventos();
        lstTopEventos.setItems(FXCollections.observableArrayList(
                top.stream().map(e -> {
                    long ventas = compras.stream().filter(c -> c.getEventoAsociado().getIdEvento().equals(e.getIdEvento())).count();
                    return "🏅 " + e.getNombre() + " (" + ventas + " compras)";
                }).toList()));

        mostrarGraficaVentas(null);
    }

    // ============= ACCIONES EVENTOS =============
    @FXML private void crearEvento(ActionEvent event) {
        if (txtEvNombre.getText().isEmpty() || cmbEvRecinto.getValue() == null) {
            mostrarErrorLogica("Debes llenar el nombre del evento y seleccionar un recinto.");
            return;
        }
        String id = "E" + String.format("%03d", gestion.listarEventos().size() + 1);
        LocalDateTime fecha = dpEvFecha.getValue() != null ?
                dpEvFecha.getValue().atTime(LocalTime.of(20, 0)) : LocalDateTime.now().plusDays(30);
        PoliticasEvento pol = new PoliticasEvento(
                txtEvPolCancel.getText().isEmpty() ? "Cancelación estándar" : txtEvPolCancel.getText(),
                txtEvPolReembolso.getText().isEmpty() ? "Reembolso del 80%" : txtEvPolReembolso.getText());
        EventoBuilder builder = new EventoBuilder()
                .conId(id).conNombre(txtEvNombre.getText())
                .conCategoria(cmbEvCategoria.getValue() == null || cmbEvCategoria.getValue().isEmpty() ? "General" : cmbEvCategoria.getValue())
                .conDescripcion(txtEvDescripcion.getText())
                .enCiudad(txtEvCiudad.getText().isEmpty() ? "Bogotá" : txtEvCiudad.getText())
                .enFecha(fecha).conRecinto(cmbEvRecinto.getValue()).conPoliticas(pol);
        gestion.crearEvento(builder);
        limpiarFormularioEvento();
        cargarDatos();
        mostrarInfoLogica("Evento \"" + txtEvNombre.getText() + "\" creado exitosamente.");
    }

    @FXML private void actualizarEvento(ActionEvent event) {
        Evento ev = tblEventos.getSelectionModel().getSelectedItem();
        if (ev == null) { mostrarErrorLogica("Selecciona un evento de la tabla para actualizar."); return; }
        if (!txtEvNombre.getText().isEmpty()) ev.setNombre(txtEvNombre.getText());
        if (cmbEvCategoria.getValue() != null && !cmbEvCategoria.getValue().isEmpty()) ev.setCategoria(cmbEvCategoria.getValue());
        if (!txtEvCiudad.getText().isEmpty()) ev.setCiudad(txtEvCiudad.getText());
        if (!txtEvDescripcion.getText().isEmpty()) ev.setDescripcion(txtEvDescripcion.getText());
        if (dpEvFecha.getValue() != null) ev.setFecha(dpEvFecha.getValue().atTime(LocalTime.of(20, 0)));
        if (cmbEvRecinto.getValue() != null) ev.setRecintoAsociado(cmbEvRecinto.getValue());
        gestion.actualizarEvento(ev);
        cargarDatos();
        mostrarInfoLogica("Evento actualizado.");
    }

    @FXML private void publicarEvento(ActionEvent event) {
        Evento ev = tblEventos.getSelectionModel().getSelectedItem();
        if (ev != null) { gestion.publicarEvento(ev); cargarDatos(); mostrarInfoLogica("Evento publicado."); }
        else mostrarErrorLogica("Selecciona un evento.");
    }
    @FXML private void pausarEvento(ActionEvent event) {
        Evento ev = tblEventos.getSelectionModel().getSelectedItem();
        if (ev != null) { gestion.pausarEvento(ev); cargarDatos(); mostrarInfoLogica("Evento pausado."); }
        else mostrarErrorLogica("Selecciona un evento.");
    }
    @FXML private void cancelarEvento(ActionEvent event) {
        Evento ev = tblEventos.getSelectionModel().getSelectedItem();
        if (ev != null) { gestion.cancelarEvento(ev); cargarDatos(); mostrarInfoLogica("Evento cancelado."); }
        else mostrarErrorLogica("Selecciona un evento.");
    }
    @FXML private void eliminarEvento(ActionEvent event) {
        Evento ev = tblEventos.getSelectionModel().getSelectedItem();
        if (ev == null) { mostrarErrorLogica("Selecciona un evento."); return; }
        if (confirmarAccion("¿Eliminar el evento \"" + ev.getNombre() + "\" permanentemente?")) {
            gestion.eliminarEvento(ev.getIdEvento()); cargarDatos(); mostrarInfoLogica("Evento eliminado.");
        }
    }

    private void limpiarFormularioEvento() {
        txtEvNombre.clear(); cmbEvCategoria.setValue(null); txtEvCiudad.clear();
        txtEvDescripcion.clear(); txtEvPolCancel.clear(); txtEvPolReembolso.clear();
        dpEvFecha.setValue(null); cmbEvRecinto.setValue(null);
    }

    // ============= ACCIONES USUARIOS =============
    @FXML private void crearUsuario(ActionEvent event) {
        if (txtUsNombre.getText().isEmpty() || txtUsEmail.getText().isEmpty()) {
            mostrarErrorLogica("Nombre y email son obligatorios.");
            return;
        }
        if (!txtUsEmail.getText().contains("@")) {
            mostrarErrorLogica("Email inválido. Debe contener @.");
            return;
        }
        if (gestion.listarUsuarios().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(txtUsEmail.getText()))) {
            mostrarErrorLogica("Ya existe un usuario con ese email.");
            return;
        }
        Usuario u = gestion.registrarUsuario(txtUsNombre.getText(), txtUsEmail.getText(),
                txtUsTel.getText(), txtUsPassword.getText().isEmpty() ? "1234" : txtUsPassword.getText());
        u.setEsAdmin(chkUsAdmin.isSelected());
        limpiarFormularioUsuario();
        cargarDatos();
        mostrarInfoLogica("Usuario \"" + txtUsNombre.getText() + "\" creado.");
    }

    @FXML private void actualizarUsuario(ActionEvent event) {
        Usuario u = tblUsuarios.getSelectionModel().getSelectedItem();
        if (u == null) { mostrarErrorLogica("Selecciona un usuario."); return; }
        if (!txtUsNombre.getText().isEmpty()) u.setNombreCompleto(txtUsNombre.getText());
        if (!txtUsEmail.getText().isEmpty()) {
            if (!txtUsEmail.getText().contains("@")) { mostrarErrorLogica("Email inválido."); return; }
            if (gestion.listarUsuarios().stream().anyMatch(x -> x.getEmail().equalsIgnoreCase(txtUsEmail.getText()) && !x.getIdUsuario().equals(u.getIdUsuario()))) {
                mostrarErrorLogica("Otro usuario ya usa ese email."); return;
            }
            u.setEmail(txtUsEmail.getText());
        }
        if (!txtUsTel.getText().isEmpty()) u.setTelefono(txtUsTel.getText());
        u.setEsAdmin(chkUsAdmin.isSelected());
        gestion.actualizarUsuario(u);
        cargarDatos();
        mostrarInfoLogica("Usuario actualizado.");
    }

    @FXML private void eliminarUsuario(ActionEvent event) {
        Usuario u = tblUsuarios.getSelectionModel().getSelectedItem();
        if (u == null) { mostrarErrorLogica("Selecciona un usuario."); return; }
        if (confirmarAccion("¿Eliminar al usuario \"" + u.getNombreCompleto() + "\" permanentemente?")) {
            gestion.eliminarUsuario(u.getIdUsuario()); cargarDatos(); mostrarInfoLogica("Usuario eliminado.");
        }
    }

    private void limpiarFormularioUsuario() {
        txtUsNombre.clear(); txtUsEmail.clear(); txtUsTel.clear();
        txtUsPassword.clear(); chkUsAdmin.setSelected(false);
    }

    // ============= ACCIONES COMPRAS =============
    @FXML private void cancelarCompraAdmin(ActionEvent event) {
        Compra c = tblCompras.getSelectionModel().getSelectedItem();
        if (c == null) { mostrarErrorLogica("Selecciona una compra."); return; }
        String estado = c.getEstadoActual().getNombreEstado();
        if ("CANCELADA".equals(estado) || "REEMBOLSADA".equals(estado)) {
            mostrarErrorLogica("La compra ya está " + estado + ". No se puede cancelar de nuevo.");
            return;
        }
        if (confirmarAccion("¿Cancelar compra " + c.getIdCompra() + " de $" + String.format("%,.0f", c.getTotal()) + "?")) {
            gestion.cancelarCompra(c); cargarDatos(); mostrarInfoLogica("Compra cancelada.");
        }
    }
    @FXML private void registrarReembolso(ActionEvent event) {
        Compra c = tblCompras.getSelectionModel().getSelectedItem();
        if (c == null) { mostrarErrorLogica("Selecciona una compra."); return; }
        if (!"CONFIRMADA".equals(c.getEstadoActual().getNombreEstado())) {
            mostrarErrorLogica("Solo se puede reembolsar una compra CONFIRMADA.");
            return;
        }
        if (confirmarAccion("¿Reembolsar compra " + c.getIdCompra() + " de $" + String.format("%,.0f", c.getTotal()) + "?")) {
            gestion.registrarReembolso(c); cargarDatos(); mostrarInfoLogica("Reembolso registrado.");
        }
    }

    // ============= ACCIONES RECINTOS =============
    @FXML private void crearRecintoAction(ActionEvent event) {
        if (txtRecNombre.getText().isEmpty()) {
            mostrarErrorLogica("El nombre del recinto es obligatorio.");
            return;
        }
        String id = "R" + String.format("%03d", gestion.listarRecintos().size() + 1);
        Recinto r = new Recinto(id, txtRecNombre.getText(),
                txtRecDir.getText().isEmpty() ? "Sin dirección" : txtRecDir.getText(),
                txtRecCiudad.getText().isEmpty() ? "Bogotá" : txtRecCiudad.getText());
        gestion.crearRecinto(r);
        limpiarFormularioRecinto();
        cargarDatos();
        mostrarInfoLogica("Recinto \"" + txtRecNombre.getText() + "\" creado.");
    }

    @FXML private void actualizarRecintoAction(ActionEvent event) {
        Recinto r = tblRecintos.getSelectionModel().getSelectedItem();
        if (r == null) { mostrarErrorLogica("Selecciona un recinto."); return; }
        if (!txtRecNombre.getText().isEmpty()) r.setNombre(txtRecNombre.getText());
        if (!txtRecDir.getText().isEmpty()) r.setDireccion(txtRecDir.getText());
        if (!txtRecCiudad.getText().isEmpty()) r.setCiudad(txtRecCiudad.getText());
        gestion.actualizarRecinto(r);
        cargarDatos();
        mostrarInfoLogica("Recinto actualizado.");
    }

    @FXML private void eliminarRecintoAction(ActionEvent event) {
        Recinto r = tblRecintos.getSelectionModel().getSelectedItem();
        if (r == null) { mostrarErrorLogica("Selecciona un recinto."); return; }
        if (confirmarAccion("¿Eliminar el recinto \"" + r.getNombre() + "\" y todas sus zonas permanentemente?")) {
            gestion.eliminarRecinto(r.getIdRecinto()); cargarDatos(); mostrarInfoLogica("Recinto eliminado.");
        }
    }

    private void limpiarFormularioRecinto() {
        txtRecNombre.clear(); txtRecDir.clear(); txtRecCiudad.clear();
    }

    // ============= ACCIONES ZONAS =============
    @FXML private void crearZonaAction(ActionEvent event) {
        Recinto r = cmbZonaRecinto.getValue();
        String nombre = cmbZonaNombre.getValue();
        if (r == null || nombre == null || nombre.isEmpty()) {
            mostrarErrorLogica("Debes seleccionar un recinto y darle nombre a la zona.");
            return;
        }
        String id = "Z" + String.format("%03d", r.getZonas().size() + 100);
        int cap = 100;
        int numAsientos = 50;
        double precio = 50000;
        try { cap = Integer.parseInt(txtZonaCapacidad.getText()); } catch (Exception e) { mostrarErrorLogica("Capacidad inválida. Se usará 100."); }
        try { numAsientos = Integer.parseInt(txtZonaAsientos.getText()); } catch (Exception e) { mostrarErrorLogica("Asientos inválido. Se usará 50."); }
        try { precio = Double.parseDouble(txtZonaPrecio.getText()); } catch (Exception e) { mostrarErrorLogica("Precio inválido. Se usará 50000."); }
        if (numAsientos > cap) {
            mostrarErrorLogica("Los asientos a crear (" + numAsientos + ") no pueden superar la capacidad (" + cap + "). Se usarán " + cap + " asientos.");
            numAsientos = cap;
        }
        Zona z = new Zona(id, nombre, cap, precio);
        // Apply strategy
        String estrategia = cmbZonaEstrategia.getValue();
        if ("Preventa (20% descuento)".equals(estrategia)) {
            z.setEstrategiaTarifa(new TarifaPreventa());
        }
        int toCreate = Math.min(numAsientos, cap);
        for (int i = 1; i <= toCreate; i++) {
            z.addAsiento(new Asiento("A-" + id + "-" + i, nombre.substring(0, Math.min(2, nombre.length())).toUpperCase(), String.valueOf(i)));
        }
        gestion.crearZona(r, z);
        limpiarFormularioZona();
        cargarDatos();
        mostrarInfoLogica("Zona \"" + nombre + "\" creada con " + toCreate + " asientos (capacidad: " + cap + ").");
    }

    @FXML private void actualizarZonaAction(ActionEvent event) {
        Zona z = tblZonas.getSelectionModel().getSelectedItem();
        if (z == null) { mostrarErrorLogica("Selecciona una zona."); return; }
        if (cmbZonaNombre.getValue() != null && !cmbZonaNombre.getValue().isEmpty()) z.setNombre(cmbZonaNombre.getValue());
        try { z.definirCapacidad(Integer.parseInt(txtZonaCapacidad.getText())); } catch (Exception ignored) {}
        try { z.definirPrecioBase(Double.parseDouble(txtZonaPrecio.getText())); } catch (Exception ignored) {}
        String estrategia = cmbZonaEstrategia.getValue();
        if ("Preventa (20% descuento)".equals(estrategia)) {
            z.setEstrategiaTarifa(new TarifaPreventa());
        } else if ("Estándar".equals(estrategia)) {
            z.setEstrategiaTarifa(new TarifaEstandar());
        }
        gestion.actualizarZona(z);
        cargarDatos();
        mostrarInfoLogica("Zona actualizada.");
    }

    @FXML private void eliminarZonaAction(ActionEvent event) {
        Zona z = tblZonas.getSelectionModel().getSelectedItem();
        if (z == null) { mostrarErrorLogica("Selecciona una zona."); return; }
        if (confirmarAccion("¿Eliminar la zona \"" + z.getNombre() + "\" y todos sus asientos permanentemente?")) {
            gestion.eliminarZona(z.getIdZona()); cargarDatos(); mostrarInfoLogica("Zona eliminada.");
        }
    }

    private void limpiarFormularioZona() {
        cmbZonaNombre.setValue(null); txtZonaCapacidad.clear(); txtZonaAsientos.clear(); txtZonaPrecio.clear(); cmbZonaEstrategia.setValue(null);
    }

    // ============= ACCIONES ASIENTOS =============
    @FXML private void cargarZonasAsiento(ActionEvent event) {
        Recinto r = cmbAsientoRecinto.getValue();
        if (r != null) cmbAsientoZona.setItems(FXCollections.observableArrayList(r.getZonas()));
    }

    @FXML private void cargarAsientosAdmin(ActionEvent event) {
        Zona z = cmbAsientoZona.getValue();
        if (z != null) tblAsientos.setItems(FXCollections.observableArrayList(z.getAsientos()));
    }

    @FXML private void habilitarAsiento(ActionEvent event) {
        Asiento a = tblAsientos.getSelectionModel().getSelectedItem();
        if (a == null) { mostrarErrorLogica("Selecciona un asiento."); return; }
        if (a.getEstado() != EstadoAsiento.BLOQUEADO) {
            mostrarErrorLogica("Solo se puede habilitar un asiento BLOQUEADO.");
            return;
        }
        a.cambiarEstado(EstadoAsiento.DISPONIBLE); cargarAsientosAdmin(null); tblAsientos.refresh();
    }

    @FXML private void bloquearAsiento(ActionEvent event) {
        Asiento a = tblAsientos.getSelectionModel().getSelectedItem();
        if (a == null) { mostrarErrorLogica("Selecciona un asiento."); return; }
        if (a.getEstado() != EstadoAsiento.DISPONIBLE) {
            mostrarErrorLogica("Solo se puede bloquear un asiento DISPONIBLE.");
            return;
        }
        a.cambiarEstado(EstadoAsiento.BLOQUEADO); cargarAsientosAdmin(null); tblAsientos.refresh();
    }

    @FXML private void liberarAsiento(ActionEvent event) {
        Asiento a = tblAsientos.getSelectionModel().getSelectedItem();
        if (a == null) { mostrarErrorLogica("Selecciona un asiento."); return; }
        if (a.getEstado() != EstadoAsiento.VENDIDO) {
            mostrarErrorLogica("Solo se puede liberar un asiento VENDIDO.");
            return;
        }
        a.cambiarEstado(EstadoAsiento.DISPONIBLE); cargarAsientosAdmin(null); tblAsientos.refresh();
    }

    @FXML private void crearAsientoAction(ActionEvent event) {
        Zona z = cmbAsientoZona.getValue();
        if (z == null) { mostrarErrorLogica("Selecciona una zona primero."); return; }
        TextInputDialog dialog = new TextInputDialog("A-" + z.getIdZona() + "-" + (z.getAsientos().size() + 1));
        dialog.setTitle("Nuevo Asiento");
        dialog.setHeaderText("Crear asiento en " + z.getNombre());
        dialog.setContentText("ID del asiento:");
        dialog.showAndWait().ifPresent(id -> {
            if (id.isBlank()) return;
            if (z.getAsientos().stream().anyMatch(a -> a.getIdAsiento().equals(id))) {
                mostrarErrorLogica("Ya existe un asiento con ID '" + id + "' en esta zona."); return;
            }
            TextInputDialog filaDlg = new TextInputDialog(z.getNombre().substring(0, Math.min(2, z.getNombre().length())).toUpperCase());
            filaDlg.setTitle("Fila");
            filaDlg.setHeaderText("Fila del asiento");
            filaDlg.setContentText("Fila:");
            filaDlg.showAndWait().ifPresent(fila -> {
                TextInputDialog numDlg = new TextInputDialog(String.valueOf(z.getAsientos().size() + 1));
                numDlg.setTitle("Número");
                numDlg.setHeaderText("Número del asiento");
                numDlg.setContentText("Número:");
                numDlg.showAndWait().ifPresent(num -> {
                    Asiento a = new Asiento(id, fila.toUpperCase(), num);
                    z.addAsiento(a);
                    cargarAsientosAdmin(null);
                    tblAsientos.refresh();
                    mostrarInfoLogica("Asiento " + id + " creado.");
                });
            });
        });
    }

    // ============= MÉTRICAS =============
    @FXML private void cambiarEventoMetrica(ActionEvent event) {
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
        double totalVip = 0, totalSeguro = 0, totalMerch = 0, totalParq = 0, totalPref = 0;
        for (Evento ev : gestion.listarEventos()) {
            Map<String, Double> servicios = gestion.obtenerIngresosServiciosAdicionales(ev);
            totalVip += servicios.getOrDefault("Acceso VIP", 0.0);
            totalSeguro += servicios.getOrDefault("Seguro Cancelación", 0.0);
            totalMerch += servicios.getOrDefault("Merchandising", 0.0);
            totalParq += servicios.getOrDefault("Parqueadero", 0.0);
            totalPref += servicios.getOrDefault("Acceso Preferencial", 0.0);
        }
        series.getData().add(new XYChart.Data<>("Acceso VIP", totalVip));
        series.getData().add(new XYChart.Data<>("Seguro Cancelación", totalSeguro));
        series.getData().add(new XYChart.Data<>("Merchandising", totalMerch));
        series.getData().add(new XYChart.Data<>("Parqueadero", totalParq));
        series.getData().add(new XYChart.Data<>("Acceso Preferencial", totalPref));
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

    // ============= INCIDENCIAS =============
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

    @FXML private void reportarIncidenciaCompra(ActionEvent event) {
        Compra c = tblCompras.getSelectionModel().getSelectedItem();
        if (c == null) { mostrarErrorLogica("Selecciona una compra."); return; }
        if (confirmarAccion("¿Reportar incidencia para compra " + c.getIdCompra() + "?")) {
            c.reportarIncidencia();
            gestion.registrarIncidencia(new Incidencia("INC-" + System.currentTimeMillis(),
                    "Incidencia Reportada", "Compra " + c.getIdCompra() + " reportada por administrador", c.getIdCompra()));
            cargarDatos();
            mostrarInfoLogica("Incidencia reportada para compra " + c.getIdCompra());
        }
    }

    @FXML private void resolverIncidenciaCompra(ActionEvent event) {
        Compra c = tblCompras.getSelectionModel().getSelectedItem();
        if (c == null) { mostrarErrorLogica("Selecciona una compra."); return; }
        if (!"INCIDENCIA".equals(c.getEstadoActual().getNombreEstado())) {
            mostrarErrorLogica("La compra no está en estado INCIDENCIA.");
            return;
        }
        if (confirmarAccion("¿Resolver incidencia para compra " + c.getIdCompra() + "? Volverá a estado PAGADA.")) {
            gestion.resolverIncidencia(c);
            cargarDatos();
            mostrarInfoLogica("Incidencia resuelta para compra " + c.getIdCompra());
        }
    }

    @FXML private void reasignarAsientosCompra(ActionEvent event) {
        Compra c = tblCompras.getSelectionModel().getSelectedItem();
        if (c == null) { mostrarErrorLogica("Selecciona una compra."); return; }
        List<Entrada> entradas = c.getItemsCompra();
        if (entradas.isEmpty()) { mostrarErrorLogica("La compra no tiene entradas."); return; }

        // Build dialog with combo boxes for old -> new seat
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reasignar Asientos - " + c.getIdCompra());
        dialog.setHeaderText("Selecciona el asiento actual y el nuevo asiento disponible");

        VBox content = new VBox(12);
        content.setStyle("-fx-padding: 16;");

        ComboBox<String> cmbAsientoViejo = new ComboBox<>();
        cmbAsientoViejo.setPrefWidth(300);
        for (Entrada e : entradas) {
            Asiento a = e.getAsiento();
            if (a != null) {
                cmbAsientoViejo.getItems().add(a.getIdAsiento() + " - " + a.getFila() + a.getNumero() + " (" + e.getZona().getNombre() + ")");
            }
        }
        if (!cmbAsientoViejo.getItems().isEmpty()) cmbAsientoViejo.getSelectionModel().select(0);

        ComboBox<String> cmbAsientoNuevo = new ComboBox<>();
        cmbAsientoNuevo.setPrefWidth(300);
        cmbAsientoNuevo.setPromptText("Selecciona un asiento disponible...");
        cmbAsientoNuevo.setDisable(true);

        // When old seat selected, filter available seats in same zone
        cmbAsientoViejo.setOnAction(ev -> {
            cmbAsientoNuevo.getItems().clear();
            cmbAsientoNuevo.setDisable(true);
            int idx = cmbAsientoViejo.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;
            Entrada entradaSel = entradas.get(idx);
            Zona zonaSel = entradaSel.getZona();
            if (zonaSel == null) return;
            cmbAsientoNuevo.getItems().add("Seleccionar...");
            for (Asiento a : zonaSel.getAsientos()) {
                if (a.getEstado() == EstadoAsiento.DISPONIBLE) {
                    cmbAsientoNuevo.getItems().add(a.getIdAsiento() + " - " + a.getFila() + a.getNumero());
                }
            }
            cmbAsientoNuevo.setDisable(false);
            cmbAsientoNuevo.getSelectionModel().select(0);
        });

        Label lblOld = new Label("Asiento actual:");
        Label lblNew = new Label("Nuevo asiento:");
        content.getChildren().addAll(lblOld, cmbAsientoViejo, lblNew, cmbAsientoNuevo);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK && cmbAsientoNuevo.getValue() != null && !cmbAsientoNuevo.getValue().equals("Seleccionar...")) {
                int idx = cmbAsientoViejo.getSelectionModel().getSelectedIndex();
                if (idx < 0) return;
                Asiento viejo = entradas.get(idx).getAsiento();
                // Find the new Asiento object
                Zona zonaSel = entradas.get(idx).getZona();
                Asiento nuevo = null;
                if (zonaSel != null) {
                    String newId = cmbAsientoNuevo.getValue().split(" - ")[0];
                    for (Asiento a : zonaSel.getAsientos()) {
                        if (a.getIdAsiento().equals(newId)) { nuevo = a; break; }
                    }
                }
                if (viejo != null && nuevo != null) {
                    gestion.reasignarAsientos(c, viejo, nuevo);
                    cargarDatos();
                    mostrarInfoLogica("Asiento reasignado: " + viejo.getIdAsiento() + " -> " + nuevo.getIdAsiento());
                }
            }
        });
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
        mostrarInfoLogica("Reporte ejecutivo generado correctamente:\n" + fileName);
    }

    @FXML private void generarPDF(ActionEvent event) {
        Usuario user = LoginController.getUsuarioLogueado();
        if (user == null || !user.isEsAdmin()) {
            mostrarErrorLogica("Acceso denegado: solo administradores pueden generar reportes.");
            return;
        }
        new ReporteProxy(user).generarReportePDF(new Filtros(), "Reporte_TicketHub.pdf");
        mostrarInfoLogica("Reporte PDF generado exitosamente.");
    }

    @FXML private void generarCSV(ActionEvent event) {
        Usuario user = LoginController.getUsuarioLogueado();
        if (user == null || !user.isEsAdmin()) {
            mostrarErrorLogica("Acceso denegado: solo administradores pueden generar reportes.");
            return;
        }
        new ReporteProxy(user).generarReporteCSV(new Filtros(), "Reporte_TicketHub.csv");
        mostrarInfoLogica("Reporte CSV generado exitosamente.");
    }

    private boolean confirmarAccion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Confirmar operación");
        alert.setContentText(mensaje);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void mostrarErrorLogica(String m) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
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
        Parent root = FXMLLoader.load(getClass().getResource("/com/tickethub/views/Login.fxml"));
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(new Scene(root, window.getWidth(), window.getHeight()));
    }
}
