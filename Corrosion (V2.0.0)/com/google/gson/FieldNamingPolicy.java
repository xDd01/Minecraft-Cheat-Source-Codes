/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.FieldNamingStrategy;
import java.lang.reflect.Field;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum FieldNamingPolicy implements FieldNamingStrategy
{
    IDENTITY{

        public String translateName(Field f2) {
            return f2.getName();
        }
    }
    ,
    UPPER_CAMEL_CASE{

        public String translateName(Field f2) {
            return FieldNamingPolicy.upperCaseFirstLetter(f2.getName());
        }
    }
    ,
    UPPER_CAMEL_CASE_WITH_SPACES{

        public String translateName(Field f2) {
            return FieldNamingPolicy.upperCaseFirstLetter(FieldNamingPolicy.separateCamelCase(f2.getName(), " "));
        }
    }
    ,
    LOWER_CASE_WITH_UNDERSCORES{

        public String translateName(Field f2) {
            return FieldNamingPolicy.separateCamelCase(f2.getName(), "_").toLowerCase();
        }
    }
    ,
    LOWER_CASE_WITH_DASHES{

        public String translateName(Field f2) {
            return FieldNamingPolicy.separateCamelCase(f2.getName(), "-").toLowerCase();
        }
    };


    private static String separateCamelCase(String name, String separator) {
        StringBuilder translation = new StringBuilder();
        for (int i2 = 0; i2 < name.length(); ++i2) {
            char character = name.charAt(i2);
            if (Character.isUpperCase(character) && translation.length() != 0) {
                translation.append(separator);
            }
            translation.append(character);
        }
        return translation.toString();
    }

    private static String upperCaseFirstLetter(String name) {
        StringBuilder fieldNameBuilder = new StringBuilder();
        int index = 0;
        char firstCharacter = name.charAt(index);
        while (index < name.length() - 1 && !Character.isLetter(firstCharacter)) {
            fieldNameBuilder.append(firstCharacter);
            firstCharacter = name.charAt(++index);
        }
        if (index == name.length()) {
            return fieldNameBuilder.toString();
        }
        if (!Character.isUpperCase(firstCharacter)) {
            String modifiedTarget = FieldNamingPolicy.modifyString(Character.toUpperCase(firstCharacter), name, ++index);
            return fieldNameBuilder.append(modifiedTarget).toString();
        }
        return name;
    }

    private static String modifyString(char firstCharacter, String srcString, int indexOfSubstring) {
        return indexOfSubstring < srcString.length() ? firstCharacter + srcString.substring(indexOfSubstring) : String.valueOf(firstCharacter);
    }
}

