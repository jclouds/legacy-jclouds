package org.jclouds.samples.googleappengine.functions;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Bucket.Metadata;
import org.jclouds.logging.Logger;
import org.jclouds.samples.googleappengine.domain.BucketResult;

import com.google.common.base.Function;
import com.google.inject.Inject;

public class MetadataToBucketResult implements Function<S3Bucket.Metadata, BucketResult> {
   private final S3Connection connection;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public MetadataToBucketResult(S3Connection connection) {
      this.connection = connection;
   }

   public BucketResult apply(Metadata from) {
      BucketResult result = new BucketResult();
      result.setName(from.getName());
      try {
         S3Bucket bucket = connection.listBucket(from.getName()).get(10, TimeUnit.SECONDS);
         if (bucket == S3Bucket.NOT_FOUND) {
            result.setStatus("not found");
         } else {
            result.setSize(bucket.getSize() + "");
         }
      } catch (Exception e) {
         logger.error(e, "Error listing bucket %1$s", result.getName());
         result.setStatus(e.getMessage());
      }
      return result;
   }
}