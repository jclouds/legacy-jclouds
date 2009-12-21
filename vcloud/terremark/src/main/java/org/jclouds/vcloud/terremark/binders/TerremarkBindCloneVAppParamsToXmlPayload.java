/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.binders;

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.vcloud.binders.BindCloneVAppParamsToXmlPayload;

import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class TerremarkBindCloneVAppParamsToXmlPayload extends BindCloneVAppParamsToXmlPayload {

   @Inject
   public TerremarkBindCloneVAppParamsToXmlPayload(BindToStringPayload stringBinder,
            @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns,
            @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema) {
     super(stringBinder, ns,schema);
   }
   
   @Override
   protected XMLBuilder buildRoot(String name, boolean deploy, boolean powerOn)
            throws ParserConfigurationException, FactoryConfigurationError {
      XMLBuilder rootBuilder = XMLBuilder.create("CloneVAppParamsType").a("name", name).a("deploy",
               deploy+"").a("powerOn", powerOn+"").a("xmlns", ns).a("xmlns:xsi",
               "http://www.w3.org/2001/XMLSchema-instance").a("xsi:schemaLocation",
               ns + " " + schema);
      return rootBuilder;
   }

}
