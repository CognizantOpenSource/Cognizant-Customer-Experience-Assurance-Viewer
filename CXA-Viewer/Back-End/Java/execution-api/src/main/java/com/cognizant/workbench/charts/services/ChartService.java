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

package com.cognizant.workbench.charts.services;

import com.cognizant.workbench.charts.beans.ChartItem;
import com.cognizant.workbench.charts.beans.ChartItemDTO;
import com.cognizant.workbench.charts.repo.ChartRepository;
import com.cognizant.workbench.charts.util.ChartUtil;
import com.cognizant.workbench.error.ResourceExistsException;
import com.cognizant.workbench.error.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by 784420 on 10/10/2019 2:27 PM
 */
@Slf4j
@Service
@AllArgsConstructor
public class ChartService {

    private ChartRepository repository;
    private ChartUtil util;

    private static final String COLLECTOR_CHART_ITEM = "Collector (ChartItem)";
    private static final String ID = "Id";

    public List<ChartItem> getAll() {
        return repository.findAll();
    }

    public ChartItem get(String id) {
        return assertAndGet(id);
    }

    public ChartItem create(ChartItemDTO dto) {
        repository.findById(dto.getId()).ifPresent(chartItem -> {
            throw new ResourceExistsException(COLLECTOR_CHART_ITEM, ID, dto.getId());
        });
        ChartItem chartItem = new ChartItem();
        util.dtoToNewEntity(dto, chartItem);
        return repository.insert(chartItem);
    }

    public ChartItem update(ChartItemDTO dto) {
        ChartItem chartItem = new ChartItem();
        util.dtoToEntity(dto, chartItem);
        return repository.save(chartItem);
    }

    public void remove(String id) {
        repository.deleteById(id);
    }

    private ChartItem assertAndGet(String id) {
        Optional<ChartItem> optional = repository.findById(id);
        return optional.orElseThrow(() -> new ResourceNotFoundException(COLLECTOR_CHART_ITEM, ID, id));
    }
}
