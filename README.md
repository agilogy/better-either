# Better Either

Better Either is a set of experimental micro-libraries to work with Either from the standard library.

It currently has only 2 projects:

- eitherSyntax: A backport of the 2.12 right biased Either methods to Scala 2.10 and 2.11 (via pimp my library)
- eitherExtras: Some more extra Either methods + helper functions to use Either as a full validation solution

## Installation

```
resolvers += Resolver.url("Agilogy Scala",url("http://dl.bintray.com/agilogy/scala/"))(Resolver.ivyStylePatterns)

libraryDependencies += "com.agilogy" %% "either-syntax" % "0.2"
libraryDependencies += "com.agilogy" %% "either-extras" % "0.2"
```

## Motivation

Both Scalaz and Cats have replacements for Either (\/ and Xor). The main motivation for those alternative Eithers is
that Either is not right biased. But right bias came with Scala 2.12, which makes coupling to specific ScalaZ or
Cats result types less appealing.

### EitherSyntax

EitherSyntax is a very small library providing the exact same methods that came with Scala 2.12 Either. Its a backport
of the right bias of Either to scala 2.10 and 2.11.

If you are already using eitherSyntax and want to switch to Scala 2.12 , it should be just a matter of removing
the dependency to eitherSyntax and all its imports (at least at source code level, no binary compatibility intended).

### EitherExtras

On the other hand, both Scalaz and Cats have specific data types for validation with error accumulation (Validation and
Validated). But coupling some (small) libraries or projects to Scalaz or Cats just to return accumulated errors may be
overkill.

EitherExtras provides some additional methods to either plus some helper functions to provide (part of) the features of
Scalaz's Validation and Cats Validated without any other dependency (a part from eitherSyntax).

## Examples

- [EitherSyntax](either-syntax/src/test/scala/com/agilogy/either/EitherSyntaxTest.scala)
- [EitherExtras syntax](either-extras/src/test/scala/com/agilogy/either/extras/EitherExtrasSyntaxTest.scala)
- [EitherExtras additional functions](either-extras/src/test/scala/com/agilogy/either/extras/EitherExtrasTest.scala)

## Inspiration

- Scalaz's \/ and Validation
- Cats Xor and Validated
- [Cats issue #1192 discussion](https://github.com/typelevel/cats/issues/1192)
- [Scala Right-bias Either #5135 pull request](https://github.com/scala/scala/pull/5135)
- [Rob Dickens scala-either-extras](https://github.com/robcd/scala-either-extras)

## Copyright and License

All code is available under the MIT license, available at http://opensource.org/licenses/mit-license.php.
The design is informed by the sources listed in Inspiration.

Copyright the maintainers, 2016.
