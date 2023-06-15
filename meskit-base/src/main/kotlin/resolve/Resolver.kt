package org.yurusanp.meskit.resolve

import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.meskit.parser.SemGuSParser
import org.yurusanp.meskit.parser.SemGuSParser.*
import org.yurusanp.meskit.resolve.ResolverResult.Multiple
import org.yurusanp.meskit.resolve.ResolverResult.Single
import org.yurusanp.meskit.surface.*
import org.yurusanp.meskit.symtab.Inner
import org.yurusanp.meskit.symtab.Scope

class Resolver(val st: ResolverState = ResolverState()) : SemGuSBaseVisitor<ResolverResult>() {
  // NOTE: avoid aggregating result of EOF
  override fun visitStart(ctx: StartContext): Multiple<Representation> {
    val reps: List<Representation> = ctx.script().command().flatMap { commandCtx -> representCommand(commandCtx) }
    return Multiple(reps)
  }

  private fun representCommand(ctx: CommandContext): List<Representation> = when (ctx) {
    is DeclareTermTypesCommandContext -> visitDeclareTermTypesCommand(ctx).reps
    is DefineFunCommandContext -> listOf(visitDefineFunCommand(ctx).rep)
    else -> throw GrammarMatchException()
  }

  // commands

  override fun visitDeclareTermTypesCommand(ctx: DeclareTermTypesCommandContext): Multiple<Def.SortFam> = let {
    val termTypeNames: List<Inner> = ctx.sortDec().map { sortDecCtx ->
      val name: Inner = insertSortSymbol(sortDecCtx.symbol())
      val arity: Int = sortDecCtx.Numeral().symbol.text.toInt()
      if (arity != 0) throw ResolverCheckException("Term type arity must be 0")
      name
    }
    val termTypeDefs: List<Def.SortFam> = ctx.termTypeDec().zip(termTypeNames) { termTypeDecCtx, termTypeName ->
      Def.SortFam(termTypeName, visitTermTypeDec(termTypeDecCtx).reps)
    }

    Multiple(termTypeDefs)
  }

  override fun visitDefineFunCommand(ctx: DefineFunCommandContext): Single<Def.FunFam> =
    visitFunctionDef(ctx.functionDef())

  override fun visitTermDec(ctx: TermDecContext): Single<Representation.Ctor> = let {
    val ctorName: Inner = insertFunSymbol(ctx.symbol().first())
    val selNames: List<Inner> = (1 until ctx.symbol().size).map {
      // term type constructors are special since there are no surface names for their selectors
      st.symMan.curScope.insert("fun", null)
    }
    val sels: List<Representation.SortedInner> = ctx.symbol().asSequence().drop(1).zip(selNames.asSequence()) { selSortSymbolCtx, selName ->
      val selSortName: Inner = lookupSortSymbol(selSortSymbolCtx)
      val selSort: Ident.Sort = Ident.Sort(Representation.IndexedInner(selSortName))
      Representation.SortedInner(selName, selSort)
    }.toList()

    Single(Representation.Ctor(ctorName, sels))
  }

  override fun visitTermTypeDec(ctx: TermTypeDecContext): Multiple<Representation.Ctor> =
    Multiple(ctx.termDec().map { visitTermDec(it).rep })

  override fun visitFunctionDef(ctx: FunctionDefContext): Single<Def.FunFam> = let {
    // the input params could be looked up in the output term only
    st.symMan.pushScope()
    val inputs: List<Representation.SortedInner> = ctx.sortedVar().map { sortedVarCtx ->
      visitSortedVar(sortedVarCtx).rep
    }
    val output: Term = representTerm(ctx.term())
    st.symMan.popScope()

    val funName: Inner = insertFunSymbol(ctx.symbol())
    val outputSort: Ident.Sort = representSort(ctx.sort())
    val lambda = Representation.Lambda(inputs, output, outputSort)

    Single(Def.FunFam(funName, lambda))
  }

  // terms

  private fun representTerm(ctx: TermContext): Term = when (ctx) {
    is LiteralTermContext -> visitLiteralTerm(ctx).rep
    is RefTermContext -> visitRefTerm(ctx).rep
    is AppTermContext -> visitAppTerm(ctx).rep
    is LetTermContext -> visitLetTerm(ctx).rep
    is ForallTermContext -> visitForallTerm(ctx).rep
    is ExistsTermContext -> visitExistsTerm(ctx).rep
    is MatchTermContext -> visitMatchTerm(ctx).rep
    is AttrTermContext -> visitAttrTerm(ctx).rep
    else -> throw GrammarMatchException()
  }

  override fun visitLiteralTerm(ctx: LiteralTermContext): Single<Term.Literal> =
    Single(Term.Literal(visitSpecConstant(ctx.specConstant()).rep))

  override fun visitRefTerm(ctx: RefTermContext): Single<Term.Ref> =
    Single(Term.Ref(representQualIdentifier(ctx.qualIdentifier())))

  override fun visitAppTerm(ctx: AppTermContext): Single<Term.App> = let {
    val ident: Ident.Fun = representQualIdentifier(ctx.qualIdentifier())
    val args: List<Term> = ctx.term().map { termCtx -> representTerm(termCtx) }
    Single(Term.App(ident, args))
  }

  override fun visitLetTerm(ctx: LetTermContext): Single<Term.Let> = let {
    // let bindings should be non-recursive and parallel
    val bindingTerms: List<Term> = ctx.varBinding().map { varBindingCtx ->
      representTerm(varBindingCtx.term())
    }

    // the bindings could be looked up in the body only
    st.symMan.pushScope()
    val bindings: List<Representation.Binding> = ctx.varBinding().zip(bindingTerms) { varBindingCtx, bindingTerm ->
      val name: Inner = insertFunSymbol(varBindingCtx.symbol())
      Representation.Binding(name, bindingTerm)
    }
    val body: Term = representTerm(ctx.term())
    st.symMan.popScope()

    Single(Term.Let(bindings, body))
  }

  override fun visitForallTerm(ctx: ForallTermContext): Single<Term.Forall> = let {
    // the parameters could be looked up in the body only
    st.symMan.pushScope()
    val params: List<Representation.SortedInner> = ctx.sortedVar().map { sortedVarCtx ->
      visitSortedVar(sortedVarCtx).rep
    }
    val body: Term = representTerm(ctx.term())
    st.symMan.popScope()

    Single(Term.Forall(params, body))
  }

  override fun visitExistsTerm(ctx: ExistsTermContext): Single<Term.Exists> = let {
    // the parameters could be looked up in the body only
    st.symMan.pushScope()
    val params: List<Representation.SortedInner> = ctx.sortedVar().map { sortedVarCtx ->
      visitSortedVar(sortedVarCtx).rep
    }
    val body: Term = representTerm(ctx.term())
    st.symMan.popScope()

    Single(Term.Exists(params, body))
  }

  override fun visitMatchTerm(ctx: MatchTermContext): Single<Term.Match> = let {
    val matchTerm: Term = representTerm(ctx.term())
    val cases: List<Representation.Case> = ctx.matchCase().map { matchCaseCtx ->
      visitMatchCase(matchCaseCtx).rep
    }
    Single(Term.Match(matchTerm, cases))
  }

  override fun visitAttrTerm(ctx: AttrTermContext): Single<Term> = let {
    val attrs: Map<String, AttrVal> = ctx.attribute().asSequence().map { attributeCtx ->
      when (val keyword: String = attributeCtx.Keyword().text) {
        ":input", ":output" -> keyword to representIOAttrVal(attributeCtx.attributeValue())
        else -> throw GrammarMatchException()
      }
    }.toMap()
    val newTerm = representTerm(ctx.term()).also { term -> term.attrs = attrs }
    Single(newTerm)
  }

  private fun representQualIdentifier(ctx: QualIdentifierContext): Ident.Fun = when (ctx) {
    is SimpleQualContext -> visitSimpleQual(ctx).rep
    is AsQualContext -> visitAsQual(ctx).rep
    else -> throw GrammarMatchException()
  }

  override fun visitSimpleQual(ctx: SimpleQualContext): Single<Ident.Fun> = let {
    val indexed: Representation.IndexedInner = identifyFun(ctx.identifier())
    Single(Ident.Fun(indexed))
  }

  override fun visitAsQual(ctx: AsQualContext): Single<Ident.Fun> = let {
    val indexed: Representation.IndexedInner = identifyFun(ctx.identifier())
    val qual: Ident.Sort = representSort(ctx.sort())
    Single(Ident.Fun(indexed, qual))
  }

  override fun visitSortedVar(ctx: SortedVarContext): Single<Representation.SortedInner> = let {
    val varName: Inner = insertFunSymbol(ctx.symbol())
    val varSort: Ident.Sort = representSort(ctx.sort())
    Single(Representation.SortedInner(varName, varSort))
  }

  override fun visitMatchCase(ctx: MatchCaseContext): Single<Representation.Case> = let {
    val patternSymbolCtxs: List<SymbolContext> = when (val patternCtx: PatternContext = ctx.pattern()) {
      is SymbolPatternContext -> listOf(patternCtx.symbol())
      is AppPatternContext -> patternCtx.symbol()
      else -> throw GrammarMatchException()
    }

    val ctorName: Inner = lookupFunSymbol(patternSymbolCtxs.first())

    // the pattern parameters could be looked up in the body only
    st.symMan.pushScope()
    val params: List<Inner> = patternSymbolCtxs.asSequence().drop(1).map { paramSymbolCtx ->
      insertFunSymbol(paramSymbolCtx)
    }.toList()
    val body: Term = representTerm(ctx.term())
    st.symMan.popScope()

    Single(Representation.Case(ctorName, params, body))
  }

  // sorts

  private fun representSort(ctx: SortContext): Ident.Sort = when (ctx) {
    is SimpleSortContext -> visitSimpleSort(ctx).rep
    is ParSortContext -> visitParSort(ctx).rep
    else -> throw GrammarMatchException()
  }

  override fun visitSimpleSort(ctx: SimpleSortContext): Single<Ident.Sort> = let {
    val indexed: Representation.IndexedInner = identifySort(ctx.identifier())
    Single(Ident.Sort(indexed))
  }

  override fun visitParSort(ctx: ParSortContext): Single<Ident.Sort> = let {
    val indexed: Representation.IndexedInner = identifySort(ctx.identifier())
    val args: List<Ident.Sort> = ctx.sort().map { sortCtx -> representSort(sortCtx) }
    Single(Ident.Sort(indexed, args))
  }

  // attributes

  private fun representIOAttrVal(ctx: AttributeValueContext): AttrVal.Composite = let {
    if (ctx !is SexprAttrValContext) throw GrammarMatchException()

    val attrVals: List<AttrVal> = ctx.sexpr().map { sexprCtx ->
      val attrValSexprCtx: AttrValSexprContext = sexprCtx
        as? AttrValSexprContext ?: throw GrammarMatchException()

      val symbolAttrValCtx: SymbolAttrValContext = attrValSexprCtx.attributeValue()
        as? SymbolAttrValContext ?: throw GrammarMatchException()

      val name: Inner = lookupFunSymbol(symbolAttrValCtx.symbol())
      AttrVal.Symbol(name)
    }

    AttrVal.Composite(attrVals)
  }

  // identifiers

  private fun identifySort(ctx: IdentifierContext): Representation.IndexedInner =
    representIdentifier(ctx) { symbolCtx -> lookupSortSymbol(symbolCtx) }

  private fun identifyFun(ctx: IdentifierContext): Representation.IndexedInner =
    representIdentifier(ctx) { symbolCtx -> lookupFunSymbol(symbolCtx) }

  private fun representIdentifier(ctx: IdentifierContext, lookup: (SymbolContext) -> Inner): Representation.IndexedInner = when (ctx) {
    is SymbolIdentifierContext -> {
      val name: Inner = lookup(ctx.symbol())
      Representation.IndexedInner(name)
    }

    is IndexedIdentifierContext -> {
      val name: Inner = lookup(ctx.symbol())
      val indices: List<Index> = ctx.index().map { representIndex(it) }
      Representation.IndexedInner(name, indices)
    }

    else -> throw GrammarMatchException()
  }

  private fun representIndex(ctx: IndexContext): Index = when (ctx) {
    is NumIndexContext -> visitNumIndex(ctx).rep
    is SymbolIndexContext -> visitSymbolIndex(ctx).rep
    else -> throw GrammarMatchException()
  }

  override fun visitNumIndex(ctx: NumIndexContext): Single<Index.Num> =
    Single(Index.Num(ctx.Numeral().symbol.text.toInt()))

  override fun visitSymbolIndex(ctx: SymbolIndexContext): Single<Index.Str> =
    Single(Index.Str(normalizeSymbol(ctx.symbol())))

  // lexemes

  override fun visitSpecConstant(ctx: SpecConstantContext): Single<SpecConst> = ctx.childTerminalNode(0).let {
    val mesSpecConst: SpecConst = when (it.symbol.type) {
      Numeral -> SpecConst.Num(it.symbol.text.toInt())
      Decimal -> SpecConst.Dec(it.symbol.text.toDouble())
      Hexadecimal -> SpecConst.Hex(it.symbol.text)
      Binary -> SpecConst.Bin(it.symbol.text)
      SemGuSParser.String -> SpecConst.Str(it.symbol.text)
      else -> throw GrammarMatchException()
    }
    Single(mesSpecConst)
  }

  /**
   * Normalizes a symbol, removing sticks that surrounds the quoted symbol.
   *
   * NOTE: a quoted symbol can contain whitespace characters, including newlines.
   */
  private fun normalizeSymbol(ctx: SymbolContext): String = ctx.childTerminalNode(0).let { sym ->
    when (sym.symbol.type) {
      SimpleSymbol -> sym.text
      QuotedSymbol -> sym.text.substring(1, sym.text.length - 1)
      else -> throw GrammarMatchException()
    }
  }

  private fun insertFunSymbol(ctx: SymbolContext, scope: Scope = st.symMan.curScope): Inner =
    scope.insert("fun", normalizeSymbol(ctx))

  private fun insertSortSymbol(ctx: SymbolContext, scope: Scope = st.symMan.curScope): Inner =
    scope.insert("sort", normalizeSymbol(ctx))

  private fun lookupFunSymbol(ctx: SymbolContext, scope: Scope = st.symMan.curScope): Inner =
    scope.lookup("fun", normalizeSymbol(ctx))

  private fun lookupSortSymbol(ctx: SymbolContext, scope: Scope = st.symMan.curScope): Inner =
    scope.lookup("sort", normalizeSymbol(ctx))
}
