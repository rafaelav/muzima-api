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
package com.muzima.api.service.impl;

import com.google.inject.Inject;
import com.muzima.api.dao.FormDao;
import com.muzima.api.dao.FormDataDao;
import com.muzima.api.dao.FormTemplateDao;
import com.muzima.api.model.Form;
import com.muzima.api.model.FormData;
import com.muzima.api.model.FormTemplate;
import com.muzima.api.service.FormService;
import com.muzima.search.api.util.CollectionUtil;
import com.muzima.search.api.util.StringUtil;
import com.muzima.util.Constants;
import org.apache.lucene.queryParser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormServiceImpl implements FormService {

    @Inject
    private FormDao formDao;

    @Inject
    private FormDataDao formDataDao;

    @Inject
    private FormTemplateDao formTemplateDao;

    protected FormServiceImpl() {
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#downloadFormByUuid(String)
     */
    @Override
    public Form downloadFormByUuid(final String uuid) throws IOException {
        Form form = null;
        Map<String, String> parameter = new HashMap<String, String>() {{
            put("uuid", uuid);
        }};
        List<Form> forms = formDao.download(parameter, Constants.UUID_FORM_RESOURCE);
        if (!CollectionUtil.isEmpty(forms)) {
            if (forms.size() > 1) {
                throw new IOException("Unable to uniquely identify a form record.");
            }
            form = forms.get(0);
        }
        return form;
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#downloadFormsByName(String)
     */
    @Override
    public List<Form> downloadFormsByName(final String name) throws IOException {
        Map<String, String> parameter = new HashMap<String, String>() {{
            put("q", name);
        }};
        return formDao.download(parameter, Constants.SEARCH_FORM_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#saveForm(com.muzima.api.model.Form)
     */
    @Override
    public void saveForm(final Form form) throws IOException {
        formDao.save(form, Constants.UUID_FORM_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#saveForms(java.util.List)
     */
    @Override
    public void saveForms(final List<Form> forms) throws IOException {
        formDao.save(forms, Constants.UUID_FORM_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#updateForm(com.muzima.api.model.Form)
     */
    @Override
    public void updateForm(final Form form) throws IOException {
        formDao.update(form, Constants.UUID_FORM_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#updateForms(java.util.List)
     */
    @Override
    public void updateForms(final List<Form> forms) throws IOException {
        formDao.update(forms, Constants.UUID_FORM_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#getFormByUuid(String)
     */
    @Override
    public Form getFormByUuid(final String uuid) throws IOException {
        return formDao.getByUuid(uuid);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#countFormByName(String)
     */
    @Override
    public Integer countFormByName(final String name) throws IOException {
        return formDao.countByName(name);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#getFormByName(String)
     */
    @Override
    public List<Form> getFormByName(final String name) throws IOException {
        return formDao.getByName(name);
    }

    /**
     * {@inheritDoc}
     *
     * @see com.muzima.api.service.FormService#countAllForms()
     */
    @Override
    public Integer countAllForms() throws IOException {
        return formDao.countAll();
    }

    /**
     * {@inheritDoc}
     *
     * @see com.muzima.api.service.FormService#getAllForms()
     */
    @Override
    public List<Form> getAllForms() throws IOException {
        return formDao.getAll();
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#deleteForm(com.muzima.api.model.Form)
     */
    @Override
    public void deleteForm(final Form form) throws IOException {
        formDao.delete(form, Constants.UUID_FORM_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#deleteForms(java.util.List)
     */
    @Override
    public void deleteForms(final List<Form> forms) throws IOException {
        formDao.delete(forms, Constants.UUID_FORM_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#isFormTemplateDownloaded(String)
     */
    @Override
    public Boolean isFormTemplateDownloaded(final String formUuid) throws IOException {
        return formTemplateDao.exists(formUuid);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#downloadFormTemplateByUuid(String)
     */
    @Override
    public FormTemplate downloadFormTemplateByUuid(final String uuid) throws IOException {
        FormTemplate formTemplate = null;
        Map<String, String> parameter = new HashMap<String, String>() {{
            put("uuid", uuid);
        }};
        List<FormTemplate> formTemplates = formTemplateDao.download(parameter, Constants.UUID_FORM_TEMPLATE_RESOURCE);
        if (!CollectionUtil.isEmpty(formTemplates)) {

            if (formTemplates.size() > 1) {
                throw new IOException("Unable to uniquely identify a form template record.");
            }
            formTemplate = formTemplates.get(0);
        }
        return formTemplate;
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#downloadFormTemplatesByName(String)
     */
    @Override
    public List<FormTemplate> downloadFormTemplatesByName(final String name) throws IOException, ParseException {
        Map<String, String> parameter = new HashMap<String, String>() {{
            put("q", name);
        }};
        return formTemplateDao.download(parameter, Constants.SEARCH_FORM_TEMPLATE_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#saveFormTemplate(com.muzima.api.model.FormTemplate)
     */
    @Override
    public void saveFormTemplate(final FormTemplate formTemplate) throws IOException {
        formTemplateDao.save(formTemplate, Constants.UUID_FORM_TEMPLATE_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#saveFormTemplates(java.util.List)
     */
    @Override
    public void saveFormTemplates(final List<FormTemplate> formTemplates) throws IOException {
        formTemplateDao.save(formTemplates, Constants.UUID_FORM_TEMPLATE_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#getFormTemplateByUuid(String)
     */
    @Override
    public FormTemplate getFormTemplateByUuid(final String uuid) throws IOException {
        return formTemplateDao.getByUuid(uuid);
    }

    /**
     * {@inheritDoc}
     *
     * @see com.muzima.api.service.FormService#countAllFormTemplates()
     */
    @Override
    public Integer countAllFormTemplates() throws IOException {
        return formTemplateDao.countAll();
    }

    /**
     * {@inheritDoc}
     *
     * @see com.muzima.api.service.FormService#getAllFormTemplates()
     */
    @Override
    public List<FormTemplate> getAllFormTemplates() throws IOException {
        return formTemplateDao.getAll();
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#deleteFormTemplate(com.muzima.api.model.FormTemplate)
     */
    @Override
    public void deleteFormTemplate(final FormTemplate formTemplate) throws IOException {
        formTemplateDao.delete(formTemplate, Constants.UUID_FORM_TEMPLATE_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#deleteFormTemplates(java.util.List)
     */
    @Override
    public void deleteFormTemplates(final List<FormTemplate> formTemplates) throws IOException {
        formTemplateDao.delete(formTemplates, Constants.UUID_FORM_TEMPLATE_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#saveFormData(com.muzima.api.model.FormData)
     */
    @Override
    public void saveFormData(final FormData formData) throws IOException {
        formDataDao.save(formData, Constants.LOCAL_FORM_DATA_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#updateFormData(com.muzima.api.model.FormData)
     */
    @Override
    public void updateFormData(final FormData formData) throws IOException {
        formDataDao.update(formData, Constants.LOCAL_FORM_DATA_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#getFormDataByUuid(String)
     */
    @Override
    public FormData getFormDataByUuid(final String uuid) throws IOException {
        return formDataDao.getFormDataByUuid(uuid);
    }

    /**
     * {@inheritDoc}
     *
     * @see com.muzima.api.service.FormService#countAllFormData()
     */
    @Override
    public Integer countAllFormData() throws IOException {
        return formDataDao.countAll();
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#getAllFormData(String)
     */
    @Override
    public List<FormData> getAllFormData(final String status) throws IOException {
        return formDataDao.getAllFormData(StringUtil.EMPTY, StringUtil.EMPTY, status);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#getFormDataByUser(String, String)
     */
    @Override
    public List<FormData> getFormDataByUser(final String userUuid, final String status) throws IOException {
        return formDataDao.getAllFormData(StringUtil.EMPTY, userUuid, status);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#getFormDataByPatient(String, String)
     */
    @Override
    public List<FormData> getFormDataByPatient(final String patientUuid, final String status) throws IOException {
        return formDataDao.getAllFormData(patientUuid, StringUtil.EMPTY, status);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#deleteFormData(com.muzima.api.model.FormData)
     */
    @Override
    public void deleteFormData(final FormData formData) throws IOException {
        formDataDao.delete(formData, Constants.LOCAL_FORM_DATA_RESOURCE);
    }

    /**
     * {@inheritDoc}
     *
     * @see FormService#deleteFormData(java.util.List)
     */
    @Override
    public void deleteFormData(final List<FormData> formData) throws IOException {
        formDataDao.delete(formData, Constants.LOCAL_FORM_DATA_RESOURCE);
    }
}
