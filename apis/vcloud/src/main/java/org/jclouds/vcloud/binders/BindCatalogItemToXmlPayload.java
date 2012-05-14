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
package org.jclouds.vcloud.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.options.CatalogItemOptions;

import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindCatalogItemToXmlPayload implements MapBinder {

   protected final String ns;
   protected final String schema;
   private final BindToStringPayload stringBinder;

   @Inject
   public BindCatalogItemToXmlPayload(BindToStringPayload stringBinder,
            @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns, @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema) {
      this.ns = ns;
      this.schema = schema;
      this.stringBinder = stringBinder;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest<?>,
               "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest<?> gRequest = (GeneratedHttpRequest<?>) request;
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");
      String name = checkNotNull(postParams.get("name"), "name").toString();
      URI entity = URI.create(checkNotNull(postParams.get("Entity"), "Entity").toString());

      CatalogItemOptions options = findOptionsInArgsOrNew(gRequest);
      try {
         return stringBinder.bindToRequest(request, generateXml(name, entity, options));
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (FactoryConfigurationError e) {
         throw new RuntimeException(e);
      } catch (TransformerException e) {
         throw new RuntimeException(e);
      }

   }

   protected String generateXml(String templateName, URI entity, CatalogItemOptions options)
            throws ParserConfigurationException, FactoryConfigurationError, TransformerException {
      XMLBuilder rootBuilder = buildRoot(templateName);
      if (options.getDescription() != null)
         rootBuilder.e("Description").t(options.getDescription());
      rootBuilder.e("Entity").a("href", entity.toASCIIString());
      for (Entry<String, String> entry : options.getProperties().entrySet()) {
         rootBuilder.e("Property").a("key", entry.getKey()).t(entry.getValue());
      }
      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return rootBuilder.asString(outputProperties);
   }

   protected XMLBuilder buildRoot(String name) throws ParserConfigurationException, FactoryConfigurationError {
      XMLBuilder rootBuilder = XMLBuilder.create("CatalogItem").a("name", name).a("xmlns", ns).a("xmlns:xsi",
               "http://www.w3.org/2001/XMLSchema-instance").a("xsi:schemaLocation", ns + " " + schema);
      return rootBuilder;
   }

   protected CatalogItemOptions findOptionsInArgsOrNew(GeneratedHttpRequest<?> gRequest) {
      for (Object arg : gRequest.getArgs()) {
         if (arg instanceof CatalogItemOptions) {
            return CatalogItemOptions.class.cast(arg);
         } else if (arg.getClass().isArray()) {
            Object[] array = (Object[]) arg;
            if (array.length > 0 && array[0] instanceof CatalogItemOptions)
               return CatalogItemOptions.class.cast(array[0]);
         }
      }
      return new CatalogItemOptions();
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException("CatalogItem is needs parameters");
   }

   protected String ifNullDefaultTo(String value, String defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }
}
