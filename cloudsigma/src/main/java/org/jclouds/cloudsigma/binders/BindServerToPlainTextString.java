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

package org.jclouds.cloudsigma.binders;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudsigma.domain.Server;
import org.jclouds.cloudsigma.functions.ListOfMapsToListOfKeyValuesDelimitedByBlankLines;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindServerToPlainTextString implements Binder {
   private final Function<Server, Map<String, String>> createServerRequestToMap;
   private final ListOfMapsToListOfKeyValuesDelimitedByBlankLines listOfMapsToListOfKeyValuesDelimitedByBlankLines;

   @Inject
   public BindServerToPlainTextString(Function<Server, Map<String, String>> createServerRequestToMap,
         ListOfMapsToListOfKeyValuesDelimitedByBlankLines listOfMapsToListOfKeyValuesDelimitedByBlankLines) {
      this.createServerRequestToMap = createServerRequestToMap;
      this.listOfMapsToListOfKeyValuesDelimitedByBlankLines = listOfMapsToListOfKeyValuesDelimitedByBlankLines;
   }
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      checkArgument(payload instanceof Server, "this binder is only valid for Server!");
      Server create = Server.class.cast(payload);
      Map<String, String> map = createServerRequestToMap.apply(create);
      request.setPayload(listOfMapsToListOfKeyValuesDelimitedByBlankLines.apply(ImmutableSet.of(map)));
      request.getPayload().getContentMetadata().setContentType(MediaType.TEXT_PLAIN);
      return request;
   }
}
