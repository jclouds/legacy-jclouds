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
package org.jclouds.rackspace.cloudfiles.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.BoundedSortedSet;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.internal.BoundedTreeSet;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This parses {@link BlobMetadata} from a gson string.
 * 
 * @author Adrian Cole
 */
public class ParseBlobMetadataListFromJsonResponse extends
         ParseJson<BoundedSortedSet<BlobMetadata>> implements InvocationContext {

   private final Provider<MutableBlobMetadata> metadataFactory;
   private GeneratedHttpRequest<?> request;

   @Inject
   public ParseBlobMetadataListFromJsonResponse(Provider<MutableBlobMetadata> metadataFactory,
            Gson gson) {
      super(gson);
      this.metadataFactory = metadataFactory;
   }

   public static class CloudFilesMetadata implements Comparable<CloudFilesMetadata> {
      public CloudFilesMetadata() {
      }

      String name;
      String hash;
      long bytes;
      String content_type;
      DateTime last_modified;

      public int compareTo(CloudFilesMetadata o) {
         return (this == o) ? 0 : name.compareTo(o.name);
      }
   }

   public BoundedSortedSet<BlobMetadata> apply(InputStream stream) {
      checkState(request != null, "request should be initialized at this point");
      checkState(request.getArgs() != null, "request.getArgs() should be initialized at this point");
      checkArgument(request.getArgs()[0] instanceof String, "arg[0] must be a container name");
      checkArgument(request.getArgs()[1] instanceof ListContainerOptions[],
               "arg[1] must be an array of ListContainerOptions");
      ListContainerOptions[] optionsList = (ListContainerOptions[]) request.getArgs()[1];
      ListContainerOptions options = optionsList.length > 0 ? optionsList[0]
               : ListContainerOptions.NONE;
      Type listType = new TypeToken<SortedSet<CloudFilesMetadata>>() {
      }.getType();

      try {
         SortedSet<CloudFilesMetadata> list = gson.fromJson(new InputStreamReader(stream, "UTF-8"),
                  listType);
         SortedSet<BlobMetadata> returnVal = Sets.newTreeSet(Iterables.transform(list,
                  new Function<CloudFilesMetadata, BlobMetadata>() {
                     public BlobMetadata apply(CloudFilesMetadata from) {
                        MutableBlobMetadata metadata = metadataFactory.get();
                        metadata.setName(from.name);
                        metadata.setSize(from.bytes);
                        metadata.setLastModified(from.last_modified);
                        metadata.setContentType(from.content_type);
                        metadata.setETag(from.hash);
                        metadata.setContentMD5(HttpUtils.fromHexString(from.hash));
                        return metadata;
                     }
                  }));
         boolean truncated = options.getMaxResults() == returnVal.size();
         String marker = truncated ? returnVal.last().getName() : null;
         return new BoundedTreeSet<BlobMetadata>(returnVal, options.getPath(), marker, options
                  .getMaxResults(), truncated);

      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      this.request = request;
   }
}