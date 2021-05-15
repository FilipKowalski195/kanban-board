package pl.lodz.zzpj.kanbanboard.remote;

import com.google.common.net.HttpHeaders;
import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.File;

@Configuration
public class RetrofitProvider {

    @Value("${cache.path}")
    private String cachePath;

    @Value("${cache.size}")
    private long cacheSize;

    @Value("${api.holidays.key}")
    private String apiKey;

    @Value("${cache.control}")
    private String cacheControl;

    @Bean
    public OkHttpClient provideOkHttpClient() {

        return new Builder()
                .cache(new Cache(new File(cachePath), cacheSize))
                .addInterceptor(chain -> {
                    var req = chain
                            .request()
                            .newBuilder()
                            .addHeader(HolidayApi.HOST_HEADER, HolidayApi.HOST)
                            .addHeader(HolidayApi.API_KEY_HEADER, apiKey)
                            .addHeader(HttpHeaders.CACHE_CONTROL, cacheControl)
                            .build();
                    return chain.proceed(req);
                }).build();
    }

    @Bean
    public HolidayApi provideHolidayApi(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(ReactorCallAdapterFactory.create())
                .baseUrl(HolidayApi.URL)
                .client(okHttpClient)
                .build()
                .create(HolidayApi.class);
    }
}
