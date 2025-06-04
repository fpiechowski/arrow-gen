package io.github.arrow.gen.example

import arrow.core.raise.effect
import io.github.arrow.gen.example.arrow.genericMemberFunctionEffect
import io.github.arrow.gen.example.arrow.genericMemberFunctionEither
import io.github.arrow.gen.example.arrow.genericMemberFunctionOrRaise
import io.github.arrow.gen.example.arrow.genericMemberFunctionUsingClassTypeParameterEffect
import io.github.arrow.gen.example.arrow.genericMemberFunctionUsingClassTypeParameterEither
import io.github.arrow.gen.example.arrow.genericMemberFunctionUsingClassTypeParameterOrRaise
import io.github.arrow.gen.example.arrow.genericTopLevelFunctionEffect
import io.github.arrow.gen.example.arrow.genericTopLevelFunctionEither
import io.github.arrow.gen.example.arrow.genericTopLevelFunctionOrRaise
import io.github.arrow.gen.example.arrow.memberFunctionEffect
import io.github.arrow.gen.example.arrow.memberFunctionEither
import io.github.arrow.gen.example.arrow.memberFunctionOrRaise
import io.github.arrow.gen.example.arrow.memberFunctionUsingClassTypeParameterEffect
import io.github.arrow.gen.example.arrow.memberFunctionUsingClassTypeParameterEither
import io.github.arrow.gen.example.arrow.memberFunctionUsingClassTypeParameterOrRaise
import io.github.arrow.gen.example.arrow.topLevelFunctionEffect
import io.github.arrow.gen.example.arrow.topLevelFunctionEither
import io.github.arrow.gen.example.arrow.topLevelFunctionOrRaise

fun main() {
    effect {
        topLevelFunctionOrRaise(1)()
        topLevelFunctionEither(1).bind()
        topLevelFunctionEffect(1).bind()

        genericTopLevelFunctionOrRaise(1, GenericContainingClass(1))
        genericTopLevelFunctionEither(1, GenericContainingClass(1))
        genericTopLevelFunctionEffect(1, GenericContainingClass(1))

        with(ContainingClass()) {
            memberFunctionOrRaise(1)()
            memberFunctionEither(1).bind()
            memberFunctionEffect(1).bind()

            genericMemberFunctionOrRaise(1)()
            genericMemberFunctionEither(1).bind()
            genericMemberFunctionEffect(1).bind()
        }

        with(GenericContainingClass(1)) {
            memberFunctionOrRaise(1)()
            memberFunctionEither(1).bind()
            memberFunctionEffect(1).bind()

            memberFunctionUsingClassTypeParameterOrRaise(1)()
            memberFunctionUsingClassTypeParameterEither(1).bind()
            memberFunctionUsingClassTypeParameterEffect(1).bind()

            genericMemberFunctionOrRaise("one")()
            genericMemberFunctionEither("one").bind()
            genericMemberFunctionEffect("one").bind()

            genericMemberFunctionUsingClassTypeParameterOrRaise("one", 1)()
            genericMemberFunctionUsingClassTypeParameterEither("one", 1).bind()
            genericMemberFunctionUsingClassTypeParameterEffect("one", 1).bind()
        }
    }
}

fun topLevelFunction(int: Int) = int

fun <T1 : Any, T2 : GenericContainingClass<T1>> genericTopLevelFunction(t: T1, t2: T2): T2 = t2

class ContainingClass {
    fun memberFunction(int: Int) = int

    fun <T> genericMemberFunction(t: T) = t
}

class GenericContainingClass<T>(
    private val t: T
) {
    fun memberFunction(int: Int) = int

    fun memberFunctionUsingClassTypeParameter(t: T) = t

    fun <T2> genericMemberFunction(t2: T2) = t2

    fun <T2> genericMemberFunctionUsingClassTypeParameter(t2: T2, t: T) = t2
}
