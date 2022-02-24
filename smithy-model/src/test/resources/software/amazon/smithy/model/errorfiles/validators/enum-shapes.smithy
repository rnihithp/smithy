$version: "2.0"

namespace ns.foo

enum StringEnum {
    IMPLICIT_VALUE

    @enumValue("explicit")
    EXPLICIT_VALUE

    @enumValue("")
    EMPTY_STRING

    @enumDefault
    DEFAULT_VALUE

    @enumValue(1)
    INT_VALUE

    @enumValue("explicit")
    DUPLICATE_VALUE

    undesirableName
}

@enum([{
    name: "FOO"
    value: "FOO"
}])
enum EnumWithEnumTrait {
    BAR
}

enum MultipleDefaults {
    @enumDefault
    DEFAULT1

    @enumDefault
    DEFAULT2
}

enum DefaultWithExplicitValue {
    @enumDefault
    @enumValue("foo")
    DEFAULT
}

intEnum IntEnum {
    IMPLICIT_VALUE

    @enumValue(1)
    EXPLICIT_VALUE

    @enumValue(0)
    ZERO

    @enumDefault
    DEFAULT_VALUE

    @enumValue("foo")
    STRING_VALUE

    @enumValue(1)
    DUPLICATE_VALUE

    @enumValue(99)
    undesirableName
}
