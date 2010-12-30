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

import java.util.Map;

import org.jclouds.deltacloud.domain.InstanceAction;
import org.jclouds.deltacloud.domain.InstanceState;
import org.jclouds.deltacloud.domain.Transition;
import org.jclouds.deltacloud.domain.TransitionAutomatically;
import org.jclouds.deltacloud.domain.TransitionOnAction;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
public class InstanceStatesHandler extends ParseSax.HandlerWithResult<Multimap<InstanceState, ? extends Transition>> {

   private Multimap<InstanceState, Transition> states = LinkedHashMultimap.create();
   private InstanceState state;

   public Multimap<InstanceState, ? extends Transition> getResult() {
      return states;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.equals("state")) {
         state = InstanceState.valueOf(attributes.get("name").toUpperCase());
      } else if (qName.equals("transition")) {
         if (attributes.containsKey("auto"))
            states.put(state, new TransitionAutomatically(InstanceState.valueOf(attributes.get("to").toUpperCase())));
         else
            states.put(
                  state,
                  new TransitionOnAction(InstanceAction.fromValue(attributes.get("action")), InstanceState
                        .valueOf(attributes.get("to").toUpperCase())));

      }
   }

}
