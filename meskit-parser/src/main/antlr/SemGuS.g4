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

grammar SemGuS;

import BaseLexer;

start
    : script EOF
    ;

script
    : command*
    ;

// commands

command
    : ParOpen CMD_Assume term ParClose
    # assumeCommand
    | ParOpen CMD_CheckSynth ParClose
    # checkSynthCommand
    | ParOpen CMD_Constraint term ParClose
    # constraintCommand
    | ParOpen CMD_DeclareDatatype symbol datatypeDec ParClose
    # declareDatatypeCommand
    // TODO: number of occurrences should be n+1
    | ParOpen CMD_DeclareDatatypes ParOpen sortDec+ ParClose ParOpen datatypeDec+ ParClose ParClose
    # declareDatatypesCommand
    | ParOpen CMD_DeclareSort symbol Numeral ParClose
    # declareSortCommand
    // TODO: number of occurrences should be n+1
    | ParOpen CMD_DeclareTermTypes ParOpen sortDec+ ParClose ParOpen datatypeDec+ ParClose ParClose
    # declareTermTypesCommand
    | ParOpen CMD_DeclareVar symbol sort ParClose
    # declareVarCommand
    | ParOpen CMD_DefineFun functionDef ParClose
    # defineFunCommand
    | ParOpen CMD_DefineFunRec functionDef ParClose
    # defineFunRecCommand
    // TODO: number of occurrences should be n+1
    | ParOpen CMD_DefineFunsRec ParOpen functionDec+ ParClose ParOpen term+ ParClose ParClose
    # defineFunsRecCommand
    | ParOpen CMD_DefineSort symbol ParOpen symbol* ParClose sort ParClose
    # defineSortCommand
    | ParOpen CMD_Push Numeral ParClose
    # pushCommand
    | ParOpen CMD_Pop Numeral ParClose
    # popCommand
    | ParOpen CMD_Reset ParClose
    # resetCommand
    | ParOpen CMD_SetInfo attribute ParClose
    # setInfoCommand
    | ParOpen CMD_SetLogic symbol ParClose
    # setLogicCommand
    | ParOpen CMD_SetOption attribute ParClose
    # setOptionCommand
    | ParOpen CMD_SynthFun symbol ParOpen sortedVar* ParClose sort ParClose
    # synthFunCommand
    ;

sortDec
    : ParOpen symbol Numeral ParClose
    ;

selectorDec
    : ParOpen symbol sort ParClose
    ;

constructorDec
    : ParOpen symbol selectorDec* ParClose
    ;

datatypeDec
    : ParOpen constructorDec+ ParClose
    | ParOpen GRW_Par ParOpen symbol+ ParClose datatypeDec+ ParClose
    ;

functionDec
    : ParOpen symbol ParOpen sortedVar* ParClose sort ParClose
    ;

functionDef
    : symbol ParOpen sortedVar* ParClose sort term
    ;

// terms

term: specConstant
    # literalTerm
    | qualIdentifier
    # identifierTerm
    | ParOpen qualIdentifier term+ ParClose
    # applicationTerm
    | ParOpen GRW_Let ParOpen varBinding+ ParClose term ParClose
    # letTerm
    | ParOpen GRW_Forall ParOpen sortedVar+ ParClose term ParClose
    # forallTerm
    | ParOpen GRW_Exists ParOpen sortedVar+ ParClose term ParClose
    # existsTerm
    | ParOpen GRW_Match term ParOpen matchCase+ ParClose ParClose
    # matchTerm
    | ParOpen GRW_Par ParOpen symbol+ ParClose term ParClose
    # parTerm
    | ParOpen GRW_Exclamation term attribute+ ParClose
    # attributeTerm
    ;

qualIdentifier
    : identifier
    | ParOpen GRW_As identifier sort ParClose
    ;

varBinding
    : ParOpen symbol term ParClose
    ;

sortedVar
    : ParOpen symbol sort ParClose
    ;

pattern
    : symbol
    # simplePattern
    | ParOpen symbol symbol+ ParClose
    # compositePattern
    ;

matchCase
    : ParOpen pattern term ParClose
    ;

// sort

sort: identifier
    # simpleSort
    | ParOpen identifier sort+ ParClose
    # parameterizedSort
    ;

// attributes

attribute
    : Keyword
    # unitAttribute
    | Keyword attributeValue
    # valuedAttribute
    ;

attributeValue
    : specConstant
    # literalAttributeValue
    | symbol
    # simpleAttributeValue
    | ParOpen sExpr* ParClose
    # listAttributeValue
    ;

// identifiers

identifier
    : symbol
    # simpleIdentifier
    | ParOpen GRW_Underscore symbol index+ ParClose
    # indexedIdentifier
    ;

index
    : Numeral
    | symbol
    ;

// s-expressions

sExpr
    : specConstant
    | symbol
    | reserved
    | Keyword
    | ParOpen sExpr* ParClose
    ;

specConstant
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
