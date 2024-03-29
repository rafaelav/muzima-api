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

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
public class Role extends OpenmrsSearchable {

    private String name;

    private List<Privilege> privileges;

    /**
     * Get the name of the role.
     *
     * @return the name of the role.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the role.
     *
     * @param name the name of the role.
     */
    public void setName(String name) {
        this.name = name;
    }

    public void add(final Privilege privilege) {
        getPrivileges().add(privilege);
    }

    /**
     * Get all privileges under this role.
     *
     * @return all privileges under this role.
     */
    public List<Privilege> getPrivileges() {
        if (privileges == null) {
            privileges = new ArrayList<Privilege>();
        }
        return privileges;
    }

    /**
     * Set all privileges under this role.
     *
     * @param privileges all privileges under this role.
     */
    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }
}
