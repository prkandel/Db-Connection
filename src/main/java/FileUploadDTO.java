
import java.io.File;
import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;

/**
 * 
 * @author Prajjwal Raj Kandel<prajjwalkandel@lftechnology.com>
 *
 */

public class FileUploadDTO {

    private String bucketName;

    private String folderName;

    private String fileName;

    private AmazonS3 s3client;

    private File file;

    private String contentType;

    private long contentLength;

    public FileUploadDTO() {

    }

    public FileUploadDTO(String bucketName, String folderName, AmazonS3 s3client) {
        this.bucketName = bucketName;
        this.folderName = folderName;
        this.s3client = s3client;
    }

    public FileUploadDTO(String bucketName, String folderName, String fileName, AmazonS3 s3client, File file) {
        this.bucketName = bucketName;
        this.folderName = folderName;
        this.fileName = fileName;
        this.s3client = s3client;
        this.setFile(file);
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public AmazonS3 getS3client() {
        return s3client;
    }

    public void setS3client(AmazonS3 s3client) {
        this.s3client = s3client;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
