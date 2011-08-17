/**
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
package org.jclouds.deltacloud.xml;

import java.util.Map;

import javax.annotation.Resource;

import org.jclouds.deltacloud.domain.Transition;
import org.jclouds.deltacloud.domain.TransitionAutomatically;
import org.jclouds.deltacloud.domain.TransitionOnAction;
import org.jclouds.deltacloud.domain.Instance.Action;
import org.jclouds.deltacloud.domain.Instance.State;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
public class InstanceStatesHandler extends ParseSax.HandlerWithResult<Multimap<State, ? extends Transition>> {

   @Resource
   protected Logger logger = Logger.NULL;

   private Multimap<State, Transition> states = LinkedHashMultimap.create();
   private State state;

   public Multimap<State, ? extends Transition> getResult() {
      return states;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.equals("state")) {
         state = instanceStateWarningOnUnrecognized(attributes.get("name"));
      } else if (qName.equals("transition")) {
         if (attributes.containsKey("auto"))
            states.put(state, new TransitionAutomatically(instanceStateWarningOnUnrecognized(attributes.get("to"))));
         else
            states.put(state, new TransitionOnAction(instanceActionWarningOnUnrecognized(attributes.get("action")),
                     instanceStateWarningOnUnrecognized(attributes.get("to"))));
      }
   }

   State instanceStateWarningOnUnrecognized(String input) {
      State state = State.fromValue(input);
      if (state == State.UNRECOGNIZED)
         logger.warn("unrecognized state: %s", input);
      return state;
   }

   Action instanceActionWarningOnUnrecognized(String input) {
      Action action = Action.fromValue(input);
      if (action == Action.UNRECOGNIZED)
         logger.warn("unrecognized action: %s", input);
      return action;
   }

}
