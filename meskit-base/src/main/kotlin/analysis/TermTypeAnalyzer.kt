package org.yurusanp.meskit.analysis

import org.yurusanp.meskit.parser.SemGuSParser.*
import org.yurusanp.meskit.symtab.SymTab
import org.yurusanp.meskit.symtab.SymTabEntry

// TODO: extract some parts to work with datatypes as well

data class SortDec(val inner: String, val arity: Int)

data class SortedInner(val sortInner: String, val inner: String)

data class Ctor(val inner: String, val selSortedInners: List<SortedInner>)

data class TermType(val sortDec: SortDec, val ctors: List<Ctor>)

fun DeclareTermTypesCommandContext.analyze(st: AnalyzerState): List<TermType> = let { ctx ->
  val sortDecs: List<SortDec> = ctx.sortDec().map { sortDecCtx ->
    val sortSym: String = sortDecCtx.symbol().analyze()
    val sortArity: Int = sortDecCtx.Numeral().symbol.text.toInt()
    val sortInner: String = st.symMan.curScope.insert(sortSym).inner
    SortDec(sortInner, sortArity)
  }

  sortDecs.asSequence().zip(ctx.termTypeDec().asSequence()).map { (sortDec, termTypeDecCtx) ->
    TermType(sortDec, termTypeDecCtx.analyze(st))
  }.toList()
}

fun TermTypeDecContext.analyze(st: AnalyzerState): List<Ctor> =
  termDec().map { it.analyze(st) }.toList()

fun TermDecContext.analyze(st: AnalyzerState): Ctor = let { termDecCtx ->
  val ctorComponents: List<String> = termDecCtx.symbol().map(SymbolContext::analyze).toList()
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
  val selSortedInners: List<SortedInner> = ctorComponents.asSequence().drop(1).mapIndexed { i, selSort ->
    SortedInner(st.symMan.curScope.lookup(selSort).inner, selInners[i])
  }.toList()
  Ctor(ctorInner, selSortedInners)
}
