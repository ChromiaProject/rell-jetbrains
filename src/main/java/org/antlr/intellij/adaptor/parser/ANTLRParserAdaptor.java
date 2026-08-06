package org.antlr.intellij.adaptor.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.psi.tree.IElementType;
import org.antlr.intellij.adaptor.lexer.PSITokenSource;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/** An adaptor that makes an ANTLR parser look like a PsiParser. */
public abstract class ANTLRParserAdaptor implements PsiParser {
	protected final Language language;
	protected final Parser parser;

	/** Create a jetbrains adaptor for an ANTLR parser object. When
	 *  the IDE requests a {@link #parse(IElementType, PsiBuilder)},
	 *  the token stream will be set on the parser.
	 */
	public ANTLRParserAdaptor(Language language, Parser parser) {
		this.language = language;
		this.parser = parser;
	}

	public Language getLanguage() {
		return language;
	}

	@NotNull
	@Override
	public ASTNode parse(IElementType root, PsiBuilder builder) {
		ProgressIndicatorProvider.checkCanceled();

		ParseTree parseTree = null;
		SyntaxErrorListener winningErrors = null;
		int fewestErrors = Integer.MAX_VALUE;

		for (Function<Parser, ParseTree> candidate : parseCandidates(root, builder)) {
			SyntaxErrorListener errors = new SyntaxErrorListener(); // trap errors
			ParseTree tree = null;
			PsiBuilder.Marker rollbackMarker = builder.mark();
			try {
				TokenSource source = new PSITokenSource(builder);
				TokenStream tokens = new CommonTokenStream(source);
				parser.setTokenStream(tokens);
				parser.setErrorHandler(createErrorStrategy()); // tweaks missing tokens
				parser.removeErrorListeners();
				parser.addErrorListener(errors);
				tree = candidate.apply(parser);
			}
			finally {
				rollbackMarker.rollbackTo();
			}

			int errorCount = errors.getSyntaxErrors().size();
			if (errorCount < fewestErrors) {
				fewestErrors = errorCount;
				parseTree = tree;
				winningErrors = errors;
			}
			if (errorCount == 0) break;
		}

		// The converter picks the errors to highlight off the parser's listeners, so the winning
		// attempt's listener has to be the one still attached.
		parser.removeErrorListeners();
		parser.addErrorListener(winningErrors);

		// Now convert ANTLR parser tree to PSI tree by mimicking subtree
		// enter/exit with mark/done calls. I *think* this creates their parse
		// tree (AST as they call it) when you call {@link PsiBuilder#getTreeBuilt}
		ANTLRParseTreeToPSIConverter listener = createListener(parser, root, builder);
		PsiBuilder.Marker rootMarker = builder.mark();
		ParseTreeWalker.DEFAULT.walk(listener, parseTree);
		while (!builder.eof()) {
			ProgressIndicatorProvider.checkCanceled();
			builder.advanceLexer();
		}
		// NOTE: parse tree returned from parse will be the
		// usual ANTLR tree ANTLRParseTreeToPSIConverter will
		// convert that to the analogous jetbrains AST nodes
		// When parsing an entire file, the root IElementType
		// will be a IFileElementType.
		//
		// When trying to rename IDs and so on, you get a
		// dummy root and a type arg identifier IElementType.
		// This results in a weird tree that has for example
		// (ID (expr (primary ID))) with the ID IElementType
		// as a subtree root as well as the appropriate leaf
		// all the way at the bottom.  The dummy ID root is a
		// CompositeElement and created by
		// ParserDefinition.createElement() despite having
		// being TokenIElementType.
		rootMarker.done(root);
		return builder.getTreeBuilt(); // calls the ASTFactory.createComposite() etc...
	}

	protected abstract ParseTree parse(Parser parser, IElementType root);

	/** The error strategy each parse attempt runs with. Subclasses override this to word the
	 *  syntax errors their language reports; the default only tweaks missing tokens. */
	protected ANTLRErrorStrategy createErrorStrategy() {
		return new ErrorStrategyAdaptor();
	}

	/** Root rules to try, in order. The first attempt that parses without syntax errors wins; if
	 *  none does, the attempt with the fewest errors is the one converted to PSI. Ties go to the
	 *  earlier candidate. Subclasses override this when a single root rule cannot describe every
	 *  text the language is asked to parse — an injected fragment, say, versus a whole file. */
	protected List<Function<Parser, ParseTree>> parseCandidates(IElementType root, PsiBuilder builder) {
		return Collections.singletonList(p -> parse(p, root));
	}

	protected ANTLRParseTreeToPSIConverter createListener(Parser parser, IElementType root, PsiBuilder builder) {
		return new ANTLRParseTreeToPSIConverter(language, parser, builder);
	}
}
