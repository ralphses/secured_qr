package com.clicks.secured_qr_backend.service;

import com.clicks.secured_qr_backend.config.security.ApiKeyUtils;
import com.clicks.secured_qr_backend.dtos.QrCodeResponse;
import com.clicks.secured_qr_backend.dtos.requests.QrCodeDto;
import com.clicks.secured_qr_backend.dtos.requests.VerifyQrCodeRequest;
import com.clicks.secured_qr_backend.exceptions.InvalidParamsException;
import com.clicks.secured_qr_backend.exceptions.ResourceNotFoundException;
import com.clicks.secured_qr_backend.exceptions.UnauthorizedUserException;
import com.clicks.secured_qr_backend.models.Client;
import com.clicks.secured_qr_backend.models.QRCodeData;
import com.clicks.secured_qr_backend.repository.QRCodeDataRepository;
import com.clicks.secured_qr_backend.utils.DTOMapper;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static com.clicks.secured_qr_backend.utils.AppUtils.*;
import static com.google.zxing.BarcodeFormat.QR_CODE;
import static java.time.LocalDateTime.now;

@Service
@Transactional
@RequiredArgsConstructor
public class QRCodeDataService {

    private final QRCodeDataRepository qrCodeDataRepository;
    private final ApiKeyUtils apiKeyUtils;
    private final ClientService clientService;
    private final DTOMapper mapper;

    /**
     * Creates a new QR code and associated data for a given client.
     *
     * @param qrCodeDto The DTO containing QR code details.
     * @param request   The HTTP servlet request.
     * @return The generated QR code response.
     */
    public QrCodeResponse create(QrCodeDto qrCodeDto, HttpServletRequest request) {
        Client client = resolveClient(request);
        return getQrCodeResponse(qrCodeDto, client);
    }

    private String getApiKey(HttpServletRequest request) {
        String key = request.getHeader("Authorization");
        if (key == null || !key.startsWith("Bearer API-")) {
            throw new InvalidParamsException("Invalid Authorization header");
        }
        return key;
    }

    private String[] getUsernameAndClient(String key) {
        try {
            String decryptedKey = apiKeyUtils.decrypt(key.substring(7).replace("API-", ""));
            return decryptedKey.split(REF_SEP);
        } catch (Exception e) {
            throw new InvalidParamsException("Invalid API key");
        }
    }

    /**
     * Creates a new QR code and associated data for a given client.
     *
     * @param qrCodeDto The DTO containing QR code details.
     * @param client    The associated client.
     * @return The generated QR code response.
     */
    public QrCodeResponse create(QrCodeDto qrCodeDto, Client client) {
        return getQrCodeResponse(qrCodeDto, client);
    }

    private QrCodeResponse getQrCodeResponse(QrCodeDto qrCodeDto, Client client) {
        try {
            String qrCodeDataRef = UUID.randomUUID().toString();
            QRCodeData codeData = QRCodeData.builder()
                    .checkOutUrl(qrCodeDto.checkOutUrl())
                    .reference(qrCodeDataRef)
                    .createdAt(now())
                    .itemName(qrCodeDto.item())
                    .amount(qrCodeDto.amount())
                    .client(client)
                    .build();

            generateQRCodeImage(qrCodeDto.checkOutUrl(), qrCodeDataRef);

            QRCodeData qrCodeData = qrCodeDataRepository.save(codeData);

            return mapper.qrCodeResponse(qrCodeData);
        } catch (Exception e) {
            throw new InvalidParamsException("Failed to create QR code");
        }
    }

    /**
     * Generates a QR code image and saves it.
     *
     * @param text         The text to be encrypted and encoded in the QR code.
     * @param qrCodeDataRef The reference for the QR code data.
     * @throws Exception If an error occurs during QR code generation.
     */
    public void generateQRCodeImage(String text, String qrCodeDataRef) throws Exception {
        String encryptedText = apiKeyUtils.encrypt(text);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(encryptedText, QR_CODE, 650, 650);

        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        saveFile(qrCodeImage, qrCodeDataRef, QR_CODE_FOLDER);
    }

    /**
     * Retrieves the QR code image as a resource.
     *
     * @param reference The reference of the QR code data.
     * @return The QR code image resource.
     */
    public Resource get(String reference) {
        Path filePath = Paths.get(QR_CODE_FOLDER, reference + ".png");
        if (Files.exists(filePath)) {
            return new FileSystemResource(filePath);
        }
        throw new ResourceNotFoundException("QR Code with reference " + reference + " not found");
    }

    /**
     * Verifies a QR code by decrypting the payment information.
     *
     * @param verifyQrCodeRequest The request containing the encrypted payment information.
     * @return The decrypted payment information (checkout URL).
     */
    public String verify(VerifyQrCodeRequest verifyQrCodeRequest) {
        try {
            String checkoutUrl = apiKeyUtils.decrypt(verifyQrCodeRequest.payment());
            if (isLegitimateQrCode(checkoutUrl)) {
                return checkoutUrl;
            } else {
                throw new IllegalStateException("QR Code not legitimate");
            }
        } catch (IllegalStateException exception) {
            throw new UnauthorizedUserException(exception.getMessage());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Invalid QR code");
        }
    }

    private boolean isLegitimateQrCode(String checkoutUrl) {
        return qrCodeDataRepository.existsByCheckOutUrl(checkoutUrl);
    }

    /**
     * Retrieves all QR codes associated with a given client.
     *
     * @param request The HTTP servlet request.
     * @return List of QR code responses.
     */
    public List<QrCodeResponse> getAll(HttpServletRequest request) {
        Client client = resolveClient(request);
        List<QRCodeData> qrCodeData = qrCodeDataRepository.findByClient_Id(client.getId());
        return qrCodeData.stream().map(mapper::qrCodeResponse).toList();
    }

    /**
     * Resolves the client associated with the API key from the request.
     *
     * @param request The HTTP servlet request.
     * @return The associated client.
     */
    private Client resolveClient(HttpServletRequest request) {
        String key = getApiKey(request);
        String[] usernameAndClient = getUsernameAndClient(key);

        if (usernameAndClient.length < 2) {
            throw new InvalidParamsException("Invalid client key");
        }

        String client = usernameAndClient[1];
        return clientService.findByReference(client);
    }

    public String verifyImage(MultipartFile qrCode) {
        try {

            BufferedImage image = ImageIO.read(qrCode.getInputStream());
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            Result result = new MultiFormatReader().decode(binaryBitmap);

            return verify(new VerifyQrCodeRequest(result.getText()));

        } catch (UnauthorizedUserException exception) {
            throw new ResourceNotFoundException(exception.getMessage());
        }
        catch (IOException | NotFoundException | NullPointerException e) {
            throw new InvalidParamsException("Image does not contain a valid QR code");
        }
    }
}
