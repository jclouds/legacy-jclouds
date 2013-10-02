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
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Detailed information on an Email Account
 *
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_list" />
 */
public class EmailAccount {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromEmailAccount(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String account;
      protected EmailQuota quota;
      protected int antispamLevel;
      protected boolean antiVirus;
      protected boolean autoRespond;
      protected String autoRespondMessage;
      protected boolean autoRespondSaveEmail;
      protected Date created;
      protected Date modified;

      /**
       * @see EmailAccount#getAccount()
       */
      public T account(String account) {
         this.account = checkNotNull(account, "account");
         return self();
      }

      /**
       * @see EmailAccount#getQuota()
       */
      public T quota(EmailQuota quota) {
         this.quota = checkNotNull(quota, "quota");
         return self();
      }

      /**
       * @see EmailAccount#getAntispamLevel()
       */
      public T antispamLevel(int antispamLevel) {
         this.antispamLevel = antispamLevel;
         return self();
      }

      /**
       * @see EmailAccount#isAntiVirus()
       */
      public T antiVirus(boolean antiVirus) {
         this.antiVirus = antiVirus;
         return self();
      }

      /**
       * @see EmailAccount#isAutoRespond()
       */
      public T autoRespond(boolean autoRespond) {
         this.autoRespond = autoRespond;
         return self();
      }

      /**
       * @see EmailAccount#getAutoRespondMessage()
       */
      public T autoRespondMessage(String autoRespondMessage) {
         this.autoRespondMessage = checkNotNull(autoRespondMessage, "autoRespondMessage");
         return self();
      }

      /**
       * @see EmailAccount#isAutoRespondSaveEmail()
       */
      public T autoRespondSaveEmail(boolean autoRespondSaveEmail) {
         this.autoRespondSaveEmail = autoRespondSaveEmail;
         return self();
      }

      /**
       * @see EmailAccount#getCreated()
       */
      public T created(Date created) {
         this.created = checkNotNull(created, "created");
         return self();
      }

      /**
       * @see EmailAccount#getModified()
       */
      public T modified(Date modified) {
         this.modified = checkNotNull(modified, "modified");
         return self();
      }

      public EmailAccount build() {
         return new EmailAccount(account, quota, antispamLevel, new GleSYSBoolean(antiVirus), new GleSYSBoolean(autoRespond), autoRespondMessage, new GleSYSBoolean(autoRespondSaveEmail), created, modified);
      }

      public T fromEmailAccount(EmailAccount in) {
         return this.account(in.getAccount())
               .quota(in.getQuota())
               .antispamLevel(in.getAntispamLevel())
               .antiVirus(in.isAntiVirus())
               .autoRespond(in.isAutoRespond())
               .autoRespondMessage(in.getAutoRespondMessage())
               .autoRespondSaveEmail(in.isAutoRespondSaveEmail())
               .created(in.getCreated())
               .modified(in.getModified());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String account;
   private final EmailQuota quota;
   private final int antispamLevel;
   private final boolean antiVirus;
   private final boolean autoRespond;
   private final String autoRespondMessage;
   private final boolean autoRespondSaveEmail;
   private final Date created;
   private final Date modified;

   @ConstructorProperties({
         "emailaccount", "quota", "antispamlevel", "antivirus", "autorespond", "autorespondmessage", "autorespondsaveemail", "created", "modified"
   })
   protected EmailAccount(String account, EmailQuota quota, int antispamLevel,
                          GleSYSBoolean antiVirus, GleSYSBoolean autoRespond, @Nullable String autoRespondMessage,
                          GleSYSBoolean autoRespondSaveEmail, Date created, @Nullable Date modified) {
      this.account = checkNotNull(account, "account");
      this.quota = checkNotNull(quota, "quota");
      this.antispamLevel = antispamLevel;
      this.antiVirus = checkNotNull(antiVirus, "antiVirus").getValue();
      this.autoRespond = checkNotNull(autoRespond, "autoRespond").getValue();
      this.autoRespondMessage = autoRespondMessage;
      this.autoRespondSaveEmail = checkNotNull(autoRespondSaveEmail, "autoRespondSaveEmail").getValue();
      this.created = checkNotNull(created, "created");
      this.modified = modified;
   }

   /**
    * @return the e-mail address for this e-mail account
    */
   public String getAccount() {
      return this.account;
   }

   /**
    * @return the quota for this e-mail account
    */
   public EmailQuota getQuota() {
      return this.quota;
   }

   /**
    * @return the antispam level of the e-mail account
    */
   public int getAntispamLevel() {
      return this.antispamLevel;
   }

   /**
    * @return true if antivirus is enabled for this e-mail account
    */
   public boolean isAntiVirus() {
      return this.antiVirus;
   }

   /**
    * @return true if auto-respond is enabled for this e-mail account
    */
   public boolean isAutoRespond() {
      return this.autoRespond;
   }
   /**
    * @return the auto-respond message for this e-mail account
    */
   @Nullable
   public String getAutoRespondMessage() {
      return this.autoRespondMessage;
   }

   /**
    * @return true if saving is enabled for auto-respond e-mails
    */
   public boolean isAutoRespondSaveEmail() {
      return this.autoRespondSaveEmail;
   }

   /**
    * @return when this account was created
    */
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return when this account was last modified
    */
   @Nullable
   public Date getModified() {
      return this.modified;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(account);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      EmailAccount that = EmailAccount.class.cast(obj);
      return Objects.equal(this.account, that.account);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("account", account).add("quota", quota).add("antispamLevel", antispamLevel).add("antiVirus", antiVirus).add("autoRespond", autoRespond).add("autoRespondMessage", autoRespondMessage).add("autoRespondSaveEmail", autoRespondSaveEmail).add("created", created).add("modified", modified);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
