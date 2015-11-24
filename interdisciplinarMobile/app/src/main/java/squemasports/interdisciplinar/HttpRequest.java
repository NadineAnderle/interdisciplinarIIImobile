package squemasports.interdisciplinar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpRequest {

    private static String _SERVER = "http://10.0.0.2:8080/";

    public static String getServer() {
        return _SERVER;
    }

    public static void setServer(String server) {
        _SERVER = server;
    }

    public static String getWS(String ws) {
        return getServer() + "ws/" + ws;
    }

    public static String getJson(String ws) throws IOException {
        StringBuilder sb = new StringBuilder();

        URL url = new URL(getWS(ws));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(false);

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String linha;
        while((linha = br.readLine()) != null) {
            sb.append(linha);
        }
        br.close();

        return sb.toString();
    }

    public static String postJson(String ws, String jsonPost) throws IOException {
        URL url = new URL(getWS(ws));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-type", "application/json");
        con.setRequestProperty("Accept", "application/json");

        OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
        System.out.println("JSON POST: " + jsonPost);
        osw.write(jsonPost);
        osw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String linha;
        while((linha = br.readLine()) != null) {
            sb.append(linha);
        }
        br.close();

        return sb.toString();
    }
}
