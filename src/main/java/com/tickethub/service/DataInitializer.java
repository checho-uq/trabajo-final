package com.tickethub.service;

import com.tickethub.model.*;
import com.tickethub.model.factory.EntradaNumeradaFactory;
import com.tickethub.model.factory.IEntradaFactory;
import com.tickethub.model.strategy.TarifaEstandar;
import com.tickethub.model.strategy.TarifaPreventa;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DataInitializer {
    private List<Usuario> usuarios;
    private List<Evento> eventos;
    private List<Compra> compras;
    private List<Recinto> recintos;
    private List<Incidencia> incidencias;

    public DataInitializer(List<Usuario> usuarios, List<Evento> eventos, List<Compra> compras, 
                          List<Recinto> recintos, List<Incidencia> incidencias) {
        this.usuarios = usuarios;
        this.eventos = eventos;
        this.compras = compras;
        this.recintos = recintos;
        this.incidencias = incidencias;
    }

    public void inicializar() {
        // --- USUARIOS ---
        Usuario admin = new Usuario("U001", "Admin General", "admin@tickethub.com", "3001234567", "admin123", true);
        admin.addMetodoPago(new MetodoPago("Tarjeta", "Visa **** 9999"));

        Usuario juan = new Usuario("U002", "Juan Pérez", "juan@test.com", "3019876543", "1234", false);
        juan.addMetodoPago(new MetodoPago("Tarjeta", "Visa **** 1234"));
        juan.addMetodoPago(new MetodoPago("PayPal", "juan@paypal.com"));

        Usuario maria = new Usuario("U003", "María García", "maria@test.com", "3023456789", "1234", false);
        maria.addMetodoPago(new MetodoPago("Tarjeta", "Mastercard **** 5678"));

        Usuario carlos = new Usuario("U004", "Carlos López", "carlos@test.com", "3034567890", "1234", false);
        carlos.addMetodoPago(new MetodoPago("PSE", "Bancolombia"));

        Usuario ana = new Usuario("U005", "Ana Martínez", "ana@test.com", "3045678901", "1234", false);
        ana.addMetodoPago(new MetodoPago("PayPal", "ana@paypal.com"));

        Usuario pedro = new Usuario("U006", "Pedro Rodríguez", "pedro@test.com", "3056789012", "1234", false);
        pedro.addMetodoPago(new MetodoPago("Tarjeta", "Visa **** 3456"));

        Usuario admin2 = new Usuario("U007", "Operador Eventos", "operador@tickethub.com", "3067890123", "admin123", true);

        usuarios.addAll(Arrays.asList(admin, juan, maria, carlos, ana, pedro, admin2));

        // --- RECINTOS Y ZONAS ---
        Recinto estadio = new Recinto("R001", "Estadio El Campín", "Cra 30 #57-60", "Bogotá");
        Zona generalE = new Zona("Z001", "General", 5000, 80000);
        Zona preferencialE = new Zona("Z002", "Preferencial", 2000, 150000);
        Zona vipE = new Zona("Z003", "VIP", 500, 350000);
        vipE.setEstrategiaTarifa(new TarifaEstandar());
        for (int i = 1; i <= 10; i++) {
            generalE.addAsiento(new Asiento("A-G" + i, "G", String.valueOf(i)));
            preferencialE.addAsiento(new Asiento("A-P" + i, "P", String.valueOf(i)));
            vipE.addAsiento(new Asiento("A-V" + i, "V", String.valueOf(i)));
        }
        estadio.administrarZonas(generalE);
        estadio.administrarZonas(preferencialE);
        estadio.administrarZonas(vipE);

        Recinto teatro = new Recinto("R002", "Teatro Colón", "Calle 10 #5-32", "Bogotá");
        Zona palcoT = new Zona("Z004", "Palco", 100, 200000);
        Zona plateaT = new Zona("Z005", "Platea", 300, 120000);
        plateaT.setEstrategiaTarifa(new TarifaPreventa());
        Zona balconT = new Zona("Z010", "Balcón", 150, 90000);
        for (int i = 1; i <= 8; i++) {
            palcoT.addAsiento(new Asiento("A-PA" + i, "PA", String.valueOf(i)));
            plateaT.addAsiento(new Asiento("A-PL" + i, "PL", String.valueOf(i)));
            balconT.addAsiento(new Asiento("A-BA" + i, "BA", String.valueOf(i)));
        }
        teatro.administrarZonas(palcoT);
        teatro.administrarZonas(plateaT);
        teatro.administrarZonas(balconT);

        Recinto centro = new Recinto("R003", "Centro de Convenciones", "Cra 37 #24-67", "Medellín");
        Zona salonA = new Zona("Z006", "Salón Principal", 800, 100000);
        Zona salonB = new Zona("Z007", "Salón B", 200, 60000);
        for (int i = 1; i <= 6; i++) {
            salonA.addAsiento(new Asiento("A-SA" + i, "SA", String.valueOf(i)));
            salonB.addAsiento(new Asiento("A-SB" + i, "SB", String.valueOf(i)));
        }
        centro.administrarZonas(salonA);
        centro.administrarZonas(salonB);

        Recinto plazaToros = new Recinto("R004", "Plaza de Toros La Macarena", "Cra 44 #62-12", "Medellín");
        Zona solPT = new Zona("Z008", "Sol", 3000, 50000);
        Zona sombraPT = new Zona("Z009", "Sombra", 1000, 90000);
        for (int i = 1; i <= 8; i++) {
            solPT.addAsiento(new Asiento("A-SOL" + i, "SOL", String.valueOf(i)));
            sombraPT.addAsiento(new Asiento("A-SOM" + i, "SOM", String.valueOf(i)));
        }
        plazaToros.administrarZonas(solPT);
        plazaToros.administrarZonas(sombraPT);

        Recinto movistarArena = new Recinto("R005", "Movistar Arena", "Calle 63 #59A-06", "Bogotá");
        Zona pisoMA = new Zona("Z011", "Piso", 2000, 180000);
        Zona gradaMA = new Zona("Z012", "Gradería", 4000, 70000);
        for (int i = 1; i <= 10; i++) {
            pisoMA.addAsiento(new Asiento("A-PI" + i, "PI", String.valueOf(i)));
            gradaMA.addAsiento(new Asiento("A-GR" + i, "GR", String.valueOf(i)));
        }
        movistarArena.administrarZonas(pisoMA);
        movistarArena.administrarZonas(gradaMA);

        recintos.addAll(Arrays.asList(estadio, teatro, centro, plazaToros, movistarArena));

        // --- POLÍTICAS COMUNES ---
        PoliticasEvento polConcierto = new PoliticasEvento("Cancelación hasta 48h antes", "Reembolso del 80%");
        PoliticasEvento polTeatro = new PoliticasEvento("No se permiten cancelaciones", "Sin reembolso");
        PoliticasEvento polConferencia = new PoliticasEvento("Cancelación hasta 24h antes", "Reembolso del 100%");

        // --- EVENTOS (usando Builder) ---
        Evento e1 = new EventoBuilder().conId("E001").conNombre("Concierto Rock Nacional")
                .conCategoria("Música").conDescripcion("Gran concierto con las mejores bandas de rock colombiano. Ven a disfrutar una noche épica de música en vivo.")
                .enCiudad("Bogotá").enFecha(LocalDateTime.now().plusDays(30))
                .conEstado(EstadoEvento.PUBLICADO).conRecinto(estadio).conPoliticas(polConcierto).build();

        Evento e2 = new EventoBuilder().conId("E002").conNombre("Hamlet - Shakespeare")
                .conCategoria("Teatro").conDescripcion("La obra maestra de William Shakespeare presentada por la Compañía Nacional de Teatro en una producción inolvidable.")
                .enCiudad("Bogotá").enFecha(LocalDateTime.now().plusDays(15))
                .conEstado(EstadoEvento.PUBLICADO).conRecinto(teatro).conPoliticas(polTeatro).build();

        Evento e3 = new EventoBuilder().conId("E003").conNombre("DevConf Colombia 2026")
                .conCategoria("Conferencia").conDescripcion("La conferencia más grande de desarrollo de software en Colombia. Networking, charlas y talleres de tecnología de punta.")
                .enCiudad("Medellín").enFecha(LocalDateTime.now().plusDays(45))
                .conEstado(EstadoEvento.PUBLICADO).conRecinto(centro).conPoliticas(polConferencia).build();

        Evento e4 = new EventoBuilder().conId("E004").conNombre("Festival Reggaetón")
                .conCategoria("Música").conDescripcion("Los mejores artistas urbanos en un solo escenario. Bad Bunny, Karol G, Feid y más.")
                .enCiudad("Medellín").enFecha(LocalDateTime.now().plusDays(60))
                .conEstado(EstadoEvento.PUBLICADO).conRecinto(plazaToros).conPoliticas(polConcierto).build();

        Evento e5 = new EventoBuilder().conId("E005").conNombre("Concierto Sinfónico")
                .conCategoria("Música").conDescripcion("La Orquesta Filarmónica de Bogotá interpreta las más grandes sinfonías de Beethoven y Mozart.")
                .enCiudad("Bogotá").enFecha(LocalDateTime.now().plusDays(20))
                .conEstado(EstadoEvento.BORRADOR).conRecinto(teatro).conPoliticas(polTeatro).build();

        Evento e6 = new EventoBuilder().conId("E006").conNombre("Stand Up Comedy Night")
                .conCategoria("Comedia").conDescripcion("Noche de risas con los mejores comediantes del país. Prepárate para una jornada de humor imperdible.")
                .enCiudad("Bogotá").enFecha(LocalDateTime.now().plusDays(10))
                .conEstado(EstadoEvento.PUBLICADO).conRecinto(centro).conPoliticas(polConferencia).build();

        Evento e7 = new EventoBuilder().conId("E007").conNombre("Coldplay - Music of the Spheres")
                .conCategoria("Música").conDescripcion("Coldplay llega a Bogotá con su tour mundial. Una experiencia audiovisual única e irrepetible.")
                .enCiudad("Bogotá").enFecha(LocalDateTime.now().plusDays(75))
                .conEstado(EstadoEvento.PUBLICADO).conRecinto(movistarArena).conPoliticas(polConcierto).build();

        Evento e8 = new EventoBuilder().conId("E008").conNombre("Expo Tecnología 2026")
                .conCategoria("Conferencia").conDescripcion("Exposición de nuevas tecnologías, inteligencia artificial, robótica y realidad virtual.")
                .enCiudad("Medellín").enFecha(LocalDateTime.now().plusDays(35))
                .conEstado(EstadoEvento.PUBLICADO).conRecinto(centro).conPoliticas(polConferencia).build();

        // Suscribir usuarios como observadores
        e1.agregarObservador(juan);
        e1.agregarObservador(maria);
        e2.agregarObservador(ana);
        e3.agregarObservador(carlos);
        e4.agregarObservador(juan);
        e4.agregarObservador(carlos);
        e4.agregarObservador(ana);
        e7.agregarObservador(juan);
        e7.agregarObservador(maria);
        e7.agregarObservador(pedro);

        eventos.addAll(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8));

        // --- COMPRAS DEMO ---
        IEntradaFactory factory = new EntradaNumeradaFactory();
        Entrada ent1 = factory.crearEntrada(generalE, generalE.getAsientos().get(0));
        Compra c1 = new Compra("CMP-DEMO001", juan, e1);
        c1.agregarEntrada(ent1);
        c1.pagar();
        c1.pagar(); // CONFIRMADA
        compras.add(c1);
        juan.getHistorialCompras().add(c1);

        Entrada ent2 = factory.crearEntrada(palcoT, palcoT.getAsientos().get(0));
        Compra c2 = new Compra("CMP-DEMO002", maria, e2);
        c2.agregarEntrada(ent2);
        c2.pagar();
        compras.add(c2);
        maria.getHistorialCompras().add(c2);

        Entrada ent3 = factory.crearEntrada(salonA, salonA.getAsientos().get(0));
        Compra c3 = new Compra("CMP-DEMO003", carlos, e3);
        c3.agregarEntrada(ent3);
        compras.add(c3);
        carlos.getHistorialCompras().add(c3);

        System.out.println("=== Datos de prueba cargados: " + usuarios.size() + " usuarios, "
                + eventos.size() + " eventos, " + recintos.size() + " recintos, "
                + compras.size() + " compras demo ===");
    }
}
