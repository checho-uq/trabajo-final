package com.logistica.service;

import com.logistica.model.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SOLID - SRP: Única responsabilidad = gestionar compras (crear, modificar, cancelar, reembolsar).
 * SOLID - DIP: Recibe IncidentService por constructor.
 */
public class PurchaseService {
    private List<Compra> compras;
    private IncidentService incidentService;

    public PurchaseService(List<Compra> compras, IncidentService incidentService) {
        this.compras = compras;
        this.incidentService = incidentService;
    }

    public Compra crearCompra(Usuario u, Evento ev, List<Entrada> entradas) {
        String id = "CMP-" + System.currentTimeMillis();
        Compra c = new Compra(id, u, ev);
        for (Entrada e : entradas) c.agregarEntrada(e);
        compras.add(c);
        u.getHistorialCompras().add(c);
        return c;
    }

    public void modificarCompra(Compra c, List<Entrada> nuevasEntradas) {
        c.getItemsCompra().clear();
        for (Entrada e : nuevasEntradas) c.agregarEntrada(e);
        c.calcularTotal();
    }

    public void cancelarCompra(Compra c) {
        c.cancelar();
        for (Entrada e : c.getItemsCompra()) e.anular();
        incidentService.registrarIncidencia(new Incidencia("INC-" + System.currentTimeMillis(),
                "Cancelación de Compra", "Compra " + c.getIdCompra() + " cancelada", c.getIdCompra()));
    }

    public List<Compra> consultarHistorialCompras(Usuario u, Filtros f) {
        return compras.stream()
                .filter(c -> c.getUsuarioAsociado().getIdUsuario().equals(u.getIdUsuario()))
                .filter(c -> f.getEstado() == null || f.getEstado().isEmpty() ||
                        c.getEstadoActual().getNombreEstado().equalsIgnoreCase(f.getEstado()))
                .collect(Collectors.toList());
    }

    public void reasignarAsientos(Compra c, Asiento viejo, Asiento nuevo) {
        viejo.cambiarEstado(EstadoAsiento.DISPONIBLE);
        nuevo.cambiarEstado(EstadoAsiento.VENDIDO);
        System.out.println("Asiento reasignado en compra " + c.getIdCompra());
    }

    public void registrarReembolso(Compra c) {
        c.cancelar();
        System.out.println("Reembolso registrado para compra " + c.getIdCompra());
    }

    public List<Compra> listarCompras() {
        return compras;
    }
}
