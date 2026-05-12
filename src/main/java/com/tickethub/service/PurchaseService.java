package com.tickethub.service;

import com.tickethub.model.*;
import com.tickethub.model.state.CompraIncidencia;
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
        // Route through INCIDENCIA if already paid/confirmed
        if (!(c.getEstadoActual() instanceof CompraIncidencia)
                && !c.getEstadoActual().getNombreEstado().equals("CREADA")) {
            c.reportarIncidencia();
            incidentService.registrarIncidencia(new Incidencia("INC-" + System.currentTimeMillis(),
                    "Cancelación con Incidencia", "Compra " + c.getIdCompra() + " en estado " + c.getEstadoActual().getNombreEstado(), c.getIdCompra()));
        }
        c.cancelar();
        for (Entrada e : c.getItemsCompra()) e.anular();
        incidentService.registrarIncidencia(new Incidencia("INC-" + System.currentTimeMillis(),
                "Cancelación de Compra", "Compra " + c.getIdCompra() + " cancelada", c.getIdCompra()));
    }

    public void resolverIncidencia(Compra c) {
        if (c.getEstadoActual() instanceof CompraIncidencia) {
            c.pagar();
            incidentService.registrarIncidencia(new Incidencia("INC-" + System.currentTimeMillis(),
                    "Incidencia Resuelta", "Compra " + c.getIdCompra() + " incidencia resuelta", c.getIdCompra()));
        }
    }

    public List<Compra> consultarHistorialCompras(Usuario u, Filtros f) {
        return compras.stream()
                .filter(c -> c.getUsuarioAsociado().getIdUsuario().equals(u.getIdUsuario()))
                .filter(c -> f.getEstado() == null || f.getEstado().isEmpty() ||
                        c.getEstadoActual().getNombreEstado().equalsIgnoreCase(f.getEstado()))
                .filter(c -> f.getFechaInicio() == null ||
                        !c.getFechaCreacion().toLocalDate().isBefore(f.getFechaInicio()))
                .filter(c -> f.getFechaFin() == null ||
                        !c.getFechaCreacion().toLocalDate().isAfter(f.getFechaFin()))
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
