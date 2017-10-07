package ru.anhot.comfortville.service.impl;

import ru.anhot.comfortville.service.SectionService;
import ru.anhot.comfortville.domain.Section;
import ru.anhot.comfortville.repository.SectionRepository;
import ru.anhot.comfortville.repository.search.SectionSearchRepository;
import ru.anhot.comfortville.service.dto.SectionDTO;
import ru.anhot.comfortville.service.mapper.SectionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Section.
 */
@Service
@Transactional
public class SectionServiceImpl implements SectionService{

    private final Logger log = LoggerFactory.getLogger(SectionServiceImpl.class);

    private final SectionRepository sectionRepository;

    private final SectionMapper sectionMapper;

    private final SectionSearchRepository sectionSearchRepository;

    public SectionServiceImpl(SectionRepository sectionRepository, SectionMapper sectionMapper, SectionSearchRepository sectionSearchRepository) {
        this.sectionRepository = sectionRepository;
        this.sectionMapper = sectionMapper;
        this.sectionSearchRepository = sectionSearchRepository;
    }

    /**
     * Save a section.
     *
     * @param sectionDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public SectionDTO save(SectionDTO sectionDTO) {
        log.debug("Request to save Section : {}", sectionDTO);
        Section section = sectionMapper.toEntity(sectionDTO);
        section = sectionRepository.save(section);
        SectionDTO result = sectionMapper.toDto(section);
        sectionSearchRepository.save(section);
        return result;
    }

    /**
     *  Get all the sections.
     *
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<SectionDTO> findAll() {
        log.debug("Request to get all Sections");
        return sectionRepository.findAll().stream()
            .map(sectionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get one section by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public SectionDTO findOne(Long id) {
        log.debug("Request to get Section : {}", id);
        Section section = sectionRepository.findOne(id);
        return sectionMapper.toDto(section);
    }

    /**
     *  Delete the  section by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Section : {}", id);
        sectionRepository.delete(id);
        sectionSearchRepository.delete(id);
    }

    /**
     * Search for the section corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<SectionDTO> search(String query) {
        log.debug("Request to search Sections for query {}", query);
        return StreamSupport
            .stream(sectionSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(sectionMapper::toDto)
            .collect(Collectors.toList());
    }
}
