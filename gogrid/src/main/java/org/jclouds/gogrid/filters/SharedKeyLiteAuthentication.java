package org.jclouds.gogrid.filters;

import com.google.common.base.Throwables;
import org.jclouds.Constants;
import org.jclouds.date.TimeStamp;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.gogrid.reference.GoGridConstants;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.logging.Logger;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * @author Oleksiy Yarmula
 */
public class SharedKeyLiteAuthentication  implements HttpRequestFilter {

    private final String apiKey;
    private final String secret;
    private final Long timeStamp;
    private final EncryptionService encryptionService;
    @Resource
    @Named(Constants.LOGGER_SIGNATURE)
    Logger signatureLog = Logger.NULL;

    @Inject
    public SharedKeyLiteAuthentication(@Named(GoGridConstants.PROPERTY_GOGRID_USER) String apiKey,
                                       @Named(GoGridConstants.PROPERTY_GOGRID_PASSWORD) String secret,
                                       @TimeStamp Long timeStamp,
                                       EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
        this.apiKey = apiKey;
        this.secret = secret;
        this.timeStamp = timeStamp;
    }

    public void filter(HttpRequest request) {
        checkArgument(checkNotNull(request, "input") instanceof GeneratedHttpRequest<?>,
               "this decorator is only valid for GeneratedHttpRequests!");
        GeneratedHttpRequest<?> generatedRequest = (GeneratedHttpRequest<?>) request;

        String toSign = createStringToSign();
        String signatureMd5 = getMd5For(toSign);

        generatedRequest.addQueryParam("sig", signatureMd5);
        generatedRequest.addQueryParam("api_key", apiKey);

        HttpUtils.logRequest(signatureLog, request, "<<");
    }

    private String createStringToSign() {
        return format("%s%s%s", apiKey, secret, timeStamp);
    }

    private String getMd5For(String stringToHash) {
        try {
            return encryptionService.md5Hex(stringToHash.getBytes());
        } catch(Exception e) {
            throw Throwables.propagate(e);
        }

    }

}
