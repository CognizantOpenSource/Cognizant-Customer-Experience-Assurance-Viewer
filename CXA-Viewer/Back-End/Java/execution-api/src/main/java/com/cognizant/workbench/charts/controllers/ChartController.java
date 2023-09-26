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

package com.cognizant.workbench.charts.controllers;

import com.cognizant.workbench.charts.beans.ChartItem;
import com.cognizant.workbench.charts.beans.ChartItemDTO;
import com.cognizant.workbench.charts.services.ChartService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by 784420 on 10/10/2019 1:06 PM
 */
@RestController
@RequestMapping("/chart")
@Slf4j
@AllArgsConstructor
public class ChartController {

    private ChartService service;

    /**
     * Getting list of charts which are available in the db
     * @return list of charts
     */
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission(#id, 'Chart','chart.read')")
    public List<ChartItem> getAll() {
        return service.getAll();
    }

    /**
     * chart details based on the id
     * @param id id of chart
     * @return chart details
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission(#id, 'Chart','chart.read')")
    public ChartItem get(@PathVariable(name = "id") String id) {
        return service.get(id);
    }

    /**
     * creating new chart based on the provided details
     * @param chartItemDTO chart details which need to create
     * @return post creation return the created chart details
     */
    @Validated
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission(#id, 'Chart','chart.create')")
    public ChartItem create(@Valid @RequestBody ChartItemDTO chartItemDTO) {
        return service.update(chartItemDTO);
    }

    /**
     * updating chart item with provided details
     * @param chartItemDTO chart details to update
     * @return updated chart details
     */
    @Validated
    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission(#id, 'Chart','chart.update')")
    public ChartItem update(@Valid @RequestBody ChartItemDTO chartItemDTO) {
        return service.update(chartItemDTO);
    }

    /**
     * deleting chart based on the id provided
     * @param id chart id which has to delete from db
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#id, 'Chart','chart.delete')")
    public void remove(@PathVariable(name = "id") String id) {
        service.remove(id);
    }
}
