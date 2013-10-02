/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.glesys.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_createaccount" />
 */
public class CreateAccountOptions extends BaseHttpRequestOptions {
   public static class Builder {
      /**
       * @see CreateAccountOptions#antispamLevel
       */
      public static CreateAccountOptions antispamLevel(int antispamLevel) {
         return new CreateAccountOptions().antispamLevel(antispamLevel);
      }

      /**
       * @see CreateAccountOptions#antiVirus
       */
      public static CreateAccountOptions antiVirus(boolean antiVirus) {
         return new CreateAccountOptions().antiVirus(antiVirus);
      }

      /**
       * @see CreateAccountOptions#autorespond
       */
      public static CreateAccountOptions autorespond(boolean autorespond) {
         return new CreateAccountOptions().autorespond(autorespond);
      }

      /**
       * @see CreateAccountOptions#autorespondSaveEmail
       */
      public static CreateAccountOptions autorespondSaveEmail(boolean autorespondSaveEmail) {
         return new CreateAccountOptions().autorespondSaveEmail(autorespondSaveEmail);
      }

      /**
       * @see CreateAccountOptions#autorespondMessage
       */
      public static CreateAccountOptions autorespondMessage(String autorespondMessage) {
         return new CreateAccountOptions().autorespondMessage(autorespondMessage);
      }
   }

   /** Configure the antispam level of the account */
   public CreateAccountOptions antispamLevel(int antispamLevel) {
      formParameters.put("antispamlevel", Integer.toString(antispamLevel));
      return this;
   }

   /** Enable or disable virus checking */
   public CreateAccountOptions antiVirus(boolean antiVirus) {
      formParameters.put("antivirus", Boolean.toString(antiVirus));
      return this;
   }

   /** Enable or disable auto-respond */
   public CreateAccountOptions autorespond(boolean autorespond) {
      formParameters.put("autorespond", Boolean.toString(autorespond));
      return this;
   }

   /** Enable or disable saving of auto-respond e-mails */
   public CreateAccountOptions autorespondSaveEmail(boolean autorespondSaveEmail) {
      formParameters.put("autorespondsaveemail", Boolean.toString(autorespondSaveEmail));
      return this;
   }

   /** Configure the auto-respond message */
   public CreateAccountOptions autorespondMessage(String autorespondMessage) {
      formParameters.put("autorespondmessage", autorespondMessage);
      return this;
   }
}
