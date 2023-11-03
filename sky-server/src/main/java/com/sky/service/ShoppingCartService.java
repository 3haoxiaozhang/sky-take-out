package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.springframework.stereotype.Service;


public interface ShoppingCartService {

    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
