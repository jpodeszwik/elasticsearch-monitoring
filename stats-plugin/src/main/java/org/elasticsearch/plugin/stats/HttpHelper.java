package org.elasticsearch.plugin.stats;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: Jakub Podeszwik
 * Date: 09/08/14
 */
public class HttpHelper {
    private ESLogger logger = ESLoggerFactory.getLogger("plugin.stats.HttpHelper");

    public void postString(String url, String data) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpPost request = new HttpPost(url);
            request.setEntity(new StringEntity(data));
            HttpResponse response = httpClient.execute(request);
            logger.debug("postString status code: " + response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            logger.error("postString problem with sending http request");
            throw e;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    public JSONObject getJsonFromUrl(String url) throws IOException {
        String response = performHttpRequest(url);
        return new JSONObject(response);
    }

    private String readContentFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    private String performHttpRequest(String url) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);

            logger.debug("performHttpRequest status code: " + response.getStatusLine().getStatusCode());

            InputStream inputStream = response.getEntity().getContent();
            return readContentFromInputStream(inputStream);
        } catch (IOException e) {
            logger.error("performHttpRequest Problem with sending http request", e);
            throw e;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
}
