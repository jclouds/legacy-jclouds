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
package org.jclouds.rackspace.cloudfiles;

import java.net.URI;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.concurrent.Timeout;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;
import org.jclouds.rackspace.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;

/**
 * Provides access to Cloud Files via their REST API.
 * <p/>
 * All commands return a Future of the result from Cloud Files. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090812.pdf" />
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface CloudFilesClient {

   CFObject newCFObject();

   /**
    * HEAD operations against an account are performed to retrieve the number of Containers and the
    * total bytes stored in Cloud Files for the account.
    * <p/>
    * Determine the number of Containers within the account and the total bytes stored. Since the
    * storage system is designed to store large amounts of data, care should be taken when
    * representing the total bytes response as an integer; when possible, convert it to a 64-bit
    * unsigned integer if your platform supports that primitive type.
    */
   AccountMetadata getAccountStatistics();

   /**
    * GET operations against the X-Storage-Url for an account are performed to retrieve a list of
    * existing storage
    * <p/>
    * Containers ordered by name. The following list describes the optional query parameters that
    * are supported with this request.
    * <ul>
    * <li>limit - For an integer value N, limits the number of results to at most N values.</li>
    * <li>marker - Given a string value X, return Object names greater in value than the specied
    * marker.</li>
    * <li>format - Specify either json or xml to return the respective serialized response.</li>
    * </ul>
    * <p/>
    * At this time, a prex query parameter is not supported at the Account level.
    * 
    *<h4>Large Container Lists</h4>
    * The system will return a maximum of 10,000 Container names per request. To retrieve subsequent
    * container names, another request must be made with a marker parameter. The marker indicates
    * where the last list left off and the system will return container names greater than this
    * marker, up to 10,000 again. Note that the marker value should be URL encoded prior to sending
    * the HTTP request.
    * <p/>
    * If 10,000 is larger than desired, a limit parameter may be given.
    * <p/>
    * If the number of container names returned equals the limit given (or 10,000 if no limit is
    * given), it can be assumed there are more container names to be listed. If the container name
    * list is exactly divisible by the limit, the last request will simply have no content.
    */
   SortedSet<ContainerMetadata> listContainers(ListContainerOptions... options);

   boolean setObjectInfo(String container, String name, Map<String, String> userMetadata);

   SortedSet<ContainerCDNMetadata> listCDNContainers(ListCdnContainerOptions... options);

   ContainerCDNMetadata getCDNMetadata(String container);

   URI enableCDN(String container, long ttl);

   URI enableCDN(String container);

   URI updateCDN(String container, long ttl);

   boolean disableCDN(String container);

   boolean createContainer(String container);

   boolean deleteContainerIfEmpty(String container);

   @Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
   ListContainerResponse<ObjectInfo> listObjects(String container, ListContainerOptions... options);

   boolean containerExists(String container);

   @Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
   String putObject(String container, CFObject object);

   @Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
   CFObject getObject(String container, String name, GetOptions... options);

   MutableObjectInfoWithMetadata getObjectInfo(String container, String name);

   void removeObject(String container, String name);

}
