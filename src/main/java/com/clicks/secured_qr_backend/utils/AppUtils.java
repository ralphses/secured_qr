package com.clicks.secured_qr_backend.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AppUtils {

    public static final String REF_SEP = "@@Client=";
    public static final String QR_CODE_FOLDER = "src/main/resources/codes";
    public static final String UPLOADS_FOLDER = "src/main/resources/uploads";

    public static String getActivationMessage(String name, String token) {

        return "Dear " + name + "\n\nClick on the link below to activate your account \n" + "http://localhost:3000/activate?token=" + token;
    }

    public static String getUrl(String path, Map<String, String> params) {

        // Get the current HttpServletRequest
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // Get the current host from the request
        String currentHost = request.getServerName();

        // Create a UriComponentsBuilder
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme(request.getScheme())
                .port(request.getServerPort())// Use the current scheme (http or https)
                .host(currentHost);

        // Add path segments to the URL
        builder.path(path);

        params.forEach(builder::queryParam);

        return builder.toUriString();

    }

    public static String saveFile(BufferedImage image, String qrCodeDataRef, String path) throws IOException {

        File directory = new File(path);

        if (!directory.exists()) {
            directory.mkdirs(); // Use mkdirs() to create parent directories if they don't exist
        }

        String filePath = path + File.separator + qrCodeDataRef + ".png";

        File qrCodePath = new File(filePath);
        ImageIO.write(image, "png", qrCodePath);

        return filePath;
    }
}
