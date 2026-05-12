package com.logistica.model.proxy;

import java.time.LocalDateTime;
import java.util.*;

/**
 * POJO que contiene todos los datos estructurados necesarios para generar un reporte.
 * Actúa como el contrato entre la capa de recolección de datos y la capa de renderizado.
 */
public class ReporteData {

    // === KPIs ===
    private int totalCompras;
    private double totalIngresos;
    private double tasaCancelacion;
    private int totalEventos;
    private int totalUsuarios;
    private double ticketPromedio;
    private int totalEntradas;
    private int eventosPublicados;
    private int eventosCancelados;

    // === Tablas ===
    private List<String[]> tablaCompras = new ArrayList<>(); // [ID, Cliente, Evento, Total, Estado, Fecha]
    private List<String[]> tablaEventos = new ArrayList<>(); // [ID, Nombre, Categoría, Ciudad, Estado, Recinto]

    // === Series para gráficos ===
    private Map<String, Double> ventasPorEvento = new LinkedHashMap<>();
    private Map<String, Double> ocupacionPorZona = new LinkedHashMap<>();
    private Map<String, Double> ingresosPorServicio = new LinkedHashMap<>();
    private Map<String, Double> distribucionEstados = new LinkedHashMap<>();
    private Map<String, Integer> comprasPorEvento = new LinkedHashMap<>();

    // === Rankings ===
    private List<String> topEventos = new ArrayList<>();

    // === Metadata ===
    private LocalDateTime fechaGeneracion = LocalDateTime.now();
    private String tituloReporte = "Reporte BookIt";
    private String periodoReporte = "";

    // === Getters & Setters ===
    public int getTotalCompras() { return totalCompras; }
    public void setTotalCompras(int v) { this.totalCompras = v; }
    public double getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(double v) { this.totalIngresos = v; }
    public double getTasaCancelacion() { return tasaCancelacion; }
    public void setTasaCancelacion(double v) { this.tasaCancelacion = v; }
    public int getTotalEventos() { return totalEventos; }
    public void setTotalEventos(int v) { this.totalEventos = v; }
    public int getTotalUsuarios() { return totalUsuarios; }
    public void setTotalUsuarios(int v) { this.totalUsuarios = v; }
    public double getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(double v) { this.ticketPromedio = v; }
    public int getTotalEntradas() { return totalEntradas; }
    public void setTotalEntradas(int v) { this.totalEntradas = v; }
    public int getEventosPublicados() { return eventosPublicados; }
    public void setEventosPublicados(int v) { this.eventosPublicados = v; }
    public int getEventosCancelados() { return eventosCancelados; }
    public void setEventosCancelados(int v) { this.eventosCancelados = v; }

    public List<String[]> getTablaCompras() { return tablaCompras; }
    public void setTablaCompras(List<String[]> v) { this.tablaCompras = v; }
    public List<String[]> getTablaEventos() { return tablaEventos; }
    public void setTablaEventos(List<String[]> v) { this.tablaEventos = v; }

    public Map<String, Double> getVentasPorEvento() { return ventasPorEvento; }
    public void setVentasPorEvento(Map<String, Double> v) { this.ventasPorEvento = v; }
    public Map<String, Double> getOcupacionPorZona() { return ocupacionPorZona; }
    public void setOcupacionPorZona(Map<String, Double> v) { this.ocupacionPorZona = v; }
    public Map<String, Double> getIngresosPorServicio() { return ingresosPorServicio; }
    public void setIngresosPorServicio(Map<String, Double> v) { this.ingresosPorServicio = v; }
    public Map<String, Double> getDistribucionEstados() { return distribucionEstados; }
    public void setDistribucionEstados(Map<String, Double> v) { this.distribucionEstados = v; }
    public Map<String, Integer> getComprasPorEvento() { return comprasPorEvento; }
    public void setComprasPorEvento(Map<String, Integer> v) { this.comprasPorEvento = v; }

    public List<String> getTopEventos() { return topEventos; }
    public void setTopEventos(List<String> v) { this.topEventos = v; }

    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public String getTituloReporte() { return tituloReporte; }
    public void setTituloReporte(String v) { this.tituloReporte = v; }
    public String getPeriodoReporte() { return periodoReporte; }
    public void setPeriodoReporte(String v) { this.periodoReporte = v; }
}
