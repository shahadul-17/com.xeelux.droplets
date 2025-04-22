package com.xeelux.droplets.core.utilities;

import com.xeelux.droplets.core.text.JsonSerializer;

import java.util.Map;

public final class ObjectUtilities {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public static Object[] getEmptyObjectArray() { return EMPTY_OBJECT_ARRAY; }

    /**
     * Prepares a map that contains all the attributes of an object.
     * @implNote This method has a serious performance penalty.
     * Performance could be improved using reflection.
     * @param object Object from which the map shall be prepared.
     * @return A map containing all the attributes of an object.
     */
    public static Map<String, Object> toMap(Object object) {
        // first, we need to serialize the object as JSON...
        var json = JsonSerializer.serialize(object);
        // then we shall deserialize the JSON as map...
        Map<String, Object> map = JsonSerializer.deserializeAsMap(json);

        // finally, we shall return the map...
        return map;
    }

    /**
     * Prepares an object (of a specified type) that contains all the attributes of the map.
     * @implNote This method has a serious performance penalty.
     * Performance could be improved using reflection.
     * @param map Map from which the object shall be prepared.
     * @param classOfType Class of the desired object type.
     * @param <Type> Type of the object.
     * @return An object of the specified type containing all the attributes of the map.
     */
    public static <Type> Type fromMap(Map<String, Object> map, Class<Type> classOfType) {
        // first, we need to serialize the map as JSON...
        final var json = JsonSerializer.serialize(map);
        // then we shall deserialize the JSON as the specified class object...
        final Type object = JsonSerializer.deserialize(json, classOfType);

        // finally, we shall return the object...
        return object;
    }
}
