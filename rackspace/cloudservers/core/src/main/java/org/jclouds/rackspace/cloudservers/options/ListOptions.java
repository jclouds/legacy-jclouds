package org.jclouds.rackspace.cloudservers.options;

import org.jclouds.rackspace.options.BaseListOptions;
import org.joda.time.DateTime;

/**
 * Options used to control the amount of detail in the request.
 * 
 * @see BaseListOptions
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @author Adrian Cole
 */
public class ListOptions extends BaseListOptions {

   public static final ListOptions NONE = new ListOptions();

   /**
    * unless used, only the name and id will be returned per row.
    * 
    * @return
    */
   public ListOptions withDetails() {
      this.pathSuffix = "/detail";
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListOptions changesSince(DateTime ifModifiedSince) {
      super.changesSince(ifModifiedSince);
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListOptions maxResults(int limit) {
      super.maxResults(limit);
      return this;

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListOptions startAt(long offset) {
      super.startAt(offset);
      return this;
   }

   public static class Builder {

      /**
       * @see ListOptions#withDetails()
       */
      public static ListOptions withDetails() {
         ListOptions options = new ListOptions();
         return options.withDetails();
      }

      /**
       * @see BaseListOptions#startAt(long)
       */
      public static ListOptions startAt(long prefix) {
         ListOptions options = new ListOptions();
         return options.startAt(prefix);
      }

      /**
       * @see BaseListOptions#maxResults(long)
       */
      public static ListOptions maxResults(int maxKeys) {
         ListOptions options = new ListOptions();
         return options.maxResults(maxKeys);
      }

      /**
       * @see BaseListOptions#changesSince(DateTime)
       */
      public static ListOptions changesSince(DateTime since) {
         ListOptions options = new ListOptions();
         return options.changesSince(since);
      }

   }
}
