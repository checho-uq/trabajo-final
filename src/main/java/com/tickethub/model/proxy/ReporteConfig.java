package com.tickethub.model.proxy;

/**
 * Configuración personalizable para la generación de reportes.
 * Permite adaptar el contenido y estilo según el tipo de reporte deseado.
 */
public class ReporteConfig {

    public enum TipoReporte {
        EJECUTIVO,    // Resumen con KPIs, insights y gráficos
        DETALLADO,    // Todo: tablas completas + gráficos + análisis
        RESUMIDO      // Solo KPIs y resumen
    }

    private TipoReporte tipo = TipoReporte.EJECUTIVO;
    private boolean incluirAnalisis = true;
    private boolean incluirGraficos = true;
    private boolean incluirTablaCompras = true;
    private boolean incluirTablaEventos = true;
    private boolean incluirRanking = true;
    private String tituloPersonalizado;

    // Colores corporativos (hex)
    private String colorPrimario = "#1a3a5c";
    private String colorSecundario = "#238636";
    private String colorAccento = "#58a6ff";

    // Presets
    public static ReporteConfig ejecutivo() {
        ReporteConfig c = new ReporteConfig();
        c.tipo = TipoReporte.EJECUTIVO;
        c.incluirTablaCompras = true;
        c.incluirTablaEventos = false;
        return c;
    }

    public static ReporteConfig detallado() {
        ReporteConfig c = new ReporteConfig();
        c.tipo = TipoReporte.DETALLADO;
        return c;
    }

    public static ReporteConfig resumido() {
        ReporteConfig c = new ReporteConfig();
        c.tipo = TipoReporte.RESUMIDO;
        c.incluirGraficos = false;
        c.incluirTablaCompras = false;
        c.incluirTablaEventos = false;
        c.incluirRanking = false;
        return c;
    }

    // Getters & Setters
    public TipoReporte getTipo() { return tipo; }
    public void setTipo(TipoReporte tipo) { this.tipo = tipo; }
    public boolean isIncluirAnalisis() { return incluirAnalisis; }
    public void setIncluirAnalisis(boolean v) { this.incluirAnalisis = v; }
    public boolean isIncluirGraficos() { return incluirGraficos; }
    public void setIncluirGraficos(boolean v) { this.incluirGraficos = v; }
    public boolean isIncluirTablaCompras() { return incluirTablaCompras; }
    public void setIncluirTablaCompras(boolean v) { this.incluirTablaCompras = v; }
    public boolean isIncluirTablaEventos() { return incluirTablaEventos; }
    public void setIncluirTablaEventos(boolean v) { this.incluirTablaEventos = v; }
    public boolean isIncluirRanking() { return incluirRanking; }
    public void setIncluirRanking(boolean v) { this.incluirRanking = v; }
    public String getTituloPersonalizado() { return tituloPersonalizado; }
    public void setTituloPersonalizado(String v) { this.tituloPersonalizado = v; }
    public String getColorPrimario() { return colorPrimario; }
    public void setColorPrimario(String v) { this.colorPrimario = v; }
    public String getColorSecundario() { return colorSecundario; }
    public void setColorSecundario(String v) { this.colorSecundario = v; }
    public String getColorAccento() { return colorAccento; }
    public void setColorAccento(String v) { this.colorAccento = v; }
}
