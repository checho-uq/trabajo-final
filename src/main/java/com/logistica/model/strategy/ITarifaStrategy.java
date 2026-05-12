package com.logistica.model.strategy;

import com.logistica.model.Zona;

/**
 * Patrón Strategy: Define una familia de algoritmos de cálculo de tarifa.
 * 
 * SOLID - ISP: Interfaz pequeña con un único método, sin métodos sobrantes.
 * SOLID - DIP: Zona depende de esta abstracción, no de implementaciones concretas.
 * SOLID - OCP: Nuevas tarifas se añaden creando nuevas implementaciones, sin modificar Zona.
 */
public interface ITarifaStrategy {
    double calcular(Zona zona);
}
