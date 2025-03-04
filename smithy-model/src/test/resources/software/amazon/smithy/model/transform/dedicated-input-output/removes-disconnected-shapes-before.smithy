$version: "1.0"

namespace smithy.example

operation GetFoo {
    input: GetFooData,
    output: Foo
}

structure GetFooData {
    @required
    id: String
}

structure Foo {
    @required
    id: String,

    @required
    createdAt: Timestamp
}
