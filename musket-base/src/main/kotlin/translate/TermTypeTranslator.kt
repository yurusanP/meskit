package org.yurusanp.musket.translate

import org.yurusanp.meskit.surface.Representation
import org.yurusanp.musket.syntax.Ann
import org.yurusanp.musket.syntax.Node
import org.yurusanp.musket.syntax.Stmt

internal fun Representation.TermTypeDef.trans(): Stmt.ADTypeDef = Stmt.ADTypeDef(
  Node.ADType(
    sortDec.inner,
    ctors.map(Representation.Ctor::trans),
    null,
  ),
)

internal fun Representation.Ctor.trans(): Node.Ctor = Node.Ctor(
  inner,
  sels.map(Representation.SelDec::trans),
)

internal fun Representation.SelDec.trans() = Stmt.VarDec(
  Node.AnnedSym(Ann.DType(sortInner), inner),
)
