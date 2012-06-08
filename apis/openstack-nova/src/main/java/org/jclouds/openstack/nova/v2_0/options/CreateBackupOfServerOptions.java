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
package org.jclouds.openstack.nova.v2_0.options;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @author Adam Lowe
 */
public class CreateBackupOfServerOptions implements MapBinder {
   public static final CreateBackupOfServerOptions NONE = new CreateBackupOfServerOptions();

   @Inject
   protected BindToJsonPayload jsonBinder;

   private Map<String, String> metadata = ImmutableMap.of();

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Map<String, Object> data = Maps.newHashMap();
      data.putAll(postParams);
      data.put("metadata", metadata);
      return jsonBinder.bindToRequest(request, ImmutableMap.of("createBackup", data));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("createBackupOfServer is a POST operation");
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (!(object instanceof CreateBackupOfServerOptions)) return false;
      final CreateBackupOfServerOptions other = CreateBackupOfServerOptions.class.cast(object);
      return equal(metadata, other.metadata);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(metadata);
   }

   protected ToStringHelper string() {
      return toStringHelper("").add("metadata", metadata);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /** @see #getMetadata() */
   public CreateBackupOfServerOptions metadata(Map<String, String> metadata) {
      this.metadata = metadata;
      return this;
   }

   /**
    * Extra image properties to include
    */
   public Map<String, String> getMetadata() {
      return metadata;
   }

   public static class Builder {
      /**
       * @see CreateBackupOfServerOptions#getMetadata()
       */
      public static CreateBackupOfServerOptions metadata(Map<String, String> metadata) {
         return new CreateBackupOfServerOptions().metadata(metadata);
      }
   }

}
