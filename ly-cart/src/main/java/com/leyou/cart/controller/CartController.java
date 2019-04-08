package com.leyou.cart.controller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@Log4j2
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        try {
            cartService.addCart(cart);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping
    public ResponseEntity<List<Cart>> queryCartList(){
        try {
            List<Cart> carts = cartService.queryCartList();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
    }
}
