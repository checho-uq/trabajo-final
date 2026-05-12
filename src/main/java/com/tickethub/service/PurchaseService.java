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
        u.actualizar("Compra creada: " + c.getIdCompra() + " - " + ev.getNombre() + " ($" + String.format("%,.0f", c.getTotal()) + ")");
        return c;
    }

    public void modificarCompra(Compra c, List<Entrada> nuevasEntradas) {
        c.getItemsCompra().clear();
        for (Entrada e : nuevasEntradas) c.agregarEntrada(e);
        c.calcularTotal();
        c.getUsuarioAsociado().actualizar("Compra " + c.getIdCompra() + " modificada. Nuevo total: $" + String.format("%,.0f", c.getTotal()));
    }

    public void cancelarCompra(Compra c) {
        String msg;
        if (c.getEstadoActual().getNombreEstado().equals("CREADA")) {
            c.cancelar();
            for (Entrada e : c.getItemsCompra()) e.anular();
            incidentService.registrarIncidencia(new Incidencia("INC-" + System.currentTimeMillis(),
                    "Cancelación de Compra", "Compra " + c.getIdCompra() + " cancelada", c.getIdCompra()));
            msg = "Compra " + c.getIdCompra() + " cancelada.";
        } else {
            c.reportarIncidencia();
            c.cancelar();
            for (Entrada e : c.getItemsCompra()) e.anular();
            incidentService.registrarIncidencia(new Incidencia("INC-" + System.currentTimeMillis(),
                    "Cancelación con Incidencia", "Compra " + c.getIdCompra() + " cancelada desde estado " + c.getEstadoActual().getNombreEstado(), c.getIdCompra()));
            msg = "Compra " + c.getIdCompra() + " cancelada (con incidencia).";
        }
        c.getUsuarioAsociado().actualizar(msg);
    }

    public void resolverIncidencia(Compra c) {
        if (c.getEstadoActual() instanceof CompraIncidencia) {
            c.pagar();
            incidentService.registrarIncidencia(new Incidencia("INC-" + System.currentTimeMillis(),
                    "Incidencia Resuelta", "Compra " + c.getIdCompra() + " incidencia resuelta", c.getIdCompra()));
            c.getUsuarioAsociado().actualizar("Incidencia resuelta para compra " + c.getIdCompra() + ". Estado: " + c.getEstadoActual().getNombreEstado());
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
        if ("CANCELADA".equals(c.getEstadoActual().getNombreEstado()) || "REEMBOLSADA".equals(c.getEstadoActual().getNombreEstado())) {
            System.out.println("No se puede reasignar: compra " + c.getIdCompra() + " está " + c.getEstadoActual().getNombreEstado());
            return;
        }
        viejo.cambiarEstado(EstadoAsiento.DISPONIBLE);
        nuevo.cambiarEstado(EstadoAsiento.VENDIDO);
        for (Entrada e : c.getItemsCompra()) {
            if (e.getAsiento() != null && e.getAsiento().getIdAsiento().equals(viejo.getIdAsiento())) {
                e.setAsiento(nuevo);
            }
        }
        System.out.println("Asiento reasignado en compra " + c.getIdCompra());
    }

    public void registrarReembolso(Compra c) {
        c.cancelar();
        c.getUsuarioAsociado().actualizar("Reembolso registrado para compra " + c.getIdCompra());
    }

    public List<Compra> listarCompras() {
        return compras;
    }
}
