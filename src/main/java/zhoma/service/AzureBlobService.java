package zhoma.service;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class AzureBlobService {

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.account-key}")
    private String accountKey;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public String uploadImage(String originalFilename, InputStream data, long length) {
        BlobContainerClient containerClient = new BlobContainerClientBuilder()
                .connectionString(getConnectionString())
                .containerName(containerName)
                .buildClient();

        String fileName = UUID.randomUUID() + "_" + originalFilename;
        BlobClient blobClient = containerClient.getBlobClient(fileName);



        BlobHttpHeaders headers = new BlobHttpHeaders();
        String contentType = "image/jpeg";  // Настройте в зависимости от типа файла
        headers.setContentType(contentType);

        blobClient.upload(data, length, true);
        blobClient.setHttpHeaders(headers);
        return blobClient.getBlobUrl();
    }

    public void deleteFile(String fileName) {
        BlobContainerClient containerClient = new BlobContainerClientBuilder()
                .connectionString(getConnectionString())
                .containerName(containerName)
                .buildClient();

        BlobClient blobClient = containerClient.getBlobClient(fileName);
        blobClient.delete();
    }

    private String getConnectionString() {
        return String.format(
                "DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net",
                accountName, accountKey
        );
    }
}
