package es.javimolla.city.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import es.javimolla.city.entity.Feature;

public class EntityUtils {
    public static String getTable(Feature feature) {
        return feature.getClass().getAnnotation(Table.class).name();
    }

    public static List<String> getPropertyFields(Class<?> cls) {
        List<String> propertyFields = new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Transient.class)) {
                Column c = field.getAnnotation(Column.class);
                if (c != null) {
                    if (StringUtils.isNotBlank(c.name())) {
                        propertyFields.add(c.name());
                    } else {
                        propertyFields.add(field.getName());
                    }
                }
            }
        }

        return propertyFields;
    }

    public static void fillEntity(Object entity, List<String> fieldsNames, Object[] fieldsValues) throws Exception {
        for (int i = 0; i < fieldsNames.size(); i++) {
            setProperty(entity, fieldsNames.get(i), fieldsValues[i]);
        }
    }

    public static void setProperty(Object entity, String fieldName, Object fieldValue) throws Exception {
        Field field = getField(entity.getClass(), fieldName);
        if (field != null) {
            BeanUtils.setProperty(entity, field.getName(), fieldValue);
        }
    }

    private static Field getField(Class<?> cls, String fieldName) throws Exception {
        Field f = null;
        try {
            f = cls.getDeclaredField(fieldName);
        } catch (Exception e) {
        }
        if (f != null)
            return f;

        for (Field field : cls.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.name() != null && column.name().equalsIgnoreCase(fieldName)) {
                return field;
            }
        }

        if (cls.getSuperclass() != null) {
            return getField(cls.getSuperclass(), fieldName);
        }

        return null;
    }
}