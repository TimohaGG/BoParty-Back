package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Menu.CommonMenuInfo;
import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.models.Menu.ShoppingList;
import com.bezkoder.springjwt.models.Menu.ShoppingListItem;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.request.Menus.*;
import com.bezkoder.springjwt.payload.response.Menu.*;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import com.bezkoder.springjwt.security.Exceptions.UserNotFoundException;
import com.bezkoder.springjwt.security.services.MenuService;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.PageRanges;
import java.io.ByteArrayOutputStream;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class MenuController {

    private final MenuService menuService;
    private final UserDetailsServiceImpl userDetailsService;
    @Autowired
    public MenuController(MenuService menuService, UserDetailsServiceImpl userDetailsService) {
        this.menuService = menuService;
        this.userDetailsService = userDetailsService;

    }

    @GetMapping("/get")
    public ResponseEntity<List<MenuResponse>> getAll() throws InterruptedException {

        User current = this.userDetailsService.getCurrentUser();
        if(current==null) {
            throw new UserNotFoundException("Can't find current user");
        }
        List<MenuResponse> res = this.menuService.getOrdersByUserId(current.getId()).stream().map(Menu::toDto).toList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/get/min/all")
    public ResponseEntity<List<MinMenuResp>> getMinAll() {
        List<MinMenuResp> res = this.menuService.getAllMin();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/amount")
    public ResponseEntity<Integer> getOrdersAmount(boolean archive) {
        return ResponseEntity.ok(this.menuService.getOrdersAmount(archive));
    }

//    @GetMapping("/get/min")
//    public ResponseEntity<List<MenuCardResponse>> getAllMin() {
//        User current = this.userDetailsService.getCurrentUser();
//        if(current==null) {
//            throw new UserNotFoundException("Can't find current user");
//        }
//        List<MenuCardResponse> res = this.menuService.getOrdersByUserId(current.getId()).stream().map(Menu::toCardDto).toList();
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }

    @GetMapping("/get/min")
    public ResponseEntity<List<MenuCardResponse>> getAllMin(int pageSize, int currentPage, boolean archive) {
        User current = this.userDetailsService.getCurrentUser();
        if(current==null) {
            throw new UserNotFoundException("Can't find current user");
        }
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC,"date"));
        List<MenuCardResponse> res = this.menuService.getOrdersInPage(pageable, archive, current.getId());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/get/{orderId}")
    public ResponseEntity<MenuResponse> getById(@PathVariable Long orderId) throws InterruptedException {
        Menu res = this.menuService.getOrderById(orderId);
        return new ResponseEntity<>(Menu.toDto(res), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<MenuResponse> create(@RequestBody MenuCreateRequest order) {
        Menu res = this.menuService.createOrder(order);
        return ResponseEntity.ok(Menu.toDto(res));
    }

    @PostMapping("/edit")
    public ResponseEntity<MenuResponse> edit(@RequestBody MenuEditRequest order) {
        Menu res = this.menuService.editOrder(order);
        return ResponseEntity.ok(Menu.toDto(res));
    }

    @PostMapping("/edit/status")
    public ResponseEntity<Boolean> editStatus(@RequestBody ToggleStatusReq req) {
        this.menuService.toggleStatus(req);
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) throws SQLException {

        long deletedId = this.menuService.deleteById(id);

        return ResponseEntity.ok(deletedId);
    }

    @PostMapping({"/info/delete", "/info/delete/"})
    public ResponseEntity<Long> deleteInfo(@RequestBody MenuInfoRemoveReq req) {
        this.menuService.deleteInfoById(req.getId());
        return ResponseEntity.ok(req.getId());
    }

    @GetMapping("/info/common")
    public ResponseEntity<List<CommonMenuInfo>> getCommonInfo(){
        List<CommonMenuInfo> res = this.menuService.getAllCommonInfo();
        if(res.isEmpty()){
            throw new NoContentException("No common info found");
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping("/info/common/add")
    public ResponseEntity<MenuCommonInfoResponse> add(@RequestBody MenuCommonInfoRequest info) {
        CommonMenuInfo res = this.menuService.createCommonInfo(info);
        return ResponseEntity.ok(res.toResponse());
    }

    @GetMapping("/shopping/get/{orderId}")
    public ResponseEntity<ShoppingListResp> getShoppingList(@PathVariable long orderId){
        ShoppingList list = this.menuService.getShopping(orderId);
        return ResponseEntity.ok(ShoppingList.toRespDto(list));
    }

    @GetMapping("/generate/{id}")
    public ResponseEntity<byte[]> generate(@PathVariable Long id) {
        ByteArrayOutputStream out = this.menuService.generateOrderPdf(id);
        byte[] pdfBytes = out.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename("File")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

    }

    @PostMapping("/shopping/toggle")
    public ResponseEntity<Boolean> toggleStatus(@RequestBody ToggleStatusReq req) {
        return ResponseEntity.ok(this.menuService.toggleShoppingStatus(req));
    }

    @PostMapping("/shopping/join")
    public ResponseEntity<MinMenuResp> getJoinShoppingList(@RequestBody JoinMenuReq ordersIds){

        return ResponseEntity.ok(this.menuService.joinOrders(ordersIds.getOrdersIds()));
    }

    @PostMapping("/shopping/comment/add")
    public ResponseEntity<String> addComment( @RequestBody AddCommentReq req){
        if(req.getComment() ==null || req.getComment().isBlank())
            return ResponseEntity.badRequest().body("Comment cannot be empty");

        String res = this.menuService.addComment(req);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/shopping/comment/remove/{id}")
    public ResponseEntity<Boolean> removeComment(@PathVariable Long id){
        if(id ==null)
            return ResponseEntity.badRequest().body(false);

        Boolean res = this.menuService.removeComment(id);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/shopping/item/add")
    public ResponseEntity<ShoppingListItemResp> addItem(@RequestBody ShoppingListItemReq req){
        ShoppingListItemResp item = this.menuService.addItem(req);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/shopping/item/remove/{id}")
    public ResponseEntity<Boolean> removeItem(@PathVariable Long id){
        if(id == null)
            return ResponseEntity.badRequest().body(false);

        return ResponseEntity.ok(this.menuService.removeItem(id));
    }


}
