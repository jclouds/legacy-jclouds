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
package org.jclouds.glesys.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_createaccount" />
 */
public class EmailCreateOptions extends BaseHttpRequestOptions {
   public static class Builder {
      /**
       * @see EmailCreateOptions#antispamLevel
       */
      public static EmailCreateOptions antispamLevel(int antispamLevel) {
         return new EmailCreateOptions().antispamLevel(antispamLevel);
      }

      /**
       * @see EmailCreateOptions#antiVirus
       */
      public static EmailCreateOptions antiVirus(boolean antiVirus) {
         return new EmailCreateOptions().antiVirus(antiVirus);
      }

      /**
       * @see EmailCreateOptions#autorespond
       */
      public static EmailCreateOptions autorespond(boolean autorespond) {
         return new EmailCreateOptions().autorespond(autorespond);
      }

      /**
       * @see EmailCreateOptions#autorespondSaveEmail
       */
      public static EmailCreateOptions autorespondSaveEmail(boolean autorespondSaveEmail) {
         return new EmailCreateOptions().autorespondSaveEmail(autorespondSaveEmail);
      }

      /**
       * @see EmailCreateOptions#autorespondMessage
       */
      public static EmailCreateOptions autorespondMessage(String autorespondMessage) {
         return new EmailCreateOptions().autorespondMessage(autorespondMessage);
      }
   }

   /** Configure the antispam level of the account */
   public EmailCreateOptions antispamLevel(int antispamLevel) {
      formParameters.put("antispamlevel", Integer.toString(antispamLevel));
      return this;
   }

   /** Enable or disable virus checking */
   public EmailCreateOptions antiVirus(boolean antiVirus) {
      formParameters.put("antivirus", Integer.toString(antiVirus ? 1 : 0));
      return this;
   }

   /** Enable or disable auto-respond */
   public EmailCreateOptions autorespond(boolean autorespond) {
      formParameters.put("autorespond", Integer.toString(autorespond ? 1 : 0));
      return this;
   }

   /** Enable or disable saving of auto-respond e-mails */
   public EmailCreateOptions autorespondSaveEmail(boolean autorespondSaveEmail) {
      formParameters.put("autorespondsaveemail", Integer.toString(autorespondSaveEmail ? 1 : 0));
      return this;
   }

   /** Configure the auto-respond message */
   public EmailCreateOptions autorespondMessage(String autorespondMessage) {
      formParameters.put("autorespondmessage", autorespondMessage);
      return this;
   }
}
