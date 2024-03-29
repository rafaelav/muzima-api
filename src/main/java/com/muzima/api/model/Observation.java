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
package com.muzima.api.model;

import java.util.Date;

public class Observation extends OpenmrsSearchable {

    private Person person;

    private Encounter encounter;

    private Concept concept;

    private Concept valueCoded;

    private Date valueDatetime;

    private Double valueNumeric;

    private String valueText;

    private Date observationDatetime;

    public Person getPerson() {
        return person;
    }

    public void setPerson(final Person person) {
        this.person = person;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public void setEncounter(final Encounter encounter) {
        this.encounter = encounter;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(final Concept concept) {
        this.concept = concept;
    }

    public Concept getValueCoded() {
        return valueCoded;
    }

    public void setValueCoded(final Concept valueCoded) {
        this.valueCoded = valueCoded;
    }

    public Date getValueDatetime() {
        return valueDatetime;
    }

    public void setValueDatetime(final Date valueDatetime) {
        this.valueDatetime = valueDatetime;
    }

    public Double getValueNumeric() {
        return valueNumeric;
    }

    public void setValueNumeric(final Double valueNumeric) {
        this.valueNumeric = valueNumeric;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(final String valueText) {
        this.valueText = valueText;
    }

    /**
     * Get the date and time of the observation.
     *
     * @return the date and time of the observation.
     */
    public Date getObservationDatetime() {
        return observationDatetime;
    }

    /**
     * Set the date and time of the observation.
     *
     * @param observationDatetime the date and time of the observation.
     */
    public void setObservationDatetime(final Date observationDatetime) {
        this.observationDatetime = observationDatetime;
    }
}
