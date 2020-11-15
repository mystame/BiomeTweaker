package me.superckl.biometweaker.proxy;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;

import me.superckl.api.biometweaker.property.BiomePropertyManager;
import me.superckl.api.biometweaker.property.PropertyField;
import me.superckl.api.biometweaker.script.AutoRegister;
import me.superckl.api.biometweaker.script.AutoRegister.ParameterOverride;
import me.superckl.api.biometweaker.script.AutoRegister.RegisterExempt;
import me.superckl.api.biometweaker.script.AutoRegisters;
import me.superckl.api.biometweaker.script.wrapper.BTParameterTypes;
import me.superckl.api.biometweaker.world.gen.feature.WorldGeneratorBuilder;
import me.superckl.api.superscript.script.ParameterType;
import me.superckl.api.superscript.script.ParameterTypes;
import me.superckl.api.superscript.script.ParameterWrapper;
import me.superckl.api.superscript.script.ScriptHandler;
import me.superckl.api.superscript.script.ScriptParser;
import me.superckl.api.superscript.script.command.ScriptCommand;
import me.superckl.api.superscript.script.command.ScriptCommandListing;
import me.superckl.api.superscript.script.command.ScriptCommandRegistry;
import me.superckl.api.superscript.script.object.ScriptObject;
import me.superckl.api.superscript.util.ConstructorListing;
import me.superckl.api.superscript.util.WarningHelper;
import me.superckl.biometweaker.BiomeTweaker;
import me.superckl.biometweaker.common.handler.BiomeEventHandler;
import me.superckl.biometweaker.common.handler.EntityEventHandler;
import me.superckl.biometweaker.common.handler.RegistryEventHandler;
import me.superckl.biometweaker.common.handler.WorldEventHandler;
import me.superckl.biometweaker.common.world.biome.property.PropertyContigReplace;
import me.superckl.biometweaker.common.world.biome.property.PropertyDecorationPerChunk;
import me.superckl.biometweaker.common.world.biome.property.PropertyGenScatteredFeatures;
import me.superckl.biometweaker.common.world.biome.property.PropertyGenStrongholds;
import me.superckl.biometweaker.common.world.biome.property.PropertyGenTallPlants;
import me.superckl.biometweaker.common.world.biome.property.PropertyGenVillages;
import me.superckl.biometweaker.common.world.biome.property.PropertyGenWeight;
import me.superckl.biometweaker.common.world.biome.property.PropertyOceanic;
import me.superckl.biometweaker.common.world.biome.property.PropertySpawnBiome;
import me.superckl.biometweaker.common.world.gen.ReplacementConstraints;
import me.superckl.biometweaker.common.world.gen.ReplacementPropertyManager;
import me.superckl.biometweaker.common.world.gen.feature.WorldGenClusterBuilder;
import me.superckl.biometweaker.common.world.gen.feature.WorldGenMineableBuilder;
import me.superckl.biometweaker.common.world.gen.feature.WorldGenPropertyManager;
import me.superckl.biometweaker.common.world.gen.feature.WorldGenSplotchBuilder;
import me.superckl.biometweaker.common.world.gen.feature.WorldGenTreesBuilder;
import me.superckl.biometweaker.script.object.BiomesScriptObject;
import me.superckl.biometweaker.script.object.TweakerScriptObject;
import me.superckl.biometweaker.script.object.block.BasicBlockStateScriptObject;
import me.superckl.biometweaker.script.object.block.BlockReplacementScriptObject;
import me.superckl.biometweaker.script.object.decoration.ClusterDecorationScriptObject;
import me.superckl.biometweaker.script.object.decoration.OreDecorationScriptObject;
import me.superckl.biometweaker.script.object.decoration.SplotchDecorationScriptObject;
import me.superckl.biometweaker.script.object.decoration.TreesDecorationScriptObject;
import me.superckl.biometweaker.util.LogHelper;
import me.superckl.biometweaker.util.ObfNameHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;

public class CommonProxy extends SidedProxy{

	@Override
	public void registerHandlers(){
		final BiomeEventHandler handler = new BiomeEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		MinecraftForge.TERRAIN_GEN_BUS.register(handler);
		MinecraftForge.ORE_GEN_BUS.register(handler);
		final EntityEventHandler eHandler = new EntityEventHandler();
		MinecraftForge.EVENT_BUS.register(eHandler);
		final WorldEventHandler wHandler = new WorldEventHandler();
		MinecraftForge.EVENT_BUS.register(wHandler);
		final RegistryEventHandler rHandler = new RegistryEventHandler();
		MinecraftForge.EVENT_BUS.register(rHandler);
	}

	@Override
	public void initProperties() {
		//Biome properties
		BiomePropertyManager.NAME = new PropertyField<>(Biome.class, ObfNameHelper.Fields.BIOMENAME.getName(), String.class);
		BiomePropertyManager.HEIGHT = new PropertyField<>(Biome.class, ObfNameHelper.Fields.BASEHEIGHT.getName(), Float.class);
		BiomePropertyManager.HEIGHT_VARIATION = new PropertyField<>(Biome.class, ObfNameHelper.Fields.HEIGHTVARIATION.getName(), Float.class);
		BiomePropertyManager.TOP_BLOCK = new PropertyField<>(Biome.class, ObfNameHelper.Fields.TOPBLOCK.getName(), IBlockState.class);
		BiomePropertyManager.FILLER_BLOCK = new PropertyField<>(Biome.class, ObfNameHelper.Fields.FILLERBLOCK.getName(), IBlockState.class);
		BiomePropertyManager.TEMPERATURE = new PropertyField<>(Biome.class, ObfNameHelper.Fields.TEMPERATURE.getName(), Float.class);
		BiomePropertyManager.HUMIDITY = new PropertyField<>(Biome.class, ObfNameHelper.Fields.RAINFALL.getName(), Float.class);
		BiomePropertyManager.WATER_COLOR = new PropertyField<>(Biome.class, ObfNameHelper.Fields.WATERCOLOR.getName(), Integer.class);
		BiomePropertyManager.ENABLE_RAIN = new PropertyField<>(Biome.class, ObfNameHelper.Fields.ENABLERAIN.getName(), Boolean.class);
		BiomePropertyManager.ENABLE_SNOW = new PropertyField<>(Biome.class, ObfNameHelper.Fields.ENABLESNOW.getName(), Boolean.class);
		BiomePropertyManager.GEN_WEIGHT = new PropertyGenWeight();
		BiomePropertyManager.GEN_VILLAGES = new PropertyGenVillages();
		BiomePropertyManager.GEN_STRONGHOLDS = new PropertyGenStrongholds();
		BiomePropertyManager.GEN_SCATTERED_FEATURES = new PropertyGenScatteredFeatures();
		BiomePropertyManager.IS_OCEANIC = new PropertyOceanic();
		BiomePropertyManager.CONTIGUOUS_REPLACEMENT = new PropertyContigReplace();
		BiomePropertyManager.IS_SPAWN_BIOME = new PropertySpawnBiome();
		BiomePropertyManager.GEN_TALL_PLANTS = new PropertyGenTallPlants();
		BiomePropertyManager.WATERLILY_PER_CHUNK = new PropertyDecorationPerChunk(EventType.LILYPAD);
		BiomePropertyManager.TREES_PER_CHUNK = new PropertyDecorationPerChunk(EventType.TREE);
		BiomePropertyManager.FLOWERS_PER_CHUNK = new PropertyDecorationPerChunk(EventType.FLOWERS);
		BiomePropertyManager.GRASS_PER_CHUNK = new PropertyDecorationPerChunk(EventType.GRASS);
		BiomePropertyManager.DEAD_BUSH_PER_CHUNK = new PropertyDecorationPerChunk(EventType.DEAD_BUSH);
		BiomePropertyManager.MUSHROOMS_PER_CHUNK = new PropertyDecorationPerChunk(EventType.SHROOM);
		BiomePropertyManager.REEDS_PER_CHUNK = new PropertyDecorationPerChunk(EventType.REED);
		BiomePropertyManager.CACTI_PER_CHUNK = new PropertyDecorationPerChunk(EventType.CACTUS);
		BiomePropertyManager.SAND_PER_CHUNK = new PropertyDecorationPerChunk(EventType.SAND);
		BiomePropertyManager.CLAY_PER_CHUNK = new PropertyDecorationPerChunk(EventType.CLAY);
		BiomePropertyManager.BIG_MUSHROOMS_PER_CHUNK = new PropertyDecorationPerChunk(EventType.BIG_SHROOM);
		BiomePropertyManager.DESERT_WELLS_PER_CHUNK = new PropertyDecorationPerChunk(EventType.DESERT_WELL);
		BiomePropertyManager.FOSSILS_PER_CHUNK = new PropertyDecorationPerChunk(EventType.FOSSIL);
		BiomePropertyManager.ICE_PER_CHUNK = new PropertyDecorationPerChunk(EventType.ICE);
		BiomePropertyManager.LAKES_PER_CHUNK = new PropertyDecorationPerChunk(EventType.LAKE_WATER);
		BiomePropertyManager.LAVA_LAKES_PER_CHUNK = new PropertyDecorationPerChunk(EventType.LAKE_LAVA);
		BiomePropertyManager.PUMPKINS_PER_CHUNK = new PropertyDecorationPerChunk(EventType.PUMPKIN);
		BiomePropertyManager.ROCK_PER_CHUNK = new PropertyDecorationPerChunk(EventType.ROCK);
		BiomePropertyManager.SAND2_PER_CHUNK = new PropertyDecorationPerChunk(EventType.SAND_PASS2);

		if(BiomeTweaker.getInstance().isTweakEnabled("oceanTopBlock"))
			BiomePropertyManager.OCEAN_TOP_BLOCK = new PropertyField<>(Biome.class, "oceanTopBlock", IBlockState.class);
		if(BiomeTweaker.getInstance().isTweakEnabled("oceanFillerBlock"))
			BiomePropertyManager.OCEAN_FILLER_BLOCK = new PropertyField<>(Biome.class, "oceanFillerBlock", IBlockState.class);
		if(BiomeTweaker.getInstance().isTweakEnabled("actualFillerBlocks"))
			BiomePropertyManager.ACTUAL_FILLER_BLOCKS = new PropertyField<>(Biome.class, "actualFillerBlocks", IBlockState[].class);
		if(BiomeTweaker.getInstance().isTweakEnabled("grassColor"))
			BiomePropertyManager.GRASS_COLOR = new PropertyField<>(Biome.class, "grassColor", Integer.class);
		if(BiomeTweaker.getInstance().isTweakEnabled("foliageColor"))
			BiomePropertyManager.FOLIAGE_COLOR = new PropertyField<>(Biome.class, "foliageColor", Integer.class);
		if(BiomeTweaker.getInstance().isTweakEnabled("skyColor"))
			BiomePropertyManager.SKY_COLOR = new PropertyField<>(Biome.class, "skyColor", Integer.class);
		if(BiomeTweaker.getInstance().isTweakEnabled("fogColor"))
			BiomePropertyManager.FOG_COLOR = new PropertyField<>(Biome.class, "fogColor", Integer.class);
		if(BiomeTweaker.getInstance().isTweakEnabled("genInitialSnow"))
			BiomePropertyManager.GEN_INITIAL_SNOW = new PropertyField<>(Biome.class, "genInitialSnow", Boolean.class);

		BiomePropertyManager.populatePropertyMap();

		//Decoration properties
		WorldGenPropertyManager.COUNT = new PropertyField<>(WorldGeneratorBuilder.class, "count", Integer.class);
		WorldGenPropertyManager.MAIN_BLOCK = new PropertyField<>(WorldGeneratorBuilder.class, "mainBlock", IBlockState.class);

		WorldGenPropertyManager.Mineable.SIZE = new PropertyField<>(WorldGenMineableBuilder.class, "size", Integer.class);
		WorldGenPropertyManager.Mineable.MIN_Y = new PropertyField<>(WorldGenMineableBuilder.class, "minHeight", Integer.class);
		WorldGenPropertyManager.Mineable.MAX_Y = new PropertyField<>(WorldGenMineableBuilder.class, "maxHeight", Integer.class);

		WorldGenPropertyManager.Cluster.RADIUS = new PropertyField<>(WorldGenClusterBuilder.class, "radius", Integer.class);
		WorldGenPropertyManager.Cluster.HEIGHT = new PropertyField<>(WorldGenClusterBuilder.class, "height", Integer.class);

		WorldGenPropertyManager.Tree.MIN_HEIGHT = new PropertyField<>(WorldGenTreesBuilder.class, "minHeight", Integer.class);
		WorldGenPropertyManager.Tree.HEIGHT_VARIATION = new PropertyField<>(WorldGenTreesBuilder.class, "heightVariation", Integer.class);
		WorldGenPropertyManager.Tree.LEAF_HEIGHT = new PropertyField<>(WorldGenTreesBuilder.class, "leafHeight", Integer.class);
		WorldGenPropertyManager.Tree.LEAF_BLOCK = new PropertyField<>(WorldGenTreesBuilder.class, "leafBlock", IBlockState.class);
		WorldGenPropertyManager.Tree.VINE_BLOCK = new PropertyField<>(WorldGenTreesBuilder.class, "vineBlock", IBlockState.class);
		WorldGenPropertyManager.Tree.GROW_VINES = new PropertyField<>(WorldGenTreesBuilder.class, "growVines", Boolean.class);
		WorldGenPropertyManager.Tree.CHECK_CAN_GROW = new PropertyField<>(WorldGenTreesBuilder.class, "checkCanGrow", Boolean.class);

		WorldGenPropertyManager.Splotch.SIZE = new PropertyField<>(WorldGenSplotchBuilder.class, "size", Integer.class);
		WorldGenPropertyManager.Splotch.REQUIRES_BASE = new PropertyField<>(WorldGenSplotchBuilder.class, "requiresBase", Boolean.class);

		WorldGenPropertyManager.populatePropertyMap();

		//Replacement Properties
		ReplacementPropertyManager.BLOCK = new PropertyField<>(ReplacementConstraints.class, "block", IBlockState.class);
		ReplacementPropertyManager.MIN_Y = new PropertyField<>(ReplacementConstraints.class, "minY", Integer.class);
		ReplacementPropertyManager.MAX_Y = new PropertyField<>(ReplacementConstraints.class, "maxY", Integer.class);
		ReplacementPropertyManager.MIN_X = new PropertyField<>(ReplacementConstraints.class, "minX", Integer.class);
		ReplacementPropertyManager.MAX_X = new PropertyField<>(ReplacementConstraints.class, "maxX", Integer.class);
		ReplacementPropertyManager.MIN_Z = new PropertyField<>(ReplacementConstraints.class, "minZ", Integer.class);
		ReplacementPropertyManager.MAX_Z = new PropertyField<>(ReplacementConstraints.class, "maxZ", Integer.class);
		ReplacementPropertyManager.MIN_CHUNK_X = new PropertyField<>(ReplacementConstraints.class, "minChunkX", Integer.class);
		ReplacementPropertyManager.MAX_CHUNK_X = new PropertyField<>(ReplacementConstraints.class, "maxChunkX", Integer.class);
		ReplacementPropertyManager.MIN_CHUNK_Z = new PropertyField<>(ReplacementConstraints.class, "minChunkZ", Integer.class);
		ReplacementPropertyManager.MAX_CHUNK_Z = new PropertyField<>(ReplacementConstraints.class, "maxChunkZ", Integer.class);
		ReplacementPropertyManager.IGNORE_META = new PropertyField<>(ReplacementConstraints.class, "ignoreMeta", Boolean.class);
		ReplacementPropertyManager.CONTIGUOUS = new PropertyField<>(ReplacementConstraints.class, "contiguous", Boolean.class);

		ReplacementPropertyManager.populatePropertyMap();
	}

	@Override
	public void setupScripts(final ASMDataTable table) {
		//Ensure BTParamterTypes registers its defaults
		BTParameterTypes.BLOCKSTATE_BUILDER.getTypeClass();
		LogHelper.debug("Discovering @AutoRegister script commands...");
		final Map<Class<? extends ScriptObject>, Map<String, ScriptCommandListing>> listings = new HashMap<>();
		final Set<ASMData> datas = table.getAll(AutoRegister.class.getCanonicalName());
		final Set<ASMData> groupedDatas = table.getAll(AutoRegisters.class.getCanonicalName());
		final Set<ASMData> allData = new HashSet<>(datas);
		for(final ASMData data:groupedDatas){
			final List<Map<String, Object>> anns =  WarningHelper.uncheckedCast(data.getAnnotationInfo().get("value"));
			anns.forEach(l -> allData.add(new ASMData(data.getCandidate(), AutoRegister.class.getCanonicalName(), data.getClassName(), data.getObjectName(), l)));
		}
		final Set<String> examinedClasses = new HashSet<>();
		for(final ASMData data:allData)
			try {
				if(examinedClasses.contains(data.getClassName()))
					continue;
				final Class<?> asmClass = Class.forName(data.getClassName());
				final Class<? extends ScriptCommand> scriptClass = asmClass.asSubclass(ScriptCommand.class);
				final AutoRegister[] classAnns = scriptClass.getAnnotationsByType(AutoRegister.class);
				final Constructor<?>[] cons = scriptClass.getConstructors();
				for(final Constructor<?> con:cons){
					if(con.isAnnotationPresent(RegisterExempt.class))
						continue;
					final AutoRegister[] methodAnns = con.getAnnotationsByType(AutoRegister.class);
					if(classAnns.length == 0 && methodAnns.length == 0)
						continue;
					AutoRegister[] annsToUse;
					if(methodAnns.length != 0)
						annsToUse = methodAnns;
					else
						annsToUse = classAnns;
					final Class<?>[] cTypes = con.getParameterTypes();
					final ParameterWrapper<?>[] pTypes = new ParameterWrapper[cTypes.length];
					for(final ParameterOverride override:con.getAnnotationsByType(ParameterOverride.class))
						pTypes[override.parameterIndex()] = ParameterTypes.getExceptionWrapper(override.exceptionKey());
					for(int i = 0; i < cTypes.length; i++){
						if(pTypes[i] != null)
							continue;
						final ParameterType<?> type = ParameterTypes.getDefaultType(cTypes[i]);
						if(type == null)
							throw new IllegalStateException("No parameter type found for parameter "+cTypes[i].getCanonicalName());
						pTypes[i] = type.hasSpecialWrapper() ? type.getSpecialWrapper():type.getSimpleWrapper();
					}
					for(final AutoRegister ann:annsToUse)
						for(final Class<? extends ScriptObject> clazz:ann.classes()){
							if(!listings.containsKey(clazz))
								listings.put(clazz, new HashMap<>());
							final Map<String, ScriptCommandListing> map = listings.get(clazz);
							if(!map.containsKey(ann.name()))
								map.put(ann.name(), new ScriptCommandListing());
							final ScriptCommandListing listing = map.get(ann.name());
							listing.addEntry(Lists.newArrayList(pTypes), WarningHelper.uncheckedCast(con));
						}
				}
				examinedClasses.add(data.getClassName());
			} catch (final Exception e) {
				LogHelper.error("Failed to auto-register a script command "+data.getAnnotationInfo().toString());
				e.printStackTrace();
			}
		for(final Entry<Class<? extends ScriptObject>, Map<String, ScriptCommandListing>> entry:listings.entrySet()){
			LogHelper.debug("Registering "+entry.getValue().size()+" commands to "+entry.getKey().getSimpleName());
			ScriptCommandRegistry.INSTANCE.registerClassListing(entry.getKey(), entry.getValue());
		}

		ScriptHandler.registerStaticObject("Tweaker", TweakerScriptObject.class);

		try {
			ConstructorListing<ScriptObject> listing = new ConstructorListing<>();
			listing.addEntry(Lists.newArrayList(BTParameterTypes.BASIC_BIOMES_PACKAGE.getVarArgsWrapper()), BiomesScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("forBiomes", listing);

			listing = new ConstructorListing<>();
			listing.addEntry(Lists.newArrayList(BTParameterTypes.TYPE_BIOMES_PACKAGE.getSpecialWrapper()), BiomesScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("forBiomesOfTypes", listing);

			listing = new ConstructorListing<>();
			listing.addEntry(Lists.newArrayList(BTParameterTypes.ALL_BIOMES_PACKAGE.getSpecialWrapper()), BiomesScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("forAllBiomes", listing);

			listing = new ConstructorListing<>();
			listing.addEntry(Lists.newArrayList(BTParameterTypes.ALL_BUT_BIOMES_PACKAGE.getSpecialWrapper()), BiomesScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("forAllBiomesExcept", listing);

			listing = new ConstructorListing<>();
			listing.addEntry(Lists.newArrayList(BTParameterTypes.INTERSECT_BIOMES_PACKAGE.getSpecialWrapper()), BiomesScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("intersectionOf", listing);

			listing = new ConstructorListing<>();
			listing.addEntry(Lists.newArrayList(BTParameterTypes.SUBTRACT_BIOMES_PACKAGE.getSpecialWrapper()), BiomesScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("subtractFrom", listing);

			listing = new ConstructorListing<>();
			listing.addEntry(Lists.newArrayList(BTParameterTypes.PROPERTY_RANGE_PACKAGE.getSpecialWrapper()), BiomesScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("forBiomesWithPropertyRange", listing);

			listing = new ConstructorListing<>();
			listing.addEntry(new ArrayList<>(), OreDecorationScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("newOreDecoration", listing);

			listing = new ConstructorListing<>();
			listing.addEntry(new ArrayList<>(), TreesDecorationScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("newTreeDecoration", listing);

			listing = new ConstructorListing<>();
			listing.addEntry(new ArrayList<>(), ClusterDecorationScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("newClusterDecoration", listing);

			listing = new ConstructorListing<>();
			listing.addEntry(new ArrayList<>(), SplotchDecorationScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("newSplotchDecoration", listing);

			listing = new ConstructorListing<>();
			listing.addEntry(Lists.newArrayList(ParameterTypes.STRING.getSimpleWrapper()), BasicBlockStateScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("forBlock", listing);

			listing = new ConstructorListing<>();
			listing.addEntry(new ArrayList<>(), BlockReplacementScriptObject.class.getDeclaredConstructor());
			ScriptParser.registerValidObjectInst("newBlockReplacement", listing);

		} catch (final Exception e2) {
			LogHelper.error("Failed to populate object listings! Some tweaks may not be applied.");
			e2.printStackTrace();
		}
	}

}
