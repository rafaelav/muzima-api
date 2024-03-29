/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package com.muzima.api.model;

import com.muzima.search.api.model.object.Searchable;

/**
 * Credential is a class to hold and persist authentication information for a user locally. User will be allowed to
 * access the system when a credential record for that user is found in the local repository.
 */
public class Credential implements Searchable {

    private String uuid;

    private String userUuid;

    private String username;

    private String salt;

    private String password;

    /**
     * Get the uuid of the credential.
     *
     * @return the uuid of the credential.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid of the credential.
     *
     * @param uuid the uuid of the credential.
     */
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the uuid of the user with this credential.
     *
     * @return the uuid of the user with this credential.
     */
    public String getUserUuid() {
        return userUuid;
    }

    /**
     * Set the uuid of the user with this credential.
     *
     * @param userUuid the uuid of the user with this credential.
     */
    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }

    /**
     * Get the username who own this credential.
     *
     * @return the username who own this credential.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username who own this credential.
     *
     * @param username the username who own this credential.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Get the salt for hashing the password.
     *
     * @return the salt for hashing the password.
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Set the salt for hashing the password.
     *
     * @param salt the salt for hashing the password.
     */
    public void setSalt(final String salt) {
        this.salt = salt;
    }

    /**
     * Get the hashed password.
     *
     * @return the hashed password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the hashed password.
     *
     * @param password the hashed password.
     */
    public void setPassword(final String password) {
        this.password = password;
    }
}
