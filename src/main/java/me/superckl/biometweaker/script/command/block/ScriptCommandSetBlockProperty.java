package me.superckl.biometweaker.script.command.block;

import com.google.gson.JsonElement;

import lombok.RequiredArgsConstructor;
import me.superckl.api.biometweaker.block.BlockStateBuilder;
import me.superckl.api.superscript.command.IScriptCommand;
import me.superckl.api.superscript.script.ParameterTypes;
import me.superckl.api.superscript.script.ScriptHandler;
import me.superckl.api.superscript.script.ScriptParser;

@RequiredArgsConstructor
public class ScriptCommandSetBlockProperty implements IScriptCommand{

	private final BlockStateBuilder<?> builder;
	private final String key;
	private final JsonElement value;

	private ScriptHandler handler;

	@Override
	public void perform() throws Exception {
		String value = this.value.getAsString();
		if(ScriptParser.isStringArg(value))
			value = ParameterTypes.STRING.tryParse(value, this.handler);
		this.builder.setProperty(this.key, value);
	}

	@Override
	public void setScriptHandler(final ScriptHandler handler) {
		this.handler = handler;
	}

}