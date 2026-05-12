package com.tickethub;

import com.tickethub.model.proxy.ReporteReal;
import com.tickethub.model.proxy.ReporteConfig;

public class TestReporte {
    public static void main(String[] args) {
        System.out.println("Iniciando test de reporte...");
        try {
            ReporteReal reporteReal = new ReporteReal();
            reporteReal.generarReporteInteligente(ReporteConfig.ejecutivo(), "Test_Reporte_Ejecutivo.pdf");
            System.out.println("Test terminado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
