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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.http.HttpException;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.s3.domain.AccessControlList.Permission;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code AccessControlListHandler}
 * 
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AccessControlListHandlerTest")
public class AccessControlListHandlerTest extends BaseHandlerTest {
   public static final String aclOwnerOnly = "<AccessControlPolicy xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><Owner><ID>1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c</ID><DisplayName>jamesmurty</DisplayName></Owner><AccessControlList><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CanonicalUser\"><ID>1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c</ID><DisplayName>jamesmurty</DisplayName></Grantee><Permission>FULL_CONTROL</Permission></Grant></AccessControlList></AccessControlPolicy>";
   public static final String aclExtreme = "<AccessControlPolicy xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><Owner><ID>1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c</ID><DisplayName>jamesmurty</DisplayName></Owner><AccessControlList><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"Group\"><URI>http://acs.amazonaws.com/groups/global/AuthenticatedUsers</URI></Grantee><Permission>WRITE</Permission></Grant><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"Group\"><URI>http://acs.amazonaws.com/groups/global/AuthenticatedUsers</URI></Grantee><Permission>READ_ACP</Permission></Grant><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CanonicalUser\"><ID>1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c</ID><DisplayName>jamesmurty</DisplayName></Grantee><Permission>WRITE</Permission></Grant><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"Group\"><URI>http://acs.amazonaws.com/groups/global/AuthenticatedUsers</URI></Grantee><Permission>WRITE_ACP</Permission></Grant><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"Group\"><URI>http://acs.amazonaws.com/groups/global/AllUsers</URI></Grantee><Permission>READ</Permission></Grant><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"Group\"><URI>http://acs.amazonaws.com/groups/global/AuthenticatedUsers</URI></Grantee><Permission>READ</Permission></Grant><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"Group\"><URI>http://acs.amazonaws.com/groups/s3/LogDelivery</URI></Grantee><Permission>WRITE</Permission></Grant><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CanonicalUser\"><ID>1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c</ID><DisplayName>jamesmurty</DisplayName></Grantee><Permission>READ</Permission></Grant><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CanonicalUser\"><ID>1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c</ID><DisplayName>jamesmurty</DisplayName></Grantee><Permission>FULL_CONTROL</Permission></Grant></AccessControlList></AccessControlPolicy>";

   ParseSax<AccessControlList> createParser() {
      ParseSax<AccessControlList> parser = factory.create(injector
               .getInstance(AccessControlListHandler.class));
      return parser;
   }

   @Test
   public void testAccessControlListOwnerOnly() throws HttpException {
      String ownerId = "1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c";
      AccessControlList acl = createParser().parse(Strings2.toInputStream(aclOwnerOnly));
      assertEquals(acl.getOwner().getId(), ownerId);
      assertEquals(acl.getOwner().getDisplayName(), "jamesmurty");
      assertEquals(acl.getPermissions(ownerId).size(), 1);
      assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
      assertEquals(acl.getGrants().size(), 1);
      assertEquals(acl.getPermissions(GroupGranteeURI.ALL_USERS).size(), 0);
      assertEquals(acl.getPermissions(GroupGranteeURI.AUTHENTICATED_USERS).size(), 0);
      assertEquals(acl.getPermissions(GroupGranteeURI.LOG_DELIVERY).size(), 0);
   }

   @Test
   public void testAccessControlListExtreme() throws HttpException {
      String ownerId = "1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c";
      AccessControlList acl = createParser().parse(Strings2.toInputStream(aclExtreme));
      assertEquals(acl.getOwner().getId(), ownerId);
      assertEquals(acl.getOwner().getDisplayName(), "jamesmurty");
      assertEquals(acl.getPermissions(ownerId).size(), 3);
      assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
      assertTrue(acl.hasPermission(ownerId, Permission.READ));
      assertTrue(acl.hasPermission(ownerId, Permission.WRITE));
      assertEquals(acl.getGrants().size(), 9);
      assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ));
      assertTrue(acl.hasPermission(GroupGranteeURI.AUTHENTICATED_USERS, Permission.READ));
      assertTrue(acl.hasPermission(GroupGranteeURI.AUTHENTICATED_USERS, Permission.WRITE));
      assertTrue(acl.hasPermission(GroupGranteeURI.AUTHENTICATED_USERS, Permission.READ_ACP));
      assertTrue(acl.hasPermission(GroupGranteeURI.AUTHENTICATED_USERS, Permission.WRITE_ACP));
      assertTrue(acl.hasPermission(GroupGranteeURI.LOG_DELIVERY, Permission.WRITE));
   }

}
