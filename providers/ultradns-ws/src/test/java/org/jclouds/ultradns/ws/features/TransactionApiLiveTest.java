/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ultradns.ws.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.TooManyTransactionsException;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "TransactionApiLiveTest")
public class TransactionApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   @Test
   public void commitTransaction() {
      String tx = api().start();
      assertNotNull(tx);
      api().commit(tx);
   }

   @Test
   public void rollbackTransaction() {
      String tx = api().start();
      assertNotNull(tx);
      api().rollback(tx);
   }

   @Test(expectedExceptions = TooManyTransactionsException.class, expectedExceptionsMessageRegExp = "Ultra API only allows 3 concurrent transactions per user")
   public void only3Transactions() {
      List<String> txIds = Lists.newArrayList();
      try {
         while (true)
            txIds.add(api().start());
      } finally {
         for (String tx : txIds)
            api().rollback(tx);
         assertEquals(txIds.size(), 3);
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "No transaction with Id AAAAAAAAAAAAAAAA found for the user .*")
   public void commitTransactionWhenNotFound() {
      api().commit("AAAAAAAAAAAAAAAA");
   }

   @Test
   public void testRollbackTransactionWhenNotFound() {
      api().rollback("AAAAAAAAAAAAAAAA");
   }

   protected TransactionApi api() {
      return api.getTransactionApi();
   }
}
