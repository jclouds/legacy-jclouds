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
package org.jclouds.mezeo.pcs;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.mezeo.pcs.domain.ContainerList;
import org.jclouds.mezeo.pcs.domain.FileInfoWithMetadata;
import org.jclouds.mezeo.pcs.domain.PCSFile;
import org.jclouds.mezeo.pcs.options.PutBlockOptions;

/**
 * Provides access to Mezeo PCS via their REST API.
 * <p/>
 * 
 * @see <a href=
 *      "http://developer.mezeo.com/mezeo-developer-center/documentation/howto-using-curl-to-access-api"
 *      />
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface PCSClient {
   PCSFile newFile();

   ContainerList list();

   ContainerList list(URI container);

   URI createContainer(String container);

   URI createContainer(URI parent, String container);

   void deleteContainer(URI container);

   URI uploadFile(URI container, PCSFile object);

   URI createFile(URI container, PCSFile object);

   void uploadBlock(URI file, PCSFile object, PutBlockOptions... options);

   void deleteFile(URI file);

   InputStream downloadFile(URI file);

   FileInfoWithMetadata getFileInfo(URI file);

   void putMetadataItem(URI resource, String key, String value);

   void addMetadataItemToMap(URI resource, String key, Map<String, String> map);
}
