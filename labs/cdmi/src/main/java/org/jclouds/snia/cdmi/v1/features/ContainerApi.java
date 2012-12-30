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
package org.jclouds.snia.cdmi.v1.features;

import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.options.CreateContainerOptions;
import org.jclouds.snia.cdmi.v1.queryparams.ContainerQueryParams;

/**
 * CDMI Container Object Resource Operations
 * 
 * @see ContainerAsyncApi
 * @author Kenneth Nagin
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 */
public interface ContainerApi {

   /**
    * get CDMI Container
    * 
    * @param containerName
    *           containerName must end with a forward slash, /.
    * @return Container
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  container = get("myContainer/");
    *  container = get("parentContainer/childContainer/");
    * }
    * 
    *         <pre>
    */
   Container get(String containerName);

   /**
    * get CDMI Container
    * 
    * @param containerName
    * @param queryParams
    *           enables getting only certain fields, metadata, children range
    * @return Container
    * 
    *         <pre>
    * Examples: 
    * {@code
    * container = get("myContainer/",ContainerQueryParams.Builder.mimetype("text/plain").field("objectName"))
    * container = get("myContainer/",ContainerQueryParams.Builder.metadata().field("objectName"))
    * }
    * </pre>
    * @see ContainerQueryParams
    */
   Container get(String containerName, ContainerQueryParams queryParams);

   /**
    * Create CDMI Container
    * 
    * @param containerName
    *           containerName must end with a forward slash, /.
    * @return Container
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  container = create("myContainer/");
    *  container = create("parentContainer/childContainer/");
    *  }
    * </pre>
    */
   Container create(String containerName);

   /**
    * Create CDMI Container
    * 
    * @param containerName
    * @param options
    *           enables adding metadata
    * @return Container
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  container = create("myContainer/",CreateContainerOptions.Builder..metadata(metaDataIn));
    *  }
    * </pre>
    * @see CreateContainerOptions
    */
   Container create(String containerName, CreateContainerOptions... options);

   /**
    * Delete CDMI Container
    * 
    * @param containerName
    */
   void delete(String containerName);

}
