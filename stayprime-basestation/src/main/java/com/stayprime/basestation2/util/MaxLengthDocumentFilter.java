/*
 * 
 */
package com.stayprime.basestation2.util;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author benjamin
 */
public class MaxLengthDocumentFilter extends DocumentFilter {
    private final AbstractDocument doc;
    private int maxChars = 0;

    public MaxLengthDocumentFilter(AbstractDocument doc, int maxChars) {
        this.doc = doc;
        this.maxChars = maxChars;
    }

    public void setMaxChars(int maxChars) {
        this.maxChars = maxChars;
    }

    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        replace(fb, offset, 0, text, attr);
    }

    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offset, int replaceLength, String text, AttributeSet attrs) throws BadLocationException {
        if (maxChars == 0) {
            super.replace(fb, offset, offset, text, attrs);
        }
        else {
            int docLength = doc.getLength();
            int textLength = text == null? 0 : text.length();
            int lengthChange = textLength - replaceLength;
            int newLength = docLength + lengthChange;
            if (newLength <= maxChars) {
                super.replace(fb, offset, replaceLength, text, attrs);
            }
            else {
                int substringLenght = maxChars - docLength + replaceLength;
                if (substringLenght > 0) {
                    String substr = text == null? null : text.substring(0, substringLenght);
                    super.replace(fb, offset, replaceLength, substr, attrs);
                }
            }
        }
    }
    
}
