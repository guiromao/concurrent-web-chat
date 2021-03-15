package org.academiadecodigo.network;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientDispatcher implements Runnable {

    private final Socket clientSocket;

    public ClientDispatcher(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
       dispatch(clientSocket);
    }


    public void dispatch(Socket clientSocket) {

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());


            String requestHeaders = fetchRequestHeaders(in);
            if (requestHeaders.isEmpty()) {
                close(clientSocket);
                return;
            }

            String resource = parseRequest(requestHeaders, out, clientSocket);

            if (!isValidRequest(resource, out, clientSocket)) {
                return;
            }

            File file = createFileAndHeader(resource, out);

            reply(out, HttpHelper.contentType(file.getPath()));
            reply(out, HttpHelper.contentLength(file.length()));

            streamFile(out, file);
            close(clientSocket);


        } catch (IOException ex) {

            close(clientSocket);
        }


    }

    private boolean isValidRequest(String resource, DataOutputStream out, Socket clientSocket) throws IOException {

        if (resource == null) {
            reply(out, HttpHelper.BAD_REQUEST);
            close(clientSocket);
            return false;
        }

        String filePath = getPathForResource(resource);

        if (!HttpMedia.isSupported(filePath)) {
            reply(out, HttpHelper.UNSUPPORTED_MEDIA);
            close(clientSocket);
            return false;
        }

        return true;
    }

    private File createFileAndHeader(String resource, DataOutputStream out) throws IOException {

        String filePath = getPathForResource(resource);
        File file = new File(filePath);

        if (!file.exists() || file.isDirectory()) {

            reply(out, HttpHelper.NOT_FOUND);
            filePath = WebServer.DOCUMENT_ROOT + "404.html";
            file = new File(filePath);

        }

        reply(out, HttpHelper.OK);
        return file;

    }

    private String parseRequest(String requestHeaders, DataOutputStream out, Socket clientSocket) throws IOException {

        String request = requestHeaders.split("\n")[0]; // request is first line of header
        String httpVerb = request.split(" ")[0]; // verb is the first word of request

        if (!httpVerb.equals("GET")) {
            reply(out, HttpHelper.NOT_ALLOWED);
            close(clientSocket);
            return null;

        }

        String resource = request.split(" ").length > 1 ? request.split(" ")[1] : null; // second word of request
        return resource;

    }

    private String fetchRequestHeaders(BufferedReader in) throws IOException {

        String line = null;
        StringBuilder builder = new StringBuilder();

        // read the full http request
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            builder.append(line + "\n");
        }

        return builder.toString();

    }

    private String getPathForResource(String resource) {

        String filePath = resource;

        Pattern pattern = Pattern.compile("(\\.[^.]+)$"); // regex for file extension
        Matcher matcher = pattern.matcher(filePath);

        // localhost:8080/
        if (!matcher.find()) {
            filePath += "/index.html";
        }

        filePath = WebServer.DOCUMENT_ROOT + filePath;

        return filePath;
    }

    private void reply(DataOutputStream out, String response) throws IOException {
        out.writeBytes(response);
    }

    private void streamFile(DataOutputStream out, File file) throws IOException {

        byte[] buffer = new byte[1024];
        FileInputStream in = new FileInputStream(file);

        int numBytes;
        while ((numBytes = in.read(buffer)) != -1) {
            out.write(buffer, 0, numBytes);
        }

        in.close();

    }

    private void close(Socket clientSocket) {

        try {

            clientSocket.close();

        } catch (IOException e) {

            e.printStackTrace();
        }

    }



}
