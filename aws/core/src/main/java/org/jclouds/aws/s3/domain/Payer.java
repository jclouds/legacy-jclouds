package org.jclouds.aws.s3.domain;

import com.google.common.base.CaseFormat;

/**
 * Specifies who pays for the download and request fees.
 * <p/>
 * In general, bucket owners pay for all Amazon S3 storage and data transfer costs associated with
 * their bucket. A bucket owner, however, can configure a bucket to be a Requester Pays bucket. With
 * Requester Pays buckets, the requester instead of the bucket owner pays the cost of the request
 * and the data download from the bucket. The bucket owner always pays the cost of storing data.
 * <p/>
 * Typically, you configure buckets to be Requester Pays when you want to share data but not incur
 * charges associated with others accessing the data. You might, for example, use Requester Pays
 * buckets when making available large data sets, such as zip code directories, reference data,
 * geospatial information, or web crawling data.
 * <h3>Important</h3> If you enable Requester Pays on a bucket, anonymous access to that bucket is
 * not allowed.
 * <p/>
 * You must authenticate all requests involving Requester Pays buckets. The request authentication
 * enables Amazon S3 to identify and charge the requester for their use of the Requester Pays
 * bucket.
 * <p/>
 * After you configure a bucket to be a Requester Pays bucket, requesters must include
 * x-amz-request-payer in their requests either in the header, for POST and GET requests, or as a
 * parameter in a REST request to show that they understand that they will be charged for the
 * request and the data download.
 * <p/>
 * Requester Pays buckets do not support the following.
 * <ul>
 * <li>Anonymous requests</li>
 * <li>BitTorrent</li>
 * <li>SOAP requests</li>
 * </ul>
 * 
 * You cannot use a Requester Pays bucket as the target bucket for end user logging, or vice versa.
 * However, you can turn on end user logging on a Requester Pays bucket where the target bucket is a
 * non Requester Pays bucket.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AmazonS3/latest/index.html?RESTrequestPaymentGET.html" />
 */
public enum Payer {
   REQUESTER, BUCKET_OWNER;

   public String value() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
   }

   public static Payer fromValue(String payer) {
      return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, payer));
   }
}
