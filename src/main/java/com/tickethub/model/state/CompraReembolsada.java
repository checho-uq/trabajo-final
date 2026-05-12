package com.tickethub.model.state;

import com.tickethub.model.Compra;

public class CompraReembolsada implements IEstadoCompra {
    @Override
    public void siguiente(Compra c) {
        System.out.println("No se puede avanzar una compra reembolsada.");
    }
    @Override
    public void cancelar(Compra c) {
        System.out.println("La compra ya fue reembolsada.");
    }
    @Override
    public String getNombreEstado() { return "REEMBOLSADA"; }
}
