# SubSeq

SubSeq (short for Sub-Sequence) is a declarative parser framework for scala that allows users to combine 
Parsers together in an expressive syntax allowing you to easily parse many different kinds of data.
SubSeq is not restricted to parsing Strings and Characters, it can be used to parse sequences of any type of token.

### What is a Parser?

In SubSeq, a Parser is something which extends the trait Parses[T, V] but what does that mean? 
In plain english it is something which takes a sequence of tokens and yields a result and the remaining unused tokens.

In SubSeq the series of tokens is represented by a Spliterator which is simply an iterator which can have branches split from it to allow it to iterate over values multiple times.

```Scala
trait Parses[T, V] {
  def parse(input: Spliterator[T]): (V, Spliterator[T])
  ...  
}
```

### Why is that useful?

Formalizing the behavior of a Parser as a generalized trait allows us to represent certain complex parsing behaviors as the combination of simpler Parsers

```Scala
val p = '[' &> SeqOf(DigitParser, ',') &> ']' //is a ParsesOptionally[Char,Seq[Int]]
val y = p.parse("[1,2,3,4]").value            //equals: Some(Seq(1,2,3,4))
val n = p.parse("(1,2,3,4)").value            //equals: None
```


### Types of Parsers

There are three main traits which implement Parses.

 * Consumes[T] extends Parses[T, Null]
 * Asserts[T] extends Parses[T, Boolean]
 * ParsesOptionally[T, V] extends Parses[T, Option[V]]

Each of these defines a subset of all possible Parsers and have different behaviors. 

A Consumer has output of type Null and cannot produce a meaningful value; so their only role is to remove tokens from the input Spliterator.

An Assertion has output of type Boolean and comments on whether or not the input tokens meet some condition.

An Optional Parser has output of type Option[V] and as a result is not guaranteed to produce a value. This is important because almost every parser has requirements the input must meet to parse correctly.

### Combining Parsers

The various Parser subtypes can be combined using the following operations. Each operation has a name and a symbolic operator which may be used interchangeably in your code.

The following examples produce identical results

```Scala
val a = IgnoreLW >> DigitParser
val b = IgnoreLW thenDo DigitParser
val c = IgnoreLW.>>(DigitParser)
val d = IgnoreLW.thenDo(DigitParser)
```

##### thenDo (">>")

The thenDo function connects consumers to other Parsers. A consumer followed by thenDo and any other Parser will result in a new Parser which consumes tokens from the input then executes the other Parser normally. The reverse is also true and 

```Scala
val p = IgnoreLW >> DigitParser               //is a ParsesOptionally[Char,Int]
val y = p.parse(" 2").value                   //equals: Some(2)
val n = p.parse("2").value                    //equals: Some(2)
```

##### andThen ("&>")


The andThen operator connects assertions to other Parsers. Unlike thenDo, which simply allows a Consumer to modify the input Sequence of tokens either before or after another parse is performed, andThen binds together a given Assertion and Parser so that the successful completion of the Parser requires the Assertion to also succeed.

```Scala
val p = '~' &> DigitParser                    //is a ParsesOptionally[Char,Int]
val y = p.parse("~2").value                   //equals: Some(2)
val n = p.parse("2").value                    //equals: None
```

##### orUse ("||")

The orUse operator connects two Assertions or two OptionalParsers. It results in either an Assertion or OptionalParser which will use the second of the two in the event that the first fails.

```Scala
val p = 'A' || 'B'                            //is a Asserts[Char]
val y1 = p.parse("A").value                   //equals: true
val y2 = p.parse("B").value                   //equals: true
val n = p.parse("C").value                    //equals: false
```

#### Concepts and Best Practices

##### Immutability

All parsers should be immutable. This enables us to re-use and recurse parsers in a reliable way
so that we know the parsers we create will always behave as intended

##### Failing Gracefully

Exceptions should be avoided as a way to indicate that a parser has failed under normal circumstances. 
If you are parsing a number but find a word then the preferred behavior would be to define your parser as a ParsesOptionally
and return None instead of throwing an exception.
Exceptions, if used, should be restricted to outcomes which represent errors not bad input.