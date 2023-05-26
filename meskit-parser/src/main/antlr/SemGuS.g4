/**
 * SemGuS parser grammar
 *
 * Based on the following specification:
 * <http://smtlib.cs.uiowa.edu/papers/smt-lib-reference-v2.6-r2021-05-12.pdf>
 * <https://sygus.org/assets/pdf/SyGuS-IF_2.1.pdf>
 * <https://www.semgus.org/res/semgus-lang.pdf>
 *
 * Copyright (c) 2023 Jack Xu <yurusanp@gmail.com>
 */

//parser grammar SemGuS;

grammar SemGuS;

import BaseLexer;

start
    : script EOF
    ;

script
    : command*
    ;

command
    : ParOpen CMD_Assume term ParClose
    | ParOpen CMD_CheckSynth ParClose
    | ParOpen CMD_Constraint term ParClose
    | ParOpen CMD_DeclareDatatype symbol datatype_dec ParClose
    // TODO: number of occurrences should be n+1
    | ParOpen CMD_DeclareDatatypes ParOpen sort_dec+ ParClose ParOpen datatype_dec+ ParClose ParClose
    | ParOpen CMD_DeclareSort symbol Numeral ParClose
    // TODO: number of occurrences should be n+1
    | ParOpen CMD_DeclareTermTypes ParOpen sort_dec+ ParClose ParOpen datatype_dec+ ParClose ParClose
    | ParOpen CMD_DeclareVar symbol sort ParClose
    | ParOpen CMD_DefineFun function_def ParClose
    | ParOpen CMD_DefineFunRec function_def ParClose
    // TODO: number of occurrences should be n+1
    | ParOpen CMD_DefineFunsRec ParOpen function_dec+ ParClose ParOpen term+ ParClose ParClose
    | ParOpen CMD_DefineSort symbol ParOpen symbol* ParClose sort ParClose
    | ParOpen CMD_Push Numeral ParClose
    | ParOpen CMD_Pop Numeral ParClose
    | ParOpen CMD_Reset ParClose
    | ParOpen CMD_SetInfo attribute ParClose
    | ParOpen CMD_SetLogic symbol ParClose
    | ParOpen CMD_SetOption attribute ParClose
    | ParOpen CMD_SynthFun symbol ParOpen sorted_var* ParClose sort ParClose
    ;

sort_dec
    : ParOpen symbol Numeral ParClose
    ;

selector_dec
    : ParOpen symbol sort ParClose
    ;

constructor_dec
    : ParOpen symbol selector_dec* ParClose
    ;

datatype_dec
    : ParOpen constructor_dec+ ParClose
    | ParOpen GRW_Par ParOpen symbol+ ParClose datatype_dec+ ParClose
    ;

function_dec
    : ParOpen symbol ParOpen sorted_var* ParClose sort ParClose
    ;

function_def
    : symbol ParOpen sorted_var* ParClose sort term
    ;

// terms and formulas

term: spec_constant
    | qual_identifier
    | ParOpen qual_identifier term+ ParClose
    | ParOpen GRW_Let ParOpen var_binding+ ParClose term ParClose
    | ParOpen GRW_Forall ParOpen sorted_var+ ParClose term ParClose
    | ParOpen GRW_Exists ParOpen sorted_var+ ParClose term ParClose
    | ParOpen GRW_Match term ParOpen match_case+ ParClose ParClose
    | ParOpen GRW_Par ParOpen symbol+ ParClose term ParClose
    | ParOpen GRW_Exclamation term attribute+ ParClose
    ;


qual_identifier
    : identifier
    | ParOpen GRW_As identifier sort ParClose
    ;

var_binding
    : ParOpen symbol term ParClose
    ;

sorted_var
    : ParOpen symbol sort ParClose
    ;

pattern
    : symbol
    | ParOpen symbol symbol+ ParClose
    ;

match_case
    : ParOpen pattern term ParClose
    ;

// sort

sort: identifier
    | ParOpen identifier sort+ ParClose
    ;

// attributes

attribute
    : Keyword
    | Keyword attribute_value
    ;

attribute_value
    : spec_constant
    | symbol
    | ParOpen s_expr* ParClose
    ;

// identifiers

identifier
    : symbol
    | ParOpen GRW_Underscore symbol index+ ParClose
    ;

index
    : Numeral
    | symbol
    ;

// s-expressions

s_expr
    : spec_constant
    | symbol
    | reserved
    | Keyword
    | ParOpen s_expr* ParClose
    ;

spec_constant
    : Numeral
    | Decimal
    | Hexadecimal
    | Binary
    | String
    ;

symbol
    : SimpleSymbol
    | QuotedSymbol
    ;

reserved
    : GRW_Exclamation
    | GRW_Underscore
    | GRW_As
    | GRW_Let
    | GRW_Exists
    | GRW_Forall
    | GRW_Match
    | GRW_Par
    | GRW_Binary
    | GRW_Decimal
    | GRW_Hexadecimal
    | GRW_Numeral
    | GRW_String
    | GRW_Constant
    | GRW_Variable
    | CMD_Assume
    | CMD_CheckSynth
    | CMD_Constraint
    | CMD_DeclareDatatype
    | CMD_DeclareDatatypes
    | CMD_DeclareSort
    | CMD_DeclareTermTypes
    | CMD_DeclareVar
    | CMD_DefineFun
    | CMD_DefineFunRec
    | CMD_DefineFunsRec
    | CMD_DefineSort
    | CMD_Push
    | CMD_Pop
    | CMD_Reset
    | CMD_SetInfo
    | CMD_SetLogic
    | CMD_SetOption
    ;
