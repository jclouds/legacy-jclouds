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

package org.jclouds.elb.xml;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.date.DateService;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.domain.LoadBalancer.AppCookieStickinessPolicy;
import org.jclouds.elb.domain.LoadBalancer.LBCookieStickinessPolicy;
import org.jclouds.elb.domain.LoadBalancer.LoadBalancerListener;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

/**
 * 
 * @author Lili Nadar
 */
public class DescribeLoadBalancersResponseHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<Set<LoadBalancer>> {
   private final DateService dateService;
   private final LoadBalancerListenerHandler listenerHandler;

   @Inject
   public DescribeLoadBalancersResponseHandler(DateService dateService, LoadBalancerListenerHandler listenerHandler) {
      this.dateService = dateService;
      this.listenerHandler = listenerHandler;
   }

   @Resource
   protected Logger logger = Logger.NULL;

   private Set<LoadBalancer> contents = Sets.newLinkedHashSet();
   private StringBuilder currentText = new StringBuilder();

   private boolean inListenerDescriptions = false;
   private boolean inInstances = false;
   private boolean inAppCookieStickinessPolicies = false;
   private boolean inLBCookieStickinessPolicies = false;
   private boolean inAvailabilityZones = false;
   // TODO unused?
   private boolean inLoadBalancerDescriptions = false;

   private LoadBalancer elb;
   // TODO unused?
   private AppCookieStickinessPolicy appCookieStickinessPolicy;
   // TODO unused?
   private LBCookieStickinessPolicy lBCookieStickinessPolicy;

   public void startElement(String uri, String localName, String qName, Attributes attributes) {

      if (qName.equals("ListenerDescriptions") || inListenerDescriptions) {
         inListenerDescriptions = true;
      } else if (qName.equals("AppCookieStickinessPolicies")) {
         inAppCookieStickinessPolicies = true;
      } else if (qName.equals("LBCookieStickinessPolicies")) {
         inLBCookieStickinessPolicies = true;
      } else if (qName.equals("LoadBalancerDescriptions")) {
         inLoadBalancerDescriptions = true;
      } else if (qName.equals("Instances")) {
         inInstances = true;
      } else if (qName.equals("AvailabilityZones")) {
         inAvailabilityZones = true;
      }

      if (qName.equals("member")) {
         if (!(inListenerDescriptions || inAppCookieStickinessPolicies || inInstances || inLBCookieStickinessPolicies || inAvailabilityZones)) {
            elb = new LoadBalancer();
         }
      }
   }

   public void endElement(String uri, String localName, String qName) {
      // if end tag is one of below then set inXYZ to false
      if (qName.equals("ListenerDescriptions")) {
         inListenerDescriptions = false;
      } else if (qName.equals("AppCookieStickinessPolicies")) {
         inAppCookieStickinessPolicies = false;
      } else if (qName.equals("LBCookieStickinessPolicies")) {
         inLBCookieStickinessPolicies = false;
      } else if (qName.equals("LoadBalancerDescriptions")) {
         inLoadBalancerDescriptions = false;
      } else if (qName.equals("Instances")) {
         inInstances = false;
      } else if (qName.equals("AvailabilityZones")) {
         inAvailabilityZones = false;
      }

      if (qName.equals("DNSName")) {
         elb.setDnsName(currentText.toString().trim());
      } else if (qName.equals("LoadBalancerName")) {
         elb.setName(currentText.toString().trim());
      } else if (qName.equals("InstanceId")) {
         elb.getInstanceIds().add(currentText.toString().trim());
      }

      else if (qName.equals("member")) {

         if (inAvailabilityZones) {
            elb.getAvailabilityZones().add(currentText.toString().trim());
         } else if (!(inListenerDescriptions || inAppCookieStickinessPolicies || inInstances
               || inLBCookieStickinessPolicies || inAvailabilityZones)) {
            try {
               String region = AWSUtils.findRegionInArgsOrNull(getRequest());
               elb.setRegion(region);
               contents.add(elb);
            } catch (NullPointerException e) {
               logger.warn(e, "malformed load balancer: %s", localName);
            }

            this.elb = null;

         }

      }

      currentText = new StringBuilder();
   }

   @Override
   public Set<LoadBalancer> getResult() {
      return contents;
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   @Override
   public DescribeLoadBalancersResponseHandler setContext(HttpRequest request) {
      listenerHandler.setContext(request);
      super.setContext(request);
      return this;
   }

   public static class LoadBalancerListenerHandler extends ParseSax.HandlerWithResult<Set<LoadBalancerListener>> {
      private Set<LoadBalancerListener> listeners = Sets.newHashSet();
      private StringBuilder currentText = new StringBuilder();
      private LoadBalancerListener listener;

      public void startElement(String uri, String name, String qName, Attributes attrs) {
         if (qName.equals("member")) {
            listener = new LoadBalancerListener();
         }
      }

      public void endElement(String uri, String name, String qName) {
         if (qName.equals("Protocol")) {
            listener.setProtocol(currentText.toString().trim());
         } else if (qName.equals("LoadBalancerPort")) {
            listener.setLoadBalancerPort(Integer.parseInt(currentText.toString().trim()));
         } else if (qName.equals("InstancePort")) {
            listener.setInstancePort(Integer.parseInt(currentText.toString().trim()));
         } else if (qName.equals("member")) {
            listeners.add(listener);
         }

         currentText = new StringBuilder();

      }

      @Override
      public Set<LoadBalancerListener> getResult() {
         return listeners;
      }

      public void characters(char ch[], int start, int length) {
         currentText.append(ch, start, length);
      }

   }
}
