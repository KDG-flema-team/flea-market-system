package com.example.fleamarketsystem.service;

import com.example.fleamarketsystem.entity.Info;
import com.example.fleamarketsystem.repository.InfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InfoService {

    private final InfoRepository infoRepository;

    /**
     * Get all info entries
     */
    @Transactional(readOnly = true)
    public List<Info> getAllInfo() {
        return infoRepository.findAll();
    }

    /**
     * Get info by ID
     */
    @Transactional(readOnly = true)
    public Optional<Info> getInfoById(Long id) {
        return infoRepository.findById(id);
    }

    /**
     * Get all important info
     */
    @Transactional(readOnly = true)
    public List<Info> getImportantInfo() {
        return infoRepository.findByisImportantTrue();
    }

    /**
     * Create new info entry
     */
    @Transactional
    public Info createInfo(Info info) {
        if (info.getCreateAt() == null) {
            info.setCreateAt(LocalDateTime.now());
        }
        if (info.getIsImportant() == null) {
            info.setIsImportant(false);
        }
        return infoRepository.save(info);
    }

    /**
     * Update existing info entry
     */
    @Transactional
    public Info updateInfo(Long id, Info updatedInfo) {
        Info info = infoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Info not found with id: " + id));
        
        if (updatedInfo.getTitle() != null) {
            info.setTitle(updatedInfo.getTitle());
        }
        if (updatedInfo.getContent() != null) {
            info.setContent(updatedInfo.getContent());
        }
        if (updatedInfo.getImageUrl() != null) {
            info.setImageUrl(updatedInfo.getImageUrl());
        }
        if (updatedInfo.getIsImportant() != null) {
            info.setIsImportant(updatedInfo.getIsImportant());
        }
        
        return infoRepository.save(info);
    }

    /**
     * Delete info entry
     */
    @Transactional
    public void deleteInfo(Long id) {
        if (!infoRepository.existsById(id)) {
            throw new RuntimeException("Info not found with id: " + id);
        }
        infoRepository.deleteById(id);
    }
}
