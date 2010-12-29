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

package org.jclouds.deltacloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.deltacloud.domain.InstanceAction;
import org.jclouds.deltacloud.domain.InstanceState;
import org.jclouds.deltacloud.domain.Transition;
import org.jclouds.deltacloud.domain.TransitionAutomatically;
import org.jclouds.deltacloud.domain.TransitionOnAction;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Tests behavior of {@code InstanceStatesHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class InstanceStatesHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/test_get_states.xml");
      Multimap<InstanceState, ? extends Transition> expects = ImmutableMultimap
            .<InstanceState, Transition> builder()
            .put(InstanceState.START, new TransitionOnAction(InstanceAction.CREATE, InstanceState.PENDING))
            .put(InstanceState.PENDING, new TransitionAutomatically(InstanceState.RUNNING))
            .putAll(InstanceState.RUNNING, new TransitionOnAction(InstanceAction.REBOOT, InstanceState.RUNNING),
                  new TransitionOnAction(InstanceAction.STOP, InstanceState.STOPPED))
            .putAll(InstanceState.STOPPED, new TransitionOnAction(InstanceAction.START, InstanceState.RUNNING),
                  new TransitionOnAction(InstanceAction.DESTROY, InstanceState.FINISH)).build();

      // not sure why this isn"t always automatically called from surefire.
      setUpInjector();
      assertEquals(factory.create(injector.getInstance(InstanceStatesHandler.class)).parse(is).entries(),
            expects.entries());

   }
}
