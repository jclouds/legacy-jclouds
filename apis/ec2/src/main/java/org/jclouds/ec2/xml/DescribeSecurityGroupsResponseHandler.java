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
package org.jclouds.ec2.xml;

import static org.jclouds.util.SaxUtils.currentOrNegative;
import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.ec2.domain.IpPermissionImpl;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.Region;
import org.xml.sax.Attributes;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Parses: DescribeSecurityGroupsResponse
 * xmlns="http://ec2.amazonaws.com/doc/2010-06-15/"
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-query-DescribeSecurityGroups.html"
 *      />
 * @author Adrian Cole
 */
public class DescribeSecurityGroupsResponseHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<Set<SecurityGroup>> {
   @Inject
   @Region
   Supplier<String> defaultRegion;

   private StringBuilder currentText = new StringBuilder();
   private Set<SecurityGroup> securtyGroups = Sets.newLinkedHashSet();
   private String groupId;
   private String groupName;
   private String ownerId;
   private String groupDescription;
   private Set<IpPermissionImpl> ipPermissions = Sets.newLinkedHashSet();
   private int fromPort;
   private int toPort;
   private Multimap<String, String> groups = LinkedHashMultimap.create();
   private String userId;
   private String userIdGroupName;
   private IpProtocol ipProtocol;
   private Set<String> ipRanges = Sets.newLinkedHashSet();

   private boolean inIpPermissions;
   private boolean inIpRanges;
   private boolean inGroups;

   public Set<SecurityGroup> getResult() {
      return securtyGroups;
   }

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (equalsOrSuffix(qName, "ipPermissions")) {
         inIpPermissions = true;
      } else if (equalsOrSuffix(qName, "ipRanges")) {
         inIpRanges = true;
      } else if (equalsOrSuffix(qName, "groups")) {
         inGroups = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "groupName")) {
         if (!inGroups)
            this.groupName = currentOrNull(currentText);
         else
            this.userIdGroupName = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "groupId")) {
         this.groupId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "ownerId")) {
         this.ownerId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "userId")) {
         this.userId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "groupDescription")) {
         this.groupDescription = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "ipProtocol")) {
         // Algorete: ipProtocol can be an empty tag on EC2 clone (e.g. OpenStack EC2)
         this.ipProtocol = IpProtocol.fromValue(currentOrNegative(currentText));
      } else if (equalsOrSuffix(qName, "fromPort")) {
         // Algorete: fromPort can be an empty tag on EC2 clone (e.g. OpenStack EC2)
         this.fromPort = Integer.parseInt(currentOrNegative(currentText));
      } else if (equalsOrSuffix(qName, "toPort")) {
         // Algorete: toPort can be an empty tag on EC2 clone (e.g. OpenStack EC2)
         this.toPort = Integer.parseInt(currentOrNegative(currentText));
      } else if (equalsOrSuffix(qName, "cidrIp")) {
         this.ipRanges.add(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "ipPermissions")) {
         inIpPermissions = false;
      } else if (equalsOrSuffix(qName, "ipRanges")) {
         inIpRanges = false;
      } else if (equalsOrSuffix(qName, "groups")) {
         inGroups = false;
      } else if (equalsOrSuffix(qName, "item")) {
         if (inIpPermissions && !inIpRanges && !inGroups) {
            // TODO groups? we need an example of VPC stuff
            ipPermissions.add(new IpPermissionImpl(ipProtocol, fromPort, toPort, groups, ImmutableSet.<String> of(),
                  ipRanges));
            this.fromPort = -1;
            this.toPort = -1;
            this.groups = LinkedHashMultimap.create();
            this.ipProtocol = null;
            this.ipRanges = Sets.newLinkedHashSet();
         } else if (inIpPermissions && !inIpRanges && inGroups) {
            this.groups.put(userId, userIdGroupName);
            this.userId = null;
            this.userIdGroupName = null;
         } else if (!inIpPermissions && !inIpRanges && !inGroups) {
            String region = AWSUtils.findRegionInArgsOrNull(getRequest());
            if (region == null)
               region = defaultRegion.get();
            securtyGroups.add(new SecurityGroup(region, groupId, groupName, ownerId, groupDescription, ipPermissions));
            this.groupName = null;
            this.groupId = null;
            this.ownerId = null;
            this.groupDescription = null;
            this.ipPermissions = Sets.newLinkedHashSet();
         }
      }

      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
