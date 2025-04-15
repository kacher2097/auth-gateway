package com.authenhub.config.application;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import java.io.DataInput;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

public class JsonMapper extends ObjectMapper {

    @Override
    public JsonMapper configure(MapperFeature f, boolean state) {
        super.configure(f, state);
        return this;
    }

    @Override
    public JsonMapper configure(SerializationFeature f, boolean state) {
        super.configure(f, state);
        return this;
    }

    @Override
    public JsonMapper configure(DeserializationFeature f, boolean state) {
        super.configure(f, state);
        return this;
    }

    @Override
    public JsonMapper configure(JsonParser.Feature f, boolean state) {
        super.configure(f, state);
        return this;
    }

    @Override
    public JsonMapper configure(JsonGenerator.Feature f, boolean state) {
        super.configure(f, state);
        return this;
    }

    public String toJson(Object value) throws Exception {
        try {
            return super.writeValueAsString(value);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public <T> T fromJson(JsonParser p, Class<T> valueType) throws Exception {
        try {
            return super.readValue(p, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public <T> T fromJson(JsonNode p, Class<T> valueType) throws Exception {
        try {
            return super.treeToValue(p, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public <T> T fromJson(JsonParser p, TypeReference<T> valueTypeRef) throws Exception {
        try {
            return super.readValue(p, valueTypeRef);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public <T> T fromJson(JsonParser p, JavaType valueType) throws Exception {
        try {
            return super.readValue(p, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(File src, Class<T> valueType) throws Exception {
        try {
            return super.readValue(src, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(File src, TypeReference<T> valueTypeRef) throws Exception {
        try {
            return super.readValue(src, valueTypeRef);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(File src, JavaType valueType) throws Exception {
        try {
            return super.readValue(src, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(URL src, Class<T> valueType) throws Exception {
        try {
            return super.readValue(src, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(URL src, TypeReference<T> valueTypeRef) throws Exception {
        try {
            return super.readValue(src, valueTypeRef);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(URL src, JavaType valueType) throws Exception {
        try {
            return super.readValue(src, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(String content, Class<T> valueType) throws Exception {
        try {
            return super.readValue(content, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(String content, TypeReference<T> valueTypeRef) throws Exception {
        try {
            return super.readValue(content, valueTypeRef);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public <T> T fromJson(Map<String, Object> content, Class<T> valueType) throws Exception {
        try {
            return super.convertValue(content, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(String content, JavaType valueType) throws Exception {
        try {
            return super.readValue(content, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(Reader src, Class<T> valueType) throws Exception {
        try {
            return super.readValue(src, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(Reader src, TypeReference<T> valueTypeRef) throws Exception {
        try {
            return super.readValue(src, valueTypeRef);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(Reader src, JavaType valueType) throws Exception {
        try {
            return super.readValue(src, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(InputStream src, Class<T> valueType) throws Exception {
        try {
            return super.readValue(src, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(InputStream src, TypeReference<T> valueTypeRef) throws Exception {
        try {
            return super.readValue(src, valueTypeRef);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(InputStream src, JavaType valueType) throws Exception {
        try {
            return super.readValue(src, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public <T> T fromJson(DataInput src, Class<T> valueType) throws Exception {
        try {
            return super.readValue(src, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T> T fromJson(DataInput src, JavaType valueType) throws Exception {
        try {
            return super.readValue(src, valueType);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

//    public JsonNode readTree(File file) {
//        try {
//            return super.readTree(file);
//        } catch (Exception e) {
//            throw new Exception(e);
//        }
//    }

//    public <T> T fromObj(Object obj, Class<T> valueType) {
//        try {
//            return super.readValue(toJson(obj), valueType);
//        } catch (Exception e) {
//            throw new Exception(e);
//        }
//    }
}
