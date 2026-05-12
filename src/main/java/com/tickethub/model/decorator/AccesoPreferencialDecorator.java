package com.tickethub.model.decorator;

import com.tickethub.model.Entrada;

public class AccesoPreferencialDecorator extends ServicioDecorator {
    public AccesoPreferencialDecorator(Entrada e) { super(e); }

    @Override
    public double getCostoAdicional() { return 35000; }

    @Override
    public String toString() { return super.toString() + " [+Acceso Preferencial]"; }
}
