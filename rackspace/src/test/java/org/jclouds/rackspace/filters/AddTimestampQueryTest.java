/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.filters;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.util.Date;

import javax.inject.Provider;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */

@Test(groups = "unit", testName = "rackspace.AddTimestampQueryTest")
public class AddTimestampQueryTest {

   @Test
   public void testApplySetsKey() {
      final Date date = new Date();
      Provider<Date> dateProvider = new Provider<Date>() {

         @Override
         public Date get() {
            return date;
         }

      };
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      request.addQueryParam("now", date.getTime() + "");
      replay(request);

      AddTimestampQuery filter = new AddTimestampQuery(dateProvider);
      filter.filter(request);
   }

}
