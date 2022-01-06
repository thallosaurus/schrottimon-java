package com.prismflux.schrottimon;

import com.prismflux.schrottimon.auth.LoginPrompt;

import java.io.*;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AuthenticationClient {

    private static String cookie = "";

    public void login(String user, String password) throws Exception {
        //URL target = null;
        HttpURLConnection conn = null;

        try {
            URL target = new URL("http://localhost:9000/login");

            conn = (HttpURLConnection) target.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes("user=" + user + "&password=" + password);
            wr.close();

            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            if (response.toString() != "ok") {
                throw new Exception("Login Failed");
            }
            //return response.toString();
            List<HttpCookie> cookies = HttpCookie.parse(conn.getHeaderField("Set-Cookie"));

            cookie = cookies.get(0).toString();
        } catch (Exception e) {
            e.printStackTrace();
            //return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String getCookieString() {
        String[] c = cookie.split("=");
        return c[1];
    }

    public static void showLoginPrompt() {
        new LoginPrompt();
    }
}
