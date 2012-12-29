/*
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

package org.jclouds.googlecompute.features;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.internal.BaseGoogleComputeApiLiveTest;
import org.jclouds.googlecompute.options.ListOptions;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.jclouds.googlecompute.features.ProjectApiLiveTest.addItemToMetadata;
import static org.jclouds.googlecompute.features.ProjectApiLiveTest.deleteItemFromMetadata;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author David Alves
 */
public class OperationApiLiveTest extends BaseGoogleComputeApiLiveTest {

   private static final String METADATA_ITEM_KEY = "operationLiveTestTestProp";
   private static final String METADATA_ITEM_VALUE = "operationLiveTestTestValue";
   private Operation addOperation;
   private Operation deleteOperation;

   private OperationApi api() {
      return context.getApi().getOperationApiForProject(getUserProject());
   }


   @Test(groups = "live")
   public void testCreateOperations() {
      //create some operations by adding and deleting metadata items
      // this will make sure there is stuff to listFirstPage
      addOperation = assertOperationDoneSucessfully(addItemToMetadata(context.getApi().getProjectApi(),
              getUserProject(), METADATA_ITEM_KEY, METADATA_ITEM_VALUE), 20);
      deleteOperation = assertOperationDoneSucessfully(deleteItemFromMetadata(context.getApi()
              .getProjectApi(), getUserProject(), METADATA_ITEM_KEY), 20);

      assertNotNull(addOperation);
      assertNotNull(deleteOperation);
   }

   @Test(groups = "live", dependsOnMethods = "testCreateOperations")
   public void testGetOperation() {
      Operation operation = api().get(addOperation.getName());
      assertNotNull(operation);
      assertOperationEquals(operation, this.addOperation);
   }

   @Test(groups = "live", dependsOnMethods = "testCreateOperations")
   public void testListOperationsWithFiltersAndPagination() {
      PagedIterable<Operation> operations = api().list(new ListOptions.Builder()
              .filter("operationType eq setMetadata")
              .maxResults(1));

      // make sure that in spite of having only one result per page we get at least two results
      final AtomicInteger counter = new AtomicInteger();
      operations.firstMatch(new Predicate<IterableWithMarker<Operation>>() {

         @Override
         public boolean apply(IterableWithMarker<Operation> input) {
            counter.addAndGet(Iterables.size(input));
            return counter.get() == 2;
         }
      });
   }

   private void assertOperationEquals(Operation result, Operation expected) {
      assertEquals(result.getName(), expected.getName());
   }


}
