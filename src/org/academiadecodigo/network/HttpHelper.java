package org.academiadecodigo.network;

import java.io.File;

public class HttpHelper {

    public static final String BAD_REQUEST = "HTTP/1.0 400 Bad Request\r\n";
    public static final String NOT_FOUND = "HTTP/1.0 404 Not Found\r\n";
    public static final String OK = "HTTP/1.0 200 Document Follows\r\n";
    public static final String UNSUPPORTED_MEDIA = "HTTP/1.0 415 Unsupported Media Type\r\n";
    public static final String NOT_ALLOWED = "HTTP/1.0 405 Method Not Allowed\r\nAllow: GET\r\n";

    public static String contentLength(long length) {
        return "Content-Length: " + length + "\r\n\r\n";
    }

    public static String contentType(String file) {

        if (HttpMedia.isImage(file)) {
            return "Content-Type: image/" + HttpMedia.getExtension(file) + "\r\n";
        }

        return "Content-Type: text/html; charset=UTF-8\r\n";

    }

}
