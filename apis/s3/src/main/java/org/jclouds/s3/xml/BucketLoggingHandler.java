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
package org.jclouds.s3.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;

import java.net.URI;
import java.util.Set;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.s3.domain.AccessControlList.Grant;
import org.jclouds.s3.domain.AccessControlList.Grantee;
import org.jclouds.s3.domain.AccessControlList.GroupGrantee;
import org.jclouds.s3.domain.BucketLogging;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * BucketLoggingStatus xmlns="http://s3.amazonaws.com/doc/2006-03-01/"
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/latest/index.html?ServerLogs.html"/>
 */
public class BucketLoggingHandler extends ParseSax.HandlerWithResult<BucketLogging> {
   private Set<Grant> targetGrants = Sets.newHashSet();
   private StringBuilder currentText = new StringBuilder();

   public BucketLogging getResult() {
      if (targetBucket == null)
         return null;
      return new BucketLogging(targetBucket, targetPrefix, targetGrants);
   }

   private String currentId;
   private String currentDisplayName;
   private String currentGranteeType;
   private String currentPermission;
   private Grantee currentGrantee;

   private String targetBucket;
   private String targetPrefix;

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("Grantee")) {
         currentGranteeType = attrs.getValue("xsi:type");
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("TargetBucket")) {
         this.targetBucket = currentOrNull(currentText);
      } else if (qName.equals("TargetPrefix")) {
         this.targetPrefix = currentOrNull(currentText);
      } else if (qName.equals("Grantee")) {
         if ("AmazonCustomerByEmail".equals(currentGranteeType)) {
            currentGrantee = new EmailAddressGrantee(currentId);
         } else if ("CanonicalUser".equals(currentGranteeType)) {
            currentGrantee = new CanonicalUserGrantee(currentId, currentDisplayName);
         } else if ("Group".equals(currentGranteeType)) {
            currentGrantee = new GroupGrantee(URI.create(currentId));
         }
      } else if (qName.equals("Grant")) {
         targetGrants.add(new Grant(currentGrantee, currentPermission));
      } else if (qName.equals("ID") || qName.equals("EmailAddress") || qName.equals("URI")) {
         currentId = currentOrNull(currentText);
      } else if (qName.equals("DisplayName")) {
         currentDisplayName = currentOrNull(currentText);
      } else if (qName.equals("Permission")) {
         currentPermission = currentOrNull(currentText);
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
