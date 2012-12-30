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
package org.jclouds.snia.cdmi.v1;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.snia.cdmi.v1.features.ContainerApi;
import org.jclouds.snia.cdmi.v1.features.DataApi;
import org.jclouds.snia.cdmi.v1.features.DataNonCDMIContentTypeApi;
import org.jclouds.snia.cdmi.v1.features.DomainApi;

/**
 * Provides synchronous access to CDMI.
 * <p/>
 * 
 * @see CDMIAsyncApi
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 * @author Adrian Cole
 */
public interface CDMIApi {

   /**
    * Provides synchronous access to Domain Object Resource Operations.
    */
   @Delegate
   DomainApi getDomainApi();

   /**
    * Provides synchronous access to Container Object Resource Operations.
    */
   @Delegate
   ContainerApi getApi();

   /**
    * Provides synchronous access to Data Object Resource Operations.
    */
   @Delegate
   @Path("/{containerName}")
   DataApi getDataApiForContainer(@PathParam("containerName") String containerName);

   /**
    * Provides synchronous access to Data Object Resource Operations.
    */
   @Delegate
   @Path("/{containerName}")
   DataNonCDMIContentTypeApi getDataNonCDMIContentTypeApiForContainer(@PathParam("containerName") String containerName);

}
