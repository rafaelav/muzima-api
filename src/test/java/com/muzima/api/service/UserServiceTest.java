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
import com.muzima.api.model.Credential;
import com.muzima.search.api.util.DigestUtil;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

/**
 * TODO: Write brief description about the class here.
 */
public class UserServiceTest {

    @Test
    public void savingCredential() throws Exception {

        Context context = ContextFactory.createContext();
        UserService userService = context.getUserService();

        List<Credential> credentialList = userService.getAllCredentials();
        for (Credential credential : credentialList) {
            userService.deleteCredential(credential);
        }

        String username = "admin";
        String password = "test";

        Credential credential = userService.getCredentialByUsername(username);
        if (credential != null)
            userService.deleteCredential(credential);

        credential = userService.getCredentialByUsername(username);
        Assert.assertNull(credential);

        String uuid = UUID.randomUUID().toString();
        String salt = DigestUtil.getSHA1Checksum(uuid);
        String hashedPassword = DigestUtil.getSHA1Checksum(salt + ":" + password);

        credential = new Credential();
        credential.setUuid(uuid);
        credential.setSalt(salt);
        credential.setPassword(hashedPassword);
        credential.setUsername(username);
        credential.setUserUuid(UUID.randomUUID().toString());
        userService.saveCredential(credential);

        Credential savedCredential = userService.getCredentialByUsername("admin");
        Assert.assertNotNull(savedCredential);
        Assert.assertEquals(salt, credential.getSalt());
        Assert.assertEquals(username, credential.getUsername());
        Assert.assertEquals(hashedPassword, credential.getPassword());
    }
}
