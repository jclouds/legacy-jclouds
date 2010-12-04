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

package org.jclouds.aws.simpledb.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.aws.simpledb.domain.ListDomainsResponse;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code ListDomainsResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "simpledb.ListDomainsResponseHandlerTest")
public class ListDomainsResponseHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/simpledb/list_domains.xml");

      ListDomainsResponse result = factory.create(injector.getInstance(ListDomainsResponseHandler.class)).parse(is);

      assertEquals(
            result,
            new ListDomainsResponseHandler.ListDomainsResponseImpl(ImmutableSet.of("Domain1-200706011651",
                  "Domain2-200706011652"), "TWV0ZXJpbmdUZXN0RG9tYWluMS0yMDA3MDYwMTE2NTY"));

   }
}
