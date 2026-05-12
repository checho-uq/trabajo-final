package com.tickethub.model.factory;

import com.tickethub.model.*;

/**
 * Patrón Factory Method: Interfaz para crear distintos tipos de Entrada.
 */
public interface IEntradaFactory {
    Entrada crearEntrada(Zona z, Asiento a);
}
