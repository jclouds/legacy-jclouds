/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */

package org.jclouds.management;

import org.jclouds.Context;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.providers.ProviderMetadata;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import java.io.IOException;


public interface JcloudsManagementCoreMBean {

   /**
    * Lists all available {@link ApiMetadata}.
    * @return
    */
   TabularData getApis() throws OpenDataException;

   /**
    * Find {@link ApiMetadata} by id.
    * @return
    */
   CompositeData findApiById(String id) throws OpenDataException;


   /**
    * Lists all available {@link ProviderMetadata}
    * @return
    */
   TabularData getProviders() throws OpenDataException;


   /**
    * Find {@link ProviderMetadata} by id.
    * @return
    */
   CompositeData findProviderById(String id) throws OpenDataException;

   /**
    * Lists all {@link Context} objects.
    * @return
    */
   TabularData getContexts() throws OpenDataException;

   /**
    * Creates a {@link Context}.
    * @param id
    * @param name
    * @param identity
    * @param credential
    * @param endpoint
    * @param overrides
    * @return
    */
   public CompositeData createContext(String id, String name, String identity, String credential, String endpoint, String overrides) throws IOException, OpenDataException;
}
