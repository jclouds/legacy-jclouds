/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
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
 * ====================================================================
 */
/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.chef;

import java.io.File;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;

/**
 * Provides synchronous access to Chef.
 * <p/>
 * 
 * @see ChefAsyncClient
 * @see <a href="TODO: insert URL of Chef documentation" />
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface ChefClient {
   String listCookbooks();

   String createCookbook(String name, File content);

   String createCookbook(String name, byte[] content);

   /**
    * creates a new client
    * 
    * @return the private key of the client. You can then use this client name and private key to
    *         access the Opscode API.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to create a client.
    * @throws HttpResponseException
    *            "409 Conflict" if the client already exists
    */
   @Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
   String createClient(String name);

   @Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
   String generateKeyForClient(String name);

   Set<String> listClients();

   boolean clientExists(String name);

   void deleteClient(String name);

}
