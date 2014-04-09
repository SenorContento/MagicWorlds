package com.elmakers.mine.bukkit.plugins.magicworlds.populator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.block.MaterialAndData;
import com.elmakers.mine.bukkit.block.MaterialBrush;

public class ReplacePopulator extends MagicBlockPopulator {
	private Map<Material, MaterialAndData> replaceMap = new HashMap<Material, MaterialAndData>();
	private int maxY = 128;
	private int minY = 3;
	
	@Override
	public void onLoad(ConfigurationSection config) {
		replaceMap.clear();
		
		maxY = config.getInt("max_y");
		if (maxY == 0) {
			maxY = 128;
		}
		minY = config.getInt("min_y");
		if (minY == 0) {
			minY = 3;
		}
		
		ConfigurationSection replaceSection = config.getConfigurationSection("replace");
		if (replaceSection == null) return;
		Map<String, Object> replaceNodes = replaceSection.getValues(false);
		for (Entry<String, Object> replaceNode : replaceNodes.entrySet()) {
			MaterialAndData fromMaterial = MaterialBrush.parseMaterialKey(replaceNode.getKey());
			if (fromMaterial == null) {
				controller.getLogger().warning("Invalid material key: " + replaceNode.getKey());
				continue;
			}
			MaterialAndData toMaterial = MaterialBrush.parseMaterialKey(replaceNode.getValue().toString());
			if (toMaterial == null) {
				controller.getLogger().warning("Invalid material key: " + replaceNode.getValue());
				continue;
			}
			replaceMap.put(fromMaterial.getMaterial(), toMaterial);
		}
	}
	
	protected void replaceBlock(Block block) {
		MaterialAndData replaceType = replaceMap.get(block.getType());
		if (replaceType != null) {
			try {
				replaceType.modify(block);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void populate(Block block, Random random) {
		if (block.getY() < minY || block.getY() > maxY) return;
		replaceBlock(block);
	}
}
