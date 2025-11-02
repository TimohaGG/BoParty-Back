package com.bezkoder.springjwt.models;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import org.springframework.context.annotation.Bean;

import java.awt.*;


public class PdfConfig {

    BaseFont baseFont;
    Font mainFont;
    public PdfConfig() {
        try {
            this.baseFont =  BaseFont.createFont("fonts/Arial Unicode.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            this.mainFont = new Font(baseFont,14);
        }
        catch (Exception e) {

        }

    }
    public PdfPCell defaultCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,this.mainFont));
        cell.setPaddingBottom(10);
        cell.setPaddingTop(10);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    public PdfPCell defaultCell(String text, int style) {
        PdfPCell cell = new PdfPCell(new Phrase(text,new Font(baseFont,14,style)));
        cell.setPaddingBottom(10);
        cell.setPaddingTop(10);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}
