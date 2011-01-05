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

package org.jclouds.aws.handlers;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code AWSClientErrorRetryHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class AWSClientErrorRetryHandlerTest {
   @Test
   public void test401DoesNotRetry() {

      AWSUtils utils = createMock(AWSUtils.class);
      HttpCommand command = createMock(HttpCommand.class);
      HttpResponse response = createMock(HttpResponse.class);

      expect(command.getFailureCount()).andReturn(0);
      expect(response.getStatusCode()).andReturn(401).atLeastOnce();

      replay(utils);
      replay(command);
      replay(response);

      AWSClientErrorRetryHandler retry = new AWSClientErrorRetryHandler(utils);

      assert !retry.shouldRetryRequest(command, response);

      verify(utils);
      verify(command);
      verify(response);

   }
}
