package com.tickethub.model.state;

import com.tickethub.model.Compra;

public class CompraCreada implements IEstadoCompra {
    @Override
    public void siguiente(Compra c) {
        c.setEstado(new CompraPagada());
        System.out.println("Compra " + c.getIdCompra() + " -> PAGADA");
    }
    @Override
    public void cancelar(Compra c) {
        c.setEstado(new CompraCancelada());
        System.out.println("Compra " + c.getIdCompra() + " -> CANCELADA");
    }
    @Override
    public String getNombreEstado() { return "CREADA"; }
}
