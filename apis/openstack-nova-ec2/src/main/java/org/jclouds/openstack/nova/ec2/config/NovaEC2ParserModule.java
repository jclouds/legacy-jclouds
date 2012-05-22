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
package org.jclouds.openstack.nova.ec2.config;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;

import com.google.common.base.Objects;
import com.google.inject.AbstractModule;

/**
 * @author Adam Lowe
 */
public class NovaEC2ParserModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(DateService.class).to(Iso8601WithDashesTreatedAsNullDateService.class);
   }

   @Singleton
   public static class Iso8601WithDashesTreatedAsNullDateService implements DateService {
      DateService delegate;
      
      @Inject
      public Iso8601WithDashesTreatedAsNullDateService(SimpleDateFormatDateService service) {
         this.delegate = service;
      }
      
      @Override
      public Date fromSeconds(long seconds) {
         return delegate.fromSeconds(seconds);
      }

      @Override
      public String cDateFormat(Date date) {
         return delegate.cDateFormat(date);
      }

      @Override
      public String cDateFormat() {
         return delegate.cDateFormat();
      }

      @Override
      public Date cDateParse(String toParse) {
         return delegate.cDateParse(toParse);
      }

      @Override
      public String rfc822DateFormat(Date date) {
         return delegate.rfc822DateFormat(date);
      }

      @Override
      public String rfc822DateFormat() {
         return delegate.rfc822DateFormat();
      }

      @Override
      public Date rfc822DateParse(String toParse) {
         return delegate.rfc822DateParse(toParse);
      }

      @Override
      public String iso8601SecondsDateFormat(Date dateTime) {
         return delegate.iso8601SecondsDateFormat(dateTime);
      }

      @Override
      public String iso8601SecondsDateFormat() {
         return delegate.iso8601SecondsDateFormat();
      }

      @Override
      public String iso8601DateFormat(Date date) {
         return delegate.iso8601DateFormat(date);
      }

      @Override
      public String iso8601DateFormat() {
         return delegate.iso8601DateFormat();
      }

      @Override
      public Date iso8601DateParse(String toParse) {
         if (Objects.equal("-", toParse)) return null;
         return delegate.iso8601DateParse(toParse);
      }

      @Override
      public Date iso8601SecondsDateParse(String toParse) {
         if (Objects.equal("-", toParse)) return null;
         return delegate.iso8601SecondsDateParse(toParse);
      }
      
      @Override
      public String rfc1123DateFormat(Date date) {
         return delegate.rfc1123DateFormat(date);
      }

      @Override
      public String rfc1123DateFormat() {
         return delegate.rfc1123DateFormat();
      }

      @Override
      public Date rfc1123DateParse(String toParse) {
         return delegate.rfc1123DateParse(toParse);
      }
   }
   
}
