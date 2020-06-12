package Insomnia.Connection;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The class Connection has a http connection and sets its settings. Such as its URL, method, headers, body,
 * etc. It also prints the information about this request's response.
 *
 * @author Negar Movaghatian
 */
public class Connection implements Serializable {

    transient private HttpURLConnection urlConnection; // An HttpURLConnection which is the base of this class
    private boolean showResponseHeaders; // Determines if the use wants to see the response header or not
    private boolean saveFile; // Determines if the user wants to save the output of the request as a file or not
    private String fileName; // The name of the file to write the request output in
    private boolean uploadBinary; // Determines if the user wants to upload a binary file
    private String binaryFileName; // The name of the binary file to upload
    private HashMap<String, String> formData; // The list of the data parameter of this request
    private HashMap<String, String> headers; // The list of the headers of this request
    private HashMap<String, String> query; // The list of the queries of this request
    private String urlString; // The url of this connection
    private String method; // The method of this connection
    private boolean followRedirect; // Shows if the user wants the program to follow redirects automatically or not
    private String responseLength; // The length of the response in byte, kilobyte or
    // megabyte depending on how large it is
    private String responseMessage; // The combination of the status code and message
    private byte[] streamBytes; // The bytes of the response
    private LinkedList<String> errors; // A list of runtime errors which occurs during running the program
    private String name; // The name of this request

    /**
     * Create a new Connection/
     * @param urlString The RUL of the request.
     * @param method The method of the request.
     * @param followRedirect Shows if the user want the program to follow redirects automatically or not.
     * @param showResponseHeaders Shows if the user want the program to show the response headers or not.
     * @param saveFile Shows if the user wants the program to save the output as a file or not.
     * @param fileName The name of the file to write the request output in.
     * @param uploadBinary Shows if the user wants to upload a binary file or not.
     * @param binaryFileName The name of the binary file to upload.
     * @param formData The list of the data parameter of this request.
     * @param headers The list of the headers of this request
     */
    public Connection(String name, String urlString, String method, boolean followRedirect, boolean showResponseHeaders, boolean saveFile,
                      String fileName, boolean uploadBinary, String binaryFileName, HashMap<String, String> formData,
                      HashMap<String, String> headers, HashMap<String, String> query) {
        this.name = name;
        this.urlString = urlString;
        this.formData = formData;
        this.headers = headers;
        this.method = method;
        this.followRedirect = followRedirect;
        this.showResponseHeaders = showResponseHeaders;
        this.saveFile = saveFile;
        this.fileName = fileName;
        this.uploadBinary = uploadBinary;
        this.binaryFileName = binaryFileName;
        this.query = query;
        errors = new LinkedList<>();
    }

    /**
     * Open the connection and set its settings.
     */
    public void runConnection() {
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(followRedirect);
            urlConnection.setRequestMethod(method);
            if (formData.size() != 0)
                putFormData();
            if (headers.size() != 0)
                putHeaders();
            if (fileName != null && fileName.contains("output_") && !fileName.contains("."))
                this.fileName = fileName.concat('.' + getResponseType());
            if (uploadBinary)
                uploadBinary();
        } catch (Exception e) {
            System.err.println("Could not connect to server: " + e.getMessage());
            errors.add("Could not connect to server: " + e.getMessage());
        }
    }

    /**
     * Print the required information of this request.
     */
    public void printResponseInfo() {
        try {
            // Print status code and message
            responseMessage = urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage();
            System.out.println("\nStatus Code: " + responseMessage);

            // Get the response body
            InputStream connectionInputStream;
            if (urlConnection.getResponseMessage().equals("OK"))
                connectionInputStream = urlConnection.getInputStream();
            else
                connectionInputStream = urlConnection.getErrorStream();

            streamBytes = StreamUtils.getStreamBytes(new BufferedInputStream(connectionInputStream));

            // Print the response body
            if (streamBytes != null) {
                String responseBody = StreamUtils.getResponseBodyText(streamBytes);
                System.out.println("\nResponse Body:\n" + responseBody);
            }
            else
                System.out.println("\nResponse Body: Empty");

            // Print headers info
            if (showResponseHeaders) {
                System.out.println("\n\nResponse Headers:");
                for (int i = 0; i < urlConnection.getHeaderFields().size(); i++)
                    System.out.println(urlConnection.getHeaderFieldKey(i) + " = " + urlConnection.getHeaderField(i));
            }

            // Show response type
            System.out.println("\nResponse Type: " + getResponseType());

            // Show response size
            if (streamBytes != null)
                responseLength = (streamBytes.length > 1048576)? (String.format("%.2fMB", (float) streamBytes.length/1048576)) :
                        (streamBytes.length > 1024)? (String.format("%.2fKB",(float) streamBytes.length/1024)) : ((float) streamBytes.length + "B");
            else
                responseLength = "0B";
            System.out.println("\nResponse Size: " + responseLength);

            // Save response
            if (saveFile)
                StreamUtils.fileWriter(streamBytes, fileName);

            urlConnection.disconnect();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while communicating with server: " + e.getMessage());
        }
    }

    /**
     * Write the request body into the requests output stream.
     * @param body The list of the body parameters.
     * @param boundary The boundary to separate the form data fields with.
     * @param bufferedOutputStream The output stream of this connection.
     */
    public void bufferOutFormData(HashMap<String, String> body, String boundary, BufferedOutputStream bufferedOutputStream) {
        try {
            for (String key : body.keySet()) {
                bufferedOutputStream.write(("--" + boundary + "\r\n").getBytes());
                if (key.contains("file")) {
                    if (!StreamUtils.isPathValid(body.get(key)))
                        System.out.println("Could not add this key to form-data");
                    bufferedOutputStream.write(("Content-Disposition: form-data; filename=\"" + (new File(body.get(key))).getName() + "\"\r\nContent-Type: Auto\r\n\r\n").getBytes());
                    try {
                        BufferedInputStream tempBufferedInputStream = new BufferedInputStream(new FileInputStream(new File(body.get(key))));
                        byte[] filesBytes = new byte[tempBufferedInputStream.available()];
                        tempBufferedInputStream.read(filesBytes);
                        bufferedOutputStream.write(filesBytes);
                        bufferedOutputStream.write("\r\n".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    bufferedOutputStream.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
                    bufferedOutputStream.write((body.get(key) + "\r\n").getBytes());
                }
            }
            bufferedOutputStream.write(("--" + boundary + "--\r\n").getBytes());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (IOException e) {
            System.err.println("Could not write form data: " + e.getMessage());
            errors.add("Could not write form data: " + e.getMessage());
        }
    }

    /**
     * Add form data information to headers of this request and write the to this request's
     * output stream using method bufferOutFormData.
     */
    private void putFormData() {
        try {
            String boundary = "X-MAXEU-BOUNDARY";
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            BufferedOutputStream requestOutputStream = new BufferedOutputStream(urlConnection.getOutputStream());
            bufferOutFormData(formData, boundary, requestOutputStream);
        } catch (Exception e) {
            System.err.println("Could not write form data: " + e.getMessage());
            errors.add("Could not write form data: " + e.getMessage());
        }
    }

    /**
     * Upload a binary file using POST method.
     */
    public void uploadBinary() {
        try {
            if (!StreamUtils.isPathValid(binaryFileName)) {
                System.out.println("Failed to upload the binary file.");
                return;
            }
            File fileToUpload = new File(binaryFileName);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/octet-stream");
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(fileToUpload));
            byte[] filesBytes = new byte[fileInputStream.available()];
            fileInputStream.read(filesBytes);
            bufferedOutputStream.write(filesBytes);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (IOException e) {
            System.err.println("Could not upload binary file: " + e.getMessage());
           errors.add("Could not upload binary file: " + e.getMessage());
        }
    }

    public void updateRequest(boolean followRedirect, String url, String method, boolean uploadBinary, String binaryFileName,
                       HashMap<String, String> formData, HashMap<String, String> headers, HashMap<String, String> query) {
        this.followRedirect = followRedirect;
        this.urlString = url;
        this.method = method;
        this.binaryFileName = binaryFileName;
        this.uploadBinary = uploadBinary;
        this.formData = formData;
        this.headers = headers;
        this.query = query;
    }

    /**
     * Add all the headers given by the user to this request's headers.
     */
    private void putHeaders() {
        for (String name : headers.keySet())
            urlConnection.setRequestProperty(name, headers.get(name));
    }

    /**
     * Get the response type of this request. For example 'txt', 'png', 'html', etc.
     * @return The type of the response of this request.
     */
    private String getResponseType() {
        String header = urlConnection.getContentType();
        if (header != null) {
            int c = header.indexOf('/') + 1;
            String type = "";
            while (c < header.length() && header.charAt(c) != ';' && header.charAt(c) != ' ' && header.charAt(c) != '\n')
                type = type.concat(header.charAt(c++) + "");
            return type;
        }
        return "Unknown";
    }

    /**
     * @return The URL this request is sent to.
     */
    public String getUrlString() {
        return urlString;
    }

    /**
     * @return The length of the response in byte, kilobyte or megabyte depending on how large it is.
     */
    public String getResponseSize() {
        return responseLength;
    }

    /**
     * @return The combination of the status code and message.
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }

    public HashMap<String, String> getQuery() {
        return query;
    }

    public String getBinaryFileName() {
        return binaryFileName;
    }

    /**
     * Get the list of the response headers.
     * @return A list of the response headers.
     */
    public HashMap<String, String> getHeaders() {
        HashMap<String, String> responseHeaders = new HashMap<>();
        for (int i = 0; i < urlConnection.getHeaderFields().size(); i++)
            responseHeaders.put(urlConnection.getHeaderFieldKey(i), urlConnection.getHeaderField(i));
        return responseHeaders;
    }

    public HashMap<String, String> getRequestHeaders() {
        return headers;
    }

    /**
     * @return True if the response is an image and false otherwise.
     */
    public boolean isImage() {
        return urlConnection.getContentType() != null && urlConnection.getContentType().contains("image");
    }

    /**
     * @return The response body as text.
     */
    public String getResponseText() {
        String responseBody = "Empty";
        if (streamBytes != null)
            responseBody = StreamUtils.getResponseBodyText(streamBytes);
        return responseBody;
    }

    /**
     * @return The bytes of the response body.
     */
    public byte[] getResponseBytes() {
        return streamBytes;
    }

    /**
     * @return A list of the errors which happened while communicating with the server.
     */
    public String getErrors() {
        String error = "";
        for (String e : errors)
            error = error.concat("- " + e + "\n");
        return error;
    }

    public HashMap<String, String> getFormData() {
        return formData;
    }

    public void setUploadBinary(boolean uploadBinary) {
        this.uploadBinary = uploadBinary;
    }

    public void setBinaryFileName(String binaryFileName) {
        this.binaryFileName = binaryFileName;
    }

    public void setFormData(HashMap<String, String> formData) {
        this.formData = formData;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return A string which contains the information about this connection such as its URL, Method, Headers, etc.
     */
    @Override
    public String toString() {
        return "URL: " + urlString + " | " +
                "Method: " + method + " | " +
                "Headers: " + headers.toString() + " | " +
                "Request Body: " + formData.toString() + " | " +
                "Follow Redirects: " + followRedirect + " | " +
                "Output File Name: " + ((saveFile)? fileName : "None");
    }
}
