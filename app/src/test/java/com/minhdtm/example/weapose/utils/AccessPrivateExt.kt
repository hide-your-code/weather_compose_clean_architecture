package com.minhdtm.example.weapose.utils

import kotlin.reflect.KProperty1
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun <R> Any.callPrivateFunction(methodName: String, vararg args: Any?): R {
    val privateMethod = this::class.functions.firstOrNull { function ->
        function.name == methodName
    }

    val argumentList = args.toMutableList()
    argumentList.add(0, this)

    if (privateMethod != null) {
        privateMethod.isAccessible = true
        return privateMethod.call(*argumentList.toTypedArray()) as R
    } else {
        throw NoSuchMethodException("Method $methodName does not exist in ${this::class.qualifiedName}")
    }
}

fun <R> Any.getPrivateProperty(propertyName: String): R {
    val privateProperty = this::class.memberProperties.firstOrNull { property ->
        property.name == propertyName
    } as? KProperty1<Any, *>

    if (privateProperty != null) {
        privateProperty.isAccessible = true
        return privateProperty.get(this) as R
    } else {
        throw NoSuchFieldException("Field $propertyName does not exist in ${this::class.qualifiedName}")
    }
}
