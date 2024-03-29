/**
 * Copyright 2012 Muzima Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.muzima.api.dao.impl;

import com.muzima.api.dao.FormDataDao;
import com.muzima.api.model.FormData;
import com.muzima.search.api.filter.Filter;
import com.muzima.search.api.filter.FilterFactory;
import com.muzima.search.api.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FormDataDaoImpl extends SearchableDaoImpl<FormData> implements FormDataDao {

    private static final String TAG = FormDataDaoImpl.class.getSimpleName();

    protected FormDataDaoImpl() {
        super(FormData.class);
    }

    /**
     * {@inheritDoc}
     *
     * @see com.muzima.api.dao.FormDataDao#getFormDataByUuid(String)
     */
    @Override
    public FormData getFormDataByUuid(final String uuid) throws IOException {
        return service.getObject(uuid, daoClass);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormDataDao#getAllFormData(String, String, String)
     */
    @Override
    public List<FormData> getAllFormData(final String patientUuid, final String userUuid,
                                         final String status) throws IOException {
        List<Filter> filters = new ArrayList<Filter>();
        if (!StringUtil.isEmpty(patientUuid)) {
            Filter patientFilter = FilterFactory.createFilter("patientUuid", patientUuid);
            filters.add(patientFilter);
        }
        if (!StringUtil.isEmpty(userUuid)) {
            Filter userFilter = FilterFactory.createFilter("userUuid", userUuid);
            filters.add(userFilter);
        }
        if (!StringUtil.isEmpty(status)) {
            Filter statusFilter = FilterFactory.createFilter("status", status);
            filters.add(statusFilter);
        }
        return service.getObjects(filters, daoClass);
    }

    @Override
    public List<FormData> getAllFormData(final String patientUuid, final String userUuid, final String status,
                                         final Integer page, final Integer pageSize) throws IOException {
        List<Filter> filters = new ArrayList<Filter>();
        if (!StringUtil.isEmpty(patientUuid)) {
            Filter patientFilter = FilterFactory.createFilter("patientUuid", patientUuid);
            filters.add(patientFilter);
        }
        if (!StringUtil.isEmpty(userUuid)) {
            Filter userFilter = FilterFactory.createFilter("userUuid", userUuid);
            filters.add(userFilter);
        }
        if (!StringUtil.isEmpty(status)) {
            Filter statusFilter = FilterFactory.createFilter("status", status);
            filters.add(statusFilter);
        }
        return service.getObjects(filters, daoClass, page, pageSize);
    }
}
