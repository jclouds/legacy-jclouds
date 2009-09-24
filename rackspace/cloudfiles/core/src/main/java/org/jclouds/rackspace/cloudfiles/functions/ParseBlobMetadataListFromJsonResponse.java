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
package org.jclouds.rackspace.cloudfiles.functions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.ParseJson;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

/**
 * This parses {@link BlobMetadata} from a gson string.
 * 
 * @author Adrian Cole
 */
public class ParseBlobMetadataListFromJsonResponse extends ParseJson<List<BlobMetadata>> {

   @Inject
   public ParseBlobMetadataListFromJsonResponse(Gson gson) {
      super(gson);
   }

   public static class CloudFilesMetadata {
      public CloudFilesMetadata() {
      }

      String name;
      String hash;
      long bytes;
      String content_type;
      DateTime last_modified;
   }

   public List<BlobMetadata> apply(InputStream stream) {
      Type listType = new TypeToken<List<CloudFilesMetadata>>() {
      }.getType();

      try {
         List<CloudFilesMetadata> list = gson.fromJson(new InputStreamReader(stream, "UTF-8"),
                  listType);
         return Lists.transform(list, new Function<CloudFilesMetadata, BlobMetadata>() {
            public BlobMetadata apply(CloudFilesMetadata from) {
               BlobMetadata metadata = new BlobMetadata(from.name);
               metadata.setSize(from.bytes);
               metadata.setLastModified(from.last_modified);
               metadata.setContentType(from.content_type);
               metadata.setETag(HttpUtils.fromHexString(from.hash));
               return metadata;
            }
         });

      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}