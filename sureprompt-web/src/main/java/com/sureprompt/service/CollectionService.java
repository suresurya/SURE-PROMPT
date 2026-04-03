package com.sureprompt.service;

import com.sureprompt.dto.CollectionDto;
import com.sureprompt.entity.Collection;
import com.sureprompt.entity.CollectionPrompt;
import com.sureprompt.entity.Prompt;
import com.sureprompt.entity.User;
import com.sureprompt.repository.CollectionPromptRepository;
import com.sureprompt.repository.CollectionRepository;
import com.sureprompt.repository.PromptRepository;
import com.sureprompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final CollectionPromptRepository collectionPromptRepository;
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;

    @Transactional
    public CollectionDto createCollection(Long userId, String name, boolean isPublic) {
        User user = userRepository.findById(userId).orElseThrow();
        
        Collection collection = Collection.builder()
                .user(user)
                .name(name)
                .isPublic(isPublic)
                .build();
                
        collection = collectionRepository.save(collection);
        
        return mapToDto(collection);
    }

    @Transactional
    public void addPromptToCollection(Long collectionId, Long promptId, Long userId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));
                
        if (!collection.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }
        
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new RuntimeException("Prompt not found"));
                
        if (!collectionPromptRepository.existsByCollectionIdAndPromptId(collectionId, promptId)) {
            CollectionPrompt cp = CollectionPrompt.builder()
                    .collection(collection)
                    .prompt(prompt)
                    .build();
            collectionPromptRepository.save(cp);
        }
    }

    public List<CollectionDto> getUserCollections(Long userId) {
        return collectionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CollectionDto mapToDto(Collection collection) {
        return CollectionDto.builder()
                .id(collection.getId())
                .name(collection.getName())
                .isPublic(collection.isPublic())
                .promptCount(collectionPromptRepository.countByCollectionId(collection.getId()))
                .build();
    }
}
