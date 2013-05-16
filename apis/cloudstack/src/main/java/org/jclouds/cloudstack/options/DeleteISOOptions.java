package org.jclouds.cloudstack.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options for the ISO deleteISO method.
 *
 * @see org.jclouds.cloudstack.features.ISOClient#deleteISO
 * @see org.jclouds.cloudstack.features.ISOAsyncClient#deleteISO
 * @author Richard Downer
 */
public class DeleteISOOptions extends BaseHttpRequestOptions {

   public static final DeleteISOOptions NONE = new DeleteISOOptions();

   /**
    * @param zoneId the ID of the zone of the ISO file. If not specified, the ISO will be deleted from all the zones
    */
   public DeleteISOOptions zoneId(String zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param zoneId the ID of the zone of the ISO file. If not specified, the ISO will be deleted from all the zones
       */
      public static DeleteISOOptions zoneId(String zoneId) {
         return new DeleteISOOptions().zoneId(zoneId);
      }
   }

}
