package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Menu.*;
import com.bezkoder.springjwt.models.PdfConfig;
import com.bezkoder.springjwt.models.Position.Ingredient;
import com.bezkoder.springjwt.models.Position.IngredientAmount;
import com.bezkoder.springjwt.models.Position.Position;
import com.bezkoder.springjwt.models.Position.PositionAmount;
import com.bezkoder.springjwt.models.Position.Units;
import com.bezkoder.springjwt.payload.request.Menus.*;
import com.bezkoder.springjwt.payload.request.Position.PosAmountRequest;
import com.bezkoder.springjwt.payload.response.Menu.*;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.security.Exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
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
    private final IShoppingListRepos iShoppingListRepos;
    private final IShoppingListItemRepos iShoppingListItemRepos;
    private final IIngredientsRepos ingredientsRepos;
    private final IUnitRepos unitRepos;
    private final WaiterRepos waiterRepos;



    @Autowired
    public MenuService(MenuRepos menuRepos, IAdditionalInfoRepos iAdditionalInfoRepos, UserDetailsServiceImpl userService, PositionAmountRepos positionAmountRepos, PositionsRepos positionsRepos, ICommonInfoRepos commonInfoRepos, PdfConfig pdfConfig, IShoppingListRepos iShoppingListRepos, IShoppingListItemRepos iShoppingListItemRepos, IIngredientsRepos ingredientsRepos, IUnitRepos unitRepos, WaiterRepos waiterRepos) {
        this.menuRepos = menuRepos;
        this.iAdditionalInfoRepos = iAdditionalInfoRepos;
        this.userService = userService;
        this.positionAmountRepos = positionAmountRepos;

        this.positionsRepos = positionsRepos;
        this.commonInfoRepos = commonInfoRepos;
        this.pdfConfig = pdfConfig;
        this.iShoppingListRepos = iShoppingListRepos;
        this.iShoppingListItemRepos = iShoppingListItemRepos;
        this.ingredientsRepos = ingredientsRepos;
        this.unitRepos = unitRepos;
        this.waiterRepos = waiterRepos;
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
                    .date(LocalDateTime.parse(
                            order.getDate(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                    ))
                    .phone(order.getPhoneNumber())
                    .duration(order.getDuration())
                    .guestsAmount(order.getGuestsAmount())
                    .format(order.getFormat())
                    .deliveryType(order.getDeliveryType())
                    .deliveryAddress(order.getDeliveryAddress())
                    .orderType(order.getOrderType())
                    .needsWaiter(order.isNeedsWaiter())
                    .prepayment(order.getPrepayment())
                    .user(this.userService.getCurrentUser())
                    .needsTax(order.isServing())
                    .taxPercentage(order.getTaxAmount())
                    .govTaxAmount(order.getGovTaxAmount())
                    .govTax(order.isGovTax())

//                .additionalInfo(order.getAdditionalInfo().stream().map(OrderAdditionalInfo::parse).toList())
                    .build();
        }catch (Exception e){
            throw new OrderCreateException("Error creating order. Not all data written");
        }

        List<PositionAmount> positionAmounts = new ArrayList<>();
        Map<Long, Waiter> cooksById = loadCooksById(order.getPositions());
        int inMenuOrder = 0;
        for (PosAmountRequest pos: order.getPositions()){
            PositionAmount posAmount = new PositionAmount();
            posAmount.setPosition(this.positionsRepos.findById(pos.getPosId()).orElseThrow(()-> new NoContentException("Position Not Found")));
            posAmount.setAmount(pos.getAmount());
            posAmount.setTitle(pos.getTitle());
            posAmount.setCook(pos.getCookId() == null ? null : cooksById.get(pos.getCookId()));
            posAmount.setInMenuOrder(pos.getInMenuOrder());
            posAmount.setInMenuOrder(inMenuOrder);
            inMenuOrder++;
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

    @Transactional
    public Menu copyOrder(long id) {
        Menu sourceOrder = this.getOrderById(id);

        Menu copiedOrder = Menu.builder()
                .client(sourceOrder.getClient())
                .date(sourceOrder.getDate())
                .phone(sourceOrder.getPhone())
                .duration(sourceOrder.getDuration())
                .guestsAmount(sourceOrder.getGuestsAmount())
                .format(sourceOrder.getFormat())
                .deliveryType(sourceOrder.getDeliveryType())
                .deliveryAddress(sourceOrder.getDeliveryAddress())
                .orderType(sourceOrder.getOrderType())
                .needsWaiter(sourceOrder.isNeedsWaiter())
                .prepayment(sourceOrder.getPrepayment())
                .user(this.userService.getCurrentUser())
                .needsTax(sourceOrder.isNeedsTax())
                .taxPercentage(sourceOrder.getTaxPercentage())
                .govTax(sourceOrder.isGovTax())
                .govTaxAmount(sourceOrder.getGovTaxAmount())
                .temporary(sourceOrder.isTemporary())
                .build();

        try {
            Menu savedOrder = this.menuRepos.save(copiedOrder);

            List<PositionAmount> copiedPositions = sourceOrder.getPositionsAmount().stream()
                    .map(position -> PositionAmount.copyPositionAmount(position, savedOrder))
                    .toList();
            savedOrder.setPositionsAmount(new ArrayList<>(copiedPositions));

            List<MenuAdditionalInfo> copiedInfo = sourceOrder.getAdditionalInfo().stream()
                    .map(info -> MenuAdditionalInfo.copy(info, savedOrder))
                    .toList();
            savedOrder.setAdditionalInfo(new ArrayList<>(copiedInfo));

            savedOrder.setTotalPrice(savedOrder.getTotalPrice());
            return this.menuRepos.save(savedOrder);
        } catch (Exception e) {
            throw new OrderCreateException(e.getMessage());
        }
    }

    @Transactional
    public Menu editOrder(MenuEditRequest request) {
        Menu order = this.getOrderById(request.getId());

        order.setDate(LocalDateTime.parse(
                request.getDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        )); // adjust parsing if datetime
        order.setClient(request.getClient());
        order.setGuestsAmount(request.getGuestsAmount());
        order.setDuration(request.getDuration());
        order.setFormat(request.getFormat());
        order.setPhone(request.getPhoneNumber());
        order.setDeliveryType(request.getDeliveryType());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setOrderType(request.getOrderType());
        order.setNeedsWaiter(request.isNeedsWaiter());
        order.setPrepayment(request.getPrepayment());
        order.setNeedsTax(request.isServing());
        order.setTaxPercentage(request.getTaxAmount());
        order.setGovTax(request.isGovTax());
        order.setGovTaxAmount(request.getGovTaxAmount());
        if(order.getShoppingList()!=null)
            order.getShoppingList().setNeedsUpdate(true);

        replacePositions(order, request.getPositions());
        replaceAdditionalInfo(order, request.getAdditionalInfo());

        try{
            order.setTotalPrice(order.getTotalPrice());
            return this.menuRepos.save(order);
        }catch (Exception e){
            throw new OrderCreateException(e.getMessage());
        }
    }

    private void replacePositions(Menu order, List<PosAmountRequest> requestedPositions) {
        new ArrayList<>(order.getPositionsAmount()).forEach(order::removePosition);

        List<PosAmountRequest> positions = requestedPositions == null ? List.of() : requestedPositions;
        Map<Long, Position> positionsById = loadPositionsById(positions);
        Map<Long, Waiter> cooksById = loadCooksById(positions);

        int inMenuOrder = 0;
        for (PosAmountRequest posReq : positions) {
            PositionAmount posAmount = new PositionAmount();
            posAmount.setOrder(order);
            posAmount.setAmount(posReq.getAmount());
            posAmount.setCook(posReq.getCookId() == null ? null : cooksById.get(posReq.getCookId()));
            posAmount.setInMenuOrder(posReq.getInMenuOrder());
            posAmount.setInMenuOrder(inMenuOrder);
            inMenuOrder++;
            if (posReq.getPosId() > 0) {
                posAmount.setPosition(positionsById.get(posReq.getPosId()));
            }

            if (posReq.getTitle() != null && !posReq.getTitle().isBlank()) {
                posAmount.setTitle(posReq.getTitle());
            }

            order.addPosition(posAmount);
        }
    }

    private Map<Long, Position> loadPositionsById(List<PosAmountRequest> positions) {
        Set<Long> positionIds = positions.stream()
                .map(PosAmountRequest::getPosId)
                .filter(id -> id > 0)
                .collect(Collectors.toSet());

        Map<Long, Position> positionsById = positionsRepos.findAllById(positionIds)
                .stream()
                .collect(Collectors.toMap(Position::getId, Function.identity()));

        if (positionsById.size() != positionIds.size()) {
            positionIds.removeAll(positionsById.keySet());
            throw new NoContentException("Position not found with id " + positionIds.iterator().next());
        }

        return positionsById;
    }

    private Map<Long, Waiter> loadCooksById(List<PosAmountRequest> positions) {
        Set<Long> cookIds = positions.stream()
                .map(PosAmountRequest::getCookId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (cookIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, Waiter> cooksById = waiterRepos.findAllById(cookIds)
                .stream()
                .collect(Collectors.toMap(Waiter::getId, Function.identity()));

        if (cooksById.size() != cookIds.size()) {
            cookIds.removeAll(cooksById.keySet());
            throw new NoContentException("Cook not found with id " + cookIds.iterator().next());
        }

        return cooksById;
    }

    private void replaceAdditionalInfo(Menu order, List<MenuInfoRequest> requestedInfo) {
        List<MenuAdditionalInfo> oldInfo = iAdditionalInfoRepos.findAllByOrderId(order.getId());
        oldInfo.forEach(order::removeInfo);
        iAdditionalInfoRepos.deleteAll(oldInfo);

        List<MenuInfoRequest> info = requestedInfo == null ? List.of() : requestedInfo;
        info.stream()
                .map(MenuAdditionalInfo::parse)
                .forEach(el -> {
                    el.setOrder(order);
                    order.addInfo(el);
                });
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

    @Transactional
    public long deleteById(Long id) {
        Menu order = this.getOrderById(id);
        this.menuRepos.delete(order);
        return id;
    }

    public ByteArrayOutputStream generateOrderPdf(Long id) {
        Menu order = this.getOrderById(id);
        return order.toPdf(pdfConfig);

    }

    public ByteArrayOutputStream generateShoppingListPdf(Long id) {
        Menu order = this.getOrderById(id);
        return order.toShoppingListPdf(pdfConfig);
    }

    public void toggleStatus(ToggleStatusReq req) {
        Menu order = this.getOrderById(req.getId());
        order.setPayed(req.isStatus());
        this.menuRepos.save(order);
    }

    public boolean toggleShoppingStatus(ToggleStatusReq req) {
        ShoppingListItem item = this.iShoppingListItemRepos.findById(req.getId()).orElse(null);
        if(item == null){
            throw new ToggleShoppingException("Can't toggle shopping list item with id " + req.getId());
        }
        item.setBought(req.isStatus());
        this.iShoppingListItemRepos.save(item);
        return req.isStatus();
    }

    public List<MenuCardResponse> getOrdersInPage(Pageable pageable, boolean archive, long userId) {
        if(archive)
            return this.menuRepos.findAllForListArchive(LocalDate.now().atStartOfDay(),userId, pageable).toList();
        else
            return this.menuRepos.findAllForList(LocalDate.now().atStartOfDay(),userId, pageable).toList();
    }

    public Integer getOrdersAmount(boolean archive) {
        if(archive)
            return this.menuRepos.findTotalArchiveOrders(LocalDate.now().atStartOfDay());
        else
            return this.menuRepos.findTotalFutureOrders(LocalDate.now().atStartOfDay());
    }

    @Transactional
    public void deleteInfoById(long id) {
        try{
            MenuAdditionalInfo info = this.iAdditionalInfoRepos.findById(id)
                    .orElseThrow(() -> new NoContentException("Menu info not found"));

            if (info.getOrder() != null) {
                info.getOrder().removeInfo(info);
            }

            this.iAdditionalInfoRepos.delete(info);
        }
        catch (Exception e){
            throw new OrderEditException("Can't delete menu info!");
        }
    }

    @Transactional
    public ShoppingList getShopping(long orderId) {

        ShoppingList res = this.iShoppingListRepos.findShoppingListByOrderId(orderId).orElse(null);

        if(res!=null && res.isNeedsUpdate()){
            this.removeShoppingList(res.getId());
            res = this.generateShoppingList(orderId);
        }
        else if(res == null)
            res = this.generateShoppingList(orderId);



        return res;
    }

    private void removeShoppingList(Long id) {
        ShoppingList shoppingList = this.iShoppingListRepos.findById(id)
                .orElseThrow(() -> new NoContentException("Shopping list not found"));

        if (shoppingList.getOrder() != null) {
            shoppingList.getOrder().setShoppingList(null);
            shoppingList.setOrder(null);
        }

        shoppingList.clearItems();
        this.iShoppingListRepos.delete(shoppingList);
        this.iShoppingListRepos.flush();
    }

    private ShoppingList generateShoppingList(Long orderId) {
        ShoppingList res = new ShoppingList();
        Menu order = this.getOrderById(orderId);
        for (PositionAmount pos : order.getPositionsAmount()){
            for (IngredientAmount ing : pos.getPosition().getIngredients()){
                ShoppingListItem item = new ShoppingListItem(ing.getIngredient(),res ,ing.getAmount() * pos.getAmount(),ing.getUnit());
                res.addItem(item);
            }
        }
        try{
            res.setOrder(order);
            normalizeList(res);
            return this.iShoppingListRepos.save(res);
        }catch (Exception e){
            throw new ShoppingCreateException(e.getMessage());
        }
    }

    private ShoppingList normalizeList(ShoppingList list) {
        Map<String, ShoppingListItem> normalizedItems = new LinkedHashMap<>();

        for (ShoppingListItem item : list.getItems()) {
            String key = item.getIngredient().getId() + ":" + item.getUnit().getId();
            ShoppingListItem existingItem = normalizedItems.get(key);

            if (existingItem != null) {
                existingItem.setAmount(existingItem.getAmount() + item.getAmount());
            } else {
                normalizedItems.put(key, item);
            }
        }

        list.setItems(new ArrayList<>(normalizedItems.values()));
        return list;
    }

    public List<MinMenuResp> getAllMin() {

        return this.menuRepos.findAllMin();
    }

    public List<MinMenuResp> getMinOrdersByCurrentUser() {
        Long userId = this.userService.getCurrentUser().getId();
        return this.menuRepos.findAllMinByUserId(userId);
    }

    public MinMenuResp joinOrders(Long[] ordersIds) {
        List<Menu> menuList = this.menuRepos.findAllById(Arrays.stream(ordersIds).toList());
        if(menuList.isEmpty())
            throw new NoContentException("Menus not found");


        List<PositionAmount> positions = menuList.stream().flatMap(x->x.getPositionsAmount().stream()).toList();

        Menu menu = Menu.builder()
                .user(this.userService.getCurrentUser())
                .temporary(true)
                .date(LocalDateTime.now())
                .client(menuList.stream().map(Menu::getDateFormatted).collect(Collectors.joining(" + ")))
                .totalPrice(menuList.stream().map(Menu::getTotalPrice).mapToInt(Integer::intValue).sum())
                .build();
        try{
            this.menuRepos.save(menu);
            menu.setPositionsAmount(new ArrayList<>());

            List<PositionAmount> merged = positions.stream()
                    .collect(Collectors.toMap(
                            pa -> pa.getPosition().getId(),
                            pa -> {
                                PositionAmount copy = new PositionAmount();
                                copy.setPosition(pa.getPosition());
                                copy.setAmount(pa.getAmount());
                                return copy;
                            },
                            (existing, incoming) -> {
                                existing.setAmount(
                                        existing.getAmount() + incoming.getAmount()
                                );
                                return existing;
                            }
                    ))
                    .values()
                    .stream()
                    .toList();

            for(PositionAmount pos : merged){
                menu.addPosition(new PositionAmount(pos.getPosition(),menu,pos.getAmount()));
            }
            this.menuRepos.save(menu);
            return menu.toMinResp();
        }
        catch (Exception e){
            throw new OrderCreateException("Can't join menu!");
        }
    }

    public String addComment(AddCommentReq req) {
        ShoppingListItem item = this.iShoppingListItemRepos.findById(req.getShoppingItemId()).orElse(null);
        if (item==null)
            throw new NoContentException("Shopping list item not found");
        item.setComment(req.getComment());
        try{
            this.iShoppingListItemRepos.save(item);
            return item.getComment();
        }catch (Exception ex){
            throw new ShoppingCreateException(ex.getMessage());
        }
    }

    public Boolean removeComment(Long id) {
        ShoppingListItem item = this.iShoppingListItemRepos.findById(id).orElse(null);
        if (item==null)
            throw new NoContentException("Shopping list item not found");
        item.setComment(null);
        try{
            this.iShoppingListItemRepos.save(item);
            return item.getComment() ==null;
        }catch (Exception ex){
            throw new ShoppingCreateException(ex.getMessage());
        }
    }

    @Transactional
    public Boolean removeItem(Long id) {
        ShoppingListItem item = this.iShoppingListItemRepos.findById(id)
                .orElseThrow(() -> new NoContentException("Shopping list item not found"));

        try {
            if (item.getShoppingList() != null) {
                item.getShoppingList().removeItem(item);
            }

            this.iShoppingListItemRepos.delete(item);
            return true;
        } catch (Exception ex) {
            throw new ShoppingCreateException(ex.getMessage());
        }
    }

    @Transactional
    public ShoppingListItemResp addItem(ShoppingListItemReq req) {
        ShoppingList shoppingList = this.iShoppingListRepos.findById(req.getShoppingListId())
                .orElseThrow(() -> new NoContentException("Shopping list not found"));

        Ingredient ingredient = this.ingredientsRepos.findById(req.getIngredientId())
                .orElseThrow(() -> new NoContentException("Ingredient not found"));

        Units unit = getShoppingItemUnit(req);

        if (shoppingList.getItems() == null) {
            shoppingList.setItems(new ArrayList<>());
        }

        ShoppingListItem item = shoppingList.getItems()
                .stream()
                .filter(existing -> Objects.equals(existing.getIngredient().getId(), ingredient.getId())
                        && Objects.equals(existing.getUnit().getId(), unit.getId()))
                .findFirst()
                .orElse(null);

        if (item != null) {
            item.setAmount(item.getAmount() + req.getAmount());
        } else {
            item = new ShoppingListItem(ingredient, shoppingList, req.getAmount(), unit);
            shoppingList.addItem(item);
        }

        try {
            ShoppingListItem savedItem = this.iShoppingListItemRepos.saveAndFlush(item);
            return ShoppingListItem.toRespDto(savedItem);
        } catch (Exception ex) {
            throw new ShoppingCreateException(ex.getMessage());
        }
    }

    private Units getShoppingItemUnit(ShoppingListItemReq req) {
        if (req.getUnitId() != null) {
            return this.unitRepos.findById(req.getUnitId())
                    .orElseThrow(() -> new NoContentException("Unit not found"));
        }

        if (req.getUnitName() != null && !req.getUnitName().isBlank()) {
            Units unit = this.unitRepos.findByUnitName(req.getUnitName());
            if (unit != null) {
                return unit;
            }
        }

        throw new NoContentException("Unit not found");
    }

    public List<MenuCardResponse> searchByNameOrDate(long userId, String name, String date) {
        LocalDate time = null;
        if(date!=null && !date.isBlank()){
            time = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        return this.menuRepos.findAllByClientOrDate(userId, name,time);
    }

    public List<ShoppingListRespMin> getAllShoppingsMin() {

        long userId = this.userService.getCurrentUser().getId();
        YearMonth currentMonth = YearMonth.now();

        LocalDateTime startOfMonth = currentMonth
                .atDay(1)
                .atStartOfDay();

        LocalDateTime startOfNextMonth = currentMonth
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay();

        List<ShoppingListRespMin> res = this.iShoppingListRepos.findAllByUserIdAndCurrentMonth(userId,startOfMonth,startOfNextMonth).stream().map(ShoppingList::toMinRespDto).toList();
        if(res.isEmpty()){
            throw new NoContentException("Shopping lists not found");
        }
        return res;
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
