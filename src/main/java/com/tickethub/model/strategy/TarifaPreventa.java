package com.tickethub.model.strategy;

import com.tickethub.model.Zona;

public class TarifaPreventa implements ITarifaStrategy {
    @Override
    public double calcular(Zona zona) {
        return zona.getPrecioBase() * 0.8; // 20% descuento en preventa
    }
}
