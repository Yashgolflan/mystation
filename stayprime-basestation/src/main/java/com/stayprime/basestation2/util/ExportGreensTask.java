/*
 * 
 */
package com.stayprime.basestation2.util;

import com.aeben.elementos.mapview.DrawableMapImage;
import com.aeben.elementos.mapview.MapImageRenderer;
import com.aeben.golfclub.CourseDefinition;
import com.aeben.golfclub.DrawableCourseImage;
import com.aeben.golfclub.GolfClub;
import com.aeben.golfclub.HoleDefinition;
import com.ezware.dialog.task.TaskDialogs;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.renderers.DashboardObjectRenderer;
import com.stayprime.geo.MapUtils;
import com.stayprime.imageview.CachedImageConverter;
import com.stayprime.view.objects.Alignment;
import com.stayprime.view.objects.BasicObjectRenderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jdesktop.application.Task;

/**
 *
 * @author benjamin
 */
public class ExportGreensTask extends Task<Object, Void> {
    private static Font font = new Font("Sans", Font.BOLD, 42);
    private static BasicStroke stroke = new BasicStroke(3);
    private static PDFont pdfFont = PDType1Font.HELVETICA_BOLD;

    private final GolfClub gc;
    private final Logger log;

    private int horiz;
    private int vert;
    private File file;
    private DrawableCourseImage flag;

    public ExportGreensTask(BaseStation2App app, GolfClub gc, Logger log) {
        this(app, gc, log, 3, 6, new File("Greens.pdf"), false);
    }

    public ExportGreensTask(BaseStation2App app, GolfClub gc, Logger log, int horiz, int vert, File file, boolean showFlag) {
        super(app);
        this.gc = gc;
        this.log = log;
        this.horiz = horiz;
        this.vert = vert;
        this.file = file;
        if (showFlag) {
            flag = app.getDashboardManager().createDefaultPinflag();
        }
    }

    @Override
    protected Object doInBackground() {
        List<CourseDefinition> courses = gc.getCourses();

        try {
            exportCoursesToPdf(courses, horiz, vert, file, flag);
        }
        catch (Exception ex) {
            if (log != null) {
                log.warn("Error exporting Green image: " + ex);
            }
            else {
                return ex;
            }
        }
        return null;
    }

    @Override
    protected void succeeded(Object result) {
        if (result instanceof Exception) {
            Exception ex = (Exception) result;
            TaskDialogs.showException(ex);
        }
    }

    public static void exportCoursesToPdf(List<CourseDefinition> courses, int horiz, int vert, File file, DrawableCourseImage flag) throws IOException {
        BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        DashboardObjectRenderer r = new DashboardObjectRenderer(new CachedImageConverter());
        r.setRenderGreenPoints(true);
        r.setRenderGreenGrid(true);
        PDDocument pdf = new PDDocument();

        for (CourseDefinition course : courses) {
            PDPage page = new PDPage(PDRectangle.A4);
            PDRectangle box = page.getCropBox();
            pdf.addPage(page);
            PDPageContentStream pdfc = new PDPageContentStream(pdf, page);
            HoleDefinition[] holes = course.getHoles();

            float pw = box.getWidth();
            float ph = box.getHeight();

            float boxX = box.getLowerLeftX();
            float boxY = box.getUpperRightY();

            float margin = .02f*pw;
            float gap = .01f*pw;

            float roww = (pw - 2*margin - (horiz-1) * gap) / horiz;
            float rowh = (ph - 4*margin - (vert-1) * gap) / vert;
            int xi = 0;
            int yi = 0;

            String text = course.getCourseName();
            writeTextCentered(text, pdfc, pw, ph, margin);

            for (int i = 0; i < holes.length; i++) {
                HoleDefinition hole = holes[i];
                createGreenImage(hole, r, image, flag);
                float size = Math.min(roww, rowh);
                float xpad = (roww - size) / 2f;
                float ypad = (rowh - size) / 2f;
                float x = boxX + margin + xi*(gap+roww) + xpad;
                float y = boxY - margin*3 - yi*(gap+rowh) - ypad - rowh;

                boolean isLastRow = i >= holes.length / horiz * horiz;
                if (isLastRow) {
                    int emptyPlaces = horiz - holes.length % horiz;
                    x += emptyPlaces * (gap+roww) / 2f;
                }
                drawPdfImage(pdf, pdfc, image, x, y, size, size);
                xi++;
                if (xi >= horiz) {
                    xi = 0;
                    yi++;
                }
                if (yi >= vert && i < holes.length - 1) {
                    yi = 0;
                    pdfc.close();
                    page = new PDPage(PDRectangle.A4);
                    pdf.addPage(page);
                    pdfc = new PDPageContentStream(pdf, page);
                    boxY = page.getCropBox().getUpperRightY();
                }
            }
            pdfc.close();
        }
        pdf.save(file);
        pdf.close();
    }

    private static void writeTextCentered(String text, PDPageContentStream pdfc, float pw, float ph, float margin) throws IOException {
        int fontSize = 12;
        float titleWidth = pdfFont.getStringWidth(text) / 1000 * fontSize;
        float titleHeight = pdfFont.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        pdfc.beginText();
        pdfc.setFont(pdfFont, fontSize);
        pdfc.moveTextPositionByAmount((pw - titleWidth) / 2, ph - margin - titleHeight);
        pdfc.drawString(text);
        pdfc.endText();
    }

    public static void createGreenImage(HoleDefinition h, DashboardObjectRenderer r, BufferedImage canvas, DrawableCourseImage flag) throws IOException {
        DrawableMapImage ref = CourseRenderUtil.createGreenViewReferenceMapImage(h, canvas.getHeight());
        MapBasedProjection p = new MapBasedProjection();
        p.setMap(ref);
        p.setImageRectangle(new Rectangle2D.Double(canvas.getWidth() / 2d - 50, 0, 100, canvas.getHeight()));
        BufferedImage holeImage = ImageIO.read(new File(h.map.getImageAddress()));
        DrawableMapImage map = new DrawableMapImage(h.map);
        map.setCachedImage(MapUtils.getNormalizedMapImage(map, holeImage));

        Graphics2D g = canvas.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        MapImageRenderer.renderMapImage(map, g, p, null, null);
        r.renderCourseObject(h.green, g, p);
        if (flag != null && h.pinLocation != null) {
            flag.diagonalSizeInMeters = 5f;
            flag.minDiagonalPixelSize = 50f;
            flag.coordinates = h.pinLocation.coordinates;
            r.renderCourseObject(flag, g, p);
        }

        g.setColor(Color.black);
        g.setStroke(stroke);
        g.drawRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.fillRect(0, 0, 60, 60);
        g.setColor(Color.white);
        String num = Integer.toString(h.number);
        BasicObjectRenderer.renderTextRect(g, 30, 30, num, font, Color.white, null, null, null, Alignment.MID_CENTER);
    }

    private static void drawPdfImage(PDDocument pdf, PDPageContentStream pdfc,
            BufferedImage image, float x, float y, float w, float h) throws IOException {
        PDImageXObject jpg = JPEGFactory.createFromImage(pdf, image);
        pdfc.drawImage(jpg, x, y, w, h);
    }

    private static void exportImage(HoleDefinition h, BufferedImage canvas) throws IOException {
        ImageIO.write(canvas, "jpg", new File(h.course.getName() + " " + h.number + ".jpg"));
    }

}
