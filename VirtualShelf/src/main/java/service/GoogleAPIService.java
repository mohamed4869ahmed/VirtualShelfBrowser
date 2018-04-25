package service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.Books.Volumes.List;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;
import org.springframework.stereotype.Service;

@Service
public class GoogleAPIService {


    public Volume.VolumeInfo getBookByISBN(String ISBN) {
        GoogleAPICredentials.errorIfNotSpecified();

        try {
            Books books = new Books.Builder(GoogleNetHttpTransport.newTrustedTransport(), new JacksonFactory(), null)
                    .setGoogleClientRequestInitializer(new BooksRequestInitializer(GoogleAPICredentials.API_KEY))
                    .build();

            String query = String.format("isbn:%s", ISBN);
            List volumesList = books.volumes().list(query);
            Volumes volumes = volumesList.execute();

            if (volumes.getTotalItems() != 1 || volumes.getItems() == null) {
                return null;
            }

            Volume volume = volumes.getItems().get(0);
            return volume.getVolumeInfo();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
