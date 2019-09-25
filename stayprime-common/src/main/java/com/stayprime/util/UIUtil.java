/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.util;

import com.ezware.common.Strings;
import com.ezware.dialog.task.TaskDialog;
import com.ezware.dialog.task.TaskDialog.StandardCommand;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Window;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author benjamin
 */
public class UIUtil {

    public static void calcColumnWidths(JTable table, int... fill) {
        JTableHeader header = table.getTableHeader();
        TableCellRenderer defaultHeaderRenderer = null;

        if (header != null)
            defaultHeaderRenderer = header.getDefaultRenderer();

        TableColumnModel columns = table.getColumnModel();
        //TableModel data = table.getModel();
        int margin = columns.getColumnMargin(); // only JDK1.3
        int rowCount = table.getRowCount();//data.getRowCount();
        int totalWidth = 0;

        for (int i = columns.getColumnCount() - 1; i >= 0; --i) {
            TableColumn column = columns.getColumn(i);
            int columnIndex = column.getModelIndex();
            int width = -1;

            TableCellRenderer h = column.getHeaderRenderer();

            boolean inFillCols = false;
            for (int col : fill) {
                if (col == i)
                    inFillCols = true;
            }

            if (!inFillCols) {
                if (h == null) {
                    h = defaultHeaderRenderer;
                }

                if (h != null) {// Not explicitly impossible
                    Component c = h.getTableCellRendererComponent(table, column
                            .getHeaderValue(), false, false, -1, i);
                    width = c.getPreferredSize().width;
                }

                for (int row = rowCount - 1; row >= 0; --row) {
                    TableCellRenderer r = table.getCellRenderer(row, i);
                    Component c = r.getTableCellRendererComponent(table, table//data
                            .getValueAt(row, i), false, false, row, i);
                    width = Math.max(width, c.getPreferredSize().width);
                }

                if (width >= 0) {
                    column.setPreferredWidth(width + margin); // <1.3: without margin
                    totalWidth += width + margin;
                }
                //else
                //totalWidth += column.getPreferredWidth();
            }

        }

        if (fill.length > 0) {
            int fillWidth = (int) (0.8f * (table.getSize().width - totalWidth) / fill.length + 1);
            for (int i : fill) {
                columns.getColumn(i).setPreferredWidth(fillWidth);
            }
        }
    }

    public static Frame getParentFrame(Component comp) {
        Component parent = comp;
        while ((parent = parent.getParent()) != null) {
            if (parent instanceof Frame)
                return (Frame) parent;
        }
        return null;
    }

    public static String wordWrapString(String string, int lineCharsThreshold) {
        int lineLength = 0;
        StringBuilder wrap = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (lineLength >= lineCharsThreshold && Character.isSpaceChar(string.charAt(i)))
                wrap.append("\n");
            else
                wrap.append(string.charAt(i));
        }

        return wrap.toString();
    }

    public static void showDetailedTaskDialog(Window parent, String instruction, String message, String details, String title, Icon icon, boolean expanded) {

        TaskDialog dlg = new TaskDialog(parent, title);

        boolean noMessage = instruction == null || Strings.isEmpty(instruction);

        dlg.setInstruction(noMessage ? "" : instruction);
        dlg.setText(message == null || Strings.isEmpty(message) ? "Details:" : message);

        dlg.setIcon(icon);
        dlg.setCommands(StandardCommand.CANCEL.derive(TaskDialog.makeKey("Close")));

        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setFont(UIManager.getFont("Label.font"));
        text.setText(details);
        text.setCaretPosition(0);

        JScrollPane scroller = new JScrollPane(text);
        scroller.setPreferredSize(new Dimension(400, 200));
        dlg.getDetails().setExpandableComponent(scroller);
        dlg.getDetails().setExpanded(expanded);

        dlg.setResizable(true);

            // Issue 22: Exception can be printed by user if required
        // ex.printStackTrace();
        dlg.setVisible(true);

    }

    public static Paint getCheckerPaint(Color c1, Color c2, int size) {
        return new TexturePaint(createCheckerTexture(c1, c2, size),new Rectangle(0,0,size,size));
    }

    public static BufferedImage createCheckerTexture(Color c1, Color c2, int size) {
        BufferedImage img = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        
        try {
            g.setColor(c1);
            g.fillRect(0, 0, size, size);
            g.setColor(c2);
            g.fillRect(0, 0, size / 2, size / 2);
            g.fillRect(size / 2, size / 2, size / 2, size / 2);
        } finally {
            g.dispose();
        }

        return img;
    }

    public static void setColumnWidths(JTable table, int ... widths) {
        TableColumnModel columns = table.getColumnModel();
        int tableWidth = table.getWidth();
        float totalWidth = 0;
        for (int w : widths) {
            totalWidth += w;
        }

        for (int i = 0; i < columns.getColumnCount() && i < widths.length; i++) {
            TableColumn column = columns.getColumn(i);
            column.setPreferredWidth(Math.round(widths[i]/totalWidth*tableWidth));
        }
    }

}
