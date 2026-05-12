package com.tickethub.model.factory;

import com.tickethub.model.*;
import java.util.concurrent.atomic.AtomicLong;

public class EntradaNumeradaFactory implements IEntradaFactory {
    private static final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Entrada crearEntrada(Zona z, Asiento a) {
        String id = "ENT-" + System.currentTimeMillis() + "-" + idCounter.getAndIncrement();
        if (a != null) {
            a.cambiarEstado(EstadoAsiento.RESERVADO);
        }
        return new EntradaBase(id, z, a, z.calcularPrecioFinal());
    }
}
