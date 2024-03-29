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
package com.muzima.api.service;

import com.muzima.api.context.Context;
import com.muzima.api.context.ContextFactory;
import com.muzima.api.model.Form;
import com.muzima.api.model.FormData;
import com.muzima.api.model.FormTemplate;
import com.muzima.search.api.util.StringUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;

/**
 * TODO: Write brief description about the class here.
 */
public class FormServiceTest {
    // baseline form
    private Form form;
    private List<Form> forms;

    private Context context;
    private FormService formService;

    private static int nextInt(int size) {
        Random random = new Random();
        return random.nextInt(size);
    }

    @Before
    public void prepare() throws Exception {
        context = ContextFactory.createContext();
        context.openSession();
        if (!context.isAuthenticated()) {
            context.authenticate("admin", "test", "http://localhost:8081/openmrs-standalone");
        }
        formService = context.getFormService();
        forms = formService.downloadFormsByName(StringUtil.EMPTY);
        form = forms.get(nextInt(forms.size()));
    }

    @After
    public void cleanUp() throws Exception {
        String tmpDirectory = System.getProperty("java.io.tmpdir");
        String lucenePath = tmpDirectory + "/muzima";
        File luceneDirectory = new File(lucenePath);
        for (String filename : luceneDirectory.list()) {
            File file = new File(luceneDirectory, filename);
            Assert.assertTrue(file.delete());
        }
        context.deauthenticate();
        context.closeSession();
    }

    /**
     * @verifies download form with matching uuid.
     * @see FormService#downloadFormByUuid(String)
     */
    @Test
    public void downloadFormByUuid_shouldDownloadFormWithMatchingUuid() throws Exception {
        Form downloadedForm = formService.downloadFormByUuid(form.getUuid());
        assertThat(downloadedForm, samePropertyValuesAs(form));
    }

    /**
     * @verifies download all form with partially matched name.
     * @see FormService#downloadFormsByName(String)
     */
    @Test
    public void downloadFormsByName_shouldDownloadAllFormWithPartiallyMatchedName() throws Exception {
        String name = form.getName();
        String partialName = name.substring(name.length() - 1);
        List<Form> downloadedForms = formService.downloadFormsByName(partialName);
        for (Form downloadedForm : downloadedForms) {
            assertThat(downloadedForm.getName(), containsString(partialName));
        }
    }

    /**
     * @verifies download all form when name is empty.
     * @see FormService#downloadFormsByName(String)
     */
    @Test
    public void downloadFormsByName_shouldDownloadAllFormWhenNameIsEmpty() throws Exception {
        List<Form> downloadedForms = formService.downloadFormsByName(StringUtil.EMPTY);
        assertThat(downloadedForms, hasSize(forms.size()));
    }

    /**
     * @verifies save form to local data repository.
     * @see FormService#saveForm(com.muzima.api.model.Form)
     */
    @Test
    public void saveForm_shouldSaveFormToLocalDataRepository() throws Exception {
        int formCounter = formService.countAllForms();
        formService.saveForm(form);
        assertThat(formCounter + 1, equalTo(formService.countAllForms()));
        formService.saveForm(form);
        assertThat(formCounter + 1, equalTo(formService.countAllForms()));
    }

    /**
     * @verifies save list of forms to local data repository.
     * @see FormService#saveForms(java.util.List)
     */
    @Test
    public void saveForms_shouldSaveListOfFormsToLocalDataRepository() throws Exception {
        int formCounter = formService.countAllForms();
        formService.saveForms(forms);
        assertThat(formCounter + forms.size(), equalTo(formService.countAllForms()));
        formService.saveForm(form);
        assertThat(formCounter + forms.size(), equalTo(formService.countAllForms()));
    }

    /**
     * @verifies replace existing form in local data repository.
     * @see FormService#updateForm(com.muzima.api.model.Form)
     */
    @Test
    public void updateForm_shouldReplaceExistingFormInLocalDataRepository() throws Exception {
        Form nullForm = formService.getFormByUuid(form.getUuid());
        assertThat(nullForm, nullValue());
        formService.saveForm(form);
        Form savedForm = formService.getFormByUuid(form.getUuid());
        assertThat(savedForm, notNullValue());
        assertThat(savedForm, samePropertyValuesAs(form));

        String formName = "New Form Name";
        savedForm.setName(formName);
        formService.updateForm(savedForm);
        Form updatedForm = formService.getFormByUuid(savedForm.getUuid());
        assertThat(updatedForm, not(samePropertyValuesAs(form)));
        assertThat(updatedForm.getName(), equalTo(formName));
    }

    /**
     * @verifies replace list of forms in local data repository.
     * @see FormService#updateForms(java.util.List)
     */
    @Test
    public void updateForms_shouldReplaceListOfFormsInLocalDataRepository() throws Exception {
        int counter = 0;
        formService.saveForms(forms);
        List<Form> savedForms = formService.getAllForms();
        for (Form form : savedForms) {
            form.setName("New Name For Form: " + counter++);
        }
        formService.updateForms(savedForms);
        List<Form> updatedForms = formService.getAllForms();
        for (Form updatedForm : updatedForms) {
            for (Form form : forms) {
                assertThat(updatedForm, not(samePropertyValuesAs(form)));
            }
            assertThat(updatedForm.getName(), containsString("New Name For Form"));
        }
    }

    /**
     * @verifies return form with matching uuid.
     * @see FormService#getFormByUuid(String)
     */
    @Test
    public void getFormByUuid_shouldReturnFormWithMatchingUuid() throws Exception {
        Form nullForm = formService.getFormByUuid(form.getUuid());
        assertThat(nullForm, nullValue());
        formService.saveForm(form);
        Form savedForm = formService.getFormByUuid(form.getUuid());
        assertThat(savedForm, notNullValue());
        assertThat(savedForm, samePropertyValuesAs(form));
    }

    /**
     * @verifies return null when no form match the uuid.
     * @see FormService#getFormByUuid(String)
     */
    @Test
    public void getFormByUuid_shouldReturnNullWhenNoFormMatchTheUuid() throws Exception {
        Form nullForm = formService.getFormByUuid(form.getUuid());
        assertThat(nullForm, nullValue());
        formService.saveForm(form);
        String randomUuid = UUID.randomUUID().toString();
        Form savedForm = formService.getFormByUuid(randomUuid);
        assertThat(savedForm, nullValue());
    }

    /**
     * @verifies count all forms with matching name.
     * @see FormService#countFormByName(String)
     */
    @Test
    public void countFormByName_shouldCountAllFormsWithMatchingName() throws Exception {
        assertThat(0, equalTo(formService.countAllForms()));
        formService.saveForm(form);
        assertThat(1, equalTo(formService.countFormByName(form.getName())));
        formService.saveForm(form);
        assertThat(1, equalTo(formService.countFormByName(form.getName())));
    }

    /**
     * @verifies return form with matching name.
     * @see FormService#getFormByName(String)
     */
    @Test
    public void getFormByName_shouldReturnFormWithMatchingName() throws Exception {
        assertThat(0, equalTo(formService.countAllForms()));
        formService.saveForm(form);
        List<Form> savedStaticForms = formService.getFormByName(form.getName());
        assertThat(1, equalTo(savedStaticForms.size()));
    }

    /**
     * @verifies return null when no form match the name.
     * @see FormService#getFormByName(String)
     */
    @Test
    public void getFormByName_shouldReturnNullWhenNoFormMatchTheName() throws Exception {
        String randomName = UUID.randomUUID().toString();
        formService.saveForm(form);
        List<Form> savedStaticForms = formService.getFormByName(randomName);
        assertThat(0, equalTo(savedStaticForms.size()));
        assertThat(form, not(isIn(savedStaticForms)));
    }

    /**
     * @verifies return number of all forms in local data repository.
     * @see FormService#countAllForms()
     */
    @Test
    public void countAllForms_shouldReturnNumberOfAllFormsInLocalDataRepository() throws Exception {
        assertThat(0, equalTo(formService.countAllForms()));
        formService.saveForms(forms);
        assertThat(forms.size(), equalTo(formService.countAllForms()));
    }

    /**
     * @verifies return all registered forms.
     * @see FormService#getAllForms()
     */
    @Test
    public void getAllForms_shouldReturnAllRegisteredForms() throws Exception {
        formService.saveForms(forms);
        List<Form> savedForms = formService.getAllForms();
        assertThat(savedForms, hasSize(forms.size()));
    }

    /**
     * @verifies return empty list when no form is registered.
     * @see FormService#getAllForms()
     */
    @Test
    public void getAllForms_shouldReturnEmptyListWhenNoFormIsRegistered() throws Exception {
        assertThat(formService.getAllForms(), hasSize(0));
    }

    /**
     * @verifies delete form from local data repository.
     * @see FormService#deleteForm(com.muzima.api.model.Form)
     */
    @Test
    public void deleteForm_shouldDeleteFormFromLocalDataRepository() throws Exception {
        assertThat(formService.getAllForms(), hasSize(0));
        formService.saveForms(forms);
        int formCount = forms.size();
        assertThat(formService.getAllForms(), hasSize(formCount));
        for (Form form : forms) {
            formService.deleteForm(form);
            assertThat(formService.getAllForms(), hasSize(--formCount));
        }
    }

    /**
     * @verifies delete list of forms from local data repository.
     * @see FormService#deleteForms(java.util.List)
     */
    @Test
    public void deleteForms_shouldDeleteListOfFormsFromLocalDataRepository() throws Exception {
        assertThat(formService.getAllForms(), hasSize(0));
        formService.saveForms(forms);
        List<Form> savedForms = formService.getAllForms();
        assertThat(savedForms, hasSize(forms.size()));
        formService.deleteForms(savedForms);
        assertThat(formService.getAllForms(), hasSize(0));
    }

    /**
     * @verifies return true if the template of a form is downloaded.
     * @see FormService#isFormTemplateDownloaded(String)
     */
    @Test
    public void isFormTemplateDownloaded_shouldReturnTrueIfTheTemplateOfAFormIsDownloaded() throws Exception {
        assertThat(formService.getAllFormTemplates(), hasSize(0));
        FormTemplate formTemplate = formService.downloadFormTemplateByUuid(form.getUuid());
        formService.saveFormTemplate(formTemplate);
        assertThat(formService.getAllFormTemplates(), hasSize(1));
        assertThat(formService.isFormTemplateDownloaded(form.getUuid()), equalTo(true));
    }

    /**
     * @verifies download the form template by the uuid of the form.
     * @see FormService#downloadFormTemplateByUuid(String)
     */
    @Test
    public void downloadFormTemplateByUuid_shouldDownloadTheFormTemplateByTheUuidOfTheForm() throws Exception {
        assertThat(formService.getAllFormTemplates(), hasSize(0));
        FormTemplate formTemplate = formService.downloadFormTemplateByUuid(form.getUuid());
        formService.saveFormTemplate(formTemplate);
        assertThat(formService.getAllFormTemplates(), hasSize(1));
        assertThat(formService.isFormTemplateDownloaded(form.getUuid()), equalTo(true));
    }

    /**
     * @verifies download the form template by the name of the form.
     * @see FormService#downloadFormTemplatesByName(String)
     */
    @Test
    public void downloadFormTemplatesByName_shouldDownloadTheFormTemplateByTheNameOfTheForm() throws Exception {
        assertThat(formService.getAllFormTemplates(), hasSize(0));
        List<FormTemplate> formTemplates = formService.downloadFormTemplatesByName(form.getName());
        formService.saveFormTemplates(formTemplates);
        assertThat(formService.getAllFormTemplates(), hasSize(formTemplates.size()));
    }

    /**
     * @verifies save the form template to the local data repository.
     * @see FormService#saveFormTemplate(com.muzima.api.model.FormTemplate)
     */
    @Test
    public void saveFormTemplate_shouldSaveTheFormTemplateToTheLocalDataRepository() throws Exception {
        assertThat(formService.getAllFormTemplates(), hasSize(0));
        FormTemplate formTemplate = formService.downloadFormTemplateByUuid(form.getUuid());
        formService.saveFormTemplate(formTemplate);
        assertThat(formService.getAllFormTemplates(), hasSize(1));
        assertThat(formService.isFormTemplateDownloaded(form.getUuid()), equalTo(true));
    }

    /**
     * @verifies save the list of form templates to local data repository.
     * @see FormService#saveFormTemplates(java.util.List)
     */
    @Test
    public void saveFormTemplates_shouldSaveTheListOfFormTemplatesToLocalDataRepository() throws Exception {
        assertThat(formService.getAllFormTemplates(), hasSize(0));
        List<FormTemplate> formTemplates = formService.downloadFormTemplatesByName(form.getName());
        formService.saveFormTemplates(formTemplates);
        assertThat(formService.getAllFormTemplates(), hasSize(formTemplates.size()));
    }

    /**
     * @verifies get the form template by the uuid.
     * @see FormService#getFormTemplateByUuid(String)
     */
    @Test
    public void getFormTemplateByUuid_shouldGetTheFormTemplateByTheUuid() throws Exception {
        assertThat(formService.getAllFormTemplates(), hasSize(0));
        FormTemplate formTemplate = formService.downloadFormTemplateByUuid(form.getUuid());
        formService.saveFormTemplate(formTemplate);
        assertThat(formService.getAllFormTemplates(), hasSize(1));
        assertThat(formService.isFormTemplateDownloaded(form.getUuid()), equalTo(true));
        FormTemplate savedFormTemplate = formService.getFormTemplateByUuid(formTemplate.getUuid());
        assertThat(savedFormTemplate, samePropertyValuesAs(formTemplate));
    }

    /**
     * @verifies count all form templates in local data repository.
     * @see FormService#countAllFormTemplates()
     */
    @Test
    public void countAllFormTemplates_shouldCountAllFormTemplatesInLocalDataRepository() throws Exception {
        assertThat(formService.getAllFormTemplates(), hasSize(0));
        List<FormTemplate> formTemplates = formService.downloadFormTemplatesByName(form.getName());
        formService.saveFormTemplates(formTemplates);
        assertThat(formService.getAllFormTemplates(), hasSize(formTemplates.size()));
        assertThat(formService.countAllFormTemplates(), equalTo(formTemplates.size()));
    }

    /**
     * @verifies return all saved form templates fom local data repository.
     * @see FormService#getAllFormTemplates()
     */
    @Test
    public void getAllFormTemplates_shouldReturnAllSavedFormTemplatesFomLocalDataRepository() throws Exception {
        assertThat(formService.getAllFormTemplates(), hasSize(0));
        List<FormTemplate> formTemplates = formService.downloadFormTemplatesByName(form.getName());
        formService.saveFormTemplates(formTemplates);
        assertThat(formService.getAllFormTemplates(), hasSize(formTemplates.size()));
    }

    /**
     * @verifies delete the form template from local data repository.
     * @see FormService#deleteFormTemplate(com.muzima.api.model.FormTemplate)
     */
    @Test
    public void deleteFormTemplate_shouldDeleteTheFormTemplateFromLocalDataRepository() throws Exception {
        assertThat(formService.getAllFormTemplates(), hasSize(0));
        List<FormTemplate> formTemplates = formService.downloadFormTemplatesByName(form.getName());
        formService.saveFormTemplates(formTemplates);
        List<FormTemplate> savedFormTemplates = formService.getAllFormTemplates();
        assertThat(savedFormTemplates, hasSize(formTemplates.size()));
        int formTemplateCounter = savedFormTemplates.size();
        for (FormTemplate savedFormTemplate : savedFormTemplates) {
            formService.deleteFormTemplate(savedFormTemplate);
            formTemplateCounter--;
            assertThat(formService.countAllFormTemplates(), equalTo(formTemplateCounter));
        }
        assertThat(formService.countAllFormTemplates(), equalTo(0));
    }

    /**
     * @verifies delete the list of forms from local data repository.
     * @see FormService#deleteFormTemplates(java.util.List)
     */
    @Test
    public void deleteFormTemplates_shouldDeleteTheListOfFormsFromLocalDataRepository() throws Exception {
        assertThat(formService.getAllFormTemplates(), hasSize(0));
        List<FormTemplate> formTemplates = formService.downloadFormTemplatesByName(form.getName());
        formService.saveFormTemplates(formTemplates);
        List<FormTemplate> savedFormTemplates = formService.getAllFormTemplates();
        assertThat(savedFormTemplates, hasSize(formTemplates.size()));
        assertThat(formService.countAllFormTemplates(), equalTo(formTemplates.size()));
        formService.deleteFormTemplates(savedFormTemplates);
        assertThat(formService.countAllFormTemplates(), equalTo(0));
    }

    /**
     * @verifies save form data to local data repository.
     * @see FormService#saveFormData(com.muzima.api.model.FormData)
     */
    @Test
    public void saveFormData_shouldSaveFormDataToLocalDataRepository() throws Exception {
        assertThat(formService.countAllFormData(), equalTo(0));
        FormData formData = new FormData();
        formService.saveFormData(formData);
        assertThat(formService.countAllFormData(), equalTo(1));
    }

    /**
     * @verifies return form data by the uuid.
     * @see FormService#getFormDataByUuid(String)
     */
    @Test
    public void getFormDataByUuid_shouldReturnFormDataByTheUuid() throws Exception {
        String formDataUuid = UUID.randomUUID().toString();
        assertThat(formService.countAllFormData(), equalTo(0));
        FormData formData = new FormData();
        formData.setUuid(formDataUuid);
        formService.saveFormData(formData);
        assertThat(formService.countAllFormData(), equalTo(1));
        assertThat(formService.getFormDataByUuid(formDataUuid), samePropertyValuesAs(formData));

    }

    /**
     * @verifies count all form data in local data repository.
     * @see FormService#countAllFormData()
     */
    @Test
    public void countAllFormData_shouldCountAllFormDataInLocalDataRepository() throws Exception {
        assertThat(formService.countAllFormData(), equalTo(0));
        FormData formData = new FormData();
        formService.saveFormData(formData);
        assertThat(formService.countAllFormData(), equalTo(1));
    }

    /**
     * @verifies return all form data with matching status from local data repository.
     * @see FormService#getAllFormData(String)
     */
    @Test
    public void getAllFormData_shouldReturnAllFormDataWithMatchingStatusFromLocalDataRepository() throws Exception {
        assertThat(formService.countAllFormData(), equalTo(0));
        FormData firstFormData = new FormData();
        firstFormData.setUuid(UUID.randomUUID().toString());
        firstFormData.setStatus("Some random status");
        formService.saveFormData(firstFormData);
        FormData secondFormData = new FormData();
        secondFormData.setUuid(UUID.randomUUID().toString());
        secondFormData.setStatus("Some other random status");
        formService.saveFormData(secondFormData);
        assertThat(formService.countAllFormData(), equalTo(2));
        List<FormData> savedFormData = formService.getAllFormData("Some random status");
        assertThat(savedFormData, hasSize(1));
        for (FormData formData : savedFormData) {
            assertThat(formData.getStatus(), equalTo("Some random status"));
        }
    }

    /**
     * @verifies return all form data with matching user and status.
     * @see FormService#getFormDataByUser(String, String)
     */
    @Test
    public void getFormDataByUser_shouldReturnAllFormDataWithMatchingUserAndStatus() throws Exception {
        String userUuid = UUID.randomUUID().toString();
        assertThat(formService.countAllFormData(), equalTo(0));
        FormData firstFormData = new FormData();
        firstFormData.setUuid(UUID.randomUUID().toString());
        firstFormData.setStatus("Some random status");
        firstFormData.setUserUuid(userUuid);
        formService.saveFormData(firstFormData);
        FormData secondFormData = new FormData();
        secondFormData.setUuid(UUID.randomUUID().toString());
        secondFormData.setStatus("Some other random status");
        secondFormData.setUserUuid(userUuid);
        formService.saveFormData(secondFormData);
        assertThat(formService.countAllFormData(), equalTo(2));
        List<FormData> savedFormData = formService.getFormDataByUser(userUuid, "Some random status");
        assertThat(savedFormData, hasSize(1));
        for (FormData formData : savedFormData) {
            assertThat(formData.getStatus(), equalTo("Some random status"));
        }
    }

    /**
     * @verifies return all form data with matching patient and status.
     * @see FormService#getFormDataByPatient(String, String)
     */
    @Test
    public void getFormDataByPatient_shouldReturnAllFormDataWithMatchingPatientAndStatus() throws Exception {
        String patientUuid = UUID.randomUUID().toString();
        assertThat(formService.countAllFormData(), equalTo(0));
        FormData firstFormData = new FormData();
        firstFormData.setUuid(UUID.randomUUID().toString());
        firstFormData.setStatus("Some random status");
        firstFormData.setPatientUuid(patientUuid);
        formService.saveFormData(firstFormData);
        FormData secondFormData = new FormData();
        secondFormData.setUuid(UUID.randomUUID().toString());
        secondFormData.setStatus("Some other random status");
        secondFormData.setPatientUuid(patientUuid);
        formService.saveFormData(secondFormData);
        assertThat(formService.countAllFormData(), equalTo(2));
        List<FormData> savedFormData = formService.getFormDataByPatient(patientUuid, "Some random status");
        assertThat(savedFormData, hasSize(1));
        for (FormData formData : savedFormData) {
            assertThat(formData.getStatus(), equalTo("Some random status"));
        }
    }

    /**
     * @verifies delete form data from local data repository.
     * @see FormService#deleteFormData(com.muzima.api.model.FormData)
     */
    @Test
    public void deleteFormData_shouldDeleteFormDataFromLocalDataRepository() throws Exception {
        String userUuid = UUID.randomUUID().toString();
        assertThat(formService.countAllFormData(), equalTo(0));
        FormData firstFormData = new FormData();
        firstFormData.setUuid(UUID.randomUUID().toString());
        firstFormData.setStatus("Some random status");
        firstFormData.setUserUuid(userUuid);
        formService.saveFormData(firstFormData);
        FormData secondFormData = new FormData();
        secondFormData.setUuid(UUID.randomUUID().toString());
        secondFormData.setStatus("Some other random status");
        secondFormData.setUserUuid(userUuid);
        formService.saveFormData(secondFormData);
        assertThat(formService.countAllFormData(), equalTo(2));
        List<FormData> formDataList = formService.getAllFormData(StringUtil.EMPTY);
        for (FormData formData : formDataList) {
            formService.deleteFormData(formData);
        }
        assertThat(formService.countAllFormData(), equalTo(0));
    }

    /**
     * @verifies delete list of form data from local data repository.
     * @see FormService#deleteFormData(java.util.List)
     */
    @Test
    public void deleteFormData_shouldDeleteListOfFormDataFromLocalDataRepository() throws Exception {
        String userUuid = UUID.randomUUID().toString();
        assertThat(formService.countAllFormData(), equalTo(0));
        FormData firstFormData = new FormData();
        firstFormData.setUuid(UUID.randomUUID().toString());
        firstFormData.setStatus("Some random status");
        firstFormData.setUserUuid(userUuid);
        formService.saveFormData(firstFormData);
        FormData secondFormData = new FormData();
        secondFormData.setUuid(UUID.randomUUID().toString());
        secondFormData.setStatus("Some other random status");
        secondFormData.setUserUuid(userUuid);
        formService.saveFormData(secondFormData);
        assertThat(formService.countAllFormData(), equalTo(2));
        List<FormData> formDataList = formService.getAllFormData(StringUtil.EMPTY);
        formService.deleteFormData(formDataList);
        assertThat(formService.countAllFormData(), equalTo(0));
    }
}
