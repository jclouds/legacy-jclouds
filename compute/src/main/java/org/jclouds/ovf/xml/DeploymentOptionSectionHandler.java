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
package org.jclouds.ovf.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.ovf.Configuration;
import org.jclouds.ovf.DeploymentOptionSection;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class DeploymentOptionSectionHandler extends
         SectionHandler<DeploymentOptionSection, DeploymentOptionSection.Builder> {
   protected Configuration.Builder configBuilder = Configuration.builder();

   @Inject
   public DeploymentOptionSectionHandler(Provider<DeploymentOptionSection.Builder> builderProvider) {
      super(builderProvider);
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "Configuration")) {
         configBuilder.id(attributes.get("id"));
         // TODO default;
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (equalsOrSuffix(qName, "Info")) {
         builder.info(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Label")) {
         configBuilder.label(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Description")) {
         configBuilder.description(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Configuration")) {
         try {
            builder.configuration(configBuilder.build());
         } finally {
            configBuilder = Configuration.builder();
         }
      }
      super.endElement(uri, localName, qName);
   }
}
