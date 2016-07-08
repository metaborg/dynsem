package dynsem.strategies;

import org.strategoxt.lang.JavaInteropRegisterer;
import org.strategoxt.lang.Strategy;

public class InteropRegisterer extends JavaInteropRegisterer {
    public InteropRegisterer() {
        super(new Strategy[] { read_property_0_2.instance, make_absolute_path_0_1.instance, ds_rmdir_0_0.instance });
    }
}
