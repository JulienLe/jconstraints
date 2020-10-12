package gov.nasa.jpf.constraints.smtlibUtility.solver;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.ExpressionVisitor;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.*;
import gov.nasa.jpf.constraints.expressions.functions.FunctionExpression;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.constraints.types.Type;

import java.math.BigInteger;

public class SMTLibExportVisitor extends AbstractExpressionVisitor<Void, Void> {


    private SMTLibExportGenContext ctx;

    public SMTLibExportVisitor(SMTLibExportGenContext ctx) {
        this.ctx = ctx;
    }

    public void transform(Expression<?> e) {
        ctx.open("assert");
        defaultVisit(e, null);
        ctx.close();
        ctx.flush();
    }

    @Override
    public <E> Void visit(Variable<E> var, Void v) {
        ctx.appendVar(var);
        return null;
    }

    @Override
    public <E> Void visit(Constant<E> c, Void v) {
        if (BuiltinTypes.SINT32.equals(c.getType())) {
            Integer i = (Integer) c.getValue();
            ctx.append("#x" + String.format("%1$08x", i));
        }
        else if (BuiltinTypes.INTEGER.equals(c.getType())) {
            BigInteger i = (BigInteger) c.getValue();
            ctx.append(i.toString());
        }
        else if (BuiltinTypes.STRING.equals(c.getType())) {
            String s = (String) c.getValue();
            ctx.append("\"" + s + "\"");
        }
        else {
            throw new IllegalArgumentException("Unsupported const type: " + c.getType());
        }
        return null;
    }

    @Override
    public Void visit(Negation n, Void v) {
        ctx.open("not");
        visit(n.getNegated(), v);
        ctx.close();
        return null;
    }

    @Override
    public Void visit(NumericBooleanExpression n, Void v) {
        ctx.open( numComp( n.getComparator(), n.getLeft().getType() ) );
        visit(n.getLeft(), v);
        visit(n.getRight(), v);
        ctx.close();
        return null;
    }

    private String numComp(NumericComparator nc, Type<?> t) {
        switch (nc) {
            case EQ: return "=";
            case NE: return "!=";
            case GE: return bvType(t) ? "bvsge" : ">=";
            case LE: return bvType(t) ? "bvsle" : "<=";
            case GT: return bvType(t) ? "bvsgt" : ">";
            case LT: return bvType(t) ? "bvslt" : "<";
            default:
                throw new IllegalArgumentException("Unsupported: " + nc);
        }
    }

    private boolean bvType(Type<?> t) {
        return BuiltinTypes.SINT8.equals(t) ||
                BuiltinTypes.SINT16.equals(t) ||
                BuiltinTypes.SINT32.equals(t) ||
                BuiltinTypes.SINT64.equals(t);
    }

    @Override
    public Void visit(RegExBooleanExpression n, Void v) {
        ctx.open("str.in.re");
        visit(n.getLeft(), v);
        visit(n.getRight(), v);
        ctx.close();
        return null;
    }

    @Override
    public Void visit(StringBooleanExpression n, Void v) {
        ctx.open( stringComp(n.getOperator() ) );
        visit(n.getLeft(), v);
        visit(n.getRight(), v);
        ctx.close();
        return null;
    }

    private String stringComp(StringBooleanOperator op) {
        switch (op) {
            case EQUALS:   return "=";
            case CONTAINS: return "str.contains";
            case PREFIXOF: return "str.prefixof";
            case SUFFIXOF: return "str.suffixof";
            default:
                throw new IllegalArgumentException("Unsupported: " + op);
        }
    }

    @Override
    public Void visit(StringIntegerExpression n, Void v) {
        ctx.open(stringIntOp( n.getOperator()));
        visit(n.getLeft(), v);
        if (StringIntegerOperator.INDEXOF.equals(n.getOperator())) {
            visit(n.getRight(), v);
            visit(n.getOffset(), v);
        }
        ctx.close();
        return null;
    }

    private String stringIntOp(StringIntegerOperator op) {
        switch (op) {
            case INDEXOF: return "str.indexof";
            case LENGTH: return "str.len";
            case TOINT: return "str.to.int";
            default:
                throw new IllegalArgumentException("Unsupported: " + op);
        }
    }

    @Override
    public Void visit(StringCompoundExpression stringCompoundExpression, Void data) {
        throw new UnsupportedOperationException("not sure about the syntax etc.");
        //return null;
    }

    @Override
    public Void visit(RegexCompoundExpression n, Void data) {
        throw new UnsupportedOperationException("not implemented yet.");
        //return null;
    }

    @Override
    public Void visit(RegexOperatorExpression n, Void data) {
        throw new UnsupportedOperationException("not implemented yet.");
        //return null;
    }

    @Override
    public <F, E> Void visit(CastExpression<F, E> cast, Void v) {
        throw new UnsupportedOperationException("casting is not supported by SMTLib support currently");
        //visit(cast.getCasted(), v);
        //return null;
    }

    @Override
    public <E> Void visit(NumericCompound<E> n, Void v) {
        ctx.open( numOp( n.getOperator(), n.getType()));
        visit(n.getLeft(), v);
        visit(n.getRight(), v);
        ctx.close();
        return null;
    }

    private String numOp(NumericOperator op, Type t) {
        switch (op) {
            case DIV:   return bvType(t) ? "bvdiv" : "/";
            case MINUS: return bvType(t) ? "bvsub" : "-";
            case MUL:   return bvType(t) ? "bvmul" : "*";
            case PLUS:  return bvType(t) ? "bvadd" : "+";
            case REM:   return bvType(t) ? "bvrem" : "rem";
            default:
                throw new IllegalArgumentException("Unsupported: " + op);
        }
    }

    @Override
    public Void visit(PropositionalCompound n, Void v) {
        ctx.open( logicOp( n.getOperator() ) );
        visit(n.getLeft(), v);
        visit(n.getRight(), v);
        ctx.close();
        return null;
    }

    private String logicOp(LogicalOperator op) {
        switch (op) {
            case AND: return "and";
            case IMPLY: return "=>";
            case OR: return "or";
            case EQUIV: return "=";
            case XOR: return "xor";
            default:
                throw new IllegalArgumentException("Unsupported: " + op);
        }
    }

    @Override
    public <E> Void visit(IfThenElse<E> n, Void v) {
        ctx.open("ite");
        visit(n.getIf(), v);
        visit(n.getThen(), v);
        visit(n.getElse(), v);
        ctx.close();
        return null;
    }

    @Override
    public <E> Void visit(UnaryMinus<E> n, Void v) {
        ctx.open("-");
        visit(n.getNegated(), v);
        ctx.close();
        return null;
    }

    @Override
    public <E> Void visit(BitvectorExpression<E> bv, Void data) {
        throw new UnsupportedOperationException("not implemented yet.");
        //return null;
    }

    @Override
    public <E> Void visit(BitvectorNegation<E> n, Void v) {
        ctx.open("bvneg");
        visit(n.getNegated(), v);
        ctx.close();
        return null;
    }

    @Override
    public Void visit(QuantifierExpression q, Void v) {
        ctx.open("" + q.getQuantifier() );
        for (Variable<?> var : q.getBoundVariables()) {
            ctx.appendLocalVarDecl(var);
        }
        visit(q.getBody());
        ctx.close();
        return null;
    }

    @Override
    public <E> Void visit(FunctionExpression<E> f, Void data) {
        throw new UnsupportedOperationException("not implemented yet.");
        //return null;
    }

    @Override
    public Void visit(BooleanExpression n, Void v) {
        ctx.open( boolOp(n.getOperator()));
        visit(n.getLeft(), v);
        visit(n.getRight(), v);
        ctx.close();
        return null;
    }

    private String boolOp(BooleanOperator op) {
        switch (op) {
            case EQ: return "=";
            case NEQ: return "!=";
            default:
                throw new IllegalArgumentException("Unsupported: " + op);
        }
    }

    @Override
    public Void visit(LetExpression letExpression, Void data) {
        throw new UnsupportedOperationException("not implemented yet.");
        //return null;
    }

    @Override
    protected <E> Void defaultVisit(Expression<E> expression, Void v) {
        visit(expression, v);
        return null;
    }
}