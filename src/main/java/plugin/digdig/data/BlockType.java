package plugin.digdig.data;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;

public class BlockType {
  private static final Map<Material, String> blockType = new HashMap<>();

  static {
    blockType.put(Material.COAL_ORE, "石炭の鉱石");
    blockType.put(Material.IRON_ORE, "鉄の鉱石");
    blockType.put(Material.COPPER_ORE, "銅の鉱石");
    blockType.put(Material.GOLD_ORE, "金の鉱石");
    blockType.put(Material.LAPIS_ORE, "ラピスラズリの鉱石");
    blockType.put(Material.REDSTONE_ORE, "レッドストーンの鉱石");
    blockType.put(Material.EMERALD_ORE, "エメラルドの鉱石");
    blockType.put(Material.DIAMOND_ORE, "ダイアモンドの鉱石");
  }

  public static String getBlockType(Material material) {
    return blockType.getOrDefault(material, "");
  }
}

