package com.tickethub.service;

import com.tickethub.model.Filtros;
import com.tickethub.model.Incidencia;
import java.util.List;
import java.util.stream.Collectors;

public class IncidentService {
    private List<Incidencia> incidencias;

    public IncidentService(List<Incidencia> incidencias) {
        this.incidencias = incidencias;
    }

    public void registrarIncidencia(Incidencia i) {
        i.registrar();
        incidencias.add(i);
    }

    public List<Incidencia> consultarIncidencias(Filtros f) {
        return incidencias.stream()
                .filter(i -> f.getFechaInicio() == null || !i.getFecha().toLocalDate().isBefore(f.getFechaInicio()))
                .filter(i -> f.getFechaFin() == null || !i.getFecha().toLocalDate().isAfter(f.getFechaFin()))
                .collect(Collectors.toList());
    }

    public List<Incidencia> listarIncidencias() {
        return incidencias;
    }
}
