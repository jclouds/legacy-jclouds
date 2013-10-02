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

import java.net.URI;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.logging.Logger;
import org.jclouds.ovf.Disk;
import org.jclouds.ovf.DiskSection;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class DiskSectionHandler extends SectionHandler<DiskSection, DiskSection.Builder> {

   @Resource
   protected Logger logger = Logger.NULL;
   protected Disk.Builder diskBuilder = Disk.builder();

   @Inject
   public DiskSectionHandler(Provider<DiskSection.Builder> builderProvider) {
      super(builderProvider);
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "Disk")) {
         diskBuilder.id(attributes.get("diskId"));
         diskBuilder.capacity(attemptToParse(attributes.get("capacity"), "capacity", attributes.get("diskId")));
         diskBuilder.parentRef(attributes.get("parentRef"));
         diskBuilder.fileRef(attributes.get("fileRef"));
         if (attributes.containsKey("format"))
            diskBuilder.format(URI.create(attributes.get("format")));
         diskBuilder.populatedSize(attemptToParse(attributes.get("populatedSize"), "populatedSize", attributes
                  .get("diskId")));
         diskBuilder.capacityAllocationUnits(attributes.get("capacityAllocationUnits"));
      }
   }

   private Long attemptToParse(String toParse, String key, String diskId) {
      Long val = null;
      if (toParse != null) {
         try {
            val = Long.valueOf(toParse);
         } catch (NumberFormatException e) {
            logger.warn("%s for disk %s not a number [%s]", key, diskId, toParse);
         }
      }
      return val;
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (equalsOrSuffix(qName, "Info")) {
         builder.info(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Disk")) {
         try {
            builder.disk(diskBuilder.build());
         } finally {
            diskBuilder = Disk.builder();
         }
      }
      super.endElement(uri, localName, qName);
   }

}
