package com.tickethub.model.state;

import com.tickethub.model.Compra;

/**
 * Patrón State: Interfaz para los estados de la Compra.
 * 
 * SOLID - ISP: Interfaz específica con solo los métodos necesarios para el estado.
 * SOLID - DIP: Compra depende de esta abstracción en lugar de estados concretos.
 * SOLID - OCP: Nuevos estados se añaden implementando IEstadoCompra, sin modificar Compra.
 */
public interface IEstadoCompra {
    void siguiente(Compra c);
    void cancelar(Compra c);
    String getNombreEstado();
}
