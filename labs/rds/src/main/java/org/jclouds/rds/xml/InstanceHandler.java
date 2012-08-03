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
package org.jclouds.rds.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rds.domain.Instance;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.HostAndPort;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/API_GetInstance.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class InstanceHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Instance> {

   protected final DateService dateService;
   protected final SubnetGroupHandler subnetGroupHandler;

   @Inject
   protected InstanceHandler(DateService dateService, SubnetGroupHandler subnetGroupHandler) {
      this.dateService = dateService;
      this.subnetGroupHandler = subnetGroupHandler;
   }

   private StringBuilder currentText = new StringBuilder();
   private Instance.Builder<?> builder = Instance.builder();

   private boolean inSubnetGroup;

   private String address;
   private Integer port;

   private ImmutableMap.Builder<String, String> securityGroupBuilder = ImmutableMap.<String, String> builder();

   private String groupName;
   private String status;

   /**
    * {@inheritDoc}
    */
   @Override
   public Instance getResult() {
      try {
         return builder.build();
      } finally {
         builder = Instance.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "DBSubnetGroup")) {
         inSubnetGroup = true;
      }
      if (inSubnetGroup) {
         subnetGroupHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {

      if (equalsOrSuffix(qName, "DBSubnetGroup")) {
         builder.subnetGroup(subnetGroupHandler.getResult());
         inSubnetGroup = false;
      } else if (inSubnetGroup) {
         subnetGroupHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "DBInstanceIdentifier")) {
         builder.id(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "InstanceCreateTime")) {
         builder.createdTime(dateService.iso8601DateParse(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "DBName")) {
         builder.name(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "AllocatedStorage")) {
         builder.allocatedStorageGB(Integer.parseInt(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "DBInstanceStatus")) {
         String rawStatus = currentOrNull(currentText);
         builder.rawStatus(rawStatus);
         builder.status(Instance.Status.fromValue(rawStatus));
      } else if (equalsOrSuffix(qName, "Address")) {
         address = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "Port")) {
         port = Integer.valueOf(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Endpoint")) {
         // sometimes in deleting state, address is null while port isn't
         if (address != null && port != null)
            builder.endpoint(HostAndPort.fromParts(address, port));
         address = null;
         port = null;
      } else if (equalsOrSuffix(qName, "DBSecurityGroupName")) {
         groupName = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "Status")) {
         status = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "DBSecurityGroup")) {
         securityGroupBuilder.put(groupName, status);
         groupName = status = null;
      } else if (equalsOrSuffix(qName, "DBSecurityGroups")) {
         builder.securityGroupNameToStatus(securityGroupBuilder.build());
         securityGroupBuilder = ImmutableMap.<String, String> builder();
      } else if (equalsOrSuffix(qName, "DBInstanceClass")) {
         builder.instanceClass(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "AvailabilityZone")) {
         builder.availabilityZone(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "MultiAZ")) {
         builder.multiAZ(Boolean.parseBoolean(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "Engine")) {
         builder.engine(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "EngineVersion")) {
         builder.engineVersion(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "LicenseModel")) {
         builder.licenseModel(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "MasterUsername")) {
         builder.masterUsername(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inSubnetGroup) {
         subnetGroupHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
