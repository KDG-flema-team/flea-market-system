package com.example.fleamarketsystem.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.fleamarketsystem.entity.Item;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.ItemRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryService categoryService;
    private final CloudinaryService cloudinaryService;

    public ItemService(ItemRepository itemRepository, CategoryService categoryService, CloudinaryService cloudinaryService) {
        this.itemRepository = itemRepository;
        this.categoryService = categoryService;
        this.cloudinaryService = cloudinaryService;
    }

    public Page<Item> searchItems(String keyword, Long categoryId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // If status is not specified, return all items regardless of status
        if (status == null || status.isEmpty()) {
            if (keyword != null && !keyword.isEmpty() && categoryId != null) {
                return itemRepository.findByNameContainingIgnoreCaseAndCategoryId(keyword, categoryId, pageable);
            } else if (keyword != null && !keyword.isEmpty()) {
                return itemRepository.findByNameContainingIgnoreCase(keyword, pageable);
            } else if (categoryId != null) {
                return itemRepository.findByCategoryId(categoryId, pageable);
            } else {
                return itemRepository.findAll(pageable);
            }
        }

        // If status is specified, filter by status
        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            return itemRepository.findByNameContainingIgnoreCaseAndCategoryIdAndStatus(keyword, categoryId, status, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            return itemRepository.findByNameContainingIgnoreCaseAndStatus(keyword, status, pageable);
        } else if (categoryId != null) {
            return itemRepository.findByCategoryIdAndStatus(categoryId, status, pageable);
        } else {
            return itemRepository.findByStatus(status, pageable);
        }
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Item saveItem(Item item, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(imageFile);
            List<String> imageUrls = item.getImageUrls();
            if (imageUrls == null) {
                imageUrls = new java.util.ArrayList<>();
            }
            imageUrls.add(imageUrl);
            item.setImageUrls(imageUrls);
        }
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.findById(id).ifPresent(item -> {
            List<String> imageUrls = item.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (String imageUrl : imageUrls) {
                    if (imageUrl == null || imageUrl.isBlank()) {
                        continue;
                    }
                    try {
                        cloudinaryService.deleteFile(imageUrl);
                    } catch (IOException e) {
                        System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
                    }
                }
            }
            itemRepository.deleteById(id);
        });
    }

    public List<Item> getItemsBySeller(User seller) {
        return itemRepository.findBySeller(seller);
    }

    public void markItemAsSold(Long itemId) {
        itemRepository.findById(itemId).ifPresent(item -> {
            item.setStatus("売却済");
            itemRepository.save(item);
        });
    }
    
    /* API */
    
    public Item save(Item item) {
        return itemRepository.save(item);
    }
    
}