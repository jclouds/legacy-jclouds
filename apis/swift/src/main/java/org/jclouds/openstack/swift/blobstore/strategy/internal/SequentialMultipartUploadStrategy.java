package org.jclouds.openstack.swift.blobstore.strategy.internal;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.swift.blobstore.SwiftBlobStore;

import static com.google.common.base.Preconditions.checkNotNull;


public class SequentialMultipartUploadStrategy implements MultipartUploadStrategy {
   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final SwiftBlobStore ablobstore;
   protected final PayloadSlicer slicer;

    public SequentialMultipartUploadStrategy(SwiftBlobStore ablobstore, PayloadSlicer slicer) {
        this.ablobstore = checkNotNull(ablobstore, "ablobstore");
        this.slicer = checkNotNull(slicer, "slicer");
    }

    @Override
    public String execute(String container, Blob blob, PutOptions options) {
        String key = blob.getMetadata().getName();
        Payload payload = blob.getPayload();
        /*MultipartUploadSlicingAlgorithm algorithm = new MultipartUploadSlicingAlgorithm();
        algorithm
                .calculateChunkSize(checkNotNull(
                        payload.getContentMetadata().getContentLength(),
                        "contentLength required on all uploads to amazon s3; please invoke payload.getContentMetadata().setContentLength(length) first"));
        int parts = algorithm.getParts();
        long chunkSize = algorithm.getChunkSize();
        if (parts > 0) {

        } */
        return "NOT IMPLEMENTED";
    }
}
