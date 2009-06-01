package org.jclouds.aws.ec2.commands.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.AWS_ACCESS_KEY_ID;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.EXPIRES;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.SIGNATURE;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.SIGNATURE_METHOD;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.SIGNATURE_VERSION;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.TIMESTAMP;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.VERSION;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jclouds.aws.ec2.reference.CommonEC2Parameters;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.aws.util.DateService;
import org.jclouds.http.HttpHeaders;
import org.jclouds.http.options.BaseHttpRequestOptions;
import org.joda.time.DateTime;

/**
 * Contains the base options needed for all EC2 QUERY API operations.<h2>
 * Extend this class in the following way to avoid massive boilerplate code: Usage:
 * <p/>
 * 
 * <pre>
 * public static class MyRequestOptions extends BaseEC2RequestOptions&lt;MyRequestOptions&gt; {
 *    static {
 *       realClass = MyRequestOptions.class;
 *    }
 * 
 *    &#064;Override
 *    public String getAction() {
 *       return &quot;MyRequest&quot;;
 *    }
 * 
 *    public String getId() {
 *       return parameters.get(&quot;id&quot;);
 *    }
 * 
 *    public MyRequestOptions withId(String id) {
 *       encodeAndReplaceParameter(&quot;id&quot;, id);
 *       return this;
 *    }
 * 
 *    public static class Builder extends BaseEC2RequestOptions.Builder {
 *       public static MyRequestOptions withId(String id) {
 *          MyRequestOptions options = new MyRequestOptions();
 *          return options.withId(id);
 *       }
 *    }
 * }
 * </pre>
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/Query-Common-Parameters.html"
 *      />
 * @see CommonEC2Parameters
 * @author Adrian Cole
 * 
 */
public abstract class BaseEC2RequestOptions<T extends EC2RequestOptions> extends
         BaseHttpRequestOptions implements EC2RequestOptions {

   public static String[] mandatoryParametersForSignature = new String[] { ACTION,
            SIGNATURE_METHOD, SIGNATURE_VERSION, VERSION };

   static final DateService dateService = new DateService();

   private String awsSecretAccessKey;

   private String awsAccessKeyId;

   /**
    * {@inheritDoc}
    * 
    * @see CommonEC2Parameters#ACTION
    */
   public abstract String getAction();

   protected static Class<? extends EC2RequestOptions> realClass = BaseEC2RequestOptions.class;

   /**
    * Sets the request property <code>Action</code> to the appropriate name
    * 
    */
   public BaseEC2RequestOptions() {
      try {
         parameters.put(ACTION, URLEncoder.encode(getAction(), "UTF-8"));
         parameters.put(SIGNATURE_METHOD, URLEncoder.encode("HmacSHA256", "UTF-8"));
         parameters.put(SIGNATURE_VERSION, URLEncoder.encode("2", "UTF-8"));
         parameters.put(VERSION, URLEncoder.encode("2009-04-04", "UTF-8"));
      } catch (UnsupportedEncodingException e) {
         assert false : e.toString();
         // job of unit test to ensure this never happens
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see CommonEC2Parameters#TIMESTAMP
    */
   @SuppressWarnings("unchecked")
   public T timeStamp() {
      encodeAndReplaceParameter(TIMESTAMP, dateService.iso8601DateFormat());
      return (T) this;
   }

   /**
    * {@inheritDoc}
    * 
    * @see CommonEC2Parameters#EXPIRES
    */
   @SuppressWarnings("unchecked")
   public T expireAt(DateTime time) {
      try {
         parameters.put(EXPIRES, URLEncoder.encode(dateService.iso8601DateFormat(checkNotNull(time,
                  "time")), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
         throw new IllegalArgumentException("bad encoding on datetime: " + time, e);
      }
      return (T) this;
   }

   @Override
   public String buildQueryString() {
      checkState(awsSecretAccessKey != null,
               "request is not ready to sign; awsSecretAccessKey not present");
      checkState(awsAccessKeyId != null, "request is not ready to sign; awsAccessKeyId not present");
      String host = getFirstHeaderOrNull(HttpHeaders.HOST);
      checkState(host != null, "request is not ready to sign; host not present");
      // timestamp is incompatible with expires
      if (parameters.get(EXPIRES) == null) {
         timeStamp();
      }
      for (String parameter : mandatoryParametersForSignature) {
         checkState(parameters.get(parameter) != null, "parameter " + parameter
                  + " is required for signature");
      }
      parameters.remove(SIGNATURE);
      encodeAndReplaceParameter(AWS_ACCESS_KEY_ID, awsAccessKeyId);

      // 1. Sort the UTF-8 query string components by parameter name with natural byte ordering.
      // -- as parameters are a SortedSet, they are already sorted.
      // 2. URL encode the parameter name and values according to the following rules...
      // -- all parameters are URL encoded on the way in
      // 3. Separate the encoded parameter names from their encoded values with the equals sign,
      // even if the parameter value is empty.
      // -- we do not allow null values.
      // 4. Separate the name-value pairs with an ampersand.
      // -- buildQueryString() does this.
      StringBuilder toSign = new StringBuilder();
      toSign.append("GET").append("\n").append(host.toLowerCase()).append("\n").append("/").append(
               "\n");
      String canonicalizedQueryString = super.buildQueryString().replaceFirst("\\?", "");
      toSign.append(canonicalizedQueryString);
      String signature;
      try {
         signature = AWSUtils.hmacSha256Base64(toSign.toString(), awsSecretAccessKey.getBytes());
         encodeAndReplaceParameter(SIGNATURE, signature);
         return super.buildQueryString();
      } catch (Exception e) {
         throw new RuntimeException("error signing request [" + toSign.toString() + "]");
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see CommonEC2Parameters#AWS_ACCESS_KEY_ID
    */
   @SuppressWarnings("unchecked")
   public T signWith(String awsAccessKeyId, String awsSecretAccessKey) {
      this.awsAccessKeyId = checkNotNull(awsAccessKeyId, "awsAccessKeyId");
      this.awsSecretAccessKey = checkNotNull(awsSecretAccessKey, "awsSecretAccessKey");
      return (T) this;
   }

   /**
    * {@inheritDoc}
    * 
    * @see HttpHeaders#HOST
    */
   @SuppressWarnings("unchecked")
   public T usingHost(String hostname) {
      this.replaceHeader(HttpHeaders.HOST, hostname);
      return (T) this;
   }

   /**
    * The types here are parameterized in effort to return the proper type of the subclass
    * 
    * @author Adrian Cole
    */
   public static abstract class Builder {

      /**
       * @see BaseEC2RequestOptions#expireAt(DateTime)
       */
      @SuppressWarnings("unchecked")
      public static <T extends EC2RequestOptions> T expireAt(DateTime time) {
         T options;
         try {
            options = (T) realClass.newInstance();
         } catch (Exception e) {
            throw new Error("incorrect configuration, class: " + realClass
                     + " should extend BaseEC2RequestOptions", e);
         }
         return (T) options.expireAt(time);
      }

      /**
       * @see BaseEC2RequestOptions#usingHost(String)
       */
      @SuppressWarnings("unchecked")
      public static <T extends EC2RequestOptions> T usingHost(String hostname) {
         T options;
         try {
            options = (T) realClass.newInstance();
         } catch (Exception e) {
            throw new Error("incorrect configuration, class: " + realClass
                     + " should extend BaseEC2RequestOptions", e);
         }
         return (T) options.usingHost(hostname);
      }

      /**
       * @see BaseEC2RequestOptions#signWith(String,String)
       */
      @SuppressWarnings("unchecked")
      public static <T extends EC2RequestOptions> T signWith(String awsAccessKeyId,
               String awsSecretAccessKey) {
         T options;
         try {
            options = (T) realClass.newInstance();
         } catch (Exception e) {
            throw new Error("incorrect configuration, class: " + realClass
                     + " should extend BaseEC2RequestOptions", e);
         }
         return (T) options.signWith(awsAccessKeyId, awsSecretAccessKey);
      }

   }
}
