//  This software code is made available "AS IS" without warranties of any
//  kind.  You may copy, display, modify and redistribute the software
//  code either by itself or as incorporated into your code; provided that
//  you do not remove any proprietary notices.  Your use of this software
//  code is at your own risk and you waive any claim against Amazon
//  Digital Services, Inc. or its affiliates with respect to your use of
//  this software code. (c) 2006-2007 Amazon Digital Services, Inc. or its
//  affiliates.

import java.util.Map;
import java.util.TreeMap;
import java.util.Arrays;

import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.CallingFormat;
import com.amazon.s3.QueryStringAuthGenerator;
import com.amazon.s3.S3Object;

public class S3Driver {

    static final String awsAccessKeyId = "<INSERT YOUR AWS ACCESS KEY ID HERE>";
    static final String awsSecretAccessKey = "<INSERT YOUR AWS SECRET ACCESS KEY HERE>";
    
    
    // convert the bucket to lowercase for vanity domains
    // the bucket name must be lowercase since DNS is case-insensitive
    static final String bucketName = awsAccessKeyId.toLowerCase() + "-test-bucket";
    static final String keyName = "test-key";
    static final String copiedKeyName = "copy-of-" + keyName;

    public static void main(String args[]) throws Exception {
        if (awsAccessKeyId.startsWith("<INSERT")) {
            System.err.println("Please examine S3Driver.java and update it with your credentials");
            System.exit(-1);
        }

        AWSAuthConnection conn =
            new AWSAuthConnection(awsAccessKeyId, awsSecretAccessKey);
        QueryStringAuthGenerator generator =
            new QueryStringAuthGenerator(awsAccessKeyId, awsSecretAccessKey);

        // Check if the bucket exists.  The high availability engineering of 
        // Amazon S3 is focused on get, put, list, and delete operations. 
        // Because bucket operations work against a centralized, global
        // resource space, it is not appropriate to make bucket create or
        // delete calls on the high availability code path of your application.
        // It is better to create or delete buckets in a separate initialization
        // or setup routine that you run less often.
        if (!conn.checkBucketExists(bucketName))
        {
            System.out.println("----- creating bucket -----");
            System.out.println(conn.createBucket(bucketName, AWSAuthConnection.LOCATION_DEFAULT, null).connection.getResponseMessage());
            // sample creating an EU located bucket.
            // (note path-style urls will not work with location-constrained buckets)
            //System.out.println(conn.createBucket(bucketName, AWSAuthConnection.LOCATION_EU, null).connection.getResponseMessage());
        }

        System.out.println("----- listing bucket -----");
        System.out.println(conn.listBucket(bucketName, null, null, null, null).entries);

        System.out.println("----- bucket location -----");
        System.out.println(conn.getBucketLocation(bucketName).getLocation());

        System.out.println("----- putting object -----");
        S3Object object = new S3Object("this is a test".getBytes(), null);
        Map headers = new TreeMap();
        headers.put("Content-Type", Arrays.asList(new String[] { "text/plain" }));
        System.out.println(
                conn.put(bucketName, keyName, object, headers).connection.getResponseMessage()
            );

        System.out.println("----- copying object -----");
        // Straight Copy; destination key will be private.
        conn.copy( bucketName, keyName, bucketName, copiedKeyName, null );
        {
            // Update the metadata; destination key will be private.
            Map updateMetadata = new TreeMap();
            updateMetadata.put("metadata-key", Arrays.asList("this will be the metadata in the copied key"));
            conn.copy( bucketName, copiedKeyName, bucketName, copiedKeyName, updateMetadata, null );
        }

        System.out.println("----- listing bucket -----");
        System.out.println(conn.listBucket(bucketName, null, null, null, null).entries);

        System.out.println("----- getting object -----");
        System.out.println(
                new String(conn.get(bucketName, keyName, null).object.data)
            );

        System.out.println("----- query string auth example -----");
        generator.setExpiresIn(60 * 1000);

        System.out.println("Try this url in your web browser (it will only work for 60 seconds)\n");
        System.out.println(generator.get(bucketName, keyName, null));
        System.out.print("\npress enter> ");
        System.in.read();

        System.out.println("\nNow try just the url without the query string arguments.  It should fail.\n");
        System.out.println(generator.makeBareURL(bucketName, keyName));
        System.out.print("\npress enter> ");
        System.in.read();

        System.out.println("----- putting object with metadata and public read acl -----");

        Map metadata = new TreeMap();
        metadata.put("blah", Arrays.asList(new String[] { "foo" }));
        object = new S3Object("this is a publicly readable test".getBytes(), metadata);

        headers = new TreeMap();
        headers.put("x-amz-acl", Arrays.asList(new String[] { "public-read" }));
        headers.put("Content-Type", Arrays.asList(new String[] { "text/plain" }));

        System.out.println(
                conn.put(bucketName, keyName + "-public", object, headers).connection.getResponseMessage()
            );

        System.out.println("----- anonymous read test -----");
        System.out.println("\nYou should be able to try this in your browser\n");
        System.out.println(generator.makeBareURL(bucketName, keyName + "-public"));
        System.out.print("\npress enter> ");
        System.in.read();
        
        System.out.println("----- path style url example -----");
        System.out.println("\nNon-location-constrained buckets can also be specified as part of the url path.  (This was the original url style supported by S3.)");
        System.out.println("\nTry this url out in your browser (it will only be valid for 60 seconds)\n");
        generator.setCallingFormat(CallingFormat.getPathCallingFormat());
        // could also have been done like this:
        //  generator = new QueryStringAuthGenerator(awsAccessKeyId, awsSecretAccessKey, true, Utils.DEFAULT_HOST, CallingFormat.getPathCallingFormat());
        generator.setExpiresIn(60 * 1000);
        System.out.println(generator.get(bucketName, keyName, null));
        System.out.print("\npress enter> ");
        System.in.read();

        System.out.println("----- getting object's acl -----");
        System.out.println(new String(conn.getACL(bucketName, keyName, null).object.data));

        System.out.println("----- deleting objects -----");
        System.out.println(
                conn.delete(bucketName, copiedKeyName, null).connection.getResponseMessage()
            );
        System.out.println(
                conn.delete(bucketName, keyName, null).connection.getResponseMessage()
            );
        System.out.println(
                conn.delete(bucketName, keyName + "-public", null).connection.getResponseMessage()
            );

        System.out.println("----- listing bucket -----");
        System.out.println(conn.listBucket(bucketName, null, null, null, null).entries);

        System.out.println("----- listing all my buckets -----");
        System.out.println(conn.listAllMyBuckets(null).entries);

        System.out.println("----- deleting bucket -----");
        System.out.println(
                conn.deleteBucket(bucketName, null).connection.getResponseMessage()
            );
    }
}
