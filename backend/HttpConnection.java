package backend;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;

/**
 * This class can be used to connect to it any URL via get or post requests.
 * */
public class HttpConnection extends SSLTrust{

    public HttpConnection(){
        super();
    }

    /**
     * Checks network availability.
     *
     * @return boolean: Returns true if network connection is available, false otherwise.*/
    public boolean networkAvailable(){
        try{
            URL url = new URL("http://www.google.com");
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
            urlConnect.getResponseCode();
            urlConnect.disconnect();
            return true;

        }catch (Exception e){
            return false;
        }
    }

    /**
     * Connects to absolute url provided via GET request method.
     * This method will fail if there is no network or if there is some other fatal error.
     *
     * @param absoluteURL: A string representation of the url to access.
     *
     * @return String: Returns a string representation of the GET request response on success. Will return an empty string on failure.
     * */
    public String GET(String absoluteURL){
        if(!networkAvailable()){
            return "";
        }

        try{
            URL url = new URL(absoluteURL);
            HttpURLConnection getRequestConnection = (HttpURLConnection) url.openConnection();
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(getRequestConnection.getInputStream()));
            String input;
            StringBuffer response = new StringBuffer();

            while((input = inputStream.readLine()) != null){
                response.append(input);
            }

            inputStream.close();
            getRequestConnection.disconnect();

            return response.toString();
        }catch(Exception e){
            System.out.println(e.getMessage());
            return "";
        }
    }

    /**
     * Connects to absolute url provided via POST request method.
     * This method will fail if there is no network or if there is some other fatal error.
     *
     * @param absoluteURL: A string representation of the url to be accessed.
     * @param params: A string representation of the parameters to be sent via post. eg. arg0=username&arg1=password.
     *              Note that the number of parameters that can be sent are not limited to any number.
     * @return String: Returns the http response in string format on success. Will return an empty string on failure.
     * */
    public String POST(String absoluteURL, String params){
        if(!networkAvailable()){
            return "";
        }

        try{
            URL url = new URL(absoluteURL);
            String input;
            StringBuffer response = new StringBuffer();

            HttpURLConnection postRequestConnect = (HttpURLConnection) url.openConnection();
            postRequestConnect.setRequestMethod("POST");
            postRequestConnect.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(postRequestConnect.getOutputStream());
            outputStream.writeBytes(params);
            outputStream.flush();
            outputStream.close();

            
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(postRequestConnect.getInputStream()));

            while((input = inputStream.readLine()) != null){
                response.append(input);
            }

            inputStream.close();
            postRequestConnect.disconnect();

            return response.toString();

        }catch(Exception e){
            System.out.println(e.getMessage());
            return "";
        }
    }

}
