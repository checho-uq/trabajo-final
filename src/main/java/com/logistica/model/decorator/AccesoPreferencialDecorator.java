package com.logistica.model.decorator;

import com.logistica.model.Entrada;

public class AccesoPreferencialDecorator extends ServicioDecorator {
    public AccesoPreferencialDecorator(Entrada e) { super(e); }

    @Override
    public double getCostoAdicional() { return 35000; }

    @Override
    public String toString() { return super.toString() + " [+Acceso Preferencial]"; }
}
