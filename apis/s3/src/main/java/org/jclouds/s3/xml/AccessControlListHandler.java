/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.s3.xml;

import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.CanonicalUser;
import org.jclouds.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.s3.domain.AccessControlList.Grantee;
import org.jclouds.s3.domain.AccessControlList.GroupGrantee;
import org.xml.sax.Attributes;

/**
 * Parses the following XML document:
 * <p/>
 * AccessControlPolicy xmlns="http://s3.amazonaws.com/doc/2006-03-01/"
 * 
 * @author James Murty
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAccessPolicy.html"/>
 */
public class AccessControlListHandler extends ParseSax.HandlerWithResult<AccessControlList> {
   private AccessControlList acl = new AccessControlList();
   private StringBuilder currentText = new StringBuilder();

   public AccessControlListHandler() {
   }

   public AccessControlList getResult() {
      return acl;
   }

   private String currentId;
   private String currentDisplayName;
   private String currentGranteeType;
   private String currentPermission;
   private Grantee currentGrantee;

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("Grantee")) {
         currentGranteeType = attrs.getValue("xsi:type");
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Owner")) {
         CanonicalUser owner = new CanonicalUser(currentId);
         owner.setDisplayName(currentDisplayName);
         acl.setOwner(owner);
      } else if (qName.equals("Grantee")) {
         if ("AmazonCustomerByEmail".equals(currentGranteeType)) {
            currentGrantee = new EmailAddressGrantee(currentId);
         } else if ("CanonicalUser".equals(currentGranteeType)) {
            currentGrantee = new CanonicalUserGrantee(currentId, currentDisplayName);
         } else if ("Group".equals(currentGranteeType)) {
            currentGrantee = new GroupGrantee(URI.create(currentId));
         }
      } else if (qName.equals("Grant")) {
         acl.addPermission(currentGrantee, currentPermission);
      }

      else if (qName.equals("ID") || qName.equals("EmailAddress") || qName.equals("URI")) {
         currentId = currentText.toString().trim();
      } else if (qName.equals("DisplayName")) {
         currentDisplayName = currentText.toString().trim();
      } else if (qName.equals("Permission")) {
         currentPermission = currentText.toString().trim();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
