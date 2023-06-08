package org.yurusanp.meskit.analysis

import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.meskit.parser.SemGuSParser.*
import org.yurusanp.meskit.symtab.SymTab
import org.yurusanp.meskit.symtab.SymTabEntry

class Analyzer(val st: AnalyzerState = AnalyzerState()) : SemGuSBaseVisitor<List<Representation>>() {
  override fun visitDeclareTermTypesCommand(ctx: DeclareTermTypesCommandContext): List<Representation.TermTypeDef> = let {
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
