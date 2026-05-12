package com.tickethub.service;

import com.tickethub.model.Recinto;
import com.tickethub.model.Zona;
import java.util.List;

public class VenueService {
    private List<Recinto> recintos;

    public VenueService(List<Recinto> recintos) {
        this.recintos = recintos;
    }

    public void crearRecinto(Recinto r) { recintos.add(r); }
    public void actualizarRecinto(Recinto r) { System.out.println("Recinto actualizado: " + r.getNombre()); }
    public void eliminarRecinto(String id) { recintos.removeIf(r -> r.getIdRecinto().equals(id)); }
    public List<Recinto> listarRecintos() { return recintos; }
    
    public void crearZona(Recinto r, Zona z) { r.administrarZonas(z); }
    public void actualizarZona(Zona z) { System.out.println("Zona actualizada: " + z.getNombre()); }
    public void eliminarZona(String id) {
        for (Recinto r : recintos) r.getZonas().removeIf(z -> z.getIdZona().equals(id));
    }
}
