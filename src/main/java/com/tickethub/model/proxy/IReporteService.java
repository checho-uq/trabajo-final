package com.tickethub.model.proxy;

import com.tickethub.model.Filtros;

/**
 * Patrón Proxy: Interfaz para el servicio de reportes.
 */
public interface IReporteService {
    void generarReporteCSV(Filtros f, String filePath);
    void generarReportePDF(Filtros f, String filePath);
    void generarReporteInteligente(ReporteConfig config, String filePath);
}
