import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HTTPStuff {

    public static String send(String uri, List<BasicNameValuePair> postParams) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(uri);

        // Request parameters and other properties
        List<NameValuePair> params = new ArrayList<>(postParams.size());
        params.addAll(postParams);
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream inputStream = entity.getContent()) {
                String newLine = System.getProperty("line.separator");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    if (result.length() > 0) {
                        result.append(newLine);
                    }
                    result.append(line);
                }
                return result.toString();
            }
        }
        return "";
    }
}
