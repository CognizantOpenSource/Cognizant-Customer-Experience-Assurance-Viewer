/*
 *
 *   Copyright (C) 2023 - Cognizant Technology Solutions
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.cognizant.workbench.pipeline.service;

import com.cognizant.workbench.error.ResourceExistsException;
import com.cognizant.workbench.error.ResourceNotFoundException;
import com.cognizant.workbench.pipeline.model.autosuggest.AutoSuggest;
import com.cognizant.workbench.pipeline.model.autosuggest.AutoSuggestDTO;
import com.cognizant.workbench.pipeline.repo.AutoSuggestRepository;
import com.cognizant.workbench.pipeline.util.AutoSuggestUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
@AllArgsConstructor
public class AutoSuggestService {

    private AutoSuggestRepository repository;
    private AutoSuggestUtil util;

    public List<AutoSuggest> getAll() {
        return repository.findAll();
    }

    public AutoSuggest get(String id) {
        return assertAndGet(id);
    }

    public AutoSuggest create(@Valid AutoSuggestDTO dto) {
        Optional<AutoSuggest> optional = repository.findById(dto.getId());
        if (optional.isPresent()){
            throw new ResourceExistsException("AutoSuggest", "Type", dto.getId());
        }else{
            AutoSuggest autoSuggest = util.convertDeployDtoToNewEntity(dto, new AutoSuggest());
            return repository.insert(autoSuggest);
        }
    }

    public AutoSuggest update(@Valid AutoSuggestDTO dto) {
        AutoSuggest autoSuggest = assertAndGet(dto.getId());
        autoSuggest = util.convertDeployDtoToEntity(dto, autoSuggest);
        return repository.save(autoSuggest);
    }

    public void remove(String id) {
        assertAndGet(id);
        repository.deleteById(id);
    }

    public void removeItem(String id, String item) {
        AutoSuggest autoSuggest = assertAndGet(id);
        Set<String> items = autoSuggest.getItems();
        items.remove(item);
        autoSuggest.setItems(items);
        repository.save(autoSuggest);
    }

    private AutoSuggest assertAndGet(String id) {
        Optional<AutoSuggest> optional = repository.findById(id);
        if (optional.isPresent()) return optional.get();
        else throw new ResourceNotFoundException("AutoSuggest", "Type", id);
    }

    public List<AutoSuggest> addAllAutoSuggestList(List<AutoSuggest> autoSuggestList) {
        return repository.saveAll(autoSuggestList);
    }
}
