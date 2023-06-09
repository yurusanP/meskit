package org.yurusanp.meskit.analysis

import org.yurusanp.meskit.analysis.AnalyzerResult.Multiple
import org.yurusanp.meskit.analysis.AnalyzerResult.Single
import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.meskit.parser.SemGuSParser.*

class Analyzer(val st: AnalyzerState = AnalyzerState()) : SemGuSBaseVisitor<AnalyzerResult>() {
  override fun visitDefineFunCommand(ctx: DefineFunCommandContext): Single<Representation.FunDef> = let {
    visitFunctionDef(ctx.functionDef())
  }

  override fun visitFunctionDef(ctx: FunctionDefContext): Single<Representation.FunDef> = let {
    val funSym: String = ctx.symbol().normalize()
    val funInner: String = st.symMan.curScope.insertFun(funSym)

    // TODO: extract later?
    val params: List<Representation.SortedInner> = ctx.sortedVar().asSequence().map { sortedVarCtx ->
      val varSort: Sort = representSort(sortedVarCtx.sort())
      val varSym: String = sortedVarCtx.symbol().normalize()
      val varInner: String = st.symMan.curScope.insertFun(varSym)
      Representation.SortedInner(varSort, varInner)
    }.toList()

    val funDec = Representation.FunDec(funInner, params, representSort(ctx.sort()))

    // TODO: terms

    Single(Representation.FunDef(funDec, TODO()))
  }

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

  private fun identifySort(ctx: IdentifierContext): Ident = when (ctx) {
    is SymbolIdentifierContext -> {
      val sym: String = ctx.symbol().normalize()
      val inner: String = st.symMan.curScope.lookupSort(sym)
      Ident.Inner(inner)
    }

    is IndexedIdentifierContext -> {
      val sym: String = ctx.symbol().normalize()
      val inner: String = st.symMan.curScope.lookupSort(sym)
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

  override fun visitDeclareTermTypesCommand(ctx: DeclareTermTypesCommandContext): Multiple<Representation.TermTypeDef> = let {
    // TODO: extract later?
    val sortDecs: List<Representation.SortDec> = ctx.sortDec().map { sortDecCtx ->
      val sortSym: String = sortDecCtx.symbol().normalize()
      val sortInner: String = st.symMan.curScope.insertSort(sortSym)
      val sortArity: Int = sortDecCtx.Numeral().symbol.text.toInt()
      Representation.SortDec(sortInner, sortArity)
    }

    val reps: List<Representation.TermTypeDef> = sortDecs.asSequence().zip(ctx.termTypeDec().asSequence()).map { (sortDec, termTypeDecCtx) ->
      Representation.TermTypeDef(sortDec, visitTermTypeDec(termTypeDecCtx).reps)
    }.toList()

    Multiple(reps)
  }

  override fun visitTermDec(ctx: TermDecContext): Single<Representation.Ctor> = let {
    val ctorComponents: List<String> = ctx.symbol().map(SymbolContext::normalize).toList()
    val ctorInner: String = st.symMan.curScope.insertFun(ctorComponents.first())
    val selInners: List<String> = (1 until ctorComponents.size).map {
      // term type constructors are special since there are no surface names for their selectors
      st.symMan.curScope.insertFun(null, ctorInner)
    }

    val selDecs: List<Representation.SelDec> = ctorComponents.asSequence().drop(1).mapIndexed { i, selSort ->
      Representation.SelDec(st.symMan.curScope.lookupSort(selSort), selInners[i])
    }.toList()

    Single(Representation.Ctor(ctorInner, selDecs))
  }

  override fun visitTermTypeDec(ctx: TermTypeDecContext): Multiple<Representation.Ctor> =
    Multiple(ctx.termDec().map { visitTermDec(it).rep })
}
