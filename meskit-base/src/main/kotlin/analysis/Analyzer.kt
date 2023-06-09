package org.yurusanp.meskit.analysis

import org.yurusanp.meskit.analysis.AnalyzerResult.Multiple
import org.yurusanp.meskit.analysis.AnalyzerResult.Single
import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.meskit.parser.SemGuSParser
import org.yurusanp.meskit.parser.SemGuSParser.*

class Analyzer(val st: AnalyzerState = AnalyzerState()) : SemGuSBaseVisitor<AnalyzerResult>() {
  // commands

  override fun visitDeclareTermTypesCommand(ctx: DeclareTermTypesCommandContext): Multiple<Representation.TermTypeDef> = let {
    // TODO: extract later?
    val sortDecs: List<Representation.SortDec> = ctx.sortDec().map { sortDecCtx ->
      val sortSym: String = sortDecCtx.symbol().normalize()
      // term-type definition could be recursive
      val sortInner: String = st.symMan.curScope.insertSort(sortSym)
      val sortArity: Int = sortDecCtx.Numeral().symbol.text.toInt()
      Representation.SortDec(sortInner, sortArity)
    }

    val reps: List<Representation.TermTypeDef> = sortDecs.zip(ctx.termTypeDec()) { sortDec, termTypeDecCtx ->
      Representation.TermTypeDef(sortDec, visitTermTypeDec(termTypeDecCtx).reps)
    }.toList()

    Multiple(reps)
  }

  override fun visitDefineFunCommand(ctx: DefineFunCommandContext): Single<Representation.FunDef> =
    visitFunctionDef(ctx.functionDef())

  override fun visitTermDec(ctx: TermDecContext): Single<Representation.Ctor> = let {
    val ctorComponents: List<String> = ctx.symbol().map(SymbolContext::normalize).toList()
    val ctorInner: String = st.symMan.curScope.insertFun(ctorComponents.first())
    val selInners: List<String> = (1 until ctorComponents.size).map {
      // term type constructors are special since there are no surface names for their selectors
      st.symMan.curScope.insertFun(null, ctorInner)
    }

    val selDecs: List<Representation.SelDec> = ctorComponents.asSequence().drop(1).zip(selInners.asSequence()) { selSort, selInner ->
      Representation.SelDec(st.symMan.curScope.lookupSort(selSort), selInner)
    }.toList()

    Single(Representation.Ctor(ctorInner, selDecs))
  }

  override fun visitTermTypeDec(ctx: TermTypeDecContext): Multiple<Representation.Ctor> =
    Multiple(ctx.termDec().map { visitTermDec(it).rep })

  override fun visitFunctionDef(ctx: FunctionDefContext): Single<Representation.FunDef> = let {
    // the parameters could be looked up in the body only
    st.symMan.pushScope()
    // TODO: extract later?
    val params: List<Representation.SortedInner> = ctx.sortedVar().map { sortedVarCtx ->
      visitSortedVar(sortedVarCtx).rep
    }

    // TODO: body
    val body: Term = representTerm(ctx.term())
    st.symMan.popScope()

    val funSym: String = ctx.symbol().normalize()
    // function definition should be non-recursive
    val funInner: String = st.symMan.curScope.insertFun(funSym)

    val funDec = Representation.FunDec(funInner, params, representSort(ctx.sort()))

    Single(Representation.FunDef(funDec, body))
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
    Single(Term.Ref(identifyFun(ctx.qualIdentifier())))

  override fun visitAppTerm(ctx: AppTermContext): Single<Term.App> = let {
    val ident: Ident = identifyFun(ctx.qualIdentifier())
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
      val varSym: String = varBindingCtx.symbol().normalize()
      val inner: String = st.symMan.curScope.insertFun(varSym)
      Representation.Binding(inner, bindingTerm)
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

  override fun visitMatchTerm(ctx: MatchTermContext): Single<Term.Match> = TODO()

  override fun visitAttrTerm(ctx: AttrTermContext): Single<Term> = TODO()

  override fun visitSortedVar(ctx: SortedVarContext): Single<Representation.SortedInner> = let {
    val varSort: Sort = representSort(ctx.sort())
    val varSym: String = ctx.symbol().normalize()
    val varInner: String = st.symMan.curScope.insertFun(varSym)
    Single(Representation.SortedInner(varSort, varInner))
  }

  // sorts

  private fun representSort(ctx: SortContext): Sort = when (ctx) {
    is SimpleSortContext -> visitSimpleSort(ctx).rep
    is ParSortContext -> visitParSort(ctx).rep
    else -> throw GrammarMatchException()
  }

  override fun visitSimpleSort(ctx: SimpleSortContext): Single<Sort.Simple> = let {
    val ident: Ident = identifySort(ctx.identifier())
    Single(Sort.Simple(ident))
  }

  override fun visitParSort(ctx: ParSortContext): Single<Sort.Par> = TODO()

  // identifiers

  private fun identifySort(ctx: IdentifierContext): Ident =
    representIdentifier(ctx, st.symMan.curScope::lookupSort)

  // TODO: deal with qual later
  private fun identifyFun(ctx: QualIdentifierContext): Ident = when (ctx) {
    is SimpleQualContext -> representIdentifier(ctx.identifier(), st.symMan.curScope::lookupFun)
    else -> throw GrammarMatchException()
  }

  private fun representIdentifier(ctx: IdentifierContext, lookup: (String) -> String): Ident = when (ctx) {
    is SymbolIdentifierContext -> {
      val sym: String = ctx.symbol().normalize()
      val inner: String = lookup(sym)
      Ident.Inner(inner)
    }

    is IndexedIdentifierContext -> {
      val sym: String = ctx.symbol().normalize()
      val inner: String = lookup(sym)
      val indices: List<Index> = ctx.index().map { representIndex(it) }
      Ident.Indexed(inner, indices)
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

  override fun visitSymbolIndex(ctx: SymbolIndexContext): Single<Index.Inner> = let {
    val sym: String = ctx.symbol().normalize()
    val inner: String = st.symMan.curScope.lookupIndex(sym)
    Single(Index.Inner(inner))
  }

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
}
