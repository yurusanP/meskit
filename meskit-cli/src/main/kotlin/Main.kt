package org.yurusanp.meskit.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import org.yurusanp.meskit.cli.repl.Repl
import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.musket.SyntaxProvider

private class Kit : CliktCommand() {
  override fun run() = Unit
}

private class Parser : CliktCommand() {
  override fun run() {
    val repl = Repl(SemGuSBaseVisitor())
    repl.start()
  }
}

private class Musket : CliktCommand() {
  override fun run() {
    val repl = Repl(SyntaxProvider())
    repl.start()
  }
}

fun main(args: Array<String>) = Kit()
  .subcommands(Parser())
  .subcommands(Musket())
  .main(args)
