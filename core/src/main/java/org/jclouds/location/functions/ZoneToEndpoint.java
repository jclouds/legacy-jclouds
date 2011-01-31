/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.location.functions;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.Zone;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ZoneToEndpoint implements Function<Object, URI> {

   private final Map<String, URI> zoneToEndpoint;

   @Inject
   public ZoneToEndpoint(@Zone Map<String, URI> zoneToEndpoint) {
      this.zoneToEndpoint = zoneToEndpoint;
   }

   @Override
   public URI apply(@Nullable Object from) {
      checkArgument(from != null, "you must specify a zone");
      return zoneToEndpoint.get(from);
   }
}