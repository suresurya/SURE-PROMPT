package com.sureprompt.service;

import com.sureprompt.entity.Tag;
import com.sureprompt.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<String> getAllActiveTags() {
        return tagRepository.findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public Tag getOrCreateTag(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> tagRepository.save(Tag.builder().name(name).active(true).build()));
    }
}
