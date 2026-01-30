package com.example.fleamarketsystem.controller.api.v1;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.dto.ItemRequest;
import com.example.fleamarketsystem.dto.ItemResponse;
import com.example.fleamarketsystem.entity.Category;
import com.example.fleamarketsystem.entity.Item;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.CategoryService;
import com.example.fleamarketsystem.service.FavoriteService;
import com.example.fleamarketsystem.service.ItemService;
import com.example.fleamarketsystem.service.UserService;

@RestController
@RequestMapping("/api/v1/items")
public class ItemRestController {
	
	private final ItemService itemService;
    private final CategoryService categoryService;
    private final FavoriteService favoriteService;
    private final UserService userService;
    
    public ItemRestController(
            ItemService itemService,
            CategoryService categoryService,
            FavoriteService favoriteService,
            UserService userService
        ) {
            this.itemService = itemService;
            this.categoryService = categoryService;
            this.favoriteService = favoriteService;
            this.userService = userService;
        }
    
    
    /* 一覧検索 */
    
    @GetMapping
    public Page<ItemResponse> search(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return itemService.searchItems(keyword, categoryId, page, size)
            .map(ItemResponse::from);
    }
    
    
    /* 詳細 */
    
    @GetMapping("/{id}")
    public ItemResponse getDetail(@PathVariable Long id) {
        Item item = itemService.getItemById(id)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        return ItemResponse.from(item);
    }
    
    
    /* 新規作成 */
    
    @PostMapping
    public ItemResponse create(
    		@LoginUser User me,
    		@RequestBody @Valid ItemRequest request
    ) {
    	
        User seller = userService.getUserByEmail(me.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));

        Category category = categoryService.getCategoryById(request.categoryId())
            .orElseThrow();

        Item item = new Item();
        item.setSeller(seller);
        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setCategory(category);

        return ItemResponse.from(itemService.save(item));
    }
    
    
    /* 更新 */
    
    @PutMapping("/{id}")
    public ItemResponse update(
        @PathVariable Long id,
        @LoginUser User me,
        @RequestBody @Valid ItemRequest request
    ) {
        Item item = itemService.getItemById(id).orElseThrow();

        if (!item.getSeller().getId().equals(me.getId())) {
            throw new RuntimeException("Forbidden");
        }

        Category category = categoryService.getCategoryById(request.categoryId())
            .orElseThrow();

        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setCategory(category);

        return ItemResponse.from(itemService.save(item));
    }
    
    
    /* 削除 */
    
    @DeleteMapping("/{id}")
    public void delete(
        @PathVariable Long id,
        @LoginUser User me
    ) {
        Item item = itemService.getItemById(id).orElseThrow();

        if (!item.getSeller().getId().equals(me.getId())) {
            throw new RuntimeException("Forbidden");
        }

        itemService.deleteItem(id);
    }
    
    
    /* お気に入り */
    
    @PostMapping("/{id}/favorite")
    public void favorite(
        @PathVariable Long id,
        @LoginUser User me
    ) {
    	
        User user = userService.getUserByEmail(me.getEmail())
        		.orElseThrow();
        
        favoriteService.addFavorite(user, id);
    }

    @DeleteMapping("/{id}/favorite")
    public void unfavorite(
        @PathVariable Long id,
        @LoginUser User me
    ) {
    	
        User user = userService.getUserByEmail(me.getEmail())
        		.orElseThrow();
        favoriteService.removeFavorite(user, id);
    }
    
}
