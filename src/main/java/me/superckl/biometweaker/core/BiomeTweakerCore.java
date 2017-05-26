package me.superckl.biometweaker.core;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import squeek.asmhelper.me.superckl.biometweaker.ObfHelper;

@SortingIndex(1001)
@MCVersion("1.11.2")
@Name("BiomeTweakerCore")
@TransformerExclusions({"me.superckl.biometweaker.core", "me.superckl.biometweaker.util.CollectionHelper", "me.superckl.biometweaker.config", "squeek.asmhelper.me.superckl.biometweaker", "me.superckl.api.superscript"})
public class BiomeTweakerCore implements IFMLLoadingPlugin{

	public static final Logger logger = LogManager.getLogger("BiomeTweakerCore");
	public static File mcLocation;
	public static boolean modifySuccess;

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {BiomeTweakerASMTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return BiomeTweakerCallHook.class.getName();
	}

	@Override
	public void injectData(final Map<String, Object> data) {
		BiomeTweakerCore.mcLocation = (File) data.get("mcLocation");
		ObfHelper.setObfuscated((Boolean) data.get("runtimeDeobfuscationEnabled"));
		ObfHelper.setRunsAfterDeobfRemapper(true);
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
