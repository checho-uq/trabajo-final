package com.logistica.service;

import com.logistica.model.*;
import java.util.List;
import java.util.stream.Collectors;

public class EventService {
    private List<Evento> eventos;
    private IncidentService incidentService;

    public EventService(List<Evento> eventos, IncidentService incidentService) {
        this.eventos = eventos;
        this.incidentService = incidentService;
    }

    public List<Evento> explorarEventos(Filtros f) {
        return eventos.stream()
                .filter(e -> e.getEstado() == EstadoEvento.PUBLICADO)
                .filter(e -> f.getCiudad() == null || f.getCiudad().isEmpty() || e.getCiudad().equalsIgnoreCase(f.getCiudad()))
                .filter(e -> f.getCategoria() == null || f.getCategoria().isEmpty() || e.getCategoria().equalsIgnoreCase(f.getCategoria()))
                .filter(e -> f.getPrecioMax() <= 0 || (e.getRecintoAsociado() != null &&
                        e.getRecintoAsociado().getZonas().stream().anyMatch(z -> z.getPrecioBase() <= f.getPrecioMax())))
                .collect(Collectors.toList());
    }

    public Evento crearEvento(EventoBuilder builder) {
        Evento e = builder.build();
        eventos.add(e);
        return e;
    }

    public void actualizarEvento(Evento e) {
        System.out.println("Evento " + e.getIdEvento() + " actualizado.");
    }

    public void eliminarEvento(String id) {
        eventos.removeIf(e -> e.getIdEvento().equals(id));
    }

    public void publicarEvento(Evento e) { e.publicar(); }
    public void pausarEvento(Evento e) { e.pausar(); }
    
    public void cancelarEvento(Evento e) {
        e.cancelar();
        incidentService.registrarIncidencia(new Incidencia("INC-" + System.currentTimeMillis(),
                "Cancelación de Evento", "Evento " + e.getNombre() + " cancelado por admin", e.getIdEvento()));
    }

    public List<Evento> listarEventos() {
        return eventos;
    }
}
