package com.logistica.model.state;

import com.logistica.model.Compra;

public class CompraIncidencia implements IEstadoCompra {
    @Override
    public void siguiente(Compra c) {
        System.out.println("Compra " + c.getIdCompra() + " en incidencia. Debe resolverse antes de avanzar.");
    }
    @Override
    public void cancelar(Compra c) {
        c.setEstado(new CompraCancelada());
        System.out.println("Compra " + c.getIdCompra() + " -> CANCELADA (desde incidencia)");
    }
    @Override
    public String getNombreEstado() { return "INCIDENCIA"; }
}
