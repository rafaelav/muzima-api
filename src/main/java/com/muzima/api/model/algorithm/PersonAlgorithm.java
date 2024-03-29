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
import com.muzima.api.model.Person;
import com.muzima.api.model.PersonName;
import com.muzima.search.api.model.object.Searchable;
import com.muzima.search.api.util.ISO8601Util;
import com.muzima.util.JsonUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class PersonAlgorithm extends BaseOpenmrsAlgorithm {

    public static final String PERSON_SIMPLE_REPRESENTATION = "(uuid,uuid)";
    public static final String PERSON_STANDARD_REPRESENTATION =
            "(uuid,gender,birthdate,names:" + PersonNameAlgorithm.PERSON_NAME_REPRESENTATION + ",uuid)";
    private PersonNameAlgorithm personNameAlgorithm;

    public PersonAlgorithm() {
        personNameAlgorithm = new PersonNameAlgorithm();
    }

    /**
     * Implementation of this method will define how the observation will be serialized from the JSON representation.
     *
     * @param serialized the json representation
     * @return the concrete observation object
     */
    @Override
    public Searchable deserialize(final String serialized) throws IOException {
        Person person = new Person();
        person.setUuid(JsonUtils.readAsString(serialized, "$['uuid']"));
        person.setGender(JsonUtils.readAsString(serialized, "$['gender']"));
        person.setBirthdate(JsonUtils.readAsDate(serialized, "$['birthdate']"));
        List<Object> personNameObjects = JsonUtils.readAsObjectList(serialized, "$['names']");
        for (Object personNameObject : personNameObjects) {
            person.addName((PersonName) personNameAlgorithm.deserialize(String.valueOf(personNameObject)));
        }
        return person;
    }

    /**
     * Implementation of this method will define how the object will be de-serialized into the String representation.
     *
     * @param object the object
     * @return the string representation
     */
    @Override
    public String serialize(final Searchable object) throws IOException {
        Person person = (Person) object;
        JSONObject jsonObject = new JSONObject();
        JsonUtils.writeAsString(jsonObject, "uuid", person.getUuid());
        JsonUtils.writeAsString(jsonObject, "gender", person.getGender());
        JsonUtils.writeAsDate(jsonObject, "birthdate", person.getBirthdate());
        JSONArray nameArray = new JSONArray();
        for (PersonName personName : person.getNames()) {
            String name = personNameAlgorithm.serialize(personName);
            nameArray.add(JsonPath.read(name, "$"));
        }
        jsonObject.put("names", nameArray);
        return jsonObject.toJSONString();
    }
}
