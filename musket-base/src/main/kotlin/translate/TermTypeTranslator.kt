package org.yurusanp.musket.translate

import org.yurusanp.meskit.analysis.Ctor
import org.yurusanp.meskit.analysis.SortedInner
import org.yurusanp.meskit.analysis.TermType
import org.yurusanp.musket.syntax.Ann
import org.yurusanp.musket.syntax.Node
import org.yurusanp.musket.syntax.Stmt

internal fun TermType.trans(): Node.ADType = Node.ADType(
  sortDec.inner,
  ctors.map(Ctor::trans),
  null,
)


internal fun Ctor.trans(): Node.Ctor = Node.Ctor(
  inner,
  selSortedInners.map(SortedInner::trans),
)

internal fun SortedInner.trans() = Stmt.VarDec(
  Node.AnnedSym(Ann.DType(sortInner), inner),
)
