/**
 * SemGuS lexer grammar
 *
 * Based on the following specification:
 * <http://smtlib.cs.uiowa.edu/papers/smt-lib-reference-v2.6-r2021-05-12.pdf>
 * <https://sygus.org/assets/pdf/SyGuS-IF_2.1.pdf>
 * <https://www.semgus.org/res/semgus-lang.pdf>
 *
 * Copyright (c) 2023 Jack Xu <yurusanp@gmail.com>
 */

lexer grammar BaseLexer;

// auxiliary lexical categories

fragment WhiteSpaceChar
    : '\u0009'
    | '\u000A'
    | '\u000D'
    | '\u0020'
    ;

fragment PrintableCharNoAscii
    : '\u0080'..'\uFFFF'
    ;

fragment PrintableChar
    : '\u0020'..'\u007E'
    | PrintableCharNoAscii
    ;

fragment PrintableCharNoDquote
    : '\u0020'..'\u0021'
    | '\u0023'..'\u007E'
    | PrintableCharNoAscii
    ;

fragment PrintableCharNoStick
    : '\u0020' .. '\u005B'
    | '\u005D' .. '\u007B'
    | '\u007D' .. '\u007E'
    | PrintableCharNoAscii
    ;

fragment Digit
    : [0-9]
    ;

fragment Letter
    : [A-Za-z]
    ;

// general reserved words

GRW_Exclamation : '!' ;
GRW_Underscore : '_' ;
GRW_As : 'as' ;
GRW_Let : 'let' ;
GRW_Exists : 'exists' ;
GRW_Forall : 'forall' ;
GRW_Match : 'match' ;
GRW_Par : 'par' ;

GRW_Binary : 'BINARY' ;
GRW_Decimal : 'DECIMAL' ;
GRW_Hexadecimal : 'HEXADECIMAL' ;
GRW_Numeral : 'NUMERAL' ;
GRW_String : 'STRING' ;

GRW_Constant : 'Constant' ;
GRW_Variable : 'Variable' ;

// command reserved words

CMD_Assume : 'assume' ;
CMD_CheckSynth : 'check-synth' ;
CMD_Constraint : 'constraint' ;
CMD_DeclareDatatype : 'declare-datatype' ;
CMD_DeclareDatatypes : 'declare-datatypes' ;
CMD_DeclareSort : 'declare-sort' ;
CMD_DeclareTermTypes : 'declare-term-types' ;
CMD_DeclareVar : 'declare-var' ;
CMD_DefineFun : 'define-fun' ;
CMD_DefineFunRec : 'define-fun-rec' ;
CMD_DefineFunsRec : 'define-funs-rec' ;
CMD_DefineSort : 'define-sort' ;
CMD_Push : 'push' ;
CMD_Pop : 'pop' ;
CMD_Reset : 'reset' ;
CMD_SetInfo : 'set-info' ;
CMD_SetLogic : 'set-logic' ;
CMD_SetOption : 'set-option' ;
CMD_SynthFun : 'synth-fun' ;

// other tokens

ParOpen
    : '('
    ;

ParClose
    : ')'
    ;

Numeral
    : '0'
    | [1-9] Digit*
    ;

Decimal
    : Numeral '.' '0'* Numeral
    ;

Hexadecimal
    : '#x' [0-9A-Fa-f]+
    ;

Binary
    : '#b' [01]+
    ;

String
    : '"' (PrintableCharNoDquote | WhiteSpaceChar | '""')* '"'
    ;

fragment SymbolChar
    : [~!@$%^&*_]
    | '-'
    | [+=<>.?/]
    ;

SimpleSymbol
    : (Letter | SymbolChar) (Digit | Letter | SymbolChar)*
    ;

QuotedSymbol
    : '|' (PrintableCharNoStick | WhiteSpaceChar)* '|'
    ;

Keyword
    : ':' SimpleSymbol
    ;

WS  :  WhiteSpaceChar+ -> skip
    ;
