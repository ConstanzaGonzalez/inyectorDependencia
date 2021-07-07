package org.utn.alg2.grp3.di;

import org.reflections.Reflections;
import org.utn.alg2.grp3.anotations.Injected;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Factory {

    static Logger logger = Logger.getLogger(Factory.class.getName());

    private static String packageScan = "test.demo";

    public static <T> T getObject(Class<T> clazz) {
        String callerClazzName = getCallerClazzName(Factory.class);
        try {
            Class.forName(callerClazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        T object = build(clazz);
        logger.info("build success " + callerClazzName);

        return object;
    }

    /**
     * Realiza la inyeccion de los fields que tienen @Injected en object
     * @param object que se tienen que inyectar
     * @return no necesita devolver un objeto porque se inyecta todo por reflection
     */
    private static <T> void inject(T object) {
        //obtiene fileds por reflection
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {

            Injected injected = field.getAnnotation(Injected.class);
            if (injected != null) {
                Class<?> fieldClazz = field.getType();
                logger.info("injecting " + injected.implementation() + " --> " + field.getName());

                // TODO chequear injection ciclica
                // Caso particular de recursividad que no se auto-inyecte
                if( field.getType() == object.getClass() ) {
                    throw new RuntimeException("Cyclic Class injection");
                }

                // estaria bueno hacer esto con un strategy
                if (Collection.class.isAssignableFrom(fieldClazz)) {
                    Collection<Object> fieldValue = implementEmptyCollection(field);

                    Class<?> implementationClazz = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    fieldClazz = implementClazzOfInterface(implementationClazz, injected);
                    logger.info("injecting " + injected.count() +" of class " + fieldClazz.getName() + " --> " + field.getName());

                    for (int i = 0; i < injected.count(); i++) {
                        fieldValue.add(build(fieldClazz));
                    }
                    setFieldClazz(object, field, fieldValue);

                } else if (fieldClazz.isArray()) {
                    Object[] fieldValue = (Object[]) Array.newInstance(field.getType().getComponentType(), injected.count());
                    Class<?> implementationClazz = ((Class) field.getGenericType()).getComponentType();
                    fieldClazz = implementClazzOfInterface(implementationClazz, injected);
                    logger.info("injecting " + injected.count() +" of class " + fieldClazz.getName() + " --> " + field.getName());

                    for(int i = 0; i < injected.count(); i++) {
                        fieldValue[i] = build(fieldClazz);
                    }
                    setFieldClazz(object, field, fieldValue);

                } else {
                    if(field.getType().isInterface()) {
                        fieldClazz = implementClazzOfInterface(fieldClazz, injected);
                    }
                    setFieldClazz(object, field, build(fieldClazz));
                }
            }
        }
    }

    /**
     * Realiza la implementacion de una Collection
     * @param field
     * @return
     */
    private static Collection<Object> implementEmptyCollection(Field field) {
        Class<?> clazz = field.getType();

        // implementaciones por default si el field es una interfaz de la herencia Collection
        if (field.getType().isInterface()) {
            if (List.class.isAssignableFrom(field.getType())) {
                clazz = ArrayList.class;
            } else if (Set.class.isAssignableFrom(field.getType())) {
                clazz = HashSet.class;
            }
        }

        try {
            return (Collection<Object>) clazz.getConstructor().newInstance(new Object[] {});
        } catch (Exception e) {
            throw new RuntimeException("Error building a Collection", e);
        } finally {
            logger.info("build success Collection " + clazz.getName());
        }

    }

    /**
     * Encuentra la implementacion de una interfaz que puede ser de un Annotation o automatica
     * @param interfaceClazz interfaz del field
     * @param injected annotation del field
     * @return
     */
    public static Class<?> implementClazzOfInterface(Class<?> interfaceClazz, Injected injected) {
        // implementation del Annotation
        Class<?> clazz = injected.implementation();

        // viene or default Object.class como implementation en la Annotation
        if (injected.implementation() == Object.class) {
            Reflections reflections = new Reflections(interfaceClazz.getPackage().getName());
            // se obtiene implementaciones de la interfaz
            Set<?> implementations = reflections.getSubTypesOf(interfaceClazz);

            if (implementations.size() == 1)
                clazz = (Class<?>) implementations.iterator().next();

            else if (implementations.size() > 1) {
                if (implementations.contains(injected.implementation())) {
                    clazz = injected.implementation();
                } else {
                    throw new RuntimeException("Error too many implementations of " + interfaceClazz.getName());
                }
            }
        }

        return clazz;
    }

    /**
     * Hace la inyeccion por reflection a object de un field con el value
     * @param object objeto que se encuentra la field a inyectar
     * @param field a inyectar
     * @param value
     */
    private static <T> void setFieldClazz(T object, Field field, Object value) {
        //se desactiva el encapsulamiento p hacer el set del field
        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("Error injecting field " + field.getName() + " with " + value.getClass().getName(), e);
        }
        field.setAccessible(false);
    }

    /**
     * Construye un objeto y al final inyecta las dependencias
     * @param clazz del Tipo <T>
     * @return devuelve un objeto de tipo <T> inyectado
     */
    private static <T> T build(Class<T> clazz) {
        logger.info("building " + clazz.getName() + "");
        T object = null;
        try {
            object = clazz.getConstructor().newInstance(new Object[] {});
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("Error building Class: " + clazz.getName());
        }

        inject(object);
        logger.info("build success " + clazz.getName());
        return object;
    }

    public static String getCallerClazzName(final Class<?> clazz) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final String className = clazz.getName();
        boolean classFound = false;
        for (int i = 1; i < stackTrace.length; i++) {
            final StackTraceElement element = stackTrace[i];
            final String callerClassName = element.getClassName();

            // check if class name is the requested class
            if (callerClassName.equals(className)) classFound = true;
            else if (classFound) return callerClassName;
        }
        return null;
    }

}