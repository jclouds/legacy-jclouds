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
package org.jclouds.vcloud.binders;

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.options.CloneVAppTemplateOptions;

import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindCloneVAppTemplateParamsToXmlPayload extends BindCloneParamsToXmlPayload<CloneVAppTemplateOptions> {

   @Inject
   public BindCloneVAppTemplateParamsToXmlPayload(BindToStringPayload stringBinder,
            @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns, @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema) {
      super(stringBinder, schema, schema);
   }

   @Override
   protected Class<CloneVAppTemplateOptions> getOptionClass() {
      return CloneVAppTemplateOptions.class;
   }

   @Override
   protected String getRootElement() {
      return "CloneVAppTemplateParams";
   }

   @Override
   protected String getSourceMediaType() {
      return VCloudMediaType.VAPPTEMPLATE_XML;
   }

}
