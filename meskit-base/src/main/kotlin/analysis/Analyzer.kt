package org.yurusanp.meskit.analysis

import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.meskit.parser.SemGuSParser.*
import org.yurusanp.meskit.symtab.SymTab
import org.yurusanp.meskit.symtab.SymTabEntry

class Analyzer(val st: AnalyzerState = AnalyzerState()) : SemGuSBaseVisitor<List<Representation>>() {
  override fun visitDefineFunCommand(ctx: DefineFunCommandContext): List<Representation.FunDef> = let {
    visitFunctionDef(ctx.functionDef())
  }

  override fun visitFunctionDef(ctx: FunctionDefContext): List<Representation.FunDef> = let {
    val funSym: String = ctx.symbol().normalize()
    val funInner: String = st.symMan.curScope.insert(funSym).inner

    // TODO: extract later?
    val params: List<Representation.SortedInner> = ctx.sortedVar().asSequence().map { sortedVarCtx ->
      val varSort: Sort = visitSort(sortedVarCtx.sort()).first()
      val varSym: String = sortedVarCtx.symbol().normalize()
      val varInner: String = st.symMan.curScope.insert(varSym).inner
      Representation.SortedInner(varSort, varInner)
    }.toList()

    val funDec = Representation.FunDec(funInner, params, visitSort(ctx.sort()).first())

    // TODO: terms

    listOf(Representation.FunDef(funDec, TODO()))
  }

  private fun visitSort(ctx: SortContext): List<Sort> =
    visit(ctx).map { it as Sort }

  override fun visitSimpleSort(ctx: SimpleSortContext): List<Sort.Simple> =
    listOf(Sort.Simple(visitIdentifier(ctx.identifier()).first()))

  override fun visitParSort(ctx: ParSortContext): List<Sort.Par> =
    listOf(Sort.Par(visitIdentifier(ctx.identifier()).first(), ctx.sort().flatMap(::visitSort)))

  private fun visitIdentifier(ctx: IdentifierContext): List<Ident> =
    visit(ctx).map { it as Ident }

  override fun visitSymbolIdentifier(ctx: SymbolIdentifierContext): List<Ident.Inner> = let {
    val sym: String = ctx.symbol().normalize()
    val inner: String = st.symMan.curScope.lookup(sym).inner
    listOf(Ident.Inner(inner))
  }

  override fun visitIndexedIdentifier(ctx: IndexedIdentifierContext): List<Ident.Indexed> = let {
    val sym: String = ctx.symbol().normalize()
    val inner: String = st.symMan.curScope.lookup(sym).inner
    val indices: List<Index> = ctx.index().flatMap(::visitIndex)
    listOf(Ident.Indexed(inner, indices))
  }

  private fun visitIndex(ctx: IndexContext): List<Index> =
    visit(ctx).map { it as Index }

  override fun visitNumIndex(ctx: NumIndexContext): List<Index.Num> =
    listOf(Index.Num(ctx.Numeral().symbol.text.toInt()))

  override fun visitSymbolIndex(ctx: SymbolIndexContext): List<Index.Inner> = let {
    val sym: String = ctx.symbol().normalize()
    val inner: String = st.symMan.curScope.lookup(sym).inner
    listOf(Index.Inner(inner))
  }

  override fun visitDeclareTermTypesCommand(ctx: DeclareTermTypesCommandContext): List<Representation.TermTypeDef> = let {
    // TODO: extract later?
    val sortDecs: List<Representation.SortDec> = ctx.sortDec().map { sortDecCtx ->
      val sortSym: String = sortDecCtx.symbol().normalize()
      val sortInner: String = st.symMan.curScope.insert(sortSym).inner
      val sortArity: Int = sortDecCtx.Numeral().symbol.text.toInt()
      Representation.SortDec(sortInner, sortArity)
    }

    sortDecs.asSequence().zip(ctx.termTypeDec().asSequence()).map { (sortDec, termTypeDecCtx) ->
      Representation.TermTypeDef(sortDec, visitTermTypeDec(termTypeDecCtx))
    }.toList()
  }

  override fun visitTermDec(ctx: TermDecContext): List<Representation.Ctor> = let {
    val ctorComponents: List<String> = ctx.symbol().map(SymbolContext::normalize).toList()
    // TODO: later when implementing datatypes, we should generate when inserting in the current scope instead
    // term type constructors are special since there are no surface names for their selectors
    val selInners: List<String> = (1 until ctorComponents.size).map { st.symMan.genInner() }
    val selSymTab: SymTab = SymTab().apply {
      selInners.forEach { selInner ->
        // no surface names for term constructor selectors, so we just use the inner names
        insert(selInner, SymTabEntry(selInner, null))
      }
    }
    val ctorInner: String = st.symMan.curScope.insert(ctorComponents.first(), selSymTab).inner
    val selDecs: List<Representation.SelDec> = ctorComponents.asSequence().drop(1).mapIndexed { i, selSort ->
      Representation.SelDec(st.symMan.curScope.lookup(selSort).inner, selInners[i])
    }.toList()
    listOf(Representation.Ctor(ctorInner, selDecs))
  }

  override fun visitTermTypeDec(ctx: TermTypeDecContext): List<Representation.Ctor> =
    ctx.termDec().flatMap { visitTermDec(it) }
}
