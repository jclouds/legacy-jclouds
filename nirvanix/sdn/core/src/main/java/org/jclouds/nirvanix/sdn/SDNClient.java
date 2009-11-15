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
package org.jclouds.nirvanix.sdn;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.concurrent.Timeout;
import org.jclouds.nirvanix.sdn.domain.UploadInfo;

/**
 * Provides access to Nirvanix SDN resources via their REST API.
 * <p/>
 * 
 * @see <a href="http://developer.nirvanix.com/sitefiles/1000/API.html" />
 * @see SDNAsyncClient
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface SDNClient {

   public Blob newBlob();

   /**
    * The GetStorageNode method is used to determine which storage node a file should be uploaded
    * to. It returns the host to upload to and an Upload Token that will be used to authenticate.
    */
   UploadInfo getStorageNode(String folderPath, long size);

   void upload(URI endpoint, String uploadToken, String folderPath, Blob blob);

   /**
    * The SetMetadata method is used to set specified metadata for a file or folder.
    */
   void setMetadata(String path, Map<String, String> metadata);

   /**
    * The GetMetadata method is used to retrieve all metadata from a file or folder.
    */
   Map<String, String> getMetadata(String path);

   /**
    * Get the contents of a file
    */
   String getFile(String path);

}
