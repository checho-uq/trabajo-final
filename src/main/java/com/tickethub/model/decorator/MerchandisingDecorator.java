package com.tickethub.model.decorator;

import com.tickethub.model.Entrada;

public class MerchandisingDecorator extends ServicioDecorator {
    public MerchandisingDecorator(Entrada e) { super(e); }

    @Override
    public double getCostoAdicional() { return 25000; }

    @Override
    public String toString() { return super.toString() + " [+Merchandising]"; }
}
