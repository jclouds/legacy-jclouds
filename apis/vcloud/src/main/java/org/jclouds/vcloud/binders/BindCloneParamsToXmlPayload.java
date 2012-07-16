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

import java.util.Map;
import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.options.CloneOptions;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public abstract class BindCloneParamsToXmlPayload<O extends CloneOptions> implements MapBinder {

   protected final String ns;
   protected final String schema;
   private final BindToStringPayload stringBinder;

   protected abstract String getRootElement();
   protected abstract String getSourceMediaType();
   protected abstract Class<O> getOptionClass();

   @Inject
   public BindCloneParamsToXmlPayload(BindToStringPayload stringBinder,
            @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns, @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema) {
      this.ns = ns;
      this.schema = schema;
      this.stringBinder = stringBinder;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
               "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");
      String name = checkNotNull(postParams.get("name"), "name").toString();
      String source = checkNotNull(postParams.get("Source"), "Source").toString();
      boolean isSourceDelete = Boolean.parseBoolean((String) postParams.get("IsSourceDelete"));

      O options = findOptionsInArgsOrNew(gRequest);
      return stringBinder.bindToRequest(request, generateXml(name, source, isSourceDelete, options));
   }

   protected String generateXml(String name, String source, boolean isSourceDelete, O options) {
      XMLBuilder rootBuilder = buildRoot(name, options);
      addElementsUnderRoot(rootBuilder, source, options, isSourceDelete);
      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      try {
         return rootBuilder.asString(outputProperties);
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }

   protected void addElementsUnderRoot(XMLBuilder rootBuilder, String source, O options, boolean isSourceDelete) {
      if (options.getDescription() != null)
         rootBuilder.e("Description").text(options.getDescription());
      rootBuilder.e("Source").a("href", source).a("type", getSourceMediaType());
      if (isSourceDelete)
         rootBuilder.e("IsSourceDelete").t("true");
   }

   protected XMLBuilder buildRoot(String name, O options) {
      try {
         return XMLBuilder.create(getRootElement()).a("xmlns", ns).a("xmlns:xsi",
                  "http://www.w3.org/2001/XMLSchema-instance").a("xsi:schemaLocation", ns + " " + schema).a("name",
                  name);
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }

   @SuppressWarnings("unchecked")
   protected O findOptionsInArgsOrNew(GeneratedHttpRequest gRequest) {
      for (Object arg : gRequest.getArgs()) {
         if (getOptionClass().isInstance(arg)) {
            return (O) arg;
         } else if (arg.getClass().isArray()) {
            Object[] array = (Object[]) arg;
            if (array.length > 0 && getOptionClass().isInstance(array[0]))
               return (O) array[0];
         }
      }
      try {
         return getOptionClass().newInstance();
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException("CloneParams is needs parameters");
   }

   protected String ifNullDefaultTo(String value, String defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }
}
