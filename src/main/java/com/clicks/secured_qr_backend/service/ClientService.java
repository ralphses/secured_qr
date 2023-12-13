package com.clicks.secured_qr_backend.service;

import com.clicks.secured_qr_backend.config.security.ApiKeyUtils;
import com.clicks.secured_qr_backend.dtos.ClientDto;
import com.clicks.secured_qr_backend.dtos.requests.NewClientRequest;
import com.clicks.secured_qr_backend.exceptions.InvalidParamsException;
import com.clicks.secured_qr_backend.exceptions.ResourceNotFoundException;
import com.clicks.secured_qr_backend.models.AppUser;
import com.clicks.secured_qr_backend.models.Client;
import com.clicks.secured_qr_backend.repository.ClientRepository;
import com.clicks.secured_qr_backend.utils.AppUtils;
import com.clicks.secured_qr_backend.utils.DTOMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.clicks.secured_qr_backend.utils.AppUtils.REF_SEP;
import static com.clicks.secured_qr_backend.utils.AppUtils.UPLOADS_FOLDER;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final AppUserService userService;
    private final ApiKeyUtils apiKeyUtils;
    private final DTOMapper mapper;

    /**
     * Creates a new client and returns the generated API key.
     *
     * @param newClientRequest The request containing client details.
     * @param authentication   The authentication object.
     * @return The generated API key.
     */
    public String create(NewClientRequest newClientRequest, Authentication authentication) {
        // Generate a unique reference for the client
        String reference = UUID.randomUUID().toString();

        // Get file paths for NIN and Business Certificate images
        String ninPath = getImagePath(newClientRequest.nin(), "nin_" + reference);
        String businessCertPath = getImagePath(newClientRequest.businessCertificate(), "business_" + reference);

        // Create the client object
        AppUser user = userService.findByEmail(authentication.getName());
        Client client = buildClient(newClientRequest, reference, ninPath, businessCertPath, user);

        // Create and set the API key
        String apiKey = createApiKey(authentication.getName(), reference);
        client.setApiKey(apiKey);

        // Save the client
        clientRepository.save(client);

        return apiKey;
    }

    private Client buildClient(NewClientRequest newClientRequest, String reference, String ninPath, String businessCertPath, AppUser user) {
        return Client.builder()
                .businessName(newClientRequest.name())
                .address(newClientRequest.address())
                .user(user)
                .businessManagerPhone(newClientRequest.businessManagerPhone())
                .yearCreated(newClientRequest.yearCreated())
                .businessManager(newClientRequest.businessManagerName())
                .noOfStaff(newClientRequest.numOfStaff())
                .ninPath(ninPath)
                .businessCertPath(businessCertPath)
                .reference(reference)
                .build();
    }

    /**
     * Stores an image file and returns its path.
     *
     * @param file      The image file.
     * @param clientRef The client reference.
     * @return The path of the stored image file.
     * @throws InvalidParamsException If an invalid image file is provided.
     */
    private String getImagePath(MultipartFile file, String clientRef) {
        try {
            return storeFile(file, clientRef);
        } catch (IOException e) {
            throw new InvalidParamsException("Invalid image file passed");
        }
    }

    private String storeFile(MultipartFile file, String clientRef) throws IOException {
        byte[] fileBytes = file.getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        return AppUtils.saveFile(image, clientRef, UPLOADS_FOLDER);
    }

    /**
     * Generates an API key for the given subject and reference.
     *
     * @param subject   The subject for the API key.
     * @param reference The reference for the API key.
     * @return The generated API key.
     * @throws RuntimeException If an error occurs while generating the API key.
     */
    public String createApiKey(String subject, String reference) {
        try {
            String input = subject + REF_SEP + reference;
            return "API-" + apiKeyUtils.encrypt(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a list of clients associated with the authenticated user.
     *
     * @param authentication The authentication object.
     * @return List of client DTOs.
     */
    public List<ClientDto> get(Authentication authentication) {
        AppUser user = userService.findByEmail(authentication.getName());
        List<Client> clients = clientRepository.findByUserId(user.getId());
        return clients.stream().map(mapper::clientDto).toList();
    }

    /**
     * Retrieves a client by its reference.
     *
     * @param clientReference The reference of the client.
     * @return The client object.
     * @throws ResourceNotFoundException If the client with the given reference is not found.
     */
    public Client findByReference(String clientReference) {
        return clientRepository.findByReference(clientReference)
                .orElseThrow(() -> new ResourceNotFoundException("Client with reference " + clientReference + " not found"));
    }
}
