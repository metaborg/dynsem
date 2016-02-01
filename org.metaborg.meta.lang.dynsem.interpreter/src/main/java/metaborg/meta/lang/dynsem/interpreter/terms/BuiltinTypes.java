package metaborg.meta.lang.dynsem.interpreter.terms;

import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class BuiltinTypes {

	public static boolean isITerm(Object value) {
		return value instanceof ITerm;
	}

	public static ITerm asITerm(Object value) {
		assert value instanceof ITerm : "TypesGen.asITerm: ITerm expected";
		return (ITerm) value;
	}

	public static ITerm expectITerm(Object value)
			throws UnexpectedResultException {
		if (value instanceof ITerm) {
			return (ITerm) value;
		}
		throw new UnexpectedResultException(value);
	}

	public static boolean isIConTerm(Object value) {
		return value instanceof IConTerm;
	}

	public static IConTerm asIConTerm(Object value) {
		assert value instanceof IConTerm : "TypesGen.ConTerm: ConTerm expected";
		return (IConTerm) value;
	}

	public static IConTerm expectIConTerm(Object value)
			throws UnexpectedResultException {
		if (value instanceof IConTerm) {
			return (IConTerm) value;
		}
		throw new UnexpectedResultException(value);
	}

	public static boolean isInteger(Object value) {
		return value instanceof Integer;
	}

	public static int asInteger(Object value) {
		assert value instanceof Integer : "TypesGen.asInteger: int expected";
		return (int) value;
	}

	public static int expectInteger(Object value)
			throws UnexpectedResultException {
		if (value instanceof Integer) {
			return (int) value;
		}
		throw new UnexpectedResultException(value);
	}

	public static boolean isString(Object value) {
		return value instanceof String;
	}

	public static String asString(Object value) {
		assert value instanceof String : "TypesGen.asString: String expected";
		return (String) value;
	}

	public static String expectString(Object value)
			throws UnexpectedResultException {
		if (value instanceof String) {
			return (String) value;
		}
		throw new UnexpectedResultException(value);
	}

}
