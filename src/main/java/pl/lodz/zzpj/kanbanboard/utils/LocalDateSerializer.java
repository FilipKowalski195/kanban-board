package pl.lodz.zzpj.kanbanboard.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer extends StdSerializer<LocalDate> {

    public LocalDateSerializer() {
        super(LocalDate.class);
    }

    @Override
    public void serialize(
            LocalDate value,
            JsonGenerator gen,
            SerializerProvider provider
    ) throws IOException {
        gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
