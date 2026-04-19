package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Menu.CommonMenuInfo;
import com.bezkoder.springjwt.models.Menu.MenuAdditionalInfo;
import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.models.PdfConfig;
import com.bezkoder.springjwt.models.Position.Position;
import com.bezkoder.springjwt.models.Position.PositionAmount;
import com.bezkoder.springjwt.payload.request.Menus.*;
import com.bezkoder.springjwt.payload.request.Position.PosAmountRequest;
import com.bezkoder.springjwt.payload.response.Menu.MenuCardResponse;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import com.bezkoder.springjwt.security.Exceptions.OrderCreateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {
    private final MenuRepos menuRepos;
    private final IAdditionalInfoRepos iAdditionalInfoRepos;
    private final PositionAmountRepos positionAmountRepos;
    private final PositionsRepos positionsRepos;
    private final UserDetailsServiceImpl userService;
    private final ICommonInfoRepos commonInfoRepos;
    private final PdfConfig pdfConfig;


    @Autowired
    public MenuService(MenuRepos menuRepos, IAdditionalInfoRepos iAdditionalInfoRepos, UserDetailsServiceImpl userService, PositionAmountRepos positionAmountRepos, PositionsRepos positionsRepos, ICommonInfoRepos commonInfoRepos, PdfConfig pdfConfig) {
        this.menuRepos = menuRepos;
        this.iAdditionalInfoRepos = iAdditionalInfoRepos;
        this.userService = userService;
        this.positionAmountRepos = positionAmountRepos;

        this.positionsRepos = positionsRepos;
        this.commonInfoRepos = commonInfoRepos;
        this.pdfConfig = pdfConfig;
    }

    public List<Menu> getOrdersByUserId(Long userId) {
        List<Menu> res = menuRepos
                .findAllByUserIdAndTemporaryFalse(userId)
                .stream()
                .sorted((order1, order2) -> order2.getDate().compareTo(order1.getDate()))
                .toList();

        if(res.isEmpty()) {
            throw new NoContentException("There are no orders");
        }
        return res;
    }

    public Menu createOrder(MenuCreateRequest order) {
        Menu newOrder;
        try{
            newOrder = Menu.builder()
                    .client(order.getClient())
                    .date(LocalDate.parse(order.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            .atStartOfDay())
                    .phone(order.getPhoneNumber())
                    .duration(order.getDuration())
                    .guestsAmount(order.getGuestsAmount())
                    .format(order.getFormat())
                    .user(this.userService.getCurrentUser())
//                .additionalInfo(order.getAdditionalInfo().stream().map(OrderAdditionalInfo::parse).toList())
                    .build();
        }catch (Exception e){
            throw new OrderCreateException("Error creating order. Not all data written");
        }

        List<PositionAmount> positionAmounts = new ArrayList<>();
        for (PosAmountRequest pos: order.getPositions()){
            PositionAmount posAmount = new PositionAmount();
            posAmount.setPosition(this.positionsRepos.findById(pos.getPosId()).orElseThrow(()-> new NoContentException("Position Not Found")));
            posAmount.setAmount(pos.getAmount());
            posAmount.setTitle(pos.getTitle());
            positionAmounts.add(posAmount);
        }
        try{
            Menu tmp =  menuRepos.save(newOrder);
            positionAmounts.forEach(el -> el.setOrder(tmp));
            tmp.setPositionsAmount(positionAmounts);
            List<MenuAdditionalInfo> info = order.getAdditionalInfo().stream().map(MenuAdditionalInfo::parse).collect(Collectors.toList());
            info.forEach(el->el.setOrder(tmp));
            tmp.setAdditionalInfo(info);

            tmp.setTotalPrice(tmp.getTotalPrice());

            return menuRepos.save(tmp);
        }catch (Exception e){
            throw new OrderCreateException(e.getMessage());
        }

    }

    private void saveInfo(List<MenuAdditionalInfo> data){
        this.iAdditionalInfoRepos.saveAll(data);
    }

    public Menu getOrderById(long id) {
        return this.menuRepos.findById(id).orElseThrow(()-> new NoContentException("Order Not Found"));
    }

    public Menu editOrder(MenuEditRequest request) {
        Menu order = this.getOrderById(request.getId());

        order.setDate(LocalDate.parse(request.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atStartOfDay()); // adjust parsing if datetime
        order.setClient(request.getClient());
        order.setGuestsAmount(request.getGuestsAmount());
        order.setDuration(request.getDuration());
        order.setFormat(request.getFormat());
        order.setPhone(request.getPhoneNumber());


        for (PositionAmount pos: order.getPositionsAmount()){
            order.removePosition(pos);
        }

        List<PositionAmount> positionAmounts = new ArrayList<>();
        for (PosAmountRequest posReq : request.getPositions()) {
            PositionAmount posAmount = new PositionAmount();
            posAmount.setOrder(order);
            posAmount.setAmount(posReq.getAmount());

            if (posReq.getPosId() > 0) {
                Position position = positionsRepos.findById(posReq.getPosId())
                        .orElseThrow(() -> new RuntimeException("Position not found with id " + posReq.getPosId()));
                posAmount.setPosition(position);
            }

            if (posReq.getTitle() != null && !posReq.getTitle().isBlank()) {
                posAmount.setTitle(posReq.getTitle());
            }

            positionAmounts.add(posAmount);
        }
        for (PositionAmount posReq : positionAmounts) {
            order.addPosition(posReq);
        }

        for(MenuAdditionalInfo info : order.getAdditionalInfo()){
            order.removeInfo(info);
        }
        List<MenuAdditionalInfo> additionalInfo = request.getAdditionalInfo().stream().map(MenuAdditionalInfo::parse).toList();
        additionalInfo.forEach(el->{el.setOrder(order);order.addInfo(el);});

        try{
            order.setTotalPrice(order.getTotalPrice());
            return this.menuRepos.save(order);
        }catch (Exception e){
            throw new OrderCreateException(e.getMessage());
        }


    }

    public CommonMenuInfo createCommonInfo(MenuCommonInfoRequest info) {
        CommonMenuInfo res = CommonMenuInfo.builder()
                .title(info.getTitle())
                .description(info.getDescription())
                .price(info.getPrice())
                .build();

        try{
            return this.commonInfoRepos.save(res);
        }
        catch (Exception e){
            throw new OrderCreateException(e.getMessage());
        }


    }

    public List<CommonMenuInfo> getAllCommonInfo() {
        return this.commonInfoRepos.findAll();
    }

    public long deleteById(Long id) {
        this.menuRepos.deleteById(id);
        return id;
    }

    public ByteArrayOutputStream generateOrderPdf(Long id) {
        Menu order = this.getOrderById(id);
        return order.toPdf(pdfConfig);

    }

    public void toggleStatus(ToggleStatusReq req) {
        Menu order = this.getOrderById(req.getId());
        order.setPayed(req.isStatus());
        this.menuRepos.save(order);
    }

    public List<MenuCardResponse> getOrdersInPage(Pageable pageable) {
        return this.menuRepos.findAllForList(LocalDate.now().atStartOfDay(), pageable).toList();
//        List<Menu> res = this.menuRepos.findAllByDateStartingWith(LocalDateTime.now(), pageable).toList();
//
//        return res.stream().map(Menu::toCardDto).toList();
    }

    public Integer getOrdersAmount() {
        return this.menuRepos.findTotalOrdersAmount();
    }


//    public List<Orders> getTempOrders(){
//        return ordersRepos.findAllByUserIdAndTemporaryTrue(userService.getCurrentUser().getId());
//    }
//
//    public Orders save(Orders orders) {
//        return ordersRepos.save(orders);
//    }
//
//    public Orders getOrderById(Long id) {
//        return ordersRepos.findById(id).orElse(null);
//    }
//
//
//
//    public PdfWriter GeneratePdf(Document document, OutputStream out, long id, OrderInfo info) throws DocumentException {
//
//        Orders order = getOrderById(id);
//        order.setNeedsTax(info.isTax());
//        ordersRepos.save(order);
//
//        PdfGenerator generator = new PdfGenerator(order, info);
//
//
//        PdfGenerator.backgroundColor = BaseColor.WHITE;
//        PdfGenerator.fontColor = BaseColor.BLACK;
//        PdfGenerator.containerColor = BaseColor.WHITE;
//        if(info.getColor().isEmpty()){
//            PdfGenerator.posHeaderColor = new BaseColor(250,187,7);
//        }
//        else{
//            int[] color = Arrays.stream(info.getColor().split(",")).mapToInt(x->Integer.parseInt(x.trim())).toArray();
//            PdfGenerator.posHeaderColor = new BaseColor(color[0],color[1],color[2]);
//        }
//
//        PdfGenerator.summaryHeaderColor = new BaseColor(91,91,91);
//
//        PdfWriter writer = PdfWriter.getInstance(document, out);
//
//        // Open the document for writing
//        document.open();
//
//
//        generator.generate(document);
//
//        document.close();
//        writer.flush();
//        return writer;
//    }
//
//    public void addAdditionalInfo(Long orderId, OrderAdditionalInfo info) {
//        Orders orders = ordersRepos.findById(orderId).orElse(null);
//        if (orders!=null){
//            info.setOrder(orders);
//            iAdditionalInfoRepos.save(info);
//        }
//
//    }
//
//    public void removeAdditionalInfo(Long id) {
//        OrderAdditionalInfo inf = iAdditionalInfoRepos.findById(id).orElse(null);
//        if (inf!=null){
//            if(inf.isCommon()){
//                inf.setOrder(null);
//                iAdditionalInfoRepos.save(inf);
//            }
//            else{
//                iAdditionalInfoRepos.delete(inf);
//            }
//        }
//    }
//
//    public void removeOrder(Long id) {
//        Orders orders = ordersRepos.findById(id).orElse(null);
//        if (orders!=null){
//            if(orders.getAdditionalInfo()!=null){
//                iAdditionalInfoRepos.deleteAll(orders.getAdditionalInfo());
//            }
//            if(orders.getPositionsAmount()!=null){
//                positionAmountRepos.deleteAll(orders.getPositionsAmount());
//            }
//        }
//        ordersRepos.deleteById(id);
//
//
//
//    }
//
//    public void saveInfo(OrderAdditionalInfo info) {
//        iAdditionalInfoRepos.save(info);
//    }
//
//    public void savePositionAmount(PositionAmount pos) {
//        positionAmountRepos.save(pos);
//    }
//
//    public List<OrderAdditionalInfo> getCommonAdditionalInfo(){
//        return iAdditionalInfoRepos.findAll().stream().filter(OrderAdditionalInfo::isCommon).toList();
//    }
//
//    public OrderAdditionalInfo getCommonInfoById(Long id) {
//        OrderAdditionalInfo tmp = iAdditionalInfoRepos.findById(id).orElse(null);
//        if(tmp==null){
//            return null;
//        }
//        if(tmp.isCommon()){
//            return tmp;
//        }
//        return null;
//    }
//
//    public String getOrderFileName(long id){
//        Orders tmp = ordersRepos.findById(id).orElse(null);
//        if(tmp==null){
//            return "";
//        }
//        return tmp.getClient() + " " + tmp.getDate()+".pdf";
//    }
//
//    public List<IngredientAmount> getShopping(List<Orders> orders) {
//
//        //orders = List.of(ordersRepos.getOrderById(74L), ordersRepos.getOrderById(75L));
//
//        // Combine all orders into one list with summed amounts
//        List<PositionAmount> positions = orders.stream()
//                .flatMap(order -> order.getPositionsAmount().stream())
//                .collect(Collectors.groupingBy(
//                        PositionAmount::getPosition, Collectors.summingInt(PositionAmount::getAmount)))
//                .entrySet().stream()
//                .map(e -> new PositionAmount(e.getKey(), e.getValue()))
//                .toList();
//
////        List<IngredientAmount> ings = positions.stream()
////                .flatMap(pos -> pos.getPosition().getIngredients().stream()
////                        .map(ingAm -> new IngredientAmount(ingAm.getIngredient(),
////                                ingAm.getAmount() * pos.getAmount(), ingAm.getUnit())))
////                .collect(Collectors.toMap(
////                        ing->ing.getIngredient().getId(),
////                        item->item,
////                        (existIt,newIt)->new IngredientAmount(existIt.getId(), existIt.getIngredient(),existIt.getUnit(),existIt.getAmount() + newIt.getAmount(), existIt.getPosition())
////                ))
////                .values().stream().toList();
//
//        List<IngredientAmount> res = new ArrayList<>();
//
//        for (PositionAmount position : positions) {
//            for (IngredientAmount ingredient : position.getPosition().getIngredients()) {
//                if(!res.contains(ingredient)){
//                    IngredientAmount tmp = new IngredientAmount(ingredient.getIngredient(), ingredient.getAmount(), ingredient.getUnit());
//                    tmp.setAmount(ingredient.getAmount() * position.getAmount());
//                    res.add(tmp);
//                }
//                else{
//                    int index = res.indexOf(ingredient);
//                    IngredientAmount tmp = res.get(index);
//                    tmp.setAmount(tmp.getAmount() + position.getAmount()*ingredient.getAmount());
//                }
//            }
//        }
//
//        res.forEach(el-> System.out.println(el.getIngredient().getName() + " " + el.getAmount()));
//
////        ings.forEach(v -> System.out.println(v.getIngredient().getName() + " " + v.getAmount() + " " + v.getUnit().getUnitName()));
//
//        return res;
//    }
//
//    public Orders createTempOrder(long[] orderIds){
//        List<Orders> orders = new ArrayList<>();
//        Arrays.stream(orderIds).forEach(orderId -> {
//            ordersRepos.findById(orderId).ifPresent(orders::add);
//        });
//
//        Orders temp = new Orders();
//        temp.setUser(orders.get(0).getUser());
//        temp.setClient(orders.stream().map(Orders::getClient).collect(Collectors.joining(" + ")));
//        temp.setTemporary(true);
//        temp = ordersRepos.save(temp);
//
//        List<PositionAmount> t = orders.stream().map(Orders::getPositionsAmount).flatMap(List::stream).toList();
//        for(PositionAmount pa : t){
//            temp.addPosition(PositionAmount.copyPositionAmount(pa,temp));
//        }
//        temp = ordersRepos.save(temp);
//        return temp;
//    }
//
//    public void removePositions(Long id) {
//        Orders orders = ordersRepos.findById(id).orElse(null);
//        if(orders!=null){
//            for(PositionAmount pa : orders.getPositionsAmount()){
//                orders.removePosition(pa);
//            }
//            save(orders);
//
//        }
//    }
//
//    public List<String[]> parseOrder(MultipartFile menu) {
//        List<String[]> res = new ArrayList<>();
//        try{
//            PdfDocument pdfDocument = new PdfDocument(menu.getInputStream());
//
//            PdfTableExtractor extractor = new PdfTableExtractor(pdfDocument);
//
//            for (int pIndex = 0;pIndex<pdfDocument.getPages().getCount();pIndex++) {
//                PdfTable[] tabelsList = extractor.extractTable(pIndex);
//                if(tabelsList != null) {
//                    for (PdfTable table : tabelsList) {
//                        for (int i = 0; i < table.getRowCount(); i++) {
//                            String[] arr = new String[table.getColumnCount()];
//                            for (int j = 0; j < table.getColumnCount(); j++) {
//                                String text = table.getText(i, j);
//                                arr[j] = text;
//                            }
//                            res.add(arr);
//                        }
//                    }
//                }
//            }
//            return res;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//       return null;
//    }
//
//    public Orders createOrderDetailsFromText(List<String[]> cells, List<String> errors) {
//        try{
//            return save(setOrderInfo(cells, errors));
//        }
//        catch (Exception e){
//            errors.add("Не вдалось створити замовлення:" + e.getMessage());
//            return null;
//        }
//    }
//
//    private Orders setOrderInfo(List<String[]> cells, List<String> errors ) {
//        Orders order = new Orders();
//        int splitIndex = 0;
//        for (int rowIndex = 0; rowIndex < cells.size(); rowIndex++) {
//            boolean finish = false;
//            for (int i = 0; i < cells.get(rowIndex).length; i++) {
//                switch (simlpifyText(cells.get(rowIndex)[i])) {
//
//                    case "замовник":{
//                        order.setClient(nextRowWithData(cells.get(rowIndex)));
//                        i=cells.get(rowIndex).length-1;
//                    }break;
//                    case "дата":{
//                        LocalDateTime date = order.getDate();
//                        LocalDateTime res = parseDate(nextRowWithData(cells.get(rowIndex)),date.format(DateTimeFormatter.ofPattern("HH:mm")));
//                        if(res==null){
//                            errors.add("Не вдалось розшифрувати дату: "+nextRowWithData(cells.get(rowIndex)));
//                        }
//                        else{
//                            order.setDate(res);
//                        }
//                        i=cells.get(rowIndex).length-1;
//                    }break;
//                    case "початокзаходу":{
//                        LocalDateTime date = order.getDate();
//                        LocalDateTime res = parseDate(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),nextRowWithData(cells.get(rowIndex)));
//
//                        if(res==null){
//                            errors.add("Не вдалось розшифрувати початок заходу: "+nextRowWithData(cells.get(rowIndex)));
//                        }
//                        else{
//                            order.setDate(res);
//                        }
//                        i=cells.get(rowIndex).length-1;
//                    }break;
//                    case "тривалість":{
//                        try{
//                            order.setDuration(Integer.parseInt(nextRowWithData(cells.get(rowIndex))));
//                        }catch (Exception e){
//                            order.setDuration(0);
//                            errors.add("Не вдалось розшифрувати тривалість: "+nextRowWithData(cells.get(rowIndex)));
//                        }
//                        i=cells.get(rowIndex).length-1;
//
//                    }break;
//                    case "к-стьзапрошених":{
//                        try{
//                            order.setGuestsAmount(Integer.parseInt(nextRowWithData(cells.get(rowIndex))));
//                        }catch (Exception e){
//                            order.setGuestsAmount(0);
//                            errors.add("Не вдалось розшифрувати к-сть запрошених: "+nextRowWithData(cells.get(rowIndex)));
//                        }
//                        i=cells.get(rowIndex).length-1;
//
//                    }break;
//                    case  "форматзаходу":{
//                        order.setFormat(nextRowWithData(cells.get(rowIndex)));
//                        i=cells.get(rowIndex).length-1;
//                    }break;
//                    case "телефонвідповідальногоменеджера":{
//                        order.setPhone(nextRowWithData(cells.get(rowIndex)));
//                        i=cells.get(rowIndex).length-1;
//                    }break;
//                    case "меню":
//                    case "позиції":{
//                        finish = true;
//                        i=cells.get(rowIndex).length-1;
//                    }break;
//                }
//            }
//            if(finish){
//                splitIndex = rowIndex;
//                break;
//            }
//        }
//        order.setUser(this.userService.getCurrentUser());
//        order.setTemporary(false);
//
//        cells.subList(0,splitIndex+1).clear();
//
//        return order;
//    }
//
//    private String nextRowWithData(String[] cells){
//       for (int i = 1; i < cells.length; i++) {
//           if(!cells[i].isEmpty()){
//               return cells[i];
//           }
//       }
//       return "";
//    }
//
//    private LocalDateTime parseDate(String date, String time){
//
//
//        String fDate = date + " " + time;
//
//        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
//                .appendValue(ChronoField.DAY_OF_MONTH) // accepts 1 or 2 digits
//                .appendLiteral('.')
//                .appendValue(ChronoField.MONTH_OF_YEAR) // accepts 1 or 2 digits
//                .appendLiteral('.')
//                .appendValueReduced(ChronoField.YEAR, 2, 4, 2000) // "25" becomes 2025
//                .appendLiteral(' ')
//                .appendPattern("HH:mm")
//                .toFormatter();
//
//        try{
//            return LocalDateTime.parse(fDate, formatter);
//        }
//        catch (Exception e){
//            return null;
//        }
//    }
//
//    private String simlpifyText(String text){
//        return text.toLowerCase().replaceAll(" ","").replaceAll("\n","");
//    }
}
