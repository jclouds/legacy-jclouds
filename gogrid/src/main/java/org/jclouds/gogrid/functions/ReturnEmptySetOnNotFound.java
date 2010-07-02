/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.gogrid.functions;

import static org.jclouds.util.Utils.propagateOrNull;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.gogrid.domain.Server;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnEmptySetOnNotFound implements Function<Exception, Set<Server>> {
   @SuppressWarnings("unchecked")
   public Set<Server> apply(Exception from) {
      if (from instanceof ResourceNotFoundException) {
         return ImmutableSet.<Server> of();
      }
      return Set.class.cast(propagateOrNull(from));
   }
}