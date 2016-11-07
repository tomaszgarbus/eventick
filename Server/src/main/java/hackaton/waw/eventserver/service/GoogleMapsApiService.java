package hackaton.waw.eventserver.service;

import com.google.maps.model.LatLng;
import hackaton.waw.eventserver.model.Location;
import hackaton.waw.eventserver.repo.LocationRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

/**
 * Created by tomek on 11/3/16.
 */
@Component
@Service
public class GoogleMapsApiService {
    @Autowired LocationRepository locationRepository;

    private static final String GOOGLE_MAPS_GEOCODING_API_KEY = "AIzaSyD6dslQugNBaveQumVTq6LqmZOUWZpT26Y";
    private static final String GOOGLE_MAPS_DISTANCE_API_KEY = "AIzaSyAG__UkhgKdjUg__6tZMiDK0OuMKLi1eXk";

    private String httpsGetQuery(String queryUrl) throws Exception {
        URL myurl = new URL(queryUrl);
        HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
        InputStream ins = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(ins);
        BufferedReader in = new BufferedReader(isr);
        String inputLine;
        String response = "";
        while ((inputLine = in.readLine()) != null)  {
            response = response + inputLine;
        }
        in.close();
        return response;
    }

    public LatLng getLocationLatLng(String locationName) {
        locationName = locationName.replace(' ', '+');
        final String queryUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + locationName + "&key=" + GOOGLE_MAPS_GEOCODING_API_KEY;
        try {
            String response = httpsGetQuery(queryUrl);
            JSONObject jsonLatLng = new JSONObject(response).getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            LatLng latLng = new LatLng(jsonLatLng.getDouble("lat"), jsonLatLng.getDouble("lng"));
            return latLng;
        } catch (Exception e) {

        }
        return null;
    }

    public Long getTravelTimeInMinutes(LatLng source, LatLng dest) {
        final String queryUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + source.lat + "," + source.lng
                + "&destinations=" + dest.lat + "," + dest.lng
                + "&key=" + GOOGLE_MAPS_DISTANCE_API_KEY;
        try {
            String response = httpsGetQuery(queryUrl);
            Long time = new JSONObject(response).getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getLong("value");
            return time/60;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateLocationsLatLng() {
        List<Location> allLocations = locationRepository.findAll();
        for (Location location : allLocations) {
            LatLng latLng = getLocationLatLng(location.getName());
            if (latLng != null){
                location.setLat(latLng.lat);
                location.setLng(latLng.lng);
                locationRepository.save(location);
            }
        }
    }



}
