package gov.nasa.jpf.constraints.smtlibUtility.parser;

import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.exceptions.ImpreciseRepresentationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.smtlib.CharSequenceReader;
import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.IParser;
import org.smtlib.IParser.ParserException;
import org.smtlib.ISort;
import org.smtlib.ISource;
import org.smtlib.SMT;
import org.smtlib.command.C_define_fun;

public class SMTLibModelParser {

  public static Valuation parseModel(String input, List<Variable<?>> vars)
      throws SMTLIBParserException {
    final SMT smt = new SMT();
    String value = extractValuePart(input);
    Valuation val = new Valuation();
    final ISource toBeParsed =
        smt.smtConfig.smtFactory.createSource(
            new CharSequenceReader(new StringReader(value), input.length(), 100, 2), null);
    final IParser parser = smt.smtConfig.smtFactory.createParser(smt.smtConfig, toBeParsed);
    try {
      while (!parser.isEOD()) {
        ICommand cmd = parser.parseCommand();
        if (cmd instanceof C_define_fun) {
          C_define_fun fun = (C_define_fun) cmd;
          ISymbol sym = fun.symbol();
          ISort sort = fun.resultSort();
          IExpr exprs = fun.expression();
          for (Variable var : vars) {
            if (var.getName().equals(sym.value())) {
              val.setParsedValue(var, resolveUnicode(exprs.toString()));
              continue;
            }
          }
        }
      }
    } catch (ParserException | IOException | ImpreciseRepresentationException e) {
      throw new SMTLIBParserException(e.getMessage());
    }
    return val;
  }

  private static String extractValuePart(String in) {
    Pattern p = Pattern.compile("^\\(model(?<value>.*)\\)(?:\\R?)$", Pattern.DOTALL);
    Matcher m = p.matcher(in);
    String value = "";
    if (m.matches()) {
      value = m.group("value");
      System.out.println(value);
    }
    return value;
  }

  static String resolveUnicode(String toString) {
    toString = toString.replaceAll(Pattern.quote("\\u{7f}"), Character.toString((char) 127));
    toString = toString.replaceAll(Pattern.quote("u{5c}"), "");
    toString = toString.replaceAll(Pattern.quote("\\u{0}"), "\0");
    if (!toString.equals("\"\"")) {
      toString = toString.replaceAll("\"\"", "\"");
    }
    return toString;
  }
}
