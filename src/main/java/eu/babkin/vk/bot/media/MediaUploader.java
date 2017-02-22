package eu.babkin.vk.bot.media;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.docs.Doc;
import com.vk.api.sdk.objects.photos.Photo;
import eu.babkin.vk.bot.media.document.DocumentMetadata;
import eu.babkin.vk.bot.media.photo.PhotoMetadata;
import eu.babkin.vk.bot.utils.HttpResponseUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class MediaUploader {

    private static final Logger logger = LoggerFactory.getLogger(MediaUploader.class);

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private VkApiClient vk;

    @Autowired
    private UserActor actor;

    @Autowired
    private HttpResponseUtils responseUtils;


    public Photo uploadPhoto(String url) throws MediaUploadException {
        String uploadUrl = getPhotoUploadUrl();

        InputStreamBody stream = getUploadStreamBody(url);

        HttpEntity entity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addPart("file", stream).build();

        HttpResponse response;
        try {
            HttpUriRequest request = RequestBuilder.post(uploadUrl).setEntity(entity).build();
            response = httpClient.execute(request);
        } catch (IOException e) {
            throw new MediaUploadException("unable to perform upload photo to url");
        }

        PhotoMetadata photoMetadata = responseUtils.toObject(response, PhotoMetadata.class);
        logger.info("saving photo {}", photoMetadata);

        List<Photo> photos = savePhotoToMessages(photoMetadata);
        return photos.get(0);
    }

    private InputStreamBody getUploadStreamBody(String url) throws MediaUploadException {
        HttpUriRequest request = RequestBuilder.get(url).build();
        try {
            InputStream imageContent = httpClient.execute(request).getEntity().getContent();
            String filename = url.substring(url.lastIndexOf("/") + 1, url.length());
            return new InputStreamBody(imageContent, ContentType.DEFAULT_BINARY, filename);
        } catch (IOException e) {
            throw new MediaUploadException("Cannot get image from " + url, e);
        }
    }

    private List<Photo> savePhotoToMessages(PhotoMetadata photoMetadata) throws MediaUploadException {
        try {
            return vk.photos()
                    .saveMessagesPhoto(actor, photoMetadata.getPhoto())
                    .server(photoMetadata.getServer())
                    .hash(photoMetadata.getHash())
                    .execute();
        } catch (Exception e) {
            throw new MediaUploadException("cannot save photo to messages", e);
        }
    }

    private String getPhotoUploadUrl() throws MediaUploadException {
        try {
            return vk.photos().getMessagesUploadServer(actor).execute().getUploadUrl();
        } catch (Exception e) {
            throw new MediaUploadException("cannot get upload url", e);
        }

    }

    public Doc uploadDocument(String url) throws MediaUploadException {
        String uploadUrl = getDocumentUploadUrl();

        InputStreamBody stream = getUploadStreamBody(url);

        HttpEntity entity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addPart("file", stream).build();

        HttpResponse response;
        try {
            HttpUriRequest request = RequestBuilder.post(uploadUrl).setEntity(entity).build();
            response = httpClient.execute(request);
        } catch (IOException e) {
            throw new MediaUploadException("unable to perform upload to url");
        }

        DocumentMetadata docMeta = responseUtils.toObject(response, DocumentMetadata.class);
        logger.info("saving photo {}", docMeta);

        List<Doc> docs = null;
        try {
            docs = vk.docs().save(actor, docMeta.getFile()).execute();
        } catch (Exception e) {
            throw new MediaUploadException("");
        }
        return docs.get(0);
    }

    private String getDocumentUploadUrl() throws MediaUploadException {
        try {
            return vk.docs().getUploadServer(actor).execute().getUploadUrl();
        } catch (Exception e) {
            throw new MediaUploadException("cannot get document upload url", e);
        }
    }
}
