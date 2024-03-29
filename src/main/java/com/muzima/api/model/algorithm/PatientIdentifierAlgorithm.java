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
import com.muzima.api.model.PatientIdentifier;
import com.muzima.api.model.PatientIdentifierType;
import com.muzima.search.api.model.object.Searchable;
import com.muzima.util.JsonUtils;
import net.minidev.json.JSONObject;

import java.io.IOException;

public class PatientIdentifierAlgorithm extends BaseOpenmrsAlgorithm {

    public static final String PATIENT_IDENTIFIER_REPRESENTATION =
            "(uuid,identifier,preferred," +
                    "identifierType:" + PatientIdentifierTypeAlgorithm.PATIENT_IDENTIFIER_TYPE_REPRESENTATION  + ")";
    private PatientIdentifierTypeAlgorithm patientIdentifierTypeAlgorithm;

    public PatientIdentifierAlgorithm() {
        this.patientIdentifierTypeAlgorithm = new PatientIdentifierTypeAlgorithm();
    }

    /**
     * Implementation of this method will define how the observation will be serialized from the JSON representation.
     *
     * @param serialized the json representation
     * @return the concrete observation object
     */
    @Override
    public Searchable deserialize(final String serialized) throws IOException {
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setUuid(JsonUtils.readAsString(serialized, "$['uuid']"));
        patientIdentifier.setIdentifier(JsonUtils.readAsString(serialized, "$['identifier']"));
        patientIdentifier.setPreferred(JsonUtils.readAsBoolean(serialized, "$['preferred']"));
        Object identifierTypeObject = JsonUtils.readAsObject(serialized, "$['identifierType']");
        PatientIdentifierType identifierType =
                (PatientIdentifierType) patientIdentifierTypeAlgorithm.deserialize(String.valueOf(identifierTypeObject));
        patientIdentifier.setIdentifierType(identifierType);
        return patientIdentifier;
    }

    /**
     * Implementation of this method will define how the object will be de-serialized into the String representation.
     *
     * @param object the object
     * @return the string representation
     */
    @Override
    public String serialize(final Searchable object) throws IOException {
        PatientIdentifier patientIdentifier = (PatientIdentifier) object;
        JSONObject jsonObject = new JSONObject();
        JsonUtils.writeAsString(jsonObject, "uuid", patientIdentifier.getUuid());
        JsonUtils.writeAsString(jsonObject, "identifier", patientIdentifier.getIdentifier());
        JsonUtils.writeAsBoolean(jsonObject, "preferred", patientIdentifier.isPreferred());
        String identifierType = patientIdentifierTypeAlgorithm.serialize(patientIdentifier.getIdentifierType());
        jsonObject.put("identifierType", JsonPath.read(identifierType, "$"));
        return jsonObject.toJSONString();
    }
}
