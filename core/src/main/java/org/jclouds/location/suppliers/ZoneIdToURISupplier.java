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
package org.jclouds.location.suppliers;

import java.net.URI;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.suppliers.fromconfig.ZoneIdToURIFromConfigurationOrDefaultToProvider;

import com.google.common.base.Supplier;
import com.google.inject.ImplementedBy;
import com.google.inject.assistedinject.Assisted;

/**
 * 
 * 
 * @author Adrian Cole
 */
@ImplementedBy(ZoneIdToURIFromConfigurationOrDefaultToProvider.class)
public interface ZoneIdToURISupplier extends Supplier<Map<String, Supplier<URI>>> {
   static interface Factory {
      /**
       * 
       * @param apiType
       *           type of the api, according to the provider. ex. {@code compute} {@code
       *           object-store}
       * @param apiVersion
       *           version of the api, or null if not present
       * @return regions mapped to default uri
       */
      ZoneIdToURISupplier createForApiTypeAndVersion(@Assisted("apiType") String apiType,
               @Nullable @Assisted("apiVersion") String apiVersion);
   }
}
