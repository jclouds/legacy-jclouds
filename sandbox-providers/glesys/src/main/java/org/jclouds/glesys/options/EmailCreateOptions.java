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
      public static EmailCreateOptions autorespondMessage(boolean autorespondMessage) {
         return new EmailCreateOptions().autorespondMessage(autorespondMessage);
      }
   }

   public EmailCreateOptions antispamLevel(int antispamLevel) {
      formParameters.put("antispamlevel", Integer.toString(antispamLevel));
      return this;
   }

   public EmailCreateOptions antiVirus(boolean antiVirus) {
      formParameters.put("antivirus", Integer.toString(antiVirus ? 1 : 0));
      return this;
   }

   public EmailCreateOptions autorespond(boolean autorespond) {
      formParameters.put("autorespond", Integer.toString(autorespond ? 1 : 0));
      return this;
   }

   public EmailCreateOptions autorespondSaveEmail(boolean autorespondSaveEmail) {
      formParameters.put("autorespondsaveemail", Integer.toString(autorespondSaveEmail ? 1 : 0));
      return this;
   }

   public EmailCreateOptions autorespondMessage(boolean autorespondMessage) {
      formParameters.put("autorespondmessage", Integer.toString(autorespondMessage ? 1 : 0));
      return this;
   }
}
