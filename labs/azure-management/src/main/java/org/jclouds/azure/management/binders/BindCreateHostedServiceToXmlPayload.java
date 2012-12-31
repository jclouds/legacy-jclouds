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
package org.jclouds.azure.management.binders;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base64;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.jclouds.azure.management.options.CreateHostedServiceOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindCreateHostedServiceToXmlPayload implements MapBinder {

   private static final CreateHostedServiceOptions NO_OPTIONS = new CreateHostedServiceOptions();

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      String serviceName = checkNotNull(postParams.get("serviceName"), "serviceName").toString();
      String label = base64().encode(checkNotNull(postParams.get("label"), "label").toString().getBytes(UTF_8));

      Optional<String> location = Optional.fromNullable((String) postParams.get("location"));
      Optional<String> affinityGroup = Optional.fromNullable((String) postParams.get("affinityGroup"));
      CreateHostedServiceOptions options = Optional
               .fromNullable((CreateHostedServiceOptions) postParams.get("options")).or(NO_OPTIONS);
      try {
         XMLBuilder createHostedService = XMLBuilder.create("CreateHostedService")
                  .a("xmlns", "http://schemas.microsoft.com/windowsazure").e("ServiceName").t(serviceName).up()
                  .e("Label").t(label).up();

         if (options.getDescription().isPresent())
            createHostedService.e("Description").t(options.getDescription().get()).up();

         if (location.isPresent())
            createHostedService.e("Location").t(location.get()).up();
         else if (affinityGroup.isPresent())
            createHostedService.e("AffinityGroup").t(affinityGroup.get()).up();
         else
            throw new IllegalArgumentException("you must specify either Location or AffinityGroup!");

         if (options.getExtendedProperties().isPresent() && options.getExtendedProperties().get().size() > 0) {
            XMLBuilder extendedProperties = createHostedService.e("ExtendedProperties");
            for (Entry<String, String> entry : options.getExtendedProperties().get().entrySet())
               extendedProperties.e("ExtendedProperty").e("Name").t(entry.getKey()).up().e("Value").t(entry.getValue());
         }
         return (R) request.toBuilder().payload(createHostedService.asString()).build();
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }

   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException("BindCreateHostedServiceToXmlPayload is needs parameters");
   }

}
