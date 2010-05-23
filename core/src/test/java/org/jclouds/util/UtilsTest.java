/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.util;

import static org.easymock.classextension.EasyMock.createMock;
import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.inject.ProvisionException;
import com.google.inject.internal.ImmutableList;
import com.google.inject.spi.Message;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "jclouds.UtilsTest")
public class UtilsTest {

   public void testGetCause() {
      AuthorizationException aex = createMock(AuthorizationException.class);
      Message message = new Message(ImmutableList.of(), "test", aex);
      ProvisionException pex = new ProvisionException(ImmutableSet.of(message));
      assertEquals(Utils.firstRootCauseOrOriginalException(pex), aex);
   }

   public void testReplaceTokens() throws UnsupportedEncodingException {
      assertEquals(Utils.replaceTokens("hello {where}", ImmutableMap.of("where", "world")),
               "hello world");
   }

   public void testMultiMax() {
      Iterable<String> values = ImmutableList.of("1", "2", "2", "3", "3");
      assertEquals(Utils.multiMax(Ordering.natural(), values), ImmutableList.of("3", "3"));
   }

   public void testMultiMax1() {
      Iterable<String> values = ImmutableList.of("1", "2", "2", "3");
      assertEquals(Utils.multiMax(Ordering.natural(), values), ImmutableList.of("3"));
   }
}
