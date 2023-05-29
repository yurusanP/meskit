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
    // NOTE: number of occurrences should be n+1
    | ParOpen CMD_DeclareDatatypes ParOpen sortDec+ ParClose ParOpen datatypeDec+ ParClose ParClose
    # declareDatatypesCommand
    | ParOpen CMD_DeclareSort symbol Numeral ParClose
    # declareSortCommand
    // NOTE: number of occurrences should be n+1
    | ParOpen CMD_DeclareTermTypes ParOpen sortDec+ ParClose ParOpen datatypeDec+ ParClose ParClose
    # declareTermTypesCommand
    | ParOpen CMD_DeclareVar symbol sort ParClose
    # declareVarCommand
    | ParOpen CMD_DefineFun functionDef ParClose
    # defineFunCommand
    | ParOpen CMD_DefineFunRec functionDef ParClose
    # defineFunRecCommand
    // NOTE: number of occurrences should be n+1
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
    # simpleDatatypeDec
    | ParOpen GRW_Par ParOpen symbol+ ParClose datatypeDec+ ParClose
    # parDatatypeDec
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
    # refTerm
    | ParOpen qualIdentifier term+ ParClose
    # appTerm
    | ParOpen GRW_Let ParOpen varBinding+ ParClose term ParClose
    # letTerm
    | ParOpen GRW_Forall ParOpen sortedVar+ ParClose term ParClose
    # forallTerm
    | ParOpen GRW_Exists ParOpen sortedVar+ ParClose term ParClose
    # existsTerm
    | ParOpen GRW_Match term ParOpen matchCase+ ParClose ParClose
    # matchTerm
    // NOTE: not needed for now, used in theory declarations
    // | ParOpen GRW_Par ParOpen symbol+ ParClose term ParClose
    // # parTerm
    | ParOpen GRW_Exclamation term attribute+ ParClose
    # attrTerm
    ;

qualIdentifier
    : identifier
    # simpleQual
    | ParOpen GRW_As identifier sort ParClose
    # asQual
    ;

varBinding
    : ParOpen symbol term ParClose
    ;

sortedVar
    : ParOpen symbol sort ParClose
    ;

pattern
    : symbol
    # symbolPattern
    | ParOpen symbol symbol+ ParClose
    # appPattern
    ;

matchCase
    : ParOpen pattern term ParClose
    ;

// sort

sort: identifier
    # simpleSort
    | ParOpen identifier sort+ ParClose
    # parSort
    ;

// attributes

attribute
    : Keyword
    # unitAttr
    | Keyword attributeValue
    # valuedAttr
    ;

attributeValue
    : specConstant
    # literalAttrVal
    | symbol
    # symbolAttrVal
    | ParOpen sexpr* ParClose
    # sexprAttrVal
    ;

sexpr
    : attributeValue
    # attrValSexpr
    | reserved
    # reservedSexpr
    | Keyword
    # keywordSexpr
    ;

// identifiers

identifier
    : symbol
    # symbolIdentifier
    | ParOpen GRW_Underscore symbol index+ ParClose
    # indexedIdentifier
    ;

index
    : Numeral
    # numIndex
    | symbol
    # symbolIndex
    ;

// lexemes

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
    | CMD_SynthFun
    ;
