package gov.nasa.jpf.constraints.smtlibUtility.parser;

import static org.testng.AssertJUnit.assertEquals;

import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import java.util.LinkedList;
import java.util.List;
import org.testng.annotations.Test;

public class SMTLIBModelParserTest {

  @Test(groups = {"base"})
  public void aBVModelParsingTest() throws SMTLIBParserException {
    String input =
        "(model\n" + "(define-fun x () (_ BitVec 32) #b01000000000000000000000000000000)\n" + ")\n";
    List<Variable<?>> vars = new LinkedList<>();
    vars.add(Variable.create(BuiltinTypes.SINT32, "x"));

    Valuation val = SMTLibModelParser.parseModel(input, vars);
    assertEquals(1, val.getVariables().size());
  }
}
