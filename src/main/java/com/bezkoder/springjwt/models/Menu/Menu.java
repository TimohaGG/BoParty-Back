package com.bezkoder.springjwt.models.Menu;

import com.bezkoder.springjwt.models.PdfConfig;
import com.bezkoder.springjwt.models.Position.PositionAmount;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.response.Menu.MenuCardResponse;
import com.bezkoder.springjwt.payload.response.Menu.MenuResponse;
import com.bezkoder.springjwt.security.Exceptions.PdfGenerateException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Menu(){
        date = LocalDate.now().atStartOfDay();
        client = "";
        guestsAmount = 0;
        duration = 0;
        format = "Бокси";
        phone = "0688714410";
        id = 0L;
        positionsAmount = new ArrayList<>();
        isPayed = false;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime date;


    @Column(nullable = true)
    private String client;
    @Column(nullable = true)
    private int guestsAmount;
    @Column(nullable = true)
    private int duration;
    @Column(nullable = true)
    private String format;
    @ColumnDefault("0688714410")
    private String phone;

    private int totalPrice;


    private boolean needsTax = false;

    private double taxPercentage = 0.06D;
    public double getTaxPercentageCalc() {
        double totalPrice = getPrice() + getAdditionalInfo().stream().mapToInt(MenuAdditionalInfo::getPrice).sum();
        return Math.floor(totalPrice - totalPrice * (1-taxPercentage)) ;
    }


    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},orphanRemoval = true)
    private List<PositionAmount> positionsAmount = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER,cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},orphanRemoval = true)
    private List<MenuAdditionalInfo> additionalInfo = new  ArrayList<>();


    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ShoppingList shoppingList;
    @ColumnDefault("false")
    private boolean temporary;

    @ColumnDefault("false")
    private boolean isPayed;


    public String getDateFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if(date!=null){
            return date.format(formatter);
        }
        else{
            return "";
        }

    }

    public int getPrice(){

        return positionsAmount.stream()
                .mapToInt(x -> (int)x.getPosition().getPrice() * x.getAmount())
                .sum();
    }

    public int getTotalPrice(){
        int sum = getPrice() + getAdditionalInfo().stream().mapToInt(MenuAdditionalInfo::getPrice).sum();
        if(needsTax){
            sum += getTaxPercentageCalc();
        }
        return sum;
    }


    public List<PositionAmount> getPositionsAmount() {
        return positionsAmount.stream().sorted(Comparator.comparing(x->x.getPosition().getCategory().getId())).toList();
    }


    public void addPosition(PositionAmount position) {
        positionsAmount.add(position);
    }

    public int getOnOnePerson(){
        if(guestsAmount==0){
            return 0;
        }
        return (int)getPrice() / guestsAmount;
    }


    public void removePosition(PositionAmount positionAmount) {
        this.positionsAmount.remove(positionAmount);
        positionAmount.setOrder(null);
    }

    public static MenuCardResponse toCardDto(Menu order){
        return MenuCardResponse.builder()
                .date(order.getDate())
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .client(order.getClient())
                .isPayed(order.isPayed())
                .build();
    }

    public static MenuResponse toDto(Menu order){
        System.out.println(order.getTotalPrice());
        return MenuResponse.builder()
                .id(order.getId())
                .date(order.getDate())
                .client(order.getClient())
                .guestsAmount(order.getGuestsAmount())
                .duration(order.getDuration())
                .format(order.getFormat())
                .phone(order.getPhone())
                .totalPrice(order.getTotalPrice())
                .isPayed(order.isPayed())
                .positions(order.getPositionsAmount().stream().map(PositionAmount::toDto).toList())
                .additionalInfo(order.getAdditionalInfo().stream().map(MenuAdditionalInfo::toResponse).toList())
                .build();
    }

    public void removeInfo(MenuAdditionalInfo info) {
        this.additionalInfo.remove(info);
        info.setOrder(null);
    }

    public void addInfo(MenuAdditionalInfo el) {
        this.additionalInfo.add(el);
    }

    public ByteArrayOutputStream toPdf(PdfConfig pdfConfig) {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(this.generateHeaderPdf(pdfConfig));

            document.close();
            return out;

        }catch (Exception e) {
            throw new PdfGenerateException("Can't generate PDF!");
        }
    }

    private PdfPTable generateHeaderPdf(PdfConfig config){
        float[] cols = {1f,1f,1f};
        PdfPTable table = new PdfPTable(cols);
        table.setWidthPercentage(100);

        table.addCell(config.defaultCell("Замовник"));
        table.addCell(config.defaultCell(this.getClient()));
        table.addCell(this.getLogoImage());

        table.addCell(config.defaultCell("Дата"));
        table.addCell(config.defaultCell(this.getClient()));


        table.addCell(config.defaultCell("Початок заходу"));
        table.addCell(config.defaultCell(this.getDate().format(DateTimeFormatter.ofPattern("HH:mm"))));
        table.addCell(config.defaultCell("Тривалість"));
        table.addCell(config.defaultCell(Integer.toString(this.getDuration())));
        table.addCell(config.defaultCell("Кі-сть запрошених"));
        table.addCell(config.defaultCell(Integer.toString(this.getGuestsAmount())));
        table.addCell(config.defaultCell("Формат заходу"));
        table.addCell(config.defaultCell(this.getFormat()));
        table.addCell(config.defaultCell("Телефон менеджера"));
        table.addCell(config.defaultCell(this.getPhone()));


        return table;
    }

    private PdfPCell getLogoImage(){
        try{
            ClassPathResource classpath = new ClassPathResource("static/asserts/img/logo.png");
            byte[] imageBytes;
            try (InputStream inputStream = classpath.getInputStream()) {
                imageBytes = inputStream.readAllBytes();
            }
            Image image = Image.getInstance(imageBytes);
            image.scaleToFit(150,150);
            PdfPCell cell = new PdfPCell(image);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cell.setRowspan(7);
            return cell;
        }catch (Exception e){
            throw new PdfGenerateException("Can't set logo image");
        }
    }
}
