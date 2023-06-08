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
    : '(' CMD_Assume term ')'
    # assumeCommand
    | '(' CMD_CheckSynth ')'
    # checkSynthCommand
    | '(' CMD_Constraint term ')'
    # constraintCommand
    | '(' CMD_DeclareDatatype symbol datatypeDec ')'
    # declareDatatypeCommand
    // NOTE: number of occurrences should be n+1
    | '(' CMD_DeclareDatatypes '(' sortDec+ ')' '(' datatypeDec+ ')' ')'
    # declareDatatypesCommand
    | '(' CMD_DeclareSort symbol Numeral ')'
    # declareSortCommand
    // NOTE: number of occurrences should be n+1
    | '(' CMD_DeclareTermTypes '(' sortDec+ ')' '(' termTypeDec+ ')' ')'
    # declareTermTypesCommand
    | '(' CMD_DeclareVar symbol sort ')'
    # declareVarCommand
    | '(' CMD_DefineFun functionDef ')'
    # defineFunCommand
    | '(' CMD_DefineFunRec functionDef ')'
    # defineFunRecCommand
    // NOTE: number of occurrences should be n+1
    | '(' CMD_DefineFunsRec '(' functionDec+ ')' '(' term+ ')' ')'
    # defineFunsRecCommand
    | '(' CMD_DefineSort symbol '(' symbol* ')' sort ')'
    # defineSortCommand
    | '(' CMD_Push Numeral ')'
    # pushCommand
    | '(' CMD_Pop Numeral ')'
    # popCommand
    | '(' CMD_Reset ')'
    # resetCommand
    | '(' CMD_SetInfo attribute ')'
    # setInfoCommand
    | '(' CMD_SetLogic symbol ')'
    # setLogicCommand
    | '(' CMD_SetOption attribute ')'
    # setOptionCommand
    | '(' CMD_SynthFun symbol '(' sortedVar* ')' sort ')'
    # synthFunCommand
    ;

sortDec
    : '(' symbol Numeral ')'
    ;

selectorDec
    : '(' symbol sort ')'
    ;

constructorDec
    : '(' symbol selectorDec* ')'
    ;

datatypeDec
    : '(' constructorDec+ ')'
    # simpleDatatypeDec
    | '(' GRW_Par '(' symbol+ ')' datatypeDec+ ')'
    # parDatatypeDec
    ;

termDec
    : '(' symbol+ ')'
    ;

termTypeDec
    : '(' termDec+ ')'
    ;

functionDec
    : '(' symbol '(' sortedVar* ')' sort ')'
    ;

functionDef
    : symbol '(' sortedVar* ')' sort term
    ;

// terms

term: specConstant
    # literalTerm
    | qualIdentifier
    # refTerm
    | '(' qualIdentifier term+ ')'
    # appTerm
    | '(' GRW_Let '(' varBinding+ ')' term ')'
    # letTerm
    | '(' GRW_Forall '(' sortedVar+ ')' term ')'
    # forallTerm
    | '(' GRW_Exists '(' sortedVar+ ')' term ')'
    # existsTerm
    | '(' GRW_Match term '(' matchCase+ ')' ')'
    # matchTerm
    // NOTE: not needed for now, used in theory declarations
    // | '(' GRW_Par '(' symbol+ ')' term ')'
    // # parTerm
    | '(' GRW_Exclamation term attribute+ ')'
    # attrTerm
    ;

qualIdentifier
    : identifier
    # simpleQual
    | '(' GRW_As identifier sort ')'
    # asQual
    ;

varBinding
    : '(' symbol term ')'
    ;

sortedVar
    : '(' symbol sort ')'
    ;

pattern
    : symbol
    # symbolPattern
    | '(' symbol symbol+ ')'
    # appPattern
    ;

matchCase
    : '(' pattern term ')'
    ;

// sort

sort: identifier
    # simpleSort
    | '(' identifier sort+ ')'
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
    | '(' sexpr* ')'
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
    | '(' GRW_Underscore symbol index+ ')'
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
