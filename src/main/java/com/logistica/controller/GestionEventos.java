package com.logistica.controller;

import com.logistica.model.*;
import com.logistica.service.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Patrón: Singleton + Facade
 * Clase central que actúa como fachada delegando la lógica a servicios especializados.
 */
public class GestionEventos {
    private static GestionEventos instance;

    // Listas de datos en memoria (Actúan como persistencia simple)
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Evento> eventos = new ArrayList<>();
    private final List<Compra> compras = new ArrayList<>();
    private final List<Recinto> recintos = new ArrayList<>();
    private final List<Incidencia> incidencias = new ArrayList<>();

    // Servicios
    private final UserService userService;
    private final EventService eventService;
    private final VenueService venueService;
    private final PurchaseService purchaseService;
    private final IncidentService incidentService;
    private final ReportService reportService;
    private final DataInitializer dataInitializer;

    private GestionEventos() {
        // Inicialización de servicios con las listas compartidas
        this.incidentService = new IncidentService(incidencias);
        this.userService = new UserService(usuarios);
        this.eventService = new EventService(eventos, incidentService);
        this.venueService = new VenueService(recintos);
        this.purchaseService = new PurchaseService(compras, incidentService);
        this.reportService = new ReportService(compras);
        this.dataInitializer = new DataInitializer(usuarios, eventos, compras, recintos, incidencias);
    }

    public static synchronized GestionEventos getInstance() {
        if (instance == null) {
            instance = new GestionEventos();
        }
        return instance;
    }

    // ====================== DATOS DE PRUEBA ======================
    public void inicializarDatosPrueba() {
        dataInitializer.inicializar();
    }

    // ====================== GESTIÓN DE USUARIOS ======================
    public Usuario registrarUsuario(String nombre, String email, String tel, String password) {
        return userService.registrarUsuario(nombre, email, tel, password);
    }

    public Usuario iniciarSesion(String email, String password) {
        return userService.iniciarSesion(email, password);
    }

    public void actualizarUsuario(Usuario u) {
        userService.actualizarUsuario(u);
    }

    public void eliminarUsuario(String id) {
        userService.eliminarUsuario(id);
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(userService.listarUsuarios());
    }

    // ====================== GESTIÓN DE EVENTOS ======================
    public List<Evento> explorarEventos(Filtros f) {
        return eventService.explorarEventos(f);
    }

    public Evento crearEvento(EventoBuilder builder) {
        return eventService.crearEvento(builder);
    }

    public void actualizarEvento(Evento e) {
        eventService.actualizarEvento(e);
    }

    public void eliminarEvento(String id) {
        eventService.eliminarEvento(id);
    }

    public void publicarEvento(Evento e) {
        eventService.publicarEvento(e);
    }

    public void pausarEvento(Evento e) {
        eventService.pausarEvento(e);
    }

    public void cancelarEvento(Evento e) {
        eventService.cancelarEvento(e);
    }

    public List<Evento> listarEventos() {
        return new ArrayList<>(eventService.listarEventos());
    }

    // ====================== GESTIÓN DE RECINTOS ======================
    public void crearRecinto(Recinto r) {
        venueService.crearRecinto(r);
    }

    public void actualizarRecinto(Recinto r) {
        venueService.actualizarRecinto(r);
    }

    public void eliminarRecinto(String id) {
        venueService.eliminarRecinto(id);
    }

    public List<Recinto> listarRecintos() {
        return new ArrayList<>(venueService.listarRecintos());
    }

    public void crearZona(Recinto r, Zona z) {
        venueService.crearZona(r, z);
    }

    public void actualizarZona(Zona z) {
        venueService.actualizarZona(z);
    }

    public void eliminarZona(String id) {
        venueService.eliminarZona(id);
    }

    // ====================== GESTIÓN DE COMPRAS ======================
    public Compra crearCompra(Usuario u, Evento ev, List<Entrada> entradas) {
        return purchaseService.crearCompra(u, ev, entradas);
    }

    public void modificarCompra(Compra c, List<Entrada> nuevasEntradas) {
        purchaseService.modificarCompra(c, nuevasEntradas);
    }

    public void cancelarCompra(Compra c) {
        purchaseService.cancelarCompra(c);
    }

    public List<Compra> consultarHistorialCompras(Usuario u, Filtros f) {
        return purchaseService.consultarHistorialCompras(u, f);
    }

    public void reasignarAsientos(Compra c, Asiento viejo, Asiento nuevo) {
        purchaseService.reasignarAsientos(c, viejo, nuevo);
    }

    public void registrarReembolso(Compra c) {
        purchaseService.registrarReembolso(c);
    }

    // ====================== GESTIÓN DE INCIDENCIAS ======================
    public void registrarIncidencia(Incidencia i) {
        incidentService.registrarIncidencia(i);
    }

    public List<Incidencia> consultarIncidencias(Filtros f) {
        return incidentService.consultarIncidencias(f);
    }

    // ====================== MÉTRICAS ======================
    public Map<String, Double> obtenerVentasPorPeriodo(LocalDate inicio, LocalDate fin) {
        return reportService.obtenerVentasPorPeriodo(inicio, fin);
    }

    public Map<String, Double> obtenerOcupacionPorZona(Evento e) {
        return reportService.obtenerOcupacionPorZona(e);
    }

    public Map<String, Double> obtenerIngresosServiciosAdicionales(Evento e) {
        return reportService.obtenerIngresosServiciosAdicionales(e);
    }

    public double obtenerTasaCancelacion() {
        return reportService.obtenerTasaCancelacion();
    }

    public List<Evento> obtenerTopEventos() {
        return reportService.obtenerTopEventos();
    }

    public double obtenerTotalIngresos() {
        return reportService.obtenerTotalIngresos();
    }

    // Getters directos (manteniendo compatibilidad)
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Evento> getEventos() { return eventos; }
    public List<Compra> getCompras() { return compras; }
    public List<Recinto> getRecintos() { return recintos; }
    public List<Incidencia> getIncidencias() { return incidencias; }
}
