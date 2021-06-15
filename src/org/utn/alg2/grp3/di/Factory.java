package org.utn.alg2.grp3.di;

//import org.reflections.Reflections;
import org.utn.alg2.grp3.anotations.Injected;

import java.lang.reflect.*;
import java.util.*;


public class Factory {
	
	public static <T> T getObject(Class<T> objectClass) {
		
		System.out.println("Instanciando un objeto de Type '" + objectClass.getSimpleName() + "'");
		T object = createObject(objectClass);

		//Guardo una instancia de objectClass
		System.out.println("-Inyectando dependencias");
		object = injector(object);//Inyectamos dependencias a la instancia
		System.out.println("---Objeto '" + objectClass.getSimpleName() + "' instanciado con exito!");
		return object;
	}
	
	private static <T> T injector(T parentObject){
		//Lista de propiedades
		Field[] campos = parentObject.getClass().getDeclaredFields();

        for (Field campo : campos) {

        	Injected injected = campo.getAnnotation(Injected.class);
        	
            if (injected != null) {
            	Class<?> fieldClass = getFieldClass(campo); //Clase del campo

        		//Error de recursividad si una clase se intenta inyectar a si misma
            	if( fieldClass == parentObject.getClass() ) {
            		throw new IllegalArgumentException("Una clase no puede inyectarse a si misma");
            	}

				Object fieldValue = getObject(fieldClass);
				setField(parentObject, campo, fieldValue);

            }
        }
        
		return parentObject;
	}
	
	//Devuelve la clase a implementar en un campo con interface
	/*private static Class<?> getInterfaceImplementationClass(Class<?> interfaceClass, Injected injected){
		Reflections reflections = new Reflections(interfaceClass.getPackage().getName());
		Set<?> implementations = reflections.getSubTypesOf(interfaceClass);//Todas las clases que implementan la interface
		//System.out.println("---Implementaciones de la interface '" + interfaceClass.getSimpleName() + "': " + implementations );
		
		Class<?> implementationClass = null;//Implementacion a usar. Clase que se va a instanciar en el campo
		//TODO tirar warning si se manda implementation que no existe (exception si existe mas de una)
		//Si existe solo una implementacion usamos esa
		if(implementations.size() == 1)
			implementationClass = (Class<?>) implementations.iterator().next();//Primer item en el set
		//Si existen varias implementaciones, y se paso alguna clase por injected, usamos esa
		else if( implementations.size() > 1 && injected.implementation() != Class.class ) {
			if(implementations.contains(injected.implementation())) 
				implementationClass = injected.implementation();
		}		
		//System.out.println("---La clase a implementar es '" + implementationClass.getSimpleName() + "'");
		
    	return implementationClass;
	}*/
	
	//Devuelve la clase de un campo. Si es coleccion, devuelve la clase parametrizada
	private static Class<?> getFieldClass(Field campo){
		Class<?> fieldClass = campo.getType();
    	Class<?> finalClass = null;

    	/*if(fieldClass.isInterface() ) {
    		finalClass = getInterfaceImplementationClass(fieldClass, campo.getAnnotation(Injected.class));
    	}*/
    	else {
    		finalClass = fieldClass;
    	}
    	return finalClass;
	}
	
	//Setea el campo del objecto con el valor enviado
	private static <T> void setField(T object, Field campo, Object value) {
		campo.setAccessible(true);//Para setear campos con private
		//Seteo el campo del parentObject con el objeto devuelto por el factory
		try {
			campo.set(object, value);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		campo.setAccessible(false);		
	}
	
	//Devuelve instancia de la clase objectClass
	private static <T> T createObject(Class<T> objectClass) {
		T object = null;
		Constructor<T> constructor = null;
		//Guardo el constructor
		try {
			constructor = objectClass.getConstructor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Creo el objecto en base al constructor
		try {
			//El array son los args del constructor
			object = constructor.newInstance(new Object[] {});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;		
	}

}
