package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.models.EnglishCardEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractEnglishCardService<E extends EnglishCardEntity> {

    protected JpaRepository<E,Long> repository;
    //protected final EnglishCardMapper<E> mapper;

    public AbstractEnglishCardService(JpaRepository<E, Long> repository) {
        this.repository = repository;
    }

    /**
     * Get e.
     *
     * @param id the id
     * @return the e
     */
    protected E get(Long id) {
        return repository.findById(id).get();
                //.orElseThrow(() -> new DemoNotFoundException(String.format("Entity %s not found", id)));
    }




    public List<E> findAll() {
        log.debug("Finding all pageable");
        final List<E> elementos = repository.findAll();
        return elementos;
        //return new PageImpl<>(mapper.toDtos(page.getContent()), page.getPageable(), page.getTotalElements());
    }

    /**
     * Find dto.
     *
     * @param id the id
     * @return the dto
     */
    public E find(Long id) {
        log.debug("Finding {}", id);
        return this.get(id);
    }

    /**
     * Delete.
     *
     * @param id the id
     */
    public void delete(Long id) {
        log.debug("Deleting {}", id);
        repository.delete(get(id));
    }

}
