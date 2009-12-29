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
package org.jclouds.aws.ec2.xml;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.Snapshot;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code DescribeSnapshotsResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.DescribeSnapshotsResponseHandlerTest")
public class DescribeSnapshotsResponseHandlerTest extends BaseHandlerTest {
   public void testApplyInputStream() {
      DateService dateService = injector.getInstance(DateService.class);
      InputStream is = getClass().getResourceAsStream("/ec2/describe_snapshots.xml");

      Set<Snapshot> expected = Sets.newLinkedHashSet();
      expected.add(new Snapshot(Region.DEFAULT, "snap-78a54011", "vol-4d826724", 10,
               Snapshot.Status.PENDING, dateService.iso8601DateParse("2008-05-07T12:51:50.000Z"),
               80, "218213537122", "Daily Backup", null));

      DescribeSnapshotsResponseHandler handler = injector
               .getInstance(DescribeSnapshotsResponseHandler.class);
      addDefaultRegionToHandler(handler);
      Set<Snapshot> result = factory.create(handler).parse(is);

      assertEquals(result, expected);
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(new Object[] { Region.DEFAULT });
      replay(request);
      handler.setContext(request);
   }
}