package org.jclouds.gogrid.functions;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.internal.GenericResponseContainer;
import org.jclouds.http.functions.ParseJson;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

/**
 * @author Oleksiy Yarmula
 */
public class ParseServerFromJsonResponse extends ParseJson<Server> {

    @Inject
    public ParseServerFromJsonResponse(Gson gson) {
        super(gson);
    }

    public Server apply(InputStream stream) {
        Type setType = new TypeToken<GenericResponseContainer<Server>>() {
        }.getType();
        GenericResponseContainer<Server> response;
        try {
            response = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("jclouds requires UTF-8 encoding", e);
        }
        return Iterables.getOnlyElement(response.getList());
    }
}
