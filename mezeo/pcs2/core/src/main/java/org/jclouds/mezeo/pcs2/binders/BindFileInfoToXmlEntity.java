/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.mezeo.pcs2.binders;

import java.util.Collections;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Key;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class BindFileInfoToXmlEntity implements Binder {

   public void bindToRequest(HttpRequest request, Object toBind) {
      Blob<?> blob = (Blob<?>) toBind;
      String bareKey = BlobStoreUtils.parseKey(new Key("trash", blob.getKey())).getKey();
      String file = String.format(
               "<file><name>%s</name><mime_type>%s</mime_type><public>false</public></file>",
               bareKey, blob.getMetadata().getContentType());
      request.setEntity(file);
      request.getHeaders().replaceValues(HttpHeaders.CONTENT_LENGTH,
               Collections.singletonList(file.getBytes().length + ""));
      request.getHeaders().replaceValues(HttpHeaders.CONTENT_TYPE,
               Collections.singletonList("application/vnd.csp.file-info+xml"));
   }
}
