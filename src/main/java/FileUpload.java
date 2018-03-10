import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class FileUpload {

    private static AWSCredentials credentials;
    private static AmazonS3 s3client;
    private static final String BUCKET_NAME = "vyaguta-dev";
    private static final String S3FOLDER_NAME = "employees";
    private static final String PATH_TO_IMAGES = "/home/leapfrog/images";

    public static void uploadMultipleFiles() throws IOException {
        credentials = new ProfileCredentialsProvider().getCredentials();
        s3client = new AmazonS3Client(credentials);
        FileUploadDTO fileUploadDto = new FileUploadDTO(BUCKET_NAME, S3FOLDER_NAME, s3client);

        File folder = new File(PATH_TO_IMAGES);
        File[] listOfFiles = folder.listFiles();

        Connection conn = null;
        try {
            conn = DbConnection.getPostgresConnection();
            PreparedStatement psFetch = conn.prepareStatement("select id from employees where employee_no = ?");
            PreparedStatement psUpdate = conn.prepareStatement("update employees set image_url = ? where employee_no = ?");

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    String fileName = listOfFiles[i].getName();
                    String employeeNo = computeEmployeeNo(fileName);
                    psFetch.setString(1, employeeNo);
                    ResultSet rs = psFetch.executeQuery();
                    String imageId = "";
                    while (rs.next()) {
                        imageId = rs.getString("id");
                    }
                    File file = new File(PATH_TO_IMAGES + "/" + fileName);
                    long contentLength = file.length();
                    fileUploadDto.setFile(file);
                    fileUploadDto.setContentLength(contentLength);
                    fileUploadDto.setFileName(imageId);
                    fileUploadDto.setContentType("image/png");
                    String imageUrl = uploadFile(fileUploadDto);
                    psUpdate.setString(1, imageUrl);
                    psUpdate.setString(2, employeeNo);
                    psUpdate.executeUpdate();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void dbOperations(Connection postgresConnection) throws SQLException {
        PreparedStatement ps = postgresConnection.prepareStatement("update employees set image_url = ? where employee_no = ?");
    }

    private static String computeEmployeeNo(String fileName) {
        return fileName.split("\\.")[0];
    }

    public static String uploadFile(FileUploadDTO fileUploadDto) throws AmazonServiceException, IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(fileUploadDto.getContentType());
        metadata.setContentLength(fileUploadDto.getContentLength());

        String key = fileUploadDto.getFolderName() + "/" + fileUploadDto.getFileName();
        String bucketName = fileUploadDto.getBucketName();
        File file = fileUploadDto.getFile();
        AmazonS3 s3client = fileUploadDto.getS3client();

        s3client.putObject(new PutObjectRequest(bucketName, key, file).withCannedAcl(CannedAccessControlList.PublicRead));
        S3Object s3Object = s3client.getObject(bucketName, key);
        String imageUrl = s3Object.getObjectContent().getHttpRequest().getURI().toString();
        System.out.println(imageUrl);
        s3Object.close();
        return imageUrl;
    }

    public static void main(String[] args) throws IOException {
        uploadMultipleFiles();
    }
}
