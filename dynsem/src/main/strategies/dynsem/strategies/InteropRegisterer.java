package dynsem.strategies;

import org.strategoxt.lang.JavaInteropRegisterer;
import org.strategoxt.lang.Strategy;

public class InteropRegisterer extends JavaInteropRegisterer {
	public InteropRegisterer() {
		super(new Strategy[] { read_property_0_2.instance, fmakeexecutable_0_0.instance,
				native_name_matches_varscheme_0_1.instance, digest_term_0_0.instance });
	}
}
