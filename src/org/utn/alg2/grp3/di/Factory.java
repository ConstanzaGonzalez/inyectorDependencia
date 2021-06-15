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
        logger.info("build success '" + callerClazzName + "'");

        return object;
    }


    /**
     * Realiza la inyeccion de los fields que tienen @Injected en object
     * @param object que se tienen que inyectar
     * @return no necesita devolver un objeto porque se inyecta todo por reflection
     */
    private static <T> void inject(T object){
        //obtiene fileds por reflection
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {

            Injected injected = field.getAnnotation(Injected.class);
            if (injected != null) {
                Class<?> fieldClazz = getFieldClazz(field);
                logger.info("injecting '" + injected.implementation() + " --> " + field.getName() + "'");

                // TODO chequear injection ciclica
                Object fieldValue = build(fieldClazz);
                setFieldClazz(object, field, fieldValue);
            }
        }
    }

    private static Class<?> getImplementationClazz(Class<?> interfaceClazz, Injected injected) throws Exception {
        Reflections reflections = new Reflections(interfaceClazz.getPackage().getName());
        Set<?> implementations = reflections.getSubTypesOf(interfaceClazz); //Todas las clases que implementan la interface
        //System.out.println("---Implementaciones de la interface '" + interfaceClazz.getSimpleName() + "': " + implementations );

        Class<?> implementationClass = null;//Implementacion a usar. Clase que se va a instanciar en el campo
        //TODO tirar warning si se manda implementation que no existe (exception si existe mas de una)
        //Si existe solo una implementacion usamos esa
        if(implementations.size() == 1)
            implementationClass = (Class<?>) implementations.iterator().next();//Primer item en el set
            //Si existen varias implementaciones, y se paso alguna clase por injected, usamos esa
        else if( implementations.size() > 1 && injected.implementation() != Class.class ) {
            if(implementations.contains(injected.implementation())) {
                implementationClass = injected.implementation();
            } else {
                throw new Exception("Error implemantation");
            }
        }
        //System.out.println("---La clase a implementar es '" + implementationClass.getSimpleName() + "'");

        return implementationClass;
    }

    private static Class<?> getFieldClazz(Field field) {
        Class<?> fieldClazz = field.getType();

        if(fieldClazz.isInterface() ) {
            try {
                fieldClazz = getImplementationClazz(fieldClazz, field.getAnnotation(Injected.class));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "problems injecting member '" + field.getName() + "'", e);
            }
        }

        return fieldClazz;
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        field.setAccessible(false);
    }


    /**
     * Construye un objeto y al final inyecta las dependencias
     * @param clazz del Tipo <T>
     * @return devuelve un objeto de tipo <T> inyectado
     */
    private static <T> T build(Class<T> clazz) {
        logger.info("building '" + clazz.getSimpleName() + "'");
        T object = null;
        try {
            object = clazz.getConstructor().newInstance(new Object[] {});
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        inject(object);
        logger.info("build success '" + clazz.getSimpleName() + "'");
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