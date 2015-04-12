package com.creativemd.craftingmanager.mod.core.transformer;

import java.io.File;
import java.util.Map;

import com.creativemd.craftingmanager.mod.CraftingManagerMod;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class CraftingLoader  implements IFMLLoadingPlugin {
	
	public static File location;

	
	@Override
	public String[] getASMTransformerClass() {
		return new String[]{CraftingTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return CraftingManagerMod.class.getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		location = (File) data.get("coremodLocation");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
