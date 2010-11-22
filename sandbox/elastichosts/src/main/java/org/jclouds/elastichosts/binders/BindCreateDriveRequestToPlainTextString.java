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

package org.jclouds.elastichosts.binders;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.elastichosts.domain.CreateDriveRequest;
import org.jclouds.elastichosts.functions.CreateDriveRequestToMap;
import org.jclouds.elastichosts.functions.ListOfMapsToListOfKeyValuesDelimitedByBlankLines;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindCreateDriveRequestToPlainTextString implements Binder {
   private final CreateDriveRequestToMap createDriveRequestToMap;
   private final ListOfMapsToListOfKeyValuesDelimitedByBlankLines listOfMapsToListOfKeyValuesDelimitedByBlankLines;

   @Inject
   public BindCreateDriveRequestToPlainTextString(CreateDriveRequestToMap createDriveRequestToMap,
         ListOfMapsToListOfKeyValuesDelimitedByBlankLines listOfMapsToListOfKeyValuesDelimitedByBlankLines) {
      this.createDriveRequestToMap = createDriveRequestToMap;
      this.listOfMapsToListOfKeyValuesDelimitedByBlankLines = listOfMapsToListOfKeyValuesDelimitedByBlankLines;
   }

   public void bindToRequest(HttpRequest request, Object payload) {
      checkArgument(payload instanceof CreateDriveRequest, "this binder is only valid for CreateDriveRequest!");
      CreateDriveRequest create = CreateDriveRequest.class.cast(payload);
      Map<String, String> map = createDriveRequestToMap.apply(create);
      request.setPayload(listOfMapsToListOfKeyValuesDelimitedByBlankLines.apply(ImmutableSet.of(map)));
      request.getPayload().getContentMetadata().setContentType(MediaType.TEXT_PLAIN);
   }
}
