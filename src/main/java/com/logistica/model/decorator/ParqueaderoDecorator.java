package com.logistica.model.decorator;

import com.logistica.model.Entrada;

public class ParqueaderoDecorator extends ServicioDecorator {
    public ParqueaderoDecorator(Entrada e) { super(e); }

    @Override
    public double getCostoAdicional() { return 20000; }

    @Override
    public String toString() { return super.toString() + " [+Parqueadero]"; }
}
