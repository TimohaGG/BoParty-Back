package com.bezkoder.springjwt.models.Menu;

import com.bezkoder.springjwt.models.PdfConfig;
import com.bezkoder.springjwt.models.Position.IngredientAmount;
import com.bezkoder.springjwt.models.Position.PositionAmount;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.response.Menu.MenuCardResponse;
import com.bezkoder.springjwt.payload.response.Menu.MenuResponse;
import com.bezkoder.springjwt.payload.response.Menu.MinMenuResp;
import com.bezkoder.springjwt.payload.response.Positions.PositionAmountResponse;
import com.bezkoder.springjwt.security.Exceptions.PdfGenerateException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itextpdf.text.*;
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
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
        duration = "";
        format = "Бокси";
        phone = "0688714410";
        deliveryType = "самовивіз";
        deliveryAddress = "";
        orderType = "бокси";
        needsWaiter = false;
        prepayment = 0;
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
    private String duration;
    @Column(nullable = true)
    private String format;
    @ColumnDefault("0688714410")
    private String phone;
    @Column(nullable = true)
    private String deliveryType;
    @Column(nullable = true, length = 2000)
    private String deliveryAddress;
    @Column(nullable = true)
    private String orderType;
    @ColumnDefault("false")
    private boolean needsWaiter;
    private double prepayment;
    private double totalPrice;

    private int sortingOrder = 0;
    private boolean needsTax = false;
    private double taxPercentage = 10;

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
    @ColumnDefault("false")
    private boolean govTax;
    private double govTaxAmount;

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
        double menuPrice = getPrice();
        if(needsTax){
            menuPrice =  menuPrice * (1 + taxPercentage / 100);
        }
        menuPrice+=getAdditionalInfoPrice();
        if(govTax)
            menuPrice = menuPrice / ((100-govTaxAmount) * 0.01);
        return (int)Math.round(menuPrice);
    }

    public int getRemainingToPay(){
        return (int)Math.max(getTotalPrice() - Math.round(prepayment), 0);
    }

    public int getServingPrice(){
        double menuPrice = getPrice();
        double withServing =  menuPrice * (1 + taxPercentage / 100);
        return (int)Math.round((withServing - menuPrice));
    }

    public int getGovTaxPrice(){
        double menuPrice = getPrice();
        if(needsTax){
            menuPrice =  menuPrice * (1 + taxPercentage / 100);
        }
        menuPrice+=getAdditionalInfoPrice();
        double withGovTax = menuPrice / ((100-govTaxAmount) * 0.01);
        return (int)Math.round((withGovTax - menuPrice));
    }

    public double getAdditionalInfoPrice(){
        if(additionalInfo !=null)
            return getAdditionalInfo().stream().mapToInt(MenuAdditionalInfo::getPrice).sum();
        else
            return 0;
    }

    public double getTaxPercentageCalc() {
        double totalPrice = getPrice();
        return Math.floor(totalPrice * (1 + taxPercentage/100) - totalPrice) ;
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
                .temporary(order.isTemporary())
                .build();
    }

    public static MenuResponse toDto(Menu order){
        return MenuResponse.builder()
                .id(order.getId())
                .date(order.getDate())
                .client(order.getClient())
                .guestsAmount(order.getGuestsAmount())
                .duration(order.getDuration())
                .format(order.getFormat())
                .phone(order.getPhone())
                .deliveryType(order.getDeliveryType())
                .deliveryAddress(order.getDeliveryAddress())
                .orderType(order.getOrderType())
                .needsWaiter(order.isNeedsWaiter())
                .prepayment(order.getPrepayment())
                .totalPrice(order.getTotalPrice())
                .isPayed(order.isPayed())
                .positions(order.getPositionsAmount().stream().map(PositionAmount::toDto).sorted(Comparator.comparing(PositionAmountResponse::getInMenuOrder)).toList())
                .additionalInfo(order.getAdditionalInfo().stream().map(MenuAdditionalInfo::toResponse).toList())
                .serving(order.needsTax)
                .taxAmount(order.getTaxPercentage())
                .govTax(order.isGovTax())
                .govTaxAmount(order.getGovTaxAmount())
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
            document.add(this.generatePositionsPdf(pdfConfig));
            document.add(this.generateSummaryPdf(pdfConfig));
            document.close();
            return out;
        }catch (Exception e) {
            throw new PdfGenerateException("Can't generate PDF!");
        }
    }

    public ByteArrayOutputStream toShoppingListPdf(PdfConfig pdfConfig) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(this.generateHeaderPdf(pdfConfig));
            document.add(this.generateShoppingPositionsPdf(pdfConfig));
            document.add(this.generateSummaryPdf(pdfConfig));
            document.close();
            return out;
        } catch (Exception e) {
            throw new PdfGenerateException("Can't generate shopping list PDF!");
        }
    }

    private Element generateShoppingPositionsPdf(PdfConfig pdfConfig) {
        float[] cols = {3f, 3f, 1f, 1f, 1f};
        PdfPTable table = new PdfPTable(cols);
        table.setWidthPercentage(100);
        table.addCell(this.generatePositionsHeader(pdfConfig));
        this.generateShoppingPositionsHeaderDescription(pdfConfig, table);

        List<PositionAmount> sortedPositions = new ArrayList<>(this.positionsAmount);
        sortedPositions.sort(Comparator.comparingInt(PositionAmount::getInMenuOrder));

        sortedPositions.forEach(pos -> {
            if (pos.getTitle() != null && !pos.getTitle().isBlank()) {
                PdfPCell cell = pdfConfig.defaultCell(pos.getTitle());
                cell.setColspan(5);
                table.addCell(cell);
            }

            table.addCell(pdfConfig.defaultCell(pos.getPosName()));
            table.addCell(pdfConfig.smallCell(buildShoppingIngredientsText(pos)));
            table.addCell(pdfConfig.defaultCell(Integer.toString((int) pos.getPosition().getWeight())));
            table.addCell(pdfConfig.defaultCell(Integer.toString(pos.getAmount())));
            table.addCell(pdfConfig.defaultCell(Integer.toString((int) pos.getPosition().getPrice())));
        });

        return table;
    }

    private String formatPdfAmount(double amount) {
        if (Math.abs(amount - Math.rint(amount)) < 0.0001d) {
            return Integer.toString((int) Math.rint(amount));
        }
        return NumberFormat.getInstance(Locale.US).format(amount).replace(",", " ");
    }

    private Element generatePositionsPdf(PdfConfig pdfConfig) {
        float[] cols = {3f, 3f,1f,1f,1f};
        PdfPTable table = new PdfPTable(cols);
        table.setWidthPercentage(100);
        table.addCell(this.generatePositionsHeader(pdfConfig));
        this.generatePositionsHeaderDescription(pdfConfig,table);

        this.positionsAmount.forEach(pos -> {
            if(pos.getTitle()!=null && !pos.getTitle().isBlank()){
                PdfPCell cell = pdfConfig.defaultCell(pos.getTitle());
                cell.setColspan(5);
                table.addCell(cell);
            }
            table.addCell(pdfConfig.defaultCell(pos.getPosName()));
            if(pos.getPosition().getImgUrl() != null && !pos.getPosition().getImgUrl().isBlank()){
                try {
                    Image img = Image.getInstance(pos.getPosition().getImgUrl());
                    img.scaleToFit(50,50);
                    table.addCell(pdfConfig.getImageCell(img));
                } catch (BadElementException | IOException e) {
                    table.addCell(" ");
                }
            }
            else{
                table.addCell(" ");
            }

            table.addCell(pdfConfig.defaultCell(Integer.toString((int)pos.getPosition().getWeight())));
            table.addCell(pdfConfig.defaultCell(Integer.toString(pos.getAmount())));
            table.addCell(pdfConfig.defaultCell(Integer.toString((int)pos.getPosition().getPrice())));
        });
        return table;
    }

    private void generatePositionsHeaderDescription(PdfConfig pdfConfig, PdfPTable table) {
        table.addCell(pdfConfig.defaultCell("Найменування",1));
        table.addCell(pdfConfig.defaultCell(""));
        table.addCell(pdfConfig.defaultCell("Вихід, грам",1));
        table.addCell(pdfConfig.defaultCell("К-сть порцій",1));
        table.addCell(pdfConfig.defaultCell("Ціна, грн",1));
    }

    private void generateShoppingPositionsHeaderDescription(PdfConfig pdfConfig, PdfPTable table) {
        table.addCell(pdfConfig.defaultCell("Найменування",1));
        table.addCell(pdfConfig.defaultCell("Список закупівлі",1));
        table.addCell(pdfConfig.defaultCell("Вихід, грам",1));
        table.addCell(pdfConfig.defaultCell("К-сть порцій",1));
        table.addCell(pdfConfig.defaultCell("Ціна, грн",1));
    }

    private String buildShoppingIngredientsText(PositionAmount positionAmount) {
        List<IngredientAmount> ingredients = new ArrayList<>(positionAmount.getPosition().getIngredients());
        ingredients.sort(Comparator.comparing(
                ingredientAmount -> ingredientAmount.getIngredient().getName(),
                String.CASE_INSENSITIVE_ORDER
        ));

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            IngredientAmount ingredientAmount = ingredients.get(i);
            if (i > 0) {
                builder.append("\n");
            }
            builder.append(ingredientAmount.getIngredient().getName())
                    .append(" - ")
                    .append(formatPdfAmount(ingredientAmount.getAmount() * positionAmount.getAmount()))
                    .append(" ")
                    .append(ingredientAmount.getUnit().getUnitName());
        }
        return builder.toString();
    }

    private Element generateSummaryPdf(PdfConfig pdfConfig) {
        float[] cols = {3f, 3f};
        PdfPTable table = new PdfPTable(cols);
        table.setWidthPercentage(100);
        table.addCell(this.generateSummaryHeader(pdfConfig));
        this.generateTotalPriceRow(table, pdfConfig);
        this.additionalInfo.forEach(pos -> {
            table.addCell(pdfConfig.defaultCell(pos.getTitle()));
            StringBuilder priceString = new StringBuilder();
            priceString.append(pos.getDescription());
            priceString.append("\n");
            if (pos.getPrice()!=0) {
                priceString.append(pos.getPrice());
                priceString.append(" грн");
            }
            table.addCell(pdfConfig.defaultCell(priceString.toString()));
        });

        if(this.needsTax){
            table.addCell(pdfConfig.defaultCell("Обслуговування " + this.taxPercentage + " %: "));
            table.addCell(pdfConfig.defaultCell(String.valueOf(getServingPrice())));
        }
        if(this.govTax){
            table.addCell(pdfConfig.defaultCell("ФОП + " + this.govTaxAmount + " %: "));
            table.addCell(pdfConfig.defaultCell(String.valueOf(getGovTaxPrice())));
        }

        this.generateFinalPriceRow(table, pdfConfig);

        if (this.prepayment > 0) {
            table.addCell(pdfConfig.defaultCell("Передплата:"));
            table.addCell(pdfConfig.defaultCell(NumberFormat.getInstance(Locale.US).format(Math.round(this.prepayment)).replace(","," ") + " грн"));

            table.addCell(pdfConfig.defaultCell("Залишок до оплати:"));
            table.addCell(pdfConfig.defaultCell(NumberFormat.getInstance(Locale.US).format(getRemainingToPay()).replace(","," ") + " грн"));
        }
        return table;
    }

    private PdfPCell generatePositionsHeader(PdfConfig config) {
        String hex = this.user!=null && this.user.getDefaultColor() != null ? this.user.getDefaultColor() : "";
        BaseColor customColor;
        if(hex.isBlank()){
            customColor = config.accentColor;
        }
        else{
            customColor = new BaseColor(
                    Integer.valueOf(hex.substring(1, 3), 16),
                    Integer.valueOf(hex.substring(3, 5), 16),
                    Integer.valueOf(hex.substring(5, 7), 16)
            );
        }


        PdfPCell cell = config.defaultCellBold("Позиції", config.accentTextColor);
        cell.setBackgroundColor(customColor);
        cell.setColspan(5);
        return cell;
    }

    private PdfPCell generateSummaryHeader(PdfConfig config) {
        PdfPCell cell = config.defaultCellBold("Загалом", config.accentTextColor);
        cell.setBackgroundColor(config.summaryHEaderColor);
        cell.setColspan(2);
        return cell;
    }

    private void generateTotalPriceRow(PdfPTable table, PdfConfig config) {
        PdfPCell header = config.defaultCell("Разом по меню, грн");
        PdfPCell amount = config.defaultCell(NumberFormat.getInstance(Locale.US).format(getPrice()).replace(","," ") + " грн");
        table.addCell(header);
        table.addCell(amount);
    }

    private void generateFinalPriceRow(PdfPTable table, PdfConfig config) {
        PdfPCell header = config.defaultCell("Всього за заходом: ",1);

        String price = NumberFormat.getInstance(Locale.US).format(getTotalPrice()).replace(","," ") + " грн";

        PdfPCell amount = config.defaultCell(price,1);
        table.addCell(header);
        table.addCell(amount);
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
        table.addCell(config.defaultCell(this.getDuration()));
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
            byte[] imageBytes = getLogoImageBytes();
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

    private byte[] getLogoImageBytes() throws IOException {
        if (this.user != null && this.user.getLogo() != null && this.user.getLogo().length > 0) {
            return this.user.getLogo();
        }

        ClassPathResource classpath = new ClassPathResource("static/asserts/img/logo.png");
        try (InputStream inputStream = classpath.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }

    public MinMenuResp toMinResp(){
        return MinMenuResp.builder()
                .id(this.id)
                .client(this.client)
                .date(this.date)
                .temporary(this.temporary)
                .build();
    }
}
