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
package org.jclouds.vcloud.director.v1_5.parsers;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.domain.SessionWithToken;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SessionWithTokenFromXMLAndHeader implements Function<HttpResponse, SessionWithToken> {
   @Resource
   protected Logger logger = Logger.NULL;
   private ParseXMLWithJAXB<Session> sessionParser;

   @Inject
   public SessionWithTokenFromXMLAndHeader(ParseXMLWithJAXB<Session> sessionParser) {
      this.sessionParser = sessionParser;
   }

   @Override
   public SessionWithToken apply(final HttpResponse from) {
      Session session = sessionParser.apply(from);
      return SessionWithToken.builder().session(session).token(from.getFirstHeaderOrNull("x-vcloud-authorization"))
               .build();
   }
}
