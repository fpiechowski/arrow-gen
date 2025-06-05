package io.github.fpiechowski.arrowgen.example

import arrow.core.raise.effect
import io.github.fpiechowski.arrowgen.example.arrow.genericMemberFunctionEffect
import io.github.fpiechowski.arrowgen.example.arrow.genericMemberFunctionEither
import io.github.fpiechowski.arrowgen.example.arrow.genericMemberFunctionOrRaise
import io.github.fpiechowski.arrowgen.example.arrow.genericMemberFunctionUsingClassTypeParameterEffect
import io.github.fpiechowski.arrowgen.example.arrow.genericMemberFunctionUsingClassTypeParameterEither
import io.github.fpiechowski.arrowgen.example.arrow.genericMemberFunctionUsingClassTypeParameterOrRaise
import io.github.fpiechowski.arrowgen.example.arrow.genericTopLevelFunctionEffect
import io.github.fpiechowski.arrowgen.example.arrow.genericTopLevelFunctionEither
import io.github.fpiechowski.arrowgen.example.arrow.genericTopLevelFunctionOrRaise
import io.github.fpiechowski.arrowgen.example.arrow.memberFunctionEffect
import io.github.fpiechowski.arrowgen.example.arrow.memberFunctionEither
import io.github.fpiechowski.arrowgen.example.arrow.memberFunctionOrRaise
import io.github.fpiechowski.arrowgen.example.arrow.memberFunctionUsingClassTypeParameterEffect
import io.github.fpiechowski.arrowgen.example.arrow.memberFunctionUsingClassTypeParameterEither
import io.github.fpiechowski.arrowgen.example.arrow.memberFunctionUsingClassTypeParameterOrRaise
import io.github.fpiechowski.arrowgen.example.arrow.topLevelFunctionEffect
import io.github.fpiechowski.arrowgen.example.arrow.topLevelFunctionEither
import io.github.fpiechowski.arrowgen.example.arrow.topLevelFunctionOrRaise

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

fun <T1 : Any, T2 : GenericContainingClass<T1>> genericTopLevelFunction(
    t: T1,
    t2: T2,
): T2 = t2

fun <T2> genericTopLevelFunctionWithInnerTypeParameter(t2: GenericContainingClass<T2>): T2 = t2.t

class ContainingClass {
    fun memberFunction(int: Int) = int

    fun <T> genericMemberFunction(t: T) = t
}

class GenericContainingClass<T>(
    val t: T,
) {
    fun memberFunction(int: Int) = int

    fun memberFunctionUsingClassTypeParameter(t: T) = t

    fun <T2> genericMemberFunction(t2: T2) = t2

    fun <T2> genericMemberFunctionUsingClassTypeParameter(
        t2: T2,
        t: T,
    ) = t2

    fun memberFunctionWithInnerTypeParameter(t2: GenericContainingClass<T>): T = t2.t
}

fun excludedTopLevelFunction(int: Int) = int
