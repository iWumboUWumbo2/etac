package aar226_akc55_ayc62_ahl88.ast;

public interface Value {
    enum ValueType{
        INTLITERAL,
        BOOLLITERAL,
        ARRAYVALUELITERAL
    }
    ValueType getValueType();

}
