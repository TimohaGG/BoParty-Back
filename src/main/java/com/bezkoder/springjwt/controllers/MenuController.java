package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Menu.CommonMenuInfo;
import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.models.Menu.ShoppingList;
import com.bezkoder.springjwt.models.Menu.ShoppingListItem;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.request.Menus.*;
import com.bezkoder.springjwt.payload.response.Menu.MenuCardResponse;
import com.bezkoder.springjwt.payload.response.Menu.MenuCommonInfoResponse;
import com.bezkoder.springjwt.payload.response.Menu.MenuResponse;
import com.bezkoder.springjwt.payload.response.Menu.ShoppingListResp;
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
    public ResponseEntity<Long> delete(@PathVariable Long id) {
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








}
