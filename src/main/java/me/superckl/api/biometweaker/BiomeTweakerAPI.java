package me.superckl.api.biometweaker;

import me.superckl.api.superscript.ScriptCommandRegistry;
import me.superckl.api.superscript.command.ScriptCommandListing;
import me.superckl.api.superscript.object.ScriptObject;
import net.minecraftforge.fml.common.Loader;

public class BiomeTweakerAPI {

	private static Class<? extends ScriptObject> biomesScriptObjectClass;
	private static Class<? extends ScriptObject> tweakerScriptObjectClass;
	private static Class<? extends ScriptObject> oreDecorationScriptObjectClass;
	private static boolean foundBiomeTweaker;

	static{
		boolean found = false;
		if(Loader.isModLoaded("BiomeTweaker"))
			try {
				BiomeTweakerAPI.biomesScriptObjectClass = (Class<? extends ScriptObject>) Class.forName("me.superckl.biometweaker.script.object.BiomesScriptObject");
				BiomeTweakerAPI.tweakerScriptObjectClass = (Class<? extends ScriptObject>) Class.forName("me.superckl.biometweaker.script.object.TweakerScriptObject");
				BiomeTweakerAPI.oreDecorationScriptObjectClass = (Class<? extends ScriptObject>) Class.forName("me.superckl.biometweaker.script.object.decoration.OreDecorationScriptObject");
				found = true;
			} catch (final Exception e) {
				found = false;
				APIInfo.log.debug("Failed to find BiomeTweaker. BiomeTweakerAPI will do nothing.");
				e.printStackTrace();
			}
		BiomeTweakerAPI.foundBiomeTweaker = found;
	}

	public static boolean foundBiomeTweaker(){
		return BiomeTweakerAPI.foundBiomeTweaker;
	}

	public static Class<? extends ScriptObject> getBiomesScriptObjectClass(){
		return BiomeTweakerAPI.biomesScriptObjectClass;
	}

	public static Class<? extends ScriptObject> getTweakerScriptObjectClass(){
		return BiomeTweakerAPI.tweakerScriptObjectClass;
	}

	public static Class<? extends ScriptObject> getOreDecorationScriptObjectClass() {
		return BiomeTweakerAPI.oreDecorationScriptObjectClass;
	}

	public static boolean registerScriptCommand(final Class<? extends ScriptObject> clazz, final String name, final ScriptCommandListing listing){
		if(BiomeTweakerAPI.foundBiomeTweaker){
			ScriptCommandRegistry.INSTANCE.registerListing(name, listing, clazz);
			return true;
		}
		return false;
	}

}
