package com.logistica.model.adapter;

/**
 * Patrón Adapter: Adapta distintos servicios de pago a una interfaz común.
 * 
 * SOLID - ISP: Interfaz mínima con un solo método (procesarPago).
 * SOLID - DIP: El controlador depende de esta abstracción, no de PayPalAdapter/TarjetaCreditoAdapter concretos.
 * SOLID - OCP: Nuevos métodos de pago se añaden implementando IPagoAdapter, sin modificar el controlador.
 */
public interface IPagoAdapter {
    boolean procesarPago(double monto);
}
