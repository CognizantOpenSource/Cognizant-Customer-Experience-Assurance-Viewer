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

package com.cognizant.workbench.pipeline.controller;

import com.cognizant.workbench.pipeline.model.autosuggest.AutoSuggest;
import com.cognizant.workbench.pipeline.model.autosuggest.AutoSuggestDTO;
import com.cognizant.workbench.pipeline.service.AutoSuggestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/auto-suggest")
@Slf4j
@AllArgsConstructor
public class AutoSuggestController {

    private AutoSuggestService service;

    /**
     * Getting all AutoSuggestions from the DB
     *
     * @return list of AutoSuggestions
     */
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('AutoSuggest','auto-suggest.read')")
    public List<AutoSuggest> getAll() {
        return service.getAll();
    }

    /**
     * Getting the AutoSuggestion details based on the provided id
     *
     * @param id id of auto suggest
     * @return AutoSuggestion details
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('AutoSuggest','auto-suggest.read')")
    public AutoSuggest get(@PathVariable(name = "id") String id) {
        return service.get(id);
    }

    /**
     * creating autosuggestion based on the autosuggestion details
     *
     * @param autoSuggestDTO details of autosuggestion which has to store in db
     * @return post creating it will return the AutoSuggestion details which are stored in DB
     */
    @Validated
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('AutoSuggest','auto-suggest.create')")
    public AutoSuggest create(@Valid @RequestBody AutoSuggestDTO autoSuggestDTO) {
        return service.create(autoSuggestDTO);
    }

    /**
     * Updating AutoSuggestion details with provided details
     *
     * @param autoSuggestDTO AutoSuggestion details which are need to update in the DB
     * @return post updating the values in DB, will return the AutoSuggestion details which are stored in DB
     */
    @Validated
    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('AutoSuggest','auto-suggest.update')")
    public AutoSuggest update(@Valid @RequestBody AutoSuggestDTO autoSuggestDTO) {
        return service.update(autoSuggestDTO);
    }

    /**
     * Deleting AutoSuggestion details based on the provided id
     *
     * @param id id of AutoSuggestion
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('AutoSuggest','auto-suggest.delete')")
    public void remove(@PathVariable(name = "id") String id) {
        service.remove(id);
    }

    /**
     * Deleting AutoSuggestion details based on the provided id and Item
     *
     * @param id   id of AutoSuggestion
     * @param item item of AutoSuggestion
     */
    @DeleteMapping("/{id}/{item}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('AutoSuggest','auto-suggest.delete')")
    public void removeItem(@PathVariable(name = "id") String id, @PathVariable(name = "item") String item) {
        service.removeItem(id, item);
    }
}
