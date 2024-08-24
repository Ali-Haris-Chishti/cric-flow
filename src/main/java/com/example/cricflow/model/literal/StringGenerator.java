package com.example.cricflow.model.literal;

import com.example.cricflow.model.*;

import java.lang.reflect.Field;

public class StringGenerator {

    public static String generateObjectString(Object object) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n").append(object.getClass().getName().toUpperCase()).append(" {").append("\n");

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ExcludedFromToString.class)) {
                try {
                    stringBuilder.append("\t")
                            .append(field.getName().toUpperCase())
                            .append(": ")
                            .append((field.get(object) == null)? "null": field.get(object))
                            .append(",\n");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    private final static Class<?> [] customClasses =
            {
                    Ball.class,
                    Ground.class,
                    Inning.class,
                    Match.class,
                    Over.class,
                    Player.class,
                    Team.class,
                    Toss.class,
                    Tournament.class
            };

//    private static boolean isCustomClass(Class<?> clazz) {
//        for (Class<?> customClass : customClasses) {
//            if (customClass == clazz) {
//                return true;
//            }
//        }
//        return false;
//    }
//    private static boolean isCustomListClass(Class<?> clazz) {
//        for (Class<?> customClass : customClasses) {
//            if (clazz == List.class || clazz == Set.class) {
//                return true;
//            }
//        }
//        return false;
//    }
}
