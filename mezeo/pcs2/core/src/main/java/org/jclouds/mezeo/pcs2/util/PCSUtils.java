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
package org.jclouds.mezeo.pcs2.util;

import java.net.URI;

import org.jclouds.http.HttpUtils;
import org.jclouds.mezeo.pcs2.functions.Key;

/**
 * Utilities for PCS connections.
 * 
 * @author Adrian Cole
 */
public class PCSUtils {
   /**
    * converts the object id into something we can use as an etag
    */
   public static byte[] getETag(URI url) {
      String id = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
      return getETag(id);
   }

   public static byte[] getETag(String id) {
      id = id.replaceAll("-", "");
      // parse url to create an "etag"
      byte[] eTag = HttpUtils.fromHexString(id);
      return eTag;
   }

   public static Key parseKey(Key key) {

      if (key.getKey().indexOf('/') != -1) {
         String container = key.getContainer() + '/'
                  + key.getKey().substring(0, key.getKey().lastIndexOf('/'));
         String newKey = key.getKey().substring(key.getKey().lastIndexOf('/') + 1);
         key = new Key(container.replaceAll("//", "/"), newKey);
      }
      return key;
   }

   public static String getContainerId(URI url) {
      String path = url.getPath();
      int indexAfterContainersSlash = path.indexOf("containers/") + "containers/".length();
      return path.substring(indexAfterContainersSlash);
   }

   public static String getFileId(URI url) {
      String path = url.getPath();
      int indexAfterContainersSlash = path.indexOf("files/") + "files/".length();
      return path.substring(indexAfterContainersSlash);
   }
}
