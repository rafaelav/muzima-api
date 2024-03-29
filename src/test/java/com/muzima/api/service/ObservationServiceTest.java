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
import com.muzima.api.model.Cohort;
import com.muzima.api.model.Concept;
import com.muzima.api.model.Observation;
import com.muzima.api.model.Patient;
import com.muzima.api.model.Person;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
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
public class ObservationServiceTest {
    private static final String GIVEN_NAME = "Test";
    private static final String CONCEPT_NAME = "ANTIRETROVIRAL PLAN";
    // baseline observation
    private Observation observation;
    private List<Patient> patients;
    private List<Concept> concepts;
    private List<Observation> observations;

    private Context context;
    private PatientService patientService;
    private ConceptService conceptService;
    private EncounterService encounterService;
    private ObservationService observationService;

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
        patientService = context.getPatientService();
        conceptService = context.getService(ConceptService.class);
        encounterService = context.getService(EncounterService.class);
        observationService = context.getObservationService();
        patients = patientService.downloadPatientsByName(GIVEN_NAME);
        concepts = conceptService.downloadConceptsByName(CONCEPT_NAME);
        observations = new ArrayList<Observation>();
        for (Patient patient : patients) {
            for (Concept concept : concepts) {
                List<Observation> downloadedObservations =
                        observationService.downloadObservationsByPatientAndConcept(patient, concept);
                observations.addAll(downloadedObservations);
            }
        }
        observation = observations.get(nextInt(observations.size()));
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
     * @verifies download all observation with matching patient and concept.
     * @see ObservationService#downloadObservationsByPatientAndConcept(com.muzima.api.model.Patient, com.muzima.api.model.Concept)
     */
    @Test
    public void downloadObservationsByPatientAndConcept_shouldDownloadAllObservationWithMatchingPatientAndConcept() throws Exception {
        Concept concept = concepts.get(nextInt(concepts.size()));
        Patient patient = patients.get(nextInt(patients.size()));
        List<Observation> downloadedObservations =
                observationService.downloadObservationsByPatientAndConcept(patient, concept);
        for (Observation downloadedObservation : downloadedObservations) {
            assertThat(downloadedObservation.getConcept(), equalTo(concept));
            assertThat(downloadedObservation.getPerson(), equalTo((Person) patient));
        }
    }

    /**
     * @verifies save observation to local data repository
     * @see ObservationService#saveObservation(com.muzima.api.model.Observation)
     */
    @Test
    public void saveObservation_shouldSaveObservationToLocalDataRepository() throws Exception {
        Person person = observation.getPerson();
        assertThat(observationService.getObservationsByPatient(person.getUuid()), hasSize(0));
        observationService.saveObservation(observation);
        assertThat(observationService.getObservationsByPatient(person.getUuid()), hasSize(1));
        List<Observation> savedObservations = observationService.getObservationsByPatient(person.getUuid());
        for (Observation savedObservation : savedObservations) {
            assertThat(savedObservation.getPerson(), equalTo(person));
        }
    }

    /**
     * @verifies save observations to local data repository
     * @see ObservationService#saveObservations(java.util.List)
     */
    @Test
    public void saveObservations_shouldSaveObservationsToLocalDataRepository() throws Exception {
        observationService.saveObservations(observations);
        List<Observation> savedObservations = new ArrayList<Observation>();
        for (Patient patient : patients) {
            savedObservations.addAll(observationService.getObservationsByPatient(patient.getUuid()));
        }
        assertThat(savedObservations.size(), equalTo(observations.size()));
    }

    /**
     * @verifies replace existing observation in the local data repository.
     * @see ObservationService#updateObservation(com.muzima.api.model.Observation)
     */
    @Test
    public void updateObservation_shouldReplaceExistingObservationInTheLocalDataRepository() throws Exception {
        Person person = observation.getPerson();
        assertThat(observationService.getObservationsByPatient(person.getUuid()), hasSize(0));
        observationService.saveObservation(observation);
        Observation savedObservation = observationService.getObservationByUuid(observation.getUuid());
        Date obsDatetime = new Date();
        savedObservation.setObservationDatetime(obsDatetime);
        observationService.updateObservation(savedObservation);
        Observation updatedObservation = observationService.getObservationByUuid(observation.getUuid());
        assertThat(updatedObservation.getObservationDatetime(), equalTo(obsDatetime));
    }

    /**
     * @verifies replace existing observations in the local data repository.
     * @see ObservationService#updateObservations(java.util.List)
     */
    @Test
    public void updateObservations_shouldReplaceExistingObservationsInTheLocalDataRepository() throws Exception {
        Person person = observation.getPerson();
        assertThat(observationService.getObservationsByPatient(person.getUuid()), hasSize(0));
        observationService.saveObservations(observations);
        Date obsDatetime = new Date();
        List<Observation> savedObservations = observationService.getObservationsByPatient(person.getUuid());
        for (Observation savedObservation : savedObservations) {
            assertThat(savedObservation.getPerson(), equalTo(person));
            savedObservation.setObservationDatetime(obsDatetime);
        }
        observationService.updateObservations(savedObservations);
        List<Observation> updatedObservations = observationService.getObservationsByPatient(person.getUuid());
        for (Observation updatedObservation : updatedObservations) {
            assertThat(updatedObservation.getPerson(), equalTo(person));
            assertThat(updatedObservation.getObservationDatetime(), equalTo(obsDatetime));
        }
    }

    /**
     * @verifies return observation with matching uuid.
     * @see ObservationService#getObservationByUuid(String)
     */
    @Test
    public void getObservationByUuid_shouldReturnObservationWithMatchingUuid() throws Exception {
        assertThat(observationService.getObservationByUuid(observation.getUuid()), nullValue());
        observationService.saveObservation(observation);
        assertThat(observationService.getObservationByUuid(observation.getUuid()), not(nullValue()));
        assertThat(observationService.getObservationByUuid(observation.getUuid()), samePropertyValuesAs(observation));
    }

    /**
     * @verifies return null when no observation match the uuid.
     * @see ObservationService#getObservationByUuid(String)
     */
    @Test
    public void getObservationByUuid_shouldReturnNullWhenNoObservationMatchTheUuid() throws Exception {
        assertThat(observationService.getObservationByUuid(observation.getUuid()), nullValue());
        observationService.saveObservation(observation);
        assertThat(observationService.getObservationByUuid(observation.getUuid()), not(nullValue()));
        String randomUuid = UUID.randomUUID().toString();
        assertThat(observationService.getObservationByUuid(randomUuid), nullValue());
    }

    /**
     * @verifies return list of all observations for the patient.
     * @see ObservationService#getObservationsByPatient(String)
     */
    @Test
    public void getObservationsByPatient_shouldReturnListOfAllObservationsForThePatient() throws Exception {
        Person person = observation.getPerson();
        assertThat(observationService.getObservationsByPatient(person.getUuid()), hasSize(0));
        observationService.saveObservations(observations);
        List<Observation> savedObservations = observationService.getObservationsByPatient(person.getUuid());
        for (Observation savedObservation : savedObservations) {
            assertThat(savedObservation.getPerson(), equalTo(person));
        }
    }

    /**
     * @verifies return empty list when no observation found for the patient.
     * @see ObservationService#getObservationsByPatient(String)
     */
    @Test
    public void getObservationsByPatient_shouldReturnEmptyListWhenNoObservationFoundForThePatient() throws Exception {
        Person person = observation.getPerson();
        assertThat(observationService.getObservationsByPatient(person.getUuid()), hasSize(0));
        observationService.saveObservations(observations);
        List<Observation> savedObservations = observationService.getObservationsByPatient(person.getUuid());
        for (Observation savedObservation : savedObservations) {
            assertThat(savedObservation.getPerson(), equalTo(person));
        }
        String randomUuid = UUID.randomUUID().toString();
        assertThat(observationService.getObservationsByPatient(randomUuid), empty());
    }

    /**
     * @verifies return list of all observations for the patient.
     * @see ObservationService#getObservationsByPatientAndConcept(String, String)
     */
    @Test
    public void getObservationsByPatientAndConcept_shouldReturnListOfAllObservationsForThePatient() throws Exception {
        Person person = observation.getPerson();
        Concept concept = observation.getConcept();
        observationService.saveObservations(observations);
        List<Observation> savedObservations =
                observationService.getObservationsByPatientAndConcept(person.getUuid(), concept.getUuid());
        for (Observation savedObservation : savedObservations) {
            assertThat(savedObservation.getPerson(), equalTo(person));
            assertThat(savedObservation.getConcept(), equalTo(concept));
        }
    }

    /**
     * @verifies return empty list when no observation found for the patient.
     * @see ObservationService#getObservationsByPatientAndConcept(String, String)
     */
    @Test
    public void getObservationsByPatientAndConcept_shouldReturnEmptyListWhenNoObservationFoundForThePatient() throws Exception {
        String randomPersonUuid = UUID.randomUUID().toString();
        String randomConceptUuid = UUID.randomUUID().toString();
        observationService.saveObservations(observations);
        List<Observation> savedObservations =
                observationService.getObservationsByPatientAndConcept(randomPersonUuid, randomConceptUuid);
        assertThat(savedObservations, empty());
    }

    /**
     * @verifies return list of all observations with matching search term on the searchable fields.
     * @see ObservationService#searchObservations(String, String)
     */
    @Test
    public void searchObservations_shouldReturnListOfAllObservationsWithMatchingSearchTermOnTheSearchableFields() throws Exception {
        conceptService.saveConcepts(concepts);
        observationService.saveObservations(observations);
        List<Observation> savedObservations = new ArrayList<Observation>();
        for (Patient patient : patients) {
            savedObservations.addAll(observationService.searchObservations(patient, CONCEPT_NAME));
        }
        assertThat(savedObservations, hasSize(observations.size()));
        for (Observation savedObservation : savedObservations) {
            assertThat(savedObservation, isIn(observations));
        }
    }

    /**
     * @verifies return empty list when no observation match the search term.
     * @see ObservationService#searchObservations(String, String)
     */
    @Test
    public void searchObservations_shouldReturnEmptyListWhenNoObservationMatchTheSearchTerm() throws Exception {
        String randomConceptName = UUID.randomUUID().toString();
        observationService.saveObservations(observations);
        List<Observation> savedObservations = new ArrayList<Observation>();
        for (Patient patient : patients) {
            savedObservations.addAll(observationService.searchObservations(patient, randomConceptName));
        }
        assertThat(savedObservations, hasSize(0));
    }

    /**
     * @verifies delete the observation from the local repository.
     * @see ObservationService#deleteObservation(com.muzima.api.model.Observation)
     */
    @Test
    public void deleteObservation_shouldDeleteTheObservationFromTheLocalRepository() throws Exception {
        observationService.saveObservations(observations);
        for (Observation savedObservation : observations) {
            assertThat(observationService.getObservationByUuid(savedObservation.getUuid()), not(nullValue()));
            observationService.deleteObservation(savedObservation);
            assertThat(observationService.getObservationByUuid(savedObservation.getUuid()), nullValue());
        }
    }

    /**
     * @verifies delete observations from the local repository.
     * @see ObservationService#deleteObservations(java.util.List)
     */
    @Test
    public void deleteObservations_shouldDeleteObservationsFromTheLocalRepository() throws Exception {
        observationService.saveObservations(observations);
        for (Observation savedObservation : observations) {
            assertThat(observationService.getObservationByUuid(savedObservation.getUuid()), not(nullValue()));
        }
        observationService.deleteObservations(observations);
        for (Observation savedObservation : observations) {
            assertThat(observationService.getObservationByUuid(savedObservation.getUuid()), nullValue());
        }
    }
}
