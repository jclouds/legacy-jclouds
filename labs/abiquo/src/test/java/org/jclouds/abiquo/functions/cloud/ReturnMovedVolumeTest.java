/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.functions.cloud;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import javax.ws.rs.core.Response.Status;

import org.easymock.EasyMock;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.xml.internal.JAXBParser;
import org.testng.annotations.Test;

import com.abiquo.server.core.infrastructure.storage.MovedVolumeDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;
import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * Unit tests for the {@link ReturnMovedVolume} function.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "ReturnMovedVolumeTest")
public class ReturnMovedVolumeTest {
   public void testReturnOriginalExceptionIfNotHttpResponseException() {
      Function<Exception, VolumeManagementDto> function = new ReturnMovedVolume(new ReturnMoveVolumeReference(
            new JAXBParser("false"), TypeLiteral.get(MovedVolumeDto.class)));

      RuntimeException exception = new RuntimeException();

      try {
         function.apply(exception);
      } catch (Exception ex) {
         assertEquals(ex, exception);
      }
   }

   public void testReturnVolume() throws IOException {
      JAXBParser xmlParser = new JAXBParser("false");
      Function<Exception, VolumeManagementDto> function = new ReturnMovedVolume(new ReturnMoveVolumeReference(
            new JAXBParser("false"), TypeLiteral.get(MovedVolumeDto.class)));

      VolumeManagementDto volume = new VolumeManagementDto();
      volume.setName("Test volume");
      MovedVolumeDto movedRef = new MovedVolumeDto();
      movedRef.setVolume(volume);

      HttpResponse response = EasyMock.createMock(HttpResponse.class);
      HttpResponseException exception = EasyMock.createMock(HttpResponseException.class);
      Payload payload = Payloads.newPayload(xmlParser.toXML(movedRef));

      // Status code is called once
      expect(response.getStatusCode()).andReturn(Status.MOVED_PERMANENTLY.getStatusCode());
      // Get response gets called twice
      expect(exception.getResponse()).andReturn(response);
      expect(exception.getResponse()).andReturn(response);
      // Get payload is called three times: one to deserialize it, and twice to
      // release it
      expect(response.getPayload()).andReturn(payload);
      expect(response.getPayload()).andReturn(payload);
      expect(response.getPayload()).andReturn(payload);
      // Get cause is called to determine the root cause
      expect(exception.getCause()).andReturn(null);

      replay(response);
      replay(exception);

      function.apply(exception);

      verify(response);
      verify(exception);
   }
}
