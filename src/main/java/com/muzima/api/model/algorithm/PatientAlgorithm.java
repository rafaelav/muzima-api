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
package com.muzima.api.model.algorithm;

import com.jayway.jsonpath.JsonPath;
import com.muzima.api.model.Patient;
import com.muzima.api.model.PatientIdentifier;
import com.muzima.api.model.PersonName;
import com.muzima.search.api.model.object.Searchable;
import com.muzima.search.api.util.ISO8601Util;
import com.muzima.util.JsonUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class PatientAlgorithm extends BaseOpenmrsAlgorithm {

    public static final String PATIENT_SIMPLE_REPRESENTATION = "(uuid)";
    public static final String PATIENT_STANDARD_REPRESENTATION =
            "(uuid,gender,birthdate," +
                    "names:" + PersonNameAlgorithm.PERSON_NAME_REPRESENTATION + "," +
                    "identifiers:" + PatientIdentifierAlgorithm.PATIENT_IDENTIFIER_REPRESENTATION + ")";
    private PersonNameAlgorithm personNameAlgorithm;
    private PatientIdentifierAlgorithm patientIdentifierAlgorithm;

    public PatientAlgorithm() {
        this.personNameAlgorithm = new PersonNameAlgorithm();
        this.patientIdentifierAlgorithm = new PatientIdentifierAlgorithm();
    }

    /**
     * Implementation of this method will define how the observation will be serialized from the JSON representation.
     *
     * @param serialized the json representation
     * @return the concrete observation object
     */
    @Override
    public Searchable deserialize(final String serialized) throws IOException {
        Patient patient = new Patient();
        patient.setUuid(JsonUtils.readAsString(serialized, "$['uuid']"));
        patient.setGender(JsonUtils.readAsString(serialized, "$['gender']"));
        patient.setBirthdate(JsonUtils.readAsDate(serialized, "$['birthdate']"));
        List<Object> personNameObjects = JsonUtils.readAsObjectList(serialized, "$['names']");
        for (Object personNameObject : personNameObjects) {
            patient.addName((PersonName) personNameAlgorithm.deserialize(String.valueOf(personNameObject)));
        }
        List<Object> identifierObjects = JsonUtils.readAsObjectList(serialized, "$['identifiers']");
        for (Object identifierObject : identifierObjects) {
            patient.addIdentifier(
                    (PatientIdentifier) patientIdentifierAlgorithm.deserialize(String.valueOf(identifierObject)));
        }
        return patient;
    }

    /**
     * Implementation of this method will define how the object will be de-serialized into the String representation.
     *
     * @param object the object
     * @return the string representation
     */
    @Override
    public String serialize(final Searchable object) throws IOException {
        Patient patient = (Patient) object;
        JSONObject jsonObject = new JSONObject();
        JsonUtils.writeAsString(jsonObject, "uuid", patient.getUuid());
        JsonUtils.writeAsString(jsonObject, "gender", patient.getGender());
        JsonUtils.writeAsDate(jsonObject, "birthdate", patient.getBirthdate());
        JSONArray nameArray = new JSONArray();
        for (PersonName personName : patient.getNames()) {
            String name = personNameAlgorithm.serialize(personName);
            nameArray.add(JsonPath.read(name, "$"));
        }
        jsonObject.put("names", nameArray);
        JSONArray identifierArray = new JSONArray();
        for (PatientIdentifier identifier : patient.getIdentifiers()) {
            String name = patientIdentifierAlgorithm.serialize(identifier);
            identifierArray.add(JsonPath.read(name, "$"));
        }
        jsonObject.put("identifiers", identifierArray);
        return jsonObject.toJSONString();
    }
}
