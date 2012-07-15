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
package org.jclouds.elb.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.elb.domain.Listener;
import org.jclouds.http.HttpRequest;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Binds the listeners request to the http request
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_CreateLoadBalancer.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class BindListenersToFormParams implements org.jclouds.rest.Binder {

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      Iterable<Listener> listeners = checkNotNull(input, "listeners must be set!") instanceof Listener ? ImmutableSet
              .of(Listener.class.cast(input)) : (Iterable<Listener>) input;

      ImmutableMultimap.Builder<String, String> formParameters = ImmutableMultimap.builder();
      int listenerIndex = 1;

      for (Listener listener : listeners) {
         formParameters.put("Listeners.member." + listenerIndex + ".LoadBalancerPort", listener.getPort() + "");
         formParameters.put("Listeners.member." + listenerIndex + ".InstancePort", listener.getInstancePort() + "");
         formParameters.put("Listeners.member." + listenerIndex + ".Protocol", listener.getProtocol() + "");
         formParameters.put("Listeners.member." + listenerIndex + ".InstanceProtocol", listener.getInstanceProtocol()
                  + "");
         if (listener.getSSLCertificateId().isPresent())
            formParameters.put("Listeners.member." + listenerIndex + ".SSLCertificateId", listener
                     .getSSLCertificateId().get() + "");
         listenerIndex++;
      }

      return (R) request.toBuilder().replaceFormParams(formParameters.build()).build();

   }

}
