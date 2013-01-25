package models;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import play.Logger;
import play.db.ebean.*;
import plugins.S3Plugin;

import java.util.*;
import javax.persistence.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Entity
public class S3File extends Model {

    @Id
    public UUID id;

    private String bucket;

    public String name;

    @Transient
    public File file;

    public URL getUrl() throws MalformedURLException {
        return new URL("https://s3.amazonaws.com/" + bucket + "/" + getActualFileName());
    }

    private String getActualFileName() {
        return id + "/" + name;
    }

    @Override
    public void save() {
        if (S3Plugin.amazonS3 == null) {
            Logger.error("Could not save because amazonS3 was null");
            throw new RuntimeException("Could not save");
        }
        else {
            this.bucket = S3Plugin.s3Bucket;
            
            super.save(); // assigns an id

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, getActualFileName(), file);
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            S3Plugin.amazonS3.putObject(putObjectRequest);
        }
    }

    @Override
    public void delete() {
        if (S3Plugin.amazonS3 == null) {
            Logger.error("Could not delete because amazonS3 was null");
            throw new RuntimeException("Could not delete");
        }
        else {
            S3Plugin.amazonS3.deleteObject(bucket, getActualFileName());
            super.delete();
        }
    }

}