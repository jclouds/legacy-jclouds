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

import org.jclouds.deltacloud.domain.Transition;
import org.jclouds.deltacloud.domain.TransitionAutomatically;
import org.jclouds.deltacloud.domain.TransitionOnAction;
import org.jclouds.deltacloud.domain.Instance.Action;
import org.jclouds.deltacloud.domain.Instance.State;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Tests behavior of {@code StatesHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "StatesHandlerTest")
public class InstanceStatesHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/test_get_states.xml");
      Multimap<State, ? extends Transition> expects = ImmutableMultimap.<State, Transition> builder().put(State.START,
               new TransitionOnAction(Action.CREATE, State.PENDING)).put(State.PENDING,
               new TransitionAutomatically(State.RUNNING))
               .putAll(State.RUNNING, new TransitionOnAction(Action.REBOOT, State.RUNNING),
                        new TransitionOnAction(Action.STOP, State.STOPPED)).putAll(State.STOPPED,
                        new TransitionOnAction(Action.START, State.RUNNING),
                        new TransitionOnAction(Action.DESTROY, State.FINISH)).build();
      assertEquals(factory.create(injector.getInstance(InstanceStatesHandler.class)).parse(is).entries(), expects
               .entries());

   }
}
