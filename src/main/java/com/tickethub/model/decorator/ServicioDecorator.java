package com.tickethub.model.decorator;

import com.tickethub.model.Entrada;
import com.tickethub.model.Zona;
import com.tickethub.model.Asiento;

/**
 * Patrón Decorator: Clase abstracta para servicios adicionales sobre Entrada.
 * 
 * SOLID - OCP: Nuevos servicios se añaden extendiendo ServicioDecorator sin modificar EntradaBase.
 * SOLID - LSP: Cualquier ServicioDecorator puede sustituir a Entrada porque getPrecioFinal() siempre retorna double.
 */
public abstract class ServicioDecorator extends Entrada {
    protected Entrada entradaDecorada;

    public ServicioDecorator(Entrada e) {
        super(e.getIdEntrada(), e.getZona(), e.getAsiento());
        this.entradaDecorada = e;
    }

    public abstract double getCostoAdicional();

    public Entrada getEntradaDecorada() { return entradaDecorada; }

    @Override
    public double getPrecioFinal() {
        return entradaDecorada.getPrecioFinal() + getCostoAdicional();
    }
}
