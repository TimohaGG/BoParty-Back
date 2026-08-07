package com.bezkoder.springjwt.models;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import org.springframework.context.annotation.Bean;

import java.awt.*;


public class PdfConfig {

    BaseFont baseFont;
    Font mainFont;
    public BaseColor accentColor;
    public BaseColor accentTextColor;
    public BaseColor summaryHEaderColor;
    public PdfConfig() {
        try {
            this.baseFont =  BaseFont.createFont("fonts/Arial Unicode.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            this.mainFont = new Font(baseFont,14);
            this.accentColor = new BaseColor(250,187,7);
            this.accentTextColor = BaseColor.WHITE;
            this.summaryHEaderColor = new BaseColor(91,91,91);
        }
        catch (Exception e) {

        }


    }
    public PdfPCell defaultCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,this.mainFont));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingBottom(15);
        cell.setPaddingTop(15);
        cell.setMinimumHeight(30);
        return cell;
    }

    public PdfPCell getImageCell(Image image) {
        PdfPCell cell = new PdfPCell(image);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingBottom(10);
        cell.setPaddingTop(10);
        return cell;
    }

    public PdfPCell defaultCell(String text, int style) {
        PdfPCell cell = new PdfPCell(new Phrase(text,new Font(baseFont,14,style)));
        cell.setPaddingBottom(10);
        cell.setPaddingTop(10);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    public PdfPCell defaultCellBold(String text, BaseColor color) {
        PdfPCell cell = new PdfPCell(new Phrase(text,new Font(baseFont,14,Font.BOLD,color)));
        cell.setPaddingBottom(10);
        cell.setPaddingTop(10);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    public PdfPCell compactCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(baseFont, 11)));
        cell.setPaddingBottom(7);
        cell.setPaddingTop(7);
        cell.setPaddingLeft(6);
        cell.setPaddingRight(6);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    public PdfPCell compactCell(String text, int style) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(baseFont, 11, style)));
        cell.setPaddingBottom(7);
        cell.setPaddingTop(7);
        cell.setPaddingLeft(6);
        cell.setPaddingRight(6);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    public PdfPCell compactCellBold(String text, BaseColor color) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(baseFont, 11, Font.BOLD, color)));
        cell.setPaddingBottom(7);
        cell.setPaddingTop(7);
        cell.setPaddingLeft(6);
        cell.setPaddingRight(6);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    public PdfPCell smallCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(baseFont, 9)));
        cell.setPaddingBottom(8);
        cell.setPaddingTop(8);
        cell.setPaddingLeft(6);
        cell.setPaddingRight(6);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }
}
