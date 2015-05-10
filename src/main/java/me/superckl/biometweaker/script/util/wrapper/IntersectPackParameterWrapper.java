package me.superckl.biometweaker.script.util.wrapper;

import java.util.List;

import me.superckl.biometweaker.script.ScriptHandler;
import me.superckl.biometweaker.script.pack.IBiomePackage;
import me.superckl.biometweaker.script.pack.IntersectBiomesPackage;
import me.superckl.biometweaker.script.util.ParameterType;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class IntersectPackParameterWrapper extends ParameterWrapper{

	public IntersectPackParameterWrapper() {
		super(ParameterType.INTERSECT_BIOMES_PACKAGE, 1, 1, false);
	}

	@Override
	public Pair<Object[], String[]> parseArgs(final ScriptHandler handler, final String... args) throws Exception {
		final List<IBiomePackage> parsed = Lists.newArrayList();
		String[] toReturn = new String[0];
		for(int i = 0; i < args.length; i++){
			final IBiomePackage obj = (IBiomePackage) ParameterType.BASIC_BIOMES_PACKAGE.tryParse(args[i], handler);
			if(obj == null){
				toReturn = new String[args.length-i];
				System.arraycopy(args, i, toReturn, 0, toReturn.length);
				break;
			}
			parsed.add(obj);
		}
		return Pair.of(new Object[] {new IntersectBiomesPackage(parsed.toArray(new IBiomePackage[parsed.size()]))}, toReturn);
	}

}
