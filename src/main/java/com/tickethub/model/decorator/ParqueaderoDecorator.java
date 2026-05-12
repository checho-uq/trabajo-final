package com.tickethub.model.decorator;

import com.tickethub.model.Entrada;

public class ParqueaderoDecorator extends ServicioDecorator {
    public ParqueaderoDecorator(Entrada e) { super(e); }

    @Override
    public double getCostoAdicional() { return 20000; }

    @Override
    public String toString() { return super.toString() + " [+Parqueadero]"; }
}
