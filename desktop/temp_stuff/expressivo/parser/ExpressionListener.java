// Generated from Expression.g4 by ANTLR 4.5.1

package expressivo.parser;
// Do not edit this .java file! Edit the grammar in Expression.g4 and re-run Antlr.

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExpressionParser}.
 */
public interface ExpressionListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#root}.
	 * @param ctx the parse tree
	 */
	void enterRoot(ExpressionParser.RootContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#root}.
	 * @param ctx the parse tree
	 */
	void exitRoot(ExpressionParser.RootContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#sum}.
	 * @param ctx the parse tree
	 */
	void enterSum(ExpressionParser.SumContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#sum}.
	 * @param ctx the parse tree
	 */
	void exitSum(ExpressionParser.SumContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#multiply}.
	 * @param ctx the parse tree
	 */
	void enterMultiply(ExpressionParser.MultiplyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#multiply}.
	 * @param ctx the parse tree
	 */
	void exitMultiply(ExpressionParser.MultiplyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#addend}.
	 * @param ctx the parse tree
	 */
	void enterAddend(ExpressionParser.AddendContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#addend}.
	 * @param ctx the parse tree
	 */
	void exitAddend(ExpressionParser.AddendContext ctx);
}