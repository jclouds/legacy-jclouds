package org.jclouds.openstack.swift.blobstore.strategy.internal;

import javax.annotation.Resource;
import javax.inject.Named;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.swift.SwiftClient;
import org.jclouds.openstack.swift.blobstore.SwiftBlobStore;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.util.Throwables2;

import java.util.SortedMap;

import static com.google.common.base.Preconditions.checkNotNull;


public class SequentialMultipartUploadStrategy implements MultipartUploadStrategy {
    public static final String PART_SEPARATOR = "/";

    @Resource
    @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
    protected Logger logger = Logger.NULL;

    protected final SwiftBlobStore ablobstore;
    protected final PayloadSlicer slicer;
    
    @Inject
    public SequentialMultipartUploadStrategy(SwiftBlobStore ablobstore, PayloadSlicer slicer) {
        this.ablobstore = checkNotNull(ablobstore, "ablobstore");
        this.slicer = checkNotNull(slicer, "slicer");
    }

    @Override
    public String execute(String container, Blob blob, PutOptions options, BlobToObject blob2Object) {
        System.out.println("here we go");
        String key = blob.getMetadata().getName();
        Payload payload = blob.getPayload();
        MultipartUploadSlicingAlgorithm algorithm = new MultipartUploadSlicingAlgorithm();
        algorithm
                .calculateChunkSize(checkNotNull(
                        payload.getContentMetadata().getContentLength(),
                        "contentLength required on all uploads to swift; please invoke payload.getContentMetadata().setContentLength(length) first"));
        int parts = algorithm.getParts();
        long chunkSize = algorithm.getChunkSize();
        if (parts > 0) {
            SwiftClient client = (SwiftClient) ablobstore.getContext()
                    .getProviderSpecificContext().getApi();

            try {
                SortedMap<Integer, String> etags = Maps.newTreeMap();
                int part;
                while ((part = algorithm.getNextPart()) <= parts) {
                    System.out.println("Uploading part " + part);
                    Payload chunkedPart = slicer.slice(payload,
                            algorithm.getNextChunkOffset(), chunkSize);
                    Blob blobPart = ablobstore.blobBuilder(blob.getMetadata().getName() + PART_SEPARATOR +
                            String.valueOf(part)).payload(chunkedPart).contentDisposition(
                            blob.getMetadata().getName() + PART_SEPARATOR + String.valueOf(part)).build();
                    client.putObject(container, blob2Object.apply(blobPart));
                }
                long remaining = algorithm.getRemaining();
                if (remaining > 0) {
                    System.out.println("Uploading tail.");
                    Payload chunkedPart = slicer.slice(payload,
                            algorithm.getNextChunkOffset(), remaining);
                    Blob blobPart = ablobstore.blobBuilder(blob.getMetadata().getName() + PART_SEPARATOR + 
                    String.valueOf(part)).payload(chunkedPart).contentDisposition(
                            blob.getMetadata().getName() + PART_SEPARATOR + String.valueOf(part)).build();
                    client.putObject(container, blob2Object.apply(blobPart));
                }
                return client.putObjectManifest(container, key);
            } catch (Exception ex) {
                RuntimeException rtex = Throwables2.getFirstThrowableOfType(ex, RuntimeException.class);
                if (rtex == null) {
                    rtex = new RuntimeException(ex);
                }
                //client.abortMultipartUpload(container, key, uploadId);
                throw rtex;
            }

        }
        return "NOT IMPLEMENTED";
    }
}
