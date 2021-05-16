package pl.lodz.zzpj.kanbanboard.remote;

import pl.lodz.zzpj.kanbanboard.remote.data.Holiday;
import reactor.core.publisher.Mono;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface HolidayApi {
    String URL = "https://public-holiday.p.rapidapi.com";
    String HOST = "public-holiday.p.rapidapi.com\"";
    String HOST_HEADER = "x-rapidapi-host";
    String API_KEY_HEADER = "x-rapidapi-key";

    @GET("/{year}/{country}")
    Mono<List<Holiday>> getHolidays(@Path("year") String year, @Path("country") String country);
}
