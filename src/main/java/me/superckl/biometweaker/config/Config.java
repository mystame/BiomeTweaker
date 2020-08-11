package me.superckl.biometweaker.config;

import java.io.File;

import lombok.Getter;
import me.superckl.biometweaker.common.reference.ModData;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

public class Config {

	private final Configuration configFile;

	@Getter
	private String[] includes;
	@Getter
	private boolean disableOutput;
	@Getter
	private boolean outputSeperateFiles;
	@Getter
	private final File btConfigFolder;

	public Config(final File config) {
		this.configFile = new Configuration(new File(config, ModData.MOD_NAME+".cfg"));
		this.btConfigFolder = config;
		this.configFile.load();
		if(this.configFile.hasChanged())
			this.configFile.save();
	}

	public void loadValues(){
		this.includes = this.configFile.getStringList("Script Files", "Scripting", new String[] {},
				"A list of script files to include that are not in the scripts folder. An example script file is created along with this configuration file.");
		this.disableOutput = this.configFile.getBoolean("Disable Output Files", "Output Files", false, "If true, BiomeTweaker will not generate any output files.");
		this.outputSeperateFiles = this.configFile.getBoolean("Output Seperate Files", "Output Files", true,
				"If true, BiomeTweaker will generate separate files for each item when creating the status report.");
		if(this.configFile.hasChanged())
			this.configFile.save();
	}

	public void onConfigChange(final OnConfigChangedEvent e){
		if(e.getModID().equals(ModData.MOD_ID))
			this.loadValues();
	}

}
