package com.tickethub.model.strategy;

import com.tickethub.model.Zona;

public class TarifaEstandar implements ITarifaStrategy {
    @Override
    public double calcular(Zona zona) {
        return zona.getPrecioBase();
    }
}
