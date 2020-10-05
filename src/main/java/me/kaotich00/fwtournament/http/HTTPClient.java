package me.kaotich00.fwtournament.http;

import com.google.common.collect.Multimap;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Set;

public class HTTPClient {

    public static String fetchHttpRequest(String URI, String requestMethod, Multimap<String,String> postDataParams, Player sender) throws ParseException {

        String response = "";
        try {
            if(requestMethod.equals("GET")) {
                URI = URI += "?" + formatPostDataParams( postDataParams );
            }
            URL url = new URL(URI);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(150000);
            conn.setConnectTimeout(150000);
            conn.setRequestMethod(requestMethod);
            conn.setDoOutput(true);

            if(requestMethod.equals("PUT")) {
                conn.setRequestProperty("Content-Type", "multipart/form-data");
                conn.setRequestProperty("Connection", "keep-alive");

                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(formatPostDataParams(postDataParams));
                out.close();

            } else {
                conn.setDoInput(true);
            }

            if(requestMethod.equals("POST")) {
                conn.setRequestProperty("Content-Type", "multipart/form-data");
                conn.setRequestProperty("Connection", "keep-alive");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(formatPostDataParams(postDataParams));

                writer.flush();
                writer.close();
                os.close();
            }

            int responseCode = conn.getResponseCode();

            if(responseCode != 200) {
                sender.sendMessage(ChatFormatter.formatErrorMessage(formatErrorCode(responseCode)));
                return null;
            }

            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line=br.readLine()) != null) {
                response += line;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {}

        return response;
    }

    public static String formatPostDataParams(Multimap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Set<String> keys = params.keySet();
        for (String keyprint : keys) {
            Collection<String> values = params.get(keyprint);
            for(String value : values){
                if(first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(keyprint, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value, "UTF-8"));
            }
        }

        return result.toString();
    }

    public static String formatErrorCode(int errorCode) {
        String errorMessage = "";
        switch(errorCode) {
            case 401:
                errorMessage = "Unauthorized (Invalid API key or insufficient permissions)";
                break;
            case 404:
                errorMessage = "Object not found within your account scope";
                break;
            case 406:
                errorMessage = "Requested format is not supported - request JSON or XML only";
                break;
            case 422:
                errorMessage = "Validation error(s) for create or update method";
                break;
            case 500:
                errorMessage = "Something went wrong on challenge api. Their fault, contact them if this persists.";
                break;
        }
        return errorMessage;
    }


}
