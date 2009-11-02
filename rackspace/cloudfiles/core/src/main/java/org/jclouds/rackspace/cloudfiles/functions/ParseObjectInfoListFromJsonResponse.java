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

import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.internal.ListContainerResponseImpl;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;
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
 * This parses {@link ObjectInfo} from a gson string.
 * 
 * @author Adrian Cole
 */
public class ParseObjectInfoListFromJsonResponse extends ParseJson<ListContainerResponse<ObjectInfo>>
         implements InvocationContext {

   private GeneratedHttpRequest<?> request;

   @Inject
   public ParseObjectInfoListFromJsonResponse(Gson gson) {
      super(gson);
   }

   public static class ObjectInfoImpl implements ObjectInfo {
      String name;
      String hash;
      long bytes;
      String content_type;
      DateTime last_modified;

      public int compareTo(ObjectInfoImpl o) {
         return (this == o) ? 0 : name.compareTo(o.name);
      }

      public Long getBytes() {
         return bytes;
      }

      public String getContentType() {
         return content_type;
      }

      public byte[] getHash() {
         return HttpUtils.fromHexString(hash);
      }

      public DateTime getLastModified() {
         return last_modified;
      }

      public String getName() {
         return name;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (int) (bytes ^ (bytes >>> 32));
         result = prime * result + ((content_type == null) ? 0 : content_type.hashCode());
         result = prime * result + ((hash == null) ? 0 : hash.hashCode());
         result = prime * result + ((last_modified == null) ? 0 : last_modified.hashCode());
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         ObjectInfoImpl other = (ObjectInfoImpl) obj;
         if (bytes != other.bytes)
            return false;
         if (content_type == null) {
            if (other.content_type != null)
               return false;
         } else if (!content_type.equals(other.content_type))
            return false;
         if (hash == null) {
            if (other.hash != null)
               return false;
         } else if (!hash.equals(other.hash))
            return false;
         if (last_modified == null) {
            if (other.last_modified != null)
               return false;
         } else if (!last_modified.equals(other.last_modified))
            return false;
         if (name == null) {
            if (other.name != null)
               return false;
         } else if (!name.equals(other.name))
            return false;
         return true;
      }

      public int compareTo(ObjectInfo o) {
         return (this == o) ? 0 : getName().compareTo(o.getName());
      }
   }

   public ListContainerResponse<ObjectInfo> apply(InputStream stream) {
      checkState(request != null, "request should be initialized at this point");
      checkState(request.getArgs() != null, "request.getArgs() should be initialized at this point");
      checkArgument(request.getArgs()[0] instanceof String, "arg[0] must be a container name");
      checkArgument(request.getArgs()[1] instanceof ListContainerOptions[],
               "arg[1] must be an array of ListContainerOptions");
      ListContainerOptions[] optionsList = (ListContainerOptions[]) request.getArgs()[1];
      ListContainerOptions options = optionsList.length > 0 ? optionsList[0]
               : ListContainerOptions.NONE;
      Type listType = new TypeToken<SortedSet<ObjectInfoImpl>>() {
      }.getType();

      try {
         SortedSet<ObjectInfoImpl> list = gson.fromJson(new InputStreamReader(stream, "UTF-8"),
                  listType);
         SortedSet<ObjectInfo> returnVal = Sets.newTreeSet(Iterables.transform(list,
                  new Function<ObjectInfoImpl, ObjectInfo>() {
                     public ObjectInfo apply(ObjectInfoImpl from) {
                        return from;
                     }
                  }));
         boolean truncated = options.getMaxResults() == returnVal.size();
         String marker = truncated ? returnVal.last().getName() : null;
         return new ListContainerResponseImpl<ObjectInfo>(returnVal, options.getPath(), marker, options
                  .getMaxResults(), truncated);

      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      this.request = request;
   }
}