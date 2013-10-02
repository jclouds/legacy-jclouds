/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.http.options.GetOptions;
import org.jclouds.openstack.swift.domain.AccountMetadata;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.openstack.swift.options.CreateContainerOptions;
import org.jclouds.openstack.swift.options.ListContainerOptions;

import com.google.inject.Provides;

/**
 * Common features between OpenStack Swift and CloudFiles
 * 
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090812.pdf" />
 * @author Adrian Cole
 */
public interface CommonSwiftClient extends Closeable {
   @Provides
   SwiftObject newSwiftObject();

   /**
    * HEAD operations against an identity are performed to retrieve the number of Containers and the
    * total bytes stored in Cloud Files for the identity.
    * <p/>
    * Determine the number of Containers within the identity and the total bytes stored. Since the
    * storage system is designed to store large amounts of data, care should be taken when
    * representing the total bytes response as an integer; when possible, convert it to a 64-bit
    * unsigned integer if your platform supports that primitive flavor.
    */
   AccountMetadata getAccountStatistics();

   /**
    * GET operations against the X-Storage-Url for an identity are performed to retrieve a list of
    * existing storage
    * <p/>
    * Containers ordered by name. The following list describes the optional query parameters that
    * are supported with this request.
    * <ul>
    * <li>limit - For an integer value N, limits the number of results to at most N values.</li>
    * <li>marker - Given a string value X, return Object names greater in value than the specified
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
   Set<ContainerMetadata> listContainers(ListContainerOptions... options);

   ContainerMetadata getContainerMetadata(String container);
   
   boolean setContainerMetadata(String container, Map<String, String> containerMetadata);
   
   boolean deleteContainerMetadata(String container, Iterable<String> metadataKeys);

   boolean createContainer(String container);

   boolean createContainer(String container, CreateContainerOptions... options);
   
   boolean deleteContainerIfEmpty(String container);

   boolean containerExists(String container);

   PageSet<ObjectInfo> listObjects(String container, ListContainerOptions... options);

   SwiftObject getObject(String container, String name, GetOptions... options);

   boolean setObjectInfo(String container, String name, Map<String, String> userMetadata);

   MutableObjectInfoWithMetadata getObjectInfo(String container, String name);

   String putObject(String container, SwiftObject object);

   /**
    * @return True If the object was copied
    * @throws CopyObjectException If the object was not copied
    */
   boolean copyObject(String sourceContainer, String sourceObject, String destinationContainer, String destinationObject);
   
   void removeObject(String container, String name);

   /**
    * @throws ContainerNotFoundException
    *            if the container is not present.
    */
   boolean objectExists(String container, String name);

   String putObjectManifest(String container, String name);
}
