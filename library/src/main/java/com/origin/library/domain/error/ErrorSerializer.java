package com.origin.library.domain.error;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

// FIXME: Use fastjson2 instead of this implementation
public class ErrorSerializer extends StdSerializer<Error> {

  public ErrorSerializer() {
    this(null);
  }

  public ErrorSerializer(Class<Error> t) {
    super(t);
  }

  @Override
  public void serialize(Error error, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {

    jsonGenerator.writeStartObject();
    jsonGenerator.writeNumberField("code", error.code);
    jsonGenerator.writeStringField("message", error.message);
    jsonGenerator.writeStringField("details", error.details);
    jsonGenerator.writeEndObject();
  }
}
