package org.jclouds.azure.storage.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options used to control paginated results (aka list commands).
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179466.aspx" />
 * @author Adrian Cole
 */
public class ListOptions extends BaseHttpRequestOptions {
   public static final ListOptions NONE = new ListOptions();

   /**
    * Filters the results to return only objects whose name begins with the specified prefix.
    */
   public ListOptions prefix(String prefix) {
      this.queryParameters.put("prefix", checkNotNull(prefix, "prefix"));
      return this;
   }

   /**
    * A string value that identifies the portion of the list to be returned with the next list
    * operation. The operation returns a marker value within the response body if the list returned
    * was not complete. The marker value may then be used in a subsequent call to request the next
    * set of list items.
    * <p/>
    * The marker value is opaque to the client.
    */
   public ListOptions marker(String marker) {
      this.queryParameters.put("marker", checkNotNull(marker, "marker"));
      return this;
   }

   /**
    * Specifies the maximum number of containers to return. If maxresults is not specified, the
    * server will return up to 5,000 items. If the parameter is set to a value greater than 5,000,
    * the server will return a Bad Request (400) error
    */
   public ListOptions maxResults(int maxresults) {
      checkState(maxresults >= 0, "maxresults must be >= 0");
      checkState(maxresults <= 10000, "maxresults must be <= 5000");
      queryParameters.put("maxresults", Integer.toString(maxresults));
      return this;
   }

   public static class Builder {

      /**
       * @see ListOptions#prefix(String)
       */
      public static ListOptions prefix(String prefix) {
         ListOptions options = new ListOptions();
         return options.prefix(prefix);
      }

      /**
       * @see ListOptions#marker(String)
       */
      public static ListOptions marker(String marker) {
         ListOptions options = new ListOptions();
         return options.marker(marker);
      }

      /**
       * @see ListOptions#maxResults(long)
       */
      public static ListOptions maxResults(int maxKeys) {
         ListOptions options = new ListOptions();
         return options.maxResults(maxKeys);
      }

   }
}
