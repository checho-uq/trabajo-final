package com.tickethub.model.proxy;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Motor de renderizado PDF profesional.
 * Genera reportes multi-página corporativos apoyado por JFreeChart para gráficos internos.
 */
public class PDFRenderer {

    // Márgenes y configuraciones globales
    private static final float MARGIN = 40;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;

    // Colores PDF corporativos (Decodificados del hex en Config)
    private Color colorPrimary;
    private Color colorSecondary;
    private Color colorAccent;

    public void renderizar(ReporteData data, AnalisisEstadistico analisis, ReporteConfig config, String path) {
        try (PDDocument document = new PDDocument()) {
            
            // Parsings de colores
            colorPrimary = Color.decode(config.getColorPrimario());
            colorSecondary = Color.decode(config.getColorSecundario());
            colorAccent = Color.decode(config.getColorAccento());

            // --- Página 1: Resumen y Gráficos ---
            PDPage page1 = new PDPage(PDRectangle.A4);
            document.addPage(page1);

            try (PDPageContentStream cs = new PDPageContentStream(document, page1)) {
                float yPostHeader = drawHeader(cs, config.getTituloPersonalizado() != null ? config.getTituloPersonalizado() : data.getTituloReporte(), data);
                float yPostKPI = drawKPICards(cs, data, yPostHeader - 20);

                float yCursor = yPostKPI - 30;

                // Sección Analítica Automática
                if (config.isIncluirAnalisis() && analisis != null) {
                    yCursor = drawAnalisisSection(cs, document, page1, analisis, yCursor);
                }

                // Gráficos (Si cabe en la página o en nueva página internamente manejado o forzando si no hay espacio)
                if (config.isIncluirGraficos()) {
                    yCursor = drawGraficos(cs, document, page1, data, yCursor - 20);
                }
            }

             // --- Páginas posteriores: Tablas ---
             if (config.isIncluirTablaCompras() && !data.getTablaCompras().isEmpty()) {
                 drawTablaCompras(document, data);
             }

            document.save(new File(path));
            System.out.println("Reporte PDF generado exitosamente: " + path);
        } catch (Exception e) {
            System.err.println("Error fatal al generar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private float drawHeader(PDPageContentStream cs, String titulo, ReporteData data) throws IOException {
        float y = PAGE_HEIGHT - MARGIN - 10;

        // Banner azul
        cs.setNonStrokingColor(colorPrimary);
        cs.addRect(0, y - 15, PAGE_WIDTH, 65);
        cs.fill();

        // Título del reporte (blanco)
        cs.setNonStrokingColor(Color.WHITE);
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 22);
        cs.newLineAtOffset(MARGIN, y + 20);
        cs.showText(titulo);
        cs.endText();

        // Subtítulo
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 10);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("Plataforma Logística - Generado el " + data.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        cs.endText();

        return y - 35; // yCursor despues del header
    }

    private float drawKPICards(PDPageContentStream cs, ReporteData data, float startY) throws IOException {
        float cardWidth = (CONTENT_WIDTH - (3 * 10)) / 4; // 4 cards con 10px spacing
        float cardHeight = 60;
        float currentX = MARGIN;

        // Card 1: Ingresos
        drawSoloCard(cs, currentX, startY, cardWidth, cardHeight, "INGRESOS TOTALES", "$" + String.format("%,.0f", data.getTotalIngresos()));
        currentX += cardWidth + 10;

        // Card 2: Compras
        drawSoloCard(cs, currentX, startY, cardWidth, cardHeight, "TRANSACCIONES", String.valueOf(data.getTotalCompras()));
        currentX += cardWidth + 10;
        
        // Card 3: Ticket Medio
        drawSoloCard(cs, currentX, startY, cardWidth, cardHeight, "TICKET MEDIO", "$" + String.format("%,.0f", data.getTicketPromedio()));
        currentX += cardWidth + 10;

        // Card 4: Tasa Cancel.
        drawSoloCard(cs, currentX, startY, cardWidth, cardHeight, "TASA CANCELACIÓN", String.format("%.1f%%", data.getTasaCancelacion()));

        return startY - cardHeight;
    }

    private void drawSoloCard(PDPageContentStream cs, float x, float y, float width, float height, String label, String value) throws IOException {
        // Fondo gris claro
        cs.setNonStrokingColor(new Color(245, 246, 248));
        cs.addRect(x, y - height, width, height);
        cs.fill();

        // Borde izquierdo de color acento
        cs.setNonStrokingColor(colorAccent);
        cs.addRect(x, y - height, 4, height);
        cs.fill();

        // Texto label
        cs.setNonStrokingColor(new Color(100, 100, 100));
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 8);
        cs.newLineAtOffset(x + 10, y - 15);
        cs.showText(label);
        cs.endText();

        // Texto valor
        cs.setNonStrokingColor(Color.BLACK);
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
        cs.newLineAtOffset(x + 10, y - 40);
        cs.showText(value);
        cs.endText();
    }

    private float drawAnalisisSection(PDPageContentStream cs, PDDocument doc, PDPage page, AnalisisEstadistico analisis, float startY) throws IOException {
        float y = startY;

        // Título de Sección
        y = drawSectionTitle(cs, "RESUMEN EJECUTIVO E INSIGHTS (Análisis Estadístico Local)", y);

        // Resume text
        cs.setNonStrokingColor(Color.DARK_GRAY);
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        y = drawWrappedText(cs, analisis.getResumenEjecutivo(), MARGIN, y, CONTENT_WIDTH, 14);

        y -= 15;

        // Insights Bullets
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        cs.beginText();
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("Hallazgos Clave:");
        cs.endText();
        y -= 15;

        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
        for(String insight : analisis.getInsights()){
             // Dibujar viñeta
             cs.setNonStrokingColor(colorSecondary);
             cs.addRect(MARGIN + 5, y - 3, 4, 4);
             cs.fill();
             cs.setNonStrokingColor(Color.DARK_GRAY);
             
             y = drawWrappedText(cs, insight, MARGIN + 15, y, CONTENT_WIDTH - 20, 12);
        }
        
        y -= 10;
        
        // Recomendaciones
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        cs.setNonStrokingColor(Color.BLACK);
        cs.beginText();
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("Recomendaciones Estratégicas:");
        cs.endText();
        y -= 15;

        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
        for(String rec : analisis.getRecomendaciones()){
             cs.setNonStrokingColor(colorAccent);
             cs.addRect(MARGIN + 5, y - 3, 4, 4);
             cs.fill();
             cs.setNonStrokingColor(Color.DARK_GRAY);
             
             y = drawWrappedText(cs, rec, MARGIN + 15, y, CONTENT_WIDTH - 20, 12);
        }

        return y;
    }

    private float drawGraficos(PDPageContentStream cs, PDDocument doc, PDPage page, ReporteData data, float startY) throws IOException {
        
        // --- 1. Gráfico de Barras: Ventas por Evento ---
        if(!data.getVentasPorEvento().isEmpty()) {
            DefaultCategoryDataset datasetBar = new DefaultCategoryDataset();
            for (Map.Entry<String, Double> entry : data.getVentasPorEvento().entrySet()) {
                datasetBar.addValue(entry.getValue(), "Ventas", entry.getKey());
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Ingresos por Evento ($)", "", "",
                    datasetBar, PlotOrientation.VERTICAL, false, true, false);
            barChart.setBackgroundPaint(Color.WHITE);
            // Color de la serie
            barChart.getCategoryPlot().getRenderer().setSeriesPaint(0, colorAccent);

            // Generar imagen PNG del chart
            BufferedImage imgBar = barChart.createBufferedImage(400, 250);
            PDImageXObject pdfImgBar = LosslessFactory.createFromImage(doc, imgBar);
            
            // Dibujar
            float imgW = 250;
            float imgH = 150;
            
            if (startY - imgH < MARGIN) {
               // Aquí habría que agregar page break, por simpleza ignoramos si no cabe en el ejemplo.
            }
            
            cs.drawImage(pdfImgBar, MARGIN, startY - imgH, imgW, imgH);
            
            // --- 2. Gráfico Pie: Ocupación / O Estados ---
            DefaultPieDataset datasetPie = new DefaultPieDataset();
            for (Map.Entry<String, Double> e : data.getDistribucionEstados().entrySet()) {
                datasetPie.setValue(e.getKey(), e.getValue());
            }

            JFreeChart pieChart = ChartFactory.createPieChart("Eventos por Estado", datasetPie, true, true, false);
            pieChart.setBackgroundPaint(Color.WHITE);
            
            BufferedImage imgPie = pieChart.createBufferedImage(300, 250);
            PDImageXObject pdfImgPie = LosslessFactory.createFromImage(doc, imgPie);

            cs.drawImage(pdfImgPie, MARGIN + imgW + 10, startY - imgH, 250, 150);
            
            return startY - imgH - 20;
        }
        
        return startY;
    }


    private void drawTablaCompras(PDDocument doc, ReporteData data) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            
            float y = drawHeader(cs, "Detalle de Transacciones", data);
            y -= 20;

            String[] headers = {"ID Compra", "Cliente", "Evento", "Total ($)", "Estado", "Fecha"};
            float[] colWidths = {80, 120, 130, 60, 60, 80}; // Total 530
            
            // Draw Headers
            cs.setNonStrokingColor(colorPrimary);
            cs.addRect(MARGIN, y - 15, CONTENT_WIDTH, 20);
            cs.fill();
            
            cs.setNonStrokingColor(Color.WHITE);
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9);
            float startX = MARGIN + 5;
            for(int i=0; i<headers.length; i++){
                cs.beginText();
                cs.newLineAtOffset(startX, y - 11);
                cs.showText(headers[i]);
                cs.endText();
                startX += colWidths[i];
            }
            
            y -= 15;
            
            // Restablecer fuente para datos
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
            
            boolean zebra = false;
            for (String[] row : data.getTablaCompras()) {
                if (y < MARGIN + 20) { // Page break simplificado (idealmente recursivo/loop abriendo nueva pagina)
                    cs.setNonStrokingColor(Color.RED);
                    cs.beginText();
                    cs.newLineAtOffset(MARGIN, MARGIN);
                    cs.showText("--- Resultados truncados por límite de página ---");
                    cs.endText();
                    break; 
                }

                if (zebra) {
                    cs.setNonStrokingColor(new Color(245, 245, 245));
                    cs.addRect(MARGIN, y - 12, CONTENT_WIDTH, 15);
                    cs.fill();
                }
                zebra = !zebra;

                cs.setNonStrokingColor(Color.DARK_GRAY);
                startX = MARGIN + 5;
                for(int i=0; i<row.length; i++){
                    cs.beginText();
                    cs.newLineAtOffset(startX, y - 8);
                    
                    // truncar si es muy largo
                    String text = row[i];
                    if(text.length() > 30 && i==1) text = text.substring(0, 27) + "...";
                    if(text.length() > 30 && i==2) text = text.substring(0, 27) + "...";

                    cs.showText(text);
                    cs.endText();
                    startX += colWidths[i];
                }
                
                y -= 15;
            }
        }
    }


    // --- Utils ---

    private float drawSectionTitle(PDPageContentStream cs, String title, float y) throws IOException {
        cs.setNonStrokingColor(colorPrimary);
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(title);
        cs.endText();
        
        // Underline
        cs.addRect(MARGIN, y - 5, 200, 1);
        cs.fill();
        return y - 20;
    }

    /** Simple word wrap text render */
    private float drawWrappedText(PDPageContentStream cs, String text, float x, float startY, float width, float leading) throws IOException {
        if(text == null || text.trim().isEmpty()) return startY;
        
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        int fontSize = 9; // Hardcoded default por simplicidad para calcular wrap bounds
        
        String[] lines = text.split("\n");
        float y = startY;
        
        for (String lineText : lines) {
             String[] words = lineText.split(" ");
             StringBuilder line = new StringBuilder();

             for (String word : words) {
                 StringBuilder testLine = new StringBuilder(line);
                 if (testLine.length() > 0) testLine.append(" ");
                 testLine.append(word);

                 float strWidth = font.getStringWidth(testLine.toString()) / 1000 * fontSize;
                 
                 if (strWidth > width && line.length() > 0) {
                     cs.beginText();
                     cs.newLineAtOffset(x, y);
                     cs.showText(line.toString());
                     cs.endText();
                     line = new StringBuilder(word);
                     y -= leading;
                 } else {
                     line = testLine;
                 }
             }

             if (line.length() > 0) {
                 cs.beginText();
                 cs.newLineAtOffset(x, y);
                 cs.showText(line.toString());
                 cs.endText();
                 y -= leading;
             }
        }
        return y;
    }
}
